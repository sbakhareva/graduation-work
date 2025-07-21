package ru.skypro.homework.service.impl;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.*;
import ru.skypro.homework.exception.NoAdsFoundException;
import ru.skypro.homework.exception.NoImagesFoundException;
import ru.skypro.homework.exception.NoUsersFoundByEmailException;
import ru.skypro.homework.exception.NoneOfYourBusinessException;
import ru.skypro.homework.mappers.*;
import ru.skypro.homework.model.AdEntity;
import ru.skypro.homework.model.AdImage;
import ru.skypro.homework.model.UserEntity;
import ru.skypro.homework.repository.AdImageRepository;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.repository.UserRepository;

import java.io.IOException;

@Service
@Transactional
@AllArgsConstructor
public class AdsService {

    private static final Logger logger = LoggerFactory.getLogger(AdsService.class);

    private final AdDTOMapper adDTOMapper;
    private final AdsDTOMapper adsDTOMapper;
    private final ExtendedAdDTOMapper extendedAdDTOMapper;
    private final CreateOrUpdateAdDTOMapper createOrUpdateAdDTOMapper;

    private final AdRepository adRepository;
    private final AdImageService adImageService;
    private final UserRepository userRepository;
    private final AdImageRepository adImageRepository;

    private boolean isAdmin(String email) {
        return userRepository.findByEmail(email)
                .map(user -> user.getRole() == Role.ADMIN)
                .orElse(false);
    }

    public Ads getAllAds() {
        return adsDTOMapper.toDto(adRepository.findAll());
    }

    public Ad addAd(CreateOrUpdateAd createOrUpdateAd,
                    MultipartFile image,
                    String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NoUsersFoundByEmailException(email));
        AdEntity adEntity = AdEntity.builder()
                .price(createOrUpdateAd.getPrice())
                .title(createOrUpdateAd.getTitle())
                .description(createOrUpdateAd.getDescription())
                .user(user)
                .build();

        adRepository.save(adEntity);

        if (image != null && !image.isEmpty()) {
            try {
                adImageService.uploadImage(adEntity.getId(), image);
            } catch (IOException e) {
                System.out.println(("Ошибка при загрузке изображения"));
            }
        }

        return adDTOMapper.toDto(adEntity);
    }

    public ExtendedAd getAdInfo(Integer id,
                                String email) {
        AdEntity adEntity = adRepository.findById(id)
                .orElseThrow(() -> new NoAdsFoundException(id));
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NoUsersFoundByEmailException(email));
        return extendedAdDTOMapper.toDto(adEntity, user);
    }

    public void deleteAd(Integer id,
                         String email) {
        AdEntity adToDelete = adRepository.findById(id)
                .orElseThrow(() -> new NoAdsFoundException(id));

        if (!adToDelete.getUser().getEmail().equals(email) && !isAdmin(email)) {
            throw new NoneOfYourBusinessException("Вы не можете удалить это объявление");
        }

        adRepository.delete(adToDelete);
    }

    public Ad updateAd(Integer id,
                       CreateOrUpdateAd createOrUpdateAd,
                       String email) {
        AdEntity adEntity = adRepository.findById(id)
                .orElseThrow(() -> new NoAdsFoundException(id));

        if (!adEntity.getUser().getEmail().equals(email) && !isAdmin(email)) {
            throw new NoneOfYourBusinessException("Вы не можете редактировать это объявление");
        }

        return createOrUpdateAdDTOMapper.updateEntityFromDto(createOrUpdateAd, adEntity);
    }

    public Ads getAdsMe(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NoUsersFoundByEmailException(email));
        return adsDTOMapper.toDto(adRepository.findAllByUserId(user.getId()));
    }

    public String updateImage(Integer id,
                              MultipartFile image,
                              String email) {
        AdEntity ad = adRepository.findById(id)
                .orElseThrow(() -> new NoAdsFoundException(id));

        if (!ad.getUser().getEmail().equals(email) && !isAdmin(email)) {
            throw new NoneOfYourBusinessException("Вы не можете обновить картинку этого объявления");
        }

        adImageService.deleteImageFile(id);
        try {
            adImageRepository.deleteByAdId(ad.getId());
        } catch (Exception e) {
            logger.error("Ошибка при удалении изображения для объявления с id {}: {}", ad.getId(), e.getMessage(), e);
        }

        try {
            adImageService.uploadImage(ad.getId(), image);
        } catch (IOException e) {
            logger.error("Ошибка при сохранении изображения");
        }
        AdImage newImage = adImageRepository.findByAdId(id)
                .orElseThrow(() -> new NoImagesFoundException("Не найдено изображений для объявления с id " + id));
        ad.setImage(newImage);
        return newImage.getFilePath();
    }
}

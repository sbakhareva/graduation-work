package ru.skypro.homework.service.impl;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.*;
import ru.skypro.homework.exception.*;
import ru.skypro.homework.mappers.*;
import ru.skypro.homework.model.AdEntity;
import ru.skypro.homework.model.AdImage;
import ru.skypro.homework.model.UserEntity;
import ru.skypro.homework.repository.AdImageRepository;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.repository.UserRepository;

import java.io.IOException;
import java.util.List;

/**
 * Сервис для работы с сущностью {@link AdEntity}.
 * Содержит методы для сохранения, обновления и получения информации об объявлениях.
 */

@Service
@Transactional
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

    public AdsService(AdDTOMapper adDTOMapper,
                      AdsDTOMapper adsDTOMapper,
                      ExtendedAdDTOMapper extendedAdDTOMapper,
                      CreateOrUpdateAdDTOMapper createOrUpdateAdDTOMapper,
                      AdRepository adRepository,
                      AdImageService adImageService,
                      UserRepository userRepository,
                      AdImageRepository adImageRepository) {
        this.adDTOMapper = adDTOMapper;
        this.adsDTOMapper = adsDTOMapper;
        this.extendedAdDTOMapper = extendedAdDTOMapper;
        this.createOrUpdateAdDTOMapper = createOrUpdateAdDTOMapper;
        this.adRepository = adRepository;
        this.adImageService = adImageService;
        this.userRepository = userRepository;
        this.adImageRepository = adImageRepository;
    }

    private boolean isAdmin(String email) {
        return userRepository.findByEmail(email)
                .map(user -> user.getRole() == Role.ADMIN)
                .orElse(false);
    }

    private boolean isOwner(Integer adId, String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NoUsersFoundByEmailException(email));

        return adRepository.findById(adId)
                .map(ad -> ad.getUser().getId().equals(user.getId()))
                .orElse(false);
    }

    /**
     * Получает все объявления, которые хранятся в базе данных.
     *
     * @return объект ДТО {@link Ads}
     */
    public Ads getAllAds() {
        List<AdEntity> ads = adRepository.findAll();
        if (ads.isEmpty()) {
            logger.info("В хранилище нет объявлений для отображения.");
        }

        return adsDTOMapper.toDto(ads);
    }

    /**
     * Сохраняет новое объявление в базу данных.
     *
     * @param createOrUpdateAd объект ДТО {@link CreateOrUpdateAd}, содержащий основную информацию об объявлении
     * @param image            загруженное пользователем фото объявления
     * @param email            email пользователя, извлеченный из {@link Authentication}
     * @return объект ДТО {@link Ad}, содержащий краткую информацию об объявлении
     * @throws NoUsersFoundByEmailException, если пользователем с указанным email не найден
     */
    public Ad addAd(CreateOrUpdateAd createOrUpdateAd,
                    MultipartFile image,
                    String email) throws IOException {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NoUsersFoundByEmailException(email));

        AdEntity ad = createOrUpdateAdDTOMapper.createEntityFromDto(createOrUpdateAd, user);

        adRepository.save(ad);

        if (image != null && !image.isEmpty()) {
            adImageService.uploadImage(ad.getId(), image);
        }

        return adDTOMapper.toDto(ad);
    }

    /**
     * Получает расширенную информацию об объявлении.
     *
     * @param id    идентификатор объявления
     * @return объект ДТО {@link ExtendedAd}, содержащий подробную информацию об объявлении и его авторе
     * @throws NoUsersFoundByEmailException, если пользователем с указанным email не найден
     * @throws NoAdsFoundException,          если не найдено объявлений по переданному id
     */
    public ExtendedAd getAdInfo(Integer id) {
        AdEntity adEntity = adRepository.findById(id)
                .orElseThrow(() -> new NoAdsFoundException(id));

        Integer userId = adEntity.getUser().getId();
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NoUsersFoundByIdException(userId));

        return extendedAdDTOMapper.toDto(adEntity, user);
    }

    /**
     * Удаляет объявление из базы данных.
     *
     * @param id    идентификатор объявления
     * @param email email пользователя, извлеченный из {@link Authentication}
     * @throws NoAdsFoundException,         если не найдено объявлений по переданному id
     * @throws NoneOfYourBusinessException, если у пользователя нет прав на удаление объявления
     */
    public void deleteAd(Integer id,
                         String email) {
        AdEntity adToDelete = adRepository.findById(id)
                .orElseThrow(() -> new NoAdsFoundException(id));

        if (!isOwner(id, email) && !isAdmin(email)) {
            throw new NoneOfYourBusinessException("Вы не можете удалить это объявление");
        }

        adRepository.delete(adToDelete);
    }

    /**
     * Обновляет информацию о существующем объявлении.
     *
     * @param id               идентификатор объявления
     * @param createOrUpdateAd объект ДТО {@link CreateOrUpdateAd}, содержащий обновленную информацию,
     *                         полученную от пользователя
     * @param email            email пользователя, извлеченный из {@link Authentication}
     * @return объект ДТО {@link Ad}, содержащий краткую информацию об объявлении с обновленными полями
     * @throws NoAdsFoundException,         если не найдено объявлений по переданному id
     * @throws NoneOfYourBusinessException, если у пользователя нет прав на редактирование объявления
     */
    public Ad updateAd(Integer id,
                       CreateOrUpdateAd createOrUpdateAd,
                       String email) {
        if (!userRepository.existsByEmail(email)) {
            throw new NoUsersFoundByEmailException(email);
        }

        AdEntity adEntity = adRepository.findById(id)
                .orElseThrow(() -> new NoAdsFoundException(id));

        if (!isOwner(id, email) && !isAdmin(email)) {
            throw new NoneOfYourBusinessException("Вы не можете редактировать это объявление");
        }

        return createOrUpdateAdDTOMapper.updateEntityFromDto(createOrUpdateAd, adEntity);
    }

    /**
     * Получает объявления авторизованного пользователя.
     *
     * @param email email пользователя, извлеченный из {@link Authentication}
     * @return объект ДТО {@link Ads}
     */
    public Ads getAdsMe(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NoUsersFoundByEmailException(email));

        List<AdEntity> ads = adRepository.findAllByUserId(user.getId());

        if (ads.isEmpty()) {
            logger.info("В хранилище нет объявлений для пользователя {}", user.getId());
        }

        return adsDTOMapper.toDto(ads);
    }

    /**
     * Обновляет картинку объявления.
     *
     * @param id    идентификатор объявления
     * @param image загруженное пользователем новое фото
     * @param email email пользователя, извлеченный из {@link Authentication}
     * @return строка со ссылкой на изображение в файловой системе
     * @throws NoneOfYourBusinessException, если у пользователя нет прав на редактирование информации об объявлении
     * @throws NoAdsFoundException,         если не найдено объявлений по переданному id
     * @throws NoImagesFoundException,      если не найдено изображений для текущего объявления
     */
    public String updateImage(Integer id,
                              MultipartFile image,
                              String email) {
        AdEntity ad = adRepository.findById(id)
                .orElseThrow(() -> new NoAdsFoundException(id));

        if (!isOwner(id, email) && !isAdmin(email)) {
            throw new NoneOfYourBusinessException("Вы не можете обновить картинку этого объявления");
        }

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

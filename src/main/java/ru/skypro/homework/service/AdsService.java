package ru.skypro.homework.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.*;
import ru.skypro.homework.exception.NoAdsFoundException;
import ru.skypro.homework.exception.NoCommentsException;
import ru.skypro.homework.exception.NoImagesFoundException;
import ru.skypro.homework.exception.NoUsersFoundException;
import ru.skypro.homework.mappers.*;
import ru.skypro.homework.model.AdEntity;
import ru.skypro.homework.model.AdImage;
import ru.skypro.homework.model.CommentEntity;
import ru.skypro.homework.model.UserEntity;
import ru.skypro.homework.repository.AdImageRepository;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.repository.CommentRepository;
import ru.skypro.homework.repository.UserRepository;

import java.io.IOException;
import java.util.List;

import static ru.skypro.homework.utils.ImageURLGenerator.generateImageUrl;

@Service
public class AdsService {

    private final AdDTOMapper adDTOMapper = new AdDTOMapper();
    private final CommentDTOMapper commentDTOMapper = new CommentDTOMapper();
    private final AdsDTOMapper adsDTOMapper = new AdsDTOMapper(adDTOMapper);
    private final ExtendedAdDTOMapper extendedAdDTOMapper = new ExtendedAdDTOMapper();
    private final CreateOrUpdateAdDTOMapper createOrUpdateAdDTOMapper = new CreateOrUpdateAdDTOMapper();
    private final CommentsDTOMapper commentsDTOMapper = new CommentsDTOMapper(commentDTOMapper);
    private final CreateOrUpdateCommentDTOMapper createOrUpdateCommentDTOMapper = new CreateOrUpdateCommentDTOMapper(commentDTOMapper);

    @Value("${default.ad.image.path}")
    private String defaultAdImagePath;

    private final AdRepository adRepository;
    private final AdImageService adImageService;
    private final UserRepository userRepository;
    private final AdImageRepository adImageRepository;
    private final CommentRepository commentRepository;

    public AdsService(AdRepository adRepository,
                      AdImageService adImageService,
                      UserRepository userRepository,
                      AdImageRepository adImageRepository, CommentRepository commentRepository) {
        this.adRepository = adRepository;
        this.adImageService = adImageService;
        this.userRepository = userRepository;
        this.adImageRepository = adImageRepository;
        this.commentRepository = commentRepository;
    }

    public Ads getAllAds() {
        return adsDTOMapper.toDto(adRepository.findAll());
    }

    public Ad addAd(CreateOrUpdateAd createOrUpdateAd, MultipartFile image, String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NoUsersFoundException("Пользователей с именем пользователя " + email + " не найдено."));
        AdEntity adEntity = AdEntity.builder()
                .price(createOrUpdateAd.getPrice())
                .title(createOrUpdateAd.getTitle())
                .description(createOrUpdateAd.getDescription())
                .user(user)
                .build();

        adRepository.save(adEntity);

        if (image != null && !image.isEmpty()) {
            try {
                adImageService.uploadAdImage(adEntity.getId(), image);
            } catch (IOException e) {
                throw new RuntimeException("Ошибка при загрузке изображения", e);
            }
        } else {
            AdImage defaultImage = new AdImage();
            defaultImage.setFilePath(defaultAdImagePath);
            defaultImage.setFileSize(0);
            defaultImage.setMediaType("image/jpeg");
            defaultImage.setAd(adEntity);

            adEntity.setImage(defaultImage);
            adRepository.save(adEntity);
        }

        return adDTOMapper.toDto(adEntity);
    }

    public ExtendedAd getAdInfo(Integer id, String email) {
        AdEntity adEntity = adRepository.findById(id)
                .orElseThrow(() -> new NoAdsFoundException("По id " + id + " нет найдено объявлений."));
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NoUsersFoundException("Пользователей с именем пользователя " + email + " не найдено."));
        return extendedAdDTOMapper.toDto(adEntity, user);
    }

    public void deleteAd(Integer id) {
        adRepository.delete(adRepository.findById(id)
                .orElseThrow(() -> new NoAdsFoundException("По id " + id + " не найдено объявлений")));
    }

    public Ad updateAd(Integer id, CreateOrUpdateAd createOrUpdateAd) {
        AdEntity adEntity = adRepository.findById(id)
                .orElseThrow(() -> new NoAdsFoundException("Не найдено объявлений с id " + id));
        return createOrUpdateAdDTOMapper.updateEntityFromDto(createOrUpdateAd, adEntity);
    }

    public Ads getAdsMe(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NoUsersFoundException("Пользователей с именем пользователя " + email + " не найдено."));
        return adsDTOMapper.toDto(adRepository.findAllByUserId(user.getId()));
    }

    public String updateImage(Integer id, MultipartFile image) {
        AdEntity ad = adRepository.findById(id)
                .orElseThrow(() -> new NoAdsFoundException("Не найдено объявлений с id " + id));
        adImageRepository.deleteByAdId(ad.getId());
        try {
            adImageService.uploadAdImage(ad.getId(), image);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        AdImage newImage = adImageRepository.findByAdId(id)
                .orElseThrow(() -> new NoImagesFoundException("Не найдено изображений с объявления с id " + id));
        ad.setImage(newImage);
        return generateImageUrl(ad);
    }

    public Comments getComments(Integer id) {
        if (!adRepository.existsById(id)) {
            throw new NoAdsFoundException("По id " + id + " не найдено объявлений.");
        }
        List<CommentEntity> comments = commentRepository.findAllByAdId(id);
        if (comments.isEmpty()) {
            throw new NoCommentsException("К этому объявлению пока нет комментариев.");
        }
        return commentsDTOMapper.toDto(comments);
    }

    public Comment addComment(Integer id, CreateOrUpdateComment createOrUpdateComment, String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NoUsersFoundException("Не найдено пользователя с именем пользователя " + email));
        AdEntity ad = adRepository.findById(id)
                .orElseThrow(() -> new NoAdsFoundException("Не найдено объявлений с id " + id));
        return createOrUpdateCommentDTOMapper.createEntityFromDto(createOrUpdateComment, user, ad);
    }

    public void deleteComment(Integer adId, Integer commentId) {
        AdEntity ad = adRepository.findById(adId)
                .orElseThrow(() -> new NoAdsFoundException("Не найдено объявлений с id " + adId));
    }
}

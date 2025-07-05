package ru.skypro.homework.mappers;

import org.springframework.stereotype.Component;
import ru.skypro.homework.dto.Ad;
import ru.skypro.homework.model.AdEntity;
import ru.skypro.homework.model.AdImage;
import ru.skypro.homework.model.UserEntity;

import static ru.skypro.homework.utils.ImageURLGenerator.generateImageUrl;

/**
 * Маппер AdEntity <-> Ad
 */
@Component
public class AdDTOMapper {

    public Ad toDto(AdEntity ad) {
        if (ad == null) {
            return null;
        }
        return new Ad(
                ad.getUser().getId(),
                generateImageUrl(ad),
                ad.getId(),
                ad.getPrice(),
                ad.getTitle()
        );
    }


    public AdEntity fromDto(Ad ad, UserEntity user, AdImage adImage) {
        if (ad == null) {
            return null;
        }
        return AdEntity.builder()
                .price(ad.getPrice())
                .title(ad.getTitle())
                .description("Описания пока нет, добавьте его!")
                .user(user)
                .image(adImage)
                .build();
    }

}

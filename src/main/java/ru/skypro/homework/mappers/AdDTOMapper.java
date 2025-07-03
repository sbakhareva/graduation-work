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

    Ad toDto(AdEntity adEntity) {
        if (adEntity == null) {
            return null;
        }
        return new Ad(
                adEntity.getUserEntity().getId(),
                generateImageUrl(adEntity),
                adEntity.getId(),
                adEntity.getPrice(),
                adEntity.getTitle()
        );
    }


    AdEntity fromDto(Ad ad, UserEntity userEntity, AdImage adImage) {
        if (ad == null) {
            return null;
        }
        return AdEntity.builder()
                .price(ad.getPrice())
                .title(ad.getTitle())
                .description("Описания пока нет, добавьте его!")
                .userEntity(userEntity)
                .image(adImage) // для Жени: тут пока такое решение,
                // когда будут готовы сервисы уже лучше подумаю, норм или нет
                .build();
    }

}

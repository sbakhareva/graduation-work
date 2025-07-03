package ru.skypro.homework.mappers;

import org.springframework.stereotype.Component;
import ru.skypro.homework.dto.AdDTO;
import ru.skypro.homework.model.Ad;
import ru.skypro.homework.model.AdImage;
import ru.skypro.homework.model.User;

import static ru.skypro.homework.utils.ImageURLGenerator.generateImageUrl;

/**
 * Маппер Ad <-> AdDTO
 */
@Component
public class AdDTOMapper {

    AdDTO toDto(Ad ad) {
        if (ad == null) {
            return null;
        }
        return new AdDTO(
                ad.getUser().getId(),
                generateImageUrl(ad),
                ad.getId(),
                ad.getPrice(),
                ad.getTitle()
        );
    }


    Ad fromDto(AdDTO adDTO, User user, AdImage adImage) {
        if (adDTO == null) {
            return null;
        }
        return Ad.builder()
                .price(adDTO.getPrice())
                .title(adDTO.getTitle())
                .description("Описания пока нет, добавьте его!")
                .user(user)
                .image(adImage) // для Жени: тут пока такое решение,
                // когда будут готовы сервисы уже лучше подумаю, норм или нет
                .build();
    }

}

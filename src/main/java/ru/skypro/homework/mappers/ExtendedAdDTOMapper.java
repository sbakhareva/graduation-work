package ru.skypro.homework.mappers;

import org.springframework.stereotype.Component;
import ru.skypro.homework.dto.ExtendedAd;
import ru.skypro.homework.model.Ad;
import ru.skypro.homework.model.User;

import static ru.skypro.homework.utils.ImageURLGenerator.generateImageUrl;

@Component
public class ExtendedAdDTOMapper {


    // не думаю, что такой маппер будет актуален, extendedAd возвращается один раз в контроллере
    public Ad fromDto(ExtendedAd extendedAd, User user) {
        if (extendedAd == null) {
            return null;
        }
        return Ad.builder()
                .price(extendedAd.getPrice())
                .title(extendedAd.getTitle())
                .description(extendedAd.getDescription())
                .user(user)
                .build();
    }

    public ExtendedAd toDto(Ad ad, User user) {
        if (ad == null) {
            return null;
        }
        return new ExtendedAd(
                ad.getId(),
                user.getFirstName(),
                user.getLastName(),
                ad.getDescription(),
                user.getEmail(),
                generateImageUrl(ad),
                user.getPhone(),
                ad.getPrice(),
                ad.getTitle()
        );
    }
}

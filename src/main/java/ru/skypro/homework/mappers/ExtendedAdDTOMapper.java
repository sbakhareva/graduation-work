package ru.skypro.homework.mappers;

import org.springframework.stereotype.Component;
import ru.skypro.homework.dto.ExtendedAd;
import ru.skypro.homework.model.AdEntity;
import ru.skypro.homework.model.UserEntity;

import static ru.skypro.homework.utils.ImageURLGenerator.generateImageUrl;

@Component
public class ExtendedAdDTOMapper {

    public ExtendedAd toDto(AdEntity adEntity, UserEntity userEntity) {
        if (adEntity == null) {
            return null;
        }
        return new ExtendedAd(
                adEntity.getId(),
                userEntity.getFirstName(),
                userEntity.getLastName(),
                adEntity.getDescription(),
                userEntity.getEmail(),
                generateImageUrl(adEntity),
                userEntity.getPhone(),
                adEntity.getPrice(),
                adEntity.getTitle()
        );
    }
}

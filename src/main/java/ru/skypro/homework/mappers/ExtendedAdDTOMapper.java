package ru.skypro.homework.mappers;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;
import ru.skypro.homework.dto.ExtendedAd;
import ru.skypro.homework.model.AdEntity;
import ru.skypro.homework.model.UserEntity;

import static ru.skypro.homework.utils.ImageURLGenerator.generateImageUrl;

@Component
@Transactional
public class ExtendedAdDTOMapper {

    public ExtendedAd toDto(AdEntity ad, UserEntity user) {
        if (ad == null) {
            return null;
        }
        return new ExtendedAd(
                ad.getId(),
                user.getFirstName(),
                user.getLastName(),
                ad.getDescription(),
                user.getEmail(),
                generateImageUrl(user),
                user.getPhone(),
                ad.getPrice(),
                ad.getTitle()
        );
    }
}

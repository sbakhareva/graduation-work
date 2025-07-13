package ru.skypro.homework.mappers;

import jakarta.transaction.Transactional;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Component;
import ru.skypro.homework.dto.ExtendedAd;
import ru.skypro.homework.model.AdEntity;
import ru.skypro.homework.model.UserEntity;

import static ru.skypro.homework.utils.ImageURLGenerator.generateAdImageUrl;

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
                generateAdImageUrl(ad.getId(), hasImage(ad)),
                user.getPhone(),
                ad.getPrice(),
                ad.getTitle()
        );
    }

    private boolean hasImage(AdEntity ad) {
        return Hibernate.isInitialized(ad.getImage()) && ad.getImage() != null;
    }
}

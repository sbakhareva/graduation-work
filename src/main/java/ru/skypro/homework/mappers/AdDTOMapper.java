package ru.skypro.homework.mappers;

import jakarta.transaction.Transactional;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Component;
import ru.skypro.homework.dto.Ad;
import ru.skypro.homework.model.AdEntity;

import static ru.skypro.homework.utils.ImageURLGenerator.generateAdImageUrl;

/**
 * Маппер AdEntity <-> Ad
 */
@Component
@Transactional
public class AdDTOMapper {

    public Ad toDto(AdEntity ad) {
        if (ad == null) {
            return null;
        }
        return new Ad(
                ad.getUser().getId(),
                generateAdImageUrl(ad.getImage(), hasImage(ad)),
                ad.getId(),
                ad.getPrice(),
                ad.getTitle()
        );
    }

    private boolean hasImage(AdEntity ad) {
        return Hibernate.isInitialized(ad.getImage()) && ad.getImage() != null;
    }
}

package ru.skypro.homework.mappers;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;
import ru.skypro.homework.dto.Ad;
import ru.skypro.homework.model.AdEntity;

import static ru.skypro.homework.utils.ImageURLGenerator.generateImageUrl;

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
                generateImageUrl(ad),
                ad.getId(),
                ad.getPrice(),
                ad.getTitle()
        );
    }
}

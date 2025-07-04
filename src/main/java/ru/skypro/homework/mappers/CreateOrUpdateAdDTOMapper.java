package ru.skypro.homework.mappers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.skypro.homework.dto.Ad;
import ru.skypro.homework.dto.CreateOrUpdateAd;
import ru.skypro.homework.model.AdEntity;

/**
 * Этот маппер позволяет обновить поля сущности AdEntity из CreateOrUpdateAd,
 * чтобы затем с помощью AdDTOMapper была возможность вернуть в ответе Ad с обновленными полями
 */
@Component
public class CreateOrUpdateAdDTOMapper {

    @Autowired
    private final AdDTOMapper adDTOMapper = new AdDTOMapper();

    public Ad updateEntityFromDto(CreateOrUpdateAd createOrUpdateAd, AdEntity ad) {
        if (createOrUpdateAd.getDescription() != null && !createOrUpdateAd.getDescription().isBlank()) {
            ad.setDescription(createOrUpdateAd.getDescription());
        }
        if (createOrUpdateAd.getPrice() != null) {
            ad.setPrice(createOrUpdateAd.getPrice());
        }
        if (createOrUpdateAd.getTitle() != null) {
            ad.setTitle(createOrUpdateAd.getTitle());
        }
        return adDTOMapper.toDto(ad);
    }
}

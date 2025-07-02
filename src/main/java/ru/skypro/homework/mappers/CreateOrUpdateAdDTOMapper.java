package ru.skypro.homework.mappers;

import org.springframework.stereotype.Component;
import ru.skypro.homework.dto.CreateOrUpdateAd;
import ru.skypro.homework.model.Ad;

/**
 * Этот маппер позволяет обновить поля сущности Ad из CreateOrUpdateAd,
 * чтобы затем с помощью AdDTOMapper была возможность вернуть в ответе AdDTO с обновленными полями
 */
@Component
public class CreateOrUpdateAdDTOMapper {

    public void updateEntityFromDto(CreateOrUpdateAd createOrUpdateAd, Ad ad) {
        if (createOrUpdateAd.getDescription() != null && !createOrUpdateAd.getDescription().isBlank()) {
            ad.setDescription(createOrUpdateAd.getDescription());
        }
        if (createOrUpdateAd.getPrice() != null) {
            ad.setPrice(createOrUpdateAd.getPrice());
        }
        if (createOrUpdateAd.getTitle() != null) {
            ad.setTitle(createOrUpdateAd.getTitle());
        }
    }
}

package ru.skypro.homework.mappers;

import org.springframework.stereotype.Component;
import ru.skypro.homework.dto.CreateOrUpdateAd;
import ru.skypro.homework.model.AdEntity;

/**
 * Этот маппер позволяет обновить поля сущности AdEntity из CreateOrUpdateAd,
 * чтобы затем с помощью AdDTOMapper была возможность вернуть в ответе Ad с обновленными полями
 */
@Component
public class CreateOrUpdateAdDTOMapper {

    public void updateEntityFromDto(CreateOrUpdateAd createOrUpdateAd, AdEntity adEntity) {
        if (createOrUpdateAd.getDescription() != null && !createOrUpdateAd.getDescription().isBlank()) {
            adEntity.setDescription(createOrUpdateAd.getDescription());
        }
        if (createOrUpdateAd.getPrice() != null) {
            adEntity.setPrice(createOrUpdateAd.getPrice());
        }
        if (createOrUpdateAd.getTitle() != null) {
            adEntity.setTitle(createOrUpdateAd.getTitle());
        }
    }
}

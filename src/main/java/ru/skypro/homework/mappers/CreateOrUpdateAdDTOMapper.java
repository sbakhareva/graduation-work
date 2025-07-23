package ru.skypro.homework.mappers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.skypro.homework.dto.Ad;
import ru.skypro.homework.dto.CreateOrUpdateAd;
import ru.skypro.homework.model.AdEntity;
import ru.skypro.homework.model.UserEntity;

/**
 * Этот маппер позволяет обновить поля сущности AdEntity из CreateOrUpdateAd,
 * чтобы затем с помощью AdDTOMapper была возможность вернуть в ответе Ad с обновленными полями
 */

@Component
public class CreateOrUpdateAdDTOMapper {

    private final AdDTOMapper adDTOMapper;

    public CreateOrUpdateAdDTOMapper(AdDTOMapper adDTOMapper) {
        this.adDTOMapper = adDTOMapper;
    }

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

    public AdEntity createEntityFromDto(CreateOrUpdateAd createOrUpdateAd, UserEntity userEntity) {
        return AdEntity.builder()
                .price(createOrUpdateAd.getPrice())
                .title(createOrUpdateAd.getTitle())
                .description(createOrUpdateAd.getDescription())
                .user(userEntity)
                .build();
    }
}

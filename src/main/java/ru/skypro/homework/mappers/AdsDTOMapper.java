package ru.skypro.homework.mappers;

import org.springframework.stereotype.Component;
import ru.skypro.homework.dto.Ad;
import ru.skypro.homework.dto.Ads;
import ru.skypro.homework.model.AdEntity;

import java.util.Collections;
import java.util.List;

@Component
public class AdsDTOMapper {

    private final AdDTOMapper adDTOMapper;

    public AdsDTOMapper(AdDTOMapper adDTOMapper) {
        this.adDTOMapper = adDTOMapper;
    }

    public Ads toDto(List<AdEntity> adEntities) {
        if (adEntities == null || adEntities.isEmpty()) {
            return new Ads(0, Collections.emptyList());
        }
        List<Ad> adsDto = adEntities.stream()
                .map(adDTOMapper::toDto)
                .toList();

        return new Ads(adsDto.size(), adsDto);
    }
}

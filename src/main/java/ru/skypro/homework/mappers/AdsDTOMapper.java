package ru.skypro.homework.mappers;

import org.springframework.stereotype.Component;
import ru.skypro.homework.dto.AdDTO;
import ru.skypro.homework.dto.Ads;
import ru.skypro.homework.model.Ad;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AdsDTOMapper {

    private final AdDTOMapper adDTOMapper;

    public AdsDTOMapper(AdDTOMapper adDTOMapper) {
        this.adDTOMapper = adDTOMapper;
    }

    public Ads toDto(List<Ad> ads) {
        if (ads == null || ads.isEmpty()) {
            return new Ads(0, Collections.emptyList());
        }
        List<AdDTO> adsDto = ads.stream()
                .map(adDTOMapper::toDto)
                .toList();

        return new Ads(adsDto.size(), adsDto);
    }
}

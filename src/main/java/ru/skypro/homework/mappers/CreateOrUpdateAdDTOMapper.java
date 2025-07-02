package ru.skypro.homework.mappers;

import org.springframework.stereotype.Component;
import ru.skypro.homework.dto.AdDTO;
import ru.skypro.homework.dto.CreateOrUpdateAd;

@Component
public class CreateOrUpdateAdDTOMapper {

    public AdDTO fromDto(CreateOrUpdateAd createOrUpdateAd,
                         Integer adId,
                         Integer authorId,
                         String imagePath) {
        if (createOrUpdateAd == null) {
            return null;
        }
        // по хорошему нужны репозитории для вытаскивания adDto из обновленной ad
        return new AdDTO(
                authorId,
                imagePath,
                adId,
                createOrUpdateAd.getPrice(),
                createOrUpdateAd.getTitle()
        );
    }
}

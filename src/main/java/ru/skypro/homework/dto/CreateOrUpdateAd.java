package ru.skypro.homework.dto;

import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class CreateOrUpdateAd {

    @Size(min = 4, max = 32)
    private String title;
    @Size(min = 0, max = 10000000)
    private Integer price;
    @Size(min = 8, max = 64)
    private String description;

    // Метод для преобразования из сущности в DTO
    public static CreateOrUpdateAd fromEntity(ru.skypro.homework.model.Ad adEntity) {
        if (adEntity == null) {
            return null;
        }
        CreateOrUpdateAd createOrUpdateAd = new CreateOrUpdateAd();
        createOrUpdateAd.setTitle(adEntity.getTitle());
        createOrUpdateAd.setPrice(adEntity.getPrice());
        createOrUpdateAd.setDescription(adEntity.getDescription());
        return createOrUpdateAd;
    }

    // Метод для преобразования из DTO в сущность
    public static ru.skypro.homework.model.Ad toEntity(CreateOrUpdateAd adDto) {
        if (adDto == null) {
            return null;
        }
        ru.skypro.homework.model.Ad adEntity = new ru.skypro.homework.model.Ad();
        adEntity.setTitle(adDto.getTitle());
        adEntity.setPrice(adDto.getPrice());
        adEntity.setDescription(adDto.getDescription());
        // Установка изображения и пользователя может быть добавлена здесь, если необходимо
        return adEntity;
    }
}

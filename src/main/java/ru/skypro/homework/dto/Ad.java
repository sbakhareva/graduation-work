package ru.skypro.homework.dto;

import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class Ad {

    private Integer author; // id автора объявления
    private String image;
    private Integer pk; // id объявления
    @Size(min = 0, max = 10000000)
    private Integer price;
    @Size(min = 4, max = 32)
    private String title;
    private String description; // Добавлено поле для описания

    // Метод для преобразования из сущности в DTO
    public static Ad fromEntity(ru.skypro.homework.model.Ad adEntity) {
        if (adEntity == null) {
            return null;
        }
        Ad ad = new Ad();
        ad.setAuthor(adEntity.getUser() != null ? adEntity.getUser().getId() : null);
        ad.setImage(adEntity.getImage() != null ? adEntity.getImage().toString() : null); // Преобразуйте AdImage в строку, если необходимо
        ad.setPk(adEntity.getId());
        ad.setPrice(adEntity.getPrice());
        ad.setTitle(adEntity.getTitle());
        ad.setDescription(adEntity.getDescription()); // Преобразование описания
        return ad;
    }

    // Метод для преобразования из DTO в сущность
    public static ru.skypro.homework.model.Ad toEntity(Ad adDto) {
        if (adDto == null) {
            return null;
        }
        ru.skypro.homework.model.Ad adEntity = new ru.skypro.homework.model.Ad();
        adEntity.setId(adDto.getPk());
        adEntity.setPrice(adDto.getPrice());
        adEntity.setTitle(adDto.getTitle());
        adEntity.setDescription(adDto.getDescription()); // Установка описания
        // Здесь нужно установить пользователя, если он доступен
        // adEntity.setUser(new User(adDto.getAuthor()));
        return adEntity;
    }
}



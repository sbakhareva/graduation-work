package ru.skypro.homework.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class ExtendedAd {
    private Integer pk;
    private String firstName;
    private String lastName;
    private String description;
    private String email;
    private String image;
    private String phone;
    private Integer price;
    private String title;

    // Метод для преобразования из сущности в DTO
    public static ExtendedAd fromEntity(ru.skypro.homework.model.Ad adEntity) {
        if (adEntity == null) {
            return null;
        }
        ExtendedAd extendedAd = new ExtendedAd();
        extendedAd.setPk(adEntity.getId());
        extendedAd.setTitle(adEntity.getTitle());
        extendedAd.setDescription(adEntity.getDescription());
        extendedAd.setPrice(adEntity.getPrice());
        extendedAd.setImage(adEntity.getImage() != null ? adEntity.getImage().toString() : null); // Преобразуйте AdImage в строку, если необходимо

        // Установка данных пользователя, если они доступны
        if (adEntity.getUser() != null) {
            extendedAd.setFirstName(adEntity.getUser().getFirstName());
            extendedAd.setLastName(adEntity.getUser().getLastName());
            extendedAd.setEmail(adEntity.getUser().getEmail());
            extendedAd.setPhone(adEntity.getUser().getPhone());
        }

        return extendedAd;
    }

    // Метод для преобразования из DTO в сущность
    public static ru.skypro.homework.model.Ad toEntity(ExtendedAd extendedAdDto) {
        if (extendedAdDto == null) {
            return null;
        }
        ru.skypro.homework.model.Ad adEntity = new ru.skypro.homework.model.Ad();
        adEntity.setId(extendedAdDto.getPk());
        adEntity.setTitle(extendedAdDto.getTitle());
        adEntity.setDescription(extendedAdDto.getDescription());
        adEntity.setPrice(extendedAdDto.getPrice());
        // Установка изображения может быть добавлена здесь, если необходимо
        // adEntity.setImage(new AdImage(extendedAdDto.getImage())); // Пример, если у вас есть конструктор в AdImage
        return adEntity;
    }
}

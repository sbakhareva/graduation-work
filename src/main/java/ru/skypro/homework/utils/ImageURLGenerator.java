package ru.skypro.homework.utils;

import ru.skypro.homework.model.AdEntity;
import ru.skypro.homework.model.UserEntity;

public class ImageURLGenerator {
    public static String generateImageUrl(AdEntity adEntity) {
        if (adEntity.getId() != null && adEntity.getId() > 0 && adEntity.getImage() != null) {
            return String.format("/adEntities/%d/image", adEntity.getId());
        } else {
            return "/adEntities-images/default-adEntity-image.jpeg";
        }
    }

    public static String generateImageUrl(UserEntity userEntity) {
        if (userEntity.getId() != null && userEntity.getId() > 0 && userEntity.getImage() != null) {
            return String.format("/users/%d/image", userEntity.getId());
        } else {
            return "/users-images/default-userEntity-image.jpeg";
        }
    }
}

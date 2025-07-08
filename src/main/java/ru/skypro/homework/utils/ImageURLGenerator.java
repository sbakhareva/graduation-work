package ru.skypro.homework.utils;

import org.springframework.stereotype.Component;
import ru.skypro.homework.model.AdEntity;
import ru.skypro.homework.model.UserEntity;

@Component
public class ImageURLGenerator {
    public static String generateImageUrl(AdEntity adEntity) {
        if (adEntity.getId() != null && adEntity.getId() > 0 && adEntity.getImage() != null) {
            return String.format("/ads-images/%d/image", adEntity.getId());
        } else {
            return "ads-images/default-ad-image.jpeg";
        }
    }

    public static String generateImageUrl(UserEntity userEntity) {
        if (userEntity.getId() != null && userEntity.getId() > 0 && userEntity.getImage() != null) {
            return String.format("/users-images/%d/image", userEntity.getId());
        } else {
            return "users-images/default-user-image.jpeg";
        }
    }
}

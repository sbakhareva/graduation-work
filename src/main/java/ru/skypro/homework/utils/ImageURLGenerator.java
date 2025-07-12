package ru.skypro.homework.utils;

import org.springframework.stereotype.Component;
import ru.skypro.homework.model.AdEntity;
import ru.skypro.homework.model.UserEntity;

@Component
public class ImageURLGenerator {
    public static String generateImageUrl(AdEntity adEntity) {
        if (adEntity.getId() != null && adEntity.getId() > 0 && adEntity.getImage() != null) {
            return adEntity.getImage().getFilePath();
        } else {
            return "/images/default-ad-image.png";
        }
    }

    public static String generateImageUrl(UserEntity userEntity) {
        if (userEntity.getId() != null && userEntity.getId() > 0 && userEntity.getImage() != null) {
            return userEntity.getImage().getFilePath();
        } else {
            return "/images/default-user-image.png";
        }
    }
}
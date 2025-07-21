package ru.skypro.homework.utils;

import org.springframework.stereotype.Component;
import ru.skypro.homework.model.AdImage;
import ru.skypro.homework.model.UserImage;

@Component
public class ImageURLGenerator {
    public static String generateAdImageUrl(AdImage image, boolean hasImage) {
        if (image != null && hasImage) {
            return "/ads/ads-images/" + image.getId();
        } else {
            return "/images/default-ad-image.png";
        }
    }

    public static String generateUserImageUrl(UserImage image) {
        if (image != null) {
            return "/users/users-images/" + image.getId();
        } else {
            return "/images/default-user-image.png";
        }
    }
}
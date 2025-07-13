package ru.skypro.homework.utils;

import org.springframework.stereotype.Component;

@Component
public class ImageURLGenerator {
    public static String generateAdImageUrl(Integer adId, boolean hasImage) {
        if (adId != null && adId > 0 && hasImage) {
            return String.format("ads-images/%d", adId);
        } else {
            return "/images/default-ad-image.png";
        }
    }

    public static String generateUserImageUrl(Integer userId, boolean hasImage) {
        if (userId != null && userId > 0 && hasImage) {
            return String.format("user-images/%d", userId);
        } else {
            return "/images/default-user-image.png";
        }
    }
}
package ru.skypro.homework.utils;

import ru.skypro.homework.model.Ad;
import ru.skypro.homework.model.User;

public class ImageURLGenerator {
    public static String generateImageUrl(Ad ad) {
        if (ad.getId() != null && ad.getId() > 0 && ad.getImage() != null) {
            return String.format("/ads/%d/image", ad.getId());
        } else {
            return "/ads-images/default-ad-image.jpeg";
        }
    }

    public static String generateImageUrl(User user) {
        if (user.getId() != null && user.getId() > 0 && user.getImage() != null) {
            return String.format("/users/%d/image", user.getId());
        } else {
            return "/users-images/default-user-image.jpeg";
        }
    }
}

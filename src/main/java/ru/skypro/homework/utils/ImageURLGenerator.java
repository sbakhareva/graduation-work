package ru.skypro.homework.utils;

import ru.skypro.homework.model.Ad;
import ru.skypro.homework.model.User;

public class ImageURLGenerator {
    public static String generateImageUrl(Ad ad) {
        if (ad.getId() != null && ad.getImage() != null) {
            return "/ads/" + ad.getId() + "/image";
        } else {
            return null;
        }
    }

    public static String generateImageUrl(User user) {
        if (user.getId() != null && user.getImage() != null) {
            return "/ads/" + user.getId() + "/image";
        } else {
            return null;
        }
    }
}

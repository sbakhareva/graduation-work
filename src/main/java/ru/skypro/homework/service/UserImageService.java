package ru.skypro.homework.service;

import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.model.UserImage;

import java.io.IOException;

public interface UserImageService {

    /**
     * Загружает изображение для пользователя
     * @param userId ID пользователя
     * @param image файл изображения
     * @throws IOException при ошибке работы с файлом
     */
    void uploadUserImage(Integer userId, MultipartFile image) throws IOException;

    //Получает изображение пользователя

    UserImage getUserImage(Integer userId);

   //Удаляет изображение пользователя

    void deleteUserImage(Integer userId);

    // Проверяет существование изображения пользователя

    boolean userImageExists(Integer userId);

    // Получает размер файла изображения пользователя

    long getUserImageSize(Integer userId);

    // Получает MIME-тип изображения пользователя

    String getUserImageMediaType(Integer userId);
}

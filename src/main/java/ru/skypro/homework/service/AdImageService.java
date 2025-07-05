package ru.skypro.homework.service;

import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.model.AdImage;

import java.io.IOException;

/**
 * Сервис для работы с изображениями объявлений
 */
public interface AdImageService {

    /**
     * Загружает изображение для объявления
     * @param adId ID объявления
     * @param image файл изображения
     * @throws IOException при ошибке работы с файлом
     */
    void uploadAdImage(Integer adId, MultipartFile image) throws IOException;

    /**
     * Получает изображение объявления
     * @param adId ID объявления
     * @return объект AdImage
     */
    AdImage getAdImage(Integer adId);

    /**
     * Удаляет изображение объявления
     * @param adId ID объявления
     */
    void deleteAdImage(Integer adId);

    /**
     * Проверяет существование изображения объявления
     * @param adId ID объявления
     * @return true если изображение существует
     */
    boolean adImageExists(Integer adId);

    /**
     * Получает размер файла изображения объявления
     * @param adId ID объявления
     * @return размер файла в байтах
     */
    long getAdImageSize(Integer adId);

    /**
     * Получает MIME-тип изображения объявления
     * @param adId ID объявления
     * @return MIME-тип файла
     */
    String getAdImageMediaType(Integer adId);

    /**
     * Получает превью изображения объявления
     * @param adId ID объявления
     * @return массив байтов превью
     */
    byte[] getAdImagePreview(Integer adId);
}

package ru.skypro.homework.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.exception.*;
import ru.skypro.homework.model.AdEntity;
import ru.skypro.homework.model.AdImage;
import ru.skypro.homework.repository.AdImageRepository;
import ru.skypro.homework.repository.AdRepository;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static java.nio.file.StandardOpenOption.CREATE_NEW;

@Service
@Transactional
public class AdImageService {

    private static final Logger logger = LoggerFactory.getLogger(AdImageService.class);

    @Value("${adEntities.image.dir.path}")
    private String adsImageDirectory;

    private static final List<String> ALLOWED_TYPES = Arrays.asList("image/jpeg", "image/png", "image/jpg");
    private static final long MAX_FILE_SIZE = 1024 * 1024 * 5; // 5MB

    private final AdImageRepository adImageRepository;
    private final AdRepository adRepository;

    public AdImageService(AdImageRepository adImageRepository, 
                         AdRepository adRepository) {
        this.adImageRepository = adImageRepository;
        this.adRepository = adRepository;
    }

    public AdImage getImage(Integer id) {
        return adImageRepository.findById(id)
                .orElseThrow(() -> new NoImagesFoundException("Не найдено картинок с id " + id));
    }

    public void uploadAdImage(Integer adId, MultipartFile image) throws IOException {
        // Проверяем существование объявления
        AdEntity ad = adRepository.findById(adId)
                .orElseThrow(() -> new NoAdsFoundException(adId));

        // Валидируем изображение
        validateImage(image);

        // Удаляем старое изображение из БД если есть
        adImageRepository.findByAdId(adId).ifPresent(adImageRepository::delete);

        // Сохраняем изображение на диск
        Path filePath = saveAdImage(adId, image);
        
        // Генерируем превью
        byte[] preview = generateImagePreview(filePath);

        // Создаем новую запись в БД
        AdImage adImage = new AdImage();
        adImage.setAd(ad);
        adImage.setFileSize(image.getSize());
        adImage.setMediaType(image.getContentType());
        adImage.setPreview(preview);
        adImage.setFilePath("/ads-images/" + adId);

        adImageRepository.save(adImage);
        logger.info("Изображение объявления {} успешно загружено", adId);
    }

    public AdImage getAdImage(Integer adId) {
        return adImageRepository.findByAdId(adId)
                .orElse(new AdImage());
    }

    public void deleteAdImageFile(Integer adId) {
        AdImage adImage = adImageRepository.findByAdId(adId)
                .orElseThrow(() -> new NoImagesFoundException("Картинки для объявления " + adId + " не найдены"));

        // Удаляем файл с диска
        if (adImage.getFilePath() != null) {
            Path filePath = Path.of(adImage.getFilePath().replace("/ads-images/", "ads-images/"));
            deleteImageFile(filePath);
        }

        // Удаляем запись из БД
        adImageRepository.delete(adImage);
        logger.info("Изображение объявления {} удалено", adId);
    }

    /**
     * Получение изображения для отображения
     */
    public byte[] getImageBytes(Integer id) throws IOException {
        AdImage adImage = getImage(id);
        
        if (adImage.getFilePath() != null && !adImage.getFilePath().isEmpty()) {
            // Пытаемся получить файл с диска
            Path filePath = Path.of(adImage.getFilePath().replace("/ads-images/", "ads-images/"));
            try {
                return getImageBytesFromFile(filePath);
            } catch (IOException e) {
                logger.warn("Не удалось прочитать файл изображения {}, используем превью", filePath);
            }
        }
        
        // Возвращаем превью из БД
        return adImage.getPreview();
    }

    /**
     * Валидация загружаемого изображения
     */
    private void validateImage(MultipartFile image) {
        if (!ALLOWED_TYPES.contains(image.getContentType())) {
            throw new InvalidFileTypeException();
        }
        if (image.getSize() > MAX_FILE_SIZE) {
            throw new FileSizeExceededException();
        }
    }

    /**
     * Сохранение изображения объявления
     */
    private Path saveAdImage(Integer adId, MultipartFile image) throws IOException {
        String extension = getExtension(Objects.requireNonNull(image.getOriginalFilename()));
        Path filePath = Path.of(adsImageDirectory, adId + "." + extension);
        
        // Создаем директории если их нет
        Files.createDirectories(filePath.getParent());
        Files.deleteIfExists(filePath);

        // Сохраняем файл
        try (InputStream is = image.getInputStream();
             OutputStream os = Files.newOutputStream(filePath, CREATE_NEW);
             BufferedInputStream bis = new BufferedInputStream(is, 1024);
             BufferedOutputStream bos = new BufferedOutputStream(os, 1024)) {
            bis.transferTo(bos);
        }

        logger.info("Изображение объявления сохранено: {}", filePath);
        return filePath;
    }

    /**
     * Генерация превью изображения
     */
    private byte[] generateImagePreview(Path filePath) throws IOException {
        try (InputStream is = Files.newInputStream(filePath);
             BufferedInputStream bis = new BufferedInputStream(is, 1024);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            
            BufferedImage image = ImageIO.read(bis);
            if (image == null) {
                throw new IOException("Не удалось прочитать изображение из файла: " + filePath);
            }

            // Создаем превью размером 100px по ширине
            int previewWidth = 100;
            int previewHeight = (int) ((double) image.getHeight() / image.getWidth() * previewWidth);
            
            BufferedImage preview = new BufferedImage(previewWidth, previewHeight, image.getType());
            Graphics2D graphics = preview.createGraphics();
            try {
                graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                graphics.drawImage(image, 0, 0, previewWidth, previewHeight, null);
            } finally {
                graphics.dispose();
            }

            String extension = getExtension(filePath.getFileName().toString());
            ImageIO.write(preview, extension, baos);
            return baos.toByteArray();
        }
    }

    /**
     * Получение расширения файла
     */
    private String getExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    /**
     * Удаление файла изображения
     */
    private void deleteImageFile(Path filePath) {
        try {
            Files.deleteIfExists(filePath);
            logger.info("Файл изображения объявления удален: {}", filePath);
        } catch (IOException e) {
            logger.error("Ошибка при удалении файла изображения объявления: {}", filePath, e);
        }
    }

    /**
     * Получение изображения по пути
     */
    private byte[] getImageBytesFromFile(Path filePath) throws IOException {
        return Files.readAllBytes(filePath);
    }
}

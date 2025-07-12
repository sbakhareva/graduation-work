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
import java.util.*;
import java.util.List;

import static java.nio.file.StandardOpenOption.CREATE_NEW;

@Service
@Transactional
public class AdImageService {

    private static final Logger logger = LoggerFactory.getLogger(AdImageService.class);

    @Value("${adEntities.image.dir.path}")
    private String directory;
    private static final List<String> ALLOWED_TYPES = Arrays.asList("image/jpeg", "image/png", "image/jpg");
    private final long MAX_FILE_SIZE = 1024 * 1024 * 5;
    private final AdImageRepository adImageRepository;
    private final AdRepository adRepository;

    public AdImageService(AdImageRepository adImageRepository, AdRepository adRepository) {
        this.adImageRepository = adImageRepository;
        this.adRepository = adRepository;
    }

    public AdImage getImage(Integer id) {
        return adImageRepository.findById(id)
                .orElseThrow(() -> new NoImagesFoundException("Не найдено картинок с id " + id));
    }

    public void uploadAdImage(Integer adId, MultipartFile image) throws IOException {
        if (!ALLOWED_TYPES.contains(image.getContentType())) {
            throw new InvalidFileTypeException();
        }
        if (image.getSize() > MAX_FILE_SIZE) {
            throw new FileSizeExceededException();
        }
        Optional<AdEntity> ad = Optional.of(adRepository.findById(adId)
                .orElseThrow(() -> new NoAdsFoundException(adId)));

        Path filePath = Path.of(directory, adId + "."
                + getExtension(Objects.requireNonNull(image.getOriginalFilename())));
        Files.createDirectories(filePath.getParent());
        Files.deleteIfExists(filePath);

        try (InputStream is = image.getInputStream();
             OutputStream os = Files.newOutputStream(filePath, CREATE_NEW);
             BufferedInputStream bis = new BufferedInputStream(is, 1024);
             BufferedOutputStream bos = new BufferedOutputStream(os, 1024)
        ) {
            bis.transferTo(bos);
        }

        AdImage adImage = getAdImage(adId);
        adImage.setAd(ad.get());
        adImage.setFilePath(filePath.toString());
        adImage.setFileSize(image.getSize());
        adImage.setMediaType(image.getContentType());
        adImage.setPreview(generateImagePreview(filePath));

        adImageRepository.save(adImage);
    }

    private String getExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    public AdImage getAdImage(Integer adId) {
        return adImageRepository.findByAdId(adId)
                .orElse(new AdImage());

    }

    private byte[] generateImagePreview(Path filePath) throws IOException {
        try (InputStream is = Files.newInputStream(filePath);
             BufferedInputStream bis = new BufferedInputStream(is, 1024);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            BufferedImage image = ImageIO.read(bis);

            int height = image.getHeight() / (image.getWidth() / 100);
            BufferedImage preview = new BufferedImage(100, height, image.getType());
            Graphics2D graphics = preview.createGraphics();
            graphics.drawImage(image, 0, 0, 100, height, null);
            graphics.dispose();

            ImageIO.write(preview, getExtension(filePath.getFileName().toString()), baos);
            return baos.toByteArray();
        }
    }

    public void deleteAdImageFile(Integer adId) {
        AdImage adImage = adImageRepository.findByAdId(adId)
                .orElseThrow(() -> new NoImagesFoundException("Картинки для объявления " + adId + " не найдены"));

        Path filePath = Path.of(adImage.getFilePath());
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            logger.error("Ошибка при удалении файла изображения: {}", filePath);
        }
    }
}

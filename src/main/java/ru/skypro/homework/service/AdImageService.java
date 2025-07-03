package ru.skypro.homework.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.exception.FileSizeExceededException;
import ru.skypro.homework.exception.InvalidFileTypeException;
import ru.skypro.homework.exception.NoAdsFoundException;
import ru.skypro.homework.exception.NoImagesFoundException;
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
import java.util.Optional;

import static java.nio.file.StandardOpenOption.CREATE_NEW;

@Service
@Transactional
public class AdImageService {

    @Value("${adEntities.image.dir.path}")
    private String directory;
    private static final List<String> ALLOWED_TYPES =  Arrays.asList("image/jpeg", "image/png", "image/jpg");
    private final long MAX_FILE_SIZE = 1024 * 1024 * 5;
    private final AdImageRepository adImageRepository;
    private final AdRepository adRepository;

    public AdImageService(AdImageRepository adImageRepository, AdRepository adRepository) {
        this.adImageRepository = adImageRepository;
        this.adRepository = adRepository;
    }

    public void uploadAdImage(Integer adId, MultipartFile image) throws IOException {
        if (!ALLOWED_TYPES.contains(image.getContentType())) {
            throw new InvalidFileTypeException("Неверный тип файла");
        }
        if (image.getSize() > MAX_FILE_SIZE) {
            throw new FileSizeExceededException("Слишком большой размер файла");
        }
        Optional<AdEntity> ad = Optional.of(adRepository.findById(adId)
                .orElseThrow(() -> new NoAdsFoundException("По id " + adId + "ничего не найдено.")));

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
        adImage.setFileSize(adImage.getFileSize());
        adImage.setMediaType(image.getContentType());
        adImage.setPreview(generateImagePreview(filePath));

        adImageRepository.save(adImage);
    }

    private String getExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    public AdImage getAdImage(Integer adId) {
        return adImageRepository.findByAdId(adId)
                .orElseThrow(() -> new NoImagesFoundException("Фотографии к объявлению по id " + adId + " не найдены."));

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
}

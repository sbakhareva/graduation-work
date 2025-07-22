package ru.skypro.homework.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.exception.*;
import ru.skypro.homework.model.AdEntity;
import ru.skypro.homework.model.AdImage;
import ru.skypro.homework.repository.AdImageRepository;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.service.ImageService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.List;

import static java.nio.file.StandardOpenOption.CREATE_NEW;

/**
 * Сервис для работы с изображениями объявлений.
 * Содержит методы для сохранения изображений в базу данных, получения байтов изображения по ссылке
 * на файл в файловой системе и удаления устаревших изображений из файловой системы.
 */
@Service
@Transactional
public class AdImageService implements ImageService {

    private static final Logger logger = LoggerFactory.getLogger(AdImageService.class);

    @Value("${adEntities.image.dir.path}")
    private String directory;
    private static final List<String> ALLOWED_TYPES = Arrays.asList("image/jpeg", "image/png", "image/jpg");
    private final long MAX_FILE_SIZE = 1024 * 1024 * 5;
    private final AdImageRepository adImageRepository;
    private final AdRepository adRepository;

    public AdImageService(AdImageRepository adImageRepository,
                          AdRepository adRepository) {
        this.adImageRepository = adImageRepository;
        this.adRepository = adRepository;
    }

    /**
     * Получает байты изображения для списка объявлений по ссылке на файл в файловой системе.
     *
     * @param id идентификатор изображения
     * @return байтовый массив для отображения изображения на фронтенде
     * @throws NoImagesFoundException, если изображение с переданным id не найдено
     */
    public ResponseEntity<byte[]> getImage(Integer id) {
        AdImage image = adImageRepository.findById(id)
                .orElseThrow(() -> new NoImagesFoundException("Не найдено картинок с id " + id));
        byte[] imageBytes;
        try {
            imageBytes = Files.readAllBytes(Path.of(image.getFilePath()));
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(image.getMediaType()));
        headers.setContentLength(imageBytes.length);

        return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
    }

    /**
     * Получает байты изображения для текущего объявления по ссылке на файл в файловой системе.
     *
     * @param adId    идентификатор объявления
     * @param imageId идентификатор изображения
     * @return байтовый массив для отображения изображения на фронтенде
     * @throws NoImagesFoundException, если изображение с переданным id не найдено
     */
    public ResponseEntity<byte[]> getThisAdImage(Integer adId, Integer imageId) {
        AdImage image = adImageRepository.findByAdId(adId)
                .orElseThrow(() -> new NoImagesFoundException("Не найдено картинок с id " + imageId + " для объявления " + adId));
        byte[] imageBytes;
        try {
            imageBytes = Files.readAllBytes(Path.of(image.getFilePath()));
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(image.getMediaType()));
        headers.setContentLength(imageBytes.length);

        return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
    }

    /**
     * Сохраняет изображение в виде {@link AdImage} в базе данных и в виде файла в файловой системе.
     *
     * @param adId  идентификатор объявления
     * @param image загруженное пользователем изображение
     * @throws InvalidFileTypeException,  если загружен файл с недопустимым типом файла
     * @throws FileSizeExceededException, если превышен допустимый размер файла
     * @throws NoAdsFoundException,       если не найдено объявлений с переданным id
     */
    @Transactional
    public void uploadImage(Integer adId, MultipartFile image) throws IOException {
        if (!ALLOWED_TYPES.contains(image.getContentType())) {
            throw new InvalidFileTypeException();
        }
        if (image.getSize() > MAX_FILE_SIZE) {
            throw new FileSizeExceededException();
        }
        Optional<AdEntity> ad = Optional.of(adRepository.findById(adId)
                .orElseThrow(() -> new NoAdsFoundException(adId)));

        adImageRepository.findByAdId(adId).ifPresent(adImageRepository::delete);

        AdImage adImage = getAdImage(adId);
        adImage.setAd(ad.get());
        adImage.setFileSize(image.getSize());
        adImage.setMediaType(image.getContentType());

        adImageRepository.save(adImage);

        String extension = getExtension(Objects.requireNonNull(image.getOriginalFilename()));
        String fullFilePath = directory + "/" + adImage.getId() + "." + extension;
        adImage.setFilePath(fullFilePath);
        adImageRepository.save(adImage);

        Path filePath = Path.of(fullFilePath);
        Files.createDirectories(filePath.getParent());
        Files.deleteIfExists(filePath);

        try (InputStream is = image.getInputStream();
             OutputStream os = Files.newOutputStream(filePath, CREATE_NEW);
             BufferedInputStream bis = new BufferedInputStream(is, 1024);
             BufferedOutputStream bos = new BufferedOutputStream(os, 1024)
        ) {
            bis.transferTo(bos);
        }
    }

    private AdImage getAdImage(Integer adId) {
        return adImageRepository.findByAdId(adId)
                .orElse(new AdImage());
    }
}

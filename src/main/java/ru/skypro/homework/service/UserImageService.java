package ru.skypro.homework.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.exception.*;
import ru.skypro.homework.model.UserEntity;
import ru.skypro.homework.model.UserImage;
import ru.skypro.homework.repository.UserImageRepository;
import ru.skypro.homework.repository.UserRepository;

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
public class UserImageService {

    private static final Logger logger = LoggerFactory.getLogger(UserImageService.class);

    @Value("${users.image.dir.path}")
    private String usersImageDirectory;

    private static final List<String> ALLOWED_TYPES = Arrays.asList("image/jpeg", "image/png", "image/jpg");
    private static final long MAX_FILE_SIZE = 1024 * 1024 * 5; // 5MB

    private final UserImageRepository userImageRepository;
    private final UserRepository userRepository;

    public UserImageService(UserImageRepository userImageRepository, 
                           UserRepository userRepository) {
        this.userImageRepository = userImageRepository;
        this.userRepository = userRepository;
    }

    public UserImage getImage(Integer id) {
        return userImageRepository.findById(id)
                .orElseThrow(() -> new NoImagesFoundException("Не найдены фото с id " + id));
    }

    public void uploadUserImage(Integer userId, MultipartFile image) throws IOException {
        // Проверяем существование пользователя
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NoUsersFoundByIdException(userId));

        // Валидируем изображение
        validateImage(image);

        // Удаляем старое изображение если есть
        userImageRepository.findByUserId(userId).ifPresent(userImageRepository::delete);

        // Сохраняем изображение на диск
        Path filePath = saveUserImage(userId, image);
        
        // Генерируем превью
        byte[] preview = generateImagePreview(filePath);

        // Создаем новую запись в БД
        UserImage userImage = new UserImage();
        userImage.setUser(user);
        userImage.setFileSize(image.getSize());
        userImage.setMediaType(image.getContentType());
        userImage.setPreview(preview);
        userImage.setFilePath("/user-images/" + userId);

        userImageRepository.save(userImage);
        logger.info("Изображение пользователя {} успешно загружено", userId);
    }

    public UserImage getUserImage(Integer userId) {
        return userImageRepository.findByUserId(userId)
                .orElse(new UserImage());
    }

    public void deleteUserImageFile(Integer userId) {
        UserImage userImage = userImageRepository.findByUserId(userId)
                .orElseThrow(() -> new NoImagesFoundException("Фото пользователя " + userId + " не найдено"));

        // Удаляем файл с диска
        if (userImage.getFilePath() != null) {
            Path filePath = Path.of(userImage.getFilePath().replace("/user-images/", "users-images/"));
            deleteImageFile(filePath);
        }

        // Удаляем запись из БД
        userImageRepository.delete(userImage);
        logger.info("Изображение пользователя {} удалено", userId);
    }

    /**
     * Получение изображения для отображения
     */
    public byte[] getImageBytes(Integer id) throws IOException {
        UserImage userImage = getImage(id);
        
        if (userImage.getFilePath() != null && !userImage.getFilePath().isEmpty()) {
            // Пытаемся получить файл с диска
            Path filePath = Path.of(userImage.getFilePath().replace("/user-images/", "users-images/"));
            try {
                return getImageBytesFromFile(filePath);
            } catch (IOException e) {
                logger.warn("Не удалось прочитать файл изображения {}, используем превью", filePath);
            }
        }
        
        // Возвращаем превью из БД
        return userImage.getPreview();
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
     * Сохранение изображения пользователя
     */
    private Path saveUserImage(Integer userId, MultipartFile image) throws IOException {
        String extension = getExtension(Objects.requireNonNull(image.getOriginalFilename()));
        Path filePath = Path.of(usersImageDirectory, userId + "." + extension);
        
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

        logger.info("Изображение пользователя сохранено: {}", filePath);
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
            logger.info("Файл изображения пользователя удален: {}", filePath);
        } catch (IOException e) {
            logger.error("Ошибка при удалении файла изображения пользователя: {}", filePath, e);
        }
    }

    /**
     * Получение изображения по пути
     */
    private byte[] getImageBytesFromFile(Path filePath) throws IOException {
        return Files.readAllBytes(filePath);
    }
}

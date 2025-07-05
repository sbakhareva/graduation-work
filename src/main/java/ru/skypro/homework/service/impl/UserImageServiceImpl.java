package ru.skypro.homework.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.exception.FileSizeExceededException;
import ru.skypro.homework.exception.InvalidFileTypeException;
import ru.skypro.homework.exception.NoImagesFoundException;
import ru.skypro.homework.exception.NoUsersFoundException;
import ru.skypro.homework.model.UserEntity;
import ru.skypro.homework.model.UserImage;
import ru.skypro.homework.repository.UserImageRepository;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.UserImageService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
public class UserImageServiceImpl implements UserImageService {

    private static final Logger logger = LoggerFactory.getLogger(UserImageServiceImpl.class);
    
    @Value("${users.image.dir.path}")
    private String directory;
    
    private static final List<String> ALLOWED_TYPES = Arrays.asList("image/jpeg", "image/png", "image/jpg");
    private static final long MAX_FILE_SIZE = 1024 * 1024 * 5; // 5MB
    
    private final UserImageRepository userImageRepository;
    private final UserRepository userRepository;

    public UserImageServiceImpl(UserImageRepository userImageRepository, UserRepository userRepository) {
        this.userImageRepository = userImageRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void uploadUserImage(Integer userId, MultipartFile image) throws IOException {
        logger.info("Начинаем загрузку изображения для пользователя с ID: {}", userId);
        
        // Валидация входных данных
        validateImageFile(image);
        
        // Проверка существования пользователя
        UserEntity user = getUserEntityById(userId);
        
        // Создание пути для файла
        Path filePath = createImageFilePath(userId, image);
        
        // Сохранение файла
        saveImageFile(image, filePath);
        
        // Создание или обновление записи в БД
        UserImage userImage = createOrUpdateUserImage(user, filePath, image);
        
        logger.info("Изображение успешно загружено для пользователя с ID: {}. Путь: {}", userId, filePath);
    }
    @Override
    public UserImage getUserImage(Integer userId) {
        logger.debug("Получение изображения для пользователя с ID: {}", userId);
        
        return userImageRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    logger.warn("Изображение для пользователя с ID {} не найдено", userId);
                    return new NoImagesFoundException("Фото пользователя по id " + userId + " не найдено.");
                });
    }

    @Override
    public void deleteUserImage(Integer userId) {
        logger.info("Удаление изображения для пользователя с ID: {}", userId);
        
        Optional<UserImage> userImageOpt = userImageRepository.findByUserId(userId);
        if (userImageOpt.isPresent()) {
            UserImage userImage = userImageOpt.get();
            
            // Удаление файла с диска
            try {
                Path filePath = Path.of(userImage.getFilePath());
                if (Files.exists(filePath)) {
                    Files.delete(filePath);
                    logger.debug("Файл изображения удален: {}", filePath);
                }
            } catch (IOException e) {
                logger.error("Ошибка при удалении файла изображения для пользователя {}: {}", userId, e.getMessage());
            }
            
            // Удаление записи из БД
            userImageRepository.delete(userImage);
            logger.info("Изображение пользователя с ID {} успешно удалено", userId);
        } else {
            logger.warn("Изображение для пользователя с ID {} не найдено для удаления", userId);
        }
    }

    @Override
    public boolean userImageExists(Integer userId) {
        return userImageRepository.findByUserId(userId).isPresent();
    }

    @Override
    public long getUserImageSize(Integer userId) {
        UserImage userImage = getUserImage(userId);
        return userImage.getFileSize();
    }

    @Override
    public String getUserImageMediaType(Integer userId) {
        UserImage userImage = getUserImage(userId);
        return userImage.getMediaType();
    }

    private void validateImageFile(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            logger.error("Попытка загрузки пустого файла");
            throw new IllegalArgumentException("Файл не может быть пустым");
        }
        
        if (!ALLOWED_TYPES.contains(image.getContentType())) {
            logger.error("Неподдерживаемый тип файла: {}", image.getContentType());
            throw new InvalidFileTypeException("Неверный тип файла. Поддерживаются: " + ALLOWED_TYPES);
        }
        
        if (image.getSize() > MAX_FILE_SIZE) {
            logger.error("Превышен размер файла: {} байт (максимум: {} байт)", image.getSize(), MAX_FILE_SIZE);
            throw new FileSizeExceededException("Слишком большой размер файла. Максимум: " + (MAX_FILE_SIZE / 1024 / 1024) + "MB");
        }
    }

    private UserEntity getUserEntityById(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("Пользователь с ID {} не найден", userId);
                    return new NoUsersFoundException("Пользователь по id " + userId + " не найден.");
                });
    }

    private Path createImageFilePath(Integer userId, MultipartFile image) throws IOException {
        String extension = getExtension(Objects.requireNonNull(image.getOriginalFilename()));
        Path filePath = Path.of(directory, userId + "." + extension);
        
        // Создание директории если не существует
        Files.createDirectories(filePath.getParent());
        
        // Удаление старого файла если существует
        Files.deleteIfExists(filePath);
        
        return filePath;
    }

    private void saveImageFile(MultipartFile image, Path filePath) throws IOException {
        Files.copy(image.getInputStream(), filePath);
        logger.debug("Файл сохранен: {}", filePath);
    }

    private UserImage createOrUpdateUserImage(UserEntity user, Path filePath, MultipartFile image) throws IOException {
        UserImage userImage = userImageRepository.findByUserId(user.getId())
                .orElse(new UserImage());
        
        userImage.setUser(user);
        userImage.setFilePath(filePath.toString());
        userImage.setFileSize(image.getSize());
        userImage.setMediaType(image.getContentType());
        userImage.setPreview(new byte[0]); // Упрощенная версия без превью
        
        return userImageRepository.save(userImage);
    }

    private String getExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex == -1) {
            throw new IllegalArgumentException("Файл должен иметь расширение");
        }
        return fileName.substring(lastDotIndex + 1).toLowerCase();
    }
} 
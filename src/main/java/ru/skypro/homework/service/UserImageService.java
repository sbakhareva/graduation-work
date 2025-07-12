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
import java.util.Optional;

import static java.nio.file.StandardOpenOption.CREATE_NEW;

@Service
@Transactional
public class UserImageService {

    private static final Logger logger = LoggerFactory.getLogger(UserImageService.class);

    @Value("${users.image.dir.path}")
    private String directory;
    private static final List<String> ALLOWED_TYPES = Arrays.asList("image/jpeg", "image/png", "image/jpg");
    private final long MAX_FILE_SIZE = 1024 * 1024 * 5;
    private final UserImageRepository userImageRepository;
    private final UserRepository userRepository;

    public UserImageService(UserImageRepository userImageRepository, UserRepository userRepository) {
        this.userImageRepository = userImageRepository;
        this.userRepository = userRepository;
    }

    public void setDefaultUserImage(UserEntity user) throws IOException {
        File defaultImageFile = new File("src/main/resources/static/images/default-user-image.png");

        UserImage defaultImage = new UserImage();
        defaultImage.setUser(user);
        defaultImage.setFilePath(defaultImageFile.getPath());
        defaultImage.setFileSize(defaultImageFile.length());
        defaultImage.setMediaType("image/jpeg");
        defaultImage.setPreview(generateImagePreview(Path.of(defaultImage.getFilePath())));

        userImageRepository.save(defaultImage);
    }

    public void uploadUserImage(Integer userId, MultipartFile image) throws IOException {
        if (!ALLOWED_TYPES.contains(image.getContentType())) {
            throw new InvalidFileTypeException();
        }
        if (image.getSize() > MAX_FILE_SIZE) {
            throw new FileSizeExceededException();
        }
        Optional<UserEntity> user = Optional.of(userRepository.findById(userId)
                .orElseThrow(() -> new NoUsersFoundByIdException(userId)));

        Path filePath = Path.of(directory, userId + "."
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

        UserImage userImage = getUserImage(userId);
        userImage.setUser(user.get());
        userImage.setFilePath(filePath.toString());
        userImage.setFileSize(userImage.getFileSize());
        userImage.setMediaType(image.getContentType());
        userImage.setPreview(generateImagePreview(filePath));

        userImageRepository.save(userImage);
    }

    private String getExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    public UserImage getUserImage(Integer userId) {
        return userImageRepository.findByUserId(userId)
                .orElse(new UserImage());
    }

    private byte[] generateImagePreview(Path filePath) throws IOException {
        try (InputStream is = Files.newInputStream(filePath);
             BufferedInputStream bis = new BufferedInputStream(is, 1024);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            BufferedImage image = ImageIO.read(bis);
            if (image == null) {
                throw new IOException("Не удалось прочитать изображение из файла: " + filePath);
            }

            int previewWidth = 100;
            int previewHeight = (int) ((double) image.getHeight() / image.getWidth() * previewWidth);
            BufferedImage preview = new BufferedImage(100, previewHeight, image.getType());
            Graphics2D graphics = preview.createGraphics();
            try {
                graphics.drawImage(image, 0, 0, 100, previewHeight, null);
            } finally {
                graphics.dispose();
            }

            ImageIO.write(preview, getExtension(filePath.getFileName().toString()), baos);
            return baos.toByteArray();
        }
    }

    public void deleteUserImageFile(Integer userId) {
        UserImage userImage = userImageRepository.findByUserId(userId)
                .orElseThrow(() -> new NoImagesFoundException("Фото пользователя " + userId + " не найдено"));

        Path filePath = Path.of(userImage.getFilePath());
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            logger.error("Ошибка при удалении файла изображения: {}", filePath);
        }
    }
}

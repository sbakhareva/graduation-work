package ru.skypro.homework.service.impl;

import lombok.AllArgsConstructor;
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
import ru.skypro.homework.model.UserEntity;
import ru.skypro.homework.model.UserImage;
import ru.skypro.homework.repository.UserImageRepository;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.ImageService;

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
public class UserImageService implements ImageService {

    private static final Logger logger = LoggerFactory.getLogger(UserImageService.class);

    @Value("${users.image.dir.path}")
    private String directory;
    private static final List<String> ALLOWED_TYPES = Arrays.asList("image/jpeg", "image/png", "image/jpg");
    private final long MAX_FILE_SIZE = 1024 * 1024 * 5;
    private final UserImageRepository userImageRepository;
    private final UserRepository userRepository;

    public UserImageService(UserImageRepository userImageRepository,
                            UserRepository userRepository) {
        this.userImageRepository = userImageRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public ResponseEntity<byte[]> getImage(Integer id) {
        UserImage image = userImageRepository.findById(id)
                .orElseThrow(() -> new NoImagesFoundException("Не найдены фото пользователя с id " + id));
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

    @Transactional
    public void uploadImage(Integer userId, MultipartFile image) throws IOException {
        if (!ALLOWED_TYPES.contains(image.getContentType())) {
            throw new InvalidFileTypeException();
        }
        if (image.getSize() > MAX_FILE_SIZE) {
            throw new FileSizeExceededException();
        }
        Optional<UserEntity> user = Optional.of(userRepository.findById(userId)
                .orElseThrow(() -> new NoUsersFoundByIdException(userId)));

        userImageRepository.findByUserId(userId).ifPresent(userImageRepository::delete);

        UserImage userImage = getUserImage(userId);
        userImage.setUser(user.get());
        userImage.setFileSize(image.getSize());
        userImage.setMediaType(image.getContentType());

        userImageRepository.save(userImage);

        String extension = getExtension(Objects.requireNonNull(image.getOriginalFilename()));
        String fullFilePath = directory + "/" + userImage.getId() + "." + extension;
        userImage.setFilePath(fullFilePath);
        userImageRepository.save(userImage);

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

    public UserImage getUserImage(Integer userId) {
        return userImageRepository.findByUserId(userId)
                .orElse(new UserImage());
    }

    public void deleteImageFile(Integer userId) {
        UserImage userImage = userImageRepository.findByUserId(userId)
                .orElseThrow(() -> new NoImagesFoundException("Фото " + userId + " не найдено"));

        Path filePath = Path.of(userImage.getFilePath());
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            logger.error("Ошибка при удалении файла изображения: {}", filePath);
        }
    }
}

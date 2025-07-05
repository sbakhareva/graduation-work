package ru.skypro.homework.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import ru.skypro.homework.exception.FileSizeExceededException;
import ru.skypro.homework.exception.InvalidFileTypeException;
import ru.skypro.homework.exception.NoImagesFoundException;
import ru.skypro.homework.exception.NoUsersFoundException;
import ru.skypro.homework.model.UserEntity;
import ru.skypro.homework.model.UserImage;
import ru.skypro.homework.repository.UserImageRepository;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.impl.UserImageServiceImpl;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserImageServiceTest {

    @Mock
    private UserImageRepository userImageRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserImageServiceImpl userImageService;

    private UserEntity testUser;
    private UserImage testUserImage;
    private MockMultipartFile validImageFile;
    private MockMultipartFile largeImageFile;
    private MockMultipartFile invalidTypeFile;

    @BeforeEach
    void setUp() throws IOException {
        // Настройка тестовой директории
        ReflectionTestUtils.setField(userImageService, "directory", "test-users-images");

        // Создание тестового пользователя
        testUser = new UserEntity();
        testUser.setId(1);
        testUser.setEmail("test@example.com");
        testUser.setFirstName("Test");
        testUser.setLastName("User");

        // Создание тестового изображения пользователя
        testUserImage = new UserImage();
        testUserImage.setId(1);
        testUserImage.setUser(testUser);
        testUserImage.setFilePath("test-users-images/1.jpg");
        testUserImage.setFileSize(1024L);
        testUserImage.setMediaType("image/jpeg");

        // Создание реального тестового изображения в формате JPEG
        validImageFile = createTestImageFile("test-image.jpg", "image/jpeg");

        // Создание файла с превышенным размером
        largeImageFile = new MockMultipartFile(
                "image",
                "large-image.jpg",
                "image/jpeg",
                new byte[6 * 1024 * 1024] // 6MB
        );

        // Создание файла с неподдерживаемым типом
        invalidTypeFile = new MockMultipartFile(
                "image",
                "test.txt",
                "text/plain",
                "test content".getBytes()
        );
    }


     // Создает тестовое изображение в формате JPEG

    private MockMultipartFile createTestImageFile(String fileName, String contentType) throws IOException {
        // Создаем простое тестовое изображение
        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        
        // Заполняем изображение простым цветом
        for (int x = 0; x < 100; x++) {
            for (int y = 0; y < 100; y++) {
                int rgb = (x * 255 / 100) << 16 | (y * 255 / 100) << 8 | 128;
                image.setRGB(x, y, rgb);
            }
        }
        
        // Сохраняем в ByteArrayOutputStream
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", baos);
        
        return new MockMultipartFile(
                "image",
                fileName,
                contentType,
                baos.toByteArray()
        );
    }

    @Test
    void uploadUserImage_Success() throws IOException {
        // Arrange
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(userImageRepository.findByUserId(1)).thenReturn(Optional.empty());
        when(userImageRepository.save(any(UserImage.class))).thenReturn(testUserImage);

        // Act & Assert
        assertDoesNotThrow(() -> userImageService.uploadUserImage(1, validImageFile));

        // Verify
        verify(userRepository).findById(1);
        verify(userImageRepository).findByUserId(1);
        verify(userImageRepository).save(any(UserImage.class));
    }

    @Test
    void uploadUserImage_UpdateExistingImage() throws IOException {
        // Arrange
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(userImageRepository.findByUserId(1)).thenReturn(Optional.of(testUserImage));
        when(userImageRepository.save(any(UserImage.class))).thenReturn(testUserImage);

        // Act & Assert
        assertDoesNotThrow(() -> userImageService.uploadUserImage(1, validImageFile));

        // Verify
        verify(userRepository).findById(1);
        verify(userImageRepository).findByUserId(1);
        verify(userImageRepository).save(any(UserImage.class));
    }

    @Test
    void uploadUserImage_UserNotFound() {
        // Arrange
        when(userRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        NoUsersFoundException exception = assertThrows(
                NoUsersFoundException.class,
                () -> userImageService.uploadUserImage(999, validImageFile)
        );
        assertEquals("Пользователь по id 999 не найден.", exception.getMessage());
    }

    @Test
    void uploadUserImage_InvalidFileType() {
        // Act & Assert
        InvalidFileTypeException exception = assertThrows(
                InvalidFileTypeException.class,
                () -> userImageService.uploadUserImage(1, invalidTypeFile)
        );
        assertTrue(exception.getMessage().contains("Неверный тип файла"));
    }

    @Test
    void uploadUserImage_FileTooLarge() {
        // Act & Assert
        FileSizeExceededException exception = assertThrows(
                FileSizeExceededException.class,
                () -> userImageService.uploadUserImage(1, largeImageFile)
        );
        assertTrue(exception.getMessage().contains("Слишком большой размер файла"));
    }

    @Test
    void uploadUserImage_NullFile() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userImageService.uploadUserImage(1, null)
        );
        assertEquals("Файл не может быть пустым", exception.getMessage());
    }

    @Test
    void uploadUserImage_EmptyFile() {
        // Arrange
        MockMultipartFile emptyFile = new MockMultipartFile(
                "image",
                "empty.jpg",
                "image/jpeg",
                new byte[0]
        );

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userImageService.uploadUserImage(1, emptyFile)
        );
        assertEquals("Файл не может быть пустым", exception.getMessage());
    }

    @Test
    void getUserImage_Success() {
        // Arrange
        when(userImageRepository.findByUserId(1)).thenReturn(Optional.of(testUserImage));

        // Act
        UserImage result = userImageService.getUserImage(1);

        // Assert
        assertNotNull(result);
        assertEquals(testUserImage, result);
        verify(userImageRepository).findByUserId(1);
    }

    @Test
    void getUserImage_NotFound() {
        // Arrange
        when(userImageRepository.findByUserId(999)).thenReturn(Optional.empty());

        // Act & Assert
        NoImagesFoundException exception = assertThrows(
                NoImagesFoundException.class,
                () -> userImageService.getUserImage(999)
        );
        assertEquals("Фото пользователя по id 999 не найдено.", exception.getMessage());
    }

    @Test
    void deleteUserImage_Success() {
        // Arrange
        when(userImageRepository.findByUserId(1)).thenReturn(Optional.of(testUserImage));

        // Act
        userImageService.deleteUserImage(1);

        // Assert
        verify(userImageRepository).findByUserId(1);
        verify(userImageRepository).delete(testUserImage);
    }

    @Test
    void deleteUserImage_NotFound() {
        // Arrange
        when(userImageRepository.findByUserId(999)).thenReturn(Optional.empty());

        // Act
        userImageService.deleteUserImage(999);

        // Assert
        verify(userImageRepository).findByUserId(999);
        verify(userImageRepository, never()).delete(any());
    }

    @Test
    void userImageExists_True() {
        // Arrange
        when(userImageRepository.findByUserId(1)).thenReturn(Optional.of(testUserImage));

        // Act
        boolean result = userImageService.userImageExists(1);

        // Assert
        assertTrue(result);
        verify(userImageRepository).findByUserId(1);
    }

    @Test
    void userImageExists_False() {
        // Arrange
        when(userImageRepository.findByUserId(999)).thenReturn(Optional.empty());

        // Act
        boolean result = userImageService.userImageExists(999);

        // Assert
        assertFalse(result);
        verify(userImageRepository).findByUserId(999);
    }

    @Test
    void getUserImageSize_Success() {
        // Arrange
        when(userImageRepository.findByUserId(1)).thenReturn(Optional.of(testUserImage));

        // Act
        long result = userImageService.getUserImageSize(1);

        // Assert
        assertEquals(1024L, result);
        verify(userImageRepository).findByUserId(1);
    }

    @Test
    void getUserImageSize_NotFound() {
        // Arrange
        when(userImageRepository.findByUserId(999)).thenReturn(Optional.empty());

        // Act & Assert
        NoImagesFoundException exception = assertThrows(
                NoImagesFoundException.class,
                () -> userImageService.getUserImageSize(999)
        );
        assertEquals("Фото пользователя по id 999 не найдено.", exception.getMessage());
    }

    @Test
    void getUserImageMediaType_Success() {
        // Arrange
        when(userImageRepository.findByUserId(1)).thenReturn(Optional.of(testUserImage));

        // Act
        String result = userImageService.getUserImageMediaType(1);

        // Assert
        assertEquals("image/jpeg", result);
        verify(userImageRepository).findByUserId(1);
    }

    @Test
    void getUserImageMediaType_NotFound() {
        // Arrange
        when(userImageRepository.findByUserId(999)).thenReturn(Optional.empty());

        // Act & Assert
        NoImagesFoundException exception = assertThrows(
                NoImagesFoundException.class,
                () -> userImageService.getUserImageMediaType(999)
        );
        assertEquals("Фото пользователя по id 999 не найдено.", exception.getMessage());
    }

    @Test
    void uploadUserImage_FileWithoutExtension() {
        // Arrange
        MockMultipartFile fileWithoutExtension = new MockMultipartFile(
                "image",
                "testfile",
                "image/jpeg",
                "test content".getBytes()
        );

        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userImageService.uploadUserImage(1, fileWithoutExtension)
        );
        assertEquals("Файл должен иметь расширение", exception.getMessage());
    }
}
package ru.skypro.homework.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.NewPassword;
import ru.skypro.homework.dto.UpdateUser;
import ru.skypro.homework.dto.User;
import ru.skypro.homework.model.UserImage;
import ru.skypro.homework.service.UserImageService;
import ru.skypro.homework.service.UserService;

@RestController
@AllArgsConstructor
@CrossOrigin(value = "http://localhost:3000")
@RequestMapping("/users")
@Tag(name = "Пользователи")
public class UserController {

    private final UserService userService;
    private final UserImageService userImageService;

    @PostMapping("/set_password")
    @Operation(summary = "Обновление пароля")
    public ResponseEntity<?> setPassword(@RequestBody NewPassword newPassword,
                                         Authentication authentication) {
        String email = authentication.getName();
        try {
            if (userService.updatePassword(newPassword, email)) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/me")
    @Operation(summary = "Получение информации об авторизованном пользователе")
    public ResponseEntity<User> getUser(Authentication authentication) {
        String email = authentication.getName();
        try {
            User user = userService.getUser(email);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PatchMapping("/me")
    @Operation(summary = "Обновление информации об авторизованном пользователе")
    public ResponseEntity<UpdateUser> updateUser(@RequestBody UpdateUser updateUser,
                                 Authentication authentication) {
        String email = authentication.getName();
        try {
            UpdateUser updatedUser = userService.updateUser(updateUser, email);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PatchMapping("/me/image")
    @Operation(summary = "Обновление аватара авторизованного пользователя")
    public ResponseEntity<?> updateUserImage(@RequestParam MultipartFile image,
                                             Authentication authentication) {
        String email = authentication.getName();
        try {
            if (userService.updateUserImage(image, email)) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping(value = "/images/{id}", produces = {MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_GIF_VALUE, "image/*"})
    @Operation(summary = "Получение изображения пользователя", tags = {"Изображения"})
    public ResponseEntity<byte[]> getUserImage(@PathVariable("id") Integer id) {
        try {
            UserImage image = userImageService.getImage(id);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.valueOf(image.getMediaType()));
            headers.setContentLength(image.getFileSize());
            headers.setCacheControl("max-age=31536000"); // Кэширование на 1 год

            // Пытаемся получить полное изображение с диска
            byte[] imageBytes = userImageService.getImageBytes(id);
            
            return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}

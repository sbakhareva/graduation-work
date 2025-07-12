package ru.skypro.homework.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
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

import java.security.PrivateKey;

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
        System.out.println(email);
        if (userService.updatePassword(newPassword, email)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/me")
    @Operation(summary = "Получение информации об авторизованном пользователе")
    public User getUser(Authentication authentication) {
        String email = authentication.getName();
        return userService.getUser(email);
    }

    @PatchMapping("/me")
    @Operation(summary = "Обновление информации об авторизованном пользователе")
    public UpdateUser updateUser(@RequestBody UpdateUser updateUser,
                                 Authentication authentication) {
        String email = authentication.getName();
        return userService.updateUser(updateUser, email);
    }

    @PatchMapping("/me/image")
    @Operation(summary = "Обновление аватара авторизованного пользователя")
    public ResponseEntity<?> updateUserImage(@RequestParam MultipartFile image,
                                             Authentication authentication) {
        String email = authentication.getName();
        if (userService.updateUserImage(image, email)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping(value = "/images/{id}", produces = {MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_GIF_VALUE, "image/*"})
    public ResponseEntity<byte[]> getImage(@PathVariable("id") Integer id) {
        UserImage image = userImageService.getImage(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf(image.getMediaType()));
        headers.setContentLength(image.getFileSize());

        return new ResponseEntity<>(image.getPreview(), headers, HttpStatus.OK);
    }

}

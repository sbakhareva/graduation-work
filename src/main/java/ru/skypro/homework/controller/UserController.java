package ru.skypro.homework.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.NewPassword;
import ru.skypro.homework.dto.UpdateUser;
import ru.skypro.homework.dto.User;
import ru.skypro.homework.service.impl.UserImageService;
import ru.skypro.homework.service.impl.UserService;

@RestController
@AllArgsConstructor
@CrossOrigin(value = "http://localhost:3000")
@RequestMapping("/users")
@Tag(name = "Пользователи")
public class UserController {

    private final UserService userService;
    private final UserImageService userImageService;

    /**
     * Обновление пароля пользователя
     *
     * @param newPassword    объект {@link NewPassword}, содержащий текущий и новый пароли
     * @param authentication информация о пользователе из хидеров
     * @return {@link ResponseEntity}:
     * <ul>
     *   <li><b>200 OK</b> – если пароль успешно обновлён</li>
     *   <li><b>401 Unauthorized</b> – если текущий пароль не совпадает</li>
     * </ul>
     */
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

    /**
     * Получение информации об авторизованном пользователе
     *
     * @param authentication информация о пользователе из хидеров
     * @return объект {@link User}, содержащий основную информацию о пользователе
     */
    @GetMapping("/me")
    @Operation(summary = "Получение информации об авторизованном пользователе")
    public User getUser(Authentication authentication) {
        String email = authentication.getName();
        return userService.getUser(email);
    }

    /**
     * Обновление информации об авторизованном пользователе
     *
     * @param updateUser     объект {@link UpdateUser}, содержащий данные пользователя для обновления информации в базе данных
     * @param authentication информация о пользователе из хидеров
     * @return объект {@link UpdateUser}, содержащий обновленные данные пользователя
     */
    @PatchMapping("/me")
    @Operation(summary = "Обновление информации об авторизованном пользователе")
    public UpdateUser updateUser(@RequestBody UpdateUser updateUser,
                                 Authentication authentication) {
        String email = authentication.getName();
        return userService.updateUser(updateUser, email);
    }

    /**
     * Обновление аватара авторизованного пользователя
     *
     * @param image          новое фото пользователя
     * @param authentication информация о пользователе из хидеров
     * @return {@link ResponseEntity}:
     * <ul>
     *   <li><b>200 OK</b> – если фото успешно обновлено</li>
     *   <li><b>401 Unauthorized</b> – если пользователь не авторизован</li>
     * </ul>
     */
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

    /**
     * Получение аватара текущего пользователя
     *
     * @param id идентификатор фото пользователя
     * @return картинка в виде массива байт
     */
    @Operation(summary = "Получение фото профиля")
    @GetMapping(value = "/users-images/{id}", produces = {MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_GIF_VALUE, "image/*"})
    public ResponseEntity<byte[]> getImage(@PathVariable("id") Integer id) {
        return userImageService.getImage(id);
    }
}

package ru.skypro.homework.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.NewPassword;
import ru.skypro.homework.dto.UpdateUser;
import ru.skypro.homework.dto.User;
import ru.skypro.homework.exception.NoImagesFoundException;
import ru.skypro.homework.exception.NoUsersFoundByEmailException;
import ru.skypro.homework.service.impl.UserImageService;
import ru.skypro.homework.service.impl.UserService;

import java.io.IOException;

@RestController
@CrossOrigin(value = "http://localhost:3000")
@RequestMapping("/users")
@Tag(name = "Пользователи")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final UserImageService userImageService;

    public UserController(UserService userService,
                          UserImageService userImageService) {
        this.userService = userService;
        this.userImageService = userImageService;
    }

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
    @Operation(summary = "Обновление пароля", responses = {
            @ApiResponse(responseCode = "200", description = "Пароль успешно обновлён"),
            @ApiResponse(responseCode = "400", description = "Неверный формат пароля"),
            @ApiResponse(responseCode = "401", description = "Текущий пароль не совпадает"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён")
    })
    public ResponseEntity<Void> setPassword(@RequestBody NewPassword newPassword,
                                            Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            String email = authentication.getName();
            userService.updatePassword(newPassword, email);
            return ResponseEntity.ok().build();
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Получение информации об авторизованном пользователе
     *
     * @param authentication информация о пользователе из хидеров
     * @return объект {@link User}, содержащий основную информацию о пользователе
     */
    @GetMapping("/me")
    @Operation(summary = "Получение информации об авторизованном пользователе", responses = {
            @ApiResponse(responseCode = "200", description = "Информация получена"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    public ResponseEntity<User> getUser(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            String email = authentication.getName();
            User user = userService.getUser(email);
            return ResponseEntity.ok(user);
        } catch (NoUsersFoundByEmailException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Обновление информации об авторизованном пользователе
     *
     * @param updateUser     объект {@link UpdateUser}, содержащий данные пользователя для обновления информации в базе данных
     * @param authentication информация о пользователе из хидеров
     * @return объект {@link UpdateUser}, содержащий обновленные данные пользователя
     */
    @PatchMapping("/me")
    @Operation(summary = "Обновление информации об авторизованном пользователе", responses = {
            @ApiResponse(responseCode = "200", description = "Данные обновлены"),
            @ApiResponse(responseCode = "400", description = "Неверные данные для обновления"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    public ResponseEntity<UpdateUser> updateUser(@RequestBody UpdateUser updateUser,
                                                 Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            String email = authentication.getName();
            UpdateUser user = userService.updateUser(updateUser, email);
            return ResponseEntity.ok(user);
        } catch (NoUsersFoundByEmailException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
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
    @Operation(summary = "Обновление аватара авторизованного пользователя", responses = {
            @ApiResponse(responseCode = "200", description = "Аватар обновлён"),
            @ApiResponse(responseCode = "400", description = "Неверный формат изображения"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    public ResponseEntity<Void> updateUserImage(@RequestParam MultipartFile image,
                                                Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            String email = authentication.getName();
            userService.updateUserImage(image, email);
            return ResponseEntity.ok().build();
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IOException e) {
            logger.error("Ошибка при загрузке фото пользователя {}: {}", authentication.getName(), e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }

    }

    /**
     * Получение аватара текущего пользователя
     *
     * @param id идентификатор фото пользователя
     * @return картинка в виде массива байт
     */
    @GetMapping(value = "/users-images/{id}", produces = {MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_GIF_VALUE, "image/*"})
    @Operation(summary = "Получение фото профиля", responses = {
            @ApiResponse(responseCode = "200", description = "Изображение получено"),
            @ApiResponse(responseCode = "404", description = "Изображение не найдено")
    })
    public ResponseEntity<byte[]> getImage(@PathVariable("id") Integer id) {
        try {
            return userImageService.getImage(id);
        } catch (NoImagesFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

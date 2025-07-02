package ru.skypro.homework.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.NewPassword;
import ru.skypro.homework.dto.Role;
import ru.skypro.homework.dto.UpdateUser;
import ru.skypro.homework.dto.UserDTO;

@RestController
@RequestMapping("/users")
@Tag(name = "Пользователи")
public class UserController {

    @PostMapping("/set_password")
    @Operation(summary = "Обновление пароля")
    public void setPassword(@RequestBody NewPassword newPassword) {
        ResponseEntity.ok();
    }

    @GetMapping("/me")
    @Operation(summary = "Получение информации об авторизованном пользователе")
    public UserDTO getUser() {
        return new UserDTO(1, "userEmail@oiia.ru", "bob", "newby", "+77777777777", Role.USER, "image");
    }

    @PatchMapping("/me")
    @Operation(summary = "Обновление информации об авторизованном пользователе")
    public UpdateUser updateUser(@RequestBody UpdateUser updateUser) {
        return new UpdateUser("alexa", "play", "+7 964 341-33-43");
    }

    @PatchMapping("/me/image")
    @Operation(summary = "Обновление аватара авторизованного пользователя")
    public void updateUserImage(@RequestParam MultipartFile image) {
        ResponseEntity.ok();
    }
}

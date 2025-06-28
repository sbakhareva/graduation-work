package ru.skypro.homework.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.NewPassword;
import ru.skypro.homework.dto.Role;
import ru.skypro.homework.dto.UpdateUser;
import ru.skypro.homework.dto.User;

@RestController
@RequestMapping("/users")
public class UserController {

    @PostMapping("/set_password")
    public void setPassword(@RequestBody NewPassword newPassword) {
        ResponseEntity.ok();
    }

    @GetMapping("/me")
    public User getUser() {
        return new User(1, "userEmail@oiia.ru", "bob", "newby", "+77777777777", Role.USER, "image");
    }

    @PatchMapping("/me")
    public UpdateUser updateUser(@RequestBody UpdateUser updateUser) {
        return new UpdateUser("alexa", "play", "+7 964 341-33-43");
    }

    @PatchMapping("/me/image")
    public void updateUserImage(@RequestParam MultipartFile image) {
        ResponseEntity.ok();
    }
}

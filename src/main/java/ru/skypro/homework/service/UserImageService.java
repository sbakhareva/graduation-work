package ru.skypro.homework.service;

import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.model.UserImage;

import java.io.IOException;

public interface UserImageService {
    void uploadUserImage(Integer userId, MultipartFile image) throws IOException;
    UserImage getUserImage(Integer userId);
}

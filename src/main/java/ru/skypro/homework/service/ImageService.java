package ru.skypro.homework.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImageService {

    ResponseEntity<byte[]> getImage(Integer id);

    void uploadImage(Integer id, MultipartFile image) throws IOException;

    default String getExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }
}

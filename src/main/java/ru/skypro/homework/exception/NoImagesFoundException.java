package ru.skypro.homework.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NoImagesFoundException extends RuntimeException {

    public NoImagesFoundException(String message) {
        super(message);
    }
}

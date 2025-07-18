package ru.skypro.homework.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NoAdsFoundException extends RuntimeException {
    public NoAdsFoundException(Integer id) {
        super("Не найдено объявлений с id " + id);
    }
}


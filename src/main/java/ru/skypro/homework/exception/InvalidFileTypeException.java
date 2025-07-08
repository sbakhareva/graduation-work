package ru.skypro.homework.exception;

import jdk.jfr.ContentType;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidFileTypeException extends RuntimeException {

    public InvalidFileTypeException() {
        super("Неверный тип файла.");
    }
}

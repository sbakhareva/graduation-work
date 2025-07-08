package ru.skypro.homework.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class FileSizeExceededException extends RuntimeException {

    public FileSizeExceededException() {
        super("Слишком большой размер файла.");
    }
}

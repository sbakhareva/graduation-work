package ru.skypro.homework.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NoCommentsException extends RuntimeException {

    public NoCommentsException() {
        super("У этого объявления пока нет комментариев.");
    }
}

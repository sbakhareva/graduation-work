package ru.skypro.homework.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NoUsersFoundByIdException extends RuntimeException {
    public NoUsersFoundByIdException(Integer id) {
        super("Не найдено пользователей по id " + id);
    }
}

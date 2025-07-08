package ru.skypro.homework.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.skypro.homework.service.UserService;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NoUsersFoundByEmailException extends RuntimeException {

    private static final Logger logger = LoggerFactory.getLogger(NoUsersFoundByEmailException.class);

    public NoUsersFoundByEmailException(String email) {
        super("Не найдено пользователей с именем пользователя " + email);
    }
}

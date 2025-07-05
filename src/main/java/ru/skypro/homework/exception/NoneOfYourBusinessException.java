package ru.skypro.homework.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class NoneOfYourBusinessException extends RuntimeException {

    public NoneOfYourBusinessException(String message) {
        super(message);
    }
}

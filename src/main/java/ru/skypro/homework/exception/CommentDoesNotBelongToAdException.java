package ru.skypro.homework.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CommentDoesNotBelongToAdException extends RuntimeException {
    public CommentDoesNotBelongToAdException(Integer commentId,
                                             Integer adId) {
        super("Комментарий " + commentId + " не принадлежит объявлению " + adId);
    }
}

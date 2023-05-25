package ru.yandex.practicum.filmorate.Exceptions;

import org.springframework.http.HttpStatus;


public class ValidationException extends RuntimeException {

    HttpStatus httpStatus;

    public ValidationException(HttpStatus httpStatus, String message) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public ValidationException(String message) {
        super(message);
    }
}
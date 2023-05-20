package ru.yandex.practicum.filmorate.Exceptions;

public class ValidationException extends RuntimeException {

    String message;
    public ValidationException(String message) {
        super(message);
    }
}
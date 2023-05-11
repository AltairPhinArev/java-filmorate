package ru.yandex.practicum.filmorate.Exceptions;

public class ValidationException extends IllegalArgumentException {

    String massage;

    public ValidationException(String massage) {
        super(massage);
    }
}
package ru.yandex.practicum.filmorate.Exceptions;

public class UserOrFilmNotFoundException extends RuntimeException {

    String message;

    public UserOrFilmNotFoundException(String message) {
        super(message);
    }
}

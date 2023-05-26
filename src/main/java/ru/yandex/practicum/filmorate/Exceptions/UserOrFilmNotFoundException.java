package ru.yandex.practicum.filmorate.Exceptions;

public class UserOrFilmNotFoundException extends RuntimeException {

    public UserOrFilmNotFoundException(String message) {
        super(message);
    }
}

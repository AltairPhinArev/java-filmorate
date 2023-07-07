package ru.yandex.practicum.filmorate.Exceptions;

public class RemoveReviewException extends RuntimeException {
    public RemoveReviewException(String message) {
        super(message);
    }

    public RemoveReviewException(String message, Throwable cause) {
        super(message, cause);
    }
}
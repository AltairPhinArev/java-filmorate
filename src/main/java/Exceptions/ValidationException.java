package Exceptions;

import java.io.IOException;

public class ValidationException extends IOException {

    String message;

    public ValidationException(String message) {
        this.message = message;
    }
}

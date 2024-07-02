package it.units.sdm.dotsandboxes.exceptions;

import java.io.IOException;
import java.util.InputMismatchException;

public class InvalidInputException extends Exception {
    public InvalidInputException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidInputException(Throwable cause) {
        super(cause);
    }

    public InvalidInputException() {
    }

    public InvalidInputException(String message) {
        super(message);
    }
}

package it.units.sdm.dotsandboxes.exceptions;

/**
 * Exception that encompasses all sorts of player-related input problems.
 */
public class InvalidInputException extends Exception {

    public InvalidInputException(String message) {
        super(message);
    }
}

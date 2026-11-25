package fr.datashare.common;

// Am.Yo : 409, ex. email déjà pris
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}

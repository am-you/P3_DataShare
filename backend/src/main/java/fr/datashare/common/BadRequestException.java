package fr.datashare.common;

// Am.Yo : 400 métier
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}

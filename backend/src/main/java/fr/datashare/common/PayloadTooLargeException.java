package fr.datashare.common;

// Am.Yo : 413, fichier > 1 Go
public class PayloadTooLargeException extends RuntimeException {

    public PayloadTooLargeException(String message) {
        super(message);
    }
}

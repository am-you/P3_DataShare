package fr.datashare.common;

// Am.Yo : 401, identifiants faux
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }
}

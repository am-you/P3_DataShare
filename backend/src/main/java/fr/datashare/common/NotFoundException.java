package fr.datashare.common;

// Am.Yo : 404, lien ou ressource inconnue
public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }
}

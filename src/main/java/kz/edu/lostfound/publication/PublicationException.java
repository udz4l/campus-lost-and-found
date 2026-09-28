package kz.edu.lostfound.publication;

public class PublicationException extends RuntimeException {

    public enum Reason {
        INVALID_DATA,
        UNAVAILABLE,
        STORAGE_ERROR,
        UNKNOWN_ERROR
    }

    private final Reason reason;

    public PublicationException(Reason reason, String message) {
        super(message);
        this.reason = reason;
    }

    public Reason getReason() {
        return reason;
    }
}
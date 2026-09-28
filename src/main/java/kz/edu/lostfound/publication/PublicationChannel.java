package kz.edu.lostfound.publication;

public interface PublicationChannel {

    String id();

    /**
     * Publishes a notice with a non-blank title and body.
     *
     * @throws PublicationException if validation or publication fails
     */
    void publish(String title, String body);
}
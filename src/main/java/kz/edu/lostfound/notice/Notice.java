package kz.edu.lostfound.notice;
import kz.edu.lostfound.publication.PublicationChannel;
import java.util.Objects;

public abstract class Notice {

    private final PublicationChannel channel;
    private final String item;
    private final String location;
    private final String contact;

    protected Notice(PublicationChannel channel,
                     String item,
                     String location,
                     String contact) {
        this.channel = Objects.requireNonNull(channel, "Channel is required");
        this.item = requireText(item, "Item");
        this.location = requireText(location, "Location");
        this.contact = requireText(contact, "Contact");
    }

    public final void publish() {
        channel.publish(title(), body());
    }

    protected abstract String title();
    protected abstract String body();

    protected String getItem() {
        return item;
    }

    protected String getLocation() {
        return location;
    }

    protected String getContact() {
        return contact;
    }

    private static String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
        return value.strip();
    }
}

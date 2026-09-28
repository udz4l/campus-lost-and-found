package kz.edu.lostfound.notice;

import kz.edu.lostfound.publication.PublicationChannel;

public class FoundItemNotice extends Notice {

    public FoundItemNotice(PublicationChannel channel,
                           String item,
                           String location,
                           String contact) {
        super(channel, item, location, contact);
    }

    @Override
    protected String title() {
        return "FOUND: " + getItem();
    }

    @Override
    protected String body() {
        return "Item: " + getItem()
                + "\nFound at: " + getLocation()
                + "\nTo claim it, describe the item and contact: " + getContact();
    }
}
package kz.edu.lostfound.notice;

import kz.edu.lostfound.publication.PublicationChannel;

public class LostItemNotice extends Notice {

    public LostItemNotice(PublicationChannel channel,
                          String item,
                          String location,
                          String contact) {
        super(channel, item, location, contact);
    }

    @Override
    protected String title() {
        return "LOST: " + getItem();
    }

    @Override
    protected String body() {
        return "Item: " + getItem()
                + "\nLast seen at: " + getLocation()
                + "\nIf you find it, please contact: " + getContact();
    }
}
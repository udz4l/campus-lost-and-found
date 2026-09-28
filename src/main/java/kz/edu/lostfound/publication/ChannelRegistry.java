package kz.edu.lostfound.publication;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.TreeMap;

public class ChannelRegistry {

    private final Map<String, PublicationChannel> channels = new TreeMap<>();

    public ChannelRegistry() {
        for (PublicationChannel channel
                : ServiceLoader.load(PublicationChannel.class)) {

            String id = channel.id().strip().toLowerCase(Locale.ROOT);

            if (id.isBlank()) {
                throw new IllegalStateException(
                        "A publication channel has an empty ID"
                );
            }

            if (channels.putIfAbsent(id, channel) != null) {
                throw new IllegalStateException(
                        "Duplicate publication channel ID: " + id
                );
            }
        }

        if (channels.isEmpty()) {
            throw new IllegalStateException(
                    "No publication channels found. Check META-INF/services"
            );
        }
    }

    public List<String> availableIds() {
        return List.copyOf(channels.keySet());
    }

    public PublicationChannel select(String input) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException(
                    "Please enter a publication channel ID"
            );
        }

        String id = input.strip().toLowerCase(Locale.ROOT);
        PublicationChannel channel = channels.get(id);

        if (channel == null) {
            throw new IllegalArgumentException(
                    "Unknown channel. Available channels: " + availableIds()
            );
        }

        return channel;
    }
}
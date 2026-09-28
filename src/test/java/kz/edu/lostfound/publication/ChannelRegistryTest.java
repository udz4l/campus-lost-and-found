package kz.edu.lostfound.publication;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ChannelRegistryTest {

    @Test
    void discoversAllRegisteredChannels() {
        ChannelRegistry registry = new ChannelRegistry();

        assertEquals(
                List.of("html", "legacy", "text"),
                registry.availableIds()
        );
    }

    @Test
    void selectsChannelFromInput() {
        ChannelRegistry registry = new ChannelRegistry();

        assertAll(
                () -> assertInstanceOf(
                        TextFileChannel.class,
                        registry.select("text")
                ),
                () -> assertInstanceOf(
                        HtmlFileChannel.class,
                        registry.select("html")
                ),
                () -> assertInstanceOf(
                        LegacyBoardAdapter.class,
                        registry.select("legacy")
                )
        );
    }

    @Test
    void acceptsSpacesAndUppercase() {
        ChannelRegistry registry = new ChannelRegistry();

        assertEquals("text", registry.select("  TEXT  ").id());
    }

    @Test
    void rejectsUnknownChannel() {
        ChannelRegistry registry = new ChannelRegistry();

        assertThrows(
                IllegalArgumentException.class,
                () -> registry.select("unknown")
        );
    }
}
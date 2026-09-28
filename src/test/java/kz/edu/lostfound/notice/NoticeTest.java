package kz.edu.lostfound.notice;

import kz.edu.lostfound.publication.PublicationChannel;
import kz.edu.lostfound.publication.PublicationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NoticeTest {

    @Test
    void lostNoticeDelegatesToChannel() {
        RecordingChannel channel = new RecordingChannel();

        Notice notice = new LostItemNotice(
                channel,
                "Black backpack",
                "Room C1.2.10",
                "@student_one"
        );

        notice.publish();

        assertAll(
                () -> assertEquals(1, channel.calls),
                () -> assertEquals(
                        "LOST: Black backpack",
                        channel.title
                ),
                () -> assertEquals(
                        "Item: Black backpack\n"
                                + "Last seen at: Room C1.2.10\n"
                                + "If you find it, please contact: @student_one",
                        channel.body
                )
        );
    }

    @Test
    void foundNoticeDelegatesToChannel() {
        RecordingChannel channel = new RecordingChannel();

        Notice notice = new FoundItemNotice(
                channel,
                "Keys",
                "Library",
                "@student_two"
        );

        notice.publish();

        assertAll(
                () -> assertEquals(1, channel.calls),
                () -> assertEquals(
                        "FOUND: Keys",
                        channel.title
                ),
                () -> assertEquals(
                        "Item: Keys\n"
                                + "Found at: Library\n"
                                + "To claim it, describe the item and contact: "
                                + "@student_two",
                        channel.body
                )
        );
    }

    @Test
    void publicationFailureReachesCaller() {
        PublicationException failure = new PublicationException(
                PublicationException.Reason.UNAVAILABLE,
                "Board is unavailable"
        );

        PublicationChannel failingChannel = new PublicationChannel() {
            @Override
            public String id() {
                return "failing";
            }

            @Override
            public void publish(String title, String body) {
                throw failure;
            }
        };

        Notice notice = new LostItemNotice(
                failingChannel,
                "Backpack",
                "Library",
                "@student"
        );

        PublicationException actual = assertThrows(
                PublicationException.class,
                notice::publish
        );

        assertSame(failure, actual);
    }

    private static class RecordingChannel implements PublicationChannel {

        private int calls;
        private String title;
        private String body;

        @Override
        public String id() {
            return "recording";
        }

        @Override
        public void publish(String title, String body) {
            calls++;
            this.title = title;
            this.body = body;
        }
    }
}
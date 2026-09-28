package kz.edu.lostfound.publication;

import kz.edu.lostfound.legacy.LegacyCampusBoard;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class LegacyBoardAdapterTest {

    @Test
    void convertsBodyToUtf8AndPassesArgumentsCorrectly() {
        StubLegacyBoard board = new StubLegacyBoard(
                LegacyCampusBoard.SUCCESS
        );

        LegacyBoardAdapter adapter = new LegacyBoardAdapter(board);

        String title = "FOUND: Ключи";
        String body = "Найдены в библиотеке";

        adapter.publish(title, body);

        assertAll(
                () -> assertEquals(1, board.calls),
                () -> assertEquals(title, board.receivedHeading),
                () -> assertArrayEquals(
                        body.getBytes(StandardCharsets.UTF_8),
                        board.receivedBytes
                )
        );
    }

    @ParameterizedTest
    @CsvSource({
            "10, INVALID_DATA",
            "20, UNAVAILABLE",
            "30, STORAGE_ERROR",
            "999, UNKNOWN_ERROR"
    })
    void translatesFailureCodes(
            int legacyCode,
            PublicationException.Reason expectedReason
    ) {
        StubLegacyBoard board = new StubLegacyBoard(legacyCode);
        LegacyBoardAdapter adapter = new LegacyBoardAdapter(board);

        PublicationException exception = assertThrows(
                PublicationException.class,
                () -> adapter.publish("FOUND: Keys", "Found in library")
        );

        assertAll(
                () -> assertEquals(expectedReason, exception.getReason()),
                () -> assertFalse(exception.getMessage().isBlank()),
                () -> assertEquals(1, board.calls)
        );
    }

    @Test
    void rejectsBlankTitleWithoutCallingLegacyBoard() {
        StubLegacyBoard board = new StubLegacyBoard(
                LegacyCampusBoard.SUCCESS
        );

        LegacyBoardAdapter adapter = new LegacyBoardAdapter(board);

        PublicationException exception = assertThrows(
                PublicationException.class,
                () -> adapter.publish("   ", "Found in library")
        );

        assertAll(
                () -> assertEquals(
                        PublicationException.Reason.INVALID_DATA,
                        exception.getReason()
                ),
                () -> assertEquals(0, board.calls)
        );
    }

    @Test
    void rejectsNullBodyWithoutCallingLegacyBoard() {
        StubLegacyBoard board = new StubLegacyBoard(
                LegacyCampusBoard.SUCCESS
        );

        LegacyBoardAdapter adapter = new LegacyBoardAdapter(board);

        PublicationException exception = assertThrows(
                PublicationException.class,
                () -> adapter.publish("FOUND: Keys", null)
        );

        assertAll(
                () -> assertEquals(
                        PublicationException.Reason.INVALID_DATA,
                        exception.getReason()
                ),
                () -> assertEquals(0, board.calls)
        );
    }

    private static class StubLegacyBoard extends LegacyCampusBoard {

        private final int result;
        private int calls;
        private byte[] receivedBytes;
        private String receivedHeading;

        private StubLegacyBoard(int result) {
            this.result = result;
        }

        @Override
        public int addRecord(byte[] messageBytes, String heading) {
            calls++;
            receivedBytes = messageBytes;
            receivedHeading = heading;
            return result;
        }
    }
}
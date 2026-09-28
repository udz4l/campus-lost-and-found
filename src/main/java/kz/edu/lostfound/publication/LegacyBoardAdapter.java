package kz.edu.lostfound.publication;

import kz.edu.lostfound.legacy.LegacyCampusBoard;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class LegacyBoardAdapter implements PublicationChannel {

    private final LegacyCampusBoard legacyBoard;

    public LegacyBoardAdapter() {
        this(new LegacyCampusBoard());
    }

    public LegacyBoardAdapter(LegacyCampusBoard legacyBoard) {
        this.legacyBoard = Objects.requireNonNull(legacyBoard);
    }

    @Override
    public String id() {
        return "legacy";
    }

    @Override
    public void publish(String title, String body) {
        if (title == null || title.isBlank()
                || body == null || body.isBlank()) {
            throw new PublicationException(
                    PublicationException.Reason.INVALID_DATA,
                    "Title and body must not be blank"
            );
        }

        byte[] messageBytes = body.getBytes(StandardCharsets.UTF_8);

        int result = legacyBoard.addRecord(messageBytes, title);

        switch (result) {
            case LegacyCampusBoard.SUCCESS -> {
                // Publication completed successfully.
            }

            case LegacyCampusBoard.INVALID_RECORD ->
                    throw new PublicationException(
                            PublicationException.Reason.INVALID_DATA,
                            "The board rejected the notice data"
                    );

            case LegacyCampusBoard.BOARD_OFFLINE ->
                    throw new PublicationException(
                            PublicationException.Reason.UNAVAILABLE,
                            "The publication board is unavailable"
                    );

            case LegacyCampusBoard.WRITE_FAILED ->
                    throw new PublicationException(
                            PublicationException.Reason.STORAGE_ERROR,
                            "The publication board could not save the notice"
                    );

            default ->
                    throw new PublicationException(
                            PublicationException.Reason.UNKNOWN_ERROR,
                            "The publication board returned an unexpected result"
                    );
        }
    }
}
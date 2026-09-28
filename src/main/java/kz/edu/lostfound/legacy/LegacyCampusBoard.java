package kz.edu.lostfound.legacy;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Objects;
import java.util.UUID;

public class LegacyCampusBoard {

    public static final int SUCCESS = 0;
    public static final int INVALID_RECORD = 10;
    public static final int BOARD_OFFLINE = 20;
    public static final int WRITE_FAILED = 30;

    private final Path directory;
    private final boolean online;

    public LegacyCampusBoard() {
        this(Path.of("output", "legacy"), true);
    }

    public LegacyCampusBoard(Path directory, boolean online) {
        this.directory = Objects.requireNonNull(directory);
        this.online = online;
    }

    public int addRecord(byte[] messageBytes, String heading) {
        if (!online) {
            return BOARD_OFFLINE;
        }

        if (messageBytes == null
                || heading == null
                || heading.isBlank()) {
            return INVALID_RECORD;
        }

        String message = new String(
                messageBytes,
                StandardCharsets.UTF_8
        );

        if (message.isBlank()) {
            return INVALID_RECORD;
        }

        String record = "CAMPUS BOARD RECORD\n"
                + "HEADING: " + heading + "\n"
                + "MESSAGE:\n" + message + "\n";

        try {
            Files.createDirectories(directory);

            Path file = directory.resolve(
                    "record-" + UUID.randomUUID() + ".txt"
            );

            Files.writeString(
                    file,
                    record,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE_NEW
            );

            return SUCCESS;
        } catch (IOException | SecurityException e) {
            return WRITE_FAILED;
        }
    }
}
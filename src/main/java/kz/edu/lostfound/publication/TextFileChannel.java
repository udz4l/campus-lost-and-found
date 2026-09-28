package kz.edu.lostfound.publication;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.UUID;

public class TextFileChannel implements PublicationChannel {

    private final Path directory;

    public TextFileChannel() {
        this(Path.of("output", "text"));
    }

    public TextFileChannel(Path directory) {
        this.directory = java.util.Objects.requireNonNull(directory);
    }

    @Override
    public String id() {
        return "text";
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

        String content = title + "\n\n" + body + "\n";

        try {
            Files.createDirectories(directory);

            Path file = directory.resolve(UUID.randomUUID() + ".txt");

            Files.writeString(
                    file,
                    content,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE_NEW
            );
        } catch (IOException | SecurityException e) {
            throw new PublicationException(
                    PublicationException.Reason.STORAGE_ERROR,
                    "Could not save the notice to the text board"
            );
        }
    }
}
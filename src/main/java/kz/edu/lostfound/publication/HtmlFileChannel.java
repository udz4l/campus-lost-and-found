package kz.edu.lostfound.publication;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Objects;
import java.util.UUID;

public class HtmlFileChannel implements PublicationChannel {

    private final Path directory;

    public HtmlFileChannel() {
        this(Path.of("output", "html"));
    }

    public HtmlFileChannel(Path directory) {
        this.directory = Objects.requireNonNull(directory);
    }

    @Override
    public String id() {
        return "html";
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

        String safeTitle = escapeHtml(title);
        String safeBody = escapeHtml(body);

        String content = """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <title>%s</title>
                    <style>
                        body {
                            font-family: sans-serif;
                            max-width: 720px;
                            margin: 40px auto;
                            padding: 20px;
                        }
                        pre {
                            font-family: inherit;
                            white-space: pre-wrap;
                            overflow-wrap: anywhere;
                        }
                    </style>
                </head>
                <body>
                    <h1>%s</h1>
                    <pre>%s</pre>
                </body>
                </html>
                """.formatted(safeTitle, safeTitle, safeBody);

        try {
            Files.createDirectories(directory);

            Path file = directory.resolve(UUID.randomUUID() + ".html");

            Files.writeString(
                    file,
                    content,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE_NEW
            );
        } catch (IOException | SecurityException e) {
            throw new PublicationException(
                    PublicationException.Reason.STORAGE_ERROR,
                    "Could not save the notice to the HTML board"
            );
        }
    }

    private static String escapeHtml(String text) {
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
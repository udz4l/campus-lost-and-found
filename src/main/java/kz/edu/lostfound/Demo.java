package kz.edu.lostfound;

import kz.edu.lostfound.notice.FoundItemNotice;
import kz.edu.lostfound.notice.LostItemNotice;
import kz.edu.lostfound.notice.Notice;
import kz.edu.lostfound.publication.ChannelRegistry;
import kz.edu.lostfound.publication.PublicationChannel;
import kz.edu.lostfound.publication.PublicationException;

import java.util.Locale;
import java.util.Scanner;

public class Demo {

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            ChannelRegistry registry = new ChannelRegistry();

            System.out.println("=== Campus Lost & Found ===");

            String type = read(scanner, "Notice type (lost/found): ")
                    .strip()
                    .toLowerCase(Locale.ROOT);

            if (!type.equals("lost") && !type.equals("found")) {
                throw new IllegalArgumentException(
                        "Notice type must be lost or found"
                );
            }

            String item = read(scanner, "Item: ");
            String location = read(scanner, "Location: ");
            String contact = read(scanner, "Contact: ");

            System.out.println(
                    "Available channels: " + registry.availableIds()
            );

            String channelId = read(scanner, "Choose channel: ");
            PublicationChannel channel = registry.select(channelId);

            Notice notice = switch (type) {
                case "lost" ->
                        new LostItemNotice(channel, item, location, contact);
                case "found" ->
                        new FoundItemNotice(channel, item, location, contact);
                default ->
                        throw new IllegalArgumentException(
                                "Unsupported notice type"
                        );
            };

            notice.publish();

            System.out.println(
                    "Notice published successfully via: " + channel.id()
            );

        } catch (PublicationException e) {
            System.out.println(
                    "Publication failed [" + e.getReason() + "]: "
                            + e.getMessage()
            );
        } catch (IllegalArgumentException e) {
            System.out.println("Input error: " + e.getMessage());
        } catch (IllegalStateException e) {
            System.out.println("Configuration error: " + e.getMessage());
        }
    }

    private static String read(Scanner scanner, String prompt) {
        System.out.print(prompt);

        if (!scanner.hasNextLine()) {
            throw new IllegalArgumentException("Input ended unexpectedly");
        }

        return scanner.nextLine();
    }
}
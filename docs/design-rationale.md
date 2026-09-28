# Campus Lost and Found — Design Rationale

## What the application does

I chose a university lost-and-found system. A student can create a notice
about something they lost or found, enter a location and contact details,
and choose where to publish it.

There are three publication options: a text file, an HTML page, and
a simulated old campus board. Everything runs locally.

## How I used Bridge

The application separates the content of a notice from the way it is
published.

Notice is the base abstraction. LostItemNotice and FoundItemNotice
extend it and provide different titles and messages. For example,
a lost-item notice describes where the item was last seen, while
a found-item notice asks the owner to describe the item before claiming it.

PublicationChannel is the implementation interface. TextFileChannel,
HtmlFileChannel, and LegacyBoardAdapter implement it.

Notice stores a PublicationChannel reference. Its publish() method
passes the title and body to that channel. This lets either notice type
work with any of the three channels without separate classes for every
combination.

## Why the old board needs an Adapter

LegacyCampusBoard does not implement PublicationChannel. Its method is
addRecord(byte[] messageBytes, String heading), which differs from
publish(String title, String body).

The differences are more than the method name. The arguments are in
a different order, the message must be passed as UTF-8 bytes, and the
method returns a number to indicate success or failure.

LegacyBoardAdapter handles these differences without changing the old
board's source. It converts the message to bytes, passes the arguments
in the correct order, and translates the result:

- 0 means success.
- 10 becomes INVALID_DATA.
- 20 becomes UNAVAILABLE.
- 30 becomes STORAGE_ERROR.
- Any unexpected code becomes UNKNOWN_ERROR.

Failures are reported through PublicationException. The adapter also
rejects null or blank input before calling the old board. Notice classes
never need to know the old board's codes or data format.

## Why I need both patterns

Bridge lets me change notice types and publication channels independently.
However, it does not automatically make the old board compatible with
PublicationChannel. That is the adapter's job.

Adapter solves the compatibility problem, but it does not create the
separation between notice types and publication channels. The two
patterns solve different problems within the same publication flow.

## Runtime channel selection

I chose Dynamic implementor selection as the required complexity module.

ChannelRegistry loads the available channels through Java ServiceLoader.
The implementations are listed in the META-INF/services configuration file.

The user enters text, html, or legacy, and the registry returns the
matching channel. The console client does not choose a fixed
implementation in advance.

## Adding new functionality

To add another notice type, I can extend Notice and provide its title
and body. Existing notice and channel classes stay unchanged. I would
still need to add the new option to the console menu.

To add another channel, I can implement PublicationChannel and register
the class in the service configuration. Existing Java classes do not
need to change.

## Tests and a limitation

The JUnit 5 tests check that both notice types send the correct data
to a test channel and do not hide publication errors. A legacy stub
checks UTF-8 conversion, argument order, and error-code translation,
including an unknown code. Other tests check channel discovery
and selection.

The main limitation is that the application only saves notices locally.
A successful publication means that a file was written, not that the
notice reached students or was read.
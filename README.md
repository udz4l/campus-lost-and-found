# Campus Lost and Found

A Java console application for publishing lost-and-found notices
at a university.

## Requirements

- JDK 21 or newer.
- Internet access for the first build to download Maven and dependencies.

Maven Wrapper is included, so a separate Maven installation is not required.

## Build and test

Run commands from the project root, where pom.xml is located.

### Windows PowerShell

```powershell
.\mvnw.cmd clean package
```

### macOS / Linux

```bash
sh mvnw clean package
```

This compiles the application, runs the tests, and creates the JAR.

## Run

```bash
java -jar target/campus-lost-and-found-1.0-SNAPSHOT.jar
```

Enter the notice type, item, location, contact details, and publication channel.

Example input:

```text
Notice type (lost/found): found
Item: Blue water bottle
Location: Library
Contact: @student
Available channels: [html, legacy, text]
Choose channel: legacy
```

Expected result:

```text
Notice published successfully via: legacy
```

## Publication channels

| Channel | Output directory | Result |
|---------|------------------|--------|
| text | output/text | A text file |
| html | output/html | An HTML page |
| legacy | output/legacy | A record created by the old campus board |

Output paths are relative to the working directory.
Each publication creates a separate file. HTML files can be opened
in a browser. The application handles one notice per run.

The legacy board is a local simulation, not a real university service.

## Design patterns

### Bridge

Notice is the Abstraction. LostItemNotice and FoundItemNotice provide
the two notice variants.

PublicationChannel is the Implementor interface, with three implementations:
TextFileChannel, HtmlFileChannel, and LegacyBoardAdapter.

Notice types and publication channels can vary independently.

### Adapter

LegacyBoardAdapter wraps LegacyCampusBoard without changing its source.
It converts the body to UTF-8 bytes, changes the argument order, and
translates integer status codes into PublicationException failures.

### Dynamic implementor selection

ChannelRegistry discovers channels through ServiceLoader.
The user selects the implementation at runtime, including the adapter.

The service registration file is:

```text
src/main/resources/META-INF/services/kz.edu.lostfound.publication.PublicationChannel
```

## Tests

Run only the tests on Windows:

```powershell
.\mvnw.cmd test
```

On macOS / Linux:

```bash
sh mvnw test
```

There are 14 test cases covering:

- Correct delegation by both notice types.
- Propagation of publication failures.
- UTF-8 conversion and argument order in the adapter.
- Translation of known and unknown legacy error codes.
- Rejection of invalid adapter input.
- Channel discovery and selection.

Tests use handwritten doubles where isolation is needed.
They do not create notice files.

## Documentation

- [Design rationale](docs/design-rationale.md)
- [UML class diagram](docs/main-uml.png)

## Limitations

Notices are saved locally. The application does not send them to students
or connect to a real campus publication service.
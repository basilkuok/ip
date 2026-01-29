import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads tasks from disk when Jarvis starts, and saves tasks to disk whenever the task list changes.
 */
public class Storage {
    private static final String FIELD_SEPARATOR = "\t";
    private static final String TODO_TYPE = "TODO";
    private static final String DEADLINE_TYPE = "DEADLINE";
    private static final String EVENT_TYPE = "EVENT";

    private final Path dataFilePathFromProjectRoot;

    /**
     * Creates a Storage that reads/writes tasks to the given file path (relative to the project root).
     */
    public Storage(Path dataFilePathFromProjectRoot) {
        this.dataFilePathFromProjectRoot = dataFilePathFromProjectRoot;
    }

    /**
     * Loads all tasks from disk.
     *
     * @return List of tasks loaded from disk.
     * @throws JarvisException If the data file exists but cannot be read/parsed.
     */
    public ArrayList<Task> loadTasks() throws JarvisException {
        Path dataFilePath = resolveDataFilePath();
        if (!Files.exists(dataFilePath)) {
            return new ArrayList<>();
        }

        ArrayList<Task> tasks = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(dataFilePath, StandardCharsets.UTF_8)) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.isBlank()) {
                    continue;
                }
                tasks.add(parseTaskLine(line, lineNumber));
            }
            return tasks;
        } catch (JarvisException exception) {
            backupCorruptedDataFileIfPossible(dataFilePath);
            throw exception;
        } catch (IOException exception) {
            throw new JarvisException("Unable to read data file: " + exception.getMessage());
        }
    }

    /**
     * Saves all tasks to disk.
     *
     * @param tasks Tasks to be saved.
     * @throws JarvisException If tasks cannot be saved to disk.
     */
    public void saveTasks(List<Task> tasks) throws JarvisException {
        Path dataFilePath = resolveDataFilePath();
        Path dataDirectory = dataFilePath.getParent();
        try {
            if (dataDirectory != null) {
                Files.createDirectories(dataDirectory);
            }

            Path tempFilePath = dataFilePath.resolveSibling(dataFilePath.getFileName() + ".tmp");
            try (BufferedWriter writer = Files.newBufferedWriter(
                    tempFilePath,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.WRITE,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING)) {
                for (Task task : tasks) {
                    writer.write(serializeTask(task));
                    writer.newLine();
                }
            }

            try {
                Files.move(tempFilePath, dataFilePath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException exception) {
                Files.move(tempFilePath, dataFilePath, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException exception) {
            throw new JarvisException("Unable to save data file: " + exception.getMessage());
        }
    }

    private Path resolveDataFilePath() {
        Path currentDirectory = Path.of("");
        if (Files.isDirectory(currentDirectory.resolve("src").resolve("main").resolve("java"))) {
            return currentDirectory.resolve(dataFilePathFromProjectRoot);
        }

        Path parentDirectory = currentDirectory.resolve("..");
        if (Files.isDirectory(parentDirectory.resolve("src").resolve("main").resolve("java"))) {
            return parentDirectory.resolve(dataFilePathFromProjectRoot);
        }

        return currentDirectory.resolve(dataFilePathFromProjectRoot);
    }

    private static Task parseTaskLine(String line, int lineNumber) throws JarvisException {
        String[] fields = line.split(FIELD_SEPARATOR, -1);
        if (fields.length < 3) {
            throw new JarvisException("Data file corrupted at line " + lineNumber + ": not enough fields.");
        }

        String type = fields[0].trim();
        boolean isDone = parseDoneMarker(fields[1].trim(), lineNumber);
        String description = unescape(fields[2]);

        Task task;
        switch (type) {
        case TODO_TYPE:
            if (fields.length != 3) {
                throw new JarvisException("Data file corrupted at line " + lineNumber + ": invalid TODO format.");
            }
            task = new Todo(description);
            break;
        case DEADLINE_TYPE:
            if (fields.length != 4) {
                throw new JarvisException("Data file corrupted at line " + lineNumber + ": invalid DEADLINE format.");
            }
            DateTimeParser.ParsedDateTime parsedBy = DateTimeParser.parseStoredDateTime(unescape(fields[3]), lineNumber);
            task = new Deadline(description, parsedBy.getValue(), parsedBy.hasTime());
            break;
        case EVENT_TYPE:
            if (fields.length != 5) {
                throw new JarvisException("Data file corrupted at line " + lineNumber + ": invalid EVENT format.");
            }
            DateTimeParser.ParsedDateTime parsedFrom = DateTimeParser.parseStoredDateTime(unescape(fields[3]), lineNumber);
            DateTimeParser.ParsedDateTime parsedTo = DateTimeParser.parseStoredDateTime(unescape(fields[4]), lineNumber);
            task = new Event(description,
                    parsedFrom.getValue(), parsedFrom.hasTime(),
                    parsedTo.getValue(), parsedTo.hasTime());
            break;
        default:
            throw new JarvisException("Data file corrupted at line " + lineNumber + ": unknown task type.");
        }

        if (isDone) {
            task.markAsDone();
        }
        return task;
    }

    private static boolean parseDoneMarker(String marker, int lineNumber) throws JarvisException {
        switch (marker) {
        case "0":
            return false;
        case "1":
            return true;
        default:
            throw new JarvisException("Data file corrupted at line " + lineNumber + ": invalid done marker.");
        }
    }

    private static String serializeTask(Task task) throws JarvisException {
        String doneMarker = task.isDone() ? "1" : "0";
        String description = escape(task.getDescription());

        if (task instanceof Todo) {
            return TODO_TYPE + FIELD_SEPARATOR + doneMarker + FIELD_SEPARATOR + description;
        }

        if (task instanceof Deadline deadline) {
            String by = DateTimeParser.formatForStorage(deadline.getBy(), deadline.hasTime());
            return DEADLINE_TYPE + FIELD_SEPARATOR + doneMarker + FIELD_SEPARATOR + description
                    + FIELD_SEPARATOR + escape(by);
        }

        if (task instanceof Event event) {
            String from = DateTimeParser.formatForStorage(event.getFrom(), event.hasTimeFrom());
            String to = DateTimeParser.formatForStorage(event.getTo(), event.hasTimeTo());
            return EVENT_TYPE + FIELD_SEPARATOR + doneMarker + FIELD_SEPARATOR + description
                    + FIELD_SEPARATOR + escape(from)
                    + FIELD_SEPARATOR + escape(to);
        }

        throw new JarvisException("Unable to save unknown task type: " + task.getClass().getSimpleName());
    }

    private static String escape(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("\t", "\\t")
                .replace("\n", "\\n");
    }

    private static String unescape(String value) {
        StringBuilder builder = new StringBuilder();
        int index = 0;
        while (index < value.length()) {
            char character = value.charAt(index);
            if (character != '\\') {
                builder.append(character);
                index++;
                continue;
            }

            if (index + 1 >= value.length()) {
                builder.append('\\');
                break;
            }

            char escapedCharacter = value.charAt(index + 1);
            switch (escapedCharacter) {
            case 't':
                builder.append('\t');
                break;
            case 'n':
                builder.append('\n');
                break;
            case '\\':
                builder.append('\\');
                break;
            default:
                builder.append('\\').append(escapedCharacter);
                break;
            }
            index += 2;
        }
        return builder.toString();
    }

    private static void backupCorruptedDataFileIfPossible(Path dataFilePath) {
        if (!Files.exists(dataFilePath)) {
            return;
        }

        try {
            Path backupPath = nextAvailableBackupPath(dataFilePath);
            Files.move(dataFilePath, backupPath);
        } catch (IOException ignored) {
            // Best-effort backup only.
        }
    }

    private static Path nextAvailableBackupPath(Path dataFilePath) {
        Path firstChoice = dataFilePath.resolveSibling(dataFilePath.getFileName() + ".corrupted");
        if (!Files.exists(firstChoice)) {
            return firstChoice;
        }

        int suffixNumber = 1;
        while (true) {
            Path candidate = dataFilePath.resolveSibling(dataFilePath.getFileName() + ".corrupted." + suffixNumber);
            if (!Files.exists(candidate)) {
                return candidate;
            }
            suffixNumber++;
        }
    }
}

package jarvis;

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
import java.util.Objects;

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
        assert dataFilePathFromProjectRoot != null : "dataFilePathFromProjectRoot should not be null";
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
        } 
        catch (JarvisException exception) {
            backupCorruptedDataFileIfPossible(dataFilePath);
            throw exception;
        } 
        catch (IOException exception) {
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
        assert tasks != null : "tasks should not be null";
        assert tasks.stream().allMatch(Objects::nonNull) : "tasks should not contain null entries";
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
                Files.move(
                        tempFilePath,
                        dataFilePath,
                        StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE);
            } 
            catch (AtomicMoveNotSupportedException exception) {
                Files.move(tempFilePath, dataFilePath, StandardCopyOption.REPLACE_EXISTING);
            }
        } 
        catch (IOException exception) {
            throw new JarvisException("Unable to save data file: " + exception.getMessage());
        }
    }

    /**
     * Resolves the data file path, accounting for different working directories
     * (e.g., running from project root vs. running from {@code text-ui-test/}).
     *
     * @return Data file path resolved against the current working directory.
     */
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

    /**
     * Parses a single stored task line into a {@link Task}.
     *
     * @param line Stored line from the data file.
     * @param lineNumber Line number in the data file (1-based).
     * @return Parsed task.
     * @throws JarvisException If the line format is invalid.
     */
    private static Task parseTaskLine(String line, int lineNumber) throws JarvisException {
        assert lineNumber > 0 : "lineNumber should be 1-based and positive";
        String[] fields = line.split(FIELD_SEPARATOR, -1);
        if (fields.length < 3) {
            throw new JarvisException("Data file corrupted at line " + lineNumber + ": not enough fields.");
        }

        String type = fields[0].trim();
        boolean isDone = parseDoneMarker(fields[1].trim(), lineNumber);
        String description = unescape(fields[2]);

        Task task = parseTaskByType(type, description, fields, lineNumber);

        if (isDone) {
            task.markAsDone();
        }
        return task;
    }

    private static Task parseTaskByType(
            String type,
            String description,
            String[] fields,
            int lineNumber) throws JarvisException {
        switch (type) {
        case TODO_TYPE:
            return parseTodo(description, fields, lineNumber);
        case DEADLINE_TYPE:
            return parseDeadline(description, fields, lineNumber);
        case EVENT_TYPE:
            return parseEvent(description, fields, lineNumber);
        default:
            throw new JarvisException("Data file corrupted at line " + lineNumber + ": unknown task type.");
        }
    }

    private static Task parseTodo(String description, String[] fields, int lineNumber) throws JarvisException {
        if (fields.length != 3 && fields.length != 4) {
            throw new JarvisException("Data file corrupted at line " + lineNumber + ": invalid TODO format.");
        }

        Task task = new Todo(description);
        applyPriorityIfPresent(task, fields, 3, lineNumber);
        return task;
    }

    private static Task parseDeadline(String description, String[] fields, int lineNumber) throws JarvisException {
        if (fields.length != 4 && fields.length != 5) {
            throw new JarvisException("Data file corrupted at line " + lineNumber + ": invalid DEADLINE format.");
        }

        DateTimeParser.ParsedDateTime parsedBy =
                DateTimeParser.parseStoredDateTime(unescape(fields[3]), lineNumber);
        Task task = new Deadline(description, parsedBy.getValue(), parsedBy.hasTime());
        applyPriorityIfPresent(task, fields, 4, lineNumber);
        return task;
    }

    private static Task parseEvent(String description, String[] fields, int lineNumber) throws JarvisException {
        if (fields.length != 5 && fields.length != 6) {
            throw new JarvisException("Data file corrupted at line " + lineNumber + ": invalid EVENT format.");
        }

        DateTimeParser.ParsedDateTime parsedFrom =
                DateTimeParser.parseStoredDateTime(unescape(fields[3]), lineNumber);
        DateTimeParser.ParsedDateTime parsedTo =
                DateTimeParser.parseStoredDateTime(unescape(fields[4]), lineNumber);

        Task task = new Event(description,
                parsedFrom.getValue(), parsedFrom.hasTime(),
                parsedTo.getValue(), parsedTo.hasTime());
        applyPriorityIfPresent(task, fields, 5, lineNumber);
        return task;
    }

    private static void applyPriorityIfPresent(
            Task task,
            String[] fields,
            int priorityFieldIndex,
            int lineNumber) throws JarvisException {
        if (fields.length == priorityFieldIndex + 1) {
            task.setPriority(Priority.parseStoredValue(fields[priorityFieldIndex], lineNumber));
        }
    }

    /**
     * Parses the stored done marker.
     *
     * @param marker Stored marker value.
     * @param lineNumber Line number in the data file (1-based).
     * @return {@code true} if done, {@code false} otherwise.
     * @throws JarvisException If the marker is invalid.
     */
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

    /**
     * Serializes the given task to a single line for saving.
     *
     * @param task Task to serialize.
     * @return Serialized task line.
     * @throws JarvisException If the task type is unsupported.
     */
    private static String serializeTask(Task task) throws JarvisException {
        String doneMarker = task.isDone() ? "1" : "0";
        String description = escape(task.getDescription());
        String priority = task.getPriority().getStorageValue();

        if (task instanceof Todo) {
            return TODO_TYPE + FIELD_SEPARATOR + doneMarker + FIELD_SEPARATOR + description
                    + FIELD_SEPARATOR + priority;
        }

        if (task instanceof Deadline deadline) {
            String by = DateTimeParser.formatForStorage(deadline.getBy(), deadline.hasTime());
            return DEADLINE_TYPE + FIELD_SEPARATOR + doneMarker + FIELD_SEPARATOR + description
                    + FIELD_SEPARATOR + escape(by)
                    + FIELD_SEPARATOR + priority;
        }

        if (task instanceof Event event) {
            String from = DateTimeParser.formatForStorage(event.getFrom(), event.hasTimeFrom());
            String to = DateTimeParser.formatForStorage(event.getTo(), event.hasTimeTo());
            return EVENT_TYPE + FIELD_SEPARATOR + doneMarker + FIELD_SEPARATOR + description
                    + FIELD_SEPARATOR + escape(from)
                    + FIELD_SEPARATOR + escape(to)
                    + FIELD_SEPARATOR + priority;
        }

        throw new JarvisException("Unable to save unknown task type: " + task.getClass().getSimpleName());
    }

    /**
     * Escapes a value so it can be stored on one line.
     */
    private static String escape(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("\t", "\\t")
                .replace("\n", "\\n");
    }

    /**
     * Unescapes a stored value.
     */
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

    /**
     * Moves a corrupted data file out of the way so Jarvis can start with a clean slate.
     */
    private static void backupCorruptedDataFileIfPossible(Path dataFilePath) {
        if (!Files.exists(dataFilePath)) {
            return;
        }

        try {
            Path backupPath = nextAvailableBackupPath(dataFilePath);
            Files.move(dataFilePath, backupPath);
        } 
        catch (IOException ignored) {
            // Best-effort backup only.
        }
    }

    /**
     * Returns a backup file path that does not already exist.
     */
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

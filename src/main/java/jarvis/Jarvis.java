package jarvis;

import java.nio.file.Path;

/**
 * Runs Jarvis Level-8, an intelligent chatbot that supports todos, deadlines, events, and deleting tasks.
 */
public class Jarvis {
    private static final String UNKNOWN_COMMAND_MESSAGE =
            "Sorry, I don't know what that means. Valid command starts with: "
                    + "todo, deadline, event, list, mark, unmark, delete, bye.";

    private final Storage storage;
    private final TaskList tasks;
    private final Ui ui;
    private final Parser parser;

    /**
     * Creates an Jarvis instance that reads/writes tasks from the given data file path.
     */
    public Jarvis(Path dataFilePathFromProjectRoot) {
        ui = new Ui();
        storage = new Storage(dataFilePathFromProjectRoot);
        parser = new Parser();

        TaskList loadedTasks;
        try {
            loadedTasks = new TaskList(storage.loadTasks());
        } catch (JarvisException exception) {
            ui.showLoadingError(exception.getMessage());
            loadedTasks = new TaskList();
        }
        tasks = loadedTasks;
    }

    /**
     * Runs Jarvis, reads commands from standard input, and exits on {@code bye}.
     */
    public void run() {
        ui.showWelcome();
        try (ui) {
            while (ui.hasNextCommand()) {
                String fullCommand = ui.readCommand();
                String trimmedCommand = fullCommand.trim();

                if (trimmedCommand.equalsIgnoreCase("bye")) {
                    ui.showBye();
                    return;
                }

                ui.showLine();
                try {
                    executeCommand(trimmedCommand);
                } catch (JarvisException exception) {
                    ui.showError(exception.getMessage());
                } finally {
                    ui.showLine();
                }
            }
        }
    }

    /**
     * Entry point for Jarvis.
     */
    public static void main(String[] args) {
        new Jarvis(Path.of("data", "Jarvis.txt")).run();
    }

    /**
     * Executes a single user command and prints the corresponding response.
     *
     * @param command Full user command line.
     * @throws JarvisException If the command is invalid or cannot be executed.
     */
    private void executeCommand(String command) throws JarvisException {
        if (command.isEmpty()) {
            throw new JarvisException("Please enter a command.");
        }

        Parser.CommandType commandType = parser.parseCommandType(command);
        switch (commandType) {
        case LIST:
            ui.showTaskList(tasks);
            return;
        case MARK: {
            int taskNumber = parser.parseTaskNumber(command);
            Task task = tasks.get(taskNumber);
            task.markAsDone();
            storage.saveTasks(tasks.getTasks());
            ui.showTaskMarked(task);
            return;
        }
        case UNMARK: {
            int taskNumber = parser.parseTaskNumber(command);
            Task task = tasks.get(taskNumber);
            task.markAsNotDone();
            storage.saveTasks(tasks.getTasks());
            ui.showTaskUnmarked(task);
            return;
        }
        case DELETE: {
            int taskNumber = parser.parseTaskNumber(command);
            Task removed = tasks.remove(taskNumber);
            storage.saveTasks(tasks.getTasks());
            ui.showTaskDeleted(removed, tasks.size());
            return;
        }
        case TODO: {
            Task task = parser.parseTodo(command);
            tasks.add(task);
            storage.saveTasks(tasks.getTasks());
            ui.showTaskAdded(task, tasks.size());
            return;
        }
        case DEADLINE: {
            Task task = parser.parseDeadline(command);
            tasks.add(task);
            storage.saveTasks(tasks.getTasks());
            ui.showTaskAdded(task, tasks.size());
            return;
        }
        case EVENT: {
            Task task = parser.parseEvent(command);
            tasks.add(task);
            storage.saveTasks(tasks.getTasks());
            ui.showTaskAdded(task, tasks.size());
            return;
        }
        case UNKNOWN:
        default:
            throw new JarvisException(UNKNOWN_COMMAND_MESSAGE);
        }
    }
}

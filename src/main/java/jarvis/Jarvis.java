package jarvis;

import java.nio.file.Path;

/**
 * Runs Jarvis Level-9, an intelligent chatbot that supports todos, deadlines, events, and deleting tasks.
 */
public class Jarvis {
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
            throw new JarvisException(Messages.EMPTY_COMMAND_MESSAGE);
        }

        Parser.CommandType commandType = parser.parseCommandType(command);
        switch (commandType) {
        case LIST:
            ui.showTaskList(tasks);
            return;
        case MARK:
            markTask(command);
            return;
        case UNMARK:
            unmarkTask(command);
            return;
        case DELETE:
            deleteTask(command);
            return;
        case FIND: {
            String keyword = parser.parseFindKeyword(command);
            ui.showMatchingTasks(tasks.findByKeyword(keyword));
            return;
        }
        case PRIORITY:
            updatePriority(command);
            return;
        case TODO:
            addTask(parser.parseTodo(command));
            return;
        case DEADLINE:
            addTask(parser.parseDeadline(command));
            return;
        case EVENT:
            addTask(parser.parseEvent(command));
            return;
        case UNKNOWN:
        default:
            throw new JarvisException(Messages.UNKNOWN_COMMAND_MESSAGE);
        }
    }

    private void persistTasks() throws JarvisException {
        storage.saveTasks(tasks.getTasks());
    }

    private void markTask(String command) throws JarvisException {
        int taskNumber = parser.parseSingleIndexCommand(command, "mark");
        Task task = tasks.get(taskNumber);
        task.markAsDone();
        persistTasks();
        ui.showTaskMarked(task);
    }

    private void unmarkTask(String command) throws JarvisException {
        int taskNumber = parser.parseSingleIndexCommand(command, "unmark");
        Task task = tasks.get(taskNumber);
        task.markAsNotDone();
        persistTasks();
        ui.showTaskUnmarked(task);
    }

    private void deleteTask(String command) throws JarvisException {
        int taskNumber = parser.parseSingleIndexCommand(command, "delete");
        Task removed = tasks.remove(taskNumber);
        persistTasks();
        ui.showTaskDeleted(removed, tasks.size());
    }

    private void addTask(Task task) throws JarvisException {
        tasks.add(task);
        persistTasks();
        ui.showTaskAdded(task, tasks.size());
    }

    private void updatePriority(String command) throws JarvisException {
        Parser.PriorityUpdate update = parser.parsePriorityUpdate(command);
        Task task = tasks.get(update.taskNumber());
        task.setPriority(update.priority());
        persistTasks();
        ui.showPriorityUpdated(task);
    }
}

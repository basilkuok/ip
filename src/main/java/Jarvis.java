import java.util.Scanner;

/**
 * Runs Jarvis Level-4, an intelligent chatbot that allows you to mark tasks as done/undone, and supports todos, deadlines, and events.
 */
public class Jarvis {
    private static final int MAX_TASKS = 100;

    /**
     * Starts Jarvis, reads commands from standard input, and exits on {@code bye}.
     */
    public static void main(String[] args) {
        String horizontalLine = "____________________________________________________________";
        System.out.println(horizontalLine);
        System.out.println(" Hello! I'm Jarvis");
        System.out.println(" I am your super-intelligent friend.");
        System.out.println(" What can I do for you?");
        System.out.println(" Tell me what's in your mind and I will echo back to you.");
        System.out.println(horizontalLine);

        Task[] tasks = new Task[MAX_TASKS];
        int taskCount = 0;

        try (Scanner input = new Scanner(System.in)) {
            while (input.hasNextLine()) {
                String fullCommand = input.nextLine();
                String trimmedCommand = fullCommand.trim();

                if (trimmedCommand.equalsIgnoreCase("bye")) {
                    System.out.println(horizontalLine);
                    System.out.println(" Bye. Hope to see you again soon!");
                    System.out.println(horizontalLine);
                    return;
                }

                System.out.println(horizontalLine);

                if (trimmedCommand.isEmpty()) {
                    System.out.println(" Please enter a command.");
                } else if (trimmedCommand.equalsIgnoreCase("list")) {
                    System.out.println(" Here are the tasks in your list:");
                    for (int i = 0; i < taskCount; i++) {
                        System.out.println(" " + (i + 1) + "." + tasks[i]);
                    }
                } else if (trimmedCommand.toLowerCase().startsWith("mark ")) {
                    Task task = getTaskForIndex(trimmedCommand, tasks, taskCount);
                    if (task != null) {
                        task.markAsDone();
                        System.out.println(" Nice! I've marked this task as done:");
                        System.out.println("  " + task);
                    }
                } else if (trimmedCommand.toLowerCase().startsWith("unmark ")) {
                    Task task = getTaskForIndex(trimmedCommand, tasks, taskCount);
                    if (task != null) {
                        task.markAsNotDone();
                        System.out.println(" OK, I've marked this task as not done yet:");
                        System.out.println("  " + task);
                    }
                } else if (trimmedCommand.toLowerCase().startsWith("todo ")) {
                    String description = trimmedCommand.substring("todo ".length()).trim();
                    if (description.isEmpty()) {
                        System.out.println(" Please provide a description for the todo.");
                    } else {
                        taskCount = addTask(tasks, taskCount, new Todo(description), horizontalLine);
                    }
                } else if (trimmedCommand.toLowerCase().startsWith("deadline ")) {
                    ParsedDeadline parsed = parseDeadline(trimmedCommand);
                    if (parsed == null) {
                        System.out.println(" Please use: deadline <description> /by <time>");
                    } else {
                        Task task = new Deadline(parsed.description, parsed.by);
                        taskCount = addTask(tasks, taskCount, task, horizontalLine);
                    }
                } else if (trimmedCommand.toLowerCase().startsWith("event ")) {
                    ParsedEvent parsed = parseEvent(trimmedCommand);
                    if (parsed == null) {
                        System.out.println(" Please use: event <description> /from <start> /to <end>");
                    } else {
                        Task task = new Event(parsed.description, parsed.from, parsed.to);
                        taskCount = addTask(tasks, taskCount, task, horizontalLine);
                    }
                } else {
                    taskCount = addTask(tasks, taskCount, new Todo(trimmedCommand), horizontalLine);
                }

                System.out.println(horizontalLine);
            }
        }
    }

    private static Task getTaskForIndex(String command, Task[] tasks, int taskCount) {
        int taskNumber = parseTaskNumber(command);
        if (!isValidTaskNumber(taskNumber, taskCount)) {
            System.out.println(" Please enter a valid task number.");
            return null;
        }
        return tasks[taskNumber - 1];
    }

    private static int addTask(Task[] tasks, int taskCount, Task task, String horizontalLine) {
        if (taskCount >= MAX_TASKS) {
            System.out.println(" Sorry, I can only store " + MAX_TASKS + " tasks.");
            return taskCount;
        }

        tasks[taskCount] = task;
        taskCount++;

        System.out.println(" Got it. I've added this task:");
        System.out.println("  " + task);
        System.out.println(" Now you have " + taskCount + " " + pluralize("task", taskCount) + " in the list.");

        return taskCount;
    }

    private static int parseTaskNumber(String command) {
        String[] parts = command.split("\\s+");
        if (parts.length < 2) {
            return -1;
        }

        try {
            return Integer.parseInt(parts[1]);
        } catch (NumberFormatException exception) {
            return -1;
        }
    }

    private static boolean isValidTaskNumber(int taskNumber, int taskCount) {
        return taskNumber >= 1 && taskNumber <= taskCount;
    }

    private static String pluralize(String word, int count) {
        if (count == 1) {
            return word;
        }
        return word + "s";
    }

    private static ParsedDeadline parseDeadline(String command) {
        String payload = command.substring("deadline ".length()).trim();
        int byIndex = payload.indexOf(" /by ");
        if (byIndex == -1) {
            return null;
        }

        String description = payload.substring(0, byIndex).trim();
        String by = payload.substring(byIndex + " /by ".length()).trim();
        if (description.isEmpty() || by.isEmpty()) {
            return null;
        }

        return new ParsedDeadline(description, by);
    }

    private static ParsedEvent parseEvent(String command) {
        String payload = command.substring("event ".length()).trim();
        int fromIndex = payload.indexOf(" /from ");
        int toIndex = payload.indexOf(" /to ");
        if (fromIndex == -1 || toIndex == -1 || fromIndex > toIndex) {
            return null;
        }

        String description = payload.substring(0, fromIndex).trim();
        String from = payload.substring(fromIndex + " /from ".length(), toIndex).trim();
        String to = payload.substring(toIndex + " /to ".length()).trim();
        if (description.isEmpty() || from.isEmpty() || to.isEmpty()) {
            return null;
        }

        return new ParsedEvent(description, from, to);
    }

    private static class ParsedDeadline {
        private final String description;
        private final String by;

        private ParsedDeadline(String description, String by) {
            this.description = description;
            this.by = by;
        }
    }

    private static class ParsedEvent {
        private final String description;
        private final String from;
        private final String to;

        private ParsedEvent(String description, String from, String to) {
            this.description = description;
            this.from = from;
            this.to = to;
        }
    }
}

import java.util.Scanner;

/**
 * Runs Jarvis Level-3, an intelligent chatbot that stores user-specified tasks, lists them back as requested, and marks tasks as done/undone.
 */
public class Jarvis {
    private static final int MAX_TASKS = 100;

    /**
     * Starts Jarvis Level-3, reads commands from standard input, stores entered tasks, lists it on {list},
     * marks tasks on {mark <index>} / {unmark <index>}, and exits on {bye}.
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
                String echo = input.nextLine();
                String trimmedEcho = echo.trim();

                if (trimmedEcho.equalsIgnoreCase("bye")) {
                    System.out.println(horizontalLine);
                    System.out.println(" Bye. Hope to see you again soon!");
                    System.out.println(horizontalLine);
                    return;
                }

                System.out.println(horizontalLine);

                if (trimmedEcho.equalsIgnoreCase("list")) {
                    System.out.println(" Here are the tasks in your list:");
                    for (int i = 0; i < taskCount; i++) {
                        System.out.println(" " + (i + 1) + "." + tasks[i]);
                    }
                } else if (trimmedEcho.toLowerCase().startsWith("mark ")) {
                    int taskNumber = parseTaskNumber(trimmedEcho);
                    if (isValidTaskNumber(taskNumber, taskCount)) {
                        tasks[taskNumber - 1].markAsDone();
                        System.out.println(" Nice! I've marked this task as done:");
                        System.out.println("  " + tasks[taskNumber - 1]);
                    } else {
                        System.out.println(" Please enter a valid task number.");
                    }
                } else if (trimmedEcho.toLowerCase().startsWith("unmark ")) {
                    int taskNumber = parseTaskNumber(trimmedEcho);
                    if (isValidTaskNumber(taskNumber, taskCount)) {
                        tasks[taskNumber - 1].markAsNotDone();
                        System.out.println(" OK, I've marked this task as not done yet:");
                        System.out.println("  " + tasks[taskNumber - 1]);
                    } else {
                        System.out.println(" Please enter a valid task number.");
                    }
                } else {
                    if (taskCount >= MAX_TASKS) {
                        System.out.println(" Sorry, I can only store " + MAX_TASKS + " tasks.");
                    } else {
                        tasks[taskCount] = new Task(trimmedEcho);
                        taskCount++;
                        System.out.println(" added: " + trimmedEcho);
                    }
                }

                System.out.println(horizontalLine);
            }
        }       
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
}

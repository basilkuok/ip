import java.util.Scanner;

/**
 * Runs Jarvis Level-2, an intelligent chatbot that stores user-specified input text and list it back.
 */
public class Jarvis {
    private static final int MAX_TASKS = 100;

    /**
     * Starts Jarvis Level-2, reads commands from user-specified input, stores entered text, lists it on {@code list},
     * and exits on {@code bye}.
     */
    public static void main(String[] args) {
        String horizontalLine = "____________________________________________________________";
        System.out.println(horizontalLine);
        System.out.println(" Hello! I'm Jarvis");
        System.out.println(" I am your super-intelligent friend.");
        System.out.println(" What can I do for you?");
        System.out.println(" Tell me what's in your mind and I will echo back to you.");
        System.out.println(horizontalLine);

        String[] tasks = new String[MAX_TASKS];
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
                    for (int i = 0; i < taskCount; i++) {
                        System.out.println(" " + (i + 1) + ". " + tasks[i]);
                    }
                } else {
                    if (taskCount < MAX_TASKS) {
                        tasks[taskCount] = echo;
                        taskCount++;
                    }
                    System.out.println(" added: " + echo);
                }

                System.out.println(horizontalLine);
            }
        }       
    }
}

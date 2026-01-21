import java.util.Scanner;

public class Jarvis {
    public static void main(String[] args) {
        String horizontalLine = "____________________________________________________________";
        System.out.println(horizontalLine);
        System.out.println(" Hello! I'm Jarvis");
        System.out.println(" I am your super-intelligent friend.");
        System.out.println(" What can I do for you?");
        System.out.println(" Tell me what's in your mind and I will echo back to you.");
        System.out.println(horizontalLine);

        Scanner input = new Scanner(System.in);
        while (input.hasNextLine()) {
            String echo = input.nextLine();
            if (echo.trim().equalsIgnoreCase("bye")) {
                System.out.println(horizontalLine);
                System.out.println(" Bye. Hope to see you again soon!");
                System.out.println(horizontalLine);
                return;
            }

            System.out.println(horizontalLine);
            System.out.println(" " + echo);
            System.out.println(horizontalLine);
        }


        
    }
}

import java.util.ArrayList;
import java.util.Scanner;

public class Bro {
    static ArrayList<Task> userTasks = new ArrayList<>();

    public static void main(String[] args) {
        String banner = "  ____                \n"
                + " | __ )  _ __   ___   \n"
                + " |  _ \\ | '__| / _ \\ \n"
                + " | |_) || |   | (_) | \n"
                + " |____/ |_|    \\___/  \n";
        Scanner scanner = new Scanner(System.in);
        System.out.println(banner);

        System.out.println("Hello, I'm Bro! What drink do you want?");
        String userInput = scanner.nextLine();
        while (!userInput.equals("bye")) {
            String actionWord = userInput.trim().split("\\s+", 2)[0];
            if (actionWord.equals("list")) {
                for (int i = 0; i < userTasks.size(); i++) {
                    System.out.println((i + 1) + ". " + "[" + userTasks.get(i).showDone() + "] " + userTasks.get(i).description);
                }
            }
            else if (actionWord.equals("mark")) {
                // can take it that we are marking an index
                int index = Integer.parseInt(userInput.trim().split("\\s+", 2)[1]);
                Task chosenTask = userTasks.get(index-1);
                chosenTask.isDone = true;
                System.out.println("Ok this item is marked!");
                System.out.println("[" + chosenTask.showDone() + "] " + chosenTask.description);

            }
            else if (actionWord.equals("unmark")) {
                int index = Integer.parseInt(userInput.trim().split("\\s+", 2)[1]);
                Task chosenTask = userTasks.get(index - 1);
                chosenTask.isDone = false;
                System.out.println("Ok this item is not marked! \n");
                System.out.println("[" + chosenTask.showDone() + "] " + chosenTask.description);
            } else {
                Task newTask = new Task(userInput);
                userTasks.add(newTask);
                System.out.println(newTask.description);
            }
            userInput = scanner.nextLine();
        }
        System.out.println("Goodbye!");
    }
}

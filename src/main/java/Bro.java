import java.util.ArrayList;
import java.util.Arrays;
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
                    System.out.println((i + 1) + ". " + userTasks.get(i).toString());;
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
            }
            else if (actionWord.equals("todo")) {
                String rest = userInput.trim().split("\\s+", 2)[1];
                ToDos newToDo = new ToDos(rest.trim());
                userTasks.add(newToDo);
                System.out.println("Got it. I've added: \n");
                System.out.println(newToDo.toString());
                System.out.println("Now you have" + userTasks.size() + " tasks in the list");
            }
            else if (actionWord.equals("deadline")) {
                String rest = userInput.trim().split("\\s+", 2)[1];
                String[] parts = rest.split("\\s*/by\\s*", 2);
                String taskDescription = parts[0].trim();
                String timeDescription = parts[1].trim();
                Deadlines newDeadline = new Deadlines(timeDescription, taskDescription);
                userTasks.add(newDeadline);

                System.out.println("Got it. I've added: \n");
                System.out.println(newDeadline.toString());
                System.out.println("Now you have " + userTasks.size() + " tasks in the list");
            }

            else if (actionWord.equals("event")) {
                String rest = userInput.trim().split("\\s+", 2)[1];
                String[] fromSplit = rest.split("\\s*/from\\s*", 2);
                String taskDescription = fromSplit[0].trim();
                String[] toSplit = fromSplit[1].split("\\s*/to\\s*", 2);
                String fromTimeDescription = toSplit[0].trim();
                String toTimeDescription = toSplit[1].trim();
                Events newEvent = new Events(fromTimeDescription, toTimeDescription, taskDescription);
                userTasks.add(newEvent);
                System.out.println("Got it. I've added: \n");
                System.out.println(newEvent.toString());
                System.out.println("Now you have " + userTasks.size() + " tasks in the list");

            }
            else {
                Task newTask = new Task(userInput);
                userTasks.add(newTask);
                System.out.println(newTask.description);
            }
            userInput = scanner.nextLine();
        }
        System.out.println("Goodbye!");
    }
}

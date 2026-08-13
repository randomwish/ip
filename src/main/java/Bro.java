import java.util.ArrayList;
import java.util.Scanner;

public class Bro {
    static ArrayList<String> userTasks = new ArrayList<>();

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
            if (userInput.equals("list")) {
                for (int i = 0; i < userTasks.size(); i++) {
                    System.out.println((i + 1) + ". " + userTasks.get(i));
                }
            } else {
                userTasks.add(userInput);
                System.out.println(userInput);
            }
            userInput = scanner.nextLine();
        }
        System.out.println("Ok bye! See you soon.");
    }
}

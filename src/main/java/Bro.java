import java.util.Scanner;

public class Bro {
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
            System.out.println(userInput);
            userInput = scanner.nextLine();
        }
        System.out.println("Ok bye! See you soon.");
    }
}

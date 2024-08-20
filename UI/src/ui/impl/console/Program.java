package ui.impl.console;

import java.util.Scanner;

public class Program {

    public static void printMenu() {
        System.out.println();
        System.out.println("Choose an option form the menu below:");
        for (Menu item: Menu.values()) {
            System.out.println("(" + item.getOrdinal() + ") " + item);
        }
    }

    public static void main(String[] args) {
        final Scanner scanner = new Scanner(System.in);
        final int exitOption = Menu.EXIT.getOrdinal();

        System.out.println("Welcome!");
        printMenu();
        String userOptionStr = scanner.nextLine();
        int userOption = Integer.parseInt(userOptionStr);

        while (userOption != exitOption) {
            Menu selectedOption = Menu.values()[userOption - 1];
            selectedOption.run();
            printMenu();
            userOptionStr = scanner.nextLine();
            userOption = Integer.parseInt(userOptionStr);
        }

        Menu.EXIT.run();
    }
}
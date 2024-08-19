package ui.impl.console;

import java.util.Scanner;

public class Program {

    public static void printMenu() {
        System.out.println();
        System.out.println("Choose an option form the menu below:");
        for (Menu s : Menu.values()) {
            System.out.println("(" + s.getOrdinal() + ") " + s);
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int exitOption = Menu.EXIT.getOrdinal();
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
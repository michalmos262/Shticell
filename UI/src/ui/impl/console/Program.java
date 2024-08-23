package ui.impl.console;

import java.util.Scanner;

public class Program {

    public static void printMenu() {
        System.out.println();
        System.out.println("Choose an option from the menu below:");
        for (Menu item: Menu.values()) {
            System.out.println("(" + item.getOrdinal() + ") " + item);
        }
    }

    public static Menu getMenuOption() {
        Scanner scanner = new Scanner(System.in);
        String userOptionStr = scanner.nextLine();
        try {
            int userOption = Integer.parseInt(userOptionStr);
            return Menu.values()[userOption - 1];
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid menu option: " + userOptionStr);
        }
    }

    public static void runProgram() {
        try {
            printMenu();
            Menu menuOption = getMenuOption();
            while (menuOption != Menu.EXIT) {
                menuOption.run();
                printMenu();
                menuOption = getMenuOption();
            }
            Menu.EXIT.run();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            runProgram();
        }
    }

    public static void main(String[] args) {
        runProgram();
    }
}
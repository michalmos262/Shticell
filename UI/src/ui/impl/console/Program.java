package ui.impl.console;

public class Program {
    public static void main(String[] args) {
        ConsoleInteraction console = new ConsoleInteraction();

        console.showSheet();
        console.updateSheetCell();
        console.showSheet();
    }
}
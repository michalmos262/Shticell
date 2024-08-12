package ui.impl.console;

public class Program {
    public static void main(String[] args) {
        ConsoleInteraction console = new ConsoleInteraction();
        while (1 == 1) {
            console.showSheet(console.getEngine().getCurrentSheetVersion());
            console.updateSheetCell();
            console.showSheetCell();
            console.showSheetVersions();
        }
    }
}
package ui.impl.console;

import engine.api.Engine;
import ui.api.Ui;

public enum Menu {
    LOAD_FILE("Load a sheet from " + Engine.SUPPORTED_FILE_TYPE.toUpperCase()) {
        @Override
        void run() {
            console.loadFile();
        }
    },
    SHOW_SHEET("Show a sheet of the current version") {
        @Override
        void run() {
            console.showCurrentVersionSheet();
        }
    },
    SHOW_CELL("Show a cell of the current version") {
        @Override
        void run() {
            console.showSheetCell();
        }
    },
    UPDATE_CELL("Update a cell") {
        @Override
        void run() {
            console.updateSheetCell();
        }
    },
    SHOW_VERSIONS("Show a sheet of a specific version") {
        @Override
        void run() {
            console.showSheetVersionsForDisplay();
        }
    },
    LOAD_SYSTEM("Load a sheet system") {
        @Override
        void run() {
            console.loadSheetVersionsFromFile();
        }
    },
    SAVE_SYSTEM("Save the current system") {
        @Override
        void run() {
            console.saveCurrentSheetVersionsToFile();
        }
    },
    EXIT("Exit program") {
        @Override
        void run() {
            console.exitProgram();
        }
    };

    private final String name;
    private static final Ui console = new ConsoleInteraction();

    Menu(String name) {
        this.name = name;
    }

    abstract void run();

    public int getOrdinal() {
        return ordinal() + 1;
    }

    @Override
    public String toString() {
        return name;
    }
}
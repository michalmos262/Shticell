package ui.impl.console;

import ui.api.Ui;

import java.io.IOException;

public enum Menu {
    LOAD_FILE("Load a one version sheet file") {
        @Override
        void run() {
            console.loadFile();
        }
    },
    SHOW_SHEET("Show current version sheet") {
        @Override
        void run() {
            console.showCurrentVersionSheet();
        }
    },
    SHOW_CELL("Show current version cell") {
        @Override
        void run() {
            console.showSheetCell();
        }
    },
    UPDATE_CELL("Update cell") {
        @Override
        void run() throws Exception {
            console.updateSheetCell();
        }
    },
    SHOW_VERSIONS("Choose a sheet version for display") {
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
        void run() throws IOException {
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

    abstract void run() throws Exception;

    public int getOrdinal() {
        return ordinal() + 1;
    }

    @Override
    public String toString() {
        return name;
    }
}
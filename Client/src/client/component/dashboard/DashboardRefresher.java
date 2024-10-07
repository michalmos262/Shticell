package client.component.dashboard;

import client.util.http.HttpClientUtil;
import javafx.beans.property.BooleanProperty;

import java.util.List;
import java.util.TimerTask;
import java.util.function.Consumer;

public class DashboardRefresher extends TimerTask {
    private BooleanProperty shouldUpdate;
    private Consumer<List<DashboardModelUI.SheetsTableEntry>> sheetsTableEntries;

    public DashboardRefresher(BooleanProperty shouldUpdate, Consumer<List<DashboardModelUI.SheetsTableEntry>> sheetsTableEntries) {
        this.shouldUpdate = shouldUpdate;
        this.sheetsTableEntries = sheetsTableEntries;
    }

    @Override
    public void run() {
        if (!shouldUpdate.get()) {
            return;
        }

//        HttpClientUtil.runAsyncGet();
    }
}

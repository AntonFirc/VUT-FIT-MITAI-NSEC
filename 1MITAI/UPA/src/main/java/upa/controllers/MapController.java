package main.java.upa.controllers;

import javafx.stage.Stage;
import main.java.upa.view.map.MapView;

import java.io.IOException;
import java.sql.SQLException;

public class MapController {

    private MapView view;

    public MapController() throws IOException, SQLException {
        view = new MapView();
    }

    public Stage getStage() {
        return view.getStage();
    }
}

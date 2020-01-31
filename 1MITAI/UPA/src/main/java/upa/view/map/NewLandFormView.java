package main.java.upa.view.map;

import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import main.java.upa.model.land.LandModel;
import main.java.upa.model.user.User;
import main.java.upa.model.user.UserModel;
import oracle.spatial.geometry.JGeometry;

import java.sql.SQLException;

public class NewLandFormView {

    private static Stage window = new Stage();
    private static MapView mapView;
    private static UserModel userModel = new UserModel();
    private static LandModel landModel = new LandModel();

    public static void setController(MapView controller) {
        mapView = controller ;
    }

    public static void initialize () {
        if (window.getModality() == null) {
            window.initModality(Modality.APPLICATION_MODAL);
        }
        window.setTitle("Add new land");
        window.setWidth(350);
        window.setHeight(450);

    }

    public static void display () {

        Label label = new Label();
        label.setText("Enter land parameters");

        Label xStartLabel = new Label("X start:");
        TextField xStart = new TextField ();
        HBox hbXStart = new HBox();
        hbXStart.setAlignment(Pos.CENTER);
        hbXStart.getChildren().addAll(xStartLabel, xStart);
        hbXStart.setSpacing(10);


        Label xEndLabel = new Label("X end:");
        TextField xEnd = new TextField ();
        HBox hbXEnd= new HBox();
        hbXEnd.setAlignment(Pos.CENTER);
        hbXEnd.getChildren().addAll(xEndLabel, xEnd);
        hbXEnd.setSpacing(10);

        Label yStartLabel = new Label("Y start:");
        TextField yStart = new TextField ();
        HBox hbYStart = new HBox();
        hbYStart.setAlignment(Pos.CENTER);
        hbYStart.getChildren().addAll(yStartLabel, yStart);
        hbYStart.setSpacing(10);


        Label yEndLabel = new Label("Y end:");
        TextField yEnd = new TextField ();
        HBox hbYEnd= new HBox();
        hbYEnd.setAlignment(Pos.CENTER);
        hbYEnd.getChildren().addAll(yEndLabel, yEnd);
        hbYEnd.setSpacing(10);

        ComboBox landCategorySelect = new ComboBox(FXCollections.observableArrayList("built-up-area", "arable-land", "nothing"));
        landCategorySelect.setPromptText("Select Land Category");
        ComboBox userSelect = new ComboBox(userModel.getAll());
        userSelect.setPromptText("Select User");

        Button apply = new Button("Apply");
        apply.setOnAction(event -> {
            // TODO CREATE NEW RECTANGLE LAND + ADD CATEGORY SELECT
            LandModel landModel = new LandModel();
            JGeometry groundPlan = landModel.createRectangleGroundPlan(Integer.parseInt(xStart.getText()), Integer.parseInt(yStart.getText()), Integer.parseInt(xEnd.getText()), Integer.parseInt(yEnd.getText()));
            try {
                User user = (User) userSelect.getValue();
                landModel.createLand(groundPlan, user.getId(), landCategorySelect.getValue().toString());
                mapView.saveChanges();
                mapView.loadMap();
                window.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(hbXStart, hbYStart, hbXEnd, hbYEnd, landCategorySelect, userSelect, apply);

        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();

    }
}

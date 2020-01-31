package main.java.upa.view.map;

import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import main.java.upa.model.land.LandModel;
import main.java.upa.model.property.Property;
import main.java.upa.model.property.PropertyModel;
import oracle.spatial.geometry.JGeometry;

import java.sql.SQLException;

public class NewPropertyFormView {

    private static Stage window = new Stage();
    private static MapView mapView;

    public static void setController(MapView controller) {
        mapView = controller ;
    }

    public static void initialize () {
        if (window.getModality() == null) {
            window.initModality(Modality.APPLICATION_MODAL);
        }
        window.setTitle("Add new property");
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

        ComboBox propertyCategorySelect = new ComboBox(FXCollections.observableArrayList("house", "flat", "hydrant"));
        propertyCategorySelect.setPromptText("Select Property Category");

        propertyCategorySelect.valueProperty().addListener(event -> {
            if(propertyCategorySelect.getValue().toString().equals("hydrant"))
            {
                hbXEnd.setVisible(false);
                hbYEnd.setVisible(false);
            }
            else {
                hbXEnd.setVisible(true);
                hbYEnd.setVisible(true);
            }
        });

        Button apply = new Button("Apply");
        apply.setOnAction(event -> {
            // TODO CREATE NEW RECTANGLE LAND + ADD CATEGORY SELECT
            PropertyModel propertyModel = new PropertyModel();
            JGeometry groundPlan = null;
            if (propertyCategorySelect.getValue().toString().equals("house") || propertyCategorySelect.getValue().toString().equals("flat"))
            {
                groundPlan = propertyModel.createRectangleGroundPlan(Integer.parseInt(xStart.getText()), Integer.parseInt(yStart.getText()), Integer.parseInt(xEnd.getText()), Integer.parseInt(yEnd.getText()));
            }
            else if (propertyCategorySelect.getValue().toString().equals("hydrant"))
            {
                groundPlan = propertyModel.createPointGroundPlan(Double.parseDouble(xStart.getText()), Double.parseDouble(yStart.getText()));
            }
            try {
                propertyModel.createProperty(groundPlan, propertyCategorySelect.getValue().toString());
                mapView.saveChanges();
                mapView.loadMap();
                window.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(hbXStart, hbYStart, hbXEnd, hbYEnd, propertyCategorySelect, apply);

        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();

    }
}

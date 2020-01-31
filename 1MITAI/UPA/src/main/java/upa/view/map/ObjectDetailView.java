package main.java.upa.view.map;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import main.java.upa.model.land.Land;
import main.java.upa.model.land.LandModel;
import main.java.upa.model.photo.PhotoModel;
import main.java.upa.model.property.Property;
import main.java.upa.model.property.PropertyModel;
import main.java.upa.model.user.User;
import main.java.upa.view.main.UploadImage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedList;

public class ObjectDetailView {

    private static Stage window = new Stage();
    private static PhotoModel pModel;

    static {
        try {
            pModel = new PhotoModel();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void initialize () {
        if (window.getModality() == null) {
            window.initModality(Modality.APPLICATION_MODAL);
        }
        window.setTitle("Object detail");
        window.setWidth(650);
        window.setHeight(150);
    }

    public static void display (int id, boolean property) throws SQLException, IOException {

        VBox layout = new VBox(10);

        LandModel landModel = new LandModel();
        PropertyModel propertyModel = new PropertyModel();

        if(property) {
            Label heading = new Label();
            heading.setText("Property "+id+" detail");

            layout.getChildren().add(heading);

            String category = propertyModel.getPropertyCategory(id);

            if(category.equals("house") || category.equals("flat")) {

                Label user = new Label();
                user.setText("User: " + (propertyModel.getPropertyUser(id) == null ? "system" : propertyModel.getPropertyUser(id).toString()));

                Button uploadImg = new Button("Upload new image");
                uploadImg.setOnAction(event -> {
                    try {
                        UploadImage.display(id);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });

                LinkedList<Integer> pPhotos = pModel.getPropertyPhotos(id);
                PhotoButton pBtn[] = new PhotoButton[pPhotos.size()];
                System.out.println(pPhotos.size());

                for (int idx = 0; idx < pPhotos.size(); idx++) {
                    pBtn[idx] = new PhotoButton(pPhotos.get(idx), id,"Photo "+idx);
                    layout.getChildren().add(pBtn[idx].photoBtn);
                }

                layout.getChildren().addAll(user, uploadImg);
            } else if(category.equals("hydrant")) {

                double distance = propertyModel.getHydrantDistance(id);

                Label distanceLabel = new Label();
                distanceLabel.setText("Distance to nearest house: " + distance + "m");

                layout.getChildren().add(distanceLabel);
            }

        } else {

            Land land = landModel.getById(id);

            Label userName = new Label();
            User user = land.getUser();
            userName.setText(user == null ? "system" : user.toString());

            Label areaSize = new Label();
            double size = landModel.getLandArea(id);
            areaSize.setText("Area: " + size + "m2");

            Label num = new Label();
            int number = landModel.countProperties(id);
            num.setText("Properties: " + number);

            Label near = new Label();
            int nearNum = landModel.nearestLand(id);
            near.setText("Nearest land: " + nearNum + "m");

            layout.getChildren().addAll(userName, areaSize, num, near);

        }

        try {
            Scene scene = new Scene(layout);
            window.setScene(scene);
            window.showAndWait();
        }
        catch (Exception ex) {

        }


    }
}

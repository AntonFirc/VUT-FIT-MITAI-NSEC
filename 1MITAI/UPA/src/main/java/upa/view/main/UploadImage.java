package main.java.upa.view.main;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import main.java.upa.model.photo.PhotoModel;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class UploadImage {

    private static Stage window = new Stage();
    private static String fileName = null;
    final static TextField imagePath = new TextField();

    public static void initialize () {
        if (window.getModality() == null) {
            window.initModality(Modality.APPLICATION_MODAL);
        }
        window.setTitle("Connection details");
        window.setWidth(650);
        window.setHeight(150);

    }

    private static void getFileName() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"));
        File selectedFile = fileChooser.showOpenDialog(window);
        if (selectedFile != null) {
            fileName = selectedFile.getAbsolutePath();
            imagePath.setText(fileName);
        }
    }

    public static void display (int propertyID) throws SQLException {
        
        Label label = new Label();
        label.setText("Choose an image to upload");

        imagePath.setPromptText("Absolute path to file");
        imagePath.setPrefColumnCount(10);

        Button fileExplorer = new Button("Open file explorer");
        fileExplorer.setOnAction( event -> getFileName());

        Button upload = new Button("Upload");
        PhotoModel pModel = new PhotoModel();
        upload.setOnAction( event -> {
            upload.setText("Uploading...");
            try {
                pModel.uploadPropertyPhoto(fileName, propertyID);
            } catch (SQLException | IOException e) {
                e.printStackTrace();
            } finally {
                window.close();
            }
        });

        VBox layout = new VBox(10);
        layout.getChildren().addAll(label, imagePath, fileExplorer, upload);
        layout.setAlignment(Pos.CENTER);



        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();

    }
}

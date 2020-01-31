package main.java.upa.view.map;

import javafx.scene.control.Button;
import main.java.upa.view.main.ImageViewer;

import java.io.IOException;
import java.sql.SQLException;

public class PhotoButton {

    public Button photoBtn;
    public ImageViewer iViewer;
    public int propertyID;

    PhotoButton(int imgID, int propertyID, String label) throws IOException, SQLException {
        photoBtn = new Button(label);
        iViewer = new ImageViewer(imgID, propertyID);
        this.propertyID = propertyID;

        photoBtn.setOnAction(event -> {
            this.iViewer.show();
        });
    }

}


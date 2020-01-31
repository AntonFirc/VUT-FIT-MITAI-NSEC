package main.java.upa.model.photo;

import javafx.scene.image.Image;

public class Photo {

    private int id;
    private int propertyId;
    private Image img;

    public Photo(int id, int propertyId, Image img) {
        this.id = id;
        this.propertyId = propertyId;
        this.img = img;
    }

    public int getId() {return id;}

    public int getPropertyid() {return propertyId;}

    public Image getImage() {return img;}

    public void resetImage(Image img) {
        this.img = img;
    }

}

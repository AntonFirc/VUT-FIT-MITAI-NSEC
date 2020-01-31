package main.java.upa.model.photo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import main.java.upa.model.DatabaseModel;
import oracle.jdbc.OraclePreparedStatement;
import oracle.jdbc.OracleResultSet;
import oracle.ord.im.OrdImage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;

import static java.lang.System.exit;

public class PhotoModel {

    public int id;
    public OrdImage image;

    private DatabaseModel dbModel;

    public PhotoModel() throws SQLException {
        this.dbModel = DatabaseModel.getInstance();
    }

    public int getNewID() throws SQLException {
        int max = 0;

        try {

            Statement st = this.dbModel.getConnection().createStatement();
            OracleResultSet set = (OracleResultSet)st.executeQuery(
                    "SELECT MAX(ID) AS MAX FROM PHOTO"
            );

            if(set.next()) {
                max = set.getInt("max");
            }

            set.close();
            st.close();

        } catch(SQLException ex) {
            System.err.println(ex.getMessage());
            exit(-1);
        }

        return max+1;

    }

    public OrdImage imgProxy(int imgID) throws SQLException
    {
        OrdImage imgProxy = null;
        OraclePreparedStatement pstmtSelect = (OraclePreparedStatement) dbModel.getConnection().prepareStatement(
                "SELECT IMAGE FROM PHOTO WHERE ID=" + imgID + " FOR UPDATE"
        );
        try
        {
            OracleResultSet rset = (OracleResultSet) pstmtSelect.executeQuery();
            try
            {
                if (rset.next())
                {
                    imgProxy = (OrdImage) rset.getORAData("IMAGE", OrdImage.getORADataFactory());
                }
            }
            finally
            {
                rset.close();
            }
        }
        finally
        {
            pstmtSelect.close();
        }
        return imgProxy;
    }

    public int uploadImage(String filename) throws SQLException, IOException
    {
        System.out.println("Beginning image upload from file: \"" + filename+"\"");
        int imgID = this.getNewID();

        boolean autoCommit = dbModel.getConnection().getAutoCommit();
        dbModel.getConnection().setAutoCommit(false);

        // insert a new record with an empty ORDImage object
        Statement stmt1 = dbModel.getConnection().createStatement();
        String insertSQL = "INSERT INTO PHOTO(ID,  IMAGE ) VALUES (" + imgID +", ordsys.ordimage.init() )";
        stmt1.executeUpdate(insertSQL);
        stmt1.close();

        OrdImage imgProxy = imgProxy(imgID);
        if(imgProxy == null)
        {
            return -1;
        }

        // load the media data from a file to the ORDImage Java object
        try {
            imgProxy.loadDataFromFile(filename);
        }
       catch (SQLException ex) {
            System.err.println(ex);
            return -1;
       }
        // set the properties of the Oracle Mm object from the Java object
        imgProxy.setProperties();

        // update the table with ORDImage Java object (data already loaded)
        String updateSQL1 = "UPDATE PHOTO SET"+
                " IMAGE=? WHERE ID = "+imgID;
        OraclePreparedStatement pstmt = (OraclePreparedStatement)
                dbModel.getConnection().prepareStatement(updateSQL1);
        pstmt.setORAData(1, imgProxy);
        pstmt.executeUpdate();
        pstmt.close();

        dbModel.getConnection().commit();

        // insert a new record with an empty ORDImage object
        Statement callProcedure = dbModel.getConnection().createStatement();
        String procedure = "begin image_generateAttributes("+imgID+"); end;";
        callProcedure.execute(procedure);
        callProcedure.close();

        System.out.println("Image uploaded.");
        dbModel.getConnection().setAutoCommit(autoCommit);

        return imgID;
    }

    public void deleteImage(int imgID) {
        System.out.println("Trying to delete image with ID \"" +imgID+"\"");
        try {
            // delete from photo table
            Statement stmt1 = dbModel.getConnection().createStatement();
            String insertSQL = "DELETE FROM PHOTO WHERE  ID=" + imgID;
            stmt1.executeUpdate(insertSQL);
            stmt1.close();
        }
        catch (SQLException ex) {
            System.err.println(ex);
            return;
        }
        finally {
            System.out.println("Image with ID \"" +imgID+"\" has been deleted.");
        }
    }

    public Photo getImage(int imgID) throws SQLException, IOException
    {
        //get image with ID
        OrdImage imgProxy = imgProxy(imgID);
        if(imgProxy == null)
        {
            return null;
        }
        //store image into local variable
        BufferedImage bufferedImg = ImageIO.read(new ByteArrayInputStream(imgProxy.getDataInByteArray()));
        Image image = SwingFXUtils.toFXImage(bufferedImg, null);

        int propertyID = getProperty(imgID);

        Photo img = new Photo(imgID, propertyID, image);

        return img;
    }

    public int getProperty(int imgID)
    {
       int propertyId = 0;

        try {
            Statement st = this.dbModel.getConnection().createStatement();
            OracleResultSet set = (OracleResultSet)st.executeQuery(
                    "SELECT PROPERTY_ID AS MAX FROM PHOTO WHERE ID = "+imgID
            );

            if(set.next()) {
                propertyId = set.getInt(1);
            }

            set.close();
            st.close();

        } catch(SQLException ex) {
            System.err.println(ex.getMessage());
            exit(-1);
        }

        return propertyId;
    }

    public Integer similarSearch(int imgID, int propertyID) throws SQLException, IOException
    {
        ObservableList<Integer> images = FXCollections.observableArrayList();
        PreparedStatement pstmtSelect = dbModel.getConnection().prepareStatement(
                "SELECT dst.*, SI_ScoreByFtrList(" +
                        "new SI_FeatureList(src.IMAGE_AC,0.7,src.IMAGE_CH,0.1,src.IMAGE_PC,0.1,src.IMAGE_TX,0.1),dst.IMAGE_SI)" +
                        " AS similarity FROM PHOTO src, PHOTO dst " +
                        "WHERE (src.ID <> dst.ID) AND (dst.PROPERTY_ID <> "+propertyID+" ) AND src.ID = " + imgID +
                        " ORDER BY similarity ASC"
        );
        OracleResultSet rset = (OracleResultSet) pstmtSelect.executeQuery();
        while (rset.next())
        {
            images.add(rset.getInt("ID"));
        }
        rset.close();
        pstmtSelect.close();
        return images.get(0);
    }

    public void uploadPropertyPhoto(String filename, int propertyID) throws SQLException, IOException {
        int imgID = uploadImage(filename);
        //update PHOTO table entry and link it to property
        Statement stmt1 = dbModel.getConnection().createStatement();
        String insertSQL = "UPDATE PHOTO SET PROPERTY_ID = "+propertyID+" WHERE ID = "+imgID;
        stmt1.executeUpdate(insertSQL);
        stmt1.close();
    }

    public void removePhotoFromProperty(int imgID, int propertyID) throws SQLException {
        //remove PHOTO table entry link to property
        Statement stmt1 = dbModel.getConnection().createStatement();
        String insertSQL = "UPDATE PHOTO SET PROPERTY_ID = null WHERE ID = "+imgID;
        stmt1.executeUpdate(insertSQL);
        stmt1.close();
    }

    public LinkedList<Integer> getPropertyPhotos(int propertyID) {
        LinkedList<Integer> photos = new LinkedList<>();

        try {

            Statement st = this.dbModel.getConnection().createStatement();
            OracleResultSet set = (OracleResultSet)st.executeQuery(
                    "SELECT ID FROM PHOTO WHERE PROPERTY_ID = "+propertyID
            );

            while(set.next()) {
                photos.add(set.getInt(1));
            }

            set.close();
            st.close();

        } catch(SQLException ex) {
            System.err.println(ex.getMessage());
            exit(-1);
        }

        return photos;
    }

    public int getPropertyIdFromImgId(int imgID) {
        int propertyId = 0;

        try {
            Statement st = this.dbModel.getConnection().createStatement();
            OracleResultSet set = (OracleResultSet)st.executeQuery(
                    "SELECT PROPERTY_ID FROM PHOTO WHERE ID = "+imgID
            );

            if(set.next()) {
                propertyId = set.getInt(1);
            }

            set.close();
            st.close();

        } catch(SQLException ex) {
            System.err.println(ex.getMessage());
            exit(-1);
        }

        return propertyId;
    }

    public Image transformImage(int imgID, String transformation) throws SQLException, IOException
    {
        boolean autoCommit = dbModel.getConnection().getAutoCommit();
        dbModel.getConnection().setAutoCommit(false);

        OrdImage imgProxy = imgProxy(imgID);
        if(imgProxy == null)
        {
            return null;
        }
        try {
            imgProxy.processCopy(transformation, imgProxy);
        }
        catch (SQLException ex) {
            System.err.println(ex);
        }
        BufferedImage bufferedImg = ImageIO.read(new ByteArrayInputStream(imgProxy.getDataInByteArray()));
        Image tImage = SwingFXUtils.toFXImage(bufferedImg, null);

        dbModel.getConnection().commit();
        dbModel.getConnection().setAutoCommit(autoCommit);

        return tImage;
    }

}

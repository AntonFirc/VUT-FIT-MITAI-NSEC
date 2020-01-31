package main.java.upa.model.user;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import main.java.upa.model.DatabaseModel;
import main.java.upa.model.land.Land;
import main.java.upa.model.property.Property;
import oracle.jdbc.OracleResultSet;
import oracle.spatial.geometry.JGeometry;

import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Struct;

import static java.lang.System.exit;

public class User {

    private int id;
    private String email;
    private String firstName;
    private String surName;

    public User(int id, String email, String firstName, String surName) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.surName = surName;
    }

    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSurName() {
        return surName;
    }

    public void setSurName(String surName) {
        this.surName = surName;
    }

    /*
    public ObservableList<Property> getUserProperties(int userId) {

        ObservableList<Property> list = FXCollections.observableArrayList();

        DatabaseModel dbModel = DatabaseModel.getInstance();

        try {

            Statement st = dbModel.getConnection().createStatement();
            OracleResultSet set = (OracleResultSet)st.executeQuery(
                    "SELECT * FROM PROPERTY WHERE SYS_USER = " + userId
            );

            while(set.next()) {
                // convert the Struct into a JGeometry
                Struct obj = (Struct) set.getObject(3);
                JGeometry jgeom = JGeometry.loadJS(obj);

                Property property = new Property(
                        set.getInt("id"),
                        jgeom,
                        set.getString("property_type")
                );
                list.add(property);
            }

            set.close();
            st.close();

        } catch(SQLException ex) {
            System.err.println(ex.getMessage());
            exit(-1);
        }

        return list;
    }*/

    public ObservableList<Land> getUserLands(int userId) {

        ObservableList<Land> list = FXCollections.observableArrayList();

        DatabaseModel dbModel = DatabaseModel.getInstance();

        try {

            Statement st = dbModel.getConnection().createStatement();
            OracleResultSet set = (OracleResultSet)st.executeQuery(
                    "SELECT * FROM LAND WHERE SYS_USER = " + userId
            );

            while(set.next()) {

                UserModel userModel = new UserModel();

                Land land = new Land(
                        set.getInt("id"),
                        null,
                        set.getString("category"),
                        userModel.getById(set.getInt("user"))
                );
                list.add(land);
            }

            set.close();
            st.close();

        } catch(SQLException ex) {
            System.err.println(ex.getMessage());
            exit(-1);
        }

        return list;
    }

    @Override
    public String toString() {
        return firstName + " " + surName;
    }
}

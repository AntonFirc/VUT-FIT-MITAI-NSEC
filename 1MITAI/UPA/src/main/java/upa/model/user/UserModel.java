package main.java.upa.model.user;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import main.java.upa.model.DatabaseModel;
import main.java.upa.model.land.Land;
import oracle.jdbc.OraclePreparedStatement;
import oracle.jdbc.OracleResultSet;

import java.sql.SQLException;
import java.sql.Statement;

import static java.lang.System.exit;

public class UserModel {

    private DatabaseModel dbModel;

    public UserModel() {
        this.dbModel = DatabaseModel.getInstance();
    }

    public void create(String email, String firstName, String surName) {

        try {

            Statement st = dbModel.getConnection().createStatement();
            st.executeQuery(
                    "INSERT INTO sys_user(email, firstname, surname) VALUES('" + email + "', '" + firstName + "', '" + surName + "')"
            );

        } catch(SQLException ex) {
            System.err.println(ex.getMessage());
            exit(-1);
        }
    }

    public User getById(int id) {

        User user = null;

        try {

            Statement st = this.dbModel.getConnection().createStatement();
            OracleResultSet set = (OracleResultSet)st.executeQuery(
                    "SELECT * FROM SYS_USER WHERE ID = " + id
            );

            if(set.next()) {
                user = new User(
                        set.getInt("id"),
                        set.getString("email"),
                        set.getString("firstname"),
                        set.getString("surname")
                );
            }

            set.close();
            st.close();

        } catch(SQLException ex) {
            System.err.println(ex.getMessage());
            exit(-1);
        }

        return user;
    }

    public ObservableList<User> getAll() {

        ObservableList<User> list = FXCollections.observableArrayList();

        try {

            Statement st = dbModel.getConnection().createStatement();
            OracleResultSet set = (OracleResultSet)st.executeQuery(
                    "SELECT * FROM SYS_USER"
            );

            while(set.next()) {
                User newUser = new User(
                        set.getInt("id"),
                        set.getString("email"),
                        set.getString("firstname"),
                        set.getString("surname")
                );
                list.add(newUser);
            }

            set.close();
            st.close();

        } catch(SQLException ex) {
            System.err.println(ex.getMessage());
            exit(-1);
        }

        return list;
    }

    public void delete(int id) {
        try {
            OraclePreparedStatement pstmtSelect = (OraclePreparedStatement) this.dbModel.getConnection().prepareStatement(
                    "delete from sys_user where id = " + id
            );
            pstmtSelect.executeQuery();
            pstmtSelect.close();
        } catch(SQLException ex) {
            System.err.println(ex.getMessage());
            exit(-1);
        }
    }
}

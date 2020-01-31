package main.java.upa.model;

import oracle.jdbc.pool.OracleDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseModel {

    private static DatabaseModel singleton = null;

    private OracleDataSource ods;
    private Connection connection;

    public DatabaseModel() {
        singleton = this;
    }

    public static DatabaseModel getInstance() {
        return singleton;
    }

    public boolean connect(String host, String port, String serviceName, String username, String password) {

        String url = "jdbc:oracle:thin:@//" + host + ":" + port + "/" + serviceName;

        try {
            this.ods = new OracleDataSource();
        } catch(SQLException ex) {
            return false;
        }

        this.ods.setURL(url);
        this.ods.setUser(username);
        this.ods.setPassword(password);

        try {
            this.connection = this.ods.getConnection();
        } catch(SQLException ex) {
            this.ods = null;
            return false;
        }

        System.out.println("Database connection successful");
        return true;
    }

    public boolean disconnect() {

        if(this.connection != null) {
            try {
                this.connection.close();
            } catch(SQLException ex) {
                return false;
            }

            this.connection = null;
        }

        return true;
    }

    public boolean isConnected() {

        return this.connection == null;
    }

    public Connection getConnection() {
        return this.connection;
    }
}

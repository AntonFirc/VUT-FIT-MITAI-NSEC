package main.java.upa.view.main;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import main.java.upa.model.DatabaseModel;
import main.java.upa.view.map.MapView;

import java.sql.SQLException;

public class DatabaseSetupView {

    private static Stage window = new Stage();
    private static MapView mapView;

    public static void setController(MapView controller) {
        mapView = controller ;
    }

    private static void saveInfo(String host, String port, String service, String user, String password, boolean connect) throws SQLException {
        MapView.DB_hostname = host;
        MapView.DB_port = port;
        MapView.DB_service = service;
        MapView.DB_user = user;
        MapView.DB_password = password;

        if (connect) {
            DatabaseModel dbModel = DatabaseModel.getInstance();
            dbModel.connect(host, port, service, user, password);
            mapView.loadMap();
        }
        window.close();
    }

    private static void validate(String host, String port, String service, String user, String password, boolean connect) throws SQLException {
        if (host.equals("")) {
            System.err.println("Please enter valid host!");
            return;
        }
        if (port.equals("")) {
            System.err.println("Please enter valid port number!");
            return;
        }
        if (service.equals("")) {
            System.err.println("Please enter valid service name!");
            return;
        }
        if (user.equals("")) {
            System.err.println("Please enter valid username!");
            return;
        }
        if (password.equals("")) {
            System.err.println("Please enter valid password!");
            return;
        }

        saveInfo(host, port, service, user, password, connect);
    }

    public static void initialize () {
        if (window.getModality() == null) {
            window.initModality(Modality.APPLICATION_MODAL);
        }
        window.setTitle("Connection details");
        window.setWidth(350);
        window.setHeight(450);

    }

    public static void display () {

        Label label = new Label();
        label.setText("Enter data source");

        final TextField hostnameInput = new TextField();
        hostnameInput.setPromptText("Host");
        hostnameInput.setPrefColumnCount(10);
        hostnameInput.setText(MapView.DB_hostname);

        final TextField portInput = new TextField();
        portInput.setPromptText("Port");
        portInput.setPrefColumnCount(10);
        portInput.setText(MapView.DB_port);

        final TextField servicenameInput = new TextField();
        servicenameInput.setPromptText("Service name");
        servicenameInput.setPrefColumnCount(10);
        servicenameInput.setText(MapView.DB_service);

        final TextField usernameInput = new TextField();
        usernameInput.setPromptText("User");
        usernameInput.setPrefColumnCount(10);
        usernameInput.setText(MapView.DB_user);


        final TextField passwordInput = new TextField();
        passwordInput.setPromptText("Password");
        passwordInput.setPrefColumnCount(10);
        passwordInput.setText(MapView.DB_password);

        Button apply = new Button("Apply");
        apply.setOnAction( e -> {
            try {
                validate(hostnameInput.getText(), portInput.getText(), servicenameInput.getText(), usernameInput.getText(), passwordInput.getText(), false);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });

        Button applyConnect = new Button("Apply and connect");
        applyConnect.setOnAction( e -> {
            try {
                validate(hostnameInput.getText(), portInput.getText(), servicenameInput.getText(), usernameInput.getText(), passwordInput.getText(), true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });

        VBox layout = new VBox(10);
        layout.getChildren().addAll(label,hostnameInput, portInput, servicenameInput, usernameInput, passwordInput, apply, applyConnect);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();

    }
}

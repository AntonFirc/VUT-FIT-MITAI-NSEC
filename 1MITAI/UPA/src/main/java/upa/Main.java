package main.java.upa;

import javafx.application.Application;
import javafx.stage.Stage;
import main.java.upa.controllers.MapController;
import main.java.upa.model.DatabaseModel;
import main.java.upa.model.ScriptRunner;

import java.io.*;
import java.sql.SQLException;

public class Main extends Application {

    public static String DB_hostname = "gort.fit.vutbr.cz";
    public static String DB_port = "1521";
    public static String DB_service = "orclpdb";
    public static String DB_user = "xfiloj01";
    public static String DB_password = "Ws2MOPee";

    @Override
    public void start(Stage primaryStage) throws Exception{
        MapController controller = new MapController();
        primaryStage = controller.getStage();
        primaryStage.show();
    }

    public static void demoImport() throws  SQLException {
        ScriptRunner sr = new ScriptRunner(DatabaseModel.getInstance().getConnection());
        Reader reader = null;
        try {
            reader = new BufferedReader(new FileReader("demoData/demo.sql"));
        }
        catch (Exception ex) {
            System.err.println(ex);
        }
        sr.runScript(reader);
    }

    public static void main(String[] args) throws SQLException, IOException {

        /* Connection with defaults */
        DatabaseModel dbModel = new DatabaseModel();
        dbModel.connect("gort.fit.vutbr.cz", "1521", "orclpdb", "xfiloj01", "Ws2MOPee");

        launch(args);
       dbModel.disconnect();
    }
}

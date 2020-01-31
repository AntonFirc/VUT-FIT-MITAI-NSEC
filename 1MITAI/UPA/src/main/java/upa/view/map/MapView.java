package main.java.upa.view.map;

import javafx.collections.ObservableList;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.stage.Stage;
import main.java.upa.Main;
import main.java.upa.controllers.MapController;
import main.java.upa.controllers.MapController;
import main.java.upa.model.DatabaseModel;
import main.java.upa.model.land.Land;
import main.java.upa.model.land.LandModel;
import main.java.upa.model.property.Property;
import main.java.upa.model.property.PropertyModel;
import main.java.upa.view.main.DatabaseSetupView;
import oracle.spatial.geometry.JGeometry;

import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.sql.SQLException;

public class MapView {

    private Stage stage;
    private Scene scene;
    private Pane mapPane;
    private static LandModel landModel = new LandModel();
    private ObservableList<Land> lands;
    private static PropertyModel propertyModel = new PropertyModel();
    private ObservableList<Property> properties;
    private String currentAction = "select";
    private Polyline polyline;
    private Boolean polylineEnd = false;

    public static String DB_hostname = "gort.fit.vutbr.cz";
    public static String DB_port = "1521";
    public static String DB_service = "orclpdb";
    public static String DB_user = "xfiloj01";
    public static String DB_password = "Ws2MOPee";


    public MapView() throws IOException, SQLException {
        DatabaseSetupView.setController(this);
        BorderPane borderPane = new BorderPane();

        stage = new Stage();
        scene = new Scene(borderPane,1280, 720);

        stage.setTitle("Kataster - UPA 2019");
        stage.setScene(scene);

        // MENU
        //create UI menu and submenus
        MenuBar menuBar = new MenuBar();
        Menu menuDB = new Menu("Database");

        MenuItem setConnection = new MenuItem("Set connection");
        MenuItem connect = new MenuItem("Connect to database");
        MenuItem demoImport = new MenuItem("Demo import");

        demoImport.setOnAction(event -> {
            try {
                Main.demoImport();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        DatabaseSetupView.initialize();
        setConnection.setOnAction( e  -> DatabaseSetupView.display());
        connect.setOnAction( e  -> {
            DatabaseModel dbModel = new DatabaseModel();
            dbModel.connect(DB_hostname, DB_port, DB_service, DB_user, DB_password);
        });

        Menu menuOperations = new Menu("Operations");
        MenuItem selectBtn = new MenuItem("Select");
        MenuItem moveBtn = new MenuItem("Move");
        MenuItem resizeBtn = new MenuItem("Resize");

        Menu menuLand = new Menu("Land");
        MenuItem newLandBtn = new MenuItem("New Land");
        MenuItem deleteLand = new MenuItem("Delete Land");
        MenuItem deleteAllLand = new MenuItem("Delete All Lands");

        Menu menuProperty = new Menu("Property");
        MenuItem newPropertyBtn = new MenuItem("New Property");
        MenuItem deleteProperty = new MenuItem("Delete Property");
        MenuItem deleteAllProperties = new MenuItem("Delete All Properties");

        Menu menuPipe = new Menu("Pipe");
        MenuItem createPipe = new MenuItem("Create Pipe");

        Menu menuSave = new Menu("Save");
        MenuItem saveChanges = new MenuItem("Save All Changes");


        menuDB.getItems().addAll(setConnection, connect, demoImport);
        menuOperations.getItems().addAll(selectBtn,moveBtn,resizeBtn);
        menuLand.getItems().addAll(newLandBtn,deleteLand,deleteAllLand);
        menuProperty.getItems().addAll(newPropertyBtn, deleteProperty,deleteAllProperties);
        menuPipe.getItems().addAll(createPipe);
        menuSave.getItems().addAll(saveChanges);
        menuBar.getMenus().addAll(menuDB, menuLand, menuProperty, menuPipe, menuOperations, menuSave);
        Label mouseControl = new Label();


        newLandBtn.setOnAction(event -> {
            NewLandFormView.initialize();
            NewLandFormView.setController(this);
            NewLandFormView.display();
        });
        saveChanges.setOnAction(event -> {
            try {
                saveChanges();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        deleteLand.setOnAction(event -> {
            currentAction = "deleteLand";
        });
        deleteAllLand.setOnAction(event -> {
            landModel.deleteAllLands();
            try {
                loadMap();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        deleteProperty.setOnAction(event -> {
            currentAction = "deleteProperty";
        });
        deleteAllProperties.setOnAction(event -> {
            propertyModel.deleteAll();
            try {
                loadMap();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        moveBtn.setOnAction(event -> {
            currentAction = "move";
        });
        resizeBtn.setOnAction(event -> {
            currentAction = "resize";
        });
        selectBtn.setOnAction(event -> {
            currentAction = "select";
        });
        createPipe.setOnAction(event -> {
            currentAction = "pipeCreate";
        });

        newPropertyBtn.setOnAction(event -> {
            NewPropertyFormView.initialize();
            NewPropertyFormView.setController(this);
            NewPropertyFormView.display();
        });


        // MAP PANE
        mapPane = new Pane();
        mapPane.setPrefSize(3000, 3000);
        mapPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        mapPane.setStyle("-fx-background-color: white,\n" +
                "        linear-gradient(from 0.5px 0.0px to 10.5px  0.0px, repeat, darkgrey 5%, transparent 5%),\n" +
                "        linear-gradient(from 0.0px 0.5px to  0.0px 10.5px, repeat, darkgrey 5%, transparent 5%);");


        mapPane.setOnMousePressed(event -> {
            if (currentAction.equals("pipeCreate")) {
                if (!polylineEnd && polyline == null) {
                    polyline = new Polyline();

                    polyline.setStrokeWidth(3);
                    polyline.setStroke(Color.BLUE);
                    polyline.getProperties().put("pipe", "true");
                    mapPane.getChildren().add(polyline);
                }
                if (event.getButton() == MouseButton.SECONDARY) {
                    try {
                        propertyModel.createProperty(propertyModel.createPipeGroundPlan(polyline), "pipe");
                        loadMap();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    currentAction = "select";
                    polylineEnd = false;
                    polyline = null;
                }
                if (polyline != null) {
                    polyline.getPoints().addAll(event.getX(), event.getY());
                }
            }
        });

        ScrollPane scrollPane = new ScrollPane(mapPane);
        scrollPane.setPrefSize(1280, 680);
        scrollPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-focus-color: transparent;");
        scrollPane.pannableProperty().set(true);

        borderPane.setTop(menuBar);
        borderPane.setCenter(scrollPane);
        borderPane.setBottom(mouseControl);

        scrollPane.setOnMouseMoved(event -> {
            mouseControl.setText("x: " + event.getX() + " y: " + event.getY());
        });

        loadMap();
    }

    public void loadMap() throws SQLException {
        // LOAD ALL GEOMETRY FROM DB TO CANVAS
        clearCanvas();
        lands = landModel.getAllLand();
        properties = propertyModel.getAll();
        for (Land land : lands) {
            loadLandGeometryToCanvas(land);
        }
        for (Property property : properties) {
            LoadPropertyGeometryToCanvas(property);
        }
    }

    private void clearCanvas() {
        mapPane.getChildren().clear();
    }

    public void saveChanges() throws SQLException {
        for (Node landGeoNode: mapPane.getChildren()) {
            if (landGeoNode.getProperties().get("type").equals("land")) {
                Rectangle landGeoNodeTmp = new Rectangle(landGeoNode.localToParent(landGeoNode.getBoundsInParent()).getMinX(), landGeoNode.localToParent(landGeoNode.getBoundsInParent()).getMinY(), landGeoNode.getBoundsInLocal().getWidth(), landGeoNode.getBoundsInLocal().getHeight());
                landGeoNodeTmp.setId(landGeoNode.getId());
                Land land = landModel.getLandByShapeId(lands, landGeoNode.getId());
                landModel.updateShape(land, landGeoNodeTmp);
            }
        }
        for (Node propertyGeoNode: mapPane.getChildren()) {
            if (propertyGeoNode.getProperties().get("type").equals("property")) {
                if (propertyGeoNode.getProperties().containsKey("pipe"))
                {
                    Polyline polylineTmp = (Polyline) propertyGeoNode;
                    Polyline polylineOffset = new Polyline();
                    int iter = 0;
                    for (double point: polylineTmp.getPoints()) {
                        iter++;
                        if (iter == 1) {
                            polylineOffset.getPoints().add(point+polylineTmp.getLayoutX());
                        }
                        if (iter == 2) {
                            polylineOffset.getPoints().add(point+polylineTmp.getLayoutY());
                            iter = 0;
                        }
                    }
                    polylineOffset.setId(propertyGeoNode.getId());
                    Property property = propertyModel.getPropertyByShapeId(properties, propertyGeoNode.getId());
                    propertyModel.updateShape(property, polylineOffset);
                }
                else
                {
                    Rectangle propertyGeoNodeTmp = new Rectangle(propertyGeoNode.localToParent(propertyGeoNode.getBoundsInParent()).getMinX(), propertyGeoNode.localToParent(propertyGeoNode.getBoundsInParent()).getMinY(), propertyGeoNode.getBoundsInLocal().getWidth(), propertyGeoNode.getBoundsInLocal().getHeight());
                    propertyGeoNodeTmp.setId(propertyGeoNode.getId());
                    Property property = propertyModel.getPropertyByShapeId(properties, propertyGeoNode.getId());
                    propertyModel.updateShape(property, propertyGeoNodeTmp);
                }
            }
        }
    }

    private void loadLandGeometryToCanvas(Land land) {
        JGeometry geo = land.getGroundPlan();
        if (geo.createShape() != null)
        {
            Rectangle2D bounds = geo.createShape().getBounds2D();
            Rectangle rect = new Rectangle(bounds.getMinX(),bounds.getMinY(),bounds.getWidth(),bounds.getHeight());
            rect.setStrokeType(StrokeType.INSIDE);
            rect.setStyle("-fx-fill: transparent; -fx-stroke: black; -fx-stroke-width: 5;");
            rect.getProperties().put("type", "land");
            rect.setOnMouseDragged(event -> {
                if (currentAction.equals("resize"))
                {
                    rect.setCursor(Cursor.NW_RESIZE);
                    if(event.getSceneX()-rect.getBoundsInLocal().getMinX() >= 0 &&
                            event.getSceneY()-rect.getBoundsInLocal().getMinY() >= 0)
                    {
                        rect.setWidth(event.getSceneX()-rect.getBoundsInLocal().getMinX());
                        rect.setHeight(event.getSceneY()-rect.getBoundsInLocal().getMinY());
                    }
                }
                else if (currentAction.equals("move"))
                {
                    rect.setCursor(Cursor.OPEN_HAND);
                    rect.setX(event.getSceneX()-(rect.getBoundsInLocal().getWidth()/2));
                    rect.setY(event.getSceneY()-(rect.getBoundsInLocal().getHeight()/2));
                }
            });
            rect.setOnMouseClicked(event -> {
                if (currentAction.equals("select"))
                {
                    System.out.println("click");
                    try {
                        ObjectDetailView.display(land.getId(), false);
                    } catch (SQLException | IOException e) {
                        e.printStackTrace();
                    }
                }
                if (currentAction.equals("deleteLand"))
                {
                    try {
                        int landId = landModel.getLandByShapeId(lands, rect.getId()).getId();
                        landModel.deleteLand(landId);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    try {
                        loadMap();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            });

            rect.setId(Integer.toString(land.hashCode()));
            land.setShapeId(rect.getId());
            mapPane.getChildren().add(rect);
        }
    }

    private void LoadPropertyGeometryToCanvas(Property property) {
        JGeometry geo = property.getGroundPlan();
        if (geo.createShape() != null || geo.getJavaPoint() != null)
        {
            if (property.getCategory().equals("hydrant"))
            {
                Circle geoNode;
                geoNode = new Circle(geo.getJavaPoint().getX(),geo.getJavaPoint().getY(),5, Color.RED);
                geoNode.getProperties().put("type", "property");

                geoNode.setOnMouseDragged(event -> {
                    if (currentAction.equals("move"))
                    {
                        geoNode.setCursor(Cursor.OPEN_HAND);
                        geoNode.setCenterX(event.getX());
                        geoNode.setCenterY(event.getY());
                    }
                });
                geoNode.setOnMouseClicked(event -> {
                    if (currentAction.equals("select"))
                    {
                        System.out.println("click");
                        try {
                            ObjectDetailView.display(property.getId(), true);
                        } catch (SQLException | IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (currentAction.equals("deleteProperty"))
                    {
                        try {
                            int propertyId = propertyModel.getPropertyByShapeId(properties, geoNode.getId()).getId();
                            propertyModel.deleteProperty(propertyId);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        try {
                            loadMap();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                });

                geoNode.setId(Integer.toString(property.hashCode()));
                property.setShapeId(geoNode.getId());
                mapPane.getChildren().add(geoNode);
            }
            else if (property.getCategory().equals("pipe"))
            {
                double[] pipePoints = geo.getOrdinatesArray();
                Polyline polyline = new Polyline();
                int iter = 0;
                double x = 0;
                double y = 0;
                for (double point: pipePoints) {
                    iter++;
                    if (iter == 1) {
                        x = point;
                    }
                    if (iter == 2) {
                        y = point;
                        polyline.getPoints().addAll(x, y);
                        iter = 0;
                    }
                }
                polyline.setStrokeWidth(3);
                polyline.setStroke(Color.BLUE);
                polyline.getProperties().put("type", "property");
                polyline.getProperties().put("pipe", "true");
                polyline.setId(Integer.toString(property.hashCode()));
                property.setShapeId(polyline.getId());
                mapPane.getChildren().add(polyline);

                polyline.setOnMouseClicked(event -> {
                    if (currentAction.equals("deleteProperty"))
                    {
                        try {
                            int propertyId = propertyModel.getPropertyByShapeId(properties, polyline.getId()).getId();
                            propertyModel.deleteProperty(propertyId);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        try {
                            loadMap();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                });
                polyline.setOnMouseDragged(event -> {
                    if (currentAction.equals("move"))
                    {
                        polyline.setCursor(Cursor.OPEN_HAND);
                        polyline.setLayoutX(event.getSceneX()-polyline.getBoundsInLocal().getMinX()-(polyline.getBoundsInLocal().getWidth()/2));
                        polyline.setLayoutY(event.getSceneY()-polyline.getBoundsInLocal().getMinY()-(polyline.getBoundsInLocal().getHeight()/2));
                    }
                });
            }
            else
            {
                Rectangle geoNode;
                Rectangle2D bounds = geo.createShape().getBounds2D();
                geoNode = new Rectangle(bounds.getMinX(),bounds.getMinY(),bounds.getWidth(),bounds.getHeight());
                geoNode.setStrokeType(StrokeType.INSIDE);
                geoNode.setStyle("-fx-fill: transparent; -fx-stroke: green; -fx-stroke-width: 5;");

                geoNode.setOnMouseDragged(event -> {
                    if (currentAction.equals("resize"))
                    {
                        geoNode.setCursor(Cursor.NW_RESIZE);
                        if(event.getSceneX()-geoNode.getBoundsInLocal().getMinX() >= 0 &&
                                event.getSceneY()-geoNode.getBoundsInLocal().getMinY() >= 0)
                        {
                            geoNode.setWidth(event.getSceneX()-geoNode.getBoundsInLocal().getMinX());
                            geoNode.setHeight(event.getSceneY()-geoNode.getBoundsInLocal().getMinY());
                        }
                    }
                    else if (currentAction.equals("move"))
                    {
                        geoNode.setCursor(Cursor.OPEN_HAND);
                        geoNode.setX(event.getSceneX()-(geoNode.getBoundsInLocal().getWidth()/2));
                        geoNode.setY(event.getSceneY()-(geoNode.getBoundsInLocal().getHeight()/2));
                    }
                });
                geoNode.setOnMouseClicked(event -> {
                    if (currentAction.equals("select"))
                    {
                        System.out.println("click");
                        try {
                            ObjectDetailView.display(property.getId(), true);
                        } catch (SQLException | IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (currentAction.equals("deleteProperty"))
                    {
                        try {
                            int propertyId = propertyModel.getPropertyByShapeId(properties, geoNode.getId()).getId();
                            propertyModel.deleteProperty(propertyId);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        try {
                            loadMap();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                });
                geoNode.getProperties().put("type", "property");
                geoNode.setId(Integer.toString(property.hashCode()));
                property.setShapeId(geoNode.getId());
                mapPane.getChildren().add(geoNode);
            }
        }
    }

    public Stage getStage() {
        return stage;
    }
}

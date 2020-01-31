package main.java.upa.model.property;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.shape.Polyline;
import main.java.upa.model.DatabaseModel;
import main.java.upa.model.land.Land;
import main.java.upa.model.user.User;
import main.java.upa.model.user.UserModel;
import oracle.jdbc.OracleResultSet;
import oracle.spatial.geometry.JGeometry;

import java.sql.*;
import java.util.stream.Collectors;

import static java.lang.System.exit;

public class PropertyModel {

    private DatabaseModel dbModel;

    public PropertyModel() {
        this.dbModel = DatabaseModel.getInstance();
    }

    public void createProperty(JGeometry groundPlan, String category) throws SQLException {
        Connection conn = dbModel.getConnection();
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement(
                    "INSERT INTO PROPERTY(ID, PROPERTY_TYPE, GROUND_PLAN) VALUES (?,?,?)");
            Struct obj = JGeometry.storeJS(conn, groundPlan);
            pstmt.setObject(1, this.getNewID());
            pstmt.setObject(2, category);
            pstmt.setObject(3, obj);
            pstmt.executeUpdate();
            System.out.println("Property created\n");
        }
        catch (SQLException e)
        {
            System.err.println("SQLException: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pstmt.close();
        }
    }

    public int getNewID() {
        int max = 0;

        try {
            Statement st = this.dbModel.getConnection().createStatement();
            OracleResultSet set = (OracleResultSet)st.executeQuery(
                    "SELECT MAX(ID) AS MAX FROM PROPERTY"
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

    public JGeometry createRectangleGroundPlan(double minX, double minY, double maxX, double maxY)
    {
        return new JGeometry(minX, minY, maxX, maxY, 0);
    }

    public JGeometry createPointGroundPlan(double x, double y) {
        return new JGeometry(x, y, JGeometry.GTYPE_POINT);
    }

    public JGeometry createPipeGroundPlan(Polyline polyline) {
        ObservableList<Double> polyPoints = polyline.getPoints();
        int dim = polyline.getPoints().size();
        return JGeometry.createLinearLineString(polyPoints.stream().mapToDouble(d -> d).toArray(), dim, 0);
    }

    public ObservableList<Property> getAll() throws SQLException {
        Connection conn = dbModel.getConnection();
        Statement stmt = conn.createStatement();
        ObservableList<Property> properties = FXCollections.observableArrayList();
        try {
            ResultSet rset = stmt.executeQuery("select * from PROPERTY");
            while (rset.next()) {
                // convert the Struct into a JGeometry
                Struct obj = (Struct) rset.getObject(3);
                JGeometry jgeom = JGeometry.loadJS(obj);

                Property newProperty = new Property(
                        rset.getInt("id"),
                        jgeom,
                        rset.getString("PROPERTY_TYPE")
                );
                properties.add(newProperty);
            }
            rset.close();
            stmt.close();
            return properties;
        }
        catch (SQLException e)
        {
            System.err.println("SQLException: " + e.getMessage());
        }
        return properties;
    }

    public Property getPropertyByShapeId(ObservableList<Property> properties, String id) {
        for (Property propertyTmp: properties) {
            if (propertyTmp.getShapeId() != null)
            {
                if (propertyTmp.getShapeId().equals(id))
                {
                    return propertyTmp;
                }
            }
        }
        return null;
    }

    public void updateShape(Property property, Node propertyGeoNode) throws SQLException {
        Bounds propertyGeoNodeBounds = propertyGeoNode.getBoundsInLocal();
        JGeometry jgeo;
        if (property.getCategory().equals("hydrant")) {
            jgeo = this.createPointGroundPlan((propertyGeoNodeBounds.getMinX()+propertyGeoNodeBounds.getMaxX())/2,(propertyGeoNodeBounds.getMinY()+propertyGeoNodeBounds.getMaxY())/2);
        }
        else if (property.getCategory().equals("pipe")) {
            jgeo = this.createPipeGroundPlan((Polyline)propertyGeoNode);
        }
        else {
            jgeo = this.createRectangleGroundPlan(propertyGeoNodeBounds.getMinX(), propertyGeoNodeBounds.getMinY(), propertyGeoNodeBounds.getMaxX(), propertyGeoNodeBounds.getMaxY());
        }
        Connection conn = dbModel.getConnection();
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement(
                    "UPDATE PROPERTY SET GROUND_PLAN=? WHERE ID = ?");
            Struct obj = JGeometry.storeJS(conn, jgeo);
            pstmt.setObject(1, obj);
            pstmt.setObject(2, property.getId());
            pstmt.executeUpdate();
            System.out.println("Property shape updated\n");
        }
        catch (SQLException e)
        {
            System.err.println("SQLException: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pstmt.close();
        }
    }

    public void deleteAll() {
        try {

            Statement st = this.dbModel.getConnection().createStatement();
            OracleResultSet set = (OracleResultSet)st.executeQuery(
                    "DELETE FROM PROPERTY"
            );
            set.close();
            st.close();

        } catch(SQLException ex) {
            System.err.println(ex.getMessage());
            exit(-1);
        }
    }

    public void deleteProperty(int propertyId) throws SQLException {
        Connection conn = dbModel.getConnection();
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement(
                    "DELETE PROPERTY WHERE ID = ?");
            pstmt.setObject(1, propertyId);
            pstmt.executeUpdate();
            System.out.println("Property deleted\n");
        }
        catch (SQLException e)
        {
            System.err.println("SQLException: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pstmt.close();
        }
    }

    public Property getById(int id) throws SQLException {
        Connection conn = dbModel.getConnection();
        Statement stmt = conn.createStatement();
        Property newProperty = null;
        try {
            ResultSet rset = stmt.executeQuery("select * from PROPERTY WHERE id = " + id);
            if (rset.next()) {
                // convert the Struct into a JGeometry
                Struct obj = (Struct) rset.getObject(3);
                JGeometry jgeom = JGeometry.loadJS(obj);

                newProperty = new Property(
                        rset.getInt("id"),
                        jgeom,
                        rset.getString("property_type")
                );
            }
            rset.close();
            stmt.close();
            return newProperty;
        }
        catch (SQLException e)
        {
            System.err.println("SQLException: " + e.getMessage());
        }
        return null;
    }

    public User getPropertyUser(int id) {

        Property property = null;

        try {
            property = getById(id);
        } catch (SQLException e) {
            e.printStackTrace();
            exit(-1);
        }

        return property.getUser();

    }

    public double getHydrantDistance(int id) {

        Property hydrant = null;
        ObservableList<Property> properties = FXCollections.observableArrayList();

        try {
            hydrant = getById(id);
            properties = getAll();
        } catch(SQLException e) {
            e.printStackTrace();
            exit(-1);
        }

        if(!hydrant.getCategory().equals("hydrant")) {
            return 0;
        }

        double minimumDistance = Double.MAX_VALUE;

        Connection conn = dbModel.getConnection();

        try {

            for (Property property: properties) {

                if(!property.getCategory().equals("house"))
                    continue;

                Statement stmt = conn.createStatement();
                ResultSet rset = stmt.executeQuery("SELECT SDO_GEOM.SDO_CENTROID(PROPERTY.GROUND_PLAN, 0.005) as GEO\n" +
                        "FROM PROPERTY\n" +
                        "WHERE PROPERTY.id = " + property.getId());
                if (rset.next()) {
                    Struct obj = (Struct) rset.getObject("GEO");
                    JGeometry jgeom = JGeometry.loadJS(obj);

                    Statement calcSt = conn.createStatement();
                    double x1 = jgeom.getJavaPoint().getX();
                    double y1 = jgeom.getJavaPoint().getY();
                    double x2 = hydrant.getGroundPlan().getJavaPoint().getX();
                    double y2 = hydrant.getGroundPlan().getJavaPoint().getY();
                    ResultSet calcRset = calcSt.executeQuery("SELECT SDO_GEOM.SDO_DISTANCE(SDO_GEOMETRY(2001, NULL, SDO_POINT_TYPE(" + x1 + ", " + y1 + ", NULL), NULL, NULL), SDO_GEOMETRY(2001, NULL, SDO_POINT_TYPE(" + x2 + ", " + y2 + ", NULL), NULL, NULL), 0.005) as DIST FROM PROPERTY WHERE PROPERTY.ID = " + hydrant.getId());
                    if(calcRset.next()) {
                        double dist = calcRset.getDouble("DIST");
                        if(dist < minimumDistance)
                            minimumDistance = dist;
                    }

                    calcRset.close();
                    calcSt.close();
                }
                rset.close();
                stmt.close();
            }

        } catch(SQLException e) {
            e.printStackTrace();
            exit(-1);
        }

        return minimumDistance != Double.MAX_VALUE ? minimumDistance : 0;
    }

//    public double getPipeSize(int id) {
//        try {
//            return getById(id).getCategory();
//        } catch(SQLException e) {
//            e.printStackTrace();
//            exit(-1);
//        }
//
//        return 0;
//    }

    public String getPropertyCategory(int id) {
        try {
            return getById(id).getCategory();
        } catch(SQLException e) {
            e.printStackTrace();
            exit(-1);
        }

        return null;
    }
}

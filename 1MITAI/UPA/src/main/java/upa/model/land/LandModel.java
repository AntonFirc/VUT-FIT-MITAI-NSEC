package main.java.upa.model.land;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import main.java.upa.model.DatabaseModel;
import main.java.upa.model.user.User;
import main.java.upa.model.user.UserModel;
import oracle.jdbc.OracleResultSet;
import oracle.spatial.geometry.JGeometry;

import java.sql.*;
import java.util.Collection;
import java.util.Properties;

import static java.lang.System.exit;

public class LandModel {
    private DatabaseModel dbModel;

    public LandModel() {
        this.dbModel = DatabaseModel.getInstance();
    }

    public void createLand(JGeometry groundPlan, Integer user, String category) throws SQLException
    {
        Connection conn = dbModel.getConnection();
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement(
                    "INSERT INTO LAND(ID, GROUND_PLAN, SYS_USER, CATEGORY) VALUES (?,?,?,?)");
            Struct obj = JGeometry.storeJS(conn, groundPlan);
            pstmt.setObject(1, this.getNewID());
            pstmt.setObject(2, obj);
            pstmt.setObject(3, user);
            pstmt.setObject(4, category);
            pstmt.executeUpdate();
            System.out.println("Land created\n");
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

    public Land getById(int id) throws SQLException {
        Connection conn = dbModel.getConnection();
        Statement stmt = conn.createStatement();
        Land newLand = null;
        try {
            ResultSet rset = stmt.executeQuery("select * from LAND WHERE id = " + id);
            if (rset.next()) {
                // convert the Struct into a JGeometry
                Struct obj = (Struct) rset.getObject(2);
                JGeometry jgeom = JGeometry.loadJS(obj);

                UserModel user = new UserModel();

                newLand = new Land(
                        rset.getInt("id"),
                        jgeom,
                        rset.getString("category"),
                        user.getById(rset.getInt("sys_user"))
                );
            }
            rset.close();
            stmt.close();
            return newLand;
        }
        catch (SQLException e)
        {
            System.err.println("SQLException: " + e.getMessage());
        }
        return null;
    }

    public Land getLandByShapeId(ObservableList<Land> lands, String shapeId) {
        for (Land landTmp: lands) {
            if (landTmp.getShapeId() != null)
            {
                if (landTmp.getShapeId().equals(shapeId))
                {
                    return landTmp;
                }
            }
        }
        return null;
    }

    public ObservableList<Land> getAllLand() throws SQLException {
        Connection conn = dbModel.getConnection();
        Statement stmt = conn.createStatement();
        ObservableList<Land> lands = FXCollections.observableArrayList();
        try {
            ResultSet rset = stmt.executeQuery("select * from LAND");
            while (rset.next()) {
                // convert the Struct into a JGeometry
                Struct obj = (Struct) rset.getObject(2);
                JGeometry jgeom = JGeometry.loadJS(obj);

                Land newLand = new Land(
                        rset.getInt("id"),
                        jgeom,
                        rset.getString("category"),
                        null // TODO ako riesit FK USER ?
                );
                lands.add(newLand);
            }
            rset.close();
            stmt.close();
            return lands;
        }
        catch (SQLException e)
        {
            System.err.println("SQLException: " + e.getMessage());
        }
        return lands;
    }

    public int getNewID() {
        int max = 0;

        try {

            Statement st = this.dbModel.getConnection().createStatement();
            OracleResultSet set = (OracleResultSet)st.executeQuery(
                    "SELECT MAX(ID) AS MAX FROM LAND"
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

    public void updateShape(Land land, Node landGeoNode) throws SQLException {
        Bounds landGeoNodeBounds = landGeoNode.getBoundsInLocal();
        JGeometry jgeo = this.createRectangleGroundPlan(landGeoNodeBounds.getMinX(), landGeoNodeBounds.getMinY(), landGeoNodeBounds.getMaxX(), landGeoNodeBounds.getMaxY());
        Connection conn = dbModel.getConnection();
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement(
                    "UPDATE LAND SET GROUND_PLAN=? WHERE ID = ?");
            Struct obj = JGeometry.storeJS(conn, jgeo);
            pstmt.setObject(1, obj);
            pstmt.setObject(2, land.getId());
            pstmt.executeUpdate();
            System.out.println("Land shape updated\n");
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

    public void deleteAllLands() {
        try {

            Statement st = this.dbModel.getConnection().createStatement();
            OracleResultSet set = (OracleResultSet)st.executeQuery(
                    "DELETE FROM LAND"
            );
            set.close();
            st.close();

        } catch(SQLException ex) {
            System.err.println(ex.getMessage());
            exit(-1);
        }
    }

    public void deleteLand(int id) throws SQLException {
        Connection conn = dbModel.getConnection();
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement(
                    "DELETE LAND WHERE ID = ?");
            pstmt.setObject(1, id);
            pstmt.executeUpdate();
            System.out.println("Land deleted\n");
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

    public double getLandArea(int id) {

        Land land = null;

        try {
            land = getById(id);
        } catch (SQLException e) {
            e.printStackTrace();
            exit(-1);
        }

        return land.getArea();
    }

    public int countProperties(int id) {

        Connection conn = dbModel.getConnection();
        Statement stmt = null;
        int ret = 0;
        try {
            stmt = conn.createStatement();
            ResultSet rset = stmt.executeQuery("SELECT COUNT(PROPERTY.ID) FROM LAND, PROPERTY WHERE LAND.id = " + id + " AND (PROPERTY.PROPERTY_TYPE = 'house' OR PROPERTY.PROPERTY_TYPE = 'flat') AND SDO_FILTER(LAND.GROUND_PLAN, PROPERTY.GROUND_PLAN) = 'TRUE'");
            if (rset.next()) {
                ret = rset.getInt(1);
            }

            rset.close();
            stmt.close();
        } catch (SQLException e) {
            System.err.println("SQLException: " + e.getMessage());
            exit(-1);
        }
        return ret;
    }

    public int nearestLand(int id) {

        Connection conn = dbModel.getConnection();
        Statement stmt = null;
        int ret = 0;
        try {
            stmt = conn.createStatement();
            ResultSet rset = stmt.executeQuery("SELECT c.ID, sdo_nn_distance (1) distance FROM LAND i, LAND c WHERE i.ID = " + id + " AND sdo_nn(c.GROUND_PLAN, i.GROUND_PLAN, 'sdo_batch_size=10', 1) = 'TRUE' ORDER BY distance");
            rset.next();
            if (rset.next()) {
                ret = rset.getInt("distance");
            }

            rset.close();
            stmt.close();
        } catch (SQLException e) {
            System.err.println("SQLException: " + e.getMessage());
            exit(-1);
        }
        return ret;
    }
}

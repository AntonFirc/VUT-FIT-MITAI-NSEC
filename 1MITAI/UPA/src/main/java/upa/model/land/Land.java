package main.java.upa.model.land;

import main.java.upa.model.DatabaseModel;
import main.java.upa.model.user.User;
import oracle.jdbc.OracleResultSet;
import oracle.spatial.geometry.JGeometry;

import java.sql.SQLException;
import java.sql.Statement;

import static java.lang.System.exit;

public class Land {

    private int id;
    private JGeometry groundPlan;
    private String category;
    private User user;
    private String shapeId;

    private DatabaseModel dbModel;

    public Land(int id, JGeometry groundPlan, String category, User user) {
        this.id = id;
        this.groundPlan = groundPlan;
        this.category = category;
        this.user = user;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public JGeometry getGroundPlan() {
        return groundPlan;
    }

    public void setGroundPlan(JGeometry groundPlan) {
        this.groundPlan = groundPlan;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getShapeId() { return shapeId; }

    public void setShapeId(String shapeId) { this.shapeId = shapeId; }

    public double getArea() {

        double areaSize = 0;

        try {

            Statement st = DatabaseModel.getInstance().getConnection().createStatement();
            OracleResultSet set = (OracleResultSet)st.executeQuery(
                    "SELECT SDO_GEOM.SDO_AREA(GROUND_PLAN, 0.005) FROM LAND WHERE ID=" + this.id
            );

            if(set.next()) {
                areaSize = set.getDouble(1);
            }

            set.close();
            st.close();

        } catch(SQLException ex) {
            System.err.println(ex.getMessage());
            exit(-1);
        }

        return areaSize;

    }
}

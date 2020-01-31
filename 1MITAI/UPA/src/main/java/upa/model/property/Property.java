package main.java.upa.model.property;

import main.java.upa.model.DatabaseModel;
import main.java.upa.model.land.Land;
import main.java.upa.model.land.LandModel;
import main.java.upa.model.user.User;
import oracle.jdbc.OracleResultSet;
import oracle.spatial.geometry.JGeometry;

import java.sql.SQLException;
import java.sql.Statement;

import static java.lang.System.exit;

public class Property {

    private int id;
    private JGeometry groundPlan;
    private String category;

    private String shapeId;

    private DatabaseModel dbModel;

    public Property(int id, JGeometry groundPlan, String category) {
        this.id = id;
        this.groundPlan = groundPlan;
        this.category = category;
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

    public String getShapeId() {
        return shapeId;
    }

    public void setShapeId(String shapeId) {
        this.shapeId = shapeId;
    }

    public User getUser() {

        User user = null;

        try {

            Statement st = DatabaseModel.getInstance().getConnection().createStatement();
            OracleResultSet set = (OracleResultSet)st.executeQuery(
                    "SELECT LAND.ID, SDO_GEOM.RELATE(LAND.GROUND_PLAN, 'determine', PROPERTY.GROUND_PLAN) as rel " +
                            "FROM LAND, PROPERTY WHERE PROPERTY.PROPERTY_TYPE NOT IN ('hydrant', 'pipe')"
            );

            Land land = null;

            while(set.next()) {
                if(!set.getString("REL").equals("DISJOINT") && !set.getString("REL").equals("TOUCH")) {
                    LandModel landModel = new LandModel();
                    land = landModel.getById(set.getInt("ID"));

                    return land.getUser();
                }
            }

            set.close();
            st.close();

            return null;

        } catch(SQLException ex) {
            System.err.println(ex.getMessage());
            exit(-1);
        }

        return null;

    }
}

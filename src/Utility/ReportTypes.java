package Utility;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.Month;

//Used in the generation of the 3 reports available on the Reports screen (Appointment Type Summary,Current User Schedule, and a Customer Phone Tree
public class ReportTypes {

    private String appointmentType;

    private Integer numOfAppointments;

    private static final ObservableList<String> reportChoices = FXCollections.observableArrayList("Monthly Appointment Type Summary", "Your Schedule", "Customer Phone Tree");
    private static final ObservableList<ReportTypes> allReportTypes = FXCollections.observableArrayList();

    public ReportTypes(String typeString, Integer typeInt) {
        this.appointmentType = typeString;
        this.numOfAppointments = typeInt;
    }

    public String getAppointmentType() {
        return appointmentType;
    }

    public void setAppointmentType(String type) {
        this.appointmentType = type;
    }

    public Integer getNumOfAppointments() {
        return numOfAppointments;
    }

    public void setNumOfAppointments(int numOfAppointments) {
        this.numOfAppointments = numOfAppointments;
    }

    public static ObservableList<String> getReportChoices() {
        return reportChoices;
    }

    public static ObservableList<ReportTypes> getAllMonthlyReportTypes() {

        allReportTypes.clear();

        Connection connection = DatabaseConnection.getConnection();
        String dataSelect = "SELECT type, COUNT(*) FROM appointment WHERE MONTHNAME(start) = ? GROUP BY type";
        Month thisMonth = LocalDateTime.now().getMonth();

        SQLQuery.setPreparedStatement(connection, dataSelect);
        PreparedStatement ps = SQLQuery.getPreparedStatement();

        try {
            ps.setString(1, String.valueOf(thisMonth));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String type = rs.getString(1);
                Integer numOfEachType = rs.getInt(2);
                ReportTypes newReport = new ReportTypes(type, numOfEachType);
                allReportTypes.add(newReport);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return allReportTypes;
    }

}

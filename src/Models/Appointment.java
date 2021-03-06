package Models;

import Utility.DatabaseConnection;
import Utility.SQLQuery;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;

public class Appointment {

    private int appointmentId;
    private int customerId;
    private int userId;
    private String type;
    private ZonedDateTime start;
    private ZonedDateTime end;

    private static final ZoneId databaseZone = ZoneId.of("UTC");

    //This ObservableList holds the different types of consultation meetings available from the company. Can edit types here.
    private static final ObservableList<String> typeList = FXCollections.observableArrayList("Scrum", "Presentation", "Project Kickoff", "Review", "Employee Evaluation");

    //This ObservableList holds the selection options for viewing Calendar by current Month or Week, or viewing all Appointments.
    private static final ObservableList<String> viewApptsBy = FXCollections.observableArrayList("Monthly", "Weekly", "All");

    //This ObservableList holds the appointment time slots generated by the getTimeSlots method (Line: ).
    private static final ObservableList<LocalTime> timeSlots = FXCollections.observableArrayList();

    //This ObservableList holds all appointments currently in the database. It is populated by the static method getAllAppointments (Line: ).
    private static final ObservableList<Appointment> allAppointments = FXCollections.observableArrayList();

    /*
    Currently the program only has one active user, but multi-user functionality is available. This ObservableList
    holds the current user's schedule and is populated by the getUserSchedule method (Line: )
     */
    private static final ObservableList<Appointment> userSchedule = FXCollections.observableArrayList();

    //Constructor
    public Appointment(int appointmentId, int customerId, String type, ZonedDateTime start, ZonedDateTime end) {

        this.appointmentId = appointmentId;
        this.customerId = customerId;
        this.type = type;
        this.start = start;
        this.end = end;

    }


    public int getAppointmentId(int customerId, ZonedDateTime start) {

        Connection connection = DatabaseConnection.getConnection();
        String appointmentIdSelect = "SELECT appointmentId FROM appointment WHERE customerId = ? and start = ?";


        DateTimeFormatter newFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String startString = start.withZoneSameInstant(databaseZone).toLocalDateTime().format(newFormat);

        SQLQuery.setPreparedStatement(connection, appointmentIdSelect);
        PreparedStatement ps = SQLQuery.getPreparedStatement();

        try {
            ps.setInt(1, customerId);
            ps.setTimestamp(2, Timestamp.valueOf(startString));
            ResultSet rs = ps.executeQuery();
            rs.next();
            appointmentId = rs.getInt(1);
            System.out.println(appointmentId);
        } catch (SQLException e) {
             System.out.println(e.getMessage());
        }

        return appointmentId;
    }

    //Getters and Setters (Some of which are not utilized at this point)
    public int getAppointmentId() {
        return appointmentId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ZonedDateTime getStart() {
        return start;
    }

    public void setStart(ZonedDateTime start) {
        this.start = start;
    }

    public ZonedDateTime getEnd() {
        return end;
    }

    public void setEnd(ZonedDateTime end) {
        this.end = end;
    }

    //Get all Appointments in database currently
    public static ObservableList<Appointment> getAllAppointments() {

        //Clear list at beginning before retrieving new updated list
        allAppointments.clear();

        Connection connection = DatabaseConnection.getConnection();
        String selectStatement = "SELECT appointmentId, customerId, type, start, end FROM appointment ORDER BY appointmentId";
        ResultSet rs;

        ZoneId userZone = ZoneId.systemDefault();

        SQLQuery.setPreparedStatement(connection, selectStatement);
        PreparedStatement ps = SQLQuery.getPreparedStatement();

        try {
            rs = ps.executeQuery();
            while (rs.next()) {
                Appointment appointment = new Appointment(
                        rs.getInt(1),
                        rs.getInt(2),
                        rs.getString(3),
                        rs.getTimestamp(4).toLocalDateTime().atZone(databaseZone).withZoneSameInstant(userZone),
                        rs.getTimestamp(5).toLocalDateTime().atZone(databaseZone).withZoneSameInstant(userZone)
                );
                allAppointments.add(appointment);
            }
            return allAppointments;

        } catch (SQLException e) {

           System.out.println(e.getMessage());
        }
        return null;
    }

    //Next 3 methods are for adding (INSERT), updating (UPDATE), and deleting (DELETE) queries to database
    public static void addAppointment(String customerName, String type, ZonedDateTime start, ZonedDateTime end) {

        Timestamp createDate = Timestamp.valueOf(LocalDateTime.now(ZoneId.of("UTC")));

        Connection connection = DatabaseConnection.getConnection();

        String appointmentInsert = "INSERT INTO appointment (customerId, userId, title, description, location, contact, type, url, start, end, createDate, createdBy, lastUpdateBy) " +
                "VALUES (?, 1, 'not needed', 'not needed', 'not needed', 'not needed', ?, 'not needed', ?, ?, ?, 'test', 'test');";
        String customerIdSelect = "SELECT customerId FROM customer WHERE customerName = ?";

        int customerId = 0;
        LocalDateTime localStart = start.withZoneSameInstant(databaseZone).toLocalDateTime();
        LocalDateTime localEnd = end.withZoneSameInstant(databaseZone).toLocalDateTime();

        System.out.println(localStart);

        SQLQuery.setPreparedStatement(connection, customerIdSelect);
        PreparedStatement ps = SQLQuery.getPreparedStatement();

        try {
            ps.setString(1, customerName);
            ResultSet rs = ps.executeQuery();
            rs.next();
            customerId = rs.getInt(1);
        } catch (SQLException e) {
            e.getMessage();
        }

        SQLQuery.setPreparedStatement(connection, appointmentInsert);
        ps = SQLQuery.getPreparedStatement();

        try {
            ps.setInt(1, customerId);
            ps.setString(2, type);
            ps.setTimestamp(3, Timestamp.valueOf(localStart));
            ps.setTimestamp(4, Timestamp.valueOf(localEnd));
            ps.setTimestamp(5, createDate);

            ps.executeUpdate();
        } catch (SQLException e) {
            e.getMessage();
        }

    }

    public static void updateAppointment(int appointmentId, String customerName, String type, ZonedDateTime start, ZonedDateTime end) {

        Connection connection = DatabaseConnection.getConnection();
        String appointmentUpdate = "UPDATE appointment SET customerId = ?, type = ?, start = ?, end = ? WHERE appointmentId = ?";
        String customerIdSelect = "SELECT customerId FROM customer WHERE customerName = ?";

        int customerId = 0;

        LocalDateTime localStart = start.withZoneSameInstant(databaseZone).toLocalDateTime();
        LocalDateTime localEnd = end.withZoneSameInstant(databaseZone).toLocalDateTime();

        SQLQuery.setPreparedStatement(connection, customerIdSelect);
        PreparedStatement ps = SQLQuery.getPreparedStatement();

        try {
            ps.setString(1, customerName);
            ResultSet rs = ps.executeQuery();
            rs.next();
            customerId = rs.getInt(1);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        SQLQuery.setPreparedStatement(connection, appointmentUpdate);
        ps = SQLQuery.getPreparedStatement();

        try {
            ps.setInt(1, customerId);
            ps.setString(2, type);
            ps.setTimestamp(3, Timestamp.valueOf(localStart));
            ps.setTimestamp(4, Timestamp.valueOf(localEnd));
            ps.setInt(5, appointmentId);

            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void deleteAppointment(int appointmentId) {

        Connection connection = DatabaseConnection.getConnection();
        String appointmentDelete = "DELETE FROM appointment WHERE appointmentId = ?";

        SQLQuery.setPreparedStatement(connection, appointmentDelete);
        PreparedStatement ps = SQLQuery.getPreparedStatement();

        try {
            ps.setInt(1, appointmentId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.getMessage();
        }
    }

    //Method creates 30 minute long appointment time slots, and adds them to an ObservableList
    public static ObservableList<LocalTime> getTimeSlots() {

        timeSlots.clear();

        //Business Hours in UTC time
        ZonedDateTime openingTime = ZonedDateTime.of(LocalDate.now(), LocalTime.of(0, 0, 0), ZoneId.of("UTC"));
        ZonedDateTime closingTime = ZonedDateTime.of(LocalDate.now(), LocalTime.of(23, 0, 0), ZoneId.of("UTC"));

        //Time Slots are in 30 minute intervals which are initialized below
        long numTimeSlots = Duration.between(openingTime, closingTime).toHours() * 2;

        ZoneId userZone = ZoneId.systemDefault();
        LocalTime openingZoned = openingTime.withZoneSameInstant(userZone).toLocalTime();
        for (int i = 0; i < numTimeSlots; i++) {

            timeSlots.add(openingZoned);

            openingZoned = openingZoned.plusMinutes(30);

        }
        return timeSlots;
    }

    public static ObservableList<String> getTypeList() {

        return typeList;
    }

    public static ObservableList<String> getViewApptsBy() {
        return viewApptsBy;
    }

    //Method retrieves currentUser userId and performs database query for that users appointments. Then stores those in an ObservableList
    public static ObservableList<Appointment> getUserSchedule() {

        userSchedule.clear();

        Connection connection = DatabaseConnection.getConnection();

        String appointmentSelect = "SELECT appointmentId, customerId, type, start, end FROM appointment WHERE userId = ?";

        User current = User.getCurrentUser();
        String currentUserName = current.getUsername();
        int userId = User.getUserId(currentUserName);

        SQLQuery.setPreparedStatement(connection, appointmentSelect);
        PreparedStatement ps = SQLQuery.getPreparedStatement();

        try {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Appointment appointment = new Appointment(rs.getInt(1),
                                                          rs.getInt(2),
                                                          rs.getString(3),
                                                          rs.getTimestamp(4).toLocalDateTime().atZone(databaseZone).withZoneSameInstant(ZoneId.systemDefault()),
                                                          rs.getTimestamp(5).toLocalDateTime().atZone(databaseZone).withZoneSameInstant(ZoneId.systemDefault())
                );
                userSchedule.add(appointment);
            }
        } catch (SQLException e) {

            System.out.println(e.getMessage());
        }
        return userSchedule;
    }

    //Method checks on Appointment update if customer name belongs to an existing customer
    public static boolean isCustomerNameInvalid(String customerName) {

        Connection connection = DatabaseConnection.getConnection();
        String checkSelect = "SELECT customerName FROM customer WHERE customerName = ?";
        boolean success = false;

        SQLQuery.setPreparedStatement(connection, checkSelect);
        PreparedStatement ps = SQLQuery.getPreparedStatement();

        try {
            ps.setString(1, customerName);
            ResultSet rs = ps.executeQuery();
            success = rs.next();
        }catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return !success;
    }

    //Method provides alert on login if user has an appointment within the next 15 minutes
    public static Appointment get15MinutesAlert() {

        Connection connection = DatabaseConnection.getConnection();
        String appointmentsSelect = "SELECT * FROM appointment WHERE start BETWEEN ? AND ? AND userId = ?";

        LocalDateTime now = LocalDateTime.now().atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
        LocalDateTime nowPlus15 = now.plusMinutes(15);

        Appointment appointment;
        int userId = User.getUserId(User.getCurrentUser().getUsername());

        SQLQuery.setPreparedStatement(connection, appointmentsSelect);
        PreparedStatement ps = SQLQuery.getPreparedStatement();

        try {
            ps.setTimestamp(1, Timestamp.valueOf(now));
            ps.setTimestamp(2, Timestamp.valueOf(nowPlus15));
            ps.setInt(3, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                appointment = new Appointment(rs.getInt("appointmentId"),
                                              rs.getInt("customerId"),
                                              rs.getString("type"),
                                              rs.getTimestamp("start").toLocalDateTime().atZone(ZoneId.of("UTC")).withZoneSameInstant(ZoneId.systemDefault()),
                                              rs.getTimestamp("end").toLocalDateTime().atZone(ZoneId.of("UTC")).withZoneSameInstant(ZoneId.systemDefault())
                );
                return appointment;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    //Method checks on save if date and time are conflicting with the USER's existing schedule
    public static boolean isAppointmentConflicting(LocalDate intendedDate, LocalTime intendedTime) {

        ObservableList<Appointment> userApptSchedule = Appointment.getUserSchedule();
        boolean conflicting = false;

        for (Appointment appointment : userApptSchedule) {
            if (appointment.getStart().toLocalDate().equals(intendedDate)) {
                System.out.println(appointment.getStart().toLocalDateTime());
                if (appointment.getStart().toLocalTime().equals(intendedTime)) {
                    conflicting = true;
                }
            }
        }
        return conflicting;
    }

}

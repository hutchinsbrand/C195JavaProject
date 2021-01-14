package View_Controller;

import Models.Appointment;
import Utility.Alerts;
import Utility.DatabaseConnection;
import Utility.SQLQuery;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Callback;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.*;
import java.util.ResourceBundle;

public class UpdateAppointmentScreenController implements Initializable {

    @FXML
    private TextField firstNameTxt;

    @FXML
    private TextField lastNameTxt;

    @FXML
    private ChoiceBox<String> typeChoice;

    @FXML
    private DatePicker datePickerField;

    @FXML
    private ComboBox<LocalTime> timeSlotChoice;

    @FXML
    private Button saveBtn;

    @FXML
    private Button cancelBtn;

    private int appointmentId;

    @FXML
    void onActionCancel() throws IOException {

        Stage stage = new Stage();
        Stage oldStage = (Stage) cancelBtn.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("HomePageScreen.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        oldStage.close();
        stage.show();

    }

    @FXML
    void onActionSave() throws IOException, NullPointerException {


        String customerName = "";
        String type = "";
        LocalDate startDate = LocalDate.now().minusDays(1);
        LocalTime startTime = LocalTime.now();
        ZonedDateTime start = ZonedDateTime.now();
        ZonedDateTime end = ZonedDateTime.now();

        //***********LAMBDA********
        Alerts appointmentAlerts = (String alertTxt) -> {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(alertTxt);
            alert.setHeaderText(null);
            alert.setContentText("Please enter a valid " + alertTxt);
            alert.showAndWait();

        };

        if (firstNameTxt.getText().isEmpty() || lastNameTxt.getText().isEmpty()) {

            appointmentAlerts.generateAlert("First and Last Name");

        } else {

            customerName = firstNameTxt.getText() + "" + lastNameTxt.getText();

        }
        if (typeChoice.getValue() == null) {

            appointmentAlerts.generateAlert("Type Choice");

        } else {

            type = typeChoice.getValue();
        }
        if (datePickerField.getValue() == null) {

            appointmentAlerts.generateAlert("Date");

        } else {

            startDate = datePickerField.getValue();
        }
        if (timeSlotChoice.getValue() == null || (datePickerField.getValue().equals(LocalDate.now()) & timeSlotChoice.getValue().isBefore(LocalTime.now()))) {

            appointmentAlerts.generateAlert("Time Slot");

        } else {

            startTime = timeSlotChoice.getValue();
        }
        if(Appointment.isAppointmentConflicting(startDate, startTime)) {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Scheduling Conflict");
            alert.setHeaderText(null);
            alert.setContentText("This time slot is unavailable. Please select another time, or a different date.");
            alert.showAndWait();

        } else {

            start = LocalDateTime.of(startDate, startTime).atZone(ZoneId.systemDefault());
            end = start.plusMinutes(30);
        }
        if (customerName.isEmpty() || type.isEmpty() || start.isBefore(ZonedDateTime.now())) {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Adding Appointment");
            alert.setHeaderText(null);
            alert.setContentText("An error has occurred adding the appointment data. Please check all fields and try again.");
            alert.showAndWait();

        } else {

            if (Appointment.isCustomerNameInvalid(customerName)) {

                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Name Doesn't Match Records");
                alert.setHeaderText(null);
                alert.setContentText("The customer name entered doesn't match records. Please enter new Customer or check for errors.");
                alert.showAndWait();

            } else {

                Appointment.updateAppointment(appointmentId, customerName, type, start, end);

                Stage stage = new Stage();
                Stage oldStage = (Stage) saveBtn.getScene().getWindow();
                Parent root = FXMLLoader.load(getClass().getResource("HomePageScreen.fxml"));
                Scene scene = new Scene(root);
                stage.setScene(scene);
                oldStage.close();
                stage.show();
            }
        }
    }

    //Method imports selected appointment data to populate fields
    public void importAppointmentData(Appointment appointment) {

        Connection connection = DatabaseConnection.getConnection();
        String customerSelect = "SELECT customerName FROM customer WHERE customerId = ?";
        int customerId = appointment.getCustomerId();
        String customerName = null;

        SQLQuery.setPreparedStatement(connection, customerSelect);
        PreparedStatement ps = SQLQuery.getPreparedStatement();

        try {
            ps.setInt(1, customerId);
            ResultSet rs = ps.executeQuery();
            rs.next();
            customerName = rs.getString(1);
        } catch (SQLException e) {
            e.getMessage();
        }

        assert customerName != null;
        int spaceLocation = customerName.lastIndexOf(' ');
        int nameSize = customerName.length();

        if (spaceLocation == -1)
            throw new IllegalArgumentException ("Customer only has one name");

        String firstName = customerName.substring(0, spaceLocation);
        String lastName = customerName.substring(spaceLocation, nameSize);

        firstNameTxt.setText(firstName);
        lastNameTxt.setText(lastName);
        typeChoice.setValue(appointment.getType());
        datePickerField.setValue(appointment.getStart().toLocalDate());
        timeSlotChoice.setValue(appointment.getStart().toLocalTime());

        appointmentId = appointment.getAppointmentId();


    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        LocalDate today = LocalDate.now();

        datePickerField.setDayCellFactory(new Callback<DatePicker, DateCell>() {
            @Override
            public DateCell call(DatePicker param) {
                return new DateCell() {
                    @Override
                    public void updateItem (LocalDate item, boolean empty) {
                        super.updateItem(item, empty);
                        setDisable(empty || item.getDayOfWeek() == DayOfWeek.SATURDAY ||
                                item.getDayOfWeek() == DayOfWeek.SUNDAY || item.isBefore(today));
                    }
                };
            }
        });

        typeChoice.setItems(Appointment.getTypeList());
        timeSlotChoice.setItems(Appointment.getTimeSlots());

    }
}

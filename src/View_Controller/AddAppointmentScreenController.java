package View_Controller;

import Models.Appointment;
import Utility.Alerts;
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
import java.time.*;
import java.util.ResourceBundle;

public class AddAppointmentScreenController implements Initializable {

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
    void onActionSave() throws IOException {

        String customerName = "";
        String type = "";
        LocalDate startDate = LocalDate.now().minusDays(1);
        LocalTime startTime = LocalTime.now();
        ZonedDateTime start = ZonedDateTime.now();
        ZonedDateTime end = ZonedDateTime.now();

        /*
        This lambda implements the Alerts interface to create an alert dialog for each field of both appointment and customer screens.
        Creating this saved easily 100 lines of code which will improve read time and make program more efficient.
         */
        Alerts appointmentAlerts = (String alertTxt) -> {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(alertTxt);
            alert.setHeaderText(null);
            alert.setContentText("Please check that the " + alertTxt + " is filled in correctly.");
            alert.showAndWait();

        };

        if (firstNameTxt.getText().isEmpty() || lastNameTxt.getText().isEmpty()) {

            appointmentAlerts.generateAlert("First and Last Name Fields");

        } else {

            customerName = firstNameTxt.getText() + " " + lastNameTxt.getText();
        }
        if (typeChoice.getValue() == null) {

            appointmentAlerts.generateAlert("Type Field");

        } else {

            type = typeChoice.getValue();
        }
        if (datePickerField.getValue() == null) {

            appointmentAlerts.generateAlert("Date Picker");

        } else {

            startDate = datePickerField.getValue();
        }
        if (timeSlotChoice.getValue() == null || (datePickerField.getValue().equals(LocalDate.now()) & timeSlotChoice.getValue().isBefore(LocalTime.now()))) {

            appointmentAlerts.generateAlert("Time Slot Field");

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
        if (customerName.isEmpty() || type.isEmpty() || start.isBefore(LocalDateTime.now().atZone(ZoneId.systemDefault()))) {

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

                Appointment.addAppointment(customerName, type, start, end);

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        /*
        This variable is used in the Lambda below. This was the best method I found to format my DatePicker cells.
        Any other way I could think of would have taken up many more lines of code, or created a method to only be used twice.
         */
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

        //Populates Choice and Combo boxes with Types and TimeSlots from the Appointment class.
        typeChoice.setItems(Appointment.getTypeList());
        timeSlotChoice.setItems(Appointment.getTimeSlots());


    }
}

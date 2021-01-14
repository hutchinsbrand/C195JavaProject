package View_Controller;

import Models.Appointment;
import Models.Customer;
import Utility.Alerts;
import Utility.AppointmentReports;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.Locale;
import java.util.ResourceBundle;

public class HomePageScreenController implements Initializable {

    @FXML
    private TableView<Customer> customerTableView;

    @FXML
    private TableColumn<Customer, Integer> customerIdCol;

    @FXML
    private TableColumn<Customer, String> customerNameCol;

    @FXML
    private TableColumn<Customer, Integer> addressIdCol;

    @FXML
    private TableColumn<Customer, String> customerAddressCol;

    @FXML
    private TableColumn<Customer, String> customerPhoneCol;

    @FXML
    private ComboBox<String> calendarByComboBox;

    @FXML
    private TableView<Appointment> apptTableView;

    @FXML
    private TableColumn<Appointment, Integer> apptIdCol;

    @FXML
    private TableColumn<Appointment, Integer> apptCustIdCol;

    @FXML
    private TableColumn<Appointment, String> apptTypeCol;

    @FXML
    private TableColumn<Appointment, LocalDateTime> apptStartCol;

    @FXML
    private TableColumn<Appointment, LocalDateTime> apptEndCol;

    @FXML
    private Button addCustomerBtn;

    @FXML
    private Button updateCustomerBtn;

    @FXML
    private Button viewReportsBtn;

    @FXML
    private Button addApptBtn;

    @FXML
    private Button updateApptBtn;

    @FXML
    void onActionAddAppt() throws IOException {

        Stage stage = new Stage();
        Stage oldStage = (Stage) addApptBtn.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("AddAppointmentScreen.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        oldStage.close();
        stage.show();


    }

    @FXML
    void onActionAddCustomer() throws IOException {

        Stage stage = new Stage();
        Stage oldStage = (Stage) addCustomerBtn.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("AddCustomerScreen.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        oldStage.close();
        stage.show();

    }

    @FXML
    void onActionDeleteAppt() {

        Appointment selectedAppointment = apptTableView.getSelectionModel().getSelectedItem();

        int selectedAppointmentId = selectedAppointment.getAppointmentId(selectedAppointment.getCustomerId(), selectedAppointment.getStart());

        Appointment.deleteAppointment(selectedAppointmentId);
        apptTableView.setItems(Appointment.getAllAppointments());

    }

    @FXML
    void onActionDeleteCustomer() {

        Customer selectedCustomer = customerTableView.getSelectionModel().getSelectedItem();
        int selectedCustomerId = selectedCustomer.getCustomerId();
        int selectedAddressId = selectedCustomer.getAddressId();

        ObservableList<Appointment> allAppointments = Appointment.getAllAppointments();

        for (Appointment appointment : allAppointments) {

            if (appointment.getCustomerId() == selectedCustomerId) {

                Appointment.deleteAppointment(appointment.getAppointmentId(selectedCustomerId, appointment.getStart()));

            }
        }

        Customer.getAllCustomers().remove(selectedCustomer);
        Customer.deleteCustomer(selectedCustomerId, selectedAddressId);

        customerTableView.setItems(Customer.getAllCustomers());
        apptTableView.setItems(Appointment.getAllAppointments());

    }

    @FXML
    void onActionUpdateAppt() throws IOException {

        Appointment appointment = apptTableView.getSelectionModel().getSelectedItem();

        Stage stage = new Stage();
        Stage oldStage = (Stage) updateApptBtn.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("UpdateAppointmentScreen.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        UpdateAppointmentScreenController controller = loader.getController();
        controller.importAppointmentData(appointment);
        stage.setScene(scene);
        oldStage.close();
        stage.show();

    }

    @FXML
    void onActionUpdateCustomer() throws IOException {

        Customer customer = customerTableView.getSelectionModel().getSelectedItem();

        Stage stage = new Stage();
        Stage oldStage = (Stage) updateCustomerBtn.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("UpdateCustomerScreen.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        UpdateCustomerScreenController controller = loader.getController();
        controller.importCustomerData(customer);
        stage.setScene(scene);
        oldStage.close();
        stage.show();

    }

    @FXML
    void onActionViewReports() throws IOException {

        Stage stage = new Stage();
        Stage oldStage = (Stage) viewReportsBtn.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("ViewReportsScreen.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        oldStage.close();
        stage.show();

    }

    @FXML
    void onActionChangeView() {

        AppointmentReports weeklyReport = () -> {

            ObservableList<Appointment> weeklyAppointments = FXCollections.observableArrayList();
            ObservableList<Appointment> appointmentsAll = Appointment.getAllAppointments();

            Locale locale = Locale.US;

            final DayOfWeek sundayStart = WeekFields.of(locale).getFirstDayOfWeek();
            final DayOfWeek saturdayEnd = DayOfWeek.of(sundayStart.getValue() - 1);
            System.out.println(saturdayEnd);

            LocalDate weekStart = LocalDate.now().with(TemporalAdjusters.previousOrSame(sundayStart));
            LocalDate weekEnd = LocalDate.now().with(TemporalAdjusters.nextOrSame(saturdayEnd));

            LocalTime midnight = LocalTime.MIDNIGHT;
            LocalTime almostMidnight = LocalTime.of(23, 59, 59);

            ZonedDateTime weekStartZoned = LocalDateTime.of(weekStart, midnight).atZone(ZoneId.systemDefault());
            ZonedDateTime weekEndZoned = LocalDateTime.of(weekEnd, almostMidnight).atZone(ZoneId.systemDefault());

            for (Appointment appointment : appointmentsAll) {
                if (appointment.getStart().isAfter(weekStartZoned) && appointment.getStart().isBefore(weekEndZoned)) {
                    weeklyAppointments.add(appointment);
                }
            }
            return weeklyAppointments;

        };
        AppointmentReports monthlyReport = () -> {

             ObservableList<Appointment> monthlyAppointments = FXCollections.observableArrayList();
             ObservableList<Appointment> appointmentsAll = Appointment.getAllAppointments();
             Month thisMonth = LocalDate.now().getMonth();


             for (Appointment appointment : appointmentsAll) {
                 LocalDate appointmentDate = appointment.getStart().toLocalDate();
                 if (appointmentDate.getMonth().equals(thisMonth)) {
                     monthlyAppointments.add(appointment);
                 }
             }
             return monthlyAppointments;
         };




        if (calendarByComboBox.getSelectionModel().getSelectedItem().equals("Weekly")) {
            apptTableView.setItems(weeklyReport.generateReport());
        }
        if (calendarByComboBox.getSelectionModel().getSelectedItem().equals("Monthly")) {
            apptTableView.setItems(monthlyReport.generateReport());
        }
        if (calendarByComboBox.getSelectionModel().getSelectedItem().equals("All")) {
            apptTableView.setItems(Appointment.getAllAppointments());
        }

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        customerIdCol.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        customerNameCol.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        addressIdCol.setCellValueFactory(new PropertyValueFactory<>("addressId"));
        customerPhoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        customerAddressCol.setCellValueFactory(new PropertyValueFactory<>("address"));
        customerTableView.setItems(Customer.getAllCustomers());

        apptIdCol.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
        apptCustIdCol.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        apptTypeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        apptStartCol.setCellValueFactory(new PropertyValueFactory<>("start"));
        apptEndCol.setCellValueFactory(new PropertyValueFactory<>("end"));
        apptTableView.setItems(Appointment.getAllAppointments());

        calendarByComboBox.setItems(Appointment.getViewApptsBy());


        Appointment appointment = Appointment.get15MinutesAlert();

        if (appointment != null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Appointment Reminder");
            alert.setHeaderText(null);
            alert.setContentText("You have a meeting starting in less than 15 minutes. Please check your schedule for more information.");
            alert.showAndWait();
        }
    }
}

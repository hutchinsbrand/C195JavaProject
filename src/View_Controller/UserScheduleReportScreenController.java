package View_Controller;

import Models.Appointment;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

public class UserScheduleReportScreenController implements Initializable {

    @FXML
    private TableView<Appointment> userScheduleTableView;

    @FXML
    private TableColumn<Appointment, Integer> userApptIdCol;

    @FXML
    private TableColumn<Appointment, Integer> userCustomerIdCol;

    @FXML
    private TableColumn<Appointment, String> userApptTypeCol;

    @FXML
    private TableColumn<Appointment, LocalDateTime> userApptStartCol;

    @FXML
    private TableColumn<Appointment, LocalDateTime> userApptEndCol;

    @FXML
    private Button returnToReportBtn;

    @FXML
    void onActionReturn() throws IOException {

        Stage stage = new Stage();
        Stage oldStage = (Stage) returnToReportBtn.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("ViewReportsScreen.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        oldStage.close();
        stage.show();

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        userApptIdCol.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
        userCustomerIdCol.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        userApptTypeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        userApptStartCol.setCellValueFactory(new PropertyValueFactory<>("start"));
        userApptEndCol.setCellValueFactory(new PropertyValueFactory<>("end"));
        userScheduleTableView.setItems(Appointment.getUserSchedule());

    }
}

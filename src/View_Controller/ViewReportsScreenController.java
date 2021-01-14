package View_Controller;

import Utility.ReportTypes;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ViewReportsScreenController implements Initializable {

    @FXML
    private ComboBox<String> reportTypeComboBox;

    @FXML
    private Button returnBtn;


    //Pulls up each report in a new window
    @FXML
    void onActionChangeType() throws IOException {

        if (reportTypeComboBox.getSelectionModel().getSelectedItem().equals("Monthly Appointment Type Summary")) {
            Stage stage = new Stage();
            Stage oldStage = (Stage) reportTypeComboBox.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("MonthlyAppointmentTypeScreen.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            oldStage.close();
            stage.show();
        }
        if (reportTypeComboBox.getSelectionModel().getSelectedItem().equals("Your Schedule")) {
            Stage stage = new Stage();
            Stage oldStage = (Stage) reportTypeComboBox.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("UserScheduleReportScreen.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            oldStage.close();
            stage.show();
        }
        if (reportTypeComboBox.getSelectionModel().getSelectedItem().equals("Customer Phone Tree")) {
            Stage stage = new Stage();
            Stage oldStage = (Stage) reportTypeComboBox.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("PhoneTreeReportScreen.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            oldStage.close();
            stage.show();
        }
    }

    @FXML
    void onActionReturn() throws IOException {

        Stage stage = new Stage();
        Stage oldStage = (Stage) returnBtn.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("HomePageScreen.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        oldStage.close();
        stage.show();

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        reportTypeComboBox.setItems(ReportTypes.getReportChoices());



    }
}

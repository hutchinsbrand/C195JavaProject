package View_Controller;

import Utility.ReportTypes;
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
import java.util.ResourceBundle;

public class MonthlyAppointmentTypeScreenController implements Initializable {

    @FXML
    private TableView<ReportTypes> monthlyTypeTableView;

    @FXML
    private TableColumn<ReportTypes, String> apptTypeCol;

    @FXML
    private TableColumn<ReportTypes, Integer> numApptsCol;

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

        apptTypeCol.setCellValueFactory(new PropertyValueFactory<>("AppointmentType"));
        numApptsCol.setCellValueFactory(new PropertyValueFactory<>("numOfAppointments"));
        monthlyTypeTableView.setItems(ReportTypes.getAllMonthlyReportTypes());

    }
}

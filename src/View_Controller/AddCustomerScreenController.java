package View_Controller;

import Models.Customer;

import Utility.Alerts;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;


public class AddCustomerScreenController {

    @FXML
    private TextField firstNameTxt;

    @FXML
    private TextField lastNameTxt;

    @FXML
    private TextField phoneTxt;

    @FXML
    private TextField addressTxt;

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
        String phoneNumber = "";
        String address = "";

        Alerts customerAlerts = (String alertTxt) -> {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(alertTxt);
            alert.setHeaderText(null);
            alert.setContentText("Please enter valid " + alertTxt);
            alert.showAndWait();

        };

        if (firstNameTxt.getText().isEmpty() || lastNameTxt.getText().isEmpty()) {
            customerAlerts.generateAlert("Customer Name");
        } else {
            customerName = firstNameTxt.getText() + " " + lastNameTxt.getText();
        }

        if (phoneTxt.getText().isEmpty()) {
            customerAlerts.generateAlert("Phone Number for Customer");
        } else {
            phoneNumber = phoneTxt.getText();
        }

        if (addressTxt.getText().isEmpty()) {
           customerAlerts.generateAlert("Address for Customer");
        } else {
            address = addressTxt.getText();
        }

        if (!firstNameTxt.getText().isEmpty() && !lastNameTxt.getText().isEmpty() && !phoneTxt.getText().isEmpty() && !addressTxt.getText().isEmpty()) {

            Customer.addCustomer(customerName, phoneNumber, address);

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

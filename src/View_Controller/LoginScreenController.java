package View_Controller;

import Models.User;
import Utility.UserLog;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

public class LoginScreenController implements Initializable {

    @FXML
    private TextField userTxt;

    @FXML
    private Label userLbl;

    @FXML
    private Label passwordLbl;

    @FXML
    private Button loginBtn;

    @FXML
    private Label titleLbl;

    @FXML
    private PasswordField passwordField;

    private String alertMessage;
    private String alertHeader;

    public void initialize (URL url, ResourceBundle rb) {

        Locale locale = Locale.getDefault();
        rb = ResourceBundle.getBundle("Utility/RB_es", locale);


        if (locale.getLanguage().equals("es")) {

            titleLbl.setText(rb.getString("title"));
            userLbl.setText(rb.getString("username"));
            passwordLbl.setText(rb.getString("password"));
            loginBtn.setText(rb.getString("login"));
            alertMessage = rb.getString("error");
            alertHeader = rb.getString("errorHeader");

        }
    }

    //Check occurs at login to test username and password. If one is incorrect, alert is displayed
    @FXML
    void onActionLogin() throws IOException {

        ObservableList<String> allUserNames = User.getAllUsernames();
        ObservableList<String> allPasswords = User.getAllPasswords();

        if (allUserNames.contains(userTxt.getText()) && allPasswords.contains(passwordField.getText())) {

            UserLog.log(userTxt.getText(), true);
            User user = new User(userTxt.getText(), passwordField.getText());
            User.setCurrentUser(user);

            Stage stage = new Stage();
            Stage oldStage = (Stage) loginBtn.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("HomePageScreen.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            oldStage.close();
            stage.show();
        }
        else {
            UserLog.log(userTxt.getText(),false);
            Alert alert = new Alert(Alert.AlertType.ERROR);

            alert.setHeaderText(null);

            if (Locale.getDefault().getLanguage().equals("es")) {

                alert.setTitle(alertHeader);
                alert.setContentText(alertMessage);
            }
            else {
                alert.setTitle("Error");
                alert.setContentText("Incorrect username and password. Please try again.");
            }
            alert.showAndWait();
        }
    }
}
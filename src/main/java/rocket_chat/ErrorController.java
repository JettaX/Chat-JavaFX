package rocket_chat;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.io.IOException;

public class ErrorController {
    @FXML
    public Label errorLabel;

    @FXML
    public Button okButton;

    public void setErrorMessage(String message) {
        errorLabel.setText(message);
    }

    public void handleOkButton(ActionEvent event) {
        okButton.getScene().getWindow().hide();
        try {
            Main.showLogin();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

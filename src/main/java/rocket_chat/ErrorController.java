package rocket_chat;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import lombok.Setter;
import rocket_chat.view.utils.BackUrl;

public class ErrorController {
    @Setter
    private BackUrl back;
    @FXML
    public Label errorLabel;

    @FXML
    public Button okButton;

    public void setErrorMessage(String message) {
        errorLabel.setText(message);
    }

    public void handleOkButton(ActionEvent event) {
        okButton.getScene().getWindow().hide();
        if (back != null) {
            if (back.equals(BackUrl.LOGIN)) {
                Main.showLogin();
            }
            if (back.equals(BackUrl.SIGNUP)) {
                Main.showSignUp();
            }
        } else {
            Main.showLogin();
        }

    }
}

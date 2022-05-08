package rocket_chat;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import rocket_chat.dao.UserDaoJDBC;
import rocket_chat.dao.UserSecureDaoJDBC;
import rocket_chat.entity.User;
import rocket_chat.repository.UserRepository;
import rocket_chat.repository.UserSecureRepository;
import rocket_chat.view.BackButton;
import rocket_chat.view.utils.BackUrl;

public class SignUpController {
    UserRepository userRepository = UserDaoJDBC.getINSTANCE();
    UserSecureRepository userSecureRepository = UserSecureDaoJDBC.getINSTANCE();

    @FXML
    public HBox titleBox;
    @FXML
    public TextField inputLogin;
    @FXML
    public TextField inputName;
    @FXML
    public TextField inputSurname;
    @FXML
    public PasswordField inputPasswordOne;
    @FXML
    public PasswordField inputPasswordTwo;
    @FXML
    public Button loginButton;

    public void initialize() {
        generateTitle();
    }

    private void generateTitle() {
        Button button = new BackButton(BackUrl.LOGIN);
        titleBox.getChildren().add(button);
    }

    public void signUpButtonAction(ActionEvent actionEvent) {
        String login = inputLogin.getText();
        String name = inputName.getText();
        String surname = inputSurname.getText();
        String passwordOne = inputPasswordOne.getText();
        String passwordTwo = inputPasswordTwo.getText();
        if (!login.isBlank() && !name.isBlank() && !surname.isBlank() && !passwordOne.isBlank() && !passwordTwo.isBlank()) {
            if (login.length() > 2 && name.length() > 2 && surname.length() > 2) {
                if (passwordOne.equals(passwordTwo) && passwordOne.length() >= 6) {
                    if (userRepository.getUserByUserName(login) == null) {
                        userRepository.saveUser(new User(login, name, surname));
                        userSecureRepository.createUserSecure(login, passwordOne);
                        Main.showLogin();
                    } else {
                        Main.showError("User with this login already exists", BackUrl.SIGNUP);
                    }
                } else {
                    Main.showError("Password is not correct or too short (min 6 symbols)", BackUrl.SIGNUP);
                }
            } else {
                Main.showError("Login or name or surname is too short (min 3 symbols)", BackUrl.SIGNUP);
            }
        } else {
            Main.showError("All fields must be filled", BackUrl.SIGNUP);
        }
    }

    public void onMouseClicked(MouseEvent mouseEvent) {
        if (mouseEvent.getButton().equals(MouseButton.BACK)) {
            Main.showLogin();
        }
    }
}

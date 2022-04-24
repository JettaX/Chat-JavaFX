package rocket_chat;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import rocket_chat.repository.*;

import java.util.logging.Logger;

public class LoginController {
    private Logger logger = Logger.getLogger(LoginController.class.getName());
    private UserRepository userRepository;
    private UserSecureRepository userSecureRepository;
    private Connection connection;
    @FXML
    public Button loginButton;
    @FXML
    public TextField inputLogin;
    @FXML
    public TextField inputPassword;

    public void initialize() {
        userSecureRepository = new UserSecureRepositoryInMemory();
        userRepository = new UserRepositoryInMemory();
        connection = new Connection();
    }

    public void loginButtonAction() {
        String login = inputLogin.getText();
        String password = inputPassword.getText();
        if (login.isBlank() || password.isBlank()) {
            return;
        }
        try {
            connection.get().sendLogin(login, password);
        } catch (NullPointerException e) {
            logger.info("Server is not connected");
        }
    }
}

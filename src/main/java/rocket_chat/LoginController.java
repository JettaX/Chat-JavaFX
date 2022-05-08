package rocket_chat;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import lombok.extern.slf4j.Slf4j;
import rocket_chat.dao.UserDaoJDBC;
import rocket_chat.repository.*;
import rocket_chat.util.TcpConnection;

@Slf4j
public class LoginController {
    private UserRepository userRepository;
    private TcpConnection tcpConnection;
    @FXML
    public Button loginButton;
    @FXML
    public TextField inputLogin;
    @FXML
    public TextField inputPassword;

    public void initialize() {
        userRepository = UserDaoJDBC.getINSTANCE();
        tcpConnection = new TcpConnection();
    }

    public void loginButtonAction() {
        String login = inputLogin.getText();
        String password = inputPassword.getText();
        if (login.isBlank() || password.isBlank()) {
            return;
        }
        try {
            tcpConnection.get().sendLogin(login, password);
        } catch (NullPointerException e) {
            log.warn("Server is not connected");
        }
    }
}

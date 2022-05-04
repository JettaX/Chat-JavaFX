package rocket_chat;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import rocket_chat.dao.UserDao;
import rocket_chat.dao.UserSecureDao;
import rocket_chat.entity.User;
import rocket_chat.repository.UserRepository;
import rocket_chat.view.BackButton;
import rocket_chat.view.utils.RoundPicture;

import java.io.IOException;

public class SettingsController {
    private UserRepository userRepository;
    @FXML
    public VBox mainBox;
    @FXML
    public HBox titleBox;
    @FXML
    public HBox infoBox;
    @FXML
    public HBox settingsBox;
    @FXML
    public TextField labelUserNameField;
    @FXML
    public TextField userNameField;
    @FXML
    public TextField labelFirstNameField;
    @FXML
    public TextField firstNameField;
    @FXML
    public TextField labelLastNameField;
    @FXML
    public TextField lastNameField;
    @FXML
    public VBox settingsBoxWrapper;
    @FXML
    public Button saveButton;

    public void initialize() {
        userRepository = UserDao.getINSTANCE();
        generateTitle();
        generateInfo();
        generateSettings();
    }

    private void generateSettings() {
        userNameField.setText(Main.user.getUserName());
        firstNameField.setText(Main.user.getFirstName());
        lastNameField.setText(Main.user.getLastName());
        HBox.setMargin(settingsBoxWrapper, new javafx.geometry.Insets(10, 0, 10, 0));
    }

    private void generateTitle() {
        Button button = new BackButton();
        titleBox.getChildren().add(button);
    }

    private void generateInfo() {
        VBox vBox = new VBox();
        Button button = new Button();
        button.getStyleClass().add("picture-button");
        button.setGraphic(RoundPicture.getRoundPicture(120, Main.user.getImagePath()));
        vBox.getChildren().add(button);

        Label labelUserName = new Label("@" .concat(Main.user.getUserName()));
        labelUserName.getStyleClass().add("label");
        vBox.setAlignment(javafx.geometry.Pos.CENTER);
        vBox.getChildren().add(labelUserName);

        Label labelNameAndSurname = new Label(Main.user.getFirstName().concat(" ").concat(Main.user.getLastName()));
        labelNameAndSurname.getStyleClass().add("label");
        vBox.setAlignment(javafx.geometry.Pos.CENTER);
        vBox.getChildren().add(labelNameAndSurname);

        HBox.setMargin(vBox, new javafx.geometry.Insets(10, 0, 10, 0));
        infoBox.getChildren().add(vBox);
    }

    public void mouseListener(MouseEvent mouseEvent) {
        if (mouseEvent.getButton().equals(MouseButton.BACK)) {
            try {
                Main.showChats(Main.user);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void editUserNameListener(MouseEvent mouseEvent) {
        setEditable();
        userNameField.setEditable(true);
    }

    public void editFirstNameListener(MouseEvent mouseEvent) {
        setEditable();
        firstNameField.setEditable(true);
    }

    public void editLastNameListener(MouseEvent mouseEvent) {
        setEditable();
        lastNameField.setEditable(true);
    }

    private void setEditable() {
        saveButton.setVisible(true);
        userNameField.setEditable(false);
        firstNameField.setEditable(false);
        lastNameField.setEditable(false);
    }

    private void reloadFields() {
        userNameField.setText(Main.user.getUserName());
        firstNameField.setText(Main.user.getFirstName());
        lastNameField.setText(Main.user.getLastName());
    }

    public void saveListener(MouseEvent mouseEvent) {
        String userName = userNameField.getText();
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();

        if (userName.isBlank() || firstName.isBlank() || lastName.isBlank()) {
            creatAlertAndShow("You must fill all fields");
            reloadFields();
        } else if (!userName.equals(Main.user.getUserName()) ||
                !firstName.equals(Main.user.getFirstName()) ||
                !lastName.equals(Main.user.getLastName())) {
            UserSecureDao.getINSTANCE().updateLogin(Main.user.getUserName(), userName);
            User user = new User(userName, firstName, lastName, Main.user.getImagePath());
            userRepository.updateUser(Main.user, user);
            Main.user = user;
            creatAlertAndShow("Your username has been changed. You must log in with your new username");
        }
    }

    private void creatAlertAndShow(String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Alert");
        alert.setHeaderText("Alert");
        alert.setContentText(content);
        alert.showAndWait();
    }
}

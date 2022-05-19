package rocket_chat;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import rocket_chat.util.HistoryUtil;
import rocket_chat.view.BackButton;
import rocket_chat.view.utils.BackUrl;

import java.util.List;

public class HistoryController {
    private String userName;
    @FXML
    public HBox titleBox;
    @FXML
    public VBox messagesWrapper;

    public void initialize() {
        generateTitle();
    }

    public void initializer(String user) {
        userName = user;
        generateMessages();
    }

    private void generateMessages() {
        List<String> list = HistoryUtil.getHistory(Main.user.getUserName(), userName);
        for (String message : list) {
            messagesWrapper.getChildren().add(new Label(message));
        }
    }

    private void generateTitle() {
        Button button = new BackButton(BackUrl.CHAT_LIST);
        titleBox.getChildren().add(button);
    }

    public void mouseListener(MouseEvent mouseEvent) {
        if (mouseEvent.getButton().equals(MouseButton.BACK)) {
            Main.showHistories();
        }
    }
}

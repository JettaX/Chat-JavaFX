package rocket_chat;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import rocket_chat.util.HistoryUtil;
import rocket_chat.view.BackButton;
import rocket_chat.view.utils.BackUrl;

import java.util.List;

public class HistoriesController {

    @FXML
    public HBox titleBox;
    @FXML
    public VBox chatsList;

    public void initialize() {
        generateTitle();
        generateChatList();
    }

    private void generateChatList() {
        List<String> list = HistoryUtil.getAllHistory(Main.user.getUserName());
        for (String s : list) {
            HBox hBox = new HBox();
            hBox.getStyleClass().add("button-chat-wrapper");
            Button button = new Button(s);
            button.getStyleClass().add("button-chat");
            HBox.setHgrow(button, javafx.scene.layout.Priority.ALWAYS);
            button.setOnMouseClicked(event -> {
                Main.showHistory(s);
            });
            hBox.getChildren().add(button);
            chatsList.getChildren().add(hBox);
        }
    }

    private void generateTitle() {
        Button button = new BackButton(BackUrl.CHAT_LIST);
        titleBox.getChildren().add(button);
    }

    public void mouseListener(MouseEvent mouseEvent) {
        if (mouseEvent.getButton().equals(MouseButton.BACK)) {
            Main.showChats(Main.user);
        }
    }
}

package rocket_chat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.Getter;
import rocket_chat.entity.Chat;
import rocket_chat.entity.Message;
import rocket_chat.repository.ChatRepository;
import rocket_chat.repository.ChatRepositoryInMemory;
import rocket_chat.repository.Connection;
import rocket_chat.validation.Validator;
import rocket_chat.view.LabelMessageNotSent;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChatController {
    private ChatRepository chatRepository;
    private Validator validator;
    @Getter
    private Chat chat;
    private Logger logger = Logger.getLogger(ChatController.class.getName());
    private Connection connection;

    @FXML
    public ScrollPane scrollPaneForMessages;
    @FXML
    Button sendButton;
    @FXML
    TextField inputField;
    @FXML
    public VBox messagesWrapper;
    @FXML
    public HBox titleWrapper;

    public void initializer(Chat chat) {
        this.chat = chat;
        connection = new Connection();
        validator = new Validator();
        chatRepository = new ChatRepositoryInMemory();
        addMessages();
        generateTitle();
    }

    public void generateTitle() {
        Button backButton = new Button("Back");
        backButton.setOnAction(event -> {
            try {
                Main.showChats(Main.user);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        backButton.getStyleClass().add("backButton");
        titleWrapper.getChildren().add(backButton);

        Label title = new Label(chat.getFriendUser().getName() + " " + chat.getFriendUser().getSurname());
        title.getStyleClass().add("titleLabel");
        titleWrapper.setAlignment(Pos.CENTER_LEFT);
        titleWrapper.getChildren().add(title);
    }

    @FXML
    protected void sendMessageListener() {
        if (!validator.isValid(inputField.getText())) {
            inputField.clear();
            return;
        }

        Message message = new Message(chat.getOwnerUser().getUserLogin(), chat.getFriendUser().getUserLogin(),
                inputField.getText());

        try {
            connection.get().sendMessage(new ObjectMapper()
                    .writeValueAsString(message));
        } catch (JsonProcessingException e) {
            logger.log(Level.WARNING, "Cant parse message");
        } catch (NullPointerException e) {
            logger.log(Level.SEVERE, "Connection not found");
        }

        addMessage(message, true);

        inputField.clear();
        inputField.requestFocus();
    }

    @FXML
    protected void keyListener(KeyEvent event) {
        if (event.getCode().getCode() == 10) {
            sendMessageListener();
        }
    }

    public void addMessage(Message message, boolean isOwner) {
        if (chat.getMessages().isEmpty()) {
            AtomicBoolean isSave = new AtomicBoolean(true);
            chatRepository.getAllChatsByUserLogin(chat.getOwnerUser().getUserLogin()).forEach(chatik -> {
                if (chat.equals(chatik)) {
                    isSave.set(false);
                }
            });
            if (isSave.get()) {
                chatRepository.saveChat(chat);
            }
        }
        chatRepository.addMessage(message);

        HBox messageWrapper = new HBox();
        messageWrapper.getStyleClass().add("messageWrapper");

        Label labelMessage =
                new Label(message.getText() + "     " + message.getTime().getHour() + ":" + message.getTime().getMinute());
        labelMessage.setWrapText(true);
        LabelMessageNotSent labelMessageNotSent = null;
        if (isOwner) {
            labelMessage.getStyleClass().add("messageLabelOwner");
            if (!Main.isServerConnected) {
                labelMessageNotSent = new LabelMessageNotSent();
                messageWrapper.setAlignment(Pos.CENTER_LEFT);
            }
            messageWrapper.setAlignment(Pos.CENTER_RIGHT);
        } else {
            labelMessage.getStyleClass().add("messageLabelFriend");
            messageWrapper.setAlignment(Pos.CENTER_LEFT);
        }
        if (labelMessageNotSent != null) {
            messageWrapper.getChildren().add(labelMessageNotSent);
        }

        messageWrapper.getChildren().add(labelMessage);
        messagesWrapper.getChildren().add(messageWrapper);

        scrollPaneForMessages.setVvalue(scrollPaneForMessages.getVmax());
    }

    public void addMessages() {
        for (Message message : chat.getMessages()) {
            HBox messageWrapper = new HBox();
            messageWrapper.getStyleClass().add("messageWrapper");
            Label label = new Label(message.getText() + "     " + message.getTime().getHour() + ":" + message.getTime().getMinute());

            label.setWrapText(true);
            if (message.getUserNameFrom().equals(chat.getOwnerUser().getUserLogin())) {
                label.getStyleClass().add("messageLabelOwner");
                messageWrapper.setAlignment(Pos.CENTER_RIGHT);
            } else {
                label.getStyleClass().add("messageLabelFriend");
                messageWrapper.setAlignment(Pos.CENTER_LEFT);
            }
            messageWrapper.getChildren().add(label);
            messagesWrapper.getChildren().add(messageWrapper);
        }
        scrollPaneForMessages.setVvalue(Double.MAX_VALUE);
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
}
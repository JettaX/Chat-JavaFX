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
import lombok.extern.slf4j.Slf4j;
import rocket_chat.dao.ChatDaoJDBC;
import rocket_chat.entity.Chat;
import rocket_chat.entity.Message;
import rocket_chat.repository.ChatRepository;
import rocket_chat.util.TcpConnection;
import rocket_chat.validation.Validator;
import rocket_chat.view.BackButton;
import rocket_chat.view.LabelMessageNotSent;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class ChatController {
    private ChatRepository chatRepository;
    private Validator validator;
    @Getter
    private Chat chat;
    private TcpConnection tcpConnection;

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
        tcpConnection = new TcpConnection();
        validator = new Validator();
        chatRepository = ChatDaoJDBC.getINSTANCE();
        addMessages();
        generateTitle();
    }

    public void generateTitle() {
        Button backButton = new BackButton();
        titleWrapper.getChildren().add(backButton);

        Label title = new Label(chat.getFriendUser().getFirstName() + " " + chat.getFriendUser().getLastName());
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

        Message message = new Message(chat, chat.getOwnerUser(), chat.getFriendUser(),
                inputField.getText());

        try {
            tcpConnection.get().sendMessage(new ObjectMapper()
                    .writeValueAsString(message));
        } catch (JsonProcessingException e) {
            log.warn("Cant parse message");
            e.printStackTrace();
        } catch (NullPointerException e) {
            log.warn("Connection not found");
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
            chatRepository.getAllChatsByUserLogin(chat.getOwnerUser().getUserName()).forEach(chatik -> {
                if (chat.equals(chatik)) {
                    isSave.set(false);
                }
            });
            if (isSave.get()) {
                chatRepository.saveChat(chat);
            }
        }
        chat.addMessage(message);

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
            if (message.getUserFrom().getUserName().equals(chat.getOwnerUser().getUserName())) {
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
package rocket_chat;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import rocket_chat.entity.Chat;
import rocket_chat.entity.User;
import rocket_chat.repository.ChatRepository;
import rocket_chat.repository.ChatRepositoryJPA;
import rocket_chat.repository.UserRepository;
import rocket_chat.repository.UserRepositoryJPA;
import rocket_chat.validation.Validator;
import rocket_chat.view.ChatsButton;

import java.io.IOException;
import java.util.List;

public class ChatsButtonsController {
    public ScrollPane scrollPaneForChats;
    private Validator validator = new Validator();
    private ChatRepository chatRepository;
    private UserRepository userRepository;
    private boolean isLoadSearch = false;
    @FXML
    public VBox chatsWrapper;
    @FXML
    public HBox searchWrapper;
    @FXML
    public TextField searchInput;

    public void initializer() {
        chatRepository = new ChatRepositoryJPA();
        userRepository = new UserRepositoryJPA();
        addChats();
        searchInput.addEventHandler(Event.ANY, event ->
        {
            if (searchInput.getText().isBlank()) {
                if (!isLoadSearch) {
                    chatsWrapper.getChildren().clear();
                    addChats();
                    isLoadSearch = false;
                }
            }
        });
    }

    private void addChats() {
        try {
            User ownerUser = Main.user;
            List<Chat> chats = chatRepository.getAllChatsByUserLogin(ownerUser.getUserName());
            for (Chat chat : chats) {
                HBox hBox = new HBox();
                Button chatButton = new ChatsButton(chat);
                HBox.setHgrow(chatButton, javafx.scene.layout.Priority.ALWAYS);
                chatButton.getStyleClass().add("chatButtonWithFriend");
                chatButton.setOnAction(event -> {
                    try {
                        Main.showChat(chat);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                hBox.getChildren().add(chatButton);
                chatsWrapper.getChildren().add(hBox);
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void searchKeyListener(KeyEvent keyEvent) {
        if (keyEvent.getCode().getCode() == 10) {
            String searchString = searchInput.getText();
            if (validator.isValid(searchString) && !searchString.equals(Main.user.getUserName())) {
                if (searchString.equals("/all")) {
                    chatsWrapper.getChildren().clear();
                    for (User user : userRepository.getUsers()) {
                        addSearchResult(user);
                    }
                } else {
                    User user = userRepository.getUserById(searchString);
                    if (user != null) {
                        chatsWrapper.getChildren().clear();
                        addSearchResult(user);
                    }
                }
            }
        }
    }


    private void addSearchResult(User user) {
        HBox hBox = new HBox();
        Button chatButton = new ChatsButton(user);
        chatButton.getStyleClass().add("chatButtonWithUser");
        HBox.setHgrow(chatButton, javafx.scene.layout.Priority.ALWAYS);
        chatButton.setOnAction(event -> {
            try {
                Main.showChat(new Chat(Main.user, user));
                isLoadSearch = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        hBox.getChildren().add(chatButton);
        chatsWrapper.getChildren().add(hBox);
    }
}
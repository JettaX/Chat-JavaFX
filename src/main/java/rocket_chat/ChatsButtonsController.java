package rocket_chat;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import rocket_chat.dao.ChatDaoJDBC;
import rocket_chat.dao.UserDaoJDBC;
import rocket_chat.entity.Chat;
import rocket_chat.entity.User;
import rocket_chat.repository.ChatRepository;
import rocket_chat.repository.UserRepository;
import rocket_chat.validation.Validator;
import rocket_chat.view.ChatsButton;

import java.util.List;
import java.util.Objects;

public class ChatsButtonsController {
    public ScrollPane scrollPaneForChats;
    private Validator validator = new Validator();
    private ChatRepository chatRepository;
    private UserRepository userRepository;
    private boolean isLoadSearch = false;
    @FXML
    public Button historyButton;
    @FXML
    public Button settingsButton;
    @FXML
    public VBox chatsWrapper;
    @FXML
    public HBox searchWrapper;
    @FXML
    public TextField searchInput;

    public void initializer() {
        chatRepository = ChatDaoJDBC.getINSTANCE();
        userRepository = UserDaoJDBC.getINSTANCE();
        settingsButton.setGraphic(new javafx.scene.image.ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/icon/settings.png")),
                35, 35, true, true)));
        historyButton.setGraphic(new javafx.scene.image.ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/icon/history.png")),
                35, 35, true, true)));
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
                    Thread thread = new Thread(() -> {
                        Chat chat1 = chatRepository.getChatByOwnerIdAndFriendId(chat.getOwnerUser().getUserName(), chat.getFriendUser().getUserName());
                        while (chat1 == null) {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        Platform.runLater(() -> Main.showChat(chat1));
                    });
                    thread.start();
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
                    List<User> userList = userRepository.searchUser(searchString);
                    if (!userList.isEmpty()) {
                        chatsWrapper.getChildren().clear();
                        for (User us : userList) {
                            addSearchResult(us);
                        }
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
            Main.showChat(new Chat(Main.user, user));
            isLoadSearch = true;
        });
        hBox.getChildren().add(chatButton);
        chatsWrapper.getChildren().add(hBox);
    }

    public void settingsButtonListener(ActionEvent actionEvent) {
        Main.showSettings();
    }

    public void historyButtonListener(ActionEvent actionEvent) {
        Main.showHistories();
    }
}
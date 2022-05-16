package rocket_chat;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import rocket_chat.entity.Chat;
import rocket_chat.entity.User;
import rocket_chat.util.TcpConnection;
import rocket_chat.view.utils.BackUrl;

import java.io.IOException;

@Slf4j
public class Main extends Application {
    public static User user;
    private static Stage stage;
    private static TcpConnection tcpConnection;
    public static ChatController chatController;
    public static boolean isFriendConnected = false;
    public static boolean isServerConnected = false;
    public static Thread thread;

    @Override
    public void start(Stage stage) {
        tcpConnection = new TcpConnection();
        this.stage = stage;
        stage.setResizable(true);
        stage.setTitle("RocketChat");
        stage.setWidth(400);
        stage.setHeight(600);
        createConnectionCheckerThread();
        showLogin();
    }

    public static void main(String[] args) {
        launch();
    }

    public static void showChat(Chat chat) {
        stage.close();
        FXMLLoader fxmlLoader = createStage("chat.fxml");
        ChatController chatController = fxmlLoader.getController();
        Main.chatController = chatController;
        chatController.initializer(chat);
    }

    public static void showChats(User user) {
        Main.user = user;
        stage.close();
        FXMLLoader fxmlLoader = createStage("chats.fxml");
        ChatsButtonsController chatsButtonsController = fxmlLoader.getController();
        chatsButtonsController.initializer();
        nullingLink();
    }

    public static void showSettings() {
        stage.close();
        createStage("settings.fxml");
        nullingLink();
    }

    public static void showError(String message, BackUrl back) {
        FXMLLoader fxmlLoader = createStage("error.fxml");
        ErrorController errorController = fxmlLoader.getController();
        errorController.setBack(back);
        errorController.setErrorMessage(message);
        nullingLink();
    }

    public static void showLogin() {
        stage.close();
        createStage("login.fxml");
        nullingLink();
    }

    public static void showSignUp() {
        stage.close();
        createStage("signup.fxml");
        nullingLink();
    }

    private static FXMLLoader createStage(String fxml) {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(fxml));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load(), stage.getWidth() - 16, stage.getHeight() - 39);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        double x = stage.getX();
        double y = stage.getY();
        stage.setScene(scene);
        stage.setX(x);
        stage.setY(y);
        stage.show();
        return fxmlLoader;
    }

    public static void exit() {
        user = null;
        isServerConnected = false;
        isFriendConnected = false;
        tcpConnection.remove();
        thread.interrupt();
        createConnectionCheckerThread();
    }

    private static void createConnectionCheckerThread() {
        thread = connectionCheckerThread();
        thread.start();
    }

    private static void nullingLink() {
        chatController = null;
    }

    private static void createConnection() throws InterruptedException {
        if (!isServerConnected && user == null) {
            new TcpListener();
        }
        if (!isServerConnected && user != null) {
            user = null;
            Platform.runLater(Main::showLogin);
            Thread.sleep(5000);
        }
    }

    private static Thread connectionCheckerThread() {
        return new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000);
                    createConnection();
                } catch (InterruptedException e) {
                    log.info("Connection checker thread interrupted");
                    return;
                }
            }
        });
    }
}
package rocket_chat;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import rocket_chat.entity.Chat;
import rocket_chat.entity.Message;
import rocket_chat.entity.User;
import rocket_chat.repository.*;

import java.io.IOException;

@Slf4j
public class Main extends Application {
    public static User user;
    private static Stage stage;
    public static ChatController chatController;
    public static boolean isFriendConnected = false;
    public static boolean isServerConnected = false;
    public static Thread thread;

    @Override
    public void start(Stage stage) throws IOException {
        /*initializerData();*/
        this.stage = stage;
        stage.setResizable(true);
        stage.setTitle("RocketChat");
        stage.setWidth(400);
        stage.setHeight(600);
        thread = connectionCheckerThread();
        thread.start();
        showLogin();
    }

    public static void main(String[] args) {
        launch();
    }

    public static void showChat(Chat chat) throws IOException {
        stage.close();
        FXMLLoader fxmlLoader = createStage("chat.fxml");
        ChatController chatController = fxmlLoader.getController();
        Main.chatController = chatController;
        chatController.initializer(chat);
    }

    public static void showChats(User user) throws IOException {
        Main.user = user;
        stage.close();
        FXMLLoader fxmlLoader = createStage("chats.fxml");
        ChatsButtonsController chatsButtonsController = fxmlLoader.getController();
        chatsButtonsController.initializer();
        nullingLink();
    }

    public static void showError(String message) throws IOException {
        FXMLLoader fxmlLoader = createStage("error.fxml");
        ErrorController errorController = fxmlLoader.getController();
        errorController.setErrorMessage(message);
        nullingLink();
    }

    public static void showLogin() throws IOException {
        stage.close();
        createStage("login.fxml");
        nullingLink();
    }

    private static FXMLLoader createStage(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(fxml));
        Scene scene = new Scene(fxmlLoader.load(), stage.getWidth() - 16, stage.getHeight() - 39);
        double x = stage.getX();
        double y = stage.getY();
        stage.setScene(scene);
        stage.setX(x);
        stage.setY(y);
        stage.show();
        return fxmlLoader;
    }

    private static void nullingLink() {
        chatController = null;
    }

    private void createConnection() throws InterruptedException {
        if (!isServerConnected && user == null) {
            new TcpListener();
        }
        if (!isServerConnected && user != null) {
            user = null;
            Platform.runLater(() -> {
                try {
                    showLogin();
                } catch (IOException e) {
                    log.warn("Error while showing loginPage");
                }
            });
            Thread.sleep(5000);
        }
    }

    private Thread connectionCheckerThread() {
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

    private void initializerData() {
        ChatRepository chatRepository = new ChatRepositoryJPA();
        UserRepository userRepository = new UserRepositoryJPA();

        User mainUser = new User("admin", "Max", "Maxon");
        User userOne = new User("lilyPit", "Lily", "Pitersky");
        User userTwo = new User("Karmenchik", "Karen", "Mamonage");
        User userThree = new User("SteveApple", "Steve", "Jobs");
        User userFour = new User("Jonson@Lol", "Jon", "Ar");
        User userFive = new User("KittyClair", "Karen", "Clair");
        User userSix = new User("KekLol", "Sara", "Bond");

        userOne.setImagePath("/images/iconsForUsers/1.jpg");
        userTwo.setImagePath("/images/iconsForUsers/2.jpg");
        userThree.setImagePath("/images/iconsForUsers/3.jpg");

        Chat chatWithOne = new Chat(mainUser, userOne);
        Chat chatWithTwo = new Chat(mainUser, userTwo);

        Chat chatWithThree = new Chat(userOne, mainUser);
        Chat chatWithFour = new Chat(userOne, userThree);

        Message message1 = new Message(chatWithOne, userOne, mainUser, "Hi");
        Message message2 = new Message(chatWithOne, mainUser, userOne, "Lol");
        Message message3 = new Message(chatWithOne, userOne, mainUser, "Bye");
        Message message4 = new Message(chatWithOne, mainUser, userOne, "Kek");
        Message message5 = new Message(chatWithOne, userOne, mainUser, "Chill");
        Message message6 = new Message(chatWithTwo, userTwo, mainUser, "Sleep");
        Message message7 = new Message(chatWithTwo, mainUser, userTwo, "Go");
        Message message8 = new Message(chatWithTwo, userTwo, mainUser, "Work");
        Message message9 = new Message(chatWithTwo, mainUser, userTwo, "Dance");
        Message message11 = new Message(chatWithTwo, mainUser, userTwo, "Конструктор поля JFormattedTextField в " +
                "качестве параметра" +
                " " +
                "получает форматирующий объект, унаследованный от абстрактного внутреннего класса AbstractFormatter. Когда в форматированное текстовое поле вводятся символы, то сразу же вызывается форматирующий объект, в задачу которого входит анализ введенного значения и принятие решения о соответствии этого значения некоторому формату. Основными составляющими форматирующего объекта являются фильтр документа DocumentFilter, который принимает решение, разрешать или нет очередное изменение в документе, а также навигационный фильтр NavigationFilter. Навигационный фильтр получает исчерпывающую информацию о перемещениях курсора в текстовом поле и способен запрещать курсору появляться в некоторых областях поля (таких как разделители номеров, дат и других данных, которые не должны редактироваться). Форматирующий объект также отвеачет за действие, которое предпринимается в случае ввода пользователем неверного значения (по умолчанию раздается звуковой сигнал).");
        Message message10 = new Message(chatWithTwo, userTwo, mainUser, "Out");

        chatWithOne.addMessage(message1);
        chatWithOne.addMessage(message2);
        chatWithOne.addMessage(message3);
        chatWithOne.addMessage(message4);
        chatWithOne.addMessage(message5);


        chatWithTwo.addMessage(message6);
        chatWithTwo.addMessage(message7);
        chatWithTwo.addMessage(message8);
        chatWithTwo.addMessage(message9);
        chatWithTwo.addMessage(message10);
        chatWithTwo.addMessage(message11);

        userRepository.saveUser(mainUser);
        userRepository.saveUser(userOne);
        userRepository.saveUser(userTwo);
        userRepository.saveUser(userThree);
        userRepository.saveUser(userFour);
        userRepository.saveUser(userFive);
        userRepository.saveUser(userSix);

        chatRepository.saveChat(chatWithOne);
        chatRepository.saveChat(chatWithTwo);
        chatRepository.saveChat(chatWithThree);
        chatRepository.saveChat(chatWithFour);
    }
}
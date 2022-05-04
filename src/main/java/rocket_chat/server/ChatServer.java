package rocket_chat.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import rocket_chat.dao.UserDao;
import rocket_chat.dao.UserSecureDao;
import rocket_chat.entity.Chat;
import rocket_chat.entity.Message;
import rocket_chat.entity.User;
import rocket_chat.entity.UserSecure;
import rocket_chat.network.TCPConnection;
import rocket_chat.network.TCPConnectionListener;
import rocket_chat.repository.*;
import rocket_chat.util.JdbcConnection;

import java.io.IOException;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.*;

@Slf4j
public class ChatServer implements TCPConnectionListener {
    public static void main(String[] args) {
        new ChatServer();
    }

    private UserSecureRepository userSecureRepository;
    private Map<String, TCPConnection> connections;
    private Map<String, Queue<String>> queues;
    private SortedSet<String> users;

    private ChatServer() {
        users = new TreeSet<>();
        userSecureRepository = UserSecureDao.getINSTANCE();
        initializeData();
        queues = new HashMap<>();
        log.info("Starting server...");
        connections = new HashMap<>();

        try (ServerSocket serverSocket = new ServerSocket(8188)) {
            while (true) {
                try {
                    new TCPConnection(serverSocket.accept(), this);
                } catch (IOException e) {
                    log.warn("Error accepting connection: {}", e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public synchronized void onConnected(TCPConnection tcpConnection, String login) {
        connections.put(login, tcpConnection);
        checkQueues(login);
        log.info("Client connected {}", login);
    }

    @Override
    public synchronized void onReceiveMessage(TCPConnection tcpConnection, String message) {
        sendMessage(message);
    }

    @Override
    public synchronized void onDisconnect(TCPConnection tcpConnection, String login) {
        if (login != null) {
            connections.remove(login);
            users.remove(login);
        }
        log.info("Client disconnected {}", login);
    }

    @Override
    public synchronized void onException(TCPConnection tcpConnection, Exception e) {
        log.warn(e.getMessage());
    }

    @Override
    public void onAttemptAuth(TCPConnection tcpConnection, String loginPassword) throws IOException {
        String login = loginPassword.split(":")[0].trim();
        String password = loginPassword.split(":")[1].trim();
        log.info("Attempt auth {}", login);
        if (users.contains(login) || !userSecureRepository.checkAuth(login, password)) {
            throw new IOException("Auth failed");
        }
    }

    @Override
    public void onAuthSuccess(TCPConnection tcpConnection, String login) {
        log.info("Auth success {}", login);
        users.add(login);
        tcpConnection.authFailed("/auth_success");
        tcpConnection.authSuccess(login);
    }

    @Override
    public void onAuthFailed(TCPConnection tcpConnection, String error) {
        log.info("Auth failed");
        if (error != null) {
            tcpConnection.authFailed(error);
        }
    }

    private void sendMessage(String message) {
        Message mess = parseMessage(message);

        String connectionId = mess.getUserTo().getUserName();

        if (connections.containsKey(connectionId)) {
            connections.get(connectionId).sendMessage(message);
        } else {
            addMessageInQueue(message, mess);
        }
    }

    private void addMessageInQueue(String gsonMessage, Message message) {
        String connectionId = message.getUserTo().getUserName();
        if (queues.containsKey(connectionId)) {
            Queue<String> queue = queues.get(connectionId);
            queue.add(gsonMessage);
        } else {
            Queue<String> queue = new java.util.LinkedList<>();
            queue.add(gsonMessage);
            queues.put(connectionId, queue);
        }
    }

    private void checkQueues(String login) {
        if (queues.containsKey(login)) {
            Queue<String> queue = queues.get(login);
            while (!queue.isEmpty()) {
                connections.get(login).sendMessage(queue.poll());
            }
        }
    }

    private Message parseMessage(String message) {
        Message mess = null;
        try {
            mess = new ObjectMapper().readerFor(Message.class).readValue(message);
        } catch (JsonProcessingException e) {
            log.warn("Error while parsing message");
        }
        return mess;
    }

    private void initializeData() {

        try (var connection = JdbcConnection.getConnection();
             var statement = connection.createStatement()) {
            String sql = new String(Files.readAllBytes(Paths.get("src/main/resources/Query.sql")));
            statement.execute(sql);
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }

        UserSecure userOneSecure = new UserSecure("admin", "1234");
        UserSecure userTwoSecure = new UserSecure("lilyPit", "1234");
        UserSecure userThreeSecure = new UserSecure("Karmenchik", "1234");
        UserSecure mainUserSecure = new UserSecure("SteveApple", "1234");
        UserSecure userFourSecure = new UserSecure("Jonson@Lol", "1234");
        UserSecure userFiveSecure = new UserSecure("KittyClair", "1234");
        UserSecure userSixSecure = new UserSecure("KekLol", "1234");

        userSecureRepository.createUserSecure(userOneSecure);
        userSecureRepository.createUserSecure(userTwoSecure);
        userSecureRepository.createUserSecure(userThreeSecure);
        userSecureRepository.createUserSecure(userFourSecure);
        userSecureRepository.createUserSecure(userFiveSecure);
        userSecureRepository.createUserSecure(userSixSecure);
        userSecureRepository.createUserSecure(mainUserSecure);

        ChatRepository chatRepository = new ChatRepositoryInMemory();
        UserRepository userRepository = UserDao.getINSTANCE();

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

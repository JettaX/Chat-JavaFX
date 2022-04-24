package rocket_chat.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import rocket_chat.entity.Message;
import rocket_chat.entity.UserSecure;
import rocket_chat.network.TCPConnection;
import rocket_chat.network.TCPConnectionListener;
import rocket_chat.repository.UserSecureRepository;
import rocket_chat.repository.UserSecureRepositoryInMemory;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChatServer implements TCPConnectionListener {
    public static void main(String[] args) {
        new ChatServer();
    }

    private final Logger logger = Logger.getLogger(ChatServer.class.getName());
    private UserSecureRepository userSecureRepository;
    private Map<String, TCPConnection> connections;
    private Map<String, Queue<String>> queues;
    private SortedSet<String> users;

    private ChatServer() {
        users = new TreeSet<>();
        userSecureRepository = new UserSecureRepositoryInMemory();
        initializeData();
        queues = new HashMap<>();
        logger.log(java.util.logging.Level.INFO, "Starting server...");
        connections = new HashMap<>();

        try (ServerSocket serverSocket = new ServerSocket(8188)) {
            while (true) {
                try {
                    new TCPConnection(serverSocket.accept(), this);
                } catch (IOException e) {
                    logger.log(java.util.logging.Level.SEVERE, "Error accepting connection: " + e.getMessage());
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
        logger.log(java.util.logging.Level.INFO, "Client connected");
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
        logger.log(java.util.logging.Level.INFO, "Client disconnected");
    }

    @Override
    public synchronized void onException(TCPConnection tcpConnection, Exception e) {
        logger.log(java.util.logging.Level.SEVERE, e.getMessage());
    }

    @Override
    public void onAttemptAuth(TCPConnection tcpConnection, String loginPassword) throws IOException {
        logger.log(java.util.logging.Level.INFO, "Attempt auth");
        String login = loginPassword.split(":")[0].trim();
        String password = loginPassword.split(":")[1].trim();
        if (users.contains(login) || !userSecureRepository.checkAuth(login, password)) {
            throw new IOException("Auth failed");
        }
    }

    @Override
    public void onAuthSuccess(TCPConnection tcpConnection, String login) {
        logger.log(java.util.logging.Level.INFO, "Auth success");
        users.add(login);
        tcpConnection.authFailed("/auth_success");
        tcpConnection.authSuccess(login);
    }

    @Override
    public void onAuthFailed(TCPConnection tcpConnection, String error) {
        logger.log(java.util.logging.Level.INFO, "Auth failed");
        if (error != null) {
            tcpConnection.authFailed(error);
        }
    }

    private void sendMessage(String message) {
        Message mess = parseMessage(message);

        String connectionId = mess.getUserNameTo();

        if (connections.containsKey(connectionId)) {
            connections.get(connectionId).sendMessage(message);
        } else {
            addMessageInQueue(message, mess);
        }
    }

    private void addMessageInQueue(String gsonMessage, Message message) {
        String connectionId = message.getUserNameTo();
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
            logger.log(Level.WARNING, "Error while parsing message", e);
        }
        return mess;
    }

    private void initializeData() {

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
    }
}

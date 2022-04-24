package rocket_chat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import rocket_chat.entity.Chat;
import rocket_chat.entity.Message;
import rocket_chat.network.TCPConnection;
import rocket_chat.network.TCPConnectionListener;
import rocket_chat.repository.*;

import java.io.IOException;
import java.net.ConnectException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TcpListener implements TCPConnectionListener {
    private Logger logger = Logger.getLogger(TcpListener.class.getName());
    private Connection connection;
    private ChatRepository chatRepository;
    private UserRepository userRepository;

    public TcpListener() {
        connection = new Connection();
        chatRepository = new ChatRepositoryInMemory();
        userRepository = new UserRepositoryInMemory();
        createConnections();
    }

    @Override
    public void onConnected(TCPConnection tcpConnection, String login) {
    }

    @Override
    public void onReceiveMessage(TCPConnection tcpConnection, String message) {
        Message mess = null;
        try {
            mess = new ObjectMapper().readerFor(Message.class).readValue(message);
        } catch (JsonProcessingException e) {
            logger.log(Level.WARNING, "Error while parsing message", e);
        }
        if (Main.chatController != null && Main.chatController.getChat().getFriendUser().getUserLogin().equals(Objects.requireNonNull(mess).getUserNameFrom())) {
            Message finalMess = mess;
            Platform.runLater(() -> Main.chatController.addMessage(finalMess, false));
        } else {
            if (!chatRepository.chatExists(mess.getUserNameTo(), mess.getUserNameFrom())) {
                chatRepository.saveChat(new Chat(userRepository.getUserById(mess.getUserNameTo()),
                        userRepository.getUserById(mess.getUserNameFrom())));
            }
            chatRepository.addMessage(mess);
        }
    }

    @Override
    public void onDisconnect(TCPConnection tcpConnection, String login) {
        tcpConnection.disconnect();
        connection.remove();
    }

    @Override
    public void onException(TCPConnection tcpConnection, Exception e) {
        logger.log(Level.WARNING, "Exception");
        tcpConnection.disconnect();
        connection.remove();
        Main.isServerConnected = false;
    }

    @Override
    public void onAttemptAuth(TCPConnection tcpConnection, String login) {

    }

    @Override
    public void onAuthSuccess(TCPConnection tcpConnection, String login) {
        Platform.runLater(() -> {
            try {
                if (login == null) {
                    throw new IOException("Login is null");
                }
                Main.isServerConnected = true;
                Main.showChats(userRepository.getUserByUserLogin(login));
            } catch (IOException e) {
                logger.log(Level.WARNING, "Error while getting user");
            }
        });
    }

    @Override
    public void onAuthFailed(TCPConnection tcpConnection, String message) {
        if (message != null && message.equals("/auth_failed")) {
            showError("Username or password is incorrect");
        }
        if (message != null && message.equals("/attempts_over")) {
            showError("Attempts are over");
            Main.thread.interrupt();
        }
        if (message != null && message.equals("/timeout")) {
            showError("Connection timeout");
            Main.thread.interrupt();
        }
    }

    private void createConnections() {
        try {
            connection.addIfNotExists(this);
        } catch (ConnectException e) {
            Main.isServerConnected = false;
        } catch (IOException e) {
            logger.log(Level.WARNING, "Error");
        }
    }

    private void showError (String message) {
        Platform.runLater(() -> {
            try {
                Main.showError(message);
            } catch (IOException e) {
                logger.log(Level.WARNING, e.getMessage());
            }
        });
    }
}

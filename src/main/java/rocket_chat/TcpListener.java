package rocket_chat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import lombok.extern.slf4j.Slf4j;
import rocket_chat.dao.ChatDaoJDBC;
import rocket_chat.dao.UserDaoJDBC;
import rocket_chat.entity.Chat;
import rocket_chat.entity.Message;
import rocket_chat.network.TCPConnection;
import rocket_chat.network.TCPConnectionListener;
import rocket_chat.repository.*;
import rocket_chat.util.HistoryUtil;
import rocket_chat.util.TcpConnection;
import rocket_chat.view.utils.BackUrl;

import java.io.IOException;
import java.net.ConnectException;
import java.util.Objects;

@Slf4j
public class TcpListener implements TCPConnectionListener {
    private TcpConnection tcpConnection;
    private ChatRepository chatRepository;
    private UserRepository userRepository;

    public TcpListener() {
        tcpConnection = new TcpConnection();
        chatRepository = ChatDaoJDBC.getINSTANCE();
        userRepository = UserDaoJDBC.getINSTANCE();
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
            log.warn("Error while parsing message", e);
        }
        if (Main.chatController != null && Main.chatController.getChat().getFriendUser().getUserName().equals(Objects.requireNonNull(mess).getUserFrom().getUserName())) {
            Message finalMess = mess;
            Platform.runLater(() -> Main.chatController.addMessage(finalMess, false));
        } else {
            if (!chatRepository.chatExists(Objects.requireNonNull(mess).getUserTo().getUserName(), mess.getUserFrom().getUserName())) {
                chatRepository.saveChat(new Chat(mess.getUserTo(), mess.getUserFrom()));
            }
            Chat chat = chatRepository.getChatByOwnerIdAndFriendId(mess.getUserTo().getUserName(), mess.getUserFrom().getUserName());
            chat.addMessage(mess);
            HistoryUtil.saveMessage(Main.user.getUserName(), mess);
        }
    }

    @Override
    public void onDisconnect(TCPConnection tcpConnection, String login) {
        tcpConnection.disconnect();
        this.tcpConnection.remove();
    }

    @Override
    public void onException(TCPConnection tcpConnection, Exception e) {
        log.warn(e.getMessage());
        tcpConnection.disconnect();
        this.tcpConnection.remove();
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
                Main.showChats(userRepository.getUserByUserName(login));
            } catch (IOException e) {
                log.warn("Error while getting user");
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
            tcpConnection.addIfNotExists(this);
        } catch (ConnectException e) {
            Main.isServerConnected = false;
        } catch (IOException e) {
            log.warn("Error");
        }
    }

    private void showError(String message) {
        Platform.runLater(() -> Main.showError(message, BackUrl.LOGIN));
    }
}

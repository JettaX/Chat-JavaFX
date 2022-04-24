package rocket_chat.network;

import java.io.IOException;

public interface TCPConnectionListener {
    void onConnected(TCPConnection tcpConnection, String login);
    void onReceiveMessage(TCPConnection tcpConnection, String message);
    void onDisconnect(TCPConnection tcpConnection, String login);
    void onException(TCPConnection tcpConnection, Exception e);
    void onAttemptAuth(TCPConnection tcpConnection, String loginPassword) throws IOException;
    void onAuthSuccess(TCPConnection tcpConnection, String login);
    void onAuthFailed(TCPConnection tcpConnection, String message);
}

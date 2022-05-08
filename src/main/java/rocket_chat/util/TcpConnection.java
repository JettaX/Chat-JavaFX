package rocket_chat.util;

import rocket_chat.network.TCPConnection;
import rocket_chat.network.TCPConnectionListener;

import java.io.IOException;

public class TcpConnection {
    private static TCPConnection connection = null;

    public void addIfNotExists(TCPConnectionListener listener) throws IOException {
        if (connection == null) {
            connection = new TCPConnection(listener, "localhost", 8188);
        }
    }

    public TCPConnection get() {
        return connection;
    }

    public void remove() {
        connection.disconnect();
        connection = null;
    }

    public boolean isConnected() {
        return connection != null;
    }
}

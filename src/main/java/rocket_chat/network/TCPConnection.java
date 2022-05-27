package rocket_chat.network;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class TCPConnection {
    private final Socket socket;
    private Thread threadTimeout;
    private final TCPConnectionListener eventListener;
    private BufferedReader in;
    private BufferedWriter out;

    private ExecutorService executorService = Executors.newFixedThreadPool(5);

    public TCPConnection(TCPConnectionListener eventListener, String ip, int port) throws IOException {
        this.socket = new Socket(ip, port);
        this.eventListener = eventListener;
        initializer(socket);
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                String login = "";
                try {
                    String authStatus = "";
                    while (!authStatus.equals("/auth_success")) {
                        authStatus = in.readLine();
                        eventListener.onAuthFailed(TCPConnection.this, authStatus);
                        if (authStatus.equals("/attempts_over") || authStatus.equals("/timeout")) {
                            eventListener.onDisconnect(TCPConnection.this, authStatus);
                        }
                    }
                    login = in.readLine();
                    eventListener.onAuthSuccess(TCPConnection.this, login);
                    eventListener.onConnected(TCPConnection.this, login);
                    while (!executorService.isShutdown()) {
                        eventListener.onReceiveMessage(TCPConnection.this, in.readLine());
                    }
                } catch (
                        IOException e) {
                    eventListener.onException(TCPConnection.this, e);
                } finally {
                    eventListener.onDisconnect(TCPConnection.this, login);
                }
            }
        });
    }

    public TCPConnection(Socket socket, TCPConnectionListener eventListener) throws IOException {
        this.socket = socket;
        this.eventListener = eventListener;
        initializer(socket);
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                String login = null;
                int countError = 0;
                AtomicInteger timeout = new AtomicInteger();
                try {
                    threadTimeout = new Thread(() -> {
                        while (!threadTimeout.isInterrupted()) {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                break;
                            }
                            timeout.getAndIncrement();
                            if (timeout.get() == 120) {
                                eventListener.onAuthFailed(TCPConnection.this, "/timeout");
                                disconnect();
                                break;
                            }
                        }
                    });
                    threadTimeout.start();

                    while (!executorService.isShutdown()) {
                        try {
                            login = in.readLine();
                            eventListener.onAttemptAuth(TCPConnection.this, login);
                        } catch (IOException e) {
                            if (countError++ > 1) {
                                eventListener.onAuthFailed(TCPConnection.this, "/attempts_over");
                                disconnect();
                            }
                            eventListener.onAuthFailed(TCPConnection.this, "/auth_failed");
                            continue;
                        }
                        login = login.split(":")[0];
                        threadTimeout.interrupt();
                        eventListener.onAuthSuccess(TCPConnection.this, login);
                        eventListener.onConnected(TCPConnection.this, login);
                        break;
                    }
                    while (!executorService.isShutdown()) {
                        eventListener.onReceiveMessage(TCPConnection.this, in.readLine());
                    }
                } catch (
                        IOException e) {
                    eventListener.onException(TCPConnection.this, e);
                } finally {
                    eventListener.onDisconnect(TCPConnection.this, login);
                }
            }
        });
    }

    private void initializer(Socket socket) throws IOException {
        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
    }

    public synchronized void sendMessage(String message) {
        write(message);
    }

    public synchronized void sendLogin(String login, String password) {
        write(login.concat(":").concat(password));
    }

    public synchronized void authSuccess(String login) {
        write(login);
    }

    public synchronized void authFailed(String error) {
        write(error);
    }

    private synchronized void write(String message) {
        try {
            out.write(message + "\r\n");
            out.flush();
        } catch (IOException e) {
            eventListener.onException(TCPConnection.this, e);
            disconnect();
        }
    }

    public synchronized void disconnect() {
        executorService.shutdown();
        if (threadTimeout != null) {
            threadTimeout.interrupt();
        }
        try {
            socket.close();
        } catch (IOException e) {
            eventListener.onException(TCPConnection.this, e);
        }
    }
}

package rocket_chat.util;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public final class JdbcConnection {
    private static final String URL_KEY = "db.url";
    private static final String USER_KEY = "db.username";
    private static final String PASSWORD_KEY = "db.password";
    private static final String POOL_SIZE_KEY = "db.poolSize";
    private static final Integer DEFAULT_POOL_SIZE = 5;
    private static BlockingQueue<Connection> pool;
    private static List<Connection> connections;

    static {
        loadDriver();
        initConnectionPool();
    }

    private JdbcConnection() {
    }

    private static void initConnectionPool() {
        String poolSize = PropertiesUtil.getProperty(POOL_SIZE_KEY);
        Integer size = poolSize == null ? DEFAULT_POOL_SIZE : Integer.parseInt(poolSize);
        pool = new ArrayBlockingQueue<>(size);
        connections = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            Connection connection = open();
            Connection proxyInstance = (Connection) Proxy.newProxyInstance(
                    JdbcConnection.class.getClassLoader(), new Class[]{Connection.class}, (proxy, method, args) ->
                            method.getName().equals("close") ? pool.add((Connection) proxy) : method.invoke(connection, args));
            pool.add(proxyInstance);
            connections.add(connection);
        }
    }

    private static void loadDriver() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to load PostgreSQL driver", e);
        }
    }

    public static Connection getConnection() {
        try {
            return pool.take();
        } catch (InterruptedException e) {
            throw new RuntimeException("Failed to get connection", e);
        }
    }

    private static Connection open() {
        try {
            return DriverManager.getConnection(
                    PropertiesUtil.getProperty(URL_KEY),
                    PropertiesUtil.getProperty(USER_KEY),
                    PropertiesUtil.getProperty(PASSWORD_KEY)
            );
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get connection", e);
        }
    }

    public static void close(Connection connection) {
        for (Connection c : connections) {
            try {
                c.close();
            } catch (SQLException e) {
                throw new RuntimeException("Failed to close connection", e);
            }
        }
    }
}

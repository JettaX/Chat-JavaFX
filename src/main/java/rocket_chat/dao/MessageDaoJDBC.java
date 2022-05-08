package rocket_chat.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import rocket_chat.entity.Message;
import rocket_chat.util.JdbcConnection;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class MessageDaoJDBC implements MessageDao {
    @Getter
    private static final MessageDaoJDBC INSTANCE = new MessageDaoJDBC();
    private static ChatDaoJDBC chatDao = ChatDaoJDBC.getINSTANCE();
    private static UserDaoJDBC userDao = UserDaoJDBC.getINSTANCE();

    private static final String SAVE_MESSAGE = "INSERT INTO messages (chat_id, user_from_id, user_to_id, text, time) " +
            "VALUES (?, ?, ?, ?, ?)";
    private static final String GET_MESSAGES = "SELECT * FROM messages WHERE chat_id = ?";
    private static final String DELETE_MESSAGE = "DELETE FROM messages WHERE chat_id = ?";

    @Override
    public void save(Message message) {
        try (var connection = JdbcConnection.getConnection();
             var statement = connection.prepareStatement(SAVE_MESSAGE)) {
            statement.setLong(1, message.getChat().getId());
            statement.setLong(2, message.getUserFrom().getId());
            statement.setLong(3, message.getUserTo().getId());
            statement.setString(4, message.getText());
            statement.setTimestamp(5, Timestamp.valueOf(message.getTime()));
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public List<Message> findAllByChatId(Long chatId) {
        try (var connection = JdbcConnection.getConnection();
             var statement = connection.prepareStatement(GET_MESSAGES)) {
            statement.setLong(1, chatId);
            List<Message> messages = new ArrayList<>();
            var resultSet = statement.executeQuery();
            while (resultSet.next()) {
                messages.add(Message.builder()
                        .id(resultSet.getLong("id"))
                        .chat(chatDao.getChatById(resultSet.getLong("chat_id")))
                        .userFrom(userDao.getUserByID(resultSet.getLong("user_from_id")))
                        .userTo(userDao.getUserByID(resultSet.getLong("user_to_id")))
                        .text(resultSet.getString("text"))
                        .time(resultSet.getTimestamp("time").toLocalDateTime())
                        .build());
            }
            return messages;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteAllByChatId(Long chatId) {
        try (var connection = JdbcConnection.getConnection();
             var statement = connection.prepareStatement(DELETE_MESSAGE)) {
            statement.setLong(1, chatId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

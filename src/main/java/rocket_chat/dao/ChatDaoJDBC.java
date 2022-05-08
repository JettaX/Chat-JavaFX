package rocket_chat.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import rocket_chat.entity.Chat;
import rocket_chat.entity.Message;
import rocket_chat.entity.User;
import rocket_chat.repository.ChatRepository;
import rocket_chat.util.JdbcConnection;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class ChatDaoJDBC implements ChatRepository {
    @Getter
    private static final ChatDaoJDBC INSTANCE = new ChatDaoJDBC();
    private static UserDaoJDBC userDaoJDBC = UserDaoJDBC.getINSTANCE();
    private static MessageDaoJDBC messageDaoJDBC = MessageDaoJDBC.getINSTANCE();
    private final static String SAVE_CHAT = "INSERT INTO chats (owner_user_id, friend_user_id) VALUES (?, ?)";
    private final static String GET_ALL_CHATS_BY_ID = "SELECT * FROM chats WHERE owner_user_id = ?";
    private final static String GET_CHAT_BY_ID_AND_FRIEND_ID =
            "SELECT * FROM chats WHERE owner_user_id = ? AND friend_user_id = ?";
    private final static String DELETE_CHAT = "DELETE FROM chats WHERE owner_user_id = ? AND friend_user_id = ?";
    private final static String GET_CHAT_BY_ID = "SELECT * FROM chats WHERE id = ?";


    @Override
    public Chat saveChat(Chat chat) {
        try (var connection = JdbcConnection.getConnection();
             var statement = connection.prepareStatement(SAVE_CHAT, Statement.RETURN_GENERATED_KEYS)) {
            var ownerUser = userDaoJDBC.getUserByUserName(chat.getOwnerUser().getUserName());
            var friendUser = userDaoJDBC.getUserByUserName(chat.getFriendUser().getUserName());
            statement.setLong(1, ownerUser.getId());
            statement.setLong(2, friendUser.getId());
            statement.executeUpdate();
            var generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                chat.setId(generatedKeys.getLong("id"));
            }
            return chat;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Chat getChatById(Long chatId) {
        try (var connection = JdbcConnection.getConnection();
             var statement = connection.prepareStatement(GET_CHAT_BY_ID)) {
            statement.setLong(1, chatId);
            var resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Chat.builder()
                        .id(resultSet.getLong("id"))
                        .ownerUser(userDaoJDBC.getUserByID(resultSet.getLong("owner_user_id")))
                        .friendUser(userDaoJDBC.getUserByID(resultSet.getLong("friend_user_id")))
                        /*.messages(messageDaoJDBC.findAllByChatId(resultSet.getLong("id")))*/
                        .build();
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Chat> getAllChatsByUserLogin(String userLogin) {
        try (var connection = JdbcConnection.getConnection();
             var statement = connection.prepareStatement(GET_ALL_CHATS_BY_ID)) {
            User user = userDaoJDBC.getUserByUserName(userLogin);
            statement.setLong(1, user.getId());
            List<Chat> chats = new ArrayList<>();
            var resultSet = statement.executeQuery();
            while (resultSet.next()) {
                chats.add(Chat.builder()
                        .id(resultSet.getLong("id"))
                        .ownerUser(userDaoJDBC.getUserByID(resultSet.getLong("owner_user_id")))
                        .friendUser(userDaoJDBC.getUserByID(resultSet.getLong("friend_user_id")))
                        .messages(messageDaoJDBC.findAllByChatId(resultSet.getLong("id")))
                        .build());
            }
            return chats;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Chat getChatByOwnerIdAndFriendId(String ownerUserName, String friendUserName) {
        try (var connection = JdbcConnection.getConnection();
             var statement = connection.prepareStatement(GET_CHAT_BY_ID_AND_FRIEND_ID)) {
            User ownerUser = userDaoJDBC.getUserByUserName(ownerUserName);
            User friendUser = userDaoJDBC.getUserByUserName(friendUserName);
            statement.setLong(1, ownerUser.getId());
            statement.setLong(2, friendUser.getId());
            var resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Chat.builder()
                        .id(resultSet.getLong("id"))
                        .ownerUser(ownerUser)
                        .friendUser(friendUser)
                        .messages(messageDaoJDBC.findAllByChatId(resultSet.getLong("id")))
                        .build();
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addMessage(Message message) {
        messageDaoJDBC.save(message);
    }

    @Override
    public boolean deleteChatByUserIdAndFriendId(String ownerUserName, String friendUserName) {
        try (var connection = JdbcConnection.getConnection();
             var statement = connection.prepareStatement(DELETE_CHAT)) {
            User ownerUser = userDaoJDBC.getUserByUserName(ownerUserName);
            User friendUser = userDaoJDBC.getUserByUserName(friendUserName);
            statement.setLong(1, ownerUser.getId());
            statement.setLong(2, friendUser.getId());
            if (statement.executeUpdate() > 0) {
                messageDaoJDBC.deleteAllByChatId(getChatByOwnerIdAndFriendId(ownerUserName, friendUserName).getId());
                return true;
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean chatExists(String ownerUserName, String friendUserName) {
        try (var connection = JdbcConnection.getConnection();
             var statement = connection.prepareStatement(GET_CHAT_BY_ID_AND_FRIEND_ID)) {
            User ownerUser = userDaoJDBC.getUserByUserName(ownerUserName);
            User friendUser = userDaoJDBC.getUserByUserName(friendUserName);
            statement.setLong(1, ownerUser.getId());
            statement.setLong(2, friendUser.getId());
            var resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

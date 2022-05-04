package rocket_chat.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import rocket_chat.entity.Chat;
import rocket_chat.entity.Message;
import rocket_chat.entity.User;
import rocket_chat.repository.ChatRepository;
import rocket_chat.util.JdbcConnection;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class ChatDao implements ChatRepository {
    @Getter
    private static final ChatDao INSTANCE = new ChatDao();
    private static UserDao userDao = UserDao.getINSTANCE();
    private final static String SAVE_CHAT = "INSERT INTO chats (owner_user_id, friend_user_id) VALUES (?, ?)";
    private final static String GET_ALL_CHATS_BY_ID = "SELECT * FROM chats WHERE owner_user_id = ?";


    @Override
    public void saveChat(Chat chat) {
        try (var connection = JdbcConnection.getConnection();
             var statement = connection.prepareStatement(SAVE_CHAT)) {
            statement.setLong(1, chat.getOwnerUser().getId());
            statement.setLong(2, chat.getFriendUser().getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public List<Chat> getAllChatsByUserLogin(String userLogin) {
        try (var connection = JdbcConnection.getConnection();
             var statement = connection.prepareStatement(SAVE_CHAT)) {
            User user = userDao.getUserByUserName(userLogin);
            statement.setLong(1, user.getId());
            List<Chat> chats = new ArrayList<>();
            var resultSet = statement.executeQuery();
            while (resultSet.next()) {
                chats.add(Chat.builder()
                        .ownerUser(user)
                        .friendUser(userDao.getUserByID(resultSet.getLong("friend_user_id")))
                        .build());
            }
            return chats;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Chat getChatByOwnerIdAndFriendId(String ownerUserName, String friendUserName) {
        return null;
    }

    @Override
    public void addMessage(Message message) {

    }

    @Override
    public boolean deleteChatByUserIdAndFriendId(String ownerUserName, String friendUserName) {
        return false;
    }

    @Override
    public boolean chatExists(String ownerUserName, String friendUserName) {
        return false;
    }
}

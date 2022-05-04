package rocket_chat.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import rocket_chat.entity.UserSecure;
import rocket_chat.repository.UserSecureRepository;
import rocket_chat.util.JdbcConnection;

import java.sql.SQLException;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class UserSecureDao implements UserSecureRepository {

    @Getter
    private static final UserSecureDao INSTANCE = new UserSecureDao();
    private static final String GET_BY_USERNAME_AND_PASSWORD =
            "SELECT * FROM user_secure WHERE user_login = ? AND user_password = ?";
    private static final String SAVE =
            "INSERT INTO user_secure (user_login, user_password) VALUES (?, ?)";
    private static final String UPDATE_LOGIN =
            "UPDATE user_secure SET user_login = ? WHERE user_login = ?";

    @Override
    public void createUserSecure(String login, String password) {
        try (var connection = JdbcConnection.getConnection();
             var statement = connection.prepareStatement(SAVE)) {
            statement.setString(1, login);
            statement.setString(2, password);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void createUserSecure(UserSecure userSecure) {
        createUserSecure(userSecure.getUserLogin(), userSecure.getUserPassword());
    }

    @Override
    public boolean checkAuth(String login, String password) {
        try (var connection = JdbcConnection.getConnection();
             var statement = connection.prepareStatement(GET_BY_USERNAME_AND_PASSWORD)) {
            statement.setString(1, login);
            statement.setString(2, password);
            var resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean updateLogin(String login, String newLogin) {
        try (var connection = JdbcConnection.getConnection();
             var statement = connection.prepareStatement(UPDATE_LOGIN)) {
            statement.setString(1, newLogin);
            statement.setString(2, login);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

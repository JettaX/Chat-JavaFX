package rocket_chat.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import rocket_chat.entity.User;
import rocket_chat.repository.UserRepository;
import rocket_chat.util.JdbcConnection;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class UserDaoJDBC implements UserRepository {
    @Getter
    private static final UserDaoJDBC INSTANCE = new UserDaoJDBC();
    private static final String SAVE_USER = "INSERT INTO users (" +
            "user_name, first_name, last_name, image_path) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_USER = "UPDATE users SET " +
            "user_name = ?, first_name = ?, last_name = ?, image_path = ? WHERE id = ?";
    private static final String DELETE_USER = "DELETE FROM users WHERE user_name = ?";
    private static final String GET_ALL_USERS = "SELECT * FROM users";
    private static final String GET_USER_BY_USERNAME = "SELECT * FROM users WHERE user_name = ?";
    private static final String SEARCH_USER_BY_USERNAME = "SELECT * FROM users WHERE lower(user_name) LIKE ? ";
    private static final String GET_USER_BY_ID = "SELECT * FROM users WHERE id = ?";

    @Override
    public User saveUser(User user) {
        try (var connection = JdbcConnection.getConnection();
             var statement = connection.prepareStatement(SAVE_USER, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, user.getUserName());
            statement.setString(2, user.getFirstName());
            statement.setString(3, user.getLastName());
            statement.setString(4, user.getImagePath());
            statement.executeUpdate();
            var resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                user.setId(resultSet.getLong("id"));
            }
            return user;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateUser(User oldUser, User newUser) {
        try (var connection = JdbcConnection.getConnection();
             var statement = connection.prepareStatement(UPDATE_USER)) {
            statement.setString(1, newUser.getUserName());
            statement.setString(2, newUser.getFirstName());
            statement.setString(3, newUser.getLastName());
            statement.setString(4, newUser.getImagePath());
            statement.setLong(5, oldUser.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public User getUserByUserName(String userName) {
        try (var connection = JdbcConnection.getConnection();
             var statement = connection.prepareStatement(GET_USER_BY_USERNAME)) {
            statement.setString(1, userName);
            var resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return User.builder()
                        .id(resultSet.getLong("id"))
                        .userName(resultSet.getString("user_name"))
                        .firstName(resultSet.getString("first_name"))
                        .lastName(resultSet.getString("last_name"))
                        .imagePath(resultSet.getString("image_path"))
                        .build();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public User getUserByID(Long id) {
        try (var connection = JdbcConnection.getConnection();
             var statement = connection.prepareStatement(GET_USER_BY_ID)) {
            statement.setLong(1, id);
            var resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return User.builder()
                        .id(resultSet.getLong("id"))
                        .userName(resultSet.getString("user_name"))
                        .firstName(resultSet.getString("first_name"))
                        .lastName(resultSet.getString("last_name"))
                        .imagePath(resultSet.getString("image_path"))
                        .build();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public List<User> searchUser(String userName) {
        try (var connection = JdbcConnection.getConnection();
             var statement = connection.prepareStatement(SEARCH_USER_BY_USERNAME)) {
            statement.setString(1, "%" + userName.toLowerCase() + "%");
            var resultSet = statement.executeQuery();
            var users = new ArrayList<User>();
            while (resultSet.next()) {
                users.add(User.builder()
                        .id(resultSet.getLong("id"))
                        .userName(resultSet.getString("user_name"))
                        .firstName(resultSet.getString("first_name"))
                        .lastName(resultSet.getString("last_name"))
                        .imagePath(resultSet.getString("image_path"))
                        .build());
            }
            return users;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<User> getUsers() {
        try (var connection = JdbcConnection.getConnection();
             var statement = connection.prepareStatement(GET_ALL_USERS)) {
            var resultSet = statement.executeQuery();
            var users = new ArrayList<User>();
            while (resultSet.next()) {
                users.add(
                        User.builder()
                                .id(resultSet.getLong("id"))
                                .userName(resultSet.getString("user_name"))
                                .firstName(resultSet.getString("first_name"))
                                .lastName(resultSet.getString("last_name"))
                                .imagePath(resultSet.getString("image_path"))
                                .build());
            }
            return users;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteUserById(String userName) {
        try (var connection = JdbcConnection.getConnection();
             var statement = connection.prepareStatement(DELETE_USER)) {
            statement.setString(1, userName);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

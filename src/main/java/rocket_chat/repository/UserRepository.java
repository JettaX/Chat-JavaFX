package rocket_chat.repository;

import rocket_chat.entity.User;

import java.util.List;

public interface UserRepository {

    public User saveUser(User user);

    public void updateUser(User oldUser, User newUser);

    public User getUserByUserName(String userName);

    public User getUserByID(Long id);

    public List<User> searchUser(String userName);

    public List<User> getUsers();

    public void deleteUserById(String userName);

}

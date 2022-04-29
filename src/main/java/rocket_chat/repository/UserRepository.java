package rocket_chat.repository;

import rocket_chat.entity.User;

import java.util.List;

public interface UserRepository {

    public void saveUser(User user);

    public User getUserById(String userName);

    public List<User> getUsers();

    public void deleteUserById(String userName);

}

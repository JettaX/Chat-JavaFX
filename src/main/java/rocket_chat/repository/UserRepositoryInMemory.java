package rocket_chat.repository;

import rocket_chat.entity.User;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class UserRepositoryInMemory implements UserRepository {

    public static List<User> list = new CopyOnWriteArrayList<>();

    public void saveUser(User user) {
        list.add(user);
    }

    public User getUserById(String userName) {
        for (User user : list) {
            if (user.getUserName().equals(userName)) {
                return user;
            }
        }
        return null;
    }

    public List<User> getUsers() {
        return list;
    }

    public void deleteUserById(String userName) {
        list.removeIf(user -> user.getUserName().equals(userName));
    }
}

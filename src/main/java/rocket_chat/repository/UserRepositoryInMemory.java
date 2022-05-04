package rocket_chat.repository;

import rocket_chat.entity.User;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

public class UserRepositoryInMemory implements UserRepository {

    public static List<User> list = new CopyOnWriteArrayList<>();

    public void saveUser(User user) {
        list.add(user);
    }

    @Override
    public void updateUser(User oldUser, User newUser) {
        for (User us : list) {
            if (us.getUserName().equals(oldUser.getUserName())) {
                us.setUserName(newUser.getUserName());
                us.setFirstName(newUser.getFirstName());
                us.setLastName(newUser.getLastName());
            }
        }
    }

    public User getUserByUserName(String userName) {
        for (User user : list) {
            if (user.getUserName().equals(userName)) {
                return user;
            }
        }
        return null;
    }

    @Override
    public User getUserByID(Long id) {
        return null;
    }

    @Override
    public List<User> searchUser(String userName) {
        Set<User> findUsers = new HashSet<>();
        list.parallelStream().filter(user -> user.getUserName().toLowerCase().startsWith(userName.toLowerCase())).forEach(findUsers::add);
        return new ArrayList<>(findUsers);
    }

    public List<User> getUsers() {
        return list;
    }

    public void deleteUserById(String userName) {
        list.removeIf(user -> user.getUserName().equals(userName));
    }
}

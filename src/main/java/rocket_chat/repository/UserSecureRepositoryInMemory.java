package rocket_chat.repository;

import rocket_chat.entity.UserSecure;

import java.util.ArrayList;
import java.util.List;

public class UserSecureRepositoryInMemory implements UserSecureRepository {
    private static List<UserSecure> list = new ArrayList<>();

    public void createUserSecure(String login, String password) {
        list.add(new UserSecure(login, password));
    }

    public void createUserSecure(UserSecure userSecure) {
        list.add(userSecure);
    }

    public boolean checkAuth(String login, String password) {
        for (UserSecure user : list) {
            if (user.getUserLogin().equals(login)) {
                return user.getUserPassword().equals(password);
            }
        }
        return false;
    }

    @Override
    public boolean updateLogin(String login, String newLogin) {
        return false;
    }
}

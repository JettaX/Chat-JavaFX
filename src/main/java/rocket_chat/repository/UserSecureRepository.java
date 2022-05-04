package rocket_chat.repository;

import rocket_chat.entity.UserSecure;

public interface UserSecureRepository {

    public void createUserSecure(String login, String password);

    public void createUserSecure(UserSecure userSecure);

    public boolean checkAuth(String login, String password);

    public boolean updateLogin(String login, String newLogin);
}

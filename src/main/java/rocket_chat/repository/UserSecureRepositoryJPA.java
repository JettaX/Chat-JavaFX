package rocket_chat.repository;

import jakarta.persistence.Query;
import rocket_chat.entity.UserSecure;

public class UserSecureRepositoryJPA implements UserSecureRepository {

    @Override
    public void createUserSecure(String login, String password) {
        HibernateSession.getSession().beginTransaction();
        UserSecure userSecure = new UserSecure(login, password);
        HibernateSession.getSession().persist(userSecure);
        HibernateSession.getSession().getTransaction().commit();
    }

    @Override
    public void createUserSecure(UserSecure userSecure) {
        HibernateSession.getSession().beginTransaction();
        HibernateSession.getSession().persist(userSecure);
        HibernateSession.getSession().getTransaction().commit();
    }

    @Override
    public boolean checkAuth(String login, String password) {
        HibernateSession.getSession().beginTransaction();
        Query query =
                HibernateSession.getSession().createQuery("select u from UserSecure u where u.userLogin = ?1", UserSecure.class);
        query.setParameter(1, login);
        HibernateSession.getSession().getTransaction().commit();
        UserSecure userSecure = (UserSecure) query.getSingleResult();
        return userSecure.getUserPassword().equals(password);
    }
}

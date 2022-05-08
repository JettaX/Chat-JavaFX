package rocket_chat.repository;

import rocket_chat.entity.User;
import rocket_chat.util.HibernateSession;

import java.util.List;

public class UserRepositoryJPA implements UserRepository {
    @Override
    public User saveUser(User user) {
        HibernateSession.getSession().beginTransaction();
        HibernateSession.getSession().persist(user);
        HibernateSession.getSession().getTransaction().commit();
        return user;
    }

    @Override
    public void updateUser(User oldUser, User newUser) {
        /*HibernateSession.getSession().beginTransaction();
        HibernateSession.getSession().merge(user);
        HibernateSession.getSession().getTransaction().commit();
        return user;*/
    }

    @Override
    public User getUserByUserName(String userName) {
        HibernateSession.getSession().beginTransaction();
        User user = HibernateSession.getSession().get(User.class, userName);
        HibernateSession.getSession().getTransaction().commit();
        return user;
    }

    @Override
    public User getUserByID(Long id) {
        return null;
    }

    @Override
    public List<User> searchUser(String userName) {
        HibernateSession.getSession().beginTransaction();
        List<User> users = HibernateSession.getSession()
                .createQuery("select u from User u where u.userName = ?1", User.class)
                .setParameter(1, userName)
                .getResultList();
        HibernateSession.getSession().getTransaction().commit();
        return users;
    }

    @Override
    public List<User> getUsers() {
        HibernateSession.getSession().beginTransaction();
        List<User> users = HibernateSession.getSession().createQuery("select u from User u", User.class).getResultList();
        HibernateSession.getSession().getTransaction().commit();
        return users;
    }

    @Override
    public void deleteUserById(String userName) {
        HibernateSession.getSession().beginTransaction();
        User user = HibernateSession.getSession().get(User.class, userName);
        HibernateSession.getSession().remove(user);
        HibernateSession.getSession().getTransaction().commit();
    }
}

package rocket_chat.repository;

import rocket_chat.entity.User;

import java.util.List;

public class UserRepositoryJPA implements UserRepository {
    @Override
    public void saveUser(User user) {
        HibernateSession.getSession().beginTransaction();
        HibernateSession.getSession().persist(user);
        HibernateSession.getSession().getTransaction().commit();
    }

    @Override
    public User getUserById(String userName) {
        HibernateSession.getSession().beginTransaction();
        User user = HibernateSession.getSession().get(User.class, userName);
        HibernateSession.getSession().getTransaction().commit();
        return user;
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

package rocket_chat;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import rocket_chat.entity.User;
import rocket_chat.util.HibernateCfg;

public class HibernateRunner {
    public static void main(String[] args) {
        User user = User.builder()
                .userName("user")
                .firstName("first")
                .lastName("last")
                .imagePath("image")
                .build();

        try (SessionFactory sessionFactory = HibernateCfg.buildSessionFactory();
             Session session = sessionFactory.openSession()) {

            session.beginTransaction();


            session.persist(user);
            session.getTransaction().commit();
        }
    }
}

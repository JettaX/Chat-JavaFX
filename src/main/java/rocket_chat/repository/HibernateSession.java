package rocket_chat.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import rocket_chat.util.HibernateCfg;

public class HibernateSession {
    private static SessionFactory sessionFactory = HibernateCfg.buildSessionFactory();
    private static Session session = sessionFactory.openSession();

    public static Session getSession() {
        if (!session.isOpen()) {
            session = sessionFactory.openSession();
        }
        return session;
    }
}

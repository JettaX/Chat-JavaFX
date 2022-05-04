package rocket_chat.util;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

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

package rocket_chat.util;

import lombok.experimental.UtilityClass;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

@UtilityClass
public class HibernateCfg {

    public static SessionFactory buildSessionFactory() {
        Configuration cfg = new Configuration();
        cfg.configure();
        return cfg.buildSessionFactory();
    }
}

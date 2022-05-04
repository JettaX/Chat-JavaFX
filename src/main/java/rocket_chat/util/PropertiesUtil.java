package rocket_chat.util;

import java.io.IOException;
import java.util.Properties;

public final class PropertiesUtil {

    private static final Properties PROPERTIES = new Properties();

    static {
        try (var inputStream = PropertiesUtil.class.getClassLoader().getResourceAsStream("application.properties")) {
            PROPERTIES.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private PropertiesUtil() {
    }

    public static String getProperty(String key) {
        return PROPERTIES.getProperty(key);
    }
}

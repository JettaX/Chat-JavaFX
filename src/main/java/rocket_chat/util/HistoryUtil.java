package rocket_chat.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import rocket_chat.entity.Message;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HistoryUtil {
    private static final String SEPARATOR = "->";
    public static final String HISTORY_DIRECTORY = "src/main/resources/history";

    public static void saveMessage(String owner, Message message) {
        String friend = owner.equals(message.getUserFrom().getUserName()) ?
                message.getUserTo().getUserName() : message.getUserFrom().getUserName();
        String path = HISTORY_DIRECTORY.concat("/").concat(owner);
        String fileName = friend.concat(".txt");
        String content = message.getUserFrom().getUserName()
                .concat(SEPARATOR)
                .concat(message.getText())
                .concat("\n");
        FileUtil.writeFile(path, fileName, content);
    }

    public static List<String> getHistory(String owner, String friend) {
        String path = HISTORY_DIRECTORY.concat("/").concat(owner);
        String fileName = friend.concat(".txt");
        return FileUtil.readFile(path, fileName);
    }

    public static List<String> getAllHistory(String owner) {
        String path = HISTORY_DIRECTORY.concat("/").concat(owner);
        return FileUtil.getAllFilesName(path);
    }
}

package rocket_chat.dao;

import rocket_chat.entity.Message;

import java.util.List;

public interface MessageDao {
    void save(Message message);
    List<Message> findAllByChatId(Long chatId);
    void deleteAllByChatId(Long chatId);
}

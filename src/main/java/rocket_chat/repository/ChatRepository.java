package rocket_chat.repository;

import rocket_chat.entity.Chat;
import rocket_chat.entity.Message;

import java.util.List;

public interface ChatRepository {
    public Chat saveChat(Chat chat);

    public Chat getChatById(Long chatId);

    public List<Chat> getAllChatsByUserLogin(String userLogin);

    public Chat getChatByOwnerIdAndFriendId(String ownerUserName, String friendUserName);

    public void addMessage(Message message);

    public boolean deleteChatByUserIdAndFriendId(String ownerUserName, String friendUserName);

    public boolean chatExists(String ownerUserName, String friendUserName);
}

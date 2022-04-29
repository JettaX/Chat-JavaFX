package rocket_chat.repository;

import rocket_chat.entity.Chat;
import rocket_chat.entity.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChatRepositoryInMemory implements ChatRepository {
    public static List<Chat> chatList = new CopyOnWriteArrayList<>();


    public void saveChat(Chat chat) {
        chatList.add(chat);
    }

    public List<Chat> getAllChatsByUserLogin(String userLogin) {
        List<Chat> list = new ArrayList<>();
        chatList.forEach(chat -> {
            if (chat.getOwnerUser().getUserName().equals(userLogin)) {
                list.add(chat);
            }
        });
        return list;
    }

    public Chat getChatByOwnerIdAndFriendId(String ownerUserName, String friendUserName) {
        for (Chat chat : chatList) {
            if (chat.getOwnerUser().getUserName().equals(ownerUserName)) {
                if (chat.getFriendUser().getUserName().equals(friendUserName)) {
                    return chat;
                }
            }
        }
        return null;
    }

    public void addMessage(Message message) {
        chatList.forEach(chat -> {
            if (chat.getOwnerUser().getUserName().equals(message.getUserFrom().getUserName()) ||
                    chat.getOwnerUser().getUserName().equals(message.getUserTo().getUserName())) {
                if (chat.getFriendUser().getUserName().equals(message.getUserTo().getUserName()) ||
                        chat.getFriendUser().getUserName().equals(message.getUserFrom().getUserName())) {
                    chat.addMessage(message);
                }
            }
        });
    }

    public boolean deleteChatByUserIdAndFriendId(String ownerUserName, String friendUserName) {
        for (Chat chat : chatList) {
            if (chat.getOwnerUser().getUserName().equals(ownerUserName)) {
                if (chat.getFriendUser().getUserName().equals(friendUserName)) {
                    return chatList.remove(chat);
                }
            }
        }
        return false;
    }

    @Override
    public boolean chatExists(String ownerUserName, String friendUserName) {
        for (Chat chat : chatList) {
            if (chat.getOwnerUser().getUserName().equals(ownerUserName)) {
                if (chat.getFriendUser().getUserName().equals(friendUserName)) {
                    return true;
                }
            }
        }
        return false;
    }


}

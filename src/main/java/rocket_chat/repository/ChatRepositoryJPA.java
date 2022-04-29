package rocket_chat.repository;

import rocket_chat.Main;
import rocket_chat.entity.Chat;
import rocket_chat.entity.Message;

import java.util.List;

public class ChatRepositoryJPA implements ChatRepository {

    @Override
    public void saveChat(Chat chat) {
        HibernateSession.getSession().beginTransaction();
        HibernateSession.getSession().persist(chat);
        HibernateSession.getSession().getTransaction().commit();
    }

    @Override
    public List<Chat> getAllChatsByUserLogin(String userLogin) {
        HibernateSession.getSession().beginTransaction();
        List<Chat> chats = HibernateSession.getSession().createQuery("select c from Chat c where c.ownerUser.userName = ?1",
                        Chat.class)
                .setParameter(1, userLogin).getResultList();
        HibernateSession.getSession().getTransaction().commit();
        return chats;
    }

    @Override
    public Chat getChatByOwnerIdAndFriendId(String ownerUserName, String friendUserName) {
        HibernateSession.getSession().beginTransaction();
        Chat chats = HibernateSession.getSession().createQuery("select c from Chat c where c.ownerUser.userName = ?1 and c" +
                        ".friendUser.userName = ?2", Chat.class)
                .setParameter(1, ownerUserName).setParameter(2, friendUserName).getSingleResult();
        HibernateSession.getSession().getTransaction().commit();
        return chats;
    }

    @Override
    public void addMessage(Message message) {
        boolean isOwner = Main.user.getUserName().equals(message.getUserFrom().getUserName());
        HibernateSession.getSession().beginTransaction();
        Chat chat = HibernateSession.getSession().createQuery(
                        "select c from Chat c where c.ownerUser.userName = ?1 and c.friendUser.userName = ?2", Chat.class)
                .setParameter(isOwner ? 1 : 2, message.getUserFrom().getUserName())
                .setParameter(isOwner ? 2 : 1, message.getUserTo().getUserName())
                .getSingleResult();
        chat.addMessage(message);
        HibernateSession.getSession().getTransaction().commit();
    }

    @Override
    public boolean deleteChatByUserIdAndFriendId(String ownerUserName, String friendUserName) {
        HibernateSession.getSession().beginTransaction();
        Chat chats = HibernateSession.getSession().createQuery("select c from Chat c where c.ownerUser.userName = ?1 and c" +
                        ".friendUser.userName = ?2", Chat.class)
                .setParameter(1, ownerUserName).setParameter(2, friendUserName).getSingleResult();
        HibernateSession.getSession().remove(chats);
        HibernateSession.getSession().getTransaction().commit();
        return chats == null;
    }

    @Override
    public boolean chatExists(String ownerUserName, String friendUserName) {
        HibernateSession.getSession().beginTransaction();
        Chat chats = HibernateSession.getSession().createQuery("select c from Chat c where c.ownerUser.userName = ?1 and c" +
                        ".friendUser.userName = ?2", Chat.class)
                .setParameter(1, ownerUserName).setParameter(2, friendUserName).getSingleResult();
        HibernateSession.getSession().getTransaction().commit();
        return chats != null;
    }
}

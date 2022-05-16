package rocket_chat.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import rocket_chat.dao.ChatDaoJDBC;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@Entity
@Table(name = "chats")
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "owner_user_id")
    private User ownerUser;
    @ManyToOne
    @JoinColumn(name = "friend_user_id")
    private User friendUser;
    @OneToMany(mappedBy = "chat", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore
    @ToString.Exclude
    private List<Message> messages = new ArrayList<>();

    public Chat(User ownerUser, User friendUser) {
        this.ownerUser = ownerUser;
        this.friendUser = friendUser;
    }
    public void addMessage(Message message) {
        messages.add(message);
        message.setChat(this);
        ChatDaoJDBC.getINSTANCE().addMessage(message);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Chat chat = (Chat) o;
        return ownerUser.equals(chat.ownerUser) && friendUser.equals(chat.friendUser);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ownerUser, friendUser);
    }
}

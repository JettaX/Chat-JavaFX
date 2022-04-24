package rocket_chat.entity;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@JsonPropertyOrder({"ownerUser", "friendUser", "messages"})
@AllArgsConstructor
@NoArgsConstructor
public class Chat {
    @Getter
    private String id;
    @Getter
    private User ownerUser;
    @Getter
    private User friendUser;
    @Getter
    private List<Message> messages = new ArrayList<>();

    public Chat(User ownerUser, User friendUser) {
        this.ownerUser = ownerUser;
        this.friendUser = friendUser;
    }

    public void addMessage(Message message) {
        messages.add(message);
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

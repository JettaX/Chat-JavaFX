package rocket_chat.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;
    @ManyToOne
    @JoinColumn(name = "chat_id")
    private Chat chat;
    @ManyToOne
    @JoinColumn(name = "user_from_id")
    private User userFrom;
    @ManyToOne
    @JoinColumn(name = "user_to_id")
    private User userTo;
    @Column(name = "text")
    private String text;
    @Column(name = "time")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime time;

    public Message(Chat chat, User userFrom, User userTo, String text) {
        this.chat = chat;
        this.userFrom = userFrom;
        this.userTo = userTo;
        this.text = text;
        this.time = LocalDateTime.now();
    }

    @JsonCreator
    public Message(
            @JsonProperty("userFrom")
            User userFrom,
            @JsonProperty("userTo")
            User userTo,
            @JsonProperty("text")
            String text,
            @JsonProperty("time")
            LocalDateTime time) {
        this.userFrom = userFrom;
        this.userTo = userTo;
        this.text = text;
        this.time = time;
    }
}

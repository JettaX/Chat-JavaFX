package rocket_chat.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
public class Message {
    @Getter
    private String userNameFrom;
    @Getter
    private String userNameTo;
    @Getter
    @Setter
    private String text;
    @Getter
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime time;

    public Message(String userNameFrom, String userNameTo, String text) {
        this.userNameFrom = userNameFrom;
        this.userNameTo = userNameTo;
        this.text = text;
        this.time = LocalDateTime.now();
    }

    @JsonCreator
    public Message(
            @JsonProperty("userNameFrom")
                    String userNameFrom,
            @JsonProperty("userNameTo")
                    String userNameTo,
            @JsonProperty("text")
                    String text,
            @JsonProperty("time")
                    LocalDateTime time) {
        this.userNameFrom = userNameFrom;
        this.userNameTo = userNameTo;
        this.text = text;
        this.time = time;
    }
}

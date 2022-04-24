package rocket_chat.entity;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonPropertyOrder({"userLogin", "id", "name", "surname"})
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Getter
    private String userLogin;
    @Getter
    @Setter
    private String name;
    @Getter
    @Setter
    private String surname;
    @Getter
    @Setter
    private String imagePath;

    public User(String userLogin, String name, String surname) {
        this.name = name;
        this.surname = surname;
        this.userLogin = userLogin;
        imagePath = "/images/default_icon.png";
    }
}

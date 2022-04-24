package rocket_chat.entity;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@JsonPropertyOrder({"userLogin", "userPassword"})
@NoArgsConstructor
public class UserSecure {
    @Getter
    private String userLogin;
    @Getter
    String userPassword;

    public UserSecure(String userLogin, String userPassword) {
        this.userLogin = userLogin;
        this.userPassword = userPassword;
    }
}

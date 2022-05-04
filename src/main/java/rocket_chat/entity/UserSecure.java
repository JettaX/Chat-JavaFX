package rocket_chat.entity;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@JsonPropertyOrder({"userLogin", "userPassword"})
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@ToString
@Table(name = "user_secure")
public class UserSecure {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_login")
    private String userLogin;
    @Column(name = "user_password")
    private String userPassword;

    public UserSecure(String userLogin, String userPassword) {
        this.userLogin = userLogin;
        this.userPassword = userPassword;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserSecure that = (UserSecure) o;
        return Objects.equals(userLogin, that.userLogin) && Objects.equals(userPassword, that.userPassword);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

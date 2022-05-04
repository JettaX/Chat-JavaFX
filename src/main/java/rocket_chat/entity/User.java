package rocket_chat.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
@Entity
@Table(name = "users")
public class User {
    @Id @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_name")
    private String userName;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "image_path")
    private String imagePath;

    public User(String userName, String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        imagePath = "/images/default_icon.png";
    }

    @JsonCreator
    public User(
            @JsonProperty("userName")
            String userName,
            @JsonProperty("firstName")
            String firstName,
            @JsonProperty("lastName")
            String lastName,
            @JsonProperty("imagePath")
            String imagePath) {
        this.userName = userName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.imagePath = imagePath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return userName != null && Objects.equals(userName, user.userName);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

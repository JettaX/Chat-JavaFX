package rocket_chat.view;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import rocket_chat.entity.Chat;
import rocket_chat.entity.User;

import java.util.Objects;

public class ChatsButton extends Button {

    public ChatsButton(Chat chat) {
        if (!chat.getMessages().isEmpty()) {
            this.setText((chat.getFriendUser().getName() + " " + chat.getFriendUser().getSurname() + "\n" +
                    chat.getMessages().get(chat.getMessages().size() - 1).getText()));
        } else {
            this.setText((chat.getFriendUser().getName() + " " + chat.getFriendUser().getSurname()
                    + "\n @" + chat.getOwnerUser().getUserLogin()));
        }
        initializer(chat.getFriendUser());
    }

    public ChatsButton(User user) {
        super(user.getName() + " " + user.getSurname() + "\n" +
                "@" + user.getUserLogin());
        initializer(user);
    }

    private void initializer(User user) {
        int imageSize = 46;
        this.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(user.getImagePath())), 0, imageSize,
                true, false);

        WritableImage writableImage = new WritableImage(image.getPixelReader(),
                (int) (image.getWidth() / 2 - imageSize / 2), 0,
                imageSize, imageSize);
        image = writableImage;

        Rectangle rectangle = new Rectangle(0, 0, imageSize, imageSize);
        rectangle.setArcWidth(50.0);
        rectangle.setArcHeight(50.0);

        ImagePattern pattern = new ImagePattern(image);
        rectangle.setFill(pattern);

        this.setGraphic(rectangle);
    }
}

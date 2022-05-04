package rocket_chat.view;

import javafx.scene.control.Button;
import rocket_chat.entity.Chat;
import rocket_chat.entity.User;
import rocket_chat.view.utils.RoundPicture;

public class ChatsButton extends Button {

    public ChatsButton(Chat chat) {
        if (!chat.getMessages().isEmpty()) {
            this.setText((chat.getFriendUser().getFirstName() + " " + chat.getFriendUser().getLastName() + "\n" +
                    chat.getMessages().get(chat.getMessages().size() - 1).getText()));
        } else {
            this.setText((chat.getFriendUser().getFirstName() + " " + chat.getFriendUser().getLastName()
                    + "\n @" + chat.getOwnerUser().getUserName()));
        }
        initializer(chat.getFriendUser());
    }

    public ChatsButton(User user) {
        super(user.getFirstName() + " " + user.getLastName() + "\n" +
                "@" + user.getUserName());
        initializer(user);
    }

    private void initializer(User user) {
        this.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        this.setGraphic(RoundPicture.getRoundPicture(46, user.getImagePath()));
    }
}

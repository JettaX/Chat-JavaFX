package rocket_chat.view;

import javafx.scene.control.Button;
import rocket_chat.Main;
import rocket_chat.view.utils.BackUrl;

public class BackButton extends Button {
    private BackUrl backUrl;

    public BackButton(BackUrl backUrl) {
        super("Back");
        this.backUrl = backUrl;
        initializer();
    }

    private void initializer() {
        this.setOnAction(event -> {
            if (backUrl.equals(BackUrl.CHAT_LIST)) {
                Main.showChats(Main.user);
            } else if (backUrl.equals(BackUrl.LOGIN)) {
                Main.showLogin();
            }
        });
        this.getStyleClass().add("backButton");
    }
}

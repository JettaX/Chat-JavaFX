package rocket_chat.view;

import javafx.scene.control.Button;
import rocket_chat.Main;

import java.io.IOException;

public class BackButton extends Button {

    public BackButton() {
        super("Back");
        initializer();
    }

    private void initializer() {
        this.setOnAction(event -> {
            try {
                Main.showChats(Main.user);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        this.getStyleClass().add("backButton");
    }
}

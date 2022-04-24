package rocket_chat.view;

import javafx.scene.control.Label;

public class LabelMessageNotSent extends Label {
    public LabelMessageNotSent() {
        super("!!!");
        this.getStyleClass().add("label-message-not-sent");
    }
}

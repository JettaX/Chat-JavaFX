package rocket_chat.view.utils;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;

import java.util.Objects;

public class RoundPicture {

    public static Rectangle getRoundPicture(int radius, String imagePath) {
        Image image = new Image(Objects.requireNonNull(RoundPicture.class.getResourceAsStream(imagePath)), 0, radius,
                true, false);

        WritableImage writableImage = new WritableImage(image.getPixelReader(),
                (int) (image.getWidth() / 2 - radius / 2), 0,
                radius, radius);
        image = writableImage;

        Rectangle rectangle = new Rectangle(0, 0, radius, radius);
        rectangle.setArcWidth(radius);
        rectangle.setArcHeight(radius);

        ImagePattern pattern = new ImagePattern(image);
        rectangle.setFill(pattern);
        return rectangle;
    }
}

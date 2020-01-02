package renderer;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

/**
 * Button class.
 */
public class Button extends Sprite {
    /**
     * Default constructor
     * @param renderLayer The JavaFX canvas.
     * @param position The position of the button.
     * @param texture The texture to be displayed.
     */
    public Button(Pane renderLayer, Point2D position, Image texture) {
        super(renderLayer, position);

        setTexture(texture);
    }
}

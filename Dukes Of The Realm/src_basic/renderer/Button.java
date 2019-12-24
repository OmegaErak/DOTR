package renderer;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

public class Button extends Sprite {
    public Button(Pane renderLayer, Image texture) {
        super (renderLayer, new Point2D(0, 0));

        setTexture(texture);
    }

    public Button(Pane renderLayer, Point2D position, Image texture) {
        super(renderLayer, position);

        setTexture(texture);
    }

    public Button(Pane renderLayer, Point2D position, Image texture, StatusBar statusBar) {
        super(renderLayer, new Point2D(0, 0));

        Point2D statusBarPos = statusBar.getPosition();

    }
}

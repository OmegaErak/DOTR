package drawable;

import base.Settings;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

/**
 * Background class. It's basically a Sprite wrapper.
 */
public class Background extends Sprite {
    /**
     * Default constructor
     * @param renderLayer The JavaFX canvas.
     * @param texture The texture.
     */
    public Background(Pane renderLayer, Image texture) {
        super(renderLayer, new Point2D(0, Settings.statusBarHeight));

        setTexture(texture);
        getTextureView().setFitWidth(Settings.windowWidth);
        getTextureView().setFitHeight(Settings.windowHeight - Settings.statusBarHeight);
    }
}

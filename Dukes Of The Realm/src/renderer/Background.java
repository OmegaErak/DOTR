package renderer;

import base.Settings;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

public class Background extends Sprite {
    public Background(Pane renderLayer, Image texture) {
        super (renderLayer, new Point2D(0, Settings.statusBarHeight));

        setTexture(texture);
        getTextureView().setFitWidth(Settings.windowWidth);
        getTextureView().setFitHeight(Settings.windowHeight - Settings.statusBarHeight);
    }
}

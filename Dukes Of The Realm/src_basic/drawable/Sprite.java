package drawable;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

/**
 * Sprite class to be derived by drawable objects.
 */
public abstract class Sprite {
    protected final Pane renderLayer;

    protected final ImageView textureView;

    protected Point2D position;

    /**
     * Default constructor.
     * @param renderLayer The JavaFX canvas.
     * @param position The position of the sprite in the window.
     */
    public Sprite(Pane renderLayer, Point2D position) {
        this.renderLayer = renderLayer;

        this.textureView = new ImageView();
        this.textureView.relocate(position.getX(), position.getY());

        this.position = position;
    }

    /**
     * Changes the texture.
     * @param texture The texture.
     */
    protected void setTexture(Image texture) {
        textureView.setImage(texture);
    }

    /**
     * Displays the sprite.
     */
    public void addToCanvas() {
        renderLayer.getChildren().add(textureView);
    }

    /**
     * Hiddes the sprite.
     */
    public void removeFromCanvas() {
        renderLayer.getChildren().remove(textureView);
    }

    /**
     * @return A texture view to the sprite's texture.
     */
    public ImageView getTextureView() {
        return textureView;
    }

    /**
     * @return The position in the window.
     */
    public Point2D getPosition() {
        return position;
    }

    /**
     * Sets the sprite's position.
     * @param position The position.
     */
    public void setPosition(Point2D position) {
        this.position = position;
        textureView.relocate(this.position.getX(), this.position.getY());
    }
}

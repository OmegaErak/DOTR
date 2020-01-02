package renderer;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public abstract class Sprite {
    protected Pane renderLayer;

    protected ImageView textureView;

    protected Point2D position;

    public Sprite(Pane renderLayer, Point2D position) {
        this.renderLayer = renderLayer;

        this.textureView = new ImageView();
        this.textureView.relocate(position.getX(), position.getY());

        this.position = position;
    }

    protected void setTexture(Image texture) {
        textureView.setImage(texture);
    }

    public void addToCanvas() {
        renderLayer.getChildren().add(textureView);
    }

    public void removeFromCanvas() {
        renderLayer.getChildren().remove(textureView);
    }

    public ImageView getTextureView() {
        return textureView;
    }

    public Point2D getPosition() {
        return position;
    }

    public void setPosition(Point2D position) {
        this.position = position;
        textureView.relocate(this.position.getX(), this.position.getY());
    }
}

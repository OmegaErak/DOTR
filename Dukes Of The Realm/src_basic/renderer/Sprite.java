package renderer;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public abstract class Sprite {
    protected Pane renderLayer;

    protected ImageView textureView;

    protected Point2D position;

    public int width;
    public int height;

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
        this.renderLayer.getChildren().add(this.textureView);
    }

    public void removeFromCanvas() {
        this.renderLayer.getChildren().remove(this.textureView);
    }

    public ImageView getTextureView() {
        return textureView;
    }

    public void setTextureView(ImageView textureView) {
        this.textureView = textureView;
    }

    public Point2D getPosition() {
        return position;
    }

    public void setPosition(Point2D position) {
        this.position = position;
    }

    public boolean collidesWith(Sprite sprite) {
        return getTextureView().getBoundsInParent().intersects(sprite.getTextureView().getBoundsInParent());
    }
}

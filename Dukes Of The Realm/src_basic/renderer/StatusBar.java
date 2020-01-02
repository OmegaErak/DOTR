package renderer;

import base.DayHolder;
import buildings.Castle;
import javafx.geometry.Point2D;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

/**
 * Status bar class so we can display important info of the game.
 */
public abstract class StatusBar {
    private Pane renderLayer;

    private HBox statusBar = new HBox();

    private Point2D position;
    private Point2D size;

    /**
     * Default constructor.
     * @param renderLayer The JavaFX canvas
     * @param position The position of the status bar.
     * @param size The size of the status bar.
     * @param cssClass The name of a CSS class to be used. It's loaded from a CSS file so we can have better formatting than the default one.
     */
    public StatusBar(Pane renderLayer, Point2D position, Point2D size, String cssClass) {
        this.renderLayer = renderLayer;

        setStyle(cssClass);
        setPosition(position);
        setSize(size);

        statusBar.getChildren().add(statusBarText);

        loadResources();
    }

    /**
     * Changes the CSS style
     * @param cssClass The name of the css class in the CSS file.
     */
    public void setStyle(String cssClass) {
        statusBar.getStyleClass().add(cssClass);
    }

    /**
     * Does nothing by default, but can be overriden by deriving classes if a certain status bar needs resources.
     */
    public void loadResources() {
    }

    /**
     * The update function to be called every frame. It's abstract so deriving classes must override it as every status bar is different.
     */
    public abstract void updateView();

    private StatusBarView view;
    protected boolean shouldRefreshView = false;

    /**
     * Sets to default menu view.
     */
    public void setDefaultMenuView() {
        view = StatusBarView.DefaultMenuView;
        shouldRefreshView = true;
    }

    /**
     * Sets to credits view.
     */
    public void setCreditsView() {
        view = StatusBarView.CreditsView;
        shouldRefreshView = true;
    }

    /**
     * Sets to default game view.
     */
    public void setDefaultGameView() {
        view = StatusBarView.DefaultGameView;
        shouldRefreshView = true;
    }

    private Castle castle;

    /**
     * @return The castle from which information is displayed.
     */
    public Castle getCurrentCastle() {
        return this.castle;
    }

    /**
     * Sets to castle view.
     * @param castle The castle form which information is going to be displayed.
     */
    public void setCastleView(Castle castle) {
        view = StatusBarView.CastleView;
        this.castle = castle;
        shouldRefreshView = true;
    }

    /**
     * Sets to troops move view.
     */
    public void setTroopsMoveView() {
        view = StatusBarView.TroopsMoveView;
        shouldRefreshView = true;
    }

    /**
     * @return The current view.
     */
    public StatusBarView getView() {
        return this.view;
    }

    /**
     * Changes the position of the status bar.
     * @param position The position.
     */
    public void setPosition(Point2D position) {
        this.position = position;
        statusBar.relocate(position.getX(), position.getY());
    }

    /**
     * @return The position of the status bar.
     */
    public Point2D getPosition() {
        return this.position;
    }

    /**
     * Changes the size of the status bar.
     * @param size The size.
     */
    public void setSize(Point2D size) {
        this.size = size;
        statusBar.setPrefSize(size.getX(), size.getY());
        statusBarText.setWrappingWidth(size.getX());
    }

    /**
     * @return The size of the status bar.
     */
    public Point2D getSize() {
        return this.size;
    }

    private Text statusBarText = new Text();

    /**
     * Changes the text displayed in the status bar.
     * @param text The text to be displayed.
     */
    public void setText(String text) {
        this.statusBarText.setText(text);
    }

    private DayHolder dayHolder;

    /**
     * Sets the day holder, which is a int wrapper.
     * @param dayHolder The day holder.
     */
    public void setDayHolder(DayHolder dayHolder) {
        this.dayHolder = dayHolder;
    }

    /**
     * @return The current day of the game.
     */
    public int getCurrentDay() {
        return this.dayHolder.day;
    }

    /**
     * Displays the status bar.
     */
    public void addToCanvas() {
        this.renderLayer.getChildren().add(statusBar);
    }

    /**
     * Hiddes the status bar.
     */
    public void removeFromCanvas() {
        this.renderLayer.getChildren().remove(statusBar);
    }

    /**
     * @return The JavaFX Box of the status bar.
     */
    public HBox getBox() {
        return statusBar;
    }
}

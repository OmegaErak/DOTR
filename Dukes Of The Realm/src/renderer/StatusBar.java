package renderer;

import base.DayHolder;
import buildings.Castle;

import javafx.geometry.Point2D;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

public abstract class StatusBar {
    private Pane renderLayer;

    private HBox statusBar = new HBox();

    private Point2D position;
    private Point2D size;

    public StatusBar(Pane renderLayer, Point2D position, Point2D size, String cssClass) {
        this.renderLayer = renderLayer;

        setStyle(cssClass);
        setPosition(position);
        setSize(size);

        statusBar.getChildren().add(statusBarText);

        loadResources();
    }

    public void setStyle(String cssClass) {
        statusBar.getStyleClass().add(cssClass);
    }

    public void loadResources() {

    }

    public abstract void updateView();

    private StatusBarView view;

    public void setDefaultMenuView() {
        view = StatusBarView.DefaultMenuView;
    }

    public void setCreditsView() {
        view = StatusBarView.CreditsView;
    }

    public void setDefaultGameView() {
        view = StatusBarView.DefaultGameView;
    }

    private Castle castle;

    public Castle getCurrentCastle() {
        return this.castle;
    }


    public void setCastleView(Castle castle) {
        view = StatusBarView.CastleView;
        this.castle = castle;
    }

    public void setTroopsRecruitView() {
        view = StatusBarView.TroopsRecruitView;
    }

    public void setTroopsMoveView() {
        view = StatusBarView.TroopsMoveView;
    }

    public StatusBarView getView() {
        return this.view;
    }

    public void setPosition(Point2D position) {
        this.position = position;
        statusBar.relocate(position.getX(), position.getY());
    }

    public Point2D getPosition() {
        return this.position;
    }

    public void setSize(Point2D size) {
        this.size = size;
        statusBar.setPrefSize(size.getX(), size.getY());
        statusBarText.setWrappingWidth(size.getX());
    }

    public Point2D getSize() {
        return this.size;
    }

    private Text statusBarText = new Text();

    public void setText(String text) {
        this.statusBarText.setText(text);
    }

    private DayHolder dayHolder;

    public void setDayHolder(DayHolder dayHolder) {
        this.dayHolder = dayHolder;
    }

    public int getCurrentDay() {
        return this.dayHolder.day;
    }

    public void removeFromCanvas() {
        this.renderLayer.getChildren().remove(statusBar);
    }

    public void addToCanvas() {
        this.renderLayer.getChildren().add(statusBar);
    }
}

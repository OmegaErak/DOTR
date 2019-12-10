package base;

import buildings.Castle;

import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

public abstract class StatusBar {
    protected Pane renderLayer;

    protected HBox statusBar = new HBox();
    protected Text statusBarText = new Text();

    public StatusBar(Pane renderLayer) {
        this.renderLayer = renderLayer;
    }


}

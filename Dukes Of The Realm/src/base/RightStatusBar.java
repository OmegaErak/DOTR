package base;

import buildings.Castle;

import javafx.scene.layout.Pane;

public class RightStatusBar extends StatusBar {
    public RightStatusBar(Pane renderLayer) {
        super(renderLayer);

        statusBar.getStyleClass().add("rightStatusBar");
        statusBar.relocate(2 * Settings.windowWidth / 3, 0);
        statusBar.setPrefSize(Settings.windowWidth / 3, Settings.statusBarHeight);

        statusBar.getChildren().add(statusBarText);
        this.renderLayer.getChildren().add(statusBar);
    }

    public void setDefaultView() {
        statusBarText.setText("");
    }

    public void setCastleView(Castle castle) {
        statusBarText.setText("Chevaliers: " + castle.getNbKnights() + "\n"
                            + "Onagres: " + castle.getNbOnagers() + "\n"
                            + "Piquiers: " + castle.getNbPikemen()
        );
    }
}

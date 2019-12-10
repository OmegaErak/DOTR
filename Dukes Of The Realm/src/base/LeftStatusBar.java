package base;

import buildings.Castle;

import javafx.scene.layout.Pane;

public class LeftStatusBar extends StatusBar {
    public LeftStatusBar(Pane renderLayer) {
        super(renderLayer);

        statusBar.getStyleClass().add("leftStatusBar");
        statusBar.relocate(0, 0);
        statusBar.setPrefSize(Settings.windowWidth / 3, Settings.statusBarHeight);
        statusBarText.setWrappingWidth(Settings.windowWidth / 3);


        statusBar.getChildren().add(statusBarText);
        this.renderLayer.getChildren().add(statusBar);
    }

    public void setDefaultMenuView() {
        statusBarText.setText("Bienvenu à Dukes of the Realm.");
    }

    public void setDefaultGameView(int currentDay) {
        statusBarText.setText("Jour actuel: " + currentDay);
    }

    public void setCastleView(Castle castle) {
        statusBarText.setText("Duc du château: " + castle.getOwner() + "\n"
                + "Niveau: " + castle.getLevel() + "\n"
                + "Revenu: " + castle.getPassiveIncome() + "\n"
                + "Trésor: " + castle.getTreasure()
        );
    }

    public void setCreditsView() {
        statusBarText.setText("Réalisé par Enzo Carré et Luis L. Marques.\nMerci à Morgane de m'avoir harcelé pendant la Nuit de l'Info.");
    }
}

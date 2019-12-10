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
    
    private Day day;
    
    public void setDayHolder(Day dayHolder) {
    	this.day = dayHolder;
    }
    
    private Castle castle;
    
    private StatusBarView view;
    
    @Override
    public void updateView() {
    	if (view == StatusBarView.DefaultMenuView) {
    		statusBarText.setText("Bienvenu à Dukes of the Realm.");
    	} else if (view == StatusBarView.CreditsView) {
    		statusBarText.setText("Réalisé par Enzo Carré et Luis L. Marques." + "\n"
    							+ "Merci à Morgane de m'avoir harcelé pendant la Nuit de l'Info.");
    	} else if (view == StatusBarView.DefaultGameView) {
    		statusBarText.setText("Jour actuel: " + day.dayInt);
    	} else if (view == StatusBarView.CastleView) {
    		String text = "Duc du château: " + castle.getOwner() + "\n"
                    + "Niveau: " + castle.getLevel() + "\n"
                    + "Revenu: " + castle.getPassiveIncome() + "\n"
                    + "Trésor: " + castle.getTreasure() + "\n";
        	
        	if (castle.isLevelingUp()) {
        		text += "Jours jusqu'à évolution: " + castle.getNextLevelRemainingTime();
        	}
        	
            statusBarText.setText(text);
    	}    	
    }
  
    @Override
    public void setDefaultMenuView() {
    	view = StatusBarView.DefaultMenuView;
    }

    @Override
    public void setDefaultGameView() {
    	view = StatusBarView.DefaultGameView;
    }

    @Override
    public void setCastleView(Castle castle) {
    	view = StatusBarView.CastleView;
    	this.castle = castle;
    }

    @Override
    public void setCreditsView() {
    	view = StatusBarView.CreditsView;
    }
}

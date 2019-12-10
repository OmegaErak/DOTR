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
    
    private Castle castle;
    private StatusBarView view;
    
    @Override
    public void updateView() {
    	if (view == StatusBarView.DefaultMenuView) {
    		statusBarText.setText("");
    	} else if (view == StatusBarView.CreditsView) {
    		statusBarText.setText("");
    	} else if (view == StatusBarView.DefaultGameView) {
    		statusBarText.setText("");
    	} else if (view == StatusBarView.CastleView) {
    		String text = "Chevaliers: " + castle.getNbKnights() + "\n"
    					+ "Onagres: " + castle.getNbOnagers() + "\n"
    					+ "Piquiers: " + castle.getNbPikemen() + "\n";
    		
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

package base;

import buildings.Castle;

import javafx.geometry.Point2D;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Spinner;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CenterStatusBar extends StatusBar {
    // 0 for nothing
    // 1 for castle click
    // 2 for castle click
    private int displayLevel = 0;
    private Castle currentCastle;

    // Buttons at display level 0:
    // 1: Recruit troops
    // 2: Gather troops and attack
    // 3: Level up
    private final int nbButtons0 = 3;
    private List<Sprite> buttons0 = new ArrayList<>(nbButtons0);
    
    // Buttons at display level 1:
    // 1 top: Knight+
    // 1 bot: Knight-
    // 2 top: Onager+
    // 2 bot: Onager-
    // 3 top: Pikeman+
    // 3 bot: Pikeman-
    // 4: Launch attack
    private final int nbButtons1 = 6;
    private List<Sprite> buttons1 = new ArrayList<>(nbButtons1);
    
    // TODO: Get rid of statics
    static private int nbKnightsAtt = 0;
    public static int getNbKnightsAtt() {
		return nbKnightsAtt;
	}

	public static int getNbOnagersAtt() {
		return nbOnagersAtt;
	}

	public static int getNbPikemenAtt() {
		return nbPikemenAtt;
	}

	static private int nbOnagersAtt = 0;
    static private int nbPikemenAtt = 0;

    public CenterStatusBar(Pane renderLayer) {
        super(renderLayer);

        statusBar.getStyleClass().add("centerStatusBar");
        statusBar.relocate(Settings.windowWidth / 3, 0);
        statusBar.setPrefSize(Settings.windowWidth / 3, Settings.statusBarHeight);

        statusBar.getChildren().add(statusBarText);
        this.renderLayer.getChildren().add(statusBar);

        createButtons();
    }

    private Alert alert = new Alert(Alert.AlertType.NONE);

    // TODO: Remove all the point2D and have only one that changes value
    private void createButtons() {
        // Load resources
        Point2D originPos = new Point2D(Settings.windowWidth / 3, 0);
        
        // Display level 0
        Image recruitButtonImg = new Image("resources/sprites/buttons/recruit.png");

        double buttonWidth = recruitButtonImg.getWidth();

        Point2D recruitButtonPos = new Point2D(originPos.getX(), originPos.getY());
        Sprite recruitButton = new Sprite(renderLayer, recruitButtonPos, recruitButtonImg);
        recruitButton.getTextureView().setOnMouseClicked(e -> {
        	
        	e.consume();
        });
        buttons0.add(recruitButton);

        Image fightButtonImg = new Image("resources/sprites/buttons/fight.png");
        Point2D fightButtonPos = new Point2D(recruitButtonPos.getX() + buttonWidth, originPos.getY());
        Sprite fightButton = new Sprite(renderLayer, fightButtonPos, fightButtonImg);
        fightButton.getTextureView().setOnMouseClicked(e -> {
        	setAttackView();
        	e.consume();
        });        
        buttons0.add(fightButton);

        Image levelUpButtonImg = new Image("resources/sprites/buttons/levelUp.png");
        Point2D levelUpButtonPos = new Point2D(fightButtonPos.getX() + buttonWidth, originPos.getY());
        Sprite levelUpButton = new Sprite(renderLayer, levelUpButtonPos, levelUpButtonImg);
        levelUpButton.getTextureView().setOnMouseClicked(e -> {
            if (currentCastle.getOwner() == 0) {
                if (currentCastle.canLevelUp()) {
                    alert.setAlertType(Alert.AlertType.CONFIRMATION);
                    alert.setContentText("Vous êtes sur? Ça vous coûtera " + currentCastle.getNextLevelBuildCost() + " florains.");

                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.get() == ButtonType.OK) {
                        currentCastle.levelUp();
                    }
                } else {
                    alert.setAlertType(Alert.AlertType.WARNING);
                    alert.setContentText("Vous ne pouvez pas améliorer votre château, soit parce qu'il est déjà en construction, soit parce que vous n'avez pas assez de florains.");
                    alert.show();
                }
            } else {
                alert.setAlertType(Alert.AlertType.WARNING);
                alert.setTitle("Attention");
                alert.setContentText("Ce n'est pas votre château");
                alert.show();
            }
            e.consume();
        });
        buttons0.add(levelUpButton);
        
        // Display level 1
        Image knightPImg = new Image("resources/sprites/buttons/knight+.png");

        buttonWidth = knightPImg.getWidth();

        Point2D knightPPos = new Point2D(originPos.getX(), originPos.getY());
        Sprite knightPButton = new Sprite(renderLayer, knightPPos, knightPImg);
        knightPButton.getTextureView().setOnMouseClicked(e -> {
        	if (nbKnightsAtt < currentCastle.getNbKnights()) {
        		++nbKnightsAtt;
        	}
        	
        	e.consume();
        });
        buttons1.add(knightPButton);

        Image onagerPImg = new Image("resources/sprites/buttons/onager+.png");
        Point2D onagerPPos = new Point2D(knightPPos.getX() + buttonWidth, originPos.getY());
        Sprite onagerPButton = new Sprite(renderLayer, onagerPPos, onagerPImg);
        onagerPButton.getTextureView().setOnMouseClicked(e -> {
        	if (nbOnagersAtt < currentCastle.getNbOnagers()) {
        		++nbOnagersAtt;        		
        	}
        	
        	e.consume();
        });        
        buttons1.add(onagerPButton);

        Image pikemanPImg = new Image("resources/sprites/buttons/pikeman+.png");
        Point2D pikemanPPos = new Point2D(onagerPPos.getX() + buttonWidth, originPos.getY());
        Sprite pikemanPButton = new Sprite(renderLayer, pikemanPPos, pikemanPImg);
        pikemanPButton.getTextureView().setOnMouseClicked(e -> {
        	if (nbPikemenAtt < currentCastle.getNbPikemen()) {
        		++nbPikemenAtt;
        	}
        	
        	e.consume();
        });
        buttons1.add(pikemanPButton);
        
        Image launchAttackImg = new Image("resources/sprites/buttons/launch_attack.png");
        Point2D launchAttackPos = new Point2D(pikemanPPos.getX() + buttonWidth, originPos.getY());
        Sprite launchAttackButton = new Sprite(renderLayer, launchAttackPos);
        launchAttackButton.getTextureView().setOnMouseClicked(e -> {
        	// TODO
        	
        	e.consume();
        });
        buttons1.add(launchAttackButton);
        
        final double buttonHeight = knightPImg.getHeight();
        originPos = new Point2D(originPos.getX(), originPos.getY() + buttonHeight);
        
        Image knightMImg = new Image("resources/sprites/buttons/knight-.png");
        Point2D knightMPos = new Point2D(originPos.getX(), originPos.getY());
        Sprite knightMButton = new Sprite(renderLayer, knightMPos, knightMImg);
        knightMButton.getTextureView().setOnMouseClicked(e -> {
        	if (nbKnightsAtt < currentCastle.getNbKnights()) {
        		++nbKnightsAtt;
        	}
        	
        	e.consume();
        });
        buttons1.add(knightMButton);

        Image onagerMImg = new Image("resources/sprites/buttons/onager-.png");
        Point2D onagerMPos = new Point2D(knightMPos.getX() + buttonWidth, originPos.getY());
        Sprite onagerMButton = new Sprite(renderLayer, onagerMPos, onagerMImg);
        onagerMButton.getTextureView().setOnMouseClicked(e -> {
        	if (nbOnagersAtt < currentCastle.getNbOnagers()) {
        		++nbOnagersAtt;        		
        	}
        	
        	e.consume();
        });      
        buttons1.add(onagerMButton);

        Image pikemanMImg = new Image("resources/sprites/buttons/pikeman-.png");
        Point2D pikemanMPos = new Point2D(onagerMPos.getX() + buttonWidth, originPos.getY());
        Sprite pikemanMButton = new Sprite(renderLayer, pikemanMPos, pikemanMImg);
        pikemanMButton.getTextureView().setOnMouseClicked(e -> {
        	if (nbPikemenAtt < currentCastle.getNbPikemen()) {
        		++nbPikemenAtt;
        	}
        	
        	e.consume();
        });
        buttons1.add(pikemanMButton);
        
        for (Sprite button : buttons0) {
        	button.getTextureView().setFitWidth(Settings.statusBarHeight / 2);
        	button.getTextureView().setFitHeight(Settings.statusBarHeight / 2);
        }
        
        for (Sprite button : buttons1) {
        	button.getTextureView().setFitWidth(Settings.statusBarHeight / 2);
        	button.getTextureView().setFitHeight(Settings.statusBarHeight / 2);
        }
    }
    
    private StatusBarView view;

    @Override
    public void updateView() {
        if (view == StatusBarView.DefaultMenuView || view == StatusBarView.DefaultGameView || view == StatusBarView.CreditsView) {
        	if (displayLevel == 0) { 
	            
        	} else if (displayLevel == 1) {        		
        		for (Sprite button : buttons0) {
	                button.removeFromCanvas();
	            }
        	} else if (displayLevel == 2) {
        		for (Sprite button : buttons1) {
	                button.removeFromCanvas();
	            }
        	}

            displayLevel = 0;
        } else if (view == StatusBarView.CastleView) {
            if (displayLevel == 0) {
                for (Sprite button : buttons0) {
                    button.addToCanvas();
                }

                displayLevel = 1;
            }
        } else if (view == StatusBarView.AttackView) {
        	if (displayLevel == 1) {
        		for (Sprite button : buttons0) {
        			button.removeFromCanvas();
        		}
        		
        		for (Sprite button : buttons1) {
        			button.addToCanvas();
        		}
        		
        		displayLevel = 2;
        	}
        }
    }

    @Override
    public void setDefaultMenuView() {
        view = StatusBarView.DefaultMenuView;
    }
    
    @Override
    public void setCreditsView() {
        view = StatusBarView.CreditsView;
    }
    
    @Override
    public void setDefaultGameView() {
        view = StatusBarView.DefaultGameView;
    }

    @Override
    public void setCastleView(Castle castle) {
        view = StatusBarView.CastleView;
        currentCastle = castle;
    }
    
    @Override
    public void setAttackView() {
    	view = StatusBarView.AttackView;
    	
    	 nbKnightsAtt = 0;
    	 nbOnagersAtt = 0;
    	 nbPikemenAtt = 0;
    }
}

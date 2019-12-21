//package renderer;
//
//import base.Settings;
//import buildings.Castle;
//
//import javafx.geometry.Point2D;
//import javafx.scene.control.Alert;
//import javafx.scene.control.ButtonType;
//import javafx.scene.image.Image;
//import javafx.scene.layout.Pane;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//
//public class CenterStatusBar extends StatusBar {
//    // 0 for nothing
//    // 1 for castle click
//    // 2 for castle click
//    private int displayLevel = 0;
//    private Castle currentCastle;
//
//    // Buttons at display level 0:
//    // 1: Recruit troops
//    // 2: Gather troops and attack
//    // 3: Level up
//    private final int nbButtons0 = 3;
//    private List<Sprite> buttons0 = new ArrayList<>(nbButtons0);
//
//    // Buttons at display level 1:
//    // 1 top: Knight+
//    // 1 bot: Knight-
//    // 2 top: Onager+
//    // 2 bot: Onager-
//    // 3 top: Pikeman+
//    // 3 bot: Pikeman-
//    // 4: Launch attack
//    private final int nbButtons1 = 6;
//    private List<Sprite> buttons1 = new ArrayList<>(nbButtons1);
//
//    public CenterStatusBar(Pane renderLayer) {
//        super(renderLayer);
//
//        statusBar.getStyleClass().add("centerStatusBar");
//        statusBar.relocate(Settings.windowWidth / 3, 0);
//        statusBar.setPrefSize(Settings.windowWidth / 3, Settings.statusBarHeight);
//
//        statusBar.getChildren().add(statusBarText);
//        this.renderLayer.getChildren().add(statusBar);
//
//        createButtons();
//    }
//
//    private Alert alert = new Alert(Alert.AlertType.NONE);
//
//    // TODO: Remove all the point2D and have only one that changes value
//    private void createButtons() {
//        // Load resources
//        Point2D originPos = new Point2D(Settings.windowWidth / 3, 0);
//
//        // Display level 0
//        Image recruitButtonImg = new Image("resources/sprites/buttons/recruit.png");
//
//        double buttonWidth = recruitButtonImg.getWidth();
//
//        Point2D recruitButtonPos = new Point2D(originPos.getX(), originPos.getY());
//        Sprite recruitButton = new Sprite(renderLayer, recruitButtonPos, recruitButtonImg);
//        recruitButton.getTextureView().setOnMouseClicked(e -> {
//
//        	e.consume();
//        });
//        buttons0.add(recruitButton);
//
//        Image fightButtonImg = new Image("resources/sprites/buttons/fight.png");
//        Point2D fightButtonPos = new Point2D(recruitButtonPos.getX() + buttonWidth, originPos.getY());
//        Sprite fightButton = new Sprite(renderLayer, fightButtonPos, fightButtonImg);
//        fightButton.getTextureView().setOnMouseClicked(e -> {
//        	setAttackView();
//        	e.consume();
//        });
//        buttons0.add(fightButton);
//
//        Image levelUpButtonImg = new Image("resources/sprites/buttons/levelUp.png");
//        Point2D levelUpButtonPos = new Point2D(fightButtonPos.getX() + buttonWidth, originPos.getY());
//        Sprite levelUpButton = new Sprite(renderLayer, levelUpButtonPos, levelUpButtonImg);
//        levelUpButton.getTextureView().setOnMouseClicked(e -> {
//
//        buttons0.add(levelUpButton);
//
//        for (Sprite button : buttons0) {
//        	button.getTextureView().setFitWidth(Settings.statusBarHeight / 2);
//        	button.getTextureView().setFitHeight(Settings.statusBarHeight / 2);
//        }
//
//        for (Sprite button : buttons1) {
//        	button.getTextureView().setFitWidth(Settings.statusBarHeight / 2);
//        	button.getTextureView().setFitHeight(Settings.statusBarHeight / 2);
//        }
//    }
//
//    private StatusBarView view;
//
//    @Override
//    public void updateView() {
//        if (view == StatusBarView.DefaultMenuView || view == StatusBarView.DefaultGameView || view == StatusBarView.CreditsView) {
//        	if (displayLevel == 0) {
//
//        	} else if (displayLevel == 1) {
//        		for (Sprite button : buttons0) {
//	                button.removeFromCanvas();
//	            }
//        	} else if (displayLevel == 2) {
//        		for (Sprite button : buttons1) {
//	                button.removeFromCanvas();
//	            }
//        	}
//
//            displayLevel = 0;
//        } else if (view == StatusBarView.CastleView) {
//            if (displayLevel == 0) {
//                for (Sprite button : buttons0) {
//                    button.addToCanvas();
//                }
//
//                displayLevel = 1;
//            }
//        } else if (view == StatusBarView.AttackView) {
//        	if (displayLevel == 1) {
//        		for (Sprite button : buttons0) {
//        			button.removeFromCanvas();
//        		}
//
//        		for (Sprite button : buttons1) {
//        			button.addToCanvas();
//        		}
//
//        		displayLevel = 2;
//        	}
//        }
package base;

import buildings.Castle;

import javafx.geometry.Point2D;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.media.MediaPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CenterStatusBar extends StatusBar {
    private List<Image> buttonsTextures = new ArrayList<>();

    // 0 for nothing
    // 1 for castle click
    // 2 for castle click
    private int displayLevel = 0;
    private Castle currentCastle;

    // Buttons:
    // 1: Recruit troops
    // 2: Gather troops and attack
    // 3: Level up
    private final int nbButtons1 = 3;
    private List<Sprite> buttons1 = new ArrayList<>(nbButtons1);

    public CenterStatusBar(Pane renderLayer) {
        super(renderLayer);

        statusBar.getStyleClass().add("centerStatusBar");
        statusBar.relocate(Settings.windowWidth / 3, 0);
        statusBar.setPrefSize(Settings.windowWidth / 3, Settings.statusBarHeight);

        statusBar.getChildren().add(statusBarText);
        this.renderLayer.getChildren().add(statusBar);

        createButtons();
    }

    private void createButtons() {
        // Load resources
        final Point2D originPos = new Point2D(Settings.windowWidth / 3, 0);
        Image recruitButtonImg = new Image("resources/sprites/buttons/recruit.png");
        buttonsTextures.add(recruitButtonImg);

        final double buttonWidth = buttonsTextures.get(0).getWidth();

        Point2D recruitButtonPos = new Point2D(originPos.getX(), originPos.getY());
        buttons1.add(new Sprite(renderLayer, recruitButtonPos, recruitButtonImg));

        Image fightButtonImg = new Image("resources/sprites/buttons/fight.png");
        buttonsTextures.add(fightButtonImg);

        Point2D fightButtonPos = new Point2D(recruitButtonPos.getX() + buttonWidth, originPos.getY());
        buttons1.add(new Sprite(renderLayer, fightButtonPos, fightButtonImg));

        Image levelUpButtonImg = new Image("resources/sprites/buttons/levelUp.png");
        buttonsTextures.add(levelUpButtonImg);

        Point2D levelUpButtonPos = new Point2D(fightButtonPos.getX() + buttonWidth, originPos.getY());
        Sprite levelUpButton = new Sprite(renderLayer, levelUpButtonPos, levelUpButtonImg);
        levelUpButton.getTextureView().setOnMouseClicked(e -> {
            if (currentCastle.canLevelUp()) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirmation");
                alert.setContentText("Vous êtes sur? Ça vous coûtera " + currentCastle.getNextLevelBuildCost() + " florains.");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK){
                    currentCastle.levelUp();
                }
            }
            e.consume();
        });
        buttons1.add(levelUpButton);
    }
    
    private StatusBarView view;

    @Override
    public void updateView() {
        if (view == StatusBarView.DefaultMenuView || view == StatusBarView.DefaultGameView) {
            for (Sprite button : buttons1) {
                button.removeFromCanvas();
            }

            displayLevel = 0;
        } else if (view == StatusBarView.CastleView) {
            if (displayLevel == 0) {
                for (Sprite button : buttons1) {
                    button.addToCanvas();
                }

                displayLevel = 1;
            }
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
        currentCastle = castle;
    }
    
    @Override
    public void setCreditsView() {
        view = StatusBarView.CreditsView;
    }
}

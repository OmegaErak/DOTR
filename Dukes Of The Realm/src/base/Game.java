package base;

import buildings.Castle;

import javafx.animation.AnimationTimer;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Game {
	private Random rdGen = new Random();
	private Group root;
	private Pane renderLayer;

	private GameMode gameMode;

	public Game() {
		this.root = new Group();
		this.root.getStylesheets().add("resources/css/application.css");

		this.renderLayer = new Pane();
		this.renderLayer.setPrefSize(Settings.windowWidth, Settings.windowHeight);
		this.renderLayer.setOnMouseClicked(e -> {
			if (this.gameMode == GameMode.Menu) {
				leftStatusBar.setDefaultMenuView();
				centerStatusBar.setDefaultMenuView();
				rightStatusBar.setDefaultMenuView();
			} else if (this.gameMode == GameMode.Game) {
				leftStatusBar.setDefaultGameView();
				centerStatusBar.setDefaultGameView();
				rightStatusBar.setDefaultGameView();
			}
			e.consume();
		});

		root.getChildren().add(this.renderLayer);

		gameMode = GameMode.Menu;
	}

	private Input input;
	private boolean isRunning = true;
	private int frameCounter = 0;
	private int framesPerDay = 60; // One second.

	private Day currentDay = new Day();

	public void run() {
		loadGame();

		AnimationTimer gameLoop = new AnimationTimer() {
			@Override
			public void handle(long now) {
				processInput(input, now);
				leftStatusBar.updateView();
				centerStatusBar.updateView();
				rightStatusBar.updateView();

				if (isRunning) {
					processInput(input, now);

					if (gameMode == GameMode.Game) {
						++frameCounter;
						if (frameCounter >= framesPerDay) {
							frameCounter -= framesPerDay;
							++currentDay.dayInt;

							for (Castle castle : castles) {
								castle.onUpdate();
							}
						}
					}
				}
			}

			private void processInput(Input input, long now) {
				if (input.isKeyPressed(KeyCode.SPACE)) {
					isRunning = !isRunning;
				}

				if (input.isKeyPressed(KeyCode.ESCAPE)) {
//					openQuickMenu();
				}
			}
		};
		gameLoop.start();
	}

	private LeftStatusBar leftStatusBar;
	private CenterStatusBar centerStatusBar;
	private RightStatusBar rightStatusBar;

	private List<Image> buttonsTextures = new ArrayList<>();

	private void loadGame() {
		input = new Input(this.root.getScene());

		Image menuBackgroundTexture = new Image("resources/sprites/backgrounds/menuBackground.png");
		Image gameBackgroundTexture = new Image("resources/sprites/backgrounds/gameBackground.png");

		menuBackground = new Sprite(renderLayer, new Point2D(0, Settings.statusBarHeight), menuBackgroundTexture);
		gameBackground = new Sprite(renderLayer, new Point2D(0, Settings.statusBarHeight), gameBackgroundTexture);

		buttonsTextures.add(new Image("resources/sprites/buttons/newGame.png"));
		buttonsTextures.add(new Image("resources/sprites/buttons/loadGame.png"));
		buttonsTextures.add(new Image("resources/sprites/buttons/credits.png"));

		// TODO: Initialise input and add listeners

		createStatusBar();

		createMenuButtons();

		// Set to default view
		setMenuView();
	}

	private Sprite menuBackground;
	private Sprite gameBackground;

	private List<Castle> castles = new ArrayList<>();

	private List<Sprite> defaultMenuButtons = new ArrayList<>();

	private void createMenuButtons() {
    	final double buttonWidth = buttonsTextures.get(0).getWidth();
    	final double buttonHeight = buttonsTextures.get(0).getHeight();

    	final double buttonPosX = Settings.windowWidth / 2.0 - buttonWidth / 2.0;

    	int buttonIndex = 0;
		// Start button
		final Point2D startButtonPos = new Point2D(buttonPosX, buttonHeight + buttonHeight);
		final Sprite startButton = new Sprite(renderLayer, startButtonPos, buttonsTextures.get(buttonIndex++));
		startButton.getTextureView().setOnMouseClicked(e -> {
			leftStatusBar.setDefaultMenuView();
			setGameView();
			e.consume();
		});
		defaultMenuButtons.add(startButton);

		final Point2D loadButtonPos = new Point2D(buttonPosX, startButtonPos.getY() + 2 * buttonHeight);
		final Sprite loadButton = new Sprite(renderLayer, loadButtonPos, buttonsTextures.get(buttonIndex++));
		loadButton.getTextureView().setOnMouseClicked(e -> {
//			setLoadGameView();
			e.consume();
		});
		defaultMenuButtons.add(loadButton);

		final Point2D creditsButtonPos = new Point2D(buttonPosX, loadButtonPos.getY() + 2 * buttonHeight);
		final Sprite creditsButton = new Sprite(renderLayer, creditsButtonPos, buttonsTextures.get(buttonIndex++));
		creditsButton.getTextureView().setOnMouseClicked(e -> {
			setCreditsView();
			e.consume();
		});
		defaultMenuButtons.add(creditsButton);
	}

	private void createCastles() {
		final int widthUpperBound = Settings.gridCellsCountX - Settings.castleSize;
		final int heightUpperBound = Settings.gridCellsCountY - Settings.castleSize;

		final int nbActiveDukes = rdGen.nextInt(Settings.nbMaxActiveDukes);
		final int nbNeutralDukes = Settings.nbMinCastles + rdGen.nextInt(Settings.nbMaxCastles - nbActiveDukes) - 1;
		final int nbCastles = 1 + nbActiveDukes + nbNeutralDukes;

		// 0 is the player
		// [1, nbActiveDukes] is for active dukes
		// [1+nbActiveDukes, 1+nbActiveDukes+nbNeutralDukes]
		int castleOwner = 0;
		while (castles.size() < nbCastles) {
			Point2D position = new Point2D(rdGen.nextInt(widthUpperBound), Settings.statusBarHeight + rdGen.nextInt(heightUpperBound));
			if (!isPositionNearACastle(position)) {
				castles.add(new Castle(renderLayer, castleOwner, position));
				++castleOwner;
			}
		}

		castles.forEach(castle -> {
			castle.getTextureView().setOnMouseClicked(e -> {
				leftStatusBar.setCastleView(castle);
				centerStatusBar.setCastleView(castle);
				rightStatusBar.setCastleView(castle);
				e.consume();
			});
		});
	}

	private boolean isPositionNearACastle(Point2D position) {
		for (Castle castle : castles) {
			Point2D position2 = castle.getPosition();
			if (position.distance(position2) < Settings.minimumCastleDistance) {
				return true;
			}
		}

		return false;
	}

	
	private void createStatusBar() {
		leftStatusBar = new LeftStatusBar(renderLayer);
		leftStatusBar.setDayHolder(currentDay);
		leftStatusBar.setDefaultMenuView();

		centerStatusBar = new CenterStatusBar(renderLayer);
		centerStatusBar.setDefaultMenuView();

		rightStatusBar = new RightStatusBar(renderLayer);
		rightStatusBar.setDefaultMenuView();
	}
	

	private void setMenuView() {
    	gameMode = GameMode.Menu;

		for (Castle castle : castles) {
			castle.removeFromCanvas();
		}

		menuBackground.addToCanvas();

		for (Sprite button : defaultMenuButtons) {
			button.addToCanvas();
		}
	}

	private void setGameView() {
    	gameMode = GameMode.Game;
		leftStatusBar.setDefaultGameView();
		centerStatusBar.setDefaultGameView();
		rightStatusBar.setDefaultGameView();

		createCastles();

		menuBackground.removeFromCanvas();

		gameBackground.addToCanvas();
		for (Castle castle : castles) {
			castle.addToCanvas();
		}
	}

	private void setCreditsView() {
		leftStatusBar.setCreditsView();
	}

	public Group getRoot() {
		return root;
	}
}
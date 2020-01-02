package base;

import buildings.Castle;

import renderer.Button;
import renderer.Background;
import renderer.StatusBar;
import renderer.StatusBarView;

import troops.Knight;

import javafx.animation.AnimationTimer;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.Random;

/**
 * Game class that manages the game's inner workings. It also manages JavaFX resources.
 */
public class Game {
	private Group root;
	private Pane renderLayer;

	private Random rdGen = new Random();

	private GameMode gameMode;

	/**
	 * Default constructor. Initialises JavaFX variables and configures them to adapt to our application.
	 */
	public Game() {
		this.root = new Group();
		this.root.getStylesheets().add("/css/application.css");

		this.renderLayer = new Pane();
		this.renderLayer.setPrefSize(Settings.windowWidth, Settings.windowHeight);
		this.renderLayer.setFocusTraversable(true);
		this.renderLayer.setOnMouseClicked(e -> {
			if (gameMode == GameMode.Menu) {
				for (StatusBar statusBar : statusBars) {
					statusBar.setDefaultMenuView();
				}
			} else if (gameMode == GameMode.Game) {
				for (StatusBar statusBar : statusBars) {
					statusBar.setDefaultGameView();
				}
			}
			e.consume();
		});

		this.renderLayer.setOnKeyPressed(key -> {
			if (key.getCode() == KeyCode.SPACE) {
				isRunning = !isRunning;
			} else if (key.getCode() == KeyCode.ESCAPE) {
				// TODO: open quick menu to save
			}
		});

		root.getChildren().add(this.renderLayer);
	}

	private boolean isRunning = true;
	private int frameCounter = 0;
	private int framesPerDay = 120; // Two seconds.

	private DayHolder currentDayHolder = new DayHolder();

	/**
	 * Core function. It will load the resources.
	 * It also manages the update of the game, which happens at 60 fps.
	 * However, a day in the game is equal to two seconds in real life.
	 */
	public void run() {
		loadGame();

		AnimationTimer gameLoop = new AnimationTimer() {
			@Override
			public void handle(long now) {
				for (StatusBar statusBar : statusBars) {
					statusBar.updateView();
				}

				if (isRunning) {
					if (gameMode == GameMode.Game) {
						++frameCounter;
						if (frameCounter >= framesPerDay) {
							frameCounter -= framesPerDay;
							++currentDayHolder.day;

							for (Castle castle : castles) {
								castle.onUpdate();
							}
						}
					}
				}
			}
		};
		gameLoop.start();
	}

	private Background menuBackground;
	private Background gameBackground;

	/**
	 * Loads the game resources. It also puts the player in the game menu.
	 */
	private void loadGame() {
		menuBackground = new Background(renderLayer, new Image("/sprites/backgrounds/menu_background.png"));
		gameBackground = new Background(renderLayer, new Image("/sprites/backgrounds/game_background.png"));

		createStatusBar();
		createMenuButtons();

		// Set to Menu view by default
		setMenuView();
	}

	private ArrayList<Button> defaultMenuButtons = new ArrayList<>();

	/**
	 * Loads the menu buttons.
	 */
	private void createMenuButtons() {
		Image texture = new Image("/sprites/buttons/new_game.png");

    	final double buttonWidth = texture.getWidth();
    	final double buttonHeight = texture.getHeight();
    	final double buttonPosX = (Settings.windowWidth - buttonWidth) / 2.0;

    	String[] buttonsPath = new String[3];
    	buttonsPath[0] = "new_game.png";
    	buttonsPath[1] = "load_game.png";
    	buttonsPath[2] = "credits.png";

		Point2D buttonPos = new Point2D(0, 0);
		Button button;

		for (String buttonPath : buttonsPath) {
			buttonPos = new Point2D(buttonPosX, buttonPos.getY() + 2 * buttonHeight);
			button = new Button(renderLayer, buttonPos, new Image("/sprites/buttons/" + buttonPath));

			defaultMenuButtons.add(button);
		}

		defaultMenuButtons.get(0).getTextureView().setOnMouseClicked(e -> {
			setGameView();
			e.consume();
		});

		defaultMenuButtons.get(1).getTextureView().setOnMouseClicked(e -> {
			// I don't get why I need to add resources/ here and not when dealing with JavaFX, my guess is that JavaFX does it internally.
			GameIO.loadGame(this, "resources/dukes.sav");
			e.consume();
		});

		defaultMenuButtons.get(2).getTextureView().setOnMouseClicked(e -> {
			setCreditsView();
			e.consume();
		});
	}

	private Castle currentPlayerCastle;
	private ArrayList<Castle> castles = new ArrayList<>();

	private ArrayList<Button> castleEnemyTargets = new ArrayList<>();
	private ArrayList<Button> castleAllyTargets = new ArrayList<>();

	/**
	 * Loads the castles textures and creates them.
	 */
	private void createCastles() {
		final int widthUpperBound = Settings.gridCellsCountX - Settings.castleSize;
		final int heightUpperBound = Settings.gridCellsCountY - Settings.castleSize;

		final int nbActiveDukes = 1 + rdGen.nextInt(Settings.nbMaxActiveDukes);
		final int nbNeutralDukes = Settings.nbMinCastles + rdGen.nextInt(1 + Settings.nbMaxCastles - nbActiveDukes) - 1;
		final int nbCastles = 1 + nbActiveDukes + nbNeutralDukes;

		// 0 is the player
		// [1, nbActiveDukes] is for active dukes
		// [1 + nbActiveDukes, 1 + nbActiveDukes+nbNeutralDukes] is for neutral dukes
		int castleOwner = 0;
		while (castles.size() < nbCastles) {
			int cellSize = Settings.cellSize;
			Point2D position = new Point2D(rdGen.nextInt(widthUpperBound/cellSize)*cellSize, Settings.statusBarHeight + rdGen.nextInt(heightUpperBound/cellSize)*cellSize);
			if (!isPositionNearACastle(position)) {
				castles.add(new Castle(renderLayer, castleOwner, position));
				++castleOwner;
			}
		}

		Image enemyTargetTexture = new Image("/sprites/castles/ennemyTarget.png");
		Image allyTargetTexture = new Image("/sprites/castles/allyTarget.png");

		currentPlayerCastle = castles.get(0);
		for (Castle castle : castles) {
			castle.getTextureView().setOnMouseClicked(e -> {
				if (castle.getOwner() == 0) {
					currentPlayerCastle = castle;
				}

				for (StatusBar statusBar : statusBars) {
					statusBar.setCastleView(castle);
				}
				e.consume();
			});

			Point2D pos = new Point2D(castle.getPosition().getX(), castle.getPosition().getY());
			Button enemyTargetButton = new Button(renderLayer, pos, enemyTargetTexture);
			enemyTargetButton.getTextureView().setFitWidth(Settings.castleSize);
			enemyTargetButton.getTextureView().setFitHeight(Settings.castleSize);
			enemyTargetButton.getTextureView().setPickOnBounds(true);
			enemyTargetButton.getTextureView().setOnMouseClicked(e -> {
				currentPlayerCastle.moveTroops(castle, selectedTroops);
				e.consume();
			});
			castleEnemyTargets.add(enemyTargetButton);

			Button allyTargetButton = new Button(renderLayer, pos, allyTargetTexture);
			allyTargetButton.getTextureView().setFitWidth(Settings.castleSize);
			allyTargetButton.getTextureView().setFitHeight(Settings.castleSize);
			allyTargetButton.getTextureView().setPickOnBounds(true);
			allyTargetButton.getTextureView().setOnMouseClicked(e -> {
				currentPlayerCastle.moveTroops(castle, selectedTroops);
				e.consume();
			});
			castleAllyTargets.add(allyTargetButton);
		}
	}

	/**
	 * Checks if there is a castle around the position passed as parameter, depending on the setting specified in the Settings class.
	 * @param position The position to check.
	 * @return True if there is a castle nearby, false otherwise.
	 */
	private boolean isPositionNearACastle(Point2D position) {
		for (Castle castle : castles) {
			Point2D position2 = castle.getPosition();
			if (position.distance(position2) < Settings.minimumCastleDistance) {
				return true;
			}
		}

		return false;
	}

	private ArrayList<Knight> selectedTroops = new ArrayList<>();
	private ArrayList<StatusBar> statusBars = new ArrayList<>();

	/**
	 * Loads the status bars that are at the top of the application.
	 */
	private void createStatusBar() {
		Point2D statusBarSize = new Point2D(Settings.leftStatusBarWidth, Settings.statusBarHeight);
		Point2D statusBarPos = new Point2D(0, 0);
		StatusBar leftStatusBar = new StatusBar(renderLayer, statusBarPos, statusBarSize, "leftStatusBar") {
			@Override
			public void updateView() {
				if (getView() == StatusBarView.DefaultMenuView) {
					setText("Bienvenu à Dukes of the Realm.");
				} else if (getView() == StatusBarView.CreditsView) {
					setText("Réalisé par Enzo Carré et Luis L. Marques." + "\n"
							+ "Merci à Morgane de m'avoir harcelé pendant la Nuit de l'Info.");
				} else if (getView() == StatusBarView.DefaultGameView) {
					setText("Jour actuel: " + getCurrentDay());
				} else if (getView() == StatusBarView.CastleView) {
					setText("Duc du château: " + getCurrentCastle().getOwnerName() + "\n");
				}
			}
		};
		leftStatusBar.setDayHolder(currentDayHolder);
		leftStatusBar.setDefaultMenuView();
		statusBars.add(leftStatusBar);

		statusBarSize = new Point2D(Settings.centerStatusBarWidth, Settings.statusBarHeight);
		statusBarPos = new Point2D(statusBarPos.getX() + leftStatusBar.getSize().getX(), statusBarPos.getY());
		StatusBar centerStatusBar = new StatusBar(renderLayer, statusBarPos, statusBarSize, "centerStatusBar") {
			private ArrayList<Button> decisionButtons;

			private ArrayList<Spinner<Integer>> moveSpinners;

			private Boolean firstFrame = true;

			@Override
			public void updateView() {
				// Should be done every frame
				if (getView() == StatusBarView.TroopsMoveView) {
					if (getCurrentCastle().getOwner() == 0) {
						final ArrayList<Integer> spinnerValues = new ArrayList<>();
						spinnerValues.add(getCurrentCastle().getNbKnights());
						for (int i = 0; i < Settings.nbDiffTroopTypes; ++i) {
							final int initialValue;
							if (firstFrame) {
								initialValue = 0;
								firstFrame = false;
							} else {
								initialValue = moveSpinners.get(i).getValue();
							}
							final SpinnerValueFactory<Integer> factory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, spinnerValues.get(i), initialValue);
							moveSpinners.get(i).setValueFactory(factory);
						}

						selectedTroops.clear();
						for (int i = 0; i < moveSpinners.get(0).getValue(); ++i) {
							selectedTroops.add(getCurrentCastle().getKnightByIndex(i));
						}
					}
				}

				// Should be done only when the view is changed
				if (shouldRefreshView) {
					setText("");

					for (Button button : decisionButtons) {
						button.removeFromCanvas();
					}

					removeSpinnersFromCanvas(moveSpinners);

					for (Button target : castleEnemyTargets) {
						target.removeFromCanvas();
					}

					for (Button target : castleAllyTargets) {
						target.removeFromCanvas();
					}

					if (getView() == StatusBarView.CastleView) {
						if (getCurrentCastle().getOwner() == 0) {
							for (Button button : decisionButtons) {
								button.addToCanvas();
							}
						}
					} else if (getView() == StatusBarView.TroopsMoveView) {
						setText("Chevaliers:");
						addSpinnersToCanvas(moveSpinners);

						for (int i = 0; i < castles.size(); ++i) {
							if (castles.get(i) == getCurrentCastle()) {
								continue;
							}

							if (castles.get(i).getOwner() == getCurrentCastle().getOwner()) {
								castleAllyTargets.get(i).addToCanvas();
							} else {
								castleEnemyTargets.get(i).addToCanvas();
							}
						}
					}
					
					shouldRefreshView = false;
				}
			}

			@Override
			public void loadResources() {
				decisionButtons = new ArrayList<>();

				Image texture = new Image("/sprites/buttons/recruit.png");
				final double buttonWidth = texture.getWidth();

				String[] decisionButtonsPaths = new String[1];
				decisionButtonsPaths[0] = "select_troops.png";

				Point2D buttonPos = new Point2D(getPosition().getX(), getPosition().getY());

				for (String buttonPath : decisionButtonsPaths) {
					Button button = new Button(renderLayer, buttonPos, new Image("/sprites/buttons/" + buttonPath));
					decisionButtons.add(button);

					buttonPos = new Point2D(buttonPos.getX() + buttonWidth, buttonPos.getY());
				}

				decisionButtons.get(0).getTextureView().setOnMouseClicked(e -> {
					setTroopsMoveView();
					e.consume();
				});

				// Spinners for troop selection
				moveSpinners = new ArrayList<>();

				final int yOffset = 30;
				Point2D spinnerPosition = new Point2D(getPosition().getX(), getPosition().getY());

				final double spinnerSize = getSize().getX() / Settings.nbDiffTroopTypes;
				for (int i = 0; i < Settings.nbDiffTroopTypes; ++i) {
					final Spinner<Integer> spinner = new Spinner<>();

					spinner.setTranslateX(spinnerPosition.getX());
					spinner.setTranslateY(spinnerPosition.getY() + yOffset);

					spinner.setPrefWidth(spinnerSize);

					moveSpinners.add(spinner);

					spinnerPosition = new Point2D(spinnerPosition.getX() + spinnerSize, spinnerPosition.getY());
				}


			}

			public void addSpinnersToCanvas(ArrayList<Spinner<Integer>> spinners) {
				for (Spinner<Integer> spinner : spinners) {
					renderLayer.getChildren().add(spinner);
				}
			}

			public void removeSpinnersFromCanvas(ArrayList<Spinner<Integer>> spinners) {
				for (Spinner<Integer> spinner : spinners) {
					renderLayer.getChildren().remove(spinner);
				}
			}
		};
		centerStatusBar.setDefaultMenuView();
		statusBars.add(centerStatusBar);

		statusBarSize = new Point2D(Settings.rightStatusBarWidth, Settings.statusBarHeight);
		statusBarPos = new Point2D(statusBarPos.getX() + centerStatusBar.getSize().getX(), statusBarPos.getY());
		StatusBar rightStatusBar = new StatusBar(renderLayer, statusBarPos, statusBarSize, "rightStatusBar") {
			@Override
			public void updateView() {
				setText("");
				if (getView() == StatusBarView.CastleView) {
					String text = "Troupes du château:" + "\n"
								+ "Chevaliers: " + getCurrentCastle().getNbKnights() + "\n";

					setText(text);
				}
			}
		};
		rightStatusBar.setDefaultMenuView();
		statusBars.add(rightStatusBar);

		for (StatusBar statusBar : statusBars) {
			statusBar.getBox().setOnMouseClicked(e -> {
				e.consume();
			});
			statusBar.addToCanvas();
		}
	}

	/**
	 * Puts the player into the game menu.
	 */
	private void setMenuView() {
		gameMode = GameMode.Menu;

		for (Castle castle : castles) {
			castle.removeFromCanvas();
		}

		menuBackground.addToCanvas();

		for (Button button : defaultMenuButtons) {
			button.addToCanvas();
		}
	}

	/**
	 * Puts the player into the game.
	 */
	private void setGameView() {
    	gameMode = GameMode.Game;
    	for (StatusBar statusBar : statusBars) {
    		statusBar.setDefaultGameView();
		}

		createCastles();

		menuBackground.removeFromCanvas();
		gameBackground.addToCanvas();

		for (Castle castle : castles) {
			castle.addToCanvas();
		}
	}

	/**
	 * Displays the credits.
	 */
	private void setCreditsView() {
		for (StatusBar statusBar : statusBars) {
			statusBar.setCreditsView();
		}
	}

	/**
	 * Returns the JavaFX group used by the application to be used by JavaFX to display.
	 * @return The JavaFX group used by the application.
	 */
	public Group getRoot() {
		return root;
	}
}
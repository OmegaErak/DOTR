package base;

import buildings.Castle;

import drawable.Button;
import drawable.Background;
import drawable.StatusBar;
import drawable.StatusBarView;

import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;

import javafx.animation.AnimationTimer;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Game class that manages the game's inner workings. It also manages JavaFX resources.
 */
public class Game {
	/**
	 * The different possible names for the dukes of the realm.
	 */
	final static private List<String> dukeNames = new ArrayList<>(Arrays.asList(
			"Jean-Cloud Van Damme",

			"Jean-Eudes",
			"Jean-Michel",
			"Jean-Marie",
			"Jean-Loup",
			"Jean-Côme",
			"Jean-Alex",
			"Jean-Kévin",
			"Jean-René",
			"Jean-Maurice",
			"Jean-Francis",
			"Jean-Jacques",
			"Jean-Noël",
			"Jean-George",
			"Jean-Brice",
			"Jean-Blaise",
			"Jean-Aimée",
			"Jean-Baptiste",
			"Jean-Bernard",
			"Jean-Briac",
			"Jean-Charles",
			"Jean-Jean",
			"Jean-Paul",
			"Jean-Ti",
			"Jean-Rêve",
			"Jean-Yves",

			"Jean-Cérien"
	));

	// Drawable objects
	private final Group root;
	private final Pane renderLayer;

	private final ArrayList<StatusBar> statusBars = new ArrayList<>();

	private Background menuBackground;
	private Background gameBackground;

	private final ArrayList<Button> defaultMenuButtons = new ArrayList<>();

	private Button saveButton;
	private Boolean isSaveButtonDisplayed = false;

	private final ArrayList<Button> castleEnemyTargets = new ArrayList<>();
	private final ArrayList<Button> castleAllyTargets = new ArrayList<>();

	private Castle currentPlayerCastle;
	private ArrayList<Castle> castles = new ArrayList<>();

	// Game objects
	private final Random rdGen = new Random();

	private GameMode gameMode;

	private boolean isRunning = true;
	private int frameCounter = 0;
	private final int framesPerDay = 120; // Two seconds.

	private final DayHolder currentDayHolder = new DayHolder();

	private Integer moveCommand;

	/**
	 * Default constructor. Initialises JavaFX variables and configures them to adapt to our application.
	 */
	public Game() {
		root = new Group();
		root.getStylesheets().add("/css/application.css");

		renderLayer = new Pane();
		renderLayer.setPrefSize(Settings.windowWidth, Settings.windowHeight);
		renderLayer.setFocusTraversable(true);
		renderLayer.setOnMouseClicked(e -> {
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

		renderLayer.setOnKeyPressed(key -> {
			if (gameMode == GameMode.Game) {
				if (key.getCode() == KeyCode.SPACE) {
					isRunning = !isRunning;
				} else if (key.getCode() == KeyCode.ESCAPE) {
					isRunning = !isRunning;
					if (isSaveButtonDisplayed) {
						saveButton.removeFromCanvas();
					} else {
						saveButton.addToCanvas();
					}
					isSaveButtonDisplayed = !isSaveButtonDisplayed;
				}
			}
		});

		root.getChildren().add(renderLayer);

		loadGame();
	}

	/**
	 * Core function. It will load the resources.
	 * It also manages the update of the game, which happens at 60 fps.
	 * However, a day in the game is equal to two seconds in real life.
	 */
	public void run() {
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

	/**
	 * Loads the game resources. It also puts the player in the game menu.
	 */
	private void loadGame() {
		menuBackground = new Background(renderLayer, new Image("/sprites/backgrounds/menu_background.png"));
		gameBackground = new Background(renderLayer, new Image("/sprites/backgrounds/game_background.png"));

		createStatusBar();
		createMenuButtons();
		createCastles();

		// Set to Menu view by default
		setMenuView();
	}

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
					if (isPlayerCastle(getCurrentCastle())) {
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

						moveCommand = moveSpinners.get(0).getValue();
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
						if (isPlayerCastle(getCurrentCastle())) {
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
			statusBar.getBox().setOnMouseClicked(e -> e.consume());
			statusBar.addToCanvas();
		}
	}

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
			GameIO.loadGame(this, "resources/dukes_basic.sav", renderLayer);
			setGameView();
			e.consume();
		});

		defaultMenuButtons.get(2).getTextureView().setOnMouseClicked(e -> {
			setCreditsView();
			e.consume();
		});

		Image saveButtonTexture = new Image("/sprites/buttons/save_game.png");
		Point2D saveButtonPos = new Point2D((Settings.windowWidth - saveButtonTexture.getWidth()) / 2.0, (Settings.windowHeight - saveButtonTexture.getHeight()) / 2.0);
		saveButton = new Button(renderLayer, saveButtonPos, saveButtonTexture);
		saveButton.getTextureView().setOnMouseClicked(e -> {
			GameIO.saveGame(this, "resources/dukes_basic.sav");

			saveButton.removeFromCanvas();
			isSaveButtonDisplayed = false;
			e.consume();
		});
	}

	/**
	 * Loads the castles textures and creates them.
	 */
	private void createCastles() {
		final int widthUpperBound = Settings.gridCellsCountX - Settings.castleSize;
		final int heightUpperBound = Settings.gridCellsCountY - Settings.castleSize;

		final int nbActiveDukes = 1 + rdGen.nextInt(Settings.nbMaxActiveDukes);
		final int nbNeutralDukes = Settings.nbMinCastles + rdGen.nextInt(1 + Settings.nbMaxCastles - nbActiveDukes) - 1;
		final int nbCastles = 1 + nbActiveDukes + nbNeutralDukes;

		int castleOwner = 0;
		while (castles.size() < nbCastles) {
			int cellSize = Settings.cellSize;
			Point2D position = new Point2D(rdGen.nextInt(widthUpperBound/cellSize)*cellSize, Settings.statusBarHeight + rdGen.nextInt(heightUpperBound/cellSize)*cellSize);
			if (!isPositionNearACastle(position)) {
				Castle castle = new Castle(renderLayer, position);
				castle.setOwner(castleOwner);

				final int index = rdGen.nextInt(dukeNames.size());
				castle.setOwnerName(dukeNames.get(index));
				dukeNames.remove(index);

				castles.add(castle);
				++castleOwner;
			}
		}

		createCastleTargets();
	}

	private void createCastleTargets() {
		Image enemyTargetTexture = new Image("/sprites/castles/ennemyTarget.png");
		Image allyTargetTexture = new Image("/sprites/castles/allyTarget.png");

		castleEnemyTargets.clear();
		castleAllyTargets.clear();

		currentPlayerCastle = castles.get(0);
		for (Castle castle : castles) {
			castle.getTextureView().setOnMouseClicked(e -> {
				if (isPlayerCastle(castle)) {
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
				currentPlayerCastle.orderMove(castle, moveCommand);
				e.consume();
			});
			castleEnemyTargets.add(enemyTargetButton);

			Button allyTargetButton = new Button(renderLayer, pos, allyTargetTexture);
			allyTargetButton.getTextureView().setFitWidth(Settings.castleSize);
			allyTargetButton.getTextureView().setFitHeight(Settings.castleSize);
			allyTargetButton.getTextureView().setPickOnBounds(true);
			allyTargetButton.getTextureView().setOnMouseClicked(e -> {
				currentPlayerCastle.orderMove(castle, moveCommand);
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
	 * @return Number of castles
	 */
	public int getNbCastles() {
		return castles.size();
	}

	/**
	 * Sets castles, without checking positions.
	 */
	public void setCastles(ArrayList<Castle> castles) {
		// Clean first
		for (Castle castle : this.castles) {
			castle.removeFromCanvas();
		}
		this.castles = castles;

		createCastleTargets();
	}
	/**
	 * @return The castles of the game.
	 */
	public ArrayList<Castle> getCastles() {
		return castles;
	}

	/**
	 * @param castle The castle to be checked.
	 * @return True if it belongs to the user, false otherwise.
	 */
	private Boolean isPlayerCastle(Castle castle) {
		return castle.getOwner() == 0;
	}

	/**
	 * Sets the current day of the game.
	 * @param day The day.
	 */
	public void setCurrentDay(int day) {
		currentDayHolder.day = day;
	}

	/**
	 * @return The current day
	 */
	public int getCurrentDay() {
		return currentDayHolder.day;
	}

	/**
	 * Returns the JavaFX group used by the application to be used by JavaFX to display.
	 * @return The JavaFX group used by the application.
	 */
	public Group getRoot() {
		return root;
	}
}
package base;

import algorithms.AStar;
import algorithms.Node;

import buildings.Castle;

import drawable.Button;
import drawable.Background;
import drawable.StatusBar;
import drawable.StatusBarView;

import troops.Business;
import troops.Knight;
import troops.Onager;
import troops.Pikeman;
import troops.Troop;

import javafx.scene.input.KeyCode;
import javafx.animation.AnimationTimer;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Game class that manages the game's inner workings. It also manages JavaFX resources.
 */
public class Game {
	/**
	 * The different possible names for the dukes of the realm.
	 */
	final static private List<String> dukeNames = new ArrayList<String>(Arrays.asList(
			"Jean-Cloud Van Damme",

			"Jean-Eudes",
			"Jean-Michel",
			"Jean-Marie",
			"Jean-Loup",
			"Jean-C�me",
			"Jean-Alex",
			"Jean-K�vin",
			"Jean-Ren�",
			"Jean-Maurice",
			"Jean-Francis",
			"Jean-Jacques",
			"Jean-No�l",
			"Jean-George",
			"Jean-Brice",
			"Jean-Blaise",
			"Jean-Aim�e",
			"Jean-Baptiste",
			"Jean-Bernard",
			"Jean-Briac",
			"Jean-Charles",
			"Jean-Jean",
			"Jean-Paul",
			"Jean-Ti",
			"Jean-R�ve",
			"Jean-Yves",

			"Jean-C�rien"
	));

	// Render objects
	private Group root;
	private Pane renderLayer;

	private List<StatusBar> statusBars = new ArrayList<>();

	private Background menuBackground;
	private Background gameBackground;

	private List<Button> defaultMenuButtons = new ArrayList<>();

	private Button saveButton;
	private Boolean isSaveButtonDisplayed = false;

	private ArrayList<Button> castleEnemyTargets = new ArrayList<>();
	private ArrayList<Button> castleAllyTargets = new ArrayList<>();
	private ArrayList<Button> castleMoneyTargets = new ArrayList<>();

	private boolean isRunning = true;
	private int frameCounter = 0;
	private int framesPerDay = 120; // Two seconds

	// Game objects
	private Random rdGen = new Random();

	private GameMode gameMode;

	private DayHolder currentDayHolder = new DayHolder();

	private int[][] gameMap = new int[Settings.gridCellsCountX / Settings.cellSize][Settings.gridCellsCountY / Settings.cellSize];

	public static ArrayList<Castle> playerCastles = new ArrayList<>();

	private Castle currentPlayerCastle;
	private ArrayList<Castle> castles = new ArrayList<>();

	private ArrayList<AtomicInteger> recruitCommand = new ArrayList<>();
	private ArrayList<Troop> selectedTroops = new ArrayList<>();
	private int moneyToTransfer;

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
								castle.unitAroundAction();
								castle.isAlive(gameMap);
								castle.onProduction();
								castle.buildingBarracks();
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
		final Point2D statusBarSize = new Point2D(Settings.windowWidth / 3.0, Settings.statusBarHeight);
		Point2D statusBarPos = new Point2D(0, 0);
		StatusBar leftStatusBar = new StatusBar(renderLayer, statusBarPos, statusBarSize, "leftStatusBar") {
			@Override
			public void updateView() {
				if (getView() == StatusBarView.DefaultMenuView) {
					setText("Bienvenu à Dukes of the Realm.");
				} else if (getView() == StatusBarView.CreditsView) {
					setText("Réalisé par Enzo Carré et Luis L. Marques." + "\n"
							+ "Merci à Morgane de m'avoir harcelé pendant la Nuit de l'Info."
							+ " Merci a Quentin Legrand pour sa participation au sprite");
				} else if (getView() == StatusBarView.DefaultGameView) {
					setText("Jour actuel: " + getCurrentDay());
				} else if (getView() == StatusBarView.CastleView) {
					String text = "Duc du château: " + getCurrentCastle().getOwnerName() + "\n"
							+ "Niveau: " + getCurrentCastle().getLevel() + "\n"
							+ "Revenu: " + getCurrentCastle().getPassiveIncome() + "\n"
							+ "Trésor: " + getCurrentCastle().getTreasure() + "\n";

					if (getCurrentCastle().isLevelingUp()) {
						text += "Jours jusqu'à évolution: " + getCurrentCastle().getNextLevelRemainingTime();
					}
					if (getCurrentCastle().isGettingWall()) {
						text += "Jours jusqu'à construction des murailles: " + getCurrentCastle().getWallTimeCost();
					}

					setText(text);
				}
			}
		};
		leftStatusBar.setDayHolder(currentDayHolder);
		leftStatusBar.setDefaultMenuView();
		statusBars.add(leftStatusBar);

		statusBarPos = new Point2D(statusBarPos.getX() + Settings.windowWidth / 3.0, statusBarPos.getY());
		StatusBar centerStatusBar = new StatusBar(renderLayer, statusBarPos, statusBarSize, "centerStatusBar") {
			private ArrayList<Button> decisionButtons;
			private Button confirmChoice;

			private ArrayList<Spinner<Integer>> recruitSpinners;
			private ArrayList<Spinner<Integer>> moveSpinners;
			private Spinner<Integer> moneySpinner;

			private Boolean firstFrame = true;

			@Override
			public void updateView() {
				// Should be done every frame
				if (getView() == StatusBarView.TroopsRecruitView) {
					if (getCurrentCastle().getOwner() == 0) {
						confirmChoice.getTextureView().setOnMouseClicked(e -> recruitTroops( recruitCommand, this.getCurrentCastle().getBarrackLevel()));
						ArrayList<Integer> spinnerValues = new ArrayList<>();
						if (firstFrame) {
							spinnerValues.add(100);
							spinnerValues.add(100);
							spinnerValues.add(100);
						} else {
							final int nbPossibleKnights = (getCurrentCastle().getTreasure() - recruitCommand.get(1).get() * Settings.onagerProdCost - recruitCommand.get(2).get() * Settings.pikemanProdCost) / Settings.knightProdCost;
							final int nbPossibleOnagers = (getCurrentCastle().getTreasure() - recruitCommand.get(0).get() * Settings.knightProdCost - recruitCommand.get(2).get() * Settings.pikemanProdCost) / Settings.onagerProdCost;
							final int nbPossiblePikemen = (getCurrentCastle().getTreasure() - recruitCommand.get(0).get() * Settings.knightProdCost - recruitCommand.get(1).get() * Settings.onagerProdCost) / Settings.pikemanProdCost;

							spinnerValues.add(nbPossibleKnights);
							spinnerValues.add(nbPossibleOnagers);
							spinnerValues.add(nbPossiblePikemen);
						}

						for (int i = 0; i < Settings.nbTroopTypes; ++i) {
							final int initialValue;
							if (firstFrame) {
								initialValue = 0;
								// TODO: WTF
								firstFrame = false;
							} else {
								initialValue = recruitSpinners.get(i).getValue();
							}

							final SpinnerValueFactory<Integer> factory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, spinnerValues.get(i), initialValue);
							recruitSpinners.get(i).setValueFactory(factory);
						}

						for (int i = 0; i < recruitSpinners.size(); ++i) {
							recruitCommand.get(i).set(recruitSpinners.get(i).getValue());
						}
					}
				} else if (getView() == StatusBarView.TroopsMoveView) {
					if (getCurrentCastle().getOwner() == 0) {
						final ArrayList<Integer> spinnerValues = new ArrayList<>();
						spinnerValues.add(getCurrentCastle().getNbKnights());
						spinnerValues.add(getCurrentCastle().getNbOnagers());
						spinnerValues.add(getCurrentCastle().getNbPikemen());
						for (int i = 0; i < Settings.nbTroopTypes; ++i) {
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

						for (int i = 0; i < moveSpinners.get(1).getValue(); ++i) {
							selectedTroops.add(getCurrentCastle().getOnagerByIndex(i));
						}

						for (int i = 0; i < moveSpinners.get(0).getValue(); ++i) {
							selectedTroops.add(getCurrentCastle().getKnightByIndex(i));
						}

						for (int i = 0; i < moveSpinners.get(2).getValue(); ++i) {
							selectedTroops.add(getCurrentCastle().getPikemanByIndex(i));
						}
					}
				} else if (getView() == StatusBarView.MoneyTransferView) {
					final int initialValue;
					if (firstFrame) {
						initialValue = 0;
						firstFrame = false;
					} else {
						initialValue = moneySpinner.getValue();
					}
					final SpinnerValueFactory<Integer> factory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, getCurrentCastle().getTreasure(), initialValue);
					moneySpinner.setValueFactory(factory);

					moneyToTransfer = moneySpinner.getValue();
				}

				// Should be done only when the view is changed
				if (shouldRefreshView) {
					setText("");

					firstFrame = true;

					confirmChoice.removeFromCanvas();

					for (Button button : decisionButtons) {
						button.removeFromCanvas();
					}

					for (Spinner<Integer> spinner : recruitSpinners) {
						spinner.setVisible(false);
					}

					for (Spinner<Integer> spinner : moveSpinners) {
						spinner.setVisible(false);
					}

					moneySpinner.setVisible(false);

					for (Button target : castleEnemyTargets) {
						target.removeFromCanvas();
					}

					for (Button target : castleAllyTargets) {
						target.removeFromCanvas();
					}

					for (Button target : castleMoneyTargets) {
						target.removeFromCanvas();
					}

					if (getView() == StatusBarView.CastleView) {
						if (getCurrentCastle().getOwner() == 0) {
							for (Button button : decisionButtons) {
								button.addToCanvas();
							}
						}
					} else if (getView() == StatusBarView.TroopsRecruitView) {
						// TODO: White space in function of spinner size
						setText("Chevaliers:                 Onagres:                    Piquiers:");
						confirmChoice.addToCanvas();
						for (Spinner<Integer> spinner : recruitSpinners) {
							spinner.setVisible(true);


						}
					} else if (getView() == StatusBarView.TroopsMoveView) {
						// TODO: White space in function of spinner size
						setText("Chevaliers:                 Onagres:                    Piquiers:");
						for (Spinner<Integer> spinner : moveSpinners) {
							spinner.setVisible(true);
						}

						for (int i = 0; i < castles.size(); ++i) {
							if (castles.get(i) == getCurrentCastle()) {
								continue;
							}

							if (castles.get(i).getOwner() == getCurrentCastle().getOwner()) {
								castleAllyTargets.get(i).addToCanvas();
							} else{
								castleEnemyTargets.get(i).addToCanvas();
							}
						}
					} else if(getView() == StatusBarView.MoneyTransferView) {
						setText("Money to transfer:");

						moneySpinner.setVisible(true);

						for (int i = 0; i < castles.size(); ++i) {
							if (castles.get(i) == getCurrentCastle()) {
								continue;
							}

							if (castles.get(i).getOwner() == getCurrentCastle().getOwner()) {
								castleMoneyTargets.get(i).addToCanvas();
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

				String[] buttonPaths1 = new String[6];
				buttonPaths1[0] = "recruit.png";
				buttonPaths1[1] = "select_troops.png";
				buttonPaths1[2] = "level_up.png";
				buttonPaths1[3] = "money.png";
				buttonPaths1[4] = "wall.png";
				buttonPaths1[5] = "barracks.png";


				final int xOfSet = -75;
				final int yOfSet = 25;
				Point2D buttonPos = new Point2D(getPosition().getX(), getPosition().getY());
				Point2D confirmChoiceButtonPos = new Point2D(getPosition().getX()+xOfSet,getPosition().getY() + yOfSet);
				Button confirmButton = new Button(renderLayer,confirmChoiceButtonPos,new Image("/sprites/buttons/confirm_choice.png"));
				confirmChoice = confirmButton;

				for (String buttonPath : buttonPaths1) {
					Button button = new Button(renderLayer, buttonPos, new Image("/sprites/buttons/" + buttonPath));
					decisionButtons.add(button);

					buttonPos = new Point2D(buttonPos.getX() + buttonWidth, buttonPos.getY());
				}

				decisionButtons.get(0).getTextureView().setOnMouseClicked(e -> {
					setTroopsRecruitView();
					e.consume();
				});

				decisionButtons.get(1).getTextureView().setOnMouseClicked(e -> {
					setTroopsMoveView();
					e.consume();
				});

				decisionButtons.get(2).getTextureView().setOnMouseClicked(e -> {
					Alert alert = new Alert(Alert.AlertType.NONE);
					if (getCurrentCastle().getOwner() == 0) {
						if (getCurrentCastle().canLevelUp() && !getCurrentCastle().isGettingWall()) {
							alert.setAlertType(Alert.AlertType.CONFIRMATION);
							alert.setContentText("Vous êtes sur? Ça vous coûtera " + getCurrentCastle().getNextLevelBuildCost() + " florains.");

							Optional<ButtonType> result = alert.showAndWait();
							if (result.get() == ButtonType.OK) {
								getCurrentCastle().levelUp();
							}
						} else {
							alert.setAlertType(Alert.AlertType.WARNING);
							alert.setContentText("Vous ne pouvez pas améliorer votre château, soit parce qu'il est déjà en travaux, soit parce que vous n'avez pas assez de florains.");
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
				decisionButtons.get(3).getTextureView().setOnMouseClicked(e -> {
					setMoneyTransfertView();
					e.consume();
				});

				decisionButtons.get(4).getTextureView().setOnMouseClicked(e -> {
					Alert alert = new Alert(Alert.AlertType.NONE);
					if (getCurrentCastle().getOwner() == 0) {
						if (!getCurrentCastle().hasWall() && !getCurrentCastle().isLevelingUp() && getCurrentCastle().getTreasure() >= getCurrentCastle().getWallCost()) {
							alert.setAlertType(Alert.AlertType.CONFIRMATION);
							alert.setContentText("Vous �tes sur ? Cela vous co�teras " + getCurrentCastle().getWallCost() + " florains.");

							Optional<ButtonType> result = alert.showAndWait();
							if (result.get() == ButtonType.OK) {
								getCurrentCastle().addWall();
							}
						} else {
							alert.setAlertType(Alert.AlertType.WARNING);
							alert.setContentText("Vous ne pouvez pas construire de muraille car soit votre ch�teau est d�j� en travaux ou vous n'avez pas assez de florains");
							alert.show();
						}
					} else {
						alert.setAlertType(Alert.AlertType.WARNING);
						alert.setTitle("Attention");
						alert.setContentText("Ce n'est pas votre ch�teau");
						alert.show();
					}
					e.consume();
				});
				decisionButtons.get(5).getTextureView().setOnMouseClicked(e -> {
					Alert alert = new Alert(Alert.AlertType.NONE);
					if (getCurrentCastle().getOwner() == 0) {
						if (!getCurrentCastle().isLevelingUp() && !getCurrentCastle().isGettingWall() &&  getCurrentCastle().getTreasure() >= getCurrentCastle().gettBarracksBuildCost()) {
							alert.setAlertType(Alert.AlertType.CONFIRMATION);
							alert.setContentText("Vous �tes sur ? Cela vous co�teras " + getCurrentCastle().gettBarracksBuildCost() + " florains.");

							Optional<ButtonType> result = alert.showAndWait();
							if (result.get() == ButtonType.OK) {
								getCurrentCastle().addBarraks();;
							}
						} else {
							alert.setAlertType(Alert.AlertType.WARNING);
							alert.setContentText("Vous ne pouvez pas construire de caserne car soit votre ch�teau est d�j� en travaux ou vous n'avez pas assez de florains");
							alert.show();
						}
					} else {
						alert.setAlertType(Alert.AlertType.WARNING);
						alert.setTitle("Attention");
						alert.setContentText("Ce n'est pas votre ch�teau");
						alert.show();
					}
					e.consume();
				});

				// Spinners for recruitment
				for (int i = 0; i < Settings.nbTroopTypes; ++i) {
					recruitCommand.add(new AtomicInteger(0));
				}

				// Spinners for troop selection
				recruitSpinners = new ArrayList<>();
				final int yOffset = 30;
				Point2D spinnerPosition = new Point2D(getPosition().getX(), getPosition().getY());

				final double spinnerSize = getSize().getX() / Settings.nbTroopTypes;
				for (int i = 0; i < Settings.nbTroopTypes; ++i) {
					final Spinner<Integer> spinner = new Spinner<>();

					spinner.setTranslateX(spinnerPosition.getX());
					spinner.setTranslateY(spinnerPosition.getY() + yOffset);

					spinner.setPrefWidth(spinnerSize);

					root.getChildren().addAll(spinner);
					spinner.setVisible(false);

					recruitSpinners.add(spinner);

					spinnerPosition = new Point2D(spinnerPosition.getX() + spinnerSize, spinnerPosition.getY());
				}

				moveSpinners = new ArrayList<>();

				spinnerPosition = new Point2D(getPosition().getX(), getPosition().getY());
				for (int i = 0; i < Settings.nbTroopTypes; ++i) {
					final Spinner<Integer> spinner = new Spinner<>();

					spinner.setTranslateX(spinnerPosition.getX());
					spinner.setTranslateY(spinnerPosition.getY() + yOffset);

					spinner.setPrefWidth(spinnerSize);

					root.getChildren().addAll(spinner);
					spinner.setVisible(false);

					moveSpinners.add(spinner);

					spinnerPosition = new Point2D(spinnerPosition.getX() + spinnerSize, spinnerPosition.getY());
				}

				// Spinner for money
				moneySpinner = new Spinner<Integer>();
				moneySpinner.setPrefWidth(Settings.windowWidth / 3.0);
				moneySpinner.setTranslateX(getPosition().getX());
				moneySpinner.setTranslateY(getPosition().getY() + yOffset);
				moneySpinner.setEditable(true);

				root.getChildren().add(moneySpinner);
			}

			@Override
			public void setCastleView(Castle castle) {
				super.setCastleView(castle);

				if (castle.getOwner() == 0) {
					// TODO: Add the real maximum value we can produce
					final ArrayList<Integer> recruitSpinnerValues = new ArrayList<>();
					recruitSpinnerValues.add(100);
					recruitSpinnerValues.add(100);
					recruitSpinnerValues.add(100);
					for (int i = 0; i < Settings.nbTroopTypes; ++i) {
						SpinnerValueFactory<Integer> factory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, recruitSpinnerValues.get(i), 0);
						recruitSpinners.get(i).setValueFactory(factory);
					}

					final ArrayList<Integer> moveSpinnerValues = new ArrayList<>(0);
					moveSpinnerValues.add(getCurrentCastle().getNbKnights());
					moveSpinnerValues.add(getCurrentCastle().getNbOnagers());
					moveSpinnerValues.add(getCurrentCastle().getNbPikemen());
					for (int i = 0; i < Settings.nbTroopTypes; ++i) {
						SpinnerValueFactory<Integer> factory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, moveSpinnerValues.get(i), 0);
						moveSpinners.get(i).setValueFactory(factory);
					}
				}
			}
		};
		centerStatusBar.setDefaultMenuView();
		statusBars.add(centerStatusBar);

		statusBarPos = new Point2D(statusBarPos.getX() + Settings.windowWidth / 3, statusBarPos.getY());
		StatusBar rightStatusBar = new StatusBar(renderLayer, statusBarPos, statusBarSize, "rightStatusBar") {
			@Override
			public void updateView() {
				setText("");
				if (getView() == StatusBarView.CastleView) {
					String text = "Chevaliers: " + getCurrentCastle().getNbKnights() + "\n"
							+ "Onagres: " + getCurrentCastle().getNbOnagers() + "\n"
							+ "Piquiers: " + getCurrentCastle().getNbPikemen() + "\n";
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
	 * Loads the status bars that are at the top of the application.
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

		// TODO: Is there a way to store functions in array?
		defaultMenuButtons.get(0).getTextureView().setOnMouseClicked(e -> {
			setGameView();
			e.consume();
		});

		defaultMenuButtons.get(1).getTextureView().setOnMouseClicked(e -> {
			GameIO.loadGame(this, "resources/dukes_advanced.sav", renderLayer);
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
			GameIO.saveGame(this, "resources/dukes_advanced.sav");

			saveButton.removeFromCanvas();
			isSaveButtonDisplayed = false;

			// Stop the pause
			isRunning = !isRunning;
			e.consume();
		});
	}

	/**
	 * Loads the castles textures and creates them.
	 */
	private void createCastles() {
		final int widthUpperBound = Settings.gridCellsCountX - Settings.castleSize;
		final int heightUpperBound = Settings.gridCellsCountY - Settings.castleSize;

		final int nbActiveDukes = rdGen.nextInt(Settings.nbMaxActiveDukes);
		final int nbNeutralDukes = Settings.nbMinCastles + rdGen.nextInt(Settings.nbMaxCastles - nbActiveDukes) - 1;
		final int nbCastles = 1 + nbActiveDukes + nbNeutralDukes;

		// 0 is the player
		// [1, nbActiveDukes] is for active dukes
		// [1+nbActiveDukes, 1+nbActiveDukes+nbNeutralDukes] is for neutral dukes
		int castleOwner = 0;
		while (castles.size() < nbCastles) {
			int cellSize = Settings.cellSize;
			Point2D position = new Point2D(rdGen.nextInt(widthUpperBound/cellSize -3)*cellSize +20, Settings.statusBarHeight + 30 + rdGen.nextInt(heightUpperBound/cellSize - 4)*cellSize);
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

		final Castle playerCastle = castles.get(0);
		playerCastles.add(playerCastle);
		for (Castle castle : castles) {
			
			for(int i = 0; i < Settings.castleSize/Settings.cellSize; i++) {
				for(int j = 0; j < Settings.castleSize/Settings.cellSize; j++) {
					int x = (int)castle.getPosition().getX() / Settings.cellSize;
					int y = (int)(castle.getPosition().getY() - Settings.statusBarHeight)/Settings.cellSize;
					gameMap[x + i][y + j] = 1;
					switch(castle.getDoorDirection()) {
					case(0):
						for(int k = 0; k < 3; k++) {
							gameMap[x+2][y+2+k] = 0;
						}
					break;
					case(1):
						for(int k = 0; k < 3; k++) {
							gameMap[x+2-k][y+2] = 0;
						}
					break;
					case(2):
						for(int k = 0; k < 3; k++) {
							gameMap[x+2][y+2-k] = 0;
						}
					break;
					case(3):
						for(int k = 0; k < 3; k++) {
							gameMap[x+2+k][y+2] = 0;
						}
					break;
					}
				}
			}
			
			castle.getTextureView().setOnMouseClicked(e -> {
				if (castle.getOwner() == 0) {
					currentPlayerCastle = castle;
				}
				for (StatusBar statusBar : statusBars) {
					statusBar.setCastleView(castle);
				}
				e.consume();
			});
		}

		createCastleTargets();
	}

	/**
	 * Creates the targets to be clicked on.
	 */
	private void createCastleTargets() {
		castleEnemyTargets.clear();
		castleAllyTargets.clear();

		Image enemyTargetTexture = new Image("/sprites/castles/ennemyTarget.png");
		Image allyTargetTexture = new Image("/sprites/castles/allyTarget.png");
		Image moneyTargetTexture = new Image("/sprites/castles/moneyTarget.png");

		ArrayList<Button> targetList = new ArrayList<>();

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
				moveTroop(selectedTroops, castle,true);
				e.consume();
			});
			castleEnemyTargets.add(enemyTargetButton);

			Button allyTargetButton = new Button(renderLayer, pos, allyTargetTexture);
			allyTargetButton.getTextureView().setFitWidth(Settings.castleSize);
			allyTargetButton.getTextureView().setFitHeight(Settings.castleSize);
			allyTargetButton.getTextureView().setPickOnBounds(true);
			allyTargetButton.getTextureView().setOnMouseClicked(e -> {
				moveTroop(selectedTroops,castle,false);
				e.consume();
			});
			castleAllyTargets.add(allyTargetButton);

			Button moneyTargetButton = new Button(renderLayer, castle.getPosition(), moneyTargetTexture);
			moneyTargetButton.getTextureView().setOnMouseClicked(e -> {
				if(currentPlayerCastle.getTreasure() >= moneyToTransfer) {
					currentPlayerCastle.setTreasure(currentPlayerCastle.getTreasure() - moneyToTransfer);
					Business business = new Business(renderLayer, currentPlayerCastle, moneyToTransfer);
					displacement(currentPlayerCastle.getPosition(), castle, business,true, business.getSpeed());
				}
				e.consume();
			});
			castleMoneyTargets.add(moneyTargetButton);

			targetList.add(allyTargetButton);
			targetList.add(enemyTargetButton);
			targetList.add(moneyTargetButton);
		}

		//Initialize evry target button
		for (Button button : targetList) {
			button.getTextureView().setFitWidth(Settings.castleSize);
			button.getTextureView().setFitHeight(Settings.castleSize);
			button.getTextureView().setPickOnBounds(true);
		}
	}

	/**
	 * Moves selected troops to a castle
	 * @param selectedTroops The troops to move.
	 * @param castle The castle that will receive the troops
	 * @param castleOwned True if the castle belongs to the player, false otherwise.
	 */
	public void moveTroop(ArrayList<Troop> selectedTroops, Castle castle, boolean castleOwned) {
		while(selectedTroops.size() >= Settings.ostSize) {
			int minSpeed = Math.min(selectedTroops.get(0).getSpeed(), selectedTroops.get(1).getSpeed());
			minSpeed = Math.min(minSpeed, selectedTroops.get(2).getSpeed());
			for(int i = 0; i < Settings.ostSize; i++) {
				currentPlayerCastle.removeTroop(selectedTroops.get(0));
				displacement(currentPlayerCastle.getPosition(), castle, selectedTroops.get(0), castleOwned, minSpeed);
				selectedTroops.remove(0);
			}
		}

		for(int i = 0; i < selectedTroops.size(); i++) {
			currentPlayerCastle.removeTroop(selectedTroops.get(0));
			displacement(currentPlayerCastle.getPosition(), castle, selectedTroops.get(0), castleOwned, selectedTroops.get(0).getSpeed());
			selectedTroops.remove(0);
		}
	}

	/**
	 * Launches the move animation.
	 * @param playerCastlePosition The starting position.
	 * @param targetedCastle The castle that will receive the troop.
	 * @param unit The troop to move.
	 * @param castleOwned True if the castle belongs to the player, false otherwise.
	 * @param speed The speed of the movement.
	 */
	private void displacement(Point2D playerCastlePosition, Castle targetedCastle, Troop unit, boolean castleOwned, int speed) {
		int dxy = Settings.castleSize / 2;
		Node start = new Node(new Point2D(playerCastlePosition.getX() + dxy, playerCastlePosition.getY() + dxy), 0, 0);
		Node end = new Node(new Point2D(targetedCastle.getPosition().getX() + dxy, targetedCastle.getPosition().getY() + dxy), 0, 0);
		Double[] path = AStar.shortestPath(start, end, gameMap, true, castleOwned);
		String unitPathName;
		if(unit.getClass() == Pikeman.class) {
			unitPathName = "pikeman";
		} else if (unit.getClass() == Knight.class) {
			unitPathName = "knight";
		} else if (unit.getClass() == Onager.class) {
			unitPathName = "onager";
		} else {
			unitPathName = "money";
		}
		Button unitButton = unit.spawnTroop(unitPathName, 0, playerCastlePosition, path, renderLayer);
		unit.setUnitButton(unitButton);
		unit.displace(path, renderLayer, unitButton, unit, gameMap, targetedCastle, castleOwned, speed);
	}

	/**
	 * Recruit troops.
	 * @param recruitCommand The command to recruit.
	 * @param barrackLevel The level of the barrack.
	 */
	private void recruitTroops(ArrayList<AtomicInteger> recruitCommand, int barrackLevel) {
		int nbKnights = recruitCommand.get(0).get();
		int nbOnagers = recruitCommand.get(1).get();
		int nbPikemen = recruitCommand.get(2).get();
		
		while ((nbKnights + nbOnagers + nbPikemen) > 0) {
				if (nbKnights >0) {
					--nbKnights;
					Knight knight = new Knight(renderLayer, currentPlayerCastle);
					currentPlayerCastle.setTreasure(currentPlayerCastle.getTreasure() - knight.getProdCost());
					currentPlayerCastle.addTroop(knight);
				} else if (nbOnagers>0) {
					--nbOnagers;
					Onager onager= new Onager(renderLayer, currentPlayerCastle);
					currentPlayerCastle.setTreasure(currentPlayerCastle.getTreasure() - onager.getProdCost());
					currentPlayerCastle.addTroop(onager);
				} else {
					--nbPikemen;
					Pikeman pikeman = new Pikeman(renderLayer, currentPlayerCastle);
					currentPlayerCastle.setTreasure(currentPlayerCastle.getTreasure() - pikeman.getProdCost());
					currentPlayerCastle.addTroop(pikeman);
				}
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
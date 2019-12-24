package base;

import buildings.Castle;

import renderer.Button;
import renderer.Background;
import renderer.StatusBar;
import renderer.StatusBarView;

import javafx.animation.AnimationTimer;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import algorithms.AStar;
import algorithms.Node;

public class Game {
	private Group root;
	private Pane renderLayer;
	private Input input;
	public static int[][]tab = new int[Settings.gridCellsCountX/Settings.cellSize][Settings.gridCellsCountY/Settings.cellSize];

	private Random rdGen = new Random();

	private GameMode gameMode;

	public Game() {
		this.root = new Group();
		this.root.getStylesheets().add("/css/application.css");

		this.renderLayer = new Pane();
		this.renderLayer.setPrefSize(Settings.windowWidth, Settings.windowHeight);
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

		root.getChildren().add(this.renderLayer);
	}

	private boolean isRunning = true;
	private int frameCounter = 0;
	private int framesPerDay = 60; // One second.

	private DayHolder currentDayHolder = new DayHolder();

	public void run() {
		loadGame();

		AnimationTimer gameLoop = new AnimationTimer() {
			@Override
			public void handle(long now) {
				processInput(input, now);
				for (StatusBar statusBar : statusBars) {
					statusBar.updateView();
				}

				if (isRunning) {
					processInput(input, now);

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

	private Background menuBackground;
	private Background gameBackground;

	private void loadGame() {
		input = new Input(this.root.getScene());

		menuBackground = new Background(renderLayer, new Image("/sprites/backgrounds/menu_background.png"));
		gameBackground = new Background(renderLayer, new Image("/sprites/backgrounds/game_background.png"));

		// TODO: Initialise input and add listeners
		input.addListeners();

		createStatusBar();

		createMenuButtons();

		// Set to Menu view by default
		setMenuView();
	}

	private List<Button> defaultMenuButtons = new ArrayList<>();

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
//			setLoadGameView();
			e.consume();
		});

		defaultMenuButtons.get(2).getTextureView().setOnMouseClicked(e -> {
			setCreditsView();
			e.consume();
		});
	}

	private List<Castle> castles = new ArrayList<>();
	private List<Button> castleTargets = new ArrayList<>();

	private void createCastles() {
		final int widthUpperBound = Settings.gridCellsCountX - Settings.castleSize;
		final int heightUpperBound = Settings.gridCellsCountY - Settings.castleSize;

		final int nbActiveDukes = 1 + rdGen.nextInt(Settings.nbMaxActiveDukes);
		final int nbNeutralDukes = Settings.nbMinCastles + rdGen.nextInt(1 + Settings.nbMaxCastles - nbActiveDukes) - 1;
		final int nbCastles = 1 + nbActiveDukes + nbNeutralDukes;

		// 0 is the player
		// [1, nbActiveDukes] is for active dukes
		// [1+nbActiveDukes, 1+nbActiveDukes+nbNeutralDukes] is for neutral dukes
		int castleOwner = 0;
		while (castles.size() < nbCastles) {
			int cellSize = Settings.cellSize;
			Point2D position = new Point2D(rdGen.nextInt(widthUpperBound/cellSize)*cellSize, Settings.statusBarHeight + rdGen.nextInt(heightUpperBound/cellSize)*cellSize);
			if (!isPositionNearACastle(position)) {
				castles.add(new Castle(renderLayer, castleOwner, position));
				++castleOwner;
			}
		}

		Image target = new Image("/sprites/castles/target.png");

		final Castle playerCastle = castles.get(0);
		for (Castle castle : castles) {
			castle.getTextureView().setOnMouseClicked(e -> {
				for (StatusBar statusBar : statusBars) {
					statusBar.setCastleView(castle);
				}
				e.consume();
			});

			if (castle.getOwner() == 0) {
				continue;
			}

			Point2D pos = new Point2D(castle.getPosition().getX(), castle.getPosition().getY());
			Button targetButton = new Button(renderLayer, pos, target);
			targetButton.getTextureView().setFitWidth(Settings.castleSize);
			targetButton.getTextureView().setFitHeight(Settings.castleSize);
			targetButton.addToCanvas();
			targetButton.getTextureView().setPickOnBounds(true);
			targetButton.getTextureView().setOnMouseClicked(e -> {
				// We use targetButton.getPosition because it's the same as castle position
				Node start = new Node(playerCastle.getPosition().getX(), playerCastle.getPosition().getY(), 0, 0);
				Node end = new Node(castle.getPosition().getX(), castle.getPosition().getY(), 0, 0);
				AStar.CheminPlusCourt(start, end, renderLayer, true);
				e.consume();
			});
			castleTargets.add(targetButton);
		}
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

	private List<StatusBar> statusBars = new ArrayList<>();

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

		statusBarPos = new Point2D(statusBarPos.getX() + Settings.windowWidth / 3.0, statusBarPos.getY());
		StatusBar centerStatusBar = new StatusBar(renderLayer, statusBarPos, statusBarSize, "centerStatusBar") {
			private List<Button> decisionButtons;

			private List<Spinner<Integer>> moveSpinners;

			@Override
			public void updateView() {
				if (shouldRefreshView) {
					setText("");

					for (Button button : decisionButtons) {
						button.removeFromCanvas();
					}

					for (Spinner<Integer> spinner : moveSpinners) {
						spinner.setVisible(false);
					}

					for (Button target : castleTargets) {
						target.removeFromCanvas();
					}

					if (getView() == StatusBarView.CastleView) {
						if (getCurrentCastle().getOwner() == 0) {
							for (Button button : decisionButtons) {
								button.addToCanvas();
							}
						}
					} else if (getView() == StatusBarView.TroopsMoveView) {
						// TODO
						setText("Chevaliers:");
						for (Spinner<Integer> spinner : moveSpinners) {
							spinner.setVisible(true);
						}

						for (Button target : castleTargets) {
							target.addToCanvas();
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

				String[] decisionButtonsPaths = new String[2];
				decisionButtonsPaths[0] = "recruit.png";
				decisionButtonsPaths[1] = "select_troops.png";

				Point2D buttonPos = new Point2D(getPosition().getX(), getPosition().getY());

				for (String buttonPath : decisionButtonsPaths) {
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

					root.getChildren().addAll(spinner);
					spinner.setVisible(false);

					moveSpinners.add(spinner);

					spinnerPosition = new Point2D(spinnerPosition.getX() + spinnerSize, spinnerPosition.getY());
				}
			}

			@Override
			public void setCastleView(Castle castle) {
				super.setCastleView(castle);

				if (castle.getOwner() == 0) {
					final ArrayList<Integer> spinnerValues = new ArrayList<>(0);
					spinnerValues.add(castle.getNbKnights());
					for (int i = 0; i < Settings.nbDiffTroopTypes; ++i) {
						SpinnerValueFactory<Integer> factory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, spinnerValues.get(i), 0);
						moveSpinners.get(i).setValueFactory(factory);
					}
				}
			}
		};
		centerStatusBar.setDefaultMenuView();
		statusBars.add(centerStatusBar);

		statusBarPos = new Point2D(statusBarPos.getX() + Settings.windowWidth / 3.0, statusBarPos.getY());
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

	private void setCreditsView() {
		for (StatusBar statusBar : statusBars) {
			statusBar.setCreditsView();
		}
	}

	public Group getRoot() {
		return root;
	}
}
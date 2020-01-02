package base;

import buildings.Castle;

import renderer.Button;
import renderer.Background;
import renderer.StatusBar;
import renderer.StatusBarView;
import troops.Knight;
import troops.Onager;
import troops.Pikeman;
import troops.Troop;
import javafx.animation.AnimationTimer;
import javafx.animation.TranslateTransition;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import algorithms.AStar;
import algorithms.Node;

public class Game {
	private Group root;
	private Pane renderLayer;

	private int[][]gameMap = new int[Settings.gridCellsCountX / Settings.cellSize][Settings.gridCellsCountY / Settings.cellSize];
	public static ArrayList<Castle> castleOwned = new ArrayList<Castle>();

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

	private void loadGame() {
		menuBackground = new Background(renderLayer, new Image("/sprites/backgrounds/menu_background.png"));
		gameBackground = new Background(renderLayer, new Image("/sprites/backgrounds/game_background.png"));

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
				castles.add(new Castle(renderLayer, castleOwner, position));
				++castleOwner;
			}
		}

		Image ennemyTarget = new Image("/sprites/castles/ennemyTarget.png");
		Image allyTarget = new Image("/sprites/castles/allyTarget.png");

		final Castle playerCastle = castles.get(0);
		castleOwned.add(playerCastle);
		for (Castle castle : castles) {
			
			for(int i = 0; i < Settings.castleSize/Settings.cellSize; i++) {
				for(int j = 0; j < Settings.castleSize/Settings.cellSize; j++) {
					int x = (int) castle.getPosition().getX()/Settings.cellSize;
					int y = (int) (castle.getPosition().getY() - Settings.statusBarHeight)/Settings.cellSize;
					gameMap[(int) x + i][(int) y + j] = 1;
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
				for (StatusBar statusBar : statusBars) {
					statusBar.setCastleView(castle);
					castle.setCurrentCastle(true);
				}
				e.consume();
			});
			if (castleOwned.contains(castle) && !castle.isSurrounded() && !castle.getCurrentCastle()) {
				Button targetButton = new Button(renderLayer, castle.getPosition(), allyTarget);
				targetButton.getTextureView().setFitWidth(Settings.castleSize);
				targetButton.getTextureView().setFitHeight(Settings.castleSize);
				targetButton.getTextureView().setPickOnBounds(true);
				targetButton.getTextureView().setOnMouseClicked(e -> {
					// We use targetButton.getPosition because it's the same as castle position
					Pikeman pikeman = new Pikeman(renderLayer,playerCastle);
					Knight knight = new Knight(renderLayer,playerCastle);
					Onager onager = new Onager(renderLayer,playerCastle);
					displacement(playerCastle.getPosition(),castle,pikeman,true);
					displacement(playerCastle.getPosition(),castle,onager,true);
					displacement(playerCastle.getPosition(),castle,knight,true);
					e.consume();
				});
				castleTargets.add(targetButton);
			}else{
				
			Button targetButton = new Button(renderLayer, castle.getPosition(), ennemyTarget);
			targetButton.getTextureView().setFitWidth(Settings.castleSize);
			targetButton.getTextureView().setFitHeight(Settings.castleSize);
			targetButton.getTextureView().setPickOnBounds(true);
			targetButton.getTextureView().setOnMouseClicked(e -> {
				// We use targetButton.getPosition because it's the same as castle position
				Pikeman pikeman = new Pikeman(renderLayer,playerCastle);
				Knight knight = new Knight(renderLayer,playerCastle);
				Onager onager = new Onager(renderLayer,playerCastle);
				displacement(playerCastle.getPosition(),castle,pikeman,false);
				displacement(playerCastle.getPosition(),castle,onager,false);
				displacement(playerCastle.getPosition(),castle,knight,false);
				
				e.consume();
			});
			castleTargets.add(targetButton);
			}
		}
		
	}
	
	private void displacement(Point2D playerCastlePosition, Castle targetedCastle,Troop unit, boolean castleOwned) {
		int dxy = Settings.castleSize/2;
		Node start = new Node(playerCastlePosition.getX() + dxy, playerCastlePosition.getY() + dxy, 0, 0);
		Node end = new Node(targetedCastle.getPosition().getX() + dxy, targetedCastle.getPosition().getY() + dxy, 0, 0);
		Double[] path = AStar.CheminPlusCourt(start, end, gameMap , renderLayer, true,castleOwned);
		String unitName;
		if(unit.getClass() == Pikeman.class) {
			unitName = "pikeman";
		}else if(unit.getClass() == Knight.class){
			unitName = "knight";
		}else {
			unitName = "onager";
		}
		Button unitButton = unit.spawnTroop(unitName, 0, playerCastlePosition, path,renderLayer);
		unit.setUnitButton(unitButton);
		unit.displace(path, renderLayer,unitButton,unit, gameMap,targetedCastle);
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
	
	private ArrayList<Troop> selectedTroops = new ArrayList<>();

	private List<StatusBar> statusBars = new ArrayList<>();

	private void createStatusBar() {
		final Point2D statusBarSize = new Point2D(Settings.windowWidth / 3, Settings.statusBarHeight);
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
					String text = "Duc du château: " + getCurrentCastle().getOwnerName() + "\n"
							+ "Niveau: " + getCurrentCastle().getLevel() + "\n"
							+ "Revenu: " + getCurrentCastle().getPassiveIncome() + "\n"
							+ "Trésor: " + getCurrentCastle().getTreasure() + "\n";

					if (getCurrentCastle().isLevelingUp()) {
						text += "Jours jusqu'à évolution: " + getCurrentCastle().getNextLevelRemainingTime();
					}

					setText(text);
				}else if(getView() == StatusBarView.TroopsMoveView) {
					
				}
			}
		};
		leftStatusBar.setDayHolder(currentDayHolder);
		leftStatusBar.setDefaultMenuView();
		statusBars.add(leftStatusBar);

		statusBarPos = new Point2D(statusBarPos.getX() + Settings.windowWidth / 3, statusBarPos.getY());
		StatusBar centerStatusBar = new StatusBar(renderLayer, statusBarPos, statusBarSize, "centerStatusBar") {
			private List<Button> decisionButtons;

			private List<Spinner> recruitSpinners;
			private List<Spinner> moveSpinners;

			@Override
			public void updateView() {
				if (shouldRefreshView) {
					setText("");

					for (Button button : decisionButtons) {
						button.removeFromCanvas();
					}

					for (Spinner spinner : recruitSpinners) {
						spinner.setVisible(false);
					}

					for (Spinner spinner : moveSpinners) {
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
					} else if (getView() == StatusBarView.TroopsRecruitView) {
						setText("Chevaliers:                 Onagres:                    Piquiers:");
						for (Spinner spinner : recruitSpinners) {
							spinner.setVisible(true);
						}
					} else if (getView() == StatusBarView.TroopsMoveView) {
						// TODO
						setText("Chevaliers:                 Onagres:                    Piquiers:");
						for (Spinner spinner : moveSpinners) {
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

				String[] buttonPaths1 = new String[3];
				buttonPaths1[0] = "recruit.png";
				buttonPaths1[1] = "select_troops.png";
				buttonPaths1[2] = "level_up.png";

				Point2D buttonPos = new Point2D(getPosition().getX(), getPosition().getY());

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
						if (getCurrentCastle().canLevelUp()) {
							alert.setAlertType(Alert.AlertType.CONFIRMATION);
							alert.setContentText("Vous êtes sur? Ça vous coûtera " + getCurrentCastle().getNextLevelBuildCost() + " florains.");

							Optional<ButtonType> result = alert.showAndWait();
							if (result.get() == ButtonType.OK) {
								getCurrentCastle().levelUp();
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

				// Spinners for recruitment
				// TODO
				recruitSpinners = new ArrayList<>();

				// Spinners for troop selection
				moveSpinners = new ArrayList<>();

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

					moveSpinners.add(spinner);

					spinnerPosition = new Point2D(spinnerPosition.getX() + spinnerSize, spinnerPosition.getY());
				}
			}

			@Override
			public void setCastleView(Castle castle) {
				super.setCastleView(castle);

				if (castle.getOwner() == 0) {
					final int spinnerValues[] = {castle.getNbKnights(), castle.getNbOnagers(), castle.getNbPikemen()};
					for (int i = 0; i < Settings.nbTroopTypes; ++i) {
						SpinnerValueFactory<Integer> factory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, spinnerValues[i], 0);
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
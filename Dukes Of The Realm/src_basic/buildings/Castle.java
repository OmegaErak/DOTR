package buildings;

import algorithms.AStar;
import algorithms.Node;

import base.Settings;

import drawable.Sprite;

import troops.Knight;

import javafx.animation.PathTransition;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Polyline;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Random;

/**
 * Castle class.
 */
public class Castle extends Sprite {
	private int owner;
	private String ownerName;

	private ArrayList<Knight> availableKnights = new ArrayList<>();

	private ArrayList<Knight> attackingTroops = new ArrayList<>();

	private final Random rdGen = new Random();

	/**
	 * Default constructor
	 * @param renderLayer The JavaFX canvas onto which we draw.
	 * @param position The position of the castle in the window.
	 */
	public Castle(Pane renderLayer, Point2D position) {
		super(renderLayer, position);

		final Image texture;
		if (owner <= Settings.nbMaxActiveDukes) {
			texture = new Image("/sprites/castles/castle_" + owner + ".png");
		} else {
			texture = new Image("/sprites/castles/castle_neutral.png");
		}

		final int nbDirections = 4;
		final int doorDirection = rdGen.nextInt(nbDirections);

		this.position = position;
		
		final int nbTroops = Settings.minNbInitTroops + rdGen.nextInt(Settings.maxNbInitTroops - Settings.minNbInitTroops);
		for (int i = 0; i < nbTroops; ++i) {
			availableKnights.add(new Knight(renderLayer, this));
		}

		setTexture(texture);

		textureView.setRotate(90 * doorDirection);
		textureView.setFitWidth(Settings.castleSize);
		textureView.setFitHeight(Settings.castleSize);
	}

	/**
	 * Update function that is called every turn (2 seconds).
	 */
	public void onUpdate() {
		final int nbNewTroops = Settings.minNbTroopsAddedPerTurn + rdGen.nextInt(1 + Settings.maxNbTroopsAddedPerTurn - Settings.minNbTroopsAddedPerTurn);
		for (int i = 0; i < nbNewTroops; ++i) {
			availableKnights.add(new Knight(renderLayer, this));
		}

		// Check if getting attacked
		if (attackingTroops.size() != 0) {
			Random rdGen = new Random();

			int damageTaken = 0;
			for (final Knight attackingKnight : attackingTroops) {
				damageTaken += attackingKnight.getDamage();
				attackingKnight.setHP(0);
				attackingKnight.removeFromCanvas();
			}

			for (int i = 0; i < damageTaken; ++i) {
				// Castle is conquered
				if (availableKnights.size() == 0) {
					this.owner = attackingTroops.get(0).getAttachedCastle().getOwner();
					this.ownerName = attackingTroops.get(0).getAttachedCastle().getOwnerName();
					this.textureView.setImage(new Image("/sprites/castles/castle_" + owner + ".png"));
					this.availableKnights.addAll(attackingTroops);
					for (Knight knight : attackingTroops) {
						knight.setPosition(position);
					}
					break;
				}

				final Knight attackedKnight = availableKnights.get(rdGen.nextInt(availableKnights.size()));
				attackedKnight.addHP(-1);
				if (!attackedKnight.isAlive()) {
					availableKnights.remove(attackedKnight);
				}
			}

			attackingTroops.clear();
		}
	}

	/**
	 * Moves troops from the castle to another castle.
	 * @param castle The target castle.
	 * @param moveCommand The number of troops to move.
	 */
	public void orderMove(Castle castle, Integer moveCommand) {
		ArrayList<Knight> selectedTroops = new ArrayList<>();
		for (int i = 0; i < moveCommand; ++i) {
			selectedTroops.add(availableKnights.get(i));
		}

		if(!selectedTroops.isEmpty()) {
			int dxy = Settings.castleSize / 2;
			Node start = new Node(new Point2D(getPosition().getX() + dxy, getPosition().getY() + dxy), 0, 0);
			Node end = new Node(new Point2D(castle.getPosition().getX() + dxy, castle.getPosition().getY() + dxy), 0, 0);
			Double[] usedPath = AStar.shortestPath(start, end, true);

			Polyline pathLine = new Polyline();
			pathLine.getPoints().addAll(usedPath);
			renderLayer.getChildren().add(pathLine);

			Polyline usedPathPolyLine = new Polyline();
			double dx = usedPath[0];
			double dy = usedPath[1];
			for(int i = 0; i < usedPath.length; i++) {
				if(i % 2 == 0) {
					usedPath[i] -= dx;
				} else {
					usedPath[i] -= dy;
				}
			}
			usedPathPolyLine.getPoints().addAll(usedPath);

			for (Knight knight : selectedTroops) {
				knight.addToCanvas();

				final PathTransition moveAnimation = new PathTransition(Duration.seconds((double)usedPath.length / knight.getSpeed()), usedPathPolyLine);
				moveAnimation.setNode(knight.getTextureView());
				moveAnimation.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);
				moveAnimation.setNode(knight.getTextureView());
				moveAnimation.play();
				moveAnimation.setOnFinished(e -> {
					renderLayer.getChildren().remove(pathLine);

					// TODO: Not the best way to do this.
					ArrayList<Knight> troopsList = new ArrayList<>();
					troopsList.add(knight);
					castle.receiveTroops(this, troopsList);
				});

				availableKnights.remove(knight);
			}

			selectedTroops.clear();
		}
	}

	/**
	 * Receives troops from a sender castle.
	 * @param sender The castle that sent the troops.
	 * @param troops The troops.
	 */
	private void receiveTroops(Castle sender, ArrayList<Knight> troops) {
		if (sender.getOwner() == this.owner) {
			availableKnights.addAll(troops);
			for (Knight knight : troops) {
				knight.setPosition(position);
				knight.setAttachedCastle(this);
				knight.removeFromCanvas();
			}
		} else {
			attackingTroops.addAll(troops);
		}
	}

	/**
	 * Sets the owner ID.
	 */
	public void setOwner(int ID) {
		owner = ID;

		final Image texture;
		if (owner <= Settings.nbMaxActiveDukes) {
			texture = new Image("/sprites/castles/castle_" + owner + ".png");
		} else {
			texture = new Image("/sprites/castles/castle_neutral.png");
		}

		setTexture(texture);
	}

	/**
	 * @return The ownerID of the owner.
	 */
	public int getOwner() {
		return owner;
	}

	/**
	 * Sets the owner name.
	 */
	public void setOwnerName(String name) {
		ownerName = name;
	}

	/**
	 * @return The name of the owner.
	 */
	public String getOwnerName() {
		return ownerName;
	}

	/**
	 * @return The number of knights inside the castle.
	 */
	public int getNbKnights() {
		return availableKnights.size();
	}

	/**
	 * Sets the knights of the castle, overriding the previous.
	 * @param knights The knights.
	 */
	public void setKnights(ArrayList<Knight> knights) {
		availableKnights = knights;
	}

	/**
	 * @return The castles knights.
	 */
	public ArrayList<Knight> getKnights() {
		return availableKnights;
	}
}

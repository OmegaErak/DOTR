package troops;

import algorithms.AStar;
import algorithms.Node;
import base.Settings;
import buildings.Castle;

import drawable.Button;
import drawable.Sprite;

import javafx.animation.PathTransition;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Polyline;
import javafx.util.Duration;

// TODO: Get rid of unit button

/**
 * Troop abstract class.
 * To be derived for any other troop.
 */
abstract public class Troop  extends Sprite {
	protected Castle attachedCastle;

	protected int health;
	protected int damage;
	protected int speed;

	protected int prodTime;
	protected int prodCost;

	protected int gameMapPosX;
	protected int gameMapPosY;

	/**
	 * Default constructor
	 * @param renderLayer The JavaFX canvas.
	 * @param castle The castle it is attached to.
	 */
	public Troop(Pane renderLayer, Castle castle) {
		super(renderLayer, castle.getPosition());

		attachedCastle = castle;
		textureView.setFitWidth(Settings.troopsSize);
		textureView.setFitHeight(Settings.troopsSize);

		// Center of castle
		setPosition(new Point2D(castle.getPosition().getX() + Settings.castleSize / 2.0, castle.getPosition().getY() + Settings.castleSize / 2.0));
	}

	/**
	 * Launches the move animation.
	 * @param targetedCastle The castle that will receive the troop.
	 * @param speed The speed of the movement.
	 */
	public void launchMovingAnimation(Castle targetedCastle, int[][] gameMap, int speed) {
		int dxy = Settings.castleSize / 2;
		Node start = new Node(new Point2D(attachedCastle.getPosition().getX() + dxy, attachedCastle.getPosition().getY() + dxy), 0, 0);
		Node end = new Node(new Point2D(targetedCastle.getPosition().getX() + dxy, targetedCastle.getPosition().getY() + dxy), 0, 0);
		Double[] path = AStar.shortestPath(start, end, gameMap, true, targetedCastle.isPlayerCastle());

		if(path != null) {
			Double x = path[path.length-2];
			Double y = path[path.length-1] - Settings.statusBarHeight;
			gameMapPosX = (int)((x - 5) / Settings.cellSize);
			gameMapPosY = (int)((y - 5) / Settings.cellSize);
			if(!targetedCastle.isPlayerCastle()) {
				gameMap[(int)(x - 5) / Settings.cellSize][(int)(y - 5) / Settings.cellSize] = 2;
			}

			Polyline polyLine = new Polyline();
			polyLine.getPoints().addAll(path);
			renderLayer.getChildren().add(polyLine);
			Polyline poly = new Polyline();
			double dx = path[0];
			double dy = path[1];
			gameMap[(int)(dx-5)/Settings.cellSize][(int)(dy-5-Settings.statusBarHeight)/Settings.cellSize]=0;
			for(int i = 0; i < path.length; i++) {
				if(i % 2 == 0) {
					path[i] -= dx;
				} else {
					path[i] -= dy;
				}
			}
			poly.getPoints().addAll(path);

			final PathTransition moveAnimation = new PathTransition(Duration.seconds((double)path.length / speed), poly);
			moveAnimation.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);
			moveAnimation.setNode(textureView);

			addToCanvas();
			moveAnimation.play();
			moveAnimation.setOnFinished(e -> {
				targetedCastle.addAttackingTroop(this);
				if(attachedCastle.getOwner() == targetedCastle.getOwner()) {
					gameMap[(int)(x - 5) / Settings.cellSize][(int)(y - 5) / Settings.cellSize] = 0;
				}
				renderLayer.getChildren().remove(polyLine);

				removeFromCanvas();
			});
		}
	}
	/**
	 * @return The x coordinate on the map.
	 */
	public int getGameMapPosX() {
		return gameMapPosX;
	}

	/**
	 * @return The y coordinate on the map.
	 */
	public int getGameMapPosY() {
		return gameMapPosY;
	}

	/**
	 * @return True if the troop is alive, false otherwise.
	 */
	public boolean isAlive() {
		return health > 0;
	}

	/**
	 * @return The production time of the troop.
	 */
	public int getProdTime() {
		return prodTime;
	}

	/**
	 * Decrements the production time of the troop.
	 */
	public void decrementProdTime() {
		--prodTime;
	}

	/**
	 * @return The speed of the troop.
	 */
	public int getSpeed() {
		return speed;
	}

	/**
	 * @return The Health Points of the troop.
	 */
	public int getHP() {
		return health;
	}

	/**
	 * Sets the hp of the troop.
	 * @param hp The hp.
	 */
	public void setHP(int hp) {
		health = hp;
	}

	public void decrementHP() {
		--health;
	}

	/**
	 * Add HP to the HP of the troop.
	 * @param hp The HP to add.
	 */
	public void addHP(int hp) {
		health += hp;
	}

	/**
	 * @return The damage of the troop.
	 */
	public int getDamage() {
		return damage;
	}

	public Castle getAttachedCastle() {
		return attachedCastle;
	}

	public int getProdCost() {
		return prodCost;
	}
}

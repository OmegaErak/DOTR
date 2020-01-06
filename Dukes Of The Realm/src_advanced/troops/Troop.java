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

	protected Button unitButton;

	protected int xPosMap;
	protected int yPosMap;

	protected Image texture;

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
	}

	/**
	 * Makes the troup visible in the canvas and add a button to it.
	 * @param renderLayer The JavaFX canvas.
	 * @param troop The troop type as String.
	 * @param owner The owner of the troop.
	 * @param castlePosition The castle position from where it goes.
	 * @param path The path it follows.
	 * @return The troop button.
	 */
	public Button spawnTroop(Pane renderLayer, String troop, int owner,Point2D castlePosition, Double[] path) {
		Image unit = new Image("/sprites/troops/" + troop + "_"+owner+".png");
		Point2D startPosition = new Point2D(castlePosition.getX() + 25,castlePosition.getY() + 25);
		Button unitButton = new Button(renderLayer,startPosition,unit);
		unitButton.setPosition(startPosition);
		unitButton.getTextureView().setFitHeight(Settings.troopsSize);
		unitButton.getTextureView().setFitWidth(Settings.troopsSize);
		unitButton.addToCanvas();
		
		return unitButton;
	}

	/**
	 * Launches the move animation.
	 * @param playerCastlePosition The starting position.
	 * @param targetedCastle The castle that will receive the troop.
	 * @param unit The troop to move.
	 * @param castleOwned True if the castle belongs to the player, false otherwise.
	 * @param speed The speed of the movement.
	 */
	public void launchMovingAnimation(Point2D playerCastlePosition, Castle targetedCastle, Troop unit, boolean castleOwned, int speed, int[][] gameMap) {
		System.out.println("suce-1");
		int dxy = Settings.castleSize / 2;
		Node start = new Node(new Point2D(playerCastlePosition.getX() + dxy, playerCastlePosition.getY() + dxy), 0, 0);
		Node end = new Node(new Point2D(targetedCastle.getPosition().getX() + dxy, targetedCastle.getPosition().getY() + dxy), 0, 0);
		Double[] path = AStar.shortestPath(start, end, gameMap, true, castleOwned);
		String unitPathName;
		System.out.println("suce");
		if(unit.getClass() == Pikeman.class) {
			unitPathName = "pikeman";
		} else if (unit.getClass() == Knight.class) {
			unitPathName = "knight";
		} else if (unit.getClass() == Onager.class) {
			unitPathName = "onager";
		} else {
			unitPathName = "money";
		}
		Button unitButton = unit.spawnTroop(renderLayer, unitPathName, 0, playerCastlePosition, path);
		unit.setUnitButton(unitButton);
		System.out.println("suce2");
		unit.displace(renderLayer, path, unitButton, unit, gameMap, targetedCastle, castleOwned, speed);
		System.out.println("suce3");
	}

	// TODO Clean
	/**
	 * Launches the animation of moving the troop.
	 * @param path The path it will follow.
	 * @param renderLayer The JavaFX canvas.
	 * @param unitButton The button of the troop.
	 * @param unit The troop.
	 * @param gameMap The game map.
	 * @param castleTargeted The target castle.
	 * @param castleOwned True if the target castle is owned by the player, false otherwise.
	 * @param speed The speed of the troop.
	 */
	public void displace( Pane renderLayer, Double[] path, Button unitButton, Troop unit, int[][] gameMap, Castle castleTargeted, boolean castleOwned, int speed) {
		if(path != null) {	
		Double x = path[path.length-2];
		Double y = path[path.length-1] - Settings.statusBarHeight;
		xPosMap = (int) ((x-5)/Settings.cellSize);
		yPosMap = (int) ((y-5)/Settings.cellSize);
		if(!castleOwned) {
			gameMap[(int) (x-5)/Settings.cellSize][(int) (y-5)/Settings.cellSize] = 2;
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

		final PathTransition moveAnimation = new PathTransition(Duration.seconds(path.length/speed), poly);
		moveAnimation.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);
		moveAnimation.setNode(unitButton.getTextureView());
		
		moveAnimation.play();
		moveAnimation.setOnFinished(e -> {
			castleTargeted.addAttackingTroop(unit);
			if(unit.getAttachedCastle().getOwner() == castleTargeted.getOwner()) {
				unitButton.removeFromCanvas();
				gameMap[(int) (x-5)/Settings.cellSize][(int) (y-5)/Settings.cellSize] = 0;
			}		
			renderLayer.getChildren().remove(polyLine);

			unitButton.removeFromCanvas();
		});
		}
	}

	/**
	 * @return The x coordinate on the map.
	 */
	public int getxPosMap() {
		return xPosMap;
	}

	/**
	 * @return The y coordinate on the map.
	 */
	public int getyPosMap() {
		return yPosMap;
	}

	/**
	 * @return The troop button.
	 */
	public Button getUnitButton() {
		return unitButton;
	}

	/**
	 * Sets the troop button.
	 * @param unitButton The button.
	 */
	public void setUnitButton(Button unitButton) {
		this.unitButton = unitButton;
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

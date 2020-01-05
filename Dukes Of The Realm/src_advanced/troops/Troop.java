package troops;

import base.Settings;
import buildings.Castle;

import drawable.Button;
import drawable.Sprite;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import javafx.animation.PathTransition;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Polyline;
import javafx.util.Duration;

import java.io.File;
import java.util.Random;

/**
 * Troop abstract class.
 * To be derived for any other troop.
 */
abstract public class Troop  extends Sprite {
	protected int prodTime;
	protected int prodCost;
	protected int speed;
	protected int health;
	protected int damage;
	protected int owner;
	protected Button unitButton;

	protected int xPosMap;
	protected int yPosMap;
	protected int timeUntilProd;

	protected Image texture;

	/**
	 * Default constructor
	 * @param renderLayer The JavaFX canvas.
	 * @param castle The castle it is attached to.
	 */
	public Troop(Pane renderLayer, Castle castle) {
		super(renderLayer, castle.getPosition());
		this.owner = castle.getOwner();
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
		unitButton.getTextureView().setFitHeight(20);
		unitButton.getTextureView().setFitWidth(20);
		unitButton.addToCanvas();
		
		return unitButton;
	}

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
			castleTargeted.addTroopAround(unit);
			if(unit.getOwner() == castleTargeted.getOwner()) {
				unitButton.removeFromCanvas();
				gameMap[(int) (x-5)/Settings.cellSize][(int) (y-5)/Settings.cellSize] = 0;
			}		
			renderLayer.getChildren().remove(polyLine);
			Random r = new Random();
			int oofType = r.nextInt(6);
			File oof = new File("resources/sound/con" + oofType + ".wav");
			try {
				Clip clip = AudioSystem.getClip();
				AudioInputStream inputStream = AudioSystem.getAudioInputStream(oof);
				clip.open(inputStream);
				clip.start();
			} catch (Exception  e1) {
				e1.printStackTrace();
			}
		});
		}
	}

	/**
	 * @return Time until the troop production is over.
	 */
	public int getTimeUntilProd() {
		return timeUntilProd;
	}

	/**
	 * Sets the time until the troop production is over.
	 * @param timeUntilProd
	 */
	public void setTimeUntilProd(int timeUntilProd) {
		this.timeUntilProd = timeUntilProd;
	}

	/**
	 * @return The x coordinate on the map.
	 */
	public int getxPosMap() {
		return xPosMap;
	}

	/**
	 * Sets the x coordinate on the map.
	 * @param xPosMap The coordinate.
	 */
	public void setxPosMap(int xPosMap) {
		this.xPosMap = xPosMap;
	}

	/**
	 * @return The y coordinate on the map.
	 */
	public int getyPosMap() {
		return yPosMap;
	}

	/**
	 * Sets the y coordinate on the map.
	 * @param yPosMap The coordinate.
	 */
	public void setyPosMap(int yPosMap) {
		this.yPosMap = yPosMap;
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
	 * @return The owner of the troop.
	 */
	public int getOwner() {
		return owner;
	}

	/**
	 * Sets the owner of the troop.
	 * @param owner The owner.
	 */
	public void setOwner(int owner) {
		this.owner = owner;
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
	 * Sets the production time of the troop.
	 * @param prodTime The time.
	 */
	public void setProdTime(int prodTime) {
		this.prodTime = prodTime;
	}

	/**
	 * @return The production cost of the troop.
	 */
	public int getProdCost() {
		return prodCost;
	}

	/**
	 * @return The speed of the troop.
	 */
	public int getSpeed() {
		return speed;
	}

	/**
	 * Sets the speed of the troop.
	 * @param speed The speed.
	 */
	public void setSpeed(int speed) {
		this.speed = speed;
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
	 * @return The damage of the troop.
	 */
	public int getDamage() {
		return damage;
	}

	/**
	 * Sets the damage of the troop.
	 * @param damage The damage.
	 */
	public void setDamage(int damage) {
		this.damage = damage;
	}
}

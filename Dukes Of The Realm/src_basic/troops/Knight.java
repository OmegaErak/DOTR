package troops;

import base.Settings;

import buildings.Castle;

import renderer.Sprite;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

/**
 * Knight class.
 */
public class Knight extends Sprite {
	private Castle attachedCastle;

	private int speed;
	private int health;
	private int damage;

	/**
	 * @param renderLayer The JavaFX canvas.
	 * @param castle The castle of the troop.
	 */
	public Knight(Pane renderLayer, Castle castle) {
		super(renderLayer, castle.getPosition());

		this.attachedCastle = castle;
		this.speed 		= 6;
		this.health 	= 3;
		this.damage 	= 5;

		final Image texture;
		if (castle.getOwner() <= Settings.nbMaxActiveDukes) {
			texture = new Image("/sprites/troops/knight_" + castle.getOwner() + ".png");
		} else {
			texture = new Image("/sprites/troops/knight_neutral.png");
		}

		setTexture(texture);

		textureView.setFitWidth(Settings.knightSize);
		textureView.setFitHeight(Settings.knightSize);
		setPosition(new Point2D(castle.getPosition().getX() + Settings.castleSize / 2.0 - Settings.knightSize / 2.0, castle.getPosition().getY() + Settings.castleSize / 2.0 - Settings.knightSize / 2.0));
	}

	/**
	 * @return True if the knight is alive, false otherwise.
	 */
	public boolean isAlive() {
		return health > 0;
	}

	/**
	 * @return The walking speed of the knight.
	 */
	public int getSpeed() {
		return speed;
	}

	/**
	 * Changes the hp of the knight
	 * @param hp The delta hp to add. Can be negative.
	 */
	public void addHP(int hp) {
		health += hp;
	}

	/**
	 * @return The damage of the knight.
	 */
	public int getDamage() {
		return damage;
	}

	/**
	 * @return The knight's attached castle.
	 */
	public Castle getAttachedCastle() {
		return attachedCastle;
	}

	/**
	 * Changes the knight's attached castle.
	 * @param attachedCastle The castle.
	 */
	public void setAttachedCastle(Castle attachedCastle) {
		this.attachedCastle = attachedCastle;
	}
}

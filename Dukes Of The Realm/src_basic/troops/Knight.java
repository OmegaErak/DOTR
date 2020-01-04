package troops;

import base.Settings;

import buildings.Castle;

import drawable.Sprite;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

/**
 * Knight class.
 */
public class Knight extends Sprite {
	private Castle attachedCastle;

	private int health = 3;
	private final int damage = 5;
	private final int speed = 6;

	/**
	 * @param renderLayer The JavaFX canvas.
	 * @param castle The castle of the troop.
	 */
	public Knight(Pane renderLayer, Castle castle) {
		super(renderLayer, castle.getPosition());

		this.attachedCastle = castle;

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
	 * Sets the HP of the knight.
	 */
	public void setHP(int hp) {
		health = hp;
	}

	/**
	 * @return The HP of the knight.
	 */
	public int getHP() {
		return health;
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

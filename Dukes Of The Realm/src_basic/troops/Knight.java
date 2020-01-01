package troops;

import base.Settings;

import buildings.Castle;

import renderer.Sprite;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

public class Knight extends Sprite {
	private Castle attachedCastle;

	private int prodTime;
	private int prodCost;
	private int speed;
	private int health;
	private int damage;

	public Knight(Pane renderLayer, Castle castle) {
		super(renderLayer, castle.getPosition());

		this.attachedCastle = castle;
		this.prodCost 	= 500;
		this.prodTime 	= 20;
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

	public boolean isAlive() {
		return health > 0;
	}

	public int getProdTime() {
		return prodTime;
	}

	public int getProdCost() {
		return prodCost;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public int getHealth() {
		return health;
	}

	public void setHealth(int health) {
		this.health = health;
	}

	public int getDamage() {
		return damage;
	}

	public Castle getAttachedCastle() {
		return attachedCastle;
	}

	public void setAttachedCastle(Castle attachedCastle) {
		this.attachedCastle = attachedCastle;
	}
}

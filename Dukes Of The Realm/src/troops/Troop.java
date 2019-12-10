package troops;

import base.Sprite;

import buildings.Castle;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

abstract public class Troop  extends Sprite {
	protected int prodTime;
	protected int prodCost;
	protected int speed;
	protected int health;
	protected int damage;
	
	protected Point2D position;

	// TODO: Remove texture from here like in castle
	public Troop(Pane renderLayer, Image texture, Castle castle) {
		super(renderLayer, castle.getPosition(), texture);
	}
	
	public boolean isAlive() {
		return health > 0;
	}

	public int getProdTime() {
		return prodTime;
	}

	public void setProdTime(int prodTime) {
		this.prodTime = prodTime;
	}

	public int getProdCost() {
		return prodCost;
	}

	public void setProdCost(int prodCost) {
		this.prodCost = prodCost;
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

	public void setDamage(int damage) {
		this.damage = damage;
	}

	public Point2D getPosition() {
		return position;
	}

	public void setPosition(Point2D position) {
		this.position = position;
	}

//	public abstract StackPane toJavaFX();
}

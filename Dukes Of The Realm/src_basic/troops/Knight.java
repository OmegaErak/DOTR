package troops;

import algorithms.Node;
import base.Settings;

import buildings.Castle;

import renderer.Sprite;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

import java.util.ArrayList;

public class Knight extends Sprite {
	private int prodTime;
	private int prodCost;
	private int speed;
	private int health;
	private int damage;

	private Boolean isMoving = false;
	private int currentNodeIndex = 0;
	private ArrayList<Node> movingPath;
	private Castle movingTarget;

	public Knight(Pane renderLayer, Castle castle) {
		super(renderLayer, castle.getPosition());
		
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

	public void onUpdate() {
		if (isMoving) {
			++currentNodeIndex;
			setPosition(movingPath.get(currentNodeIndex).getPosition());
		}
	}

	public void moveToCastle(Castle castle, ArrayList<Node> path) {
		isMoving = true;
		movingPath = path;
		movingTarget = castle;
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
}

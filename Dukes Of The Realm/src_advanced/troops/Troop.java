package troops;

import java.io.File;
import java.util.Random;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import base.Settings;
import buildings.Castle;
import javafx.animation.PathTransition;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Polyline;
import javafx.util.Duration;
import renderer.Button;
import renderer.Sprite;

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
	
	public Troop(Pane renderLayer, Castle castle) {
		super(renderLayer, castle.getPosition());
		this.owner = castle.getOwner();
	}
	
	public Button spawnTroop(String troop, int owner,Point2D castlePosition, Double[] path, Pane renderLayer) {	
		Image unit = new Image("/sprites/troops/" + troop + "_"+owner+".png");
		Point2D startPosition = new Point2D(castlePosition.getX() + 25,castlePosition.getY() + 25);
		Button unitButton = new Button(renderLayer,startPosition,unit);
		unitButton.setPosition(startPosition);
		unitButton.getTextureView().setFitHeight(20);
		unitButton.getTextureView().setFitWidth(20);
		unitButton.addToCanvas();
		
		return unitButton;
	}
	
	public void displace(Double[] path, Pane renderLayer,Button unitButton,Troop unit , int[][] gameMap, Castle castleTargeted,boolean castleOwned, int speed) {
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
			int oofType = r.nextInt(2) + 1;
			File oof = new File("resources/sound/rire" + oofType + ".wav");
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
	

	
	
	
	
	public int getTimeUntilProd() {
		return timeUntilProd;
	}

	public void setTimeUntilProd(int timeUntilProd) {
		this.timeUntilProd = timeUntilProd;
	}

	public int getxPosMap() {
		return xPosMap;
	}

	public void setxPosMap(int xPosMap) {
		this.xPosMap = xPosMap;
	}

	public int getyPosMap() {
		return yPosMap;
	}

	public void setyPosMap(int yPosMap) {
		this.yPosMap = yPosMap;
	}

	public Button getUnitButton() {
		return unitButton;
	}

	public void setUnitButton(Button unitButton) {
		this.unitButton = unitButton;
	}

	public int getOwner() {
		return owner;
	}

	public void setOwner(int owner) {
		this.owner = owner;
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
}

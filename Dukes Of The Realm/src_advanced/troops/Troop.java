package troops;

import java.io.File;
import java.util.Random;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

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

	protected Image texture;
	
	public Troop(Pane renderLayer, Castle castle) {
		super(renderLayer, castle.getPosition());
	}
	
	public Button spawnTroop(String troop, int owner,Castle castle, Double[] path, Pane renderLayer) {	
		Image unit = new Image("/sprites/troops/" + troop + "_"+owner+".png");
		Point2D startPosition = new Point2D(castle.getPosition().getX() + 15,castle.getPosition().getY() + 15);
		Button unitButton = new Button(renderLayer,startPosition,unit);
		unitButton.setPosition(startPosition);
		unitButton.getTextureView().setFitHeight(20);
		unitButton.getTextureView().setFitWidth(20);
		unitButton.addToCanvas();
		
		return unitButton;
	}
	
	public void displacement(Double[] path, Pane renderLayer,Button unitButton) {		
		Polyline polyLine = new Polyline();
		polyLine.getPoints().addAll(path);
		renderLayer.getChildren().add(polyLine);
		Polyline poly = new Polyline();
		double dx = path[0];
		double dy = path[1];
		for(int i = 0; i < path.length; i++) {
			if(i % 2 == 0) {
				path[i] -= dx;
			} else {
				path[i] -= dy;
			}
		}
		poly.getPoints().addAll(path);

		final PathTransition moveAnimation = new PathTransition(Duration.seconds(path.length/6), poly);
		moveAnimation.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);
		moveAnimation.setNode(unitButton.getTextureView());
		moveAnimation.play();
		moveAnimation.setOnFinished(e -> {
			renderLayer.getChildren().remove(polyLine);
			Random r = new Random();
			int oofType = r.nextInt(2);
			File oof = new File("resources/sound/oof" + oofType + ".wav");
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

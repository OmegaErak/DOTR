package buildings;

import base.Direction;
import base.Settings;
import base.Sprite;

import troops.Knight;
import troops.Onager;
import troops.Pikeman;
import troops.Troop;

import java.util.ArrayList;
import java.util.Random;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

public class Castle extends Sprite {	
	private Image texture;
	private Image buildingTexture;

	private int owner;
	private int level;
	private int treasure;
	
	private Point2D position;
	private int doorDirection;
	
	private ArrayList<Troop> availableTroops = new ArrayList<>();
	private ArrayList<Troop> inProductionTroops = new ArrayList<>();

	private int nbKnights = 0;
	private int nbOnagers = 0;
	private int nbPikemen = 0;

	private int passiveIncome;
	private int nextLevelBuildCost;
	private int nextLevelBuildTime;

	private boolean isLevelingUp;
	private int timeUntilLevelUp = -1;
	
	private Random rdGen = new Random();

	public Castle(Pane renderLayer, int owner, Point2D position) {
		super(renderLayer, position);

		if (owner <= Settings.nbMaxActiveDukes) {
			texture = new Image("resources/sprites/castles/castle_" + owner + ".png");
			buildingTexture = new Image("resources/sprites/castles/castle_" + owner + "_build.png");
		} else {
			texture = new Image("resources/sprites/castles/castle_neutral.png");
			buildingTexture = new Image("resources/sprites/castles/castle_neutral_build.png");
		}

		this.owner = owner;
		this.level = 1;
		this.treasure = Settings.initialTreasure;
		this.doorDirection = rdGen.nextInt(Direction.nbDirections);

		this.position = position;
		
		final int nbTroops = Settings.nbMinInitTroops + rdGen.nextInt(Settings.nbMaxInitTroops - Settings.nbMinInitTroops);
		for (int i = 0; i < nbTroops; ++i) {
			final int troop = rdGen.nextInt(Settings.nbDiffTroopTypes);
			
			if (troop == 0) {
				availableTroops.add(new Knight(renderLayer, this));
				++nbKnights;
			} else if (troop == 1) {
				availableTroops.add(new Onager(renderLayer, this));
				++nbOnagers;
			} else if (troop == 2) {
				availableTroops.add(new Pikeman(renderLayer, this));
				++nbPikemen;
			}
		}

		setTexture(texture);

		textureView.setRotate(90 * doorDirection);
		textureView.setFitWidth(Settings.castleSize);
		textureView.setFitHeight(Settings.castleSize);

		updateData();
	}

	public void onUpdate() {
		this.treasure += this.passiveIncome;

		if (this.isLevelingUp) {
			if (this.timeUntilLevelUp > 0) {
				--this.timeUntilLevelUp;
			} else if (this.timeUntilLevelUp == 0) {
				this.level += 1;
				this.isLevelingUp = false;
				--timeUntilLevelUp;

				removeFromCanvas();
				setTexture(texture);
				addToCanvas();
				updateData();
			}
		}
	}

	private void updateData() {
		this.passiveIncome = 100 * this.level;
		this.nextLevelBuildCost = 1000 * this.level;
		this.nextLevelBuildTime = 10 + 5 * this.level;
	}
	
	public int getTreasure() {
		return treasure;
	}

	public int getOwner() {
		return owner;
	}

	public void setOwner(int owner) {
		this.owner = owner;
	}

	public int getDoorDirection() {
		return doorDirection;
	}

	public int getLevel() {
		return level;
	}

	public int getNbKnights() { return nbKnights; }

	public int getNbOnagers() { return nbOnagers; }

	public int getNbPikemen() { return nbPikemen; }

	public int getPassiveIncome() {
		return passiveIncome;
	}

	public int getNextLevelBuildCost() {
		return nextLevelBuildCost;
	}
	
	public int getNextLevelRemainingTime() {
		return timeUntilLevelUp;
	}

	public boolean canLevelUp() {
		return this.treasure >= this.nextLevelBuildCost && !this.isLevelingUp;
	}

	public void levelUp() {
		this.isLevelingUp = true;
		this.timeUntilLevelUp = this.nextLevelBuildTime;
		this.treasure -= this.nextLevelBuildCost;

		removeFromCanvas();
		setTexture(buildingTexture);
		addToCanvas();
	}

	public boolean isLevelingUp() {
		return isLevelingUp;
	}

	public Point2D getPosition() {
		return position;
	}

	public void setPosition(Point2D position) {
		this.position = position;
	}
}

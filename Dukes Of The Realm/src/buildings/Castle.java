package buildings;

import java.util.ArrayList;
import java.util.Random;

import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import base.Settings;
import troops.Troop;

public class Castle {
	
	private final Tooltip tooltip = new Tooltip();
	
	private int owner;
	private int level;
	private int treasure;
	
	private Point2D position;
	private int doorDirection;
	
	private ArrayList<Troop> availableTroops;
	private ArrayList<Troop> inProductionTroops;

	private int passiveIncome;
	private int nextLevelBuildCost;
	private int nextLevelBuildTime;

	private boolean isLevelingUp;
	private int timeUntilLevelUp;
	
	private Random rdGenerator;

	public Castle(int owner, Point2D position) {
		super();
		rdGenerator = new Random();

		this.owner = owner;
		this.level = 1;
		this.treasure = Settings.initialTreasure;
		this.doorDirection = rdGenerator.nextInt(4);
		
		this.position = position;

		this.availableTroops = new ArrayList<Troop>();
		this.inProductionTroops = new ArrayList<Troop>();
	}

	public void onUpdate() {
		this.treasure += this.passiveIncome;

		if (this.isLevelingUp)
		{
			if (this.timeUntilLevelUp > 0)
				this.timeUntilLevelUp -= 1;
			else if (this.timeUntilLevelUp == 0)
			{
				this.level += 1;
				this.isLevelingUp = false;

				this.passiveIncome = 10 * this.level;
				this.nextLevelBuildCost = 1000 * this.level;
				this.nextLevelBuildTime = 100 + 50 * this.level;
			}
		}
	}
	
	
	
	public void onRender() {
		// TODO
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

	public boolean canLevelUp() {
		return this.treasure >= this.nextLevelBuildCost;
	}

	public void levelUp() {
		this.isLevelingUp = true;
		this.timeUntilLevelUp = this.nextLevelBuildTime;
	}

	public Point2D getPosition() {
		return position;
	}

	public void setPosition(Point2D position) {
		this.position = position;
	}
}

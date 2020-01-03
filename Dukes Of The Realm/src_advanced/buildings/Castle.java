package buildings;

import base.Direction;
import base.Game;
import base.Settings;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import renderer.Sprite;
import troops.Knight;
import troops.Onager;
import troops.Pikeman;
import troops.Troop;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Castle extends Sprite {
	final static private List<String> dukeNames = new ArrayList<String>(Arrays.asList(
			"Jean-Cloud Van Damme",

			"Jean-Eudes",
			"Jean-Michel",
			"Jean-Marie",
			"Jean-Loup",
			"Jean-Côme",
			"Jean-Alex",
			"Jean-Kévin",
			"Jean-René",
			"Jean-Maurice",
			"Jean-Francis",
			"Jean-Jacques",
			"Jean-Noël",
			"Jean-George",
			"Jean-Brice",
			"Jean-Blaise",
			"Jean-Aimée",
			"Jean-Baptiste",
			"Jean-Bernard",
			"Jean-Briac",
			"Jean-Charles",
			"Jean-Jean",
			"Jean-Paul",
			"Jean-Ti",
			"Jean-Rêve",
			"Jean-Yves",

			"Jean-Cérien"
	));

	private Image texture;
	private Image buildingTexture;
	private Image armoredTexture;

	private int owner;
	private String ownerName;

	private int level;
	private int treasure;
	
	private Point2D position;
	private int doorDirection;
	
	private ArrayList<Pikeman> availablePikeman = new ArrayList<>();
	private ArrayList<Knight> availableKnight = new ArrayList<>();
	private ArrayList<Onager> availableOnager = new ArrayList<>();
	private ArrayList<Troop> availableTroop = new ArrayList<>();
	private ArrayList<Troop> inProductionTroops = new ArrayList<>();
	private ArrayList<Troop> troopAround = new ArrayList<>();

	private int nbKnights = 0;
	private int nbOnagers = 0;
	private int nbPikemen = 0;
	
	private int surrounded;
	
	private boolean currentCastle;

	private int passiveIncome;
	private int nextLevelBuildCost;
	private int nextLevelBuildTime;
	private int wallHealth = 50;
	private int wallCost = 3000;
	private int wallTimeCost = 10;
	
	private boolean isGettingWall;
	private boolean hasWall;
	private boolean isBuildingWall;
	private boolean isLevelingUp;
	private int timeUntilLevelUp = -1;
	
	private Random rdGen = new Random();

	public Castle(Pane renderLayer, int owner, Point2D position) {
		super(renderLayer, position);

		if (owner <= Settings.nbMaxActiveDukes) {
			texture = new Image("/sprites/castles/castle_" + owner + ".png");
			armoredTexture = new Image("/sprites/castles/armored_castle_" + owner + ".png");
			buildingTexture = new Image("/sprites/castles/castle_" + owner + "_build.png");
		} else {
			texture = new Image("/sprites/castles/castle_neutral.png");
			buildingTexture = new Image("/sprites/castles/castle_neutral_build.png");
			armoredTexture = new Image("/sprites/castles/armored_castle_neutral.png");
		}

		this.owner = owner;

		final int index = rdGen.nextInt(dukeNames.size());
		this.ownerName = dukeNames.get(index);
		dukeNames.remove(index);

		this.level = 1;
		this.treasure = Settings.initialTreasure;
		this.doorDirection = rdGen.nextInt(Direction.nbDirections);

		this.position = position;
		
		final int nbTroops = Settings.nbMinInitTroops + rdGen.nextInt(Settings.nbMaxInitTroops - Settings.nbMinInitTroops);
		for (int i = 0; i < nbTroops; ++i) {
			final int troop = rdGen.nextInt(Settings.nbTroopTypes);
			
			if (troop == 0) {
				availableKnight.add(new Knight(renderLayer, this));
				++nbKnights;
			} else if (troop == 1) {
				availableOnager.add(new Onager(renderLayer, this));
				++nbOnagers;
			} else if (troop == 2) {
				availablePikeman.add(new Pikeman(renderLayer, this));
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
				if(this.hasWall) {
					setTexture(armoredTexture);
				}else {					
					setTexture(texture);
				}
				addToCanvas();
				updateData();
			}
		}else if(this.isBuildingWall) {
			if (this.wallTimeCost > 0) {
				--this.wallTimeCost;
			} else if (this.wallTimeCost == 0) {
				this.isBuildingWall = false;
				--wallTimeCost;
				
				this.hasWall = true;

				removeFromCanvas();
				setTexture(armoredTexture);
				addToCanvas();
				updateData();
			}
		}
	}
	
	
	public void addWall() {
		this.isBuildingWall = true;
		this.treasure -= this.wallCost;

		removeFromCanvas();
		setTexture(buildingTexture);
		addToCanvas();
	}

	
	public void unitAroundAction() {
		int amountOfDamage = 0;
		for(int i=0; i<troopAround.size();i++){
			Troop unit = troopAround.get(i);
			if(unit.getOwner() == owner) {
				transfertTroop(unit);
				troopAround.remove(unit);
			}else {				
			amountOfDamage += unit.getDamage();
			}
			
		}
		if(this.hasWall) {
			this.wallHealth -= amountOfDamage;
			if(this.wallHealth <=0) {
				this.hasWall = false;
				removeFromCanvas();
				setTexture(texture);
				addToCanvas();
			}
		}
		int i=0;
		while (i < amountOfDamage && (availableKnight.size() != 0 || availableOnager.size() != 0 || availablePikeman.size() != 0)) {
			Random r = new Random();
			int whoIsTakingDamage = r.nextInt(Settings.nbTroopTypes);
			if(whoIsTakingDamage == 0) {
				//Knight takes damage
				if(availableKnight.isEmpty()) {
					--i;
				}else {		
					Knight knight = availableKnight.get(0);
					knight.setHealth(knight.getHealth()-1);
					if(!knight.isAlive()) {
						availableKnight.remove(0);
						--nbKnights;
					}
				}
			}else if(whoIsTakingDamage == 1) {
				//Pikeman takes damage
				if(availablePikeman.isEmpty()) {
					--i;
				}else {
					Pikeman pikeman = availablePikeman.get(0);
					pikeman.setHealth(pikeman.getHealth()-1);
					if(!pikeman.isAlive()) {
						availablePikeman.remove(0);
						--nbPikemen;
					}
				}
			}else if(whoIsTakingDamage == 2) {
				//Onager takes damage
				if(availableOnager.isEmpty()) {
					--i;
				}else {
					Onager onager= availableOnager.get(0);
					onager.setHealth(onager.getHealth()-1);
					if(!onager.isAlive()) {
						availableOnager.remove(0);
						--nbOnagers;
					}
				}
			}else {
				break;
			}
			++i;
		}
	}
	
	public void isAlive(int[][] gameMap) {
		if(availableKnight.isEmpty() && availablePikeman.isEmpty() && availableOnager.isEmpty()) {
			Image newTexture = new Image("/sprites/castles/castle_" + troopAround.get(0).getOwner() + ".png");
			Image newBuildTexture = new Image("/sprites/castles/castle_" + troopAround.get(0).getOwner() + "_build.png");
			Image newArmoredTexture = new Image("/sprites/castles/armored_castle_" + troopAround.get(0).getOwner() + ".png");
			texture = newTexture;
			buildingTexture = newBuildTexture;
			armoredTexture = newArmoredTexture;
			
			for(int i=0;i<troopAround.size();i++) {
				transfertTroop(troopAround.get(i));
				troopAround.get(i).getUnitButton().removeFromCanvas();			
				gameMap[troopAround.get(i).getxPosMap()][troopAround.get(i).getyPosMap()] = 0;
			}
			troopAround.clear();
			setOwner(0);
			removeFromCanvas();	
			setTexture(texture);
			addToCanvas();
			Game.castleOwned.add(this);
		}
	}
	
	
	public void transfertTroop(Troop troop) {
		if(troop.getClass() == Pikeman.class) {
			availablePikeman.add((Pikeman) troop);
			++nbPikemen;
			troop.removeFromCanvas();
		}else if(troop.getClass() == Knight.class) {
			availableKnight.add((Knight) troop);
			++nbKnights;
		}else if(troop.getClass() == Onager.class){
			availableOnager.add((Onager) troop);
			++nbOnagers;
		}else {
			treasure += troop.getProdCost();
		}
	}
	
	
	
	
	
	public boolean isGettingWall() {
		return isGettingWall;
	}

	public void setGettingWall(boolean isGettingWall) {
		this.isGettingWall = isGettingWall;
	}

	public boolean hasWall() {
		return hasWall;
	}

	public void setHasWall(boolean hasWall) {
		this.hasWall = hasWall;
	}

	public int getWallCost() {
		return wallCost;
	}

	public void setWallCost(int wallCost) {
		this.wallCost = wallCost;
	}

	public void setTreasure(int treasure) {
		this.treasure = treasure;
	}

	public boolean isSurrounded() {
		return  troopAround.size() >= Settings.surround;
	}

	private void updateData() {
		this.passiveIncome = 100 * this.level;
		this.nextLevelBuildCost = 1000 * this.level;
		this.nextLevelBuildTime = 10 + 5 * this.level;
	}
		
	public boolean getCurrentCastle() {
		return currentCastle;
	}

	public void setCurrentCastle(boolean currentCastle) {
		this.currentCastle = currentCastle;
	}

	public int getSurrounded() {
		return surrounded;
	}

	public void setSurrounded(int surrounded) {
		this.surrounded = surrounded;
	}

	public void setAvailablePikeman(ArrayList<Pikeman> availablePikeman) {
		this.availablePikeman = availablePikeman;
	}

	public void setAvailableKnight(ArrayList<Knight> availableKnight) {
		this.availableKnight = availableKnight;
	}

	public void setAvailableOnager(ArrayList<Onager> availableOnager) {
		this.availableOnager = availableOnager;
	}
	
	public void addTroopAround(Troop troop) {
		troopAround.add(troop);
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

	public String getOwnerName() {
		return ownerName;
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

	
	
	public int getWallTimeCost() {
		return wallTimeCost;
	}

	public void setWallTimeCost(int wallTimeCost) {
		this.wallTimeCost = wallTimeCost;
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
	
	public ArrayList<Pikeman> getAvailablePikeman(){
		return availablePikeman;
	}
	
	public ArrayList<Knight> getAvailableKnight(){
		return availableKnight;
	}
	
	public ArrayList<Onager> getAvailableOnager(){
		return availableOnager;
	}
	

	public Point2D getPosition() {
		return position;
	}

	public void setPosition(Point2D position) {
		this.position = position;
	}
}

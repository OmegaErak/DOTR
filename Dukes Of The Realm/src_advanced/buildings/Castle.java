package buildings;

import base.Direction;
import base.Game;
import base.Settings;

import drawable.Sprite;

import troops.*;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.Random;

/**
 * Castle class.
 */
public class Castle extends Sprite {
	// Render objects
	private Image texture;
	private Image buildingTexture;
	private Image armoredTexture;

	// Game objects
	private int owner;
	private String ownerName;

	private int level;
	private int treasure;
	
	private Point2D position;
	private int doorDirection;
	
	private ArrayList<Pikeman> availablePikemen = new ArrayList<>();
	private ArrayList<Knight> availableKnights = new ArrayList<>();
	private ArrayList<Onager> availableOnagers = new ArrayList<>();
	
	private int passiveIncome;
	private int nextLevelBuildCost;
	private int nextLevelBuildTime;
	private int wallHealth = 50;
	private int wallCost = 3000;
	private int wallTimeCost = 10;
	
	private boolean hasWall;
	private boolean isBuildingWall;
	private boolean isLevelingUp;
	private int timeUntilLevelUp = -1;

	private int barrackLevel = 1;

	private boolean isBarrackLevelingUp;
	private int timeUntilBarrackLevelUp = 20;
	private int barracksBuildCost = 1500;

	private ArrayList<Troop> inProductionTroops = new ArrayList<>();
	private ArrayList<Troop> troopAround = new ArrayList<>();
	private boolean isProducingTroops;

	/**
	 * Default constructor
	 * @param renderLayer The JavaFX canvas onto which we draw.
	 * @param position The position of the castle in the window.
	 */
	public Castle(Pane renderLayer, Point2D position) {
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

		Random rdGen = new Random();

		this.level = 1;
		this.treasure = Settings.initialTreasure;
		this.doorDirection = rdGen.nextInt(Direction.nbDirections);

		this.position = position;
		
		final int nbTroops = Settings.nbMinInitTroops + rdGen.nextInt(Settings.nbMaxInitTroops - Settings.nbMinInitTroops);
		for (int i = 0; i < nbTroops; ++i) {
			final int troopType = rdGen.nextInt(Settings.nbTroopTypes);
			
			if (troopType == 0) {
				availableKnights.add(new Knight(renderLayer, this));
			} else if (troopType == 1) {
				availableOnagers.add(new Onager(renderLayer, this));
			} else if (troopType == 2) {
				availablePikemen.add(new Pikeman(renderLayer, this));
			}
		}

		setTexture(texture);

		textureView.setRotate(90 * doorDirection);
		textureView.setFitWidth(Settings.castleSize);
		textureView.setFitHeight(Settings.castleSize);

		updateData();
	}

	/**
	 * Update function that is called every turn (2 seconds).
	 */
	public void onUpdate(int[][] gameMap) {
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

		if (this.isBarrackLevelingUp) {
			if (this.timeUntilBarrackLevelUp > 0) {
				--this.timeUntilBarrackLevelUp;
			} else if (this.timeUntilBarrackLevelUp == 0) {
				this.barrackLevel += 1;
				this.barracksBuildCost *= 2;
				this.isBarrackLevelingUp = false;
				--timeUntilBarrackLevelUp;

				removeFromCanvas();
				if(this.hasWall) {
					setTexture(armoredTexture);
				} else {
					setTexture(texture);
				}
				addToCanvas();
				updateData();
			}
		}

		onEnemyAttack(gameMap);
		onTroopProduction();
	}

	/**
	 * Updates the data after leveling up.
	 */
	private void updateData() {
		this.passiveIncome = 100 * this.level;
		this.nextLevelBuildCost = 1000 * this.level;
		this.nextLevelBuildTime = 10 + 5 * this.level;
	}

	/**
	 * Adds a wall around the castle.
	 */
	public void addWall() {
		this.isBuildingWall = true;
		this.treasure -= this.wallCost;

		removeFromCanvas();
		setTexture(buildingTexture);
		addToCanvas();
	}

	/**
	 * Manages the castle when there are troops around.
	 */
	public void onEnemyAttack(int[][] gameMap) {
		int amountOfDamage = 0;
		for(int i = 0; i < troopAround.size(); i++) {
			Troop unit = troopAround.get(i);
			if (unit.getOwner() == owner) {
				receiveTroop(unit);
				troopAround.remove(unit);
			} else {
				amountOfDamage += unit.getDamage();
			}
			
		}
		if (this.hasWall) {
			this.wallHealth -= amountOfDamage;
			if(this.wallHealth <=0) {
				this.hasWall = false;
				removeFromCanvas();
				setTexture(texture);
				addToCanvas();
			}
		}

		int i = 0;
		while (i < amountOfDamage && (availableKnights.size() != 0 || availableOnagers.size() != 0 || availablePikemen.size() != 0)) {
			Random r = new Random();
			int whoIsTakingDamage = r.nextInt(Settings.nbTroopTypes);
			if (whoIsTakingDamage == 0) {
				//Knight takes damage
				if(availableKnights.isEmpty()) {
					--i;
				}else {		
					Knight knight = availableKnights.get(0);
					knight.setHealth(knight.getHP()-1);
					if(!knight.isAlive()) {
						availableKnights.remove(0);
					}
				}
			} else if(whoIsTakingDamage == 1) {
				//Pikeman takes damage
				if(availablePikemen.isEmpty()) {
					--i;
				}else {
					Pikeman pikeman = availablePikemen.get(0);
					pikeman.setHealth(pikeman.getHP()-1);
					if(!pikeman.isAlive()) {
						availablePikemen.remove(0);
					}
				}
			} else if(whoIsTakingDamage == 2) {
				//Onager takes damage
				if(availableOnagers.isEmpty()) {
					--i;
				}else {
					Onager onager= availableOnagers.get(0);
					onager.setHealth(onager.getHP()-1);
					if(!onager.isAlive()) {
						availableOnagers.remove(0);
					}
				}
			} else {
				break;
			}
			++i;
		}

		// If it has been conquered.
		if(availableKnights.isEmpty() && availablePikemen.isEmpty() && availableOnagers.isEmpty() && !troopAround.isEmpty()) {
			Image newTexture = new Image("/sprites/castles/castle_" + troopAround.get(0).getOwner() + ".png");
			Image newBuildTexture = new Image("/sprites/castles/castle_" + troopAround.get(0).getOwner() + "_build.png");
			Image newArmoredTexture = new Image("/sprites/castles/armored_castle_" + troopAround.get(0).getOwner() + ".png");
			texture = newTexture;
			buildingTexture = newBuildTexture;
			armoredTexture = newArmoredTexture;

			for (Troop troop : troopAround) {
				receiveTroop(troopAround.get(i));
				troopAround.get(i).getUnitButton().removeFromCanvas();
				gameMap[troopAround.get(i).getxPosMap()][troopAround.get(i).getyPosMap()] = 0;
			}
			troopAround.clear();
			setOwner(0);
			removeFromCanvas();
			setTexture(texture);
			addToCanvas();
			Game.playerCastles.add(this);
		}
	}

	/**
	 * Receives a troop.
	 * @param troop The troop to receive.
	 */
	public void receiveTroop(Troop troop) {
		if(troop.getClass() == Pikeman.class) {
			availablePikemen.add((Pikeman) troop);
			troop.removeFromCanvas();
		} else if(troop.getClass() == Knight.class) {
			availableKnights.add((Knight) troop);
		} else if(troop.getClass() == Onager.class){
			availableOnagers.add((Onager) troop);
		} else {
			treasure += ((Camel)troop).getMoney();
		}
	}

	/**
	 * Removes a troop from the castle.
	 * @param troop The troop to remove.
	 */
	public void removeTroop(Troop troop) {
		if(troop.getClass() == Pikeman.class) {
			availablePikemen.remove(0);
		}else if(troop.getClass() == Knight.class){
			availableKnights.remove(0);
		}else if(troop.getClass() == Onager.class){
			availableOnagers.remove(0);
		}
	}

	/**
	 * Produces a troop.
	 * @param troop The troop to be produced.
	 */
	public void produceTroop(Troop troop) {
		if(this.treasure >= troop.getProdCost()) {
			this.isProducingTroops = true;
			this.treasure -= troop.getProdCost();
			inProductionTroops.add(troop);
		}
	}

	/**
	 * Called when producing troops.
	 */
	public void onTroopProduction() {
		if (this.isProducingTroops) {
			int numberOfToopsInProd = Math.min(barrackLevel, inProductionTroops.size());

			for(int i = 0; i < numberOfToopsInProd; i++) {
				if(inProductionTroops.get(i).getProdTime() > 0) {
					inProductionTroops.get(i).setProdTime(inProductionTroops.get(i).getProdTime()-1);
					System.out.println(inProductionTroops.get(i).getClass());
					System.out.println(inProductionTroops.get(i).getProdTime());
				} else {
					if (inProductionTroops.get(i).getClass() == Pikeman.class) {
						availablePikemen.add((Pikeman) inProductionTroops.get(i));
					} else if(inProductionTroops.get(i).getClass() == Knight.class) {
						availableKnights.add((Knight) inProductionTroops.get(i));
					} else {
						availableOnagers.add((Onager) inProductionTroops.get(i));
					}
					inProductionTroops.remove(i);
				}
				if (inProductionTroops.size() <= barrackLevel) {
					--numberOfToopsInProd;
					--i;
				}
			}
		}
	}

	/**
	 * Levels up the barrack of the castle.
	 */
	public void levelUpBarrack() {
		final int timeIncrementation = 20;

		this.isBarrackLevelingUp = true;
		this.timeUntilBarrackLevelUp += timeIncrementation;
		this.treasure -= this.barracksBuildCost;

		removeFromCanvas();
		setTexture(buildingTexture);
		addToCanvas();
	}

	/**
	 * @return The build cost of the barrack.
	 */
	public int getBarrackBuildCost() {
		return barracksBuildCost;
	}

	/**
	 * @return The barrack level.
	 */
	public int getBarrackLevel() {
		return barrackLevel;
	}

	/**
	 * Sets the barrack level.
	 * @param barrackLevel The level.
	 */
	public void setBarrackLevel(int barrackLevel) {
		this.barrackLevel = barrackLevel;
	}

	/**
	 * @param index The index of the knight.
	 * @return The indexed knight.
	 */
	public Knight getKnightByIndex(int index) {
		return availableKnights.get(index);
	}

	/**
	 * @param index The index of the onager.
	 * @return The indexed onager.
	 */
	public Onager getOnagerByIndex(int index) {
		return availableOnagers.get(index);
	}

	/**
	 * @param index The index of the pikeman.
	 * @return The indexed pikeman.
	 */
	public Pikeman getPikemanByIndex(int index) {
		return availablePikemen.get(index);
	}

	/**
	 * @return True if the wall is being built, false otherwise.
	 */
	public boolean isBuildingWall() {
		return isBuildingWall;
	}

	/**
	 * @return True if the castle has a wall, false otherwise.
	 */
	public boolean hasWall() {
		return hasWall;
	}

	/**
	 * @return The cost of building the wall.
	 */
	public int getWallCost() {
		return wallCost;
	}

	/**
	 * @param treasure Sets the treasure of the castle.
	 */
	public void setTreasure(int treasure) {
		this.treasure = treasure;
	}

	/**
	 * Adds a troop to the queue of around troops.
	 * @param troop The troop.
	 */
	public void addTroopAround(Troop troop) {
		troopAround.add(troop);
	}

	/**
	 * @return The treasure of the castle.
	 */
	public int getTreasure() {
		return treasure;
	}

	/**
	 * @return The owner of the castle.
	 */
	public int getOwner() {
		return owner;
	}

	/**
	 * Sets the owner of the castle.
	 * @param owner The owner.
	 */
	public void setOwner(int owner) {
		this.owner = owner;

		final Image texture;
		if (owner <= Settings.nbMaxActiveDukes) {
			texture = new Image("/sprites/castles/castle_" + owner + ".png");
		} else {
			texture = new Image("/sprites/castles/castle_neutral.png");
		}

		setTexture(texture);
	}

	/**
	 * @return The owner name.
	 */
	public String getOwnerName() {
		return ownerName;
	}

	/**
	 * @return The door direction as int.
	 */
	public int getDoorDirection() {
		return doorDirection;
	}

	/**
	 * @return The level of the castle.
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * @return The number of troops.
	 */
	public int getNbTroops() {
		return availableKnights.size() + availableOnagers.size() + availablePikemen.size();
	}

	/**
	 * @return The number of knights.
	 */
	public int getNbKnights() { return availableKnights.size(); }

	/**
	 * @return The number of onagers.
	 */
	public int getNbOnagers() { return availableOnagers.size(); }

	/**
	 * @return The number of pikemen.
	 */
	public int getNbPikemen() { return availablePikemen.size(); }

	/**
	 * @return The passive income of the castle.
	 */
	public int getPassiveIncome() {
		return passiveIncome;
	}

	/**
	 * @return The cost of the next level.
	 */
	public int getNextLevelBuildCost() {
		return nextLevelBuildCost;
	}

	/**
	 * @return The time remaining until the next level is finished.
	 */
	public int getNextLevelRemainingTime() {
		return timeUntilLevelUp;
	}

	/**
	 * @return The time to build to wall.
	 */
	public int getWallTimeCost() {
		return wallTimeCost;
	}

	/**
	 * Sets the time to build the wall.
	 * @param wallTimeCost The time.
	 */
	public void setWallTimeCost(int wallTimeCost) {
		this.wallTimeCost = wallTimeCost;
	}

	/**
	 * @return True if it can level up, false otherwise.
	 */
	public boolean canLevelUp() {
		return this.treasure >= this.nextLevelBuildCost && !this.isLevelingUp;
	}

	/**
	 * Launches the leveling up of the castle.
	 */
	public void levelUp() {
		this.isLevelingUp = true;
		this.timeUntilLevelUp = this.nextLevelBuildTime;
		this.treasure -= this.nextLevelBuildCost;

		removeFromCanvas();
		setTexture(buildingTexture);
		addToCanvas();
	}

	/**
	 * @return True if the castle is leveling up, false otherwise.
	 */
	public boolean isLevelingUp() {
		return isLevelingUp;
	}

	/**
 	 * @return The troops of the castle.
	 */
	public ArrayList<Troop> getTroops() {
		ArrayList<Troop> troops = new ArrayList<>();
		troops.addAll(availableKnights);
		troops.addAll(availableOnagers);
		troops.addAll(availablePikemen);

		return troops;
	}

	/**
	 * @return The position of the castle.
	 */
	public Point2D getPosition() {
		return position;
	}

	/**
	 * Sets the position of the castle.
	 * @param position The position.
	 */
	public void setPosition(Point2D position) {
		this.position = position;
	}

	/**
	 * Sets the owner name.
	 * @param name The name.
	 */
	public void setOwnerName(String name) {
		ownerName = name;
	}

	/**
	 * Sets the troops of the castle.
	 * @param troops The troops.
	 */
	public void setTroops(ArrayList<Troop> troops) {
		ArrayList<Knight> knights = new ArrayList<>();
		ArrayList<Onager> onagers = new ArrayList<>();
		ArrayList<Pikeman> pikemen = new ArrayList<>();

		for (Troop troop : troops) {
			if (troop.getClass() == Knight.class) {
				knights.add((Knight)troop);
			} else if (troop.getClass() == Onager.class) {
				onagers.add((Onager)troop);
			} else if (troop.getClass() == Pikeman.class) {
				pikemen.add((Pikeman)troop);
			}
		}

		availableKnights = knights;
		availableOnagers = onagers;
		availablePikemen = pikemen;
	}

	/**
	 * Sets the level of the castle.
	 * @param level The level.
	 */
	public void setLevel(int level) {
		this.level = level;
	}
}

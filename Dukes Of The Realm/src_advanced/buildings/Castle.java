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
import java.util.concurrent.atomic.AtomicInteger;

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

	private boolean isInConstruction = false;
	private int timeUntilConstructionFinished;

	private boolean hasWall;
	private int wallHP = Settings.castleWallHP;
	private boolean isLevelingUpWall;
	private int timeUntilWallBuild = -1;

	private boolean isLevelingUp;
	private int timeUntilLevelUp = -1;

	private int barrackLevel = 1;
	private int barrackBuildCost = 1600;
	private int barrackBuildTime = 20;
	private boolean isLevelingUpBarrack;
	private int timeUntilBarrackLevelUp = -1;

	private boolean isProducingTroops;
	private ArrayList<Troop> inProductionTroops = new ArrayList<>();

	private ArrayList<Troop> leavingTroops = new ArrayList<>();
	private ArrayList<Troop> attackingTroops = new ArrayList<>();

	/**
	 * Default constructor
	 * @param renderLayer The JavaFX canvas onto which we draw.
	 * @param position The position of the castle in the window.
	 */
	public Castle(Pane renderLayer, Point2D position, int owner) {
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

		level = 1;
		treasure = Settings.initialTreasure;
		doorDirection = rdGen.nextInt(Direction.nbDirections);

		this.position = position;
		
		final int nbTroops = Settings.nbMinInitTroops + rdGen.nextInt(Settings.nbMaxInitTroops - Settings.nbMinInitTroops);
		for (int i = 0; i < nbTroops; ++i) {
			final int troopType = rdGen.nextInt(Settings.nbTroopTypes);
			
			if (troopType == 0) {
				Knight knight = new Knight(renderLayer, this);
				knight.setOwner(this.owner);
				availableKnights.add(knight);	
			} else if (troopType == 1) {
				Onager onager = new Onager(renderLayer, this);
				onager.setOwner(this.owner);
				availableOnagers.add(onager);
			} else if (troopType == 2) {
				Pikeman pikeman = new Pikeman(renderLayer, this);
				pikeman.setOwner(this.owner);
				availablePikemen.add(pikeman);
			}
		}

		setTexture(texture);

		textureView.setRotate(90 * doorDirection);
		textureView.setFitWidth(Settings.castleSize);
		textureView.setFitHeight(Settings.castleSize);

		updateData();
	}

	/**
	 * Update function that is called every turn.
	 */
	public void onUpdate(int[][] gameMap) {
		treasure += passiveIncome;

		onConstruction();
		onTroopProduction();
		onEnemySiege(gameMap);
	}

	private void onConstruction() {
		if (isInConstruction) {
			if (isLevelingUp) {
				if (timeUntilLevelUp > 0) {
					--timeUntilLevelUp;
				} else if (timeUntilLevelUp == 0) {
					level += 1;
					isLevelingUp = false;

					--timeUntilLevelUp;

					if (this.hasWall) {
						setTexture(armoredTexture);
					} else {
						setTexture(texture);
					}

					isInConstruction = false;
					updateData();
				}
			} else if (isLevelingUpWall) {
				if (timeUntilWallBuild > 0) {
					--timeUntilWallBuild;
				} else if (timeUntilWallBuild == 0) {
					isLevelingUpWall = false;
					hasWall = true;

					--timeUntilWallBuild;

					setTexture(armoredTexture);

					isInConstruction = false;
					updateData();
				}
			} else if (isLevelingUpBarrack) {
				if (timeUntilBarrackLevelUp > 0) {
					--timeUntilBarrackLevelUp;
				} else if (timeUntilBarrackLevelUp == 0) {
					barrackLevel += 1;
					isLevelingUpBarrack = false;

					--timeUntilBarrackLevelUp;

					if (this.hasWall) {
						setTexture(armoredTexture);
					} else {
						setTexture(texture);
					}

					isInConstruction = false;
					updateData();
				}
			}

			--timeUntilConstructionFinished;
		}
	}

	/**
	 * Called when producing troops.
	 */
	private void onTroopProduction() {
		if (isProducingTroops) {
			int nbToopsInProd = Math.min(barrackLevel, inProductionTroops.size());

			for(int i = 0; i < nbToopsInProd; i++) {
				if (inProductionTroops.get(i).getProdTime() > 0) {
					inProductionTroops.get(i).setProdTime(inProductionTroops.get(i).getProdTime() - 1);
					// TODO: Debug purposes
					System.out.println(inProductionTroops.get(i).getClass());
					System.out.println(inProductionTroops.get(i).getProdTime());
				} else {
					if (inProductionTroops.get(i).getClass() == Knight.class) {
						availableKnights.add((Knight)inProductionTroops.get(i));
					} else if (inProductionTroops.get(i).getClass() == Onager.class) {
						availableOnagers.add((Onager)inProductionTroops.get(i));
					} else if (inProductionTroops.get(i).getClass() == Pikeman.class) {
						availablePikemen.add((Pikeman)inProductionTroops.get(i));
					} else {
						throw new RuntimeException("Recruiting unsupported troop type.");
					}

					inProductionTroops.remove(i);
				}
				if (inProductionTroops.size() <= barrackLevel) {
					--nbToopsInProd;
					--i;
				}
			}
		}
	}

	/**
	 * Manages the castle when there are troops around.
	 */
	private void onEnemySiege(int[][] gameMap) {
		int amountOfDamage = 0;
		for(int i = 0; i < attackingTroops.size(); i++) {
			Troop unit = attackingTroops.get(i);
			if (unit.getOwner() == owner) {
				receiveTroop(unit);
				attackingTroops.remove(unit);
			} else {
				amountOfDamage += unit.getDamage();
			}
		}

		if (hasWall) {
			wallHP -= amountOfDamage;
			if(wallHP <= 0) {
				hasWall = false;
				setTexture(texture);
			}
		}else {
			

		int appliedDamage = 0;
		while (appliedDamage < amountOfDamage && getNbTroops() != 0) {
			Random r = new Random();
			int whoIsTakingDamage = r.nextInt(Settings.nbTroopTypes);
			if (whoIsTakingDamage == 0) {
				if (availableKnights.isEmpty()) {
					--appliedDamage;
				} else {
					Knight knight = availableKnights.get(0);
					knight.setHP(knight.getHP() - 1);
					if (!knight.isAlive()) {
						availableKnights.remove(knight);
					}
				}
			}
			else if(whoIsTakingDamage == 1) {
				if (availableOnagers.isEmpty()) {
					--appliedDamage;
				} else {
					Onager onager= availableOnagers.get(0);
					onager.setHP(onager.getHP() - 1);
					if(!onager.isAlive()) {
						availableOnagers.remove(onager);
					}
				}
			} else if (whoIsTakingDamage == 2) {
				if (availablePikemen.isEmpty()) {
					--appliedDamage;
				} else {
					Pikeman pikeman = availablePikemen.get(0);
					pikeman.setHP(pikeman.getHP() - 1);
					if (!pikeman.isAlive()) {
						availablePikemen.remove(pikeman);
					}
				} else {
					break;
				}
				++i;
			}

			++appliedDamage;
		}

		// If it has been conquered.
		if(getNbTroops() == 0 && !attackingTroops.isEmpty()) {
			Image newTexture = new Image("/sprites/castles/castle_" + attackingTroops.get(0).getOwner() + ".png");
			Image newBuildTexture = new Image("/sprites/castles/castle_" + attackingTroops.get(0).getOwner() + "_build.png");
			Image newArmoredTexture = new Image("/sprites/castles/armored_castle_" + attackingTroops.get(0).getOwner() + ".png");
			texture = newTexture;
			buildingTexture = newBuildTexture;
			armoredTexture = newArmoredTexture;

			for (Troop troop : attackingTroops) {
				receiveTroop(troop);
				gameMap[troop.getxPosMap()][troop.getyPosMap()] = 0;
			}
			attackingTroops.clear();

			setOwner(Game.playerID);

			setTexture(texture);
			Game.playerCastles.add(this);
		} else {
			attackingTroops.clear();
		}
	}

	/**
	 * Updates the data after leveling up.
	 */
	private void updateData() {
		passiveIncome = 100 * level;
		nextLevelBuildCost = 1000 * level;
		nextLevelBuildTime = 10 + 5 * level;

		barrackBuildCost = (int)Math.pow(1.5, barrackLevel) * barrackBuildCost;
		barrackBuildTime = (int)Math.pow(1.1, barrackLevel) * barrackBuildTime;
	}

	/**
	 * @return True if it can build wall, false otherwise.
	 */
	public boolean canBuildWall() {
		return (!hasWall && treasure >= Settings.castleWallBuildCost && !isInConstruction);
	}

	/**
	 * Launches the wall construction.
	 */
	public void levelUpWall() {
		isInConstruction = true;
		setTexture(buildingTexture);

		treasure -= Settings.castleWallBuildCost;
		isLevelingUpWall = true;
		timeUntilWallBuild = Settings.castleWallBuildTime;
		timeUntilConstructionFinished = timeUntilWallBuild;
	}

	/**
	 * Receives a troop.
	 * @param troop The troop to receive.
	 */
	public void receiveTroop(Troop troop) {
		if(troop.getClass() == Pikeman.class) {
			availablePikemen.add((Pikeman)troop);
			troop.removeFromCanvas();
		} else if(troop.getClass() == Knight.class) {
			availableKnights.add((Knight)troop);
		} else if(troop.getClass() == Onager.class){
			availableOnagers.add((Onager)troop);
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
	 * @return The number of knights being produced.
	 */
	public int getNbProducingKnights() {
		int nb = 0;
		for (Troop troop : inProductionTroops) {
			if (troop.getClass() == Knight.class) {
				++nb;
			}
		}

		return nb;
	}
	
	public boolean isPlayerCastle() {
		return this.owner == 0;
	}

	/**
	 * @return The number of onagers being produced.
	 */
	public int getNbProducingOnagers() {
		int nb = 0;
		for (Troop troop : inProductionTroops) {
			if (troop.getClass() == Onager.class) {
				++nb;
			}
		}

		return nb;
	}

	/**
	 * @return The number of pikemen being produced.
	 */
	public int getNbProducingPikemen() {
		int nb = 0;
		for (Troop troop : inProductionTroops) {
			if (troop.getClass() == Pikeman.class) {
				++nb;
			}
		}

		return nb;
	}

	/**
	 * @return True if it can level up the barrack, false otherwise.
	 */
	public boolean canLevelUpBarrack() {
		return (barrackLevel < Settings.castleBarrackMaxLevel && treasure >= barrackBuildCost && !isInConstruction);
	}

	/**
	 * Levels up the barrack of the castle.
	 */
	public void levelUpBarrack() {
		isInConstruction = true;
		setTexture(buildingTexture);

		treasure -= barrackBuildCost;

		isLevelingUpBarrack = true;
		timeUntilBarrackLevelUp = barrackBuildTime;
		timeUntilConstructionFinished = timeUntilBarrackLevelUp;
	}

	/**
	 * @return True if the wall is being built, false otherwise.
	 */
	public boolean isLevelingUpWall() {
		return isLevelingUpWall;
	}
	
	
	/**
	 * @param hasWAll sets if the castle has wall or not.
	 */
	public void setHasWall(boolean hasWall) {
		this.hasWall = hasWall;
	}

	/**
	 * @return True if the castle has a wall, false otherwise.
	 */
	public boolean hasWall() {
		return hasWall;
	}

	/**
	 * Sets if the castle has a wall or not.
	 * @param wall True if the castle has a wall, false otherwise.
	 */
	public void setHasWall(boolean wall) {
		hasWall = wall;
	}

	/**
	 * @return The treasure of the castle.
	 */
	public int getTreasure() {
		return treasure;
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
	public void addAttackingTroop(Troop troop) {
		attackingTroops.add(troop);
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
	 * Returns The door direction as int, knowing that:
	 * 0: South.
	 * 1: West.
	 * 2: North.
	 * 3: East.
	 * @return Door direction as int.
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
		return getNbKnights() + getNbOnagers() + getNbPikemen();
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
	 * @return True if it can level up, false otherwise.
	 */
	public boolean canLevelUp() {
		return (level < Settings.castleMaxLevel && treasure >= nextLevelBuildCost && !isInConstruction);
	}

	/**
	 * Launches the leveling up of the castle.
	 */
	public void levelUp() {
		isInConstruction = true;
		setTexture(buildingTexture);

		treasure -= nextLevelBuildCost;
		isLevelingUp = true;
		timeUntilLevelUp = nextLevelBuildTime;
		timeUntilConstructionFinished = timeUntilLevelUp;
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

	/**
	 * @return True if it belongs to the user, false otherwise.
	 */
	public boolean isPlayerCastle() {
		return owner == 0;
	}

	/**
	 * Recruit troops.
	 * @param recruitCommand The command to recruit.
	 */
	public void orderRecruit(ArrayList<AtomicInteger> recruitCommand) {
		int nbKnights = recruitCommand.get(0).get();
		int nbOnagers = recruitCommand.get(1).get();
		int nbPikemen = recruitCommand.get(2).get();

		while (nbKnights + nbOnagers + nbPikemen> 0) {
			if (nbKnights >0) {
				--nbKnights;
				Knight knight = new Knight(renderLayer, this);
				produceTroop(knight);
			} else if (nbOnagers>0) {
				--nbOnagers;
				Onager onager= new Onager(renderLayer, this);
				produceTroop(onager);
			} else {
				--nbPikemen;
				Pikeman pikeman = new Pikeman(renderLayer, this);
				produceTroop(pikeman);
			}
		}
	}

	public void orderMoneyTransfer(Castle receiver, int money, int[][] gameMap) {
		treasure -= money;
		Camel camel = new Camel(renderLayer, this, money);
		camel.launchMovingAnimation(getPosition(), receiver, camel, receiver.isPlayerCastle(), camel.getSpeed(), gameMap);
	}
	/**
	 * Moves selected troops to a castle
	 * @param receiver The castle that will receive the troops.
	 * @param moveCommand The move command.
	 */
	public void orderMove(Castle receiver, ArrayList<AtomicInteger> moveCommand, int[][] gameMap) {
		ArrayList<Troop> selectedTroops = new ArrayList<>();
		for (int i = 0; i < moveCommand.get(0).get(); ++i) {
			selectedTroops.add(availableKnights.get(i));
		}

		for (int i = 0; i < moveCommand.get(1).get(); ++i) {
			selectedTroops.add(availableOnagers.get(i));
		}

		for (int i = 0; i < moveCommand.get(2).get(); ++i) {
			selectedTroops.add(availablePikemen.get(i));
		}

		while(selectedTroops.size() >= Settings.ostSize) {
			int minSpeed = Math.min(selectedTroops.get(0).getSpeed(), selectedTroops.get(1).getSpeed());
			minSpeed = Math.min(minSpeed, selectedTroops.get(2).getSpeed());
			for(int i = 0; i < Settings.ostSize; i++) {
				Troop troop = selectedTroops.get(0);
				removeTroop(troop);
				troop.launchMovingAnimation(getPosition(), receiver, selectedTroops.get(0), receiver.isPlayerCastle(), minSpeed, gameMap);
				selectedTroops.remove(0);
			}
		}

		for(int i = 0; i < selectedTroops.size(); i++) {
			Troop troop = selectedTroops.get(0);
			removeTroop(troop);
			troop.launchMovingAnimation(getPosition(), receiver, selectedTroops.get(0), receiver.isPlayerCastle(), selectedTroops.get(0).getSpeed(), gameMap);
			selectedTroops.remove(0);
		}
	}

	public int getBarrackLevel() {
		return barrackLevel;
	}

	public void setBarrackLevel(int level) {
		barrackLevel = level;
	}

	public int getBarrackBuildCost() {
		return barrackBuildCost;
	}

	public int getTimeUntilConstruction() {
		return timeUntilConstructionFinished;
	}

	public boolean isInConstruction() {
		return isInConstruction;
	}
}

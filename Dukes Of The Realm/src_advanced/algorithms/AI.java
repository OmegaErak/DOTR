package algorithms;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import base.Settings;
import buildings.Castle;

public class AI {
	public enum Action {
		Recruit,
		Move,

		UpgradeCastle,
		UpgradeWall,
		UpgradeBarrack,

		Wait
	}

	private ArrayList<Castle> castles;
	int[][] gameMap;

	private Random rdGen = new Random();

	private Castle currentCastle;

	private double currentActionProb = Settings.AIInitialProbOfAction;

	AtomicInteger nbKnights = new AtomicInteger();
	AtomicInteger nbOnagers = new AtomicInteger();
	AtomicInteger nbPikemen = new AtomicInteger();

	public AI(ArrayList<Castle> castles, int[][] gameMap) {
		this.castles = castles;
		this.gameMap = gameMap;
	}

	public void onUpdate() {
		for (Castle castle : castles) {
			if (!castle.isPlayerCastle()) {
				if (shouldAct()) {
					updateCurrentCastle();
					applyRandomAction();	
				}
			}
		}
	}
	
	public void updateCurrentCastle() {
		int castleSelectionIndex = rdGen.nextInt(castles.size());
		while(castles.get(castleSelectionIndex).isPlayerCastle()) {
			castleSelectionIndex = rdGen.nextInt(castles.size());
		}
		currentCastle = castles.get(castleSelectionIndex);
	}

	public Castle selectCastleForAttack() {
		int castleSelectionIndex = rdGen.nextInt(castles.size());
		while(!castles.get(castleSelectionIndex).isPlayerCastle() && castles.get(castleSelectionIndex).getOwner() == currentCastle.getOwner()) {
			castleSelectionIndex = rdGen.nextInt(castles.size());
		}
		return castles.get(castleSelectionIndex);
	}

	private boolean shouldAct() {
		double prob = rdGen.nextDouble();
		boolean shouldAct = false;

		if (prob <= currentActionProb) {
			currentActionProb = Settings.AIInitialProbOfAction;
			shouldAct = true;
		} else {
			// If currentActionProb overflows 1.0, it's not a problem. It only means that the AI will have a 100% probability of acting.
			currentActionProb += rdGen.nextDouble() / 10;
		}

		return shouldAct;
	}

	public void applyRandomAction() {
		Action action = selectRandomAction();

		switch (action) {
		case Move:
			ArrayList<AtomicInteger> moveCommand = new ArrayList<>();
			
			nbKnights.set(rdGen.nextInt(currentCastle.getNbKnights()+1));
			nbOnagers.set(rdGen.nextInt(currentCastle.getNbOnagers()+1));
			nbPikemen.set(rdGen.nextInt(currentCastle.getNbPikemen()+1));
			moveCommand.add(nbKnights);
			moveCommand.add(nbOnagers);
			moveCommand.add(nbPikemen);
			currentCastle.orderMove(selectCastleForAttack(), moveCommand, gameMap);

			break;
		case Recruit:
			if (currentCastle.getTreasure() > Settings.AIMinimalTreasureForRecruitement && !isBuilding())
				currentCastle.orderRecruit(createRecruitCommand());
			break;
		case UpgradeBarrack:
			if (currentCastle.canLevelUpBarrack())
				currentCastle.levelUpBarrack();
			break;
		case UpgradeCastle:
			if (currentCastle.canLevelUp())
				currentCastle.levelUp();
			break;
		case UpgradeWall:
			if (currentCastle.canBuildWall())
				currentCastle.levelUpWall();	
			break;
		case Wait:
			break;
		}
	}

	private Action selectRandomAction() {
		int actionInt = rdGen.nextInt(Action.values().length);
		Action action;
		switch (actionInt) {
			case (0): {
				action = Action.Recruit;
				break;
			}
			case (1): {
				action = Action.Move;
				break;
			}
			case (2): {
				action = Action.UpgradeCastle;
				break;
			}
			case (3): {
				action = Action.UpgradeWall;
				break;
			}
			case (4): {
				action = Action.UpgradeBarrack;
				break;
			}
			default: {
				action = Action.Wait;
				break;
			}
		}

		return action;
	}

	public boolean isBuilding() {
		return currentCastle.isInConstruction();
	}

	public ArrayList<AtomicInteger> createRecruitCommand(){
		ArrayList<AtomicInteger> recruitCommand = new ArrayList<>();
		int knights = 0;
		int onagers = 0;
		int pikeman = 0;
		int budget = rdGen.nextInt((currentCastle.getTreasure()-Settings.AIMinimalTreasureForRecruitement)/100)*100 + Settings.AIMinimalTreasureForRecruitement;
		while(budget > 0) {
			switch(rdGen.nextInt(2)) {
				case(0):
					if(budget >= Settings.knightProdCost)
						budget -= Settings.knightProdCost;
					++knights;
					break;
				case(1):
					if(budget >= Settings.onagerProdCost)
						budget -= Settings.onagerProdCost;
					++onagers;
					break;
				case(2):
					if (budget >= Settings.pikemanProdCost)
						budget -= Settings.pikemanProdCost;
					++pikeman;
					break;
			}
		}
		nbKnights.set(knights);
		nbOnagers.set(onagers);
		nbPikemen.set(pikeman);
		recruitCommand.add(nbKnights);
		recruitCommand.add(nbOnagers);
		recruitCommand.add(nbPikemen);
		
		return recruitCommand;
	}

	
//
//
//	public ArrayList<Troop> iaSelectTroop() {
//		ArrayList<Troop> selectedTroops = new ArrayList<Troop>();
//		if(iaCastle.getNbPikemen()>1) {
//			Pikeman pikeman = iaCastle.getPikemanByIndex(0);
//			selectedTroops.add(pikeman);
//		}else if(iaCastle.getNbKnights()>1) {
//			Knight knight = iaCastle.getKnightByIndex(0);
//			selectedTroops.add(knight);
//		}else if(iaCastle.getNbOnagers()>1) {
//			Onager onager = iaCastle.getOnagerByIndex(0);
//			selectedTroops.add(onager);
//		}
//		return selectedTroops;
//	}
//
//	public Castle getIaCastle() {
//		return iaCastle;
//	}
//
//	public void setIaCastle(Castle iaCastle) {
//		this.iaCastle = iaCastle;
//	}
}

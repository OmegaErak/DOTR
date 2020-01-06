package algorithms;

import java.util.ArrayList;
import java.util.Random;

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
	};

	private ArrayList<Castle> castles;

	private Random rdGen = new Random();

	private Castle currentCastle;

	private double currentActionProb = Settings.AIInitialProbOfAction;

	private int castleSelection;

	public AI(ArrayList<Castle> castles) {
		this.castles = castles;
	}

	public void onUpdate() {
		for (Castle castle : castles) {
			if (!castle.isPlayerCastle()) {
				if (shouldAct()) {
					applyRandomAction();
				}
			}
		}
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

		// TODO
		switch (action) {

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

//	public boolean iaActionTime() {
//		System.out.println(this.timeUntilAction);
//		if(this.timeUntilAction>0) {
//			this.timeUntilAction -= 1;
//		}else {
//			this.iaCastle = castles.get(castleSelection);
//			return true;
//		}
//		return false;
//	}
//
//	public Castle selectCastleForAction() {
//		int castleSelectionIndex = rdGen.nextInt(castles.size());
//		while(castles.get(castleSelectionIndex).isPlayerCastle()) {
//			castleSelectionIndex = rdGen.nextInt(castles.size());
//
//		}
//		this.iaCastle = castles.get(castleSelectionIndex);
//		return castles.get(castleSelectionIndex);
//	}
//
//	public Castle selectCastleForAttack(Castle castle) {
//		int castleSelectionIndex = rdGen.nextInt(castles.size());
//		while(!castles.get(castleSelectionIndex).isPlayerCastle() && castles.get(castleSelectionIndex).getOwner() == castle.getOwner()) {
//			castleSelectionIndex = rdGen.nextInt(castles.size());
//		}
//		return castles.get(castleSelectionIndex);
//	}
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

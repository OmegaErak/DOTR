package algorithms;

import java.util.ArrayList;
import java.util.Random;

import base.Settings;
import buildings.Castle;
import troops.Knight;
import troops.Onager;
import troops.Pikeman;
import troops.Troop;

public class IA {

	private Castle iaCastle;
	private ArrayList<Castle> castles;
	private int timeUntilAction = Settings.iaTimeAction;
	private Random rdGen = new Random();
	private int castleSelection;
	
		public IA(ArrayList<Castle> castles) {
			this.castles = castles;
			System.out.println(castles != null);
		}
		
		public boolean iaActionTime() {
			System.out.println(this.timeUntilAction);
			if(this.timeUntilAction>0) {
				this.timeUntilAction -= 1;
			}else {
				timeUntilAction = Settings.iaTimeAction;
				this.iaCastle = castles.get(castleSelection);
				return true;
			}
			return false;
		}
		
		public Castle selectCastleForAction() {
			int castleSelectionIndex = rdGen.nextInt(castles.size());
			while(castles.get(castleSelectionIndex).isPlayerCastle()) {
				castleSelectionIndex = rdGen.nextInt(castles.size());
				
			}
			this.iaCastle = castles.get(castleSelectionIndex);
			return castles.get(castleSelectionIndex);
		}
		
		public Castle selectCastleForAttack(Castle castle) {
			int castleSelectionIndex = rdGen.nextInt(castles.size());
			while(!castles.get(castleSelectionIndex).isPlayerCastle() && castles.get(castleSelectionIndex).getOwner() == castle.getOwner()) {
				castleSelectionIndex = rdGen.nextInt(castles.size());
			}
			return castles.get(castleSelectionIndex);
		}
		
		
		
		
		public ArrayList<Troop> iaSelectTroop() {
			ArrayList<Troop> selectedTroops = new ArrayList<Troop>();
			if(iaCastle.getNbPikemen()>1) {
				Pikeman pikeman = iaCastle.getPikemanByIndex(0);
				selectedTroops.add(pikeman);
			}else if(iaCastle.getNbKnights()>1) {
				Knight knight = iaCastle.getKnightByIndex(0);
				selectedTroops.add(knight);
			}else if(iaCastle.getNbOnagers()>1) {
				Onager onager = iaCastle.getOnagerByIndex(0);
				selectedTroops.add(onager);
			}
			return selectedTroops;
		}

		public Castle getIaCastle() {
			return iaCastle;
		}

		public void setIaCastle(Castle iaCastle) {
			this.iaCastle = iaCastle;
		}
		
		
		
}

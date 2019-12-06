package troops;

import buildings.Castle;

public class Pikeman extends Troop {

	public Pikeman(int numberOfUnit, Castle castle) {
		this.prodCost 	= 100;
		this.prodTime 	= 5;
		this.speed 		= 2;
		this.health 	= 3;
		this.damage 	= 1;
		
		this.setPosition(castle.getPosition());
	}

}

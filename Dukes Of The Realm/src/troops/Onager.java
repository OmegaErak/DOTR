package troops;

import buildings.Castle;

public class Onager extends Troop {
	
	public Onager(int numberOfUnit, Castle castle) {
		this.prodCost 	= 1000;
		this.prodTime 	= 50;
		this.speed 		= 1;
		this.health 	= 5;
		this.damage 	= 10;
		
		this.setPosition(castle.getPosition());
	}


}

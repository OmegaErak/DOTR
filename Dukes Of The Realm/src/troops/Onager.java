package troops;

import buildings.Castle;

import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

public class Onager extends Troop {
	
	public Onager(Pane renderLayer, Image texture, Castle castle) {
		super(renderLayer, texture, castle);

		this.prodCost 	= 1000;
		this.prodTime 	= 50;
		this.speed 		= 1;
		this.health 	= 5;
		this.damage 	= 10;
		
		this.setPosition(castle.getPosition());
	}


}

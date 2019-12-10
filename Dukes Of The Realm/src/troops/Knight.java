package troops;

import buildings.Castle;

import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

public class Knight extends Troop {

	public Knight(Pane renderLayer, Image texture, Castle castle) {
		super(renderLayer, texture, castle);

		this.prodCost 	= 500;
		this.prodTime 	= 20;
		this.speed 		= 6;
		this.health 	= 3;
		this.damage 	= 5;
		
		//this.setPosition(castle.getPosition());
	}
	
	


}

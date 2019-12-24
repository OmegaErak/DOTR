package troops;

import base.Settings;
import buildings.Castle;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

public class Onager extends Troop {
	
	public Onager(Pane renderLayer, Castle castle) {
		super(renderLayer, castle);

		this.prodCost 	= 1000;
		this.prodTime 	= 50;
		this.speed 		= 1;
		this.health 	= 5;
		this.damage 	= 10;
		
		if (castle.getOwner() <= Settings.nbMaxActiveDukes) {
			texture = new Image("/sprites/troops/onager_" + castle.getOwner() + ".png");
		} else {
			texture = new Image("/sprites/troops/onager_neutral.png");
		}
		
		setTexture(texture);
	}


}

package troops;

import base.Settings;
import buildings.Castle;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

public class Knight extends Troop {

	public Knight(Pane renderLayer, Castle castle) {
		super(renderLayer, castle);

		prodCost 	= 500;
		this.prodTime 	= 20;
		this.speed 		= 10;
		this.health 	= 3;
		this.damage 	= 5;
		
		if (castle.getOwner() <= Settings.nbMaxActiveDukes) {
			texture = new Image("/sprites/troops/knight_" + castle.getOwner() + ".png");
		} else {
			texture = new Image("/sprites/troops/knight_neutral.png");
		}

		
		setTexture(texture);
	}
}

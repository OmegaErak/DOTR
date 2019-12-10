package troops;

import buildings.Castle;

import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

public class Pikeman extends Troop {

	public Pikeman(Pane renderLayer, Image texture, Castle castle) {
		super(renderLayer, texture, castle);

		this.prodCost 	= 100;
		this.prodTime 	= 5;
		this.speed 		= 2;
		this.health 	= 3;
		this.damage 	= 1;
	}
}

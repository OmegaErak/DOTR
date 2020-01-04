package troops;

import base.Settings;
import buildings.Castle;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

public class Pikeman extends Troop {

	public Pikeman(Pane renderLayer, Castle castle) {
		super(renderLayer, castle);

		this.prodCost 	= 100;
		this.prodTime 	= 5;
		this.speed 		= 4;
		this.health 	= 3;
		this.damage 	= 1;
		
		if (castle.getOwner() <= Settings.nbMaxActiveDukes) {
			texture = new Image("/sprites/troops/pikeman_" + castle.getOwner() + ".png");
		} else {
			texture = new Image("/sprites/troops/pikeman_neutral.png");
		}
		
		setTexture(texture);
	}
}

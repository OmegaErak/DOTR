package troops;

import base.Settings;

import buildings.Castle;

import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

/**
 * Onager class
 */
public class Onager extends Troop {
	/**
	 * @param renderLayer The JavaFX canvas.
	 * @param castle The castle of the troop.
	 */
	public Onager(Pane renderLayer, Castle castle) {
		super(renderLayer, castle);

		this.prodCost 	= Settings.onagerProdCost;
		this.prodTime 	= Settings.onagerProdTime;
		this.health 	= Settings.onagerHP;
		this.damage 	= Settings.onagerDamage;
		this.speed 		= Settings.onagerSpeed;

		if (castle.getOwner() <= Settings.nbMaxActiveDukes) {
			texture = new Image("/sprites/troops/onager_" + castle.getOwner() + ".png");
		} else {
			texture = new Image("/sprites/troops/onager_neutral.png");
		}
		
		setTexture(texture);
	}


}

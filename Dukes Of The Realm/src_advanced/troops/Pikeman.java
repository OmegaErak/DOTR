package troops;

import base.Settings;
import buildings.Castle;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

/**
 * Pikeman class
 */
public class Pikeman extends Troop {
	/**
	 * @param renderLayer The JavaFX canvas.
	 * @param castle The castle of the troop.
	 */
	public Pikeman(Pane renderLayer, Castle castle) {
		super(renderLayer, castle);

		this.prodCost 	= Settings.pikemanProdCost;
		this.prodTime 	= Settings.pikemanProdTime;
		this.health 	= Settings.pikemanHP;
		this.damage 	= Settings.pikemanDamage;
		this.speed 		= Settings.pikemanSpeed;

		if (castle.getOwner() <= Settings.nbMaxActiveDukes) {
			texture = new Image("/sprites/troops/pikeman_" + castle.getOwner() + ".png");
		} else {
			texture = new Image("/sprites/troops/pikeman_neutral.png");
		}
		
		setTexture(texture);
	}
}

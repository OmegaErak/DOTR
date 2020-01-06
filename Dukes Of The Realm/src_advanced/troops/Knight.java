package troops;

import base.Settings;
import buildings.Castle;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

/**
 * Knight class.
 */
public class Knight extends Troop {
	/**
	 * @param renderLayer The JavaFX canvas.
	 * @param castle The castle of the troop.
	 */
	public Knight(Pane renderLayer, Castle castle) {
		super(renderLayer, castle);

		this.prodCost 	= Settings.knightProdCost;
		this.prodTime 	= Settings.knightProdTime;
		this.health 	= Settings.knightHP;
		this.damage 	= Settings.knightDamage;
		this.speed 		= Settings.knightSpeed;

		Image texture;
		if (castle.getOwner() <= Settings.nbMaxActiveDukes) {
			texture = new Image("/sprites/troops/knight_" + castle.getOwner() + ".png");
		} else {
			texture = new Image("/sprites/troops/knight_neutral.png");
		}

		setTexture(texture);
	}
}

package troops;

import base.Settings;
import buildings.Castle;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

/**
 * Unit that transfers money.
 */
public class Camel extends Troop {
	// It's also the money being transferred.
	private final int prodCost;

	public Camel(Pane renderLayer, Castle castle, int prodCost) {
		super(renderLayer, castle);

		this.prodCost 	= prodCost;
		this.prodTime 	= Settings.camelProdTime;
		this.health 	= Settings.camelHP;
		this.damage 	= Settings.camelDamage;
		this.speed 		= Settings.camelSpeed;

		// TODO: Camel texture
		Image texture = new Image("/sprites/troops/money_0.png");
		setTexture(texture);
	}

	public int getMoney() {
		return prodCost;
	}
	
}

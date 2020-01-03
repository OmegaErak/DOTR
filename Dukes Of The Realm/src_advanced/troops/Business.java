package troops;

import buildings.Castle;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

public class Business extends Troop{
	

	public Business(Pane renderLayer, Castle castle,int prodCost) {
		super(renderLayer, castle);

		this.prodCost 	= prodCost;
		this.prodTime 	= 0;
		this.speed 		= 6;
		this.health 	= 1;
		this.damage 	= 0;
		texture = new Image("/sprites/troops/money.png");
		setTexture(texture);
	}
	
	
}

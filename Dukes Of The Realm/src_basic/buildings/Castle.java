package buildings;

import base.Direction;
import base.Settings;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import renderer.Sprite;
import troops.Knight;
import troops.Troop;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Castle extends Sprite {
	final static private List<String> dukeNames = new ArrayList<String>(Arrays.asList(
			"Jean-Cloud Van Damme",

			"Jean-Eudes",
			"Jean-Michel",
			"Jean-Marie",
			"Jean-Loup",
			"Jean-Côme",
			"Jean-Alex",
			"Jean-Kévin",
			"Jean-René",
			"Jean-Maurice",
			"Jean-Francis",
			"Jean-Jacques",
			"Jean-Noël",
			"Jean-George",
			"Jean-Brice",
			"Jean-Blaise",
			"Jean-Aimée",
			"Jean-Baptiste",
			"Jean-Bernard",
			"Jean-Briac",
			"Jean-Charles",
			"Jean-Jean",
			"Jean-Paul",
			"Jean-Ti",
			"Jean-Rêve",
			"Jean-Yves",

			"Jean-Cérien"
	));

	private Image texture;

	private int owner;
	private String ownerName;

	private Point2D position;
	private int doorDirection;
	
	private ArrayList<Troop> availableTroops = new ArrayList<>();

	private int nbKnights = 0;

	private Random rdGen = new Random();

	public Castle(Pane renderLayer, int owner, Point2D position) {
		super(renderLayer, position);

		if (owner <= Settings.nbMaxActiveDukes) {
			texture = new Image("/sprites/castles/castle_" + owner + ".png");
		} else {
			texture = new Image("/sprites/castles/castle_neutral.png");
		}

		this.owner = owner;

		final int index = rdGen.nextInt(dukeNames.size());
		this.ownerName = dukeNames.get(index);
		dukeNames.remove(index);

		this.doorDirection = rdGen.nextInt(Direction.nbDirections);

		this.position = position;
		
		final int nbTroops = Settings.minNbInitTroops + rdGen.nextInt(Settings.maxNbInitTroops - Settings.minNbInitTroops);
		for (int i = 0; i < nbTroops; ++i) {
			availableTroops.add(new Knight(renderLayer, this));
			++nbKnights;
		}

		setTexture(texture);

		textureView.setRotate(90 * doorDirection);
		textureView.setFitWidth(Settings.castleSize);
		textureView.setFitHeight(Settings.castleSize);
	}

	public void onUpdate() {
		final int nbNewTroops = Settings.minNbTroopsAddedPerTurn + rdGen.nextInt(1 + Settings.maxNbTroopsAddedPerTurn - Settings.minNbTroopsAddedPerTurn);
		for (int i = 0; i < nbNewTroops; ++i) {
			availableTroops.add(new Knight(renderLayer, this));
			++nbKnights;
		}
	}

	public int getOwner() {
		return owner;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public int getDoorDirection() {
		return doorDirection;
	}

	public int getNbKnights() { return nbKnights; }

	public Point2D getPosition() {
		return position;
	}

	public void setPosition(Point2D position) {
		this.position = position;
	}
}

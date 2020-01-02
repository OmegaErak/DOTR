package buildings;

import base.Direction;
import base.Settings;

import renderer.Sprite;
import troops.Knight;

import javafx.animation.PathTransition;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Polyline;
import javafx.util.Duration;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import java.io.File;
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

	private int owner;
	private String ownerName;

	private Point2D position;
	private int doorDirection;

	private ArrayList<Knight> availableKnights = new ArrayList<>();

	private ArrayList<Knight> attackingTroops = new ArrayList<>();

	private Random rdGen = new Random();

	public Castle(Pane renderLayer, int owner, Point2D position) {
		super(renderLayer, position);

		final Image texture;
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
			availableKnights.add(new Knight(renderLayer, this));
		}

		setTexture(texture);

		textureView.setRotate(90 * doorDirection);
		textureView.setFitWidth(Settings.castleSize);
		textureView.setFitHeight(Settings.castleSize);
	}

	public void onUpdate() {
		final int nbNewTroops = Settings.minNbTroopsAddedPerTurn + rdGen.nextInt(1 + Settings.maxNbTroopsAddedPerTurn - Settings.minNbTroopsAddedPerTurn);
		for (int i = 0; i < nbNewTroops; ++i) {
			availableKnights.add(new Knight(renderLayer, this));
		}

		Random rdGen = new Random();
		// Check if getting attacked
		if (attackingTroops.size() != 0) {
			Boolean isAttackFinished = false;
			for (Knight attackingKnight : attackingTroops) {
				for (int i = 0; i < attackingKnight.getDamage(); ++i) {

					// Castle is conquered
					if (availableKnights.size() == 0) {
						this.owner = attackingTroops.get(0).getAttachedCastle().getOwner();
						this.ownerName = attackingTroops.get(0).getAttachedCastle().getOwnerName();
						isAttackFinished = true;
						break;
					}

					final Knight attackedKnight = availableKnights.get(rdGen.nextInt(availableKnights.size()));
					attackedKnight.setHealth(attackedKnight.getHealth() - 1);
					if (attackedKnight.getHealth() == 0) {
						availableKnights.remove(attackedKnight);
					}
				}

				if (isAttackFinished) {
					break;
				}
			}
		}
	}

	// TODO: Variable sized and fixed sized arrays in same function
	public void moveTroops(Castle castle, ArrayList<Knight> selectedTroops, Double[] usedPath) {
		// TODO: Two polylines?
		Polyline polyLine = new Polyline();
		polyLine.getPoints().addAll(usedPath);
		renderLayer.getChildren().add(polyLine);
		Polyline poly = new Polyline();
		double dx = usedPath[0];
		double dy = usedPath[1];
		for(int i = 0; i < usedPath.length; i++) {
			if(i % 2 == 0) {
				usedPath[i] -= dx;
			} else {
				usedPath[i] -= dy;
			}
		}
		poly.getPoints().addAll(usedPath);

		for (Knight knight : selectedTroops) {
			knight.addToCanvas();

			// TODO: Magic constant
			final PathTransition moveAnimation = new PathTransition(Duration.seconds(usedPath.length / 6.0), poly);
			moveAnimation.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);
			moveAnimation.setNode(knight.getTextureView());
			moveAnimation.play();
			moveAnimation.setOnFinished(e -> {
				renderLayer.getChildren().remove(polyLine);
				Random rdGen = new Random();
				int oofType = rdGen.nextInt(2);
				File oof = new File("resources/sound/oof" + oofType + ".wav");
				try {
					Clip clip = AudioSystem.getClip();
					AudioInputStream inputStream = AudioSystem.getAudioInputStream(oof);
					clip.open(inputStream);
					clip.start();
				} catch (Exception e1) {
					e1.printStackTrace();
				}

				// TODO: Not the best way to do this. Is there a way to check if every animation is finished?
				ArrayList<Knight> troopsList = new ArrayList<>();
				troopsList.add(knight);
				castle.receiveTroops(this, troopsList);
			});

			availableKnights.remove(knight);
		}
	}

	public void receiveTroops(Castle sender, ArrayList<Knight> troops) {
		if (sender.getOwner() == this.owner) {
			availableKnights.addAll(troops);
		} else {
			attackingTroops.addAll(troops);
		}
	}

	public int getOwner() {
		return owner;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public ArrayList<Knight> getTroops() {
		return availableKnights;
	}

	public int getNbKnights() {
		return availableKnights.size();
	}

	public Knight getKnightByIndex(int index) {
		return availableKnights.get(index);
	}
}

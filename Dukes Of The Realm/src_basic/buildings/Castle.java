package buildings;

import algorithms.AStar;
import algorithms.Node;

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
	final static private List<String> dukeNames = new ArrayList<>(Arrays.asList(
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

		final int nbDirections = 4;
		final int doorDirection = rdGen.nextInt(nbDirections);

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

		// Check if getting attacked
		if (attackingTroops.size() != 0) {
			Random rdGen = new Random();

			int damageTaken = 0;
			for (final Knight attackingKnight : attackingTroops) {
				damageTaken += attackingKnight.getDamage();
				attackingKnight.removeFromCanvas();
			}

			for (int i = 0; i < damageTaken; ++i) {
				// Castle is conquered
				if (availableKnights.size() == 0) {
					this.owner = attackingTroops.get(0).getAttachedCastle().getOwner();
					this.ownerName = attackingTroops.get(0).getAttachedCastle().getOwnerName();
					this.textureView.setImage(new Image("/sprites/castles/castle_" + owner + ".png"));
					this.availableKnights.addAll(attackingTroops);
					break;
				}

				final Knight attackedKnight = availableKnights.get(rdGen.nextInt(availableKnights.size()));
				attackedKnight.addHP(-1);
				if (!attackedKnight.isAlive()) {
					availableKnights.remove(attackedKnight);
				}
			}

			attackingTroops.clear();
		}
	}

	public void moveTroops(Castle castle, ArrayList<Knight> selectedTroops) {
		int dxy = Settings.castleSize / 2;
		Node start = new Node(new Point2D(getPosition().getX() + dxy, getPosition().getY() + dxy), 0, 0);
		Node end = new Node(new Point2D(castle.getPosition().getX() + dxy, castle.getPosition().getY() + dxy), 0, 0);
		Double[] usedPath = AStar.shortestPath(start, end, true);

		Polyline pathLine = new Polyline();
		pathLine.getPoints().addAll(usedPath);
		renderLayer.getChildren().add(pathLine);

		Polyline usedPathPolyLine = new Polyline();
		double dx = usedPath[0];
		double dy = usedPath[1];
		for(int i = 0; i < usedPath.length; i++) {
			if(i % 2 == 0) {
				usedPath[i] -= dx;
			} else {
				usedPath[i] -= dy;
			}
		}
		usedPathPolyLine.getPoints().addAll(usedPath);

		for (Knight knight : selectedTroops) {
			knight.addToCanvas();

			final PathTransition moveAnimation = new PathTransition(Duration.seconds((double)usedPath.length / knight.getSpeed()), usedPathPolyLine);
			moveAnimation.setNode(knight.getTextureView());
			moveAnimation.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);
			moveAnimation.setOnFinished(e -> {
				renderLayer.getChildren().remove(pathLine);

				if (castle.getOwner() != this.getOwner()) {
					final int nbDiffSounds = 2;
					Random rdGen = new Random();
					int oofType = rdGen.nextInt(nbDiffSounds);
					File oof = new File("resources/sound/oof" + oofType + ".wav");
					try {
						Clip clip = AudioSystem.getClip();
						AudioInputStream inputStream = AudioSystem.getAudioInputStream(oof);
						clip.open(inputStream);
						clip.start();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}

				// TODO: Not the best way to do this. Is there a way to check if every animation is finished?
				ArrayList<Knight> troopsList = new ArrayList<>();
				troopsList.add(knight);
				castle.receiveTroops(this, troopsList);
			});
			moveAnimation.play();

			availableKnights.remove(knight);
		}

		selectedTroops.clear();
	}

	public void receiveTroops(Castle sender, ArrayList<Knight> troops) {
		if (sender.getOwner() == this.owner) {
			availableKnights.addAll(troops);
			for (Knight knight : troops) {
				knight.setAttachedCastle(this);
				knight.removeFromCanvas();
			}
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

	public ArrayList<Knight> getKnights() {
		return availableKnights;
	}

	public int getNbKnights() {
		return availableKnights.size();
	}

	public Knight getKnightByIndex(int index) {
		return availableKnights.get(index);
	}
}

package base;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Random;


import base.Direction;
import buildings.Castle;

public class DOTR {

    public DOTR() {}

    public void onUpdate() {
        // TODO
    }

    public void onRender() {
        // TODO
    }

	Tile castle = new Tile(Color.DARKGREY, Settings.castleSize, Settings.castleSize , Color.BLACK,1, false);
    public boolean nearACastle(ArrayList<Point2D> coord, int radius, Point2D p) {
		boolean flag = true;
		for(int pt = 0; pt < coord.size() ; ++pt) {
			Point2D p2 = coord.get(pt);
			flag = flag && (p.distance(p2) >= radius);
		}

		return flag;
	}
    
    public ArrayList<Point2D> RandomPosForCastle(){
    	Point2D p;
		Random r = new Random();
		ArrayList<Point2D> Coord = new ArrayList<Point2D>();
		while(Coord.size() < Settings.minCastles + r.nextInt(Settings.maxCastles - Settings.minCastles)) {
			p = new Point2D (r.nextInt(Settings.nbCasesHor) * Settings.castleSize, r.nextInt(Settings.nbCasesVer) * Settings.castleSize);
			if(nearACastle(Coord,Settings.LimitCastleRadius,p)) {
				Coord.add(p);
			}
		}
		return Coord;
    }

	//Generation of the grid
	public Parent createContent() {
		Pane root = new Pane();
		root.setPrefSize(Settings.windowWidth, Settings.windowHeight);
		
		final int nbCasesHor = Settings.windowWidth / Settings.castleSize;
		final int nbCasesVer = Settings.windowHeight / Settings.castleSize;
		Tile tile;
		Path path = new Path(null , null);

		for(int i = 0; i < nbCasesHor; ++i) {
			for(int j = 0; j < nbCasesVer; ++j) {
				Point2D p = new Point2D (i,j);
				tile = new Tile(null, Settings.castleSize, Settings.castleSize, Color.BLACK,1 , true , p ,path);
				tile.setTranslateX(i * Settings.castleSize);
				tile.setTranslateY(j * Settings.castleSize);
				
					
				root.getChildren().add(tile);
			}
			
		}

		
		ListIterator<Point2D> li = RandomPosForCastle().listIterator();
		Point2D p;
		castle.generateCastle(root, li.next(), Color.BLUE);
		while(li.hasNext()) {
			p = li.next();
			castle.generateCastle(root,p,Color.DARKGREY);
		}
		
		
		
		
		
		// Generation of neutral Castle
		// TODO: Abstract into function
		/*Point2D p;
		Random r = new Random();
		ArrayList<Point2D> Coord = new ArrayList<Point2D>();
		ArrayList<Castle> cstl = new ArrayList<Castle>();
		while(Coord.size() < Settings.minCastles + r.nextInt(Settings.maxCastles - Settings.minCastles)) {
			p = new Point2D (r.nextInt(nbCasesHor) * Settings.castleSize, r.nextInt(nbCasesVer) * Settings.castleSize);
			if(nearACastle(Coord,100,p)) {
				Coord.add(p);
			}
		}

		ListIterator<Point2D> li = Coord.listIterator();
		int i = 1;
		while(li.hasNext()) {
			p = li.next();
			double x = p.getX();
			double y = p.getY();
			Castle neutral_castle = new Castle(i, p);
			cstl.add(neutral_castle);
			
			Tile castle = new Tile(Color.DARKGREY, Settings.castleSize, Settings.castleSize , Color.BLACK);
			castle.setTranslateX(x);
			castle.setTranslateY(y);
			root.getChildren().add(castle);
			
			// Door generation
			if(neutral_castle.getDoorDirection() == Direction.North || neutral_castle.getDoorDirection() == Direction.South) {
				Tile door = new Tile(Color.WHITE, Settings.castleSize / 2, Settings.castleSize / 4, null);
				if(neutral_castle.getDoorDirection() == Direction.North) {
					door.setTranslateX(x + Settings.castleSize / 4);
					door.setTranslateY(y + 1); 
				}
				else {
					door.setTranslateX(x + Settings.castleSize / 4 );
					door.setTranslateY(y + (float)3 / (float)4 * Settings.castleSize );
				}
				
				root.getChildren().add(door);
			}
			else {
				Tile door = new Tile(Color.WHITE, Settings.castleSize / 4, Settings.castleSize / 2, null);
				if(neutral_castle.getDoorDirection() == Direction.East) {
					door.setTranslateX(x +1);
					door.setTranslateY(y + Settings.castleSize / 4); 
				}
				else {
					door.setTranslateX(x + (float)3 / (float)4 * Settings.castleSize);
					door.setTranslateY(y + Settings.castleSize / 4); 
				}
				
				root.getChildren().add(door);
			}
			
			i += 1;
		}*/
		
		return root;
	}
	
	
}
package base;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Random;

import buildings.Castle;
import javafx.event.EventType;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.Shadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import troops.Knight;
import troops.Onager;
import troops.Pikeman;
import troops.Troop;

public class Tile extends StackPane{
	
	
	private Paint value;
	private int wifth;
	private int height;
	private Paint stroke;
	private Point2D p;
	
	public Tile(Paint value, int width, int height, Paint stroke, double arg0 , boolean crossable , Point2D p , Path path) {
		this.p = p;
		
		Rectangle border = new Rectangle(width, height);
		Shadow shadow = new Shadow();
		border.setFill(value);
		border.setStroke(stroke);
		border.setStrokeWidth(arg0);
		this.setOnMouseEntered(e->border.setEffect(shadow));
		this.setOnMouseExited(e->border.setEffect(null));
		this.setOnMouseClicked(e -> path.setPath(path, p));
//		System.out.println(path);
		
		
		
		
		setAlignment(Pos.CENTER);
		getChildren().add(border);
		
		
	}
	
	public Tile(Paint value, int width, int height, Paint stroke, Rectangle rec, Castle castle, Pane root , boolean crossable) {
		rec = new Rectangle(width, height);
		rec.setFill(value);
		rec.setStroke(stroke);
		Tooltip t = new Tooltip("Chateau de " + castle.getOwner() + "\nNiveau " + castle.getLevel() + "\nFlorins " + castle.getTreasure());
		t.setFont(Font.font(16));
		Tooltip.install(rec, t);
		this.setOnMouseClicked(e -> generateTroop(root,castle,new Knight(1)));
		
				
		setAlignment(Pos.CENTER);
		getChildren().addAll(rec);
	}
	
	
	public Tile(Paint value, int width, int height, Paint stroke, double arg0 , boolean crossable) {
		Rectangle border = new Rectangle(width, height);
		border.setFill(value);
		border.setStroke(stroke);
		border.setStrokeWidth(arg0);
		this.setOnMouseEntered(e->border.setFill(Color.LIGHTGREY));
		this.setOnMouseExited(e->border.setFill(null));
//		this.getOnMouseClicked(e->));
		
		
		setAlignment(Pos.CENTER);
		getChildren().add(border);
	}
	
	

	
	public Parent generateTroop(Pane root, Castle Castle, Troop troop) {
		Tile unit = new Tile(null, Settings.castleSize - 4  , Settings.castleSize - 4 , Color.BLACK, 4 , false);
		Text text = new Text("F");
		if(troop.getClass() == Knight.class) {
			text = new Text("K");
			text.setFont(Font.font ("Verdana", 20));
			text.setFill(Color.RED);
			
			
		}else if (troop.getClass() == Pikeman.class){
			text = new Text("P");
			text.setFont(Font.font ("Verdana", 20));
			text.setFill(Color.RED);
			
		} else if(troop.getClass() == Onager.class) {
			text = new Text("O");
			text.setFont(Font.font ("Verdana", 20));
			text.setFill(Color.RED);
			
		}
		
		double x = Castle.getPosition().getX();
		double y = Castle.getPosition().getY();
		if(Castle.getDoorDirection() == Direction.North || Castle.getDoorDirection() == Direction.South) {
			if(Castle.getDoorDirection() == Direction.North) {
				unit.setTranslateX(x);
				unit.setTranslateY(y - Settings.castleSize ); 
			}
			else {
				unit.setTranslateX(x );
				unit.setTranslateY(y + Settings.castleSize);
			}
			
		}
		else {
			if(Castle.getDoorDirection() == Direction.East) {
				unit.setTranslateX(x - Settings.castleSize);
				unit.setTranslateY(y); 
			}
			else {
				unit.setTranslateX(x + Settings.castleSize);
				unit.setTranslateY(y); 
			}
			
			
		}
		root.getChildren().addAll(unit,text);
		return root;
	}


	public Parent generateCastle(Pane root, Point2D p , Paint color) {

		
		int i = 1;
		double x = p.getX();
		double y = p.getY();
		Castle Castle = new Castle(i, p);
		Rectangle rec = new Rectangle();
		
		Tile castle = new Tile(color, Settings.castleSize, Settings.castleSize , Color.BLACK, rec , Castle,root , false);		
		castle.setTranslateX(x);
		castle.setTranslateY(y);
		
		Tooltip t = new Tooltip("Chateau de " + Castle.getOwner() + "Florins" + Castle.getTreasure() + "\n" + "Niveau" + Castle.getLevel());
		Tooltip.install(rec, t);
		root.getChildren().add(castle);
		
			
		// Door generation
		if(Castle.getDoorDirection() == Direction.North || Castle.getDoorDirection() == Direction.South) {
			Tile door = new Tile(Color.WHITE, Settings.castleSize / 2, Settings.castleSize / 4, null ,1 , false);
			if(Castle.getDoorDirection() == Direction.North) {
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
			Tile door = new Tile(Color.WHITE, Settings.castleSize / 4, Settings.castleSize / 2, null,1, false);
			if(Castle.getDoorDirection() == Direction.East) {
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
		return root;
	}



	public Point2D getP() {
		return p;
	}

	public void setP(Point2D p) {
		this.p = p;
	}

	public Paint getValue() {
		return value;
	}


	public void setValue(Paint value) {
		this.value = value;
	}


	public int getWifth() {
		return wifth;
	}


	public void setWifth(int wifth) {
		this.wifth = wifth;
	}



	public void setHeight(int height) {
		this.height = height;
	}


	public Paint getStroke() {
		return stroke;
	}


	public void setStroke(Paint stroke) {
		this.stroke = stroke;
	}
	
	public Rectangle getRectangle(Rectangle rec) {
		return rec;
	}
	
	
}
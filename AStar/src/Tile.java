import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Random;

import javafx.event.EventType;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;


public class Tile extends StackPane{
	
	
	private Paint value;
	private int wifth;
	private int height;
	private Paint stroke;
	private Point2D p;
	
	public Tile(Paint value, int width, int height, Paint stroke, double arg0 , boolean crossable , Point2D p , Path path , Pane root) {
		this.p = p;
		
		Rectangle border = new Rectangle(width, height);
		border.setFill(value);
		border.setStroke(stroke);
		border.setStrokeWidth(arg0);
		this.setOnMouseEntered(e->border.setFill(Color.LIGHTGREY));
		this.setOnMouseExited(e->border.setFill(null));
		this.setOnMouseClicked(e -> path.setPath(path, p , root));
//		this.setOnMouseClicked(e -> {System.out.println(p) ;System.out.println(new Node(p.getX(),p.getY(),0,0).voisin());});
//		System.out.println(path);
		
		
		
		
		setAlignment(Pos.CENTER);
		getChildren().add(border);
		
		
	}
	
	public Tile(Paint value, int width, int height, Paint stroke, double arg0 , boolean crossable , Point2D p) {
		this.p = p;
		
		Rectangle border = new Rectangle(width, height);
		border.setFill(value);
		border.setStroke(stroke);
		border.setStrokeWidth(arg0);
//		System.out.println(path);
		
		
		
		
		setAlignment(Pos.CENTER);
		getChildren().add(border);
		
		
	}
}
import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;

public class Path {
	
	private Point2D p1;
	private Point2D p2;

	public Path( Point2D p1, Point2D p2) {
		this.p1 = p1;
		this.p2 = p2;
	}
	
	public List<Node> setPath(Path path, Point2D p , Pane root) {
		List<Node> ShortestPath = new ArrayList<Node>();
		if(path.p1 == null) {
			path.setP1(p);
		}else if (path.getP2() == null){
			path.setP2(p);
			Node start = new Node(path.getP1().getX() , path.getP1().getY(), 0 ,0);
			Node end = new Node(path.getP2().getX() , path.getP2().getY(), 0 ,0);
			System.out.println(start);
			System.out.println(end);
			ShortestPath = AEtoile.CheminPlusCourt(start , end , root);
			path.setP1(null);
			path.setP2(null);
			
		}
		return ShortestPath;
		
	}
	

	public Point2D getP1() {
		return p1;
	}

	public void setP1(Point2D p1) {
		this.p1 = p1;
	}

	public Point2D getP2() {
		return p2;
	}

	public void setP2(Point2D p2) {
		this.p2 = p2;
	}

	@Override
	public String toString() {
		return "Path [p1=" + p1 + ", p2=" + p2 + "]";
	}
	
	
	
}

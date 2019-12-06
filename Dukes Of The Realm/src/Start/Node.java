package Start;
import java.util.ArrayList;

public class Node {
	
	private double x;
	private double y;
	private double cout;
	private double heuristique;
	
	
	public Node(double x, double y, double cout, double heuristique) {
		this.x = x;
		this.y = y;
		this.cout = cout;
		this.heuristique = heuristique;
	}
	
	public ArrayList<Node> voisin(){
		ArrayList<Node> voisin = new ArrayList<Node>();
		
		Node v0 = new Node(x + 1,y,cout,heuristique);
		voisin.add(v0);
		
		Node v1 = new Node(x - 1,y,cout,heuristique);
		voisin.add(v1);
		
		Node v2 = new Node(x,y + 1,cout,heuristique);
		voisin.add(v2);
		
		Node v3 = new Node(x,y - 1,cout,heuristique);
		voisin.add(v3);
		
		return voisin;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getCout() {
		return cout;
	}

	public void setCout(double cout) {
		this.cout = cout;
	}

	public double getHeuristique() {
		return heuristique;
	}

	public void setHeuristique(double heuristique) {
		this.heuristique = heuristique;
	}
	
	
}
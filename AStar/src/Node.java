import java.util.ArrayList;

public class Node {
	
	private double x;
	private double y;
	private double cout;
	private double heuristique;
	private Node cameFrom;
	
	
	public Node(double x, double y, double cout, double heuristique) {
		this.x = x;
		this.y = y;
		this.cout = cout;
		this.heuristique = heuristique;
		this.cameFrom = cameFrom;
	}
	
	public ArrayList<Node> voisin(){
		ArrayList<Node> voisin = new ArrayList<Node>();
		
		
		if(x + Settings.castleSize < Settings.windowWidth) {
		Node v0 = new Node(x + Settings.castleSize,y,cout,heuristique);
		voisin.add(v0);
		}
		
		if(x - Settings.castleSize >= 0) {
		Node v1 = new Node(x - Settings.castleSize,y,cout,heuristique);
		voisin.add(v1);
		}
		
		
		if(y + Settings.castleSize < Settings.windowHeight) {
		Node v2 = new Node(x,y + Settings.castleSize,cout,heuristique);
		voisin.add(v2);
		}
		
		
		if(y - Settings.castleSize >= 0) {
		Node v3 = new Node(x,y - Settings.castleSize,cout,heuristique);
		voisin.add(v3);
		}
		
		return voisin;
	}

	public Node getCameFrom() {
		return cameFrom;
	}

	public void setCameFrom(Node cameFrom) {
		this.cameFrom = cameFrom;
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

	@Override
	public String toString() {
		return "Node [x=" + x + ", y=" + y + ", cout=" + cout + ", heuristique=" + heuristique + "]";
	}

	public double getHeuristique() {
		return heuristique;
	}

	public void setHeuristique(double heuristique) {
		this.heuristique = heuristique;
	}
	
	
}
package algorithms;
import java.util.ArrayList;

import base.Game;
import base.Settings;


public class Node {
	
	private double x;
	private double y;
	private double cout;
	private double heuristique;
	private Node cameFrom;
	private double f;
	
	
	public Node(double x, double y, double cout, double heuristique) {
		this.x = x;
		this.y = y;
		this.cout = cout;
		this.heuristique = heuristique;
	}
	
	public ArrayList<Node> voisin(int[][] tab , boolean allowDiagonale){
		ArrayList<Node> voisin = new ArrayList<Node>();
		
		double xm = x - Settings.cellSize;
		double xp = x + Settings.cellSize;
		double ym = y - Settings.cellSize;
		double yp = y + Settings.cellSize;
		
		
		
		if(xp < Settings.windowWidth && isCrossable((int) xp,(int) y , tab)) {
		Node v0 = new Node(xp,y,cout,heuristique);
		voisin.add(v0);
		}
		
		if(xp < Settings.windowWidth && yp < Settings.windowHeight && isCrossable((int) xp ,(int) yp , tab) && allowDiagonale) {
		Node vd0 = new Node(xp , yp,cout,heuristique);
		voisin.add(vd0);
		}
		
		if(xm >= 0 && isCrossable((int) xm,(int) y, tab)) {
		Node v1 = new Node(xm,y,cout,heuristique);
		voisin.add(v1);
		}
		
		if(xm >= 0 && yp < Settings.windowHeight && isCrossable((int) xm ,(int) yp, tab) && allowDiagonale) {
		Node vd1 = new Node(xm , yp,cout,heuristique);
		voisin.add(vd1);
		}
		
		
		if(yp < Settings.windowHeight && isCrossable((int) x,(int) yp, tab)) {
		Node v2 = new Node(x , yp,cout,heuristique);
		voisin.add(v2);
		}
		
		if(xm >= 0 && ym >= Settings.statusBarHeight && isCrossable((int) xm ,(int) ym, tab) && allowDiagonale) {
		Node vd2 = new Node(xm , ym,cout,heuristique);
		voisin.add(vd2);
		}
		
		
		if(ym >= Settings.statusBarHeight && isCrossable((int) x,(int) ym, tab)) {
		Node v3 = new Node(x , ym,cout,heuristique);
		voisin.add(v3);
		}
		
		if(xp < Settings.windowWidth && ym >= Settings.statusBarHeight && isCrossable((int) xp ,(int) ym, tab) && allowDiagonale) {
		Node vd3 = new Node(xp , ym,cout,heuristique);
		voisin.add(vd3);
		}
		
		return voisin;
	}
	
	public boolean isCrossable(int x, int y,int[][] tab) {
		
		if(tab[x / Settings.cellSize][(y - Settings.statusBarHeight) / Settings.cellSize] == 1) {
			return false;
		}else {
			return true;
		}
		
		
	}
	
	public boolean isArround(Node node) {
		double dx = Math.sqrt( (x - node.getX()) * (x - node.getX()) );
		double dy = Math.sqrt( (y - node.getY()) * (y - node.getY()) );
		
		return(dx <=30 && dy <= 30);

	}
	

	public double getF() {
		return f;
	}

	public void setF(double f) {
		this.f = f;
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


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Node other = (Node) obj;
		if (cameFrom == null) {
			if (other.cameFrom != null)
				return false;
		} else if (!cameFrom.equals(other.cameFrom))
			return false;
		if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
			return false;
		if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
			return false;
		return true;
	}
	
	
}
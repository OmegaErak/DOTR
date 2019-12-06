import javafx.geometry.Point2D;

public class Kruskal {
	
	private double x;
	private double y;
	private double cout;
	private double heuristique;
	
	
	
	
	public Kruskal(double x, double y, double cout, double heuristique) {
		super();
		this.x = x;
		this.y = y;
		this.cout = cout;
		this.heuristique = heuristique;
	}


	public int compare2noeuds(Kruskal n1 , Kruskal n2) {
		if( n1.getHeuristique() < n1.getHeuristique()) {
			return 1;
		}else if(n1.getHeuristique() == n1.getHeuristique()){
			return 0;
		}else {
			return -1;
		}
	}

	public void CheminPlusCourt() {
		
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

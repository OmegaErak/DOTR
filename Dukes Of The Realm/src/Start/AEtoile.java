package Start;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.ListIterator;

import javafx.geometry.Point2D;

public abstract class AEtoile {

	public AEtoile() {
		
	}
	
	
	public static class compare2Noeuds implements Comparator<Node>{

		@Override
		public int compare(Node n1, Node n2) {
			if(n1.getHeuristique() < n2.getHeuristique()) {
				return 1;
			}else if(n1.getHeuristique() == n2.getHeuristique()) {
				return 0;
			}else {
				return -1;
			}
		}
		
	}
	
	
	
	
	
	Node start;
	
	public static ArrayList<Point2D> CheminPlusCourt(Node start , Node end) {
		LinkedList<Node> closedList = new LinkedList<Node>();
		LinkedList<Node> openList = new LinkedList<Node>();
		ArrayList<Point2D> path = new ArrayList<Point2D>();
		Collections.sort(openList,new compare2Noeuds());
		openList.add(start);
		
		while(!openList.isEmpty()) {
			Node u = openList.pop();
			if(u.getX() == end.getX() && u.getY() == end.getY()) {
				ListIterator<Node> li = closedList.listIterator();
				while(li.hasNext()) {
					Node n = li.next();
					 Point2D p = new Point2D(n.getX(),n.getY());
					 System.out.println(p);
					path.add(p);
				}
				return path;
			}
			for(Node v : u.voisin()) {
				if(closedList.contains(v) || (openList.contains(v) && (openList.get(openList.indexOf(v)).getCout() > v.getCout()))){
					
				}else {
					v.setCout(u.getCout() + 1);
					Point2D p1 = new Point2D(v.getX(),v.getY());
					Point2D p2 = new Point2D(end.getX() , end.getY());
					
					v.setHeuristique(v.getCout() + p1.distance(p2));
					openList.add(v);
				}
				closedList.add(u);
			}
			
			
			
		}
		return null;
		
	}
	
	
	
//		   Fonction cheminPlusCourt(g:Graphe, objectif:Nœud, depart:Nœud)
//		       closedList = File()
//		       openList = FilePrioritaire(comparateur=compare2Noeuds)
//		       openList.ajouter(depart)
//		       tant que openList n'est pas vide
//		           u = openList.depiler()
//		           si u.x == objectif.x et u.y == objectif.y
//		               reconstituerChemin(u)
//		               terminer le programme
//		           pour chaque voisin v de u dans g
//		               si v existe dans closedList ou si v existe dans openList avec un cout inférieur
//		                    neRienFaire()
//		               sinon
//		                    v.cout = u.cout +1 
//		                    v.heuristique = v.cout + distance([v.x, v.y], [objectif.x, objectif.y])
//		                    openList.ajouter(v)
//		           closedList.ajouter(u)
//		       terminer le programme (avec erreur)
}

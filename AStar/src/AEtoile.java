
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public abstract class AEtoile {

	public AEtoile() {
		
	}
	
	public static class NodeComparator implements Comparator<Node>
	{
	    @Override
	    public int compare(Node a, Node b)
	    {
	    	return Double.compare(a.getHeuristique(), b.getHeuristique());
	    }
	}
	
	
//	public static class compare2Noeuds implements Comparator<Node>{
//
//		@Override
//		public int compare(Node n1, Node n2) {
//			if(n1.getHeuristique() < n2.getHeuristique()) {
//				return 1;
//			}else if(n1.getHeuristique() == n2.getHeuristique()) {
//				return 0;
//			}else {
//				return -1;
//			}
//		}
//		
//	}
	
	
	private static List<Point2D> reconstructPath( Node current) {
		
			List<Point2D> totalPath = new ArrayList<>(200); // arbitrary value, we'll most likely have more than 10 which is default for java
			
			// total_path := [current]
			Point2D currentPath = new Point2D(current.getX() , current.getY());
			totalPath.add( currentPath);
				
			// while current in came_from:
			// current := came_from[current]
			while( (current = current.getCameFrom()) != null) {

				// total_path.append(current)
				Point2D path = new Point2D(current.getX() , current.getY());
				totalPath.add(path);
		       
			}
	        
			// return total_path
			return totalPath;
		}
	
	
	
	
	Node start;
	static boolean conatinsNeighbor;
	
	public static List<Point2D> CheminPlusCourt(Node start , Node end , Pane root) {
		Tile tile;
		int tileCount = Settings.nbCasesHor * Settings.nbCasesVer;
		Set<Node> closedList = new HashSet<Node>(tileCount);
		PriorityQueue<Node> openList = new PriorityQueue<Node>(tileCount , new NodeComparator());
		//ArrayList<Point2D> path = new ArrayList<Point2D>();
		
		openList.add(start);
		
		start.setCout(0);
		start.setHeuristique(start.getCout() + heuristicCostEstimate(start,end));
		
		while(!openList.isEmpty()) {
			
			Node u = openList.poll();
			if(u == end) {
				List<Point2D>  reconstructedPath = reconstructPath(end);
				for(int i = 0 ; i < reconstructedPath.size() ; i++) {
					Point2D cell = reconstructedPath.get(i);
					tile = new Tile(Color.INDIANRED, Settings.castleSize, Settings.castleSize, Color.BLACK,1 , true , cell);
					tile.setTranslateX(cell.getX());
					tile.setTranslateY(cell.getY());
					root.getChildren().addAll(tile);
					
				}
				
				return reconstructedPath;
			}
			
			closedList.add(u);
			
			for(Node neighbor : u.voisin()) {
				Point2D merde = null ;
				//System.out.println(v);
				tile = new Tile(Color.INDIANRED, Settings.castleSize, Settings.castleSize, Color.BLACK,1 , true , merde);
				tile.setTranslateX(neighbor.getX());
				tile.setTranslateY(neighbor.getY());
				root.getChildren().addAll(tile);
				
				if(closedList.contains(neighbor)){
					continue;
				}
				
				double tentativeScoreG = u.getCout() + distBetween( u, neighbor);
				
				if( !(conatinsNeighbor= openList.contains(neighbor)) || Double.compare(tentativeScoreG, neighbor.getCout()) < 0) {
					
					// came_from[neighbor] := current
					neighbor.setCameFrom(u);
				
					// g_score[neighbor] := tentative_g_score
					neighbor.setCout(tentativeScoreG);
					
					// f_score[neighbor] := g_score[neighbor] + heuristic_cost_estimate(neighbor, goal)
					neighbor.setHeuristique(heuristicCostEstimate(neighbor, end));
					neighbor.setHeuristique(neighbor.getCout() + neighbor.getHeuristique());
					
				}
				
//				neighbor.setCout(u.getCout() + 1);
//				Point2D p1 = new Point2D(neighbor.getX(),neighbor.getY());
//				Point2D p2 = new Point2D(end.getX() , end.getY());
//				Point2D pex = new Point2D(neighbor.getX() , neighbor.getY());		
//				neighbor.setHeuristique(neighbor.getCout() + p1.distance(p2));					
				openList.add(neighbor);
				
				
			}
			
			
			
		}
		return null;
		
	}
	
	private static double distBetween(Node current, Node neighbor) {
		return heuristicCostEstimate( current, neighbor);
	}
	
	/**
	 * Distance between two cells. We use the euclidian distance here. 
	 * Used in the algorithm as distance calculation between a cell and the goal. 
	 */
	private static double heuristicCostEstimate(Node from, Node to) {
		
		return Math.sqrt((from.getX()-to.getX())*(from.getX()-to.getX() + (from.getY() - to.getY())*(from.getY() - to.getY())));
		
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

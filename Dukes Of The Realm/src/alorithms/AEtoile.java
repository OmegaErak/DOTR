package alorithms;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

import base.Settings;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public abstract class AEtoile {

	
	
	
	
	
	
	
	
	
	public AEtoile() {
		
	}
	
	public static class NodeComparator implements Comparator<Node>
	{
	    @Override
	    public int compare(Node a, Node b)
	    {
	    	return Double.compare(a.getF(), b.getF());
	    }
	}
	

	
	
	private static List<Node> reconstructPath( Node current) {
		
			List<Node> totalPath = new ArrayList<>(200); // arbitrary value, we'll most likely have more than 10 which is default for java
			
			// total_path := [current]
			totalPath.add( current);
				
			// while current in came_from:
			// current := came_from[current]
			while( (current = current.getCameFrom()) != null) {

				// total_path.append(current)
				totalPath.add(current);
		       
			}
	        
			// return total_path
			return totalPath;
		}
	
	
	
	
	
	
	public static List<Node> CheminPlusCourt(Node start , Node end , Pane root, boolean allowDiagonale) {
		
		Node current = null;
		boolean containsNeighbor;
		
		int cellCount = Settings.gridCellsCountX/Settings.cellSize * Settings.gridCellsCountY/Settings.cellSize;
		Set<Node> closedList = new HashSet<Node>(cellCount);
		PriorityQueue<Node> openList = new PriorityQueue<Node>(cellCount , new NodeComparator());
		
		openList.add(start);
		
		start.setCout(0);
		start.setF(start.getCout() + heuristicCostEstimate(start,end));
		
		while(!openList.isEmpty()) {
			
			if(closedList.size() > cellCount) {
				System.out.println("Error infinite loop");
				return null;
			}
			
			current  = openList.poll();
			if(current.getX() == end.getX() && current.getY() == end.getY()) {
				List<Node>reconstructedPath = reconstructPath(current);
				for(int i = 0 ; i < reconstructedPath.size() - 1 ; i++) {
					Point2D from = new Point2D(reconstructedPath.get(i).getX() , reconstructedPath.get(i).getY());
					Point2D to = new Point2D(reconstructedPath.get(i+1).getX() , reconstructedPath.get(i+1).getY());
					Line line = new Line();
					line.setStartX(from.getX() + Settings.castleSize/2);
					line.setStartY(from.getY()+ Settings.castleSize/2);
					line.setEndX(to.getX()+ Settings.castleSize/2);
					line.setEndY(to.getY()+ Settings.castleSize/2);
					root.getChildren().add(line);
					
				}
				
				return reconstructedPath;
			}
			
			closedList.add(current);
			
			for(Node neighbor : current.voisin(allowDiagonale)) {
				
				double x = neighbor.getX();
				double y = neighbor.getY();
				
				boolean allReadyVisited = false;
				
				for(Node v : closedList) {
					if(v.getX() == x && v.getY() == y) {
						allReadyVisited = true;
						break;
					}
				}
						
				if(allReadyVisited){
					continue;
				}
				
//				double tentativeScoreG = current.getCout() + distBetween( current, neighbor);
				double tentativeScoreG = current.getCout() + 1;
				
				containsNeighbor = false;
				
				for(Node v : openList) {
					if(v.getX() == x && v.getY() == y) {
						containsNeighbor = true;
						break;
					}
				}
				
				if( !containsNeighbor || Double.compare(tentativeScoreG, neighbor.getCout()) < 0) {
					
					// came_from[neighbor] := current
					neighbor.setCameFrom(current);
				
					// g_score[neighbor] := tentative_g_score
					neighbor.setCout(tentativeScoreG);
					
					// f_score[neighbor] := g_score[neighbor] + heuristic_cost_estimate(neighbor, end)
					neighbor.setHeuristique(heuristicCostEstimate(neighbor, end));
					neighbor.setF(neighbor.getCout() + neighbor.getHeuristique());
					
				}
				
				if(!containsNeighbor) {
					openList.add(neighbor);
				}
				
				
				
			}
			
			
			
		}
		System.out.println("No path");
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
		
		Point2D p1 = new Point2D(from.getX() , from.getY());
		Point2D p2 = new Point2D(to.getX() , to.getY());
		
		return p1.distance(p2)/Settings.castleSize * 5;
		
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

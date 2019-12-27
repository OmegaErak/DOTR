package algorithms;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

import base.Settings;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;

public abstract class AStar {
	public AStar() {
	}
	
	public static class NodeComparator implements Comparator<Node>	{
	    @Override
	    public int compare(Node a, Node b) {
	    	return Double.compare(a.getF(), b.getF());
	    }
	}

	private static List<Node> reconstructPath( Node current) {
			List<Node> totalPath = new ArrayList<>(200); // arbitrary value, we'll most likely have more than 10 which is default for java
			totalPath.add( current);
				
			while( (current = current.getCameFrom()) != null) {
				totalPath.add(current);
			}
	        
			// return total_path
			return totalPath;
		}

	public static List<Node> CheminPlusCourt(Node start , Node end , Pane root, boolean allowDiagonale) {
		Node current = null;
		boolean containsNeighbor;
		
		int cellCount = Settings.gridCellsCountX/Settings.cellSize * Settings.gridCellsCountY/Settings.cellSize;
		Set<Node> closedList = new HashSet<>(cellCount);

		PriorityQueue<Node> openList = new PriorityQueue<>(cellCount , new NodeComparator());
		openList.add(start);
		
		start.setCout(0);
		start.setF(start.getCout() + heuristicCostEstimate(start,end));
		
		while(!openList.isEmpty()) {
			if(closedList.size() > cellCount) {
				System.out.println("Error infinite loop");
				return null;
			}
			
			current  = openList.poll();
			if(current.getX() == end.getX() && current.getY() == end.getY() || current.arround(end)) {
				List<Node>reconstructedPath = reconstructPath(current);
				for(int i = 0 ; i < reconstructedPath.size() - 1 ; i++) {
					Point2D from = new Point2D(reconstructedPath.get(i).getX() , reconstructedPath.get(i).getY());
					Point2D to = new Point2D(reconstructedPath.get(i+1).getX() , reconstructedPath.get(i+1).getY());
					Line line = new Line();
					line.setStartX(from.getX());
					line.setStartY(from.getY());
					line.setEndX(to.getX());
					line.setEndY(to.getY());
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
				
				double tentativeScoreG = current.getCout() + 1;
				
				containsNeighbor = false;
				for(Node v : openList) {
					if(v.getX() == x && v.getY() == y) {
						containsNeighbor = true;
						break;
					}
				}
				
				if(!containsNeighbor || Double.compare(tentativeScoreG, neighbor.getCout()) < 0) {
					neighbor.setCameFrom(current);
					neighbor.setCout(tentativeScoreG);
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
}

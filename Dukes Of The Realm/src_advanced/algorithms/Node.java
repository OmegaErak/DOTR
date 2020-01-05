package algorithms;

import base.Settings;
import javafx.geometry.Point2D;

import java.util.ArrayList;

public class Node {
	private Point2D position;

	private double cost;
	private double heuristicCost;
	private double totalCost;

	private Node fatherNode;

	/**
	 * @param position The position in the window.
	 * @param cost Its cost.
	 * @param heuristicCost Its heuristic cost.
	 */
	public Node(Point2D position, double cost, double heuristicCost) {
		this.position = position;

		this.cost = cost;
		this.heuristicCost = heuristicCost;
	}

	/**
	 * @param allowDiagonals True to allow diagonal paths, false to not.
	 * @return The node neighbours of the node.
	 */
	public ArrayList<Node> getNeighbours(int[][] gameMap, boolean allowDiagonals){
		ArrayList<Node> neighbours = new ArrayList<>();

		int x = (int)position.getX();
		int y = (int)position.getY();
		int xm = (int)position.getX() - Settings.cellSize;
		int xp = (int)position.getX() + Settings.cellSize;
		int yp = (int)position.getY() + Settings.cellSize;
		int ym = (int)position.getY() - Settings.cellSize;

		Point2D upperPoint = new Point2D(x, ym);
		if(ym >= 0 && isCrossable(upperPoint, gameMap)) {
			Node upperPointNode = new Node(upperPoint, cost, heuristicCost);
			neighbours.add(upperPointNode);
		}

		Point2D upperRightPoint = new Point2D(xp, ym);
		if(xp < Settings.windowWidth && ym >= 0 && allowDiagonals && isCrossable(upperRightPoint, gameMap)) {
			Node upperRightPointNode = new Node(upperRightPoint, cost, heuristicCost);
			neighbours.add(upperRightPointNode);
		}

		Point2D rightPoint =  new Point2D(xp, y);
		if(xp < Settings.windowWidth && isCrossable(rightPoint, gameMap)) {
			Node rightPointNode = new Node(rightPoint, cost, heuristicCost);
			neighbours.add(rightPointNode);
		}

		Point2D bottomRightPoint = new Point2D(xp, yp);
		if(xp < Settings.windowWidth && yp < Settings.windowHeight && allowDiagonals && isCrossable(bottomRightPoint, gameMap)) {
			Node bottomRightPointNode = new Node(bottomRightPoint, cost, heuristicCost);
			neighbours.add(bottomRightPointNode);
		}

		Point2D bottomPoint = new Point2D(x, yp);
		if(yp < Settings.windowHeight && isCrossable(bottomPoint, gameMap)) {
			Node bottomPointNode = new Node(bottomPoint, cost, heuristicCost);
			neighbours.add(bottomPointNode);
		}

		Point2D bottomLeftPoint = new Point2D(xm, yp);
		if(xm >= 0 && yp < Settings.windowHeight && allowDiagonals && isCrossable(bottomLeftPoint, gameMap)) {
			Node bottomLeftPointNode = new Node(bottomLeftPoint, cost, heuristicCost);
			neighbours.add(bottomLeftPointNode);
		}

		Point2D leftPoint = new Point2D(xm, y);
		if(xm >= 0 && isCrossable(leftPoint, gameMap)) {
			Node leftPointNode = new Node(leftPoint, cost, heuristicCost);
			neighbours.add(leftPointNode);
		}

		Point2D upperLeftPoint = new Point2D(xm, ym);
		if(xm >= 0 && ym >= 0 && allowDiagonals && isCrossable(upperLeftPoint, gameMap)) {
			Node upperLeftPointNode = new Node(upperLeftPoint, cost, heuristicCost);
			neighbours.add(upperLeftPointNode);
		}

		return neighbours;
	}

	/**
	 * @param point The point to cross in the map.
	 * @param gameMap The map of the game.
	 * @return True if we can walk over the point, false otherwise.
	 */
	public boolean isCrossable(Point2D point, int[][] gameMap) {
		int x = (int)(point.getX() / Settings.cellSize);
		int y = (int)((point.getY() - Settings.statusBarHeight) / Settings.cellSize);

		return gameMap[x][y] == 0;
	}

	/**
	 * @param node The node to be checked.
	 * @return True if the node passed as parameter is around the current node, false otherwise.
	 */
	public boolean isAroundNode(Node node) {
		double dx = Math.sqrt((position.getX() - node.getPosition().getX()) * (position.getX() - node.getPosition().getX()));
		double dy = Math.sqrt((position.getY() - node.getPosition().getY()) * (position.getY() - node.getPosition().getY()));

		final int distance = (Settings.castleSize + Settings.knightSize) / 2;
		return (dx <= distance && dy <= distance);
	}

	/**
	 * @return The total cost of the node.
	 */
	public double getTotalCost() {
		return totalCost;
	}

	/**
	 * @param totalCost The total cost of the node.
	 */
	public void setTotalCost(double totalCost) {
		this.totalCost = totalCost;
	}

	/**
	 * @return The father of the Node.
	 */
	public Node getFatherNode() {
		return fatherNode;
	}

	/**
	 * @param fatherNode The father of the node.
	 */
	public void setFatherNode(Node fatherNode) {
		this.fatherNode = fatherNode;
	}

	/**
	 * @return The position in the window.
	 */
	public Point2D getPosition() {
		return position;
	}

	/**
	 * @return The cost of the node.
	 */
	public double getCost() {
		return cost;
	}

	/**
	 * @param cost The cost of the node.
	 */
	public void setCost(double cost) {
		this.cost = cost;
	}

	/**
	 * @return The heuristic cost of the node.
	 */
	public double getHeuristicCost() {
		return heuristicCost;
	}

	/**
	 * @param heuristicCost The heuristic cost of the node.
	 */
	public void setHeuristicCost(double heuristicCost) {
		this.heuristicCost = heuristicCost;
	}

	/**
	 * @return The string to be displayed when printing a node.
	 */
	@Override
	public String toString() {
		return "Node [x=" + position.getX() + ", y=" + position.getY() + ", cost=" + cost + ", Heuristic cost=" + heuristicCost + "]";
	}

	/**
	 * Compares two nodes.
	 * @param obj The other node.
	 * @return True if the two nodes are equal, false otherwise.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (obj == null)
			return false;

		if (getClass() != obj.getClass())
			return false;

		Node other = (Node)obj;
		if (fatherNode == null) {
			if (other.fatherNode != null)
				return false;
		} else if (!(fatherNode == other.fatherNode))
			return false;

		if (Double.doubleToLongBits(position.getX()) != Double.doubleToLongBits(other.getPosition().getX()))
			return false;

		return Double.doubleToLongBits(position.getY()) == Double.doubleToLongBits(other.getPosition().getY());
	}
}
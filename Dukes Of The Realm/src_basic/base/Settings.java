package base;

public class Settings {
	static final public int statusBarHeight = 100;

	static final public int nbMaxActiveDukes = 5;

	static final public int windowWidth = 1200;
	static final public int windowHeight = 800;

	static final public int gridCellsCountX = windowWidth;
	static final public int gridCellsCountY = (windowHeight - statusBarHeight);
	
	static final public int cellSize = 10;

	static final public int cellSizeYpx = windowHeight / gridCellsCountX;
	static final public int cellSizeXpx = windowWidth / gridCellsCountY;

	static final public int castleSize = 50; // In pixels
	static final public int knightSize = 15; // In pixels

	static final public int minimumCastleDistance = 200;

	static public final int nbMaxCastles = 10;
	static public final int nbMinCastles = 8;
	
	static public final int nbDiffTroopTypes = 1;
	static public final int minNbTroopsAddedPerTurn = 1;
	static public final int maxNbTroopsAddedPerTurn = 3;
	
	static public final int minNbInitTroops = 4;
	static public final int maxNbInitTroops = 8;
}
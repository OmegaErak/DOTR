package base;

public class Settings {
	static final public int statusBarHeight = 100;

	static final public int nbMaxActiveDukes = 5;
	static final public int initialTreasure = 0;
	
	static final public int windowWidth = 1200;
	static final public int windowHeight = 800;

	static final public int gridCellsCountX = windowWidth;
	static final public int gridCellsCountY = (windowHeight - statusBarHeight);
	
	static final public int cellSize = 10;

	static final public int cellSizeYpx = windowHeight / gridCellsCountX;
	static final public int cellSizeXpx = windowWidth / gridCellsCountY;

	static final public int castleSize = 50; // In grid cells
	static final public int knightSize = 15; // In grid cells

	static final public int minimumCastleDistance = 200;

	static public final int nbMaxCastles = 10;
	static public final int nbMinCastles = 8;
	
	static public final int surround = 24;
	
	static public final int ostSize = 3;
	
	// Knight, onager and pikeman
	static public final int nbTroopTypes = 3;
	
	static public final int nbMinInitTroops = 4;
	static public final int nbMaxInitTroops = 8;
}
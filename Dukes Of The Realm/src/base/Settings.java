package base;

public class Settings {
	static final public int statusBarHeight = 100;

	static final public int nbMaxPlayers = 6;
	static final public int initialTreasure = 0;
	
	static final public int windowWidth = 1200;
	static final public int windowHeight = 800;

	static final public int gridCellsCountX = windowWidth;
	static final public int gridCellsCountY = (windowHeight - statusBarHeight);

	static final public int cellSizeYpx = windowHeight / gridCellsCountX;
	static final public int cellSizeXpx = windowWidth / gridCellsCountY;

	static final public int castleSize = 60; // In grid cells

	static final public int minimumCastleDistance = 200;

	static public final int nbMaxCastles = 10;
	static public final int nbMinCastles = 2;
}
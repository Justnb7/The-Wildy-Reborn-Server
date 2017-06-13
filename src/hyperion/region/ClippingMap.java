package hyperion.region;

public class ClippingMap {

	public static final int MAP_SIZE = 32;

	private int[][][] flags = new int[4][MAP_SIZE][MAP_SIZE];

	private RegionCoordinates coords;

	public ClippingMap(RegionCoordinates coords) {
		this.coords = coords;
	}

	public int getFlag(int x, int y, int z) {
		return flags[z][x][y];
	}
	
	public static final int REGION_SIZE = 128;

	public static final int MAX_MAP_X = 16383, MAX_MAP_Y = 16383;

}

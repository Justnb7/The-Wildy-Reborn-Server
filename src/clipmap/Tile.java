package clipmap;

import com.model.game.location.Location;

/**
 * Tile is the class used by Hyperion, but to save the pain of renaming 260+ uses I've just put all instanciated methods into Location,
 * and made Tile extend it!
 *
 * static create() had to exist with a cast to return the expected type for hyperion implementations.
 */
public class Tile extends Location {

	public static Tile create(int x, int y, int z) {
		return new Tile(x, y, z);
	}

	public Tile(int x, int y, int z) {
		super(x, y, z);
	}

	public static Tile create(Location location) {
		return create(location.getX(), location.getY(), location.getZ());
	}
}
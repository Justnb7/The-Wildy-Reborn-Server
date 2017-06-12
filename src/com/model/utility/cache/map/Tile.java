package com.model.utility.cache.map;

import com.model.game.location.Location;

/**
 * Tile is the class used by Hyperion, but to save the pain of renaming 260+ uses I've just put all instanciated methods into Location,
 * and made Tile extend it!
 *
 * static create() had to exist with a cast to return the expected type for hyperion implementations.
 */
public class Tile extends Location {

	public static Tile create(int x, int y, int z) {
		return (Tile)Location.create(x, y, z);
	}
}
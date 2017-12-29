package com.venenatis.game.content.minigames.singleplayer.barrows;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

import com.venenatis.game.location.Location;
import com.venenatis.game.model.boudary.Boundary;
import com.venenatis.game.util.Utility;

/**
 * Hold information about the Barrows minigame.
 * 
 * @author Lennard
 * @author Stan
 */
public enum BarrowsInformation {
	
	DHAROK(0, 1673, Boundary.create(new Location(3572, 3295), new Location(3578, 3300)), new Location(3556, 9718, 3), 20720, 4763),
	GUTHAN(1, 1674, Boundary.create(new Location(3574, 3279), new Location(3580, 3284)), new Location(3534, 9704, 3), 20722, 4765),
	VERAC(2, 1677, Boundary.create(new Location(3553, 3294), new Location(3560, 3300)), new Location(3578, 9706, 3), 20772, 4771),
	TORAG(3, 1676, Boundary.create(new Location(3550, 3280), new Location(3557, 3286)), new Location(3568, 9683, 3), 20721, 4769),
	AHRIM(4, 1672, Boundary.create(new Location(3562, 3285), new Location(3568, 3291)), new Location(3557, 9703, 3), 20770, 4761),
	KARIL(5, 1675, Boundary.create(new Location(3562, 3273), new Location(3568, 3278)), new Location(3546, 9684, 3), 20771, 4767);
	
	/**
	 * The identifier of the barrows brother.
	 */
	private final int identifier;
	
	/**
	 * The npcId of the barrows brother that will be spawned.
	 */
	private final int npcId;
	
	/**
	 * {@link Boundary} of the hill-area that determines what crypt will be broken into.
	 */
	private final Boundary hillBoundary;
	
	/**
	 * {@link Location} that the player will be moved to after digging.
	 */
	private final Location teleportLocation;

	/**
	 * The crypts objectId.
	 */
	private final int objectId;
	
	private final int purpleHead;
	
	private BarrowsInformation(final int identifier, int npcId, Boundary hillBoundary, Location teleportLocation, int objectId, int purpleHead) {
		this.identifier = identifier;
		this.npcId = npcId;
		this.hillBoundary = hillBoundary;
		this.teleportLocation = teleportLocation;
		this.objectId = objectId;
		this.purpleHead = purpleHead;
	}

	public static final Set<BarrowsInformation> VALUES = Collections.unmodifiableSet(EnumSet.allOf(BarrowsInformation.class));
	
	/**
	 * Checks if the specified {@link Location} is within one of the Crypts
	 * boundaries.
	 * 
	 * @param location
	 *            The Location that is being checked.
	 * @return Optional Location if the Location matches on of the Boundaries.
	 */
	public static Optional<Location> getCryptLocation(final Location location) {
		for (BarrowsInformation info : VALUES) {
			if (info == null) {
				continue;
			}
			if (info.getHillBoundary().isIn(location)) {
				return Optional.of(info.getTeleportLocation());
			}
		}
		return Optional.empty();
	}
	
	/**
	 * Checks if the specified objectId matches with one of the Barrows
	 * objectIds.
	 * 
	 * @param objectId
	 *            The objectId that is being checked.
	 * @return Optional BarrowsInformation if the objectId matches.
	 */
	public static Optional<BarrowsInformation> forObjectId(final int objectId) {
		for (BarrowsInformation info : VALUES) {
			if (info == null) {
				continue;
			}
			if (info.getObjectId() == objectId) {
				return Optional.of(info);
			}
		}
		return Optional.empty();
	}
	
	/**
	 * Checks if the specified identifier matches with one of the Barrows
	 * identifiers.
	 * 
	 * @param identifier
	 *            The identifier that is being checked.
	 * @return Optional BarrowsInformation if the identifier matches.
	 */
	public static Optional<BarrowsInformation> forBrotherIdentifier(final int identifier) {
		for (BarrowsInformation info : VALUES) {
			if (info == null) {
				continue;
			}
			if (info.getIdentifier() == identifier) {
				return Optional.of(info);
			}
		}
		return Optional.empty();
	}
	
	public int getNpcId() {
		return npcId;
	}

	public Boundary getHillBoundary() {
		return hillBoundary;
	}

	public Location getTeleportLocation() {
		return teleportLocation;
	}

	public int getObjectId() {
		return objectId;
	}

	public int getIdentifier() {
		return identifier;
	}
	
	@Override
	public String toString() {
		return Utility.optimizeText(name().toLowerCase());
	}

	public int getPurpleHead() {
		return purpleHead;
	}
	
}
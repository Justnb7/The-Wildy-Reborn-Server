package com.venenatis.game.world.pathfinder.impl;

import com.venenatis.game.cache.definitions.AnyRevObjectDefinition;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.world.object.GameObject;
import com.venenatis.game.world.pathfinder.PathFinder;
import com.venenatis.game.world.pathfinder.PathState;

public class ObjectPathFinder {

	public static PathState find(Player player, GameObject object) {
		int walkToData = 0;
		int type = -2;
		int direction;
		int sizeX;
		int sizeY;
		if ((object.getType() == 10 || object.getType() == 11 || object.getType() == 22)) {
			type = -1;
			int rotation = object.getFace();
			AnyRevObjectDefinition def = object.getDefinition();
			if (rotation == 1 || rotation == 3) {
				sizeX = object.getDefinition().xLength();
				sizeY = object.getDefinition().yLength();
			} else {
				sizeX = object.getDefinition().xLength();
				sizeY = object.getDefinition().yLength();
			}
			walkToData = object.getDefinition().getWalkToFlag();
			if (object.getFace() != 0)
				walkToData = (walkToData << rotation & 0xf)
						+ (walkToData >> 4 - rotation);
			direction = 0;
		} else {
			sizeX = sizeY = 1;
			type = object.getType();
			direction = object.getFace();
		}
		final int finalX = object.getLocation().getX();
		final int finalY = object.getLocation().getY();
		return PathFinder.doPath(
				new VariablePathFinder(type, walkToData, direction, sizeX,
						sizeY), player, finalX, finalY);
	}

}

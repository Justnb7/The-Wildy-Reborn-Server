package com.model.game.character.pathfinder.impl;

import com.model.game.character.pathfinder.PathFinder;
import com.model.game.character.pathfinder.PathState;
import com.model.game.character.player.Player;
import com.model.game.object.GameObject;
import com.model.utility.cache.ObjectDefinition;

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
			ObjectDefinition def = object.getDefinition();
			if (rotation == 1 || rotation == 3) {
				sizeX = object.cacheDef().sizeX;
				sizeY = object.cacheDef().sizeY;
			} else {
				sizeX = object.cacheDef().sizeX;
				sizeY = object.cacheDef().sizeY;
			}
			walkToData = object.cacheDef().walkToFlag;
			if (object.getFace() != 0)
				walkToData = (walkToData << rotation & 0xf)
						+ (walkToData >> 4 - rotation);
			direction = 0;
		} else {
			sizeX = sizeY = 1;
			type = object.getType();
			direction = object.getFace();
		}
		final int finalX = object.getPosition().getX();
		final int finalY = object.getPosition().getY();
		return PathFinder.doPath(
				new VariablePathFinder(type, walkToData, direction, sizeX,
						sizeY), player, finalX, finalY);
	}

}

package com.model.game.character.pathfinder.impl;

import com.model.game.character.pathfinder.PathState;
import com.oldschool.game.model.definitions.ObjectDefinition;
import com.oldschool.game.model.entity.player.Player;
import com.oldschool.game.world.object.GameObject;

public class ObjectPathFinder {
	
	/**
	 * Finds the path to the object
	 * @param player
	 * @param object
	 * @return
	 */
	public static PathState find(Player player, GameObject object) {
		int walkToData = 0;
		int type = -2;
		int direction;
		int sizeX;
		int sizeY;
		if ((object.getType() == 10 || object.getType() == 11 || object.getType() == 22)) {
			type = -1;
			int rotation = object.getDirection();
			ObjectDefinition def = object.getDefinition();
			if (rotation == 1 || rotation == 3) {
				sizeX = object.getWidth();
				sizeY = object.getHeight();
			} else {
				sizeX = object.getWidth();
				sizeY = object.getHeight();
			}
			walkToData = def.getSurroundings();
			if (object.getDirection() != 0)
				walkToData = (walkToData << rotation & 0xf)
						+ (walkToData >> 4 - rotation);
			direction = 0;
		} else {
			sizeX = sizeY = 1;
			type = object.getType();
			direction = object.getDirection();
		}
		final int finalX = object.getPosition().getX();
		final int finalY = object.getPosition().getY();
		return player.doPath(
				new VariablePathFinder(type, walkToData, direction, sizeX,
						sizeY), player, finalX, finalY);
	}

}

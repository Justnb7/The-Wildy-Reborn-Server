package com.model.game.character.npc.drops;

import java.util.Comparator;

public class DropComparator implements Comparator<Drop> {

	@Override
	public int compare(Drop dropA, Drop dropB) {
		if (dropA.getRarity() < dropB.getRarity()) {
			return 1;
		} else if (dropA.getRarity() > dropB.getRarity()) {
			return -1;
		}
		return Math.random() < 0.5 ? 1 : -1;
	}

}

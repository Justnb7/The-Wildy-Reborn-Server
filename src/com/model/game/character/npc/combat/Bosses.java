package com.model.game.character.npc.combat;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.model.game.character.npc.combat.combat_scripts.KetZek;


/**
 * 
 * @author Patrick van Elderen
 * @date Feb, 23-2-2016
 */

public class Bosses {
	
	private static Map<Integer, Boss> bosses = new HashMap<>();
	
	
	private static final KetZek KET_ZEK = new KetZek(3125);
	
	private static final int[] DRAGONS = {247, 252, 264, 268, 270, 273, 274};
	
	static {
		
		bosses.put(KET_ZEK.npcId, KET_ZEK);

	}
	
	public static Boss get(int npcId) {
		if (!bosses.containsKey(npcId))
			return null;
		return bosses.get(npcId);
	}
	
	public static boolean isBoss(int npcId) {
		return Objects.nonNull(get(npcId));
	}
}

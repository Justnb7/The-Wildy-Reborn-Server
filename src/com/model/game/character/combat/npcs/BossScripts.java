package com.model.game.character.combat.npcs;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.model.game.character.combat.npcs.script.AhrimTheBlighted;
import com.model.game.character.combat.npcs.script.KetZek;
import com.model.game.character.combat.npcs.script.TokXil;
import com.model.game.character.combat.npcs.script.TzTokJad;


/**
 * All the bosses that have a custom script
 * @author Patrick van Elderen
 * @date Feb, 23-2-2016
 */
public class BossScripts {
	
	private static Map<Integer, AbstractBossCombat> bosses = new HashMap<>();
	
	
	private static final KetZek KET_ZEK = new KetZek(3125);
	private static final TokXil TOK_XIL = new TokXil(2193);
	private static final TzTokJad JAD = new TzTokJad(3127);
	
	private static final AhrimTheBlighted AHRIM_THE_BLIGHTED = new AhrimTheBlighted(1672);
	
	private static final int[] DRAGONS = {247, 252, 264, 268, 270, 273, 274};
	
	static {
		
		bosses.put(KET_ZEK.npcId, KET_ZEK);
		bosses.put(JAD.npcId, JAD);
		bosses.put(TOK_XIL.npcId, TOK_XIL);
		
		bosses.put(AHRIM_THE_BLIGHTED.npcId, AHRIM_THE_BLIGHTED);

	}
	
	public static AbstractBossCombat get(int npcId) {
		if (!bosses.containsKey(npcId))
			return null;
		return bosses.get(npcId);
	}
	
	public static boolean isBoss(int npcId) {
		return Objects.nonNull(get(npcId));
	}
}

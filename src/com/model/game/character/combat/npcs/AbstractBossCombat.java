package com.model.game.character.combat.npcs;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.model.game.character.Entity;
import com.model.game.character.combat.npcs.script.Cave_Kraken;
import com.model.game.character.combat.npcs.script.Kraken;
import com.model.game.character.combat.npcs.script.SkeletalWyvern;
import com.model.game.character.combat.npcs.script.barrows.AhrimTheBlighted;
import com.model.game.character.combat.npcs.script.dagannoths.DagannothPrime;
import com.model.game.character.combat.npcs.script.dagannoths.DagannothRex;
import com.model.game.character.combat.npcs.script.dagannoths.DagannothSupreme;
import com.model.game.character.combat.npcs.script.fight_cave.KetZek;
import com.model.game.character.combat.npcs.script.fight_cave.TokXil;
import com.model.game.character.combat.npcs.script.fight_cave.TzTokJad;
import com.model.game.character.combat.npcs.script.godwars.bandos.GeneralGraardor;
import com.model.game.character.combat.npcs.script.godwars.zamorak.KrilTsutsaroth;
import com.model.game.character.combat.npcs.script.godwars.zamorak.ZaklnGritch;
import com.model.game.character.npc.NPC;
import com.model.game.character.player.Player;

/**
 * An abstract bossing system.
 * @author Patrick van Elderen
 * @version 1.3
 * @date Feb, 23-2-2016
 * @edited 19-3-2017
 */
public abstract class AbstractBossCombat {
	
    private static Map<Integer, AbstractBossCombat> bosses = new HashMap<>();
	
	static {
		
		bosses.put(3125, new KetZek());
		bosses.put(3127, new TzTokJad());
		bosses.put(2193, new TokXil());
		
		bosses.put(1672, new AhrimTheBlighted());
		bosses.put(2215, new GeneralGraardor());
		
		bosses.put(465, new SkeletalWyvern());
		
		bosses.put(492, new Cave_Kraken());
		bosses.put(494, new Kraken());
		
		bosses.put(2267, new DagannothRex());
		bosses.put(2266, new DagannothPrime());
		bosses.put(2265, new DagannothSupreme());
		
		bosses.put(3129, new KrilTsutsaroth());
		bosses.put(3131, new ZaklnGritch());

	}
	
	public static AbstractBossCombat get(int npcId) {
		if (!bosses.containsKey(npcId))
			return null;
		return bosses.get(npcId);
	}
	
	public static boolean isBoss(int npcId) {
		return Objects.nonNull(get(npcId));
	}
	
	public abstract void execute(Entity attacker, Entity victim);
	
	public abstract int distance(Entity attacker);
	
	public abstract void dropLoot(Player player, NPC npc);
	
}

package com.venenatis.game.model.combat.npcs;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.venenatis.game.model.combat.npcs.impl.Cave_Kraken;
import com.venenatis.game.model.combat.npcs.impl.Kraken;
import com.venenatis.game.model.combat.npcs.impl.SkeletalWyvern;
import com.venenatis.game.model.combat.npcs.impl.barrows.AhrimTheBlighted;
import com.venenatis.game.model.combat.npcs.impl.dagannoths.DagannothPrime;
import com.venenatis.game.model.combat.npcs.impl.dagannoths.DagannothRex;
import com.venenatis.game.model.combat.npcs.impl.dagannoths.DagannothSupreme;
import com.venenatis.game.model.combat.npcs.impl.fight_caves.KetZek;
import com.venenatis.game.model.combat.npcs.impl.fight_caves.TokXil;
import com.venenatis.game.model.combat.npcs.impl.fight_caves.TzTokJad;
import com.venenatis.game.model.combat.npcs.impl.godwars.bandos.GeneralGraardor;
import com.venenatis.game.model.combat.npcs.impl.godwars.zamorak.KrilTsutsaroth;
import com.venenatis.game.model.combat.npcs.impl.godwars.zamorak.ZaklnGritch;
import com.venenatis.game.model.combat.npcs.impl.wilderness.ChaosFanatic;
import com.venenatis.game.model.combat.npcs.impl.wilderness.Chaos_Elemental;
import com.venenatis.game.model.combat.npcs.impl.wilderness.King_Black_Dragon;
import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.entity.npc.NPC;
import com.venenatis.game.model.entity.player.Player;

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
		
		bosses.put(2054, new Chaos_Elemental());
		bosses.put(239,  new King_Black_Dragon());
		bosses.put(6619,  new ChaosFanatic());

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

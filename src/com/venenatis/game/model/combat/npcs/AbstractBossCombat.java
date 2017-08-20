package com.venenatis.game.model.combat.npcs;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.venenatis.game.model.combat.npcs.impl.*;
import com.venenatis.game.model.combat.npcs.impl.barrows.*;
import com.venenatis.game.model.combat.npcs.impl.dagannoths.*;
import com.venenatis.game.model.combat.npcs.impl.fight_caves.*;
import com.venenatis.game.model.combat.npcs.impl.godwars.armadyl.*;
import com.venenatis.game.model.combat.npcs.impl.godwars.bandos.*;
import com.venenatis.game.model.combat.npcs.impl.godwars.zamorak.*;
import com.venenatis.game.model.combat.npcs.impl.slayer.DarkBeast;
import com.venenatis.game.model.combat.npcs.impl.slayer.Kraken;
import com.venenatis.game.model.combat.npcs.impl.slayer.SkeletalWyvern;
import com.venenatis.game.model.combat.npcs.impl.wilderness.*;
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
		
		bosses.put(492, new CaveKraken());
		bosses.put(494, new Kraken());
		bosses.put(4005, new DarkBeast());
		
		bosses.put(2267, new DagannothRex());
		bosses.put(2266, new DagannothPrime());
		bosses.put(2265, new DagannothSupreme());
		
		bosses.put(3129, new KrilTsutsaroth());
		bosses.put(3131, new ZaklnGritch());
		
		bosses.put(3162, new Kreearra());
		bosses.put(3163, new WingmanSkree());
		bosses.put(3164, new FlockleaderGeerin());
		
		bosses.put(2054, new Chaos_Elemental());
		bosses.put(239,  new KingBlackDragon());
		bosses.put(6619, new ChaosFanatic());
		bosses.put(3359, new ZombiesChampion());
		
		bosses.put(5779, new GiantMole());
		AbstractBossCombat mo = new DagannothMother();
		bosses.put(6361, mo);
		bosses.put(6362, mo);
		bosses.put(6365, mo);
		
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

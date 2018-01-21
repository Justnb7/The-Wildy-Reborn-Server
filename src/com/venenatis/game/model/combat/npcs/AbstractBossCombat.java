package com.venenatis.game.model.combat.npcs;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.venenatis.game.model.combat.CombatFormulae;
import com.venenatis.game.model.combat.npcs.impl.*;
import com.venenatis.game.model.combat.npcs.impl.barrows.*;
import com.venenatis.game.model.combat.npcs.impl.dagannoths.*;
import com.venenatis.game.model.combat.npcs.impl.dragons.*;
import com.venenatis.game.model.combat.npcs.impl.fight_caves.*;
import com.venenatis.game.model.combat.npcs.impl.godwars.armadyl.*;
import com.venenatis.game.model.combat.npcs.impl.godwars.bandos.*;
import com.venenatis.game.model.combat.npcs.impl.godwars.saradomin.*;
import com.venenatis.game.model.combat.npcs.impl.godwars.zamorak.*;
import com.venenatis.game.model.combat.npcs.impl.slayer.*;
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
		bosses.put(1673, new DharokTheWretched());
		bosses.put(1674, new GuthanTheInfested());
		bosses.put(1675, new KarilTheTainted());
		bosses.put(1676, new ToragTheCorrupted());
		bosses.put(1677, new VeracTheDefiled());
		
		bosses.put(465, new SkeletalWyvern());
		
		bosses.put(492, new CaveKraken());
		bosses.put(494, new Kraken());
		bosses.put(5535, new EnormousTentacle());
		
		bosses.put(2267, new DagannothRex());
		bosses.put(2266, new DagannothPrime());
		bosses.put(2265, new DagannothSupreme());
		
		bosses.put(2215, new GeneralGraardor());
		bosses.put(2217, new SergeantSteelwill());
		bosses.put(2218, new SergeantGrimspike());
		
		bosses.put(2205, new CommanderZilyana());
		bosses.put(2207, new Growler());
		bosses.put(2208, new Bree());
		
		bosses.put(3129, new KrilTsutsaroth());
		bosses.put(3131, new ZaklnGritch());
		bosses.put(3132, new BalfrugKreeyath());
		
		bosses.put(3162, new Kreearra());
		bosses.put(3163, new WingmanSkree());
		bosses.put(3164, new FlockleaderGeerin());
		
		bosses.put(2054, new Chaos_Elemental());
		bosses.put(239,  new KingBlackDragon());
		bosses.put(6619, new ChaosFanatic());
		bosses.put(3359, new ZombiesChampion());
		bosses.put(6615, new Scorpia());
		bosses.put(6618, new CrazyArchaeologist());
		
		bosses.put(5779, new GiantMole());
		AbstractBossCombat mo = new DagannothMother();
		bosses.put(6361, mo);
		bosses.put(6362, mo);
		bosses.put(6365, mo);
		
		AbstractBossCombat bronze_dragon = new BronzeDragon();
		bosses.put(7253, bronze_dragon);
		bosses.put(270, bronze_dragon);
		bosses.put(271, bronze_dragon);
		
		AbstractBossCombat iron_dragon = new IronDragon();
		bosses.put(7254, iron_dragon);
		bosses.put(272, iron_dragon);
		bosses.put(273, iron_dragon);
		
		AbstractBossCombat steel_dragon = new SteelDragon();
		bosses.put(7255, steel_dragon);
		bosses.put(274, steel_dragon);
		bosses.put(275, steel_dragon);
		
		AbstractBossCombat green_dragon = new GreenDragon();
		bosses.put(260, green_dragon);
		bosses.put(261, green_dragon);
		bosses.put(262, green_dragon);
		bosses.put(263, green_dragon);
		bosses.put(264, green_dragon);
		
		AbstractBossCombat blue_dragon = new BlueDragon();
		bosses.put(265, blue_dragon);
		bosses.put(266, blue_dragon);
		bosses.put(267, blue_dragon);
		bosses.put(268, blue_dragon);
		bosses.put(269, blue_dragon);
		
		AbstractBossCombat black_dragon = new BlackDragon();
		bosses.put(252, black_dragon);
		bosses.put(253, black_dragon);
		bosses.put(254, black_dragon);
		bosses.put(255, black_dragon);
		bosses.put(256, black_dragon);
		bosses.put(257, black_dragon);
		bosses.put(258, black_dragon);
		bosses.put(259, black_dragon);
		
		AbstractBossCombat lava_dragon = new LavaDragon();
		bosses.put(6593, lava_dragon);
		
		AbstractBossCombat skeletal_wyvern = new SkeletalWyvern();
		bosses.put(465, skeletal_wyvern);
		bosses.put(466, skeletal_wyvern);
		bosses.put(467, skeletal_wyvern);
		bosses.put(468, skeletal_wyvern);
		
		
		AbstractBossCombat dark_beast = new DarkBeast();
		bosses.put(4005, dark_beast);
		bosses.put(7250, dark_beast);
		
		AbstractBossCombat giant_rock_crab = new Large_Rock_Crab();
		bosses.put(2261, giant_rock_crab);
		
		AbstractBossCombat dagg = new Melee_Dagganoth();
		bosses.put(5942, dagg);
		
		AbstractBossCombat dagg2 = new Ranged_Dagganoth();
		bosses.put(5943, dagg2);
		
		AbstractBossCombat spin = new Spinolyp();
		bosses.put(5947, spin);
		
		AbstractBossCombat wallaski = new Wallaski();
		bosses.put(5938, wallaski);
		bosses.put(5939, wallaski);
		
		bosses.put(319, new CorporealBeast());
		
		AbstractBossCombat venenatis = new Venenatis();
		bosses.put(6504, venenatis);
		bosses.put(6610, venenatis);
		bosses.put(8017, venenatis);
		
		
		AbstractBossCombat lizardman_shaman = new LizardmanShaman();
		bosses.put(6766, lizardman_shaman);
		bosses.put(6767, lizardman_shaman);
		bosses.put(7573, lizardman_shaman);
		bosses.put(7574, lizardman_shaman);
		bosses.put(7744, lizardman_shaman);
		bosses.put(7745, lizardman_shaman);
		
		AbstractBossCombat barrows_crypt_npc = new BarrowsCryptNPC();
		bosses.put(1678, barrows_crypt_npc);
		bosses.put(1679, barrows_crypt_npc);
		bosses.put(1680, barrows_crypt_npc);
		bosses.put(1681, barrows_crypt_npc);
		bosses.put(1682, barrows_crypt_npc);
		bosses.put(1683, barrows_crypt_npc);
		bosses.put(1684, barrows_crypt_npc);
		bosses.put(1685, barrows_crypt_npc);
		bosses.put(1686, barrows_crypt_npc);
		bosses.put(1687, barrows_crypt_npc);
		bosses.put(1688, barrows_crypt_npc);
		
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

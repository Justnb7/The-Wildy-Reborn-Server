package com.model.game.character.npc.combat;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.model.game.character.npc.combat.combat_scripts.Ahrim_The_Blighted;
import com.model.game.character.npc.combat.combat_scripts.Balfrug_Kreeyath;
import com.model.game.character.npc.combat.combat_scripts.Barrelchest;
import com.model.game.character.npc.combat.combat_scripts.Bree;
import com.model.game.character.npc.combat.combat_scripts.Callisto;
import com.model.game.character.npc.combat.combat_scripts.Chaos_Elemental;
import com.model.game.character.npc.combat.combat_scripts.Chaos_Fanatic;
import com.model.game.character.npc.combat.combat_scripts.Commander_Zilyana;
import com.model.game.character.npc.combat.combat_scripts.Corporeal_Beast;
import com.model.game.character.npc.combat.combat_scripts.Crazy_Archaeologist;
import com.model.game.character.npc.combat.combat_scripts.Dagannoth_Prime;
import com.model.game.character.npc.combat.combat_scripts.Dagannoth_Supreme;
import com.model.game.character.npc.combat.combat_scripts.Dragon;
import com.model.game.character.npc.combat.combat_scripts.Enormous_Tentacle;
import com.model.game.character.npc.combat.combat_scripts.Flockleader_Geerin;
import com.model.game.character.npc.combat.combat_scripts.General_Graardor;
import com.model.game.character.npc.combat.combat_scripts.Growler;
import com.model.game.character.npc.combat.combat_scripts.King_Black_Dragon;
import com.model.game.character.npc.combat.combat_scripts.Kraken;
import com.model.game.character.npc.combat.combat_scripts.Kree_Arra;
import com.model.game.character.npc.combat.combat_scripts.Krill_Tsutsaroth;
import com.model.game.character.npc.combat.combat_scripts.Scorpia;
import com.model.game.character.npc.combat.combat_scripts.Scorpia_Guardian;
import com.model.game.character.npc.combat.combat_scripts.Sergeant_Grimspike;
import com.model.game.character.npc.combat.combat_scripts.Sergeant_Steelwill;
import com.model.game.character.npc.combat.combat_scripts.Venenatis;
import com.model.game.character.npc.combat.combat_scripts.Vetion;
import com.model.game.character.npc.combat.combat_scripts.Wingman_Skree;
import com.model.game.character.npc.combat.combat_scripts.Zakln_Gritch;
import com.model.game.character.npc.combat.combat_scripts.Zombie_Champion;


/**
 * 
 * @author Patrick van Elderen
 * @date Feb, 23-2-2016
 */

public class Bosses {
	
	private static Map<Integer, Boss> bosses = new HashMap<>();
	
	private static final Ahrim_The_Blighted AHRIM_THE_BLIGHTED = new Ahrim_The_Blighted(1672);
	private static final Chaos_Elemental CHAOS_ELEMENTAL = new Chaos_Elemental(2054);
	private static final Chaos_Fanatic CHAOS_FANATIC = new Chaos_Fanatic(6619);
	private static final Crazy_Archaeologist CRAZY_ARCHAEOLOGIST = new Crazy_Archaeologist(6618);
	private static final Corporeal_Beast CORPOREAL_BEAST = new Corporeal_Beast(319);
	private static final General_Graardor GENERAL_GRAARDOR = new General_Graardor(2215);
	private static final Sergeant_Grimspike GRIMSPIKE = new Sergeant_Grimspike(2218);
	private static final Sergeant_Steelwill STEELWILL = new Sergeant_Steelwill(2217);
	private static final Callisto CALLISTO = new Callisto(6609);
	private static final Vetion VETION = new Vetion(6611);
	private static final Dagannoth_Prime PRIME = new Dagannoth_Prime(2266);
	private static final Dagannoth_Supreme SUPREME = new Dagannoth_Supreme(2265);
	private static final Venenatis VENENATIS = new Venenatis(6610);
	private static final Scorpia SCORPIA = new Scorpia(6615);
	private static final Scorpia_Guardian SCORPIA_GUARDIAN = new Scorpia_Guardian(6617);
	private static final Kraken KRAKEN = new Kraken(494);
	private static final Enormous_Tentacle ENORMOUS_TENTACLE = new Enormous_Tentacle(5534);
	private static final Kree_Arra KREE_ARRA = new Kree_Arra(3162);
	private static final Wingman_Skree WINGMAN_SKREE = new Wingman_Skree(3163);
	private static final Flockleader_Geerin FLOCKLEADER_GEERIN = new Flockleader_Geerin(3164);
	private static final Balfrug_Kreeyath BALFRUG_KREEYATH = new Balfrug_Kreeyath(3132);
	private static final Zakln_Gritch ZAKLN_GRITCH = new Zakln_Gritch(3131);
	private static final Krill_Tsutsaroth KRILL_TSUTAROTH = new Krill_Tsutsaroth(3129);
	private static final Commander_Zilyana COMMANDER_ZILYANA = new Commander_Zilyana(2205);
	private static final Bree BREE = new Bree(2208);
	private static final Growler GROWLER = new Growler(2207);
	private static final King_Black_Dragon King_Black_Dragon = new King_Black_Dragon(239);
	private static final Barrelchest BARRELCHEST = new Barrelchest(6342);
	private static final Zombie_Champion ZOMBIES_CHAMPION = new Zombie_Champion(3359);
	
	private static final Dragon RED_DRAGON_247 = new Dragon(247);
	private static final Dragon RED_DRAGON_248 = new Dragon(248);
	private static final Dragon RED_DRAGON_249 = new Dragon(249);
	private static final Dragon RED_DRAGON_250 = new Dragon(250);
	private static final Dragon RED_DRAGON_251 = new Dragon(251);
	private static final Dragon BLACK_DRAGON_252 = new Dragon(252);
	private static final Dragon BLACK_DRAGON_253 = new Dragon(253);
	private static final Dragon BLACK_DRAGON_254 = new Dragon(254);
	private static final Dragon BLACK_DRAGON_255 = new Dragon(255);
	private static final Dragon BLACK_DRAGON_256 = new Dragon(256);
	private static final Dragon BLACK_DRAGON_257 = new Dragon(257);
	private static final Dragon BLACK_DRAGON_258 = new Dragon(258);
	private static final Dragon BLACK_DRAGON_259 = new Dragon(259);
	private static final Dragon GREEN_DRAGON_260 = new Dragon(260);
	private static final Dragon GREEN_DRAGON_261 = new Dragon(261);
	private static final Dragon GREEN_DRAGON_262 = new Dragon(262);
	private static final Dragon GREEN_DRAGON_263 = new Dragon(263);
	private static final Dragon GREEN_DRAGON_264 = new Dragon(264);
	private static final Dragon BLUE_DRAGON_265 = new Dragon(265);
	private static final Dragon BLUE_DRAGON_266 = new Dragon(266);
	private static final Dragon BLUE_DRAGON_267 = new Dragon(267);
	private static final Dragon BLUE_DRAGON_268 = new Dragon(268);
	private static final Dragon BLUE_DRAGON_269 = new Dragon(269);
	private static final Dragon BRONZE_DRAGON_270 = new Dragon(270);
	private static final Dragon BRONZE_DRAGON_271 = new Dragon(271);
	private static final Dragon IRON_DRAGON_272 = new Dragon(272);
	private static final Dragon IRON_DRAGON_273 = new Dragon(273);
	private static final Dragon STEEL_DRAGON_274 = new Dragon(274);
	private static final Dragon STEEL_DRAGON_275 = new Dragon(275);
	
	
	static {
		bosses.put(AHRIM_THE_BLIGHTED.npcId, AHRIM_THE_BLIGHTED);
		bosses.put(CHAOS_ELEMENTAL.npcId, CHAOS_ELEMENTAL);
		bosses.put(CHAOS_FANATIC.npcId, CHAOS_FANATIC);
		bosses.put(CRAZY_ARCHAEOLOGIST.npcId, CRAZY_ARCHAEOLOGIST);
		bosses.put(CORPOREAL_BEAST.npcId, CORPOREAL_BEAST);
		bosses.put(GENERAL_GRAARDOR.npcId, GENERAL_GRAARDOR);
		bosses.put(GRIMSPIKE.npcId, GRIMSPIKE);
		bosses.put(STEELWILL.npcId, STEELWILL);
		bosses.put(CALLISTO.npcId, CALLISTO);
		bosses.put(VETION.npcId, VETION);
		bosses.put(PRIME.npcId, PRIME);
		bosses.put(SUPREME.npcId, SUPREME);
		bosses.put(VENENATIS.npcId, VENENATIS);
		bosses.put(SCORPIA.npcId, SCORPIA);
		bosses.put(SCORPIA_GUARDIAN.npcId, SCORPIA_GUARDIAN);
		bosses.put(KREE_ARRA.npcId, KREE_ARRA);
		bosses.put(KRAKEN.npcId, KRAKEN);
		bosses.put(ENORMOUS_TENTACLE.npcId, ENORMOUS_TENTACLE);
		bosses.put(WINGMAN_SKREE.npcId, WINGMAN_SKREE);
		bosses.put(FLOCKLEADER_GEERIN.npcId, FLOCKLEADER_GEERIN);
		bosses.put(BALFRUG_KREEYATH.npcId, BALFRUG_KREEYATH);
		bosses.put(ZAKLN_GRITCH.npcId, ZAKLN_GRITCH);
		bosses.put(KRILL_TSUTAROTH.npcId, KRILL_TSUTAROTH);
		bosses.put(COMMANDER_ZILYANA.npcId, COMMANDER_ZILYANA);
		bosses.put(BREE.npcId, BREE);
		bosses.put(GROWLER.npcId, GROWLER);
		bosses.put(King_Black_Dragon.npcId, King_Black_Dragon);
		bosses.put(BARRELCHEST.npcId, BARRELCHEST);
		bosses.put(ZOMBIES_CHAMPION.npcId, ZOMBIES_CHAMPION);
		/**
		 * Dragons
		 */
		bosses.put(RED_DRAGON_247.npcId, RED_DRAGON_247);
		bosses.put(RED_DRAGON_248.npcId, RED_DRAGON_248);
		bosses.put(RED_DRAGON_249.npcId, RED_DRAGON_249);
		bosses.put(RED_DRAGON_250.npcId, RED_DRAGON_250);
		bosses.put(RED_DRAGON_251.npcId, RED_DRAGON_251);
		bosses.put(BLACK_DRAGON_252.npcId, BLACK_DRAGON_252);
		bosses.put(BLACK_DRAGON_253.npcId, BLACK_DRAGON_253);
		bosses.put(BLACK_DRAGON_254.npcId, BLACK_DRAGON_254);
		bosses.put(BLACK_DRAGON_255.npcId, BLACK_DRAGON_255);
		bosses.put(BLACK_DRAGON_256.npcId, BLACK_DRAGON_256);
		bosses.put(BLACK_DRAGON_257.npcId, BLACK_DRAGON_257);
		bosses.put(BLACK_DRAGON_258.npcId, BLACK_DRAGON_257);
		bosses.put(BLACK_DRAGON_259.npcId, BLACK_DRAGON_259);
		bosses.put(GREEN_DRAGON_260.npcId, GREEN_DRAGON_260);
		bosses.put(GREEN_DRAGON_261.npcId, GREEN_DRAGON_261);
		bosses.put(GREEN_DRAGON_262.npcId, GREEN_DRAGON_262);
		bosses.put(GREEN_DRAGON_263.npcId, GREEN_DRAGON_263);
		bosses.put(GREEN_DRAGON_264.npcId, GREEN_DRAGON_264);
		bosses.put(BLUE_DRAGON_265.npcId, BLUE_DRAGON_265);
		bosses.put(BLUE_DRAGON_266.npcId, BLUE_DRAGON_266);
		bosses.put(BLUE_DRAGON_267.npcId, BLUE_DRAGON_267);
		bosses.put(BLUE_DRAGON_268.npcId, BLUE_DRAGON_268);
		bosses.put(BLUE_DRAGON_269.npcId, BLUE_DRAGON_269);
		bosses.put(BRONZE_DRAGON_270.npcId, BRONZE_DRAGON_270);
		bosses.put(BRONZE_DRAGON_271.npcId, BRONZE_DRAGON_271);
		bosses.put(IRON_DRAGON_272.npcId, IRON_DRAGON_272);
		bosses.put(IRON_DRAGON_273.npcId, IRON_DRAGON_273);
		bosses.put(STEEL_DRAGON_274.npcId, STEEL_DRAGON_274);
		bosses.put(STEEL_DRAGON_275.npcId, STEEL_DRAGON_275);
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

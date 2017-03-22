package com.model.game.character.player.content.cluescrolls;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Iterables;
import com.model.game.character.npc.NPC;
import com.model.game.character.player.Player;
import com.model.game.character.player.PlayerUpdating;
import com.model.game.item.IntervalItem;
import com.model.game.item.Item;
import com.model.game.item.ground.GroundItem;
import com.model.game.item.ground.GroundItemHandler;
import com.model.utility.Chance;
import com.model.utility.Utility;
import com.model.utility.json.definitions.ItemDefinition;

/**
 * @author lare96 <http://github.com/lare96>
 */
public final class ClueScrollHandler {

	public static final int DIG_RADIUS = 2;

	public static final int[] ELITE_CLUE_DROPS = { 2265, 2266, 2267, 6615, 6610, 6609, 6611, 494, 3162, 2205, 2215, 3129, 5779, 2054 };
	
	public static final IntervalItem[] BASIC_CLUE_REWARDS = {
			new IntervalItem(208, 5, 25), new IntervalItem(4587), new IntervalItem(1127), new IntervalItem(1079),
			new IntervalItem(1163), new IntervalItem(1201), new IntervalItem(2579), new IntervalItem(2572),
			new IntervalItem(2570), new IntervalItem(2550), new IntervalItem(892, 100, 250),
			new IntervalItem(9244, 200, 300), new IntervalItem(9245, 50, 144), new IntervalItem(19484, 5, 8),
			new IntervalItem(830, 25, 50), new IntervalItem(13663), new IntervalItem(13664), new IntervalItem(13665),
			new IntervalItem(13658), new IntervalItem(13640), new IntervalItem(13642), new IntervalItem(13643),
			new IntervalItem(13644), new IntervalItem(13646), new IntervalItem(85), new IntervalItem(989) };

	public static final IntervalItem[] EASY_CLUE_REWARDS = { new IntervalItem(12205), new IntervalItem(12207),
			new IntervalItem(12209), new IntervalItem(12211), new IntervalItem(12213), new IntervalItem(12215),
			new IntervalItem(12217), new IntervalItem(12219), new IntervalItem(12221), new IntervalItem(12223),
			new IntervalItem(12225), new IntervalItem(12227), new IntervalItem(12229), new IntervalItem(12231),
			new IntervalItem(12233), new IntervalItem(12235), new IntervalItem(12237), new IntervalItem(12239),
			new IntervalItem(12241), new IntervalItem(12243), new IntervalItem(20169), new IntervalItem(20172),
			new IntervalItem(20175), new IntervalItem(20178), new IntervalItem(20181), new IntervalItem(20184),
			new IntervalItem(20187), new IntervalItem(20190), new IntervalItem(20193), new IntervalItem(20196),
			new IntervalItem(2583), new IntervalItem(2585), new IntervalItem(2587), new IntervalItem(2589),
			new IntervalItem(2591), new IntervalItem(2593), new IntervalItem(2595), new IntervalItem(2597),
			new IntervalItem(2633), new IntervalItem(2635), new IntervalItem(2637), new IntervalItem(12247),
			new IntervalItem(2631), new IntervalItem(12245), new IntervalItem(12449), new IntervalItem(12451),
			new IntervalItem(12453), new IntervalItem(12455), new IntervalItem(12445), new IntervalItem(12447),
			new IntervalItem(7390), new IntervalItem(7392), new IntervalItem(7394), new IntervalItem(7396),
			new IntervalItem(7386), new IntervalItem(7388), new IntervalItem(7362), new IntervalItem(7364),
			new IntervalItem(7366), new IntervalItem(7368), new IntervalItem(7332), new IntervalItem(7338),
			new IntervalItem(7344), new IntervalItem(7350), new IntervalItem(7356), new IntervalItem(10306),
			new IntervalItem(10308), new IntervalItem(10310), new IntervalItem(10312), new IntervalItem(10314),
			new IntervalItem(10404), new IntervalItem(10406), new IntervalItem(10408), new IntervalItem(10410),
			new IntervalItem(10412), new IntervalItem(10414), new IntervalItem(10424), new IntervalItem(10426),
			new IntervalItem(10428), new IntervalItem(10430), new IntervalItem(10432), new IntervalItem(10434),
			new IntervalItem(10714), new IntervalItem(10715), new IntervalItem(10716), new IntervalItem(10717),
			new IntervalItem(10718), new IntervalItem(10366), new IntervalItem(12375), new IntervalItem(10458),
			new IntervalItem(10460), new IntervalItem(10462), new IntervalItem(10464), new IntervalItem(10466),
			new IntervalItem(10468), new IntervalItem(12253), new IntervalItem(12255), new IntervalItem(12265),
			new IntervalItem(12267), new IntervalItem(12193), new IntervalItem(12195), new IntervalItem(12249),
			new IntervalItem(12251), new IntervalItem(20211), new IntervalItem(20214), new IntervalItem(20217),
			new IntervalItem(20166), new IntervalItem(20205), new IntervalItem(20208), new IntervalItem(20199),
			new IntervalItem(20202), new IntervalItem(20164) };

	public static final IntervalItem[] MEDIUM_CLUE_REWARDS = { new IntervalItem(12277), new IntervalItem(12279),
			new IntervalItem(12281), new IntervalItem(12283), new IntervalItem(12285), new IntervalItem(12287),
			new IntervalItem(12289), new IntervalItem(12291), new IntervalItem(12293), new IntervalItem(12295),
			new IntervalItem(2599), new IntervalItem(2601), new IntervalItem(2603), new IntervalItem(2605),
			new IntervalItem(2607), new IntervalItem(2609), new IntervalItem(2611), new IntervalItem(2613),
			new IntervalItem(2577), new IntervalItem(12598), new IntervalItem(2579), new IntervalItem(2645),
			new IntervalItem(2647), new IntervalItem(2649), new IntervalItem(12299), new IntervalItem(12301),
			new IntervalItem(12303), new IntervalItem(12305), new IntervalItem(12307), new IntervalItem(7319),
			new IntervalItem(7321), new IntervalItem(7323), new IntervalItem(7325), new IntervalItem(7327),
			new IntervalItem(12309), new IntervalItem(12311), new IntervalItem(12313), new IntervalItem(7370),
			new IntervalItem(7372), new IntervalItem(7378), new IntervalItem(7380), new IntervalItem(10296),
			new IntervalItem(10298), new IntervalItem(10300), new IntervalItem(10302), new IntervalItem(10304),
			new IntervalItem(10666), new IntervalItem(10669), new IntervalItem(10672), new IntervalItem(10675),
			new IntervalItem(10678), new IntervalItem(10748), new IntervalItem(10400), new IntervalItem(10402),
			new IntervalItem(10420), new IntervalItem(10422), new IntervalItem(12315), new IntervalItem(12317),
			new IntervalItem(12339), new IntervalItem(12341), new IntervalItem(10416), new IntervalItem(10418),
			new IntervalItem(10436), new IntervalItem(10438), new IntervalItem(12343), new IntervalItem(12345),
			new IntervalItem(12347), new IntervalItem(12349), new IntervalItem(10364), new IntervalItem(12377),
			new IntervalItem(10446), new IntervalItem(10448), new IntervalItem(10450), new IntervalItem(10452),
			new IntervalItem(10454), new IntervalItem(10456), new IntervalItem(12203), new IntervalItem(12259),
			new IntervalItem(12271), new IntervalItem(12273), new IntervalItem(12261), new IntervalItem(12197),
			new IntervalItem(12201), new IntervalItem(12257), new IntervalItem(12269), new IntervalItem(12361),
			new IntervalItem(12428), new IntervalItem(20275), new IntervalItem(12319), new IntervalItem(20243),
			new IntervalItem(20240), new IntervalItem(12359), new IntervalItem(20246), new IntervalItem(20266),
			new IntervalItem(20269), new IntervalItem(20272), new IntervalItem(20249) };

	public static final IntervalItem[] HARD_CLUE_REWARDS = { new IntervalItem(2615), new IntervalItem(2617),
			new IntervalItem(2619), new IntervalItem(2621), new IntervalItem(2623), new IntervalItem(2625),
			new IntervalItem(2627), new IntervalItem(2629), new IntervalItem(2653), new IntervalItem(2655),
			new IntervalItem(2657), new IntervalItem(2659), new IntervalItem(2361), new IntervalItem(2363),
			new IntervalItem(2365), new IntervalItem(2367), new IntervalItem(2369), new IntervalItem(2371),
			new IntervalItem(2373), new IntervalItem(2375), new IntervalItem(12460), new IntervalItem(12462),
			new IntervalItem(12464), new IntervalItem(12466), new IntervalItem(12468), new IntervalItem(12470),
			new IntervalItem(12472), new IntervalItem(12474), new IntervalItem(12476), new IntervalItem(12478),
			new IntervalItem(12480), new IntervalItem(12482), new IntervalItem(12484), new IntervalItem(12486),
			new IntervalItem(12488), new IntervalItem(12389), new IntervalItem(12391), new IntervalItem(20146),
			new IntervalItem(20149), new IntervalItem(20152), new IntervalItem(20155), new IntervalItem(20158),
			new IntervalItem(20161), new IntervalItem(3481), new IntervalItem(3483), new IntervalItem(3485),
			new IntervalItem(3486), new IntervalItem(3488), new IntervalItem(7336), new IntervalItem(7342),
			new IntervalItem(7348), new IntervalItem(7354), new IntervalItem(7360), new IntervalItem(10286),
			new IntervalItem(10288), new IntervalItem(10290), new IntervalItem(10292), new IntervalItem(10294),
			new IntervalItem(7374), new IntervalItem(7376), new IntervalItem(7382), new IntervalItem(7384),
			new IntervalItem(12327), new IntervalItem(12329), new IntervalItem(12331), new IntervalItem(12333),
			new IntervalItem(7398), new IntervalItem(7399), new IntervalItem(7400), new IntervalItem(2581),
			new IntervalItem(2639), new IntervalItem(2641), new IntervalItem(2643), new IntervalItem(12321),
			new IntervalItem(12323), new IntervalItem(12325), new IntervalItem(8950), new IntervalItem(10350),
			new IntervalItem(10348), new IntervalItem(10346), new IntervalItem(10352), new IntervalItem(10342),
			new IntervalItem(10338), new IntervalItem(10340), new IntervalItem(10344), new IntervalItem(10334),
			new IntervalItem(10330), new IntervalItem(10332), new IntervalItem(10336), new IntervalItem(12422),
			new IntervalItem(12424), new IntervalItem(12426), new IntervalItem(12437), new IntervalItem(20014),
			new IntervalItem(20017), new IntervalItem(10362), new IntervalItem(10368), new IntervalItem(10370),
			new IntervalItem(10372), new IntervalItem(10374), new IntervalItem(10376), new IntervalItem(10378),
			new IntervalItem(10380), new IntervalItem(10382), new IntervalItem(10384), new IntervalItem(10386),
			new IntervalItem(10388), new IntervalItem(10390), new IntervalItem(12490), new IntervalItem(12492),
			new IntervalItem(12494), new IntervalItem(12496), new IntervalItem(12498), new IntervalItem(12500),
			new IntervalItem(12502), new IntervalItem(12504), new IntervalItem(12506), new IntervalItem(12508),
			new IntervalItem(12510), new IntervalItem(12512), new IntervalItem(10466), new IntervalItem(10468),
			new IntervalItem(10470), new IntervalItem(10472), new IntervalItem(10474), new IntervalItem(10440),
			new IntervalItem(10442), new IntervalItem(10444), new IntervalItem(10446), new IntervalItem(10448),
			new IntervalItem(12199), new IntervalItem(12263), new IntervalItem(12275), new IntervalItem(12363),
			new IntervalItem(12365), new IntervalItem(12367), new IntervalItem(12369), new IntervalItem(12516),
			new IntervalItem(12514), new IntervalItem(12379), new IntervalItem(19912), new IntervalItem(19915),
			new IntervalItem(19918) };

	public static final IntervalItem[] ELITE_CLUE_REWARDS = { new IntervalItem(12436), new IntervalItem(12414),
			new IntervalItem(12415), new IntervalItem(12416), new IntervalItem(12417), new IntervalItem(12418),
			new IntervalItem(12419), new IntervalItem(12420), new IntervalItem(12421), new IntervalItem(12457),
			new IntervalItem(12458), new IntervalItem(12459), new IntervalItem(12351), new IntervalItem(12441),
			new IntervalItem(12443), new IntervalItem(12373), new IntervalItem(12335), new IntervalItem(12337),
			new IntervalItem(12389), new IntervalItem(12391), new IntervalItem(20146), new IntervalItem(20149),
			new IntervalItem(20152), new IntervalItem(20155), new IntervalItem(20158), new IntervalItem(20161),
			new IntervalItem(3481), new IntervalItem(3483), new IntervalItem(3485), new IntervalItem(3486),
			new IntervalItem(3488), new IntervalItem(12432), new IntervalItem(12353), new IntervalItem(12355),
			new IntervalItem(12540), new IntervalItem(12363), new IntervalItem(12365), new IntervalItem(12367),
			new IntervalItem(12369), new IntervalItem(12371), new IntervalItem(12381), new IntervalItem(12383),
			new IntervalItem(12385), new IntervalItem(12387), new IntervalItem(12596), new IntervalItem(12430),
			new IntervalItem(12439), new IntervalItem(12393), new IntervalItem(12395), new IntervalItem(12397),
			new IntervalItem(19988), new IntervalItem(19991), new IntervalItem(19994), new IntervalItem(19997),
			new IntervalItem(20005), new IntervalItem(19958), new IntervalItem(19961), new IntervalItem(19964),
			new IntervalItem(19967), new IntervalItem(19970), new IntervalItem(19973), new IntervalItem(19976),
			new IntervalItem(19979), new IntervalItem(19982), new IntervalItem(19985), new IntervalItem(19941),
			new IntervalItem(10350), new IntervalItem(10348), new IntervalItem(10346), new IntervalItem(10352),
			new IntervalItem(10342), new IntervalItem(10338), new IntervalItem(10340), new IntervalItem(10344),
			new IntervalItem(10334), new IntervalItem(10330), new IntervalItem(10332), new IntervalItem(10336),
			new IntervalItem(12422), new IntervalItem(12424), new IntervalItem(12426), new IntervalItem(12437),
			new IntervalItem(20014), new IntervalItem(20017) };

	public static final IntervalItem[] ULTRA_RARE = { new IntervalItem(10350), new IntervalItem(10348),
			new IntervalItem(10346), new IntervalItem(10352), new IntervalItem(10342), new IntervalItem(10338),
			new IntervalItem(10340), new IntervalItem(10344), new IntervalItem(10334), new IntervalItem(10330),
			new IntervalItem(10332), new IntervalItem(10336), new IntervalItem(12422), new IntervalItem(12424),
			new IntervalItem(12426), new IntervalItem(12437), new IntervalItem(20014), new IntervalItem(20017) };

	public static Item[] determineReward(Player p, ClueDifficulty c) {
		int amount = Utility.inclusiveRandom(c.minReward, c.maxReward);
		List<Item> items = new LinkedList<>();

		if (Chance.VERY_RARE.successful(Utility.r)) {
			IntervalItem item = Utility.randomElement(ULTRA_RARE).clone();
			items.add(new Item(item.id, item.amount));
			amount--;
			PlayerUpdating.executeGlobalMessage("<shad=000000><col=FF5E00>News: " + Utility.formatPlayerName(p.getName()) + " has just received " + ItemDefinition.forId(item.id).getName() + "x" + item.amount + " from a clue scroll!");
		}

		for (int i = 0; i < amount; i++) {
			// if we hit the uncommon table
			
			if (Chance.UNCOMMON.successful(Utility.r)) {
				IntervalItem item = Utility.randomElement(c.rewards).clone();
				items.add(new Item(item.id, item.amount));
			} else {
				// we didn't hit the uncommon table, give them a shit loot
				IntervalItem item = Utility.randomElement(ClueScrollHandler.BASIC_CLUE_REWARDS).clone();
				items.add(new Item(item.id, item.amount));
			}
		}

		return Iterables.toArray(items, Item.class);
	}

	public static boolean npcDrop(Player player, NPC npc) {
		if (player.clueContainer != null && npc.spawnedBy == player.getIndex() && npc.forClue && player.bossDifficulty != null) {
			StringBuilder builder = new StringBuilder("The boss drops a casket, ");
			if (player.getItems().freeSlots() > 0) {
				player.getItems().addItem(2714, 1);
				builder.append("it is added to your inventory!");
			} else if (player.getItems().freeBankSlots() > 0) {
				player.getItems().sendItemToAnyTab(2714, 1);
				builder.append("it is added to your bank!");
			} else {
				GroundItemHandler.createGroundItem(new GroundItem(new Item(2714), npc.getX(), npc.getY(), npc.heightLevel, player));
				builder.append("it is dropped on the floor!");
			}
			player.getActionSender().sendMessage(builder.toString());
			player.clueContainer = null;
			return true;
		}
		return false;
	}

	public static boolean giveReward(Player player) {
		if (player.clueContainer != null) {
			StringBuilder builder = new StringBuilder("You dig and find a casket, ");
			if (player.getItems().freeSlots() > 0) {
				player.getItems().addItem(2714, 1);
				builder.append("it is added to your inventory!");
			} else if (player.getItems().freeBankSlots() > 0) {
				player.getItems().sendItemToAnyTab(2714, 1);
				builder.append("it is added to your bank!");
			} else {
				GroundItemHandler.createGroundItem(new GroundItem(new Item(2714), player.getX(), player.getY(), player.getZ(), player));
				builder.append("it is dropped on the floor!");
			}
			player.getActionSender().sendMessage(builder.toString());
			player.clueContainer = null;
			return true;
		}
		return false;
	}

	public static ClueScroll[] getStages(ClueDifficulty c) {
		int amount = Utility.inclusiveRandom(c.minLeft, c.maxLeft);
		Set<ClueScroll> stages = new HashSet<>(amount);
		amount++;
		for (int i = 0; i < amount; i++)
			stages.add(Utility.randomElement(ClueScroll.values()));
		return Iterables.toArray(stages, ClueScroll.class);
	}
}
package com.model.game.character.player.content.cluescrolls;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.google.common.collect.Iterables;
import com.model.game.character.npc.Npc;
import com.model.game.character.player.Player;
import com.model.game.character.player.PlayerUpdating;
import com.model.game.character.player.packets.encode.impl.SendMessagePacket;
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
	public static final double CLUE_DROP_RATE = 0.008;

	public static final int[] ELITE_CLUE_DROPS = { 4186, 4972, 2881, 2882, 2883, 4172, 4173, 4174, 4175, 3847, 4291, 6222, 6247, 6260, 6203, 3340, 3200 };
	public static final IntervalItem[] DUMMY_CLUE_REWARDS = { new IntervalItem(1085), new IntervalItem(1097), new IntervalItem(1094), new IntervalItem(1113), new IntervalItem(1127), new IntervalItem(1147), new IntervalItem(1149), new IntervalItem(1163), new IntervalItem(1185), new IntervalItem(1201), new IntervalItem(1207), new IntervalItem(1213), new IntervalItem(1215), new IntervalItem(1218), new IntervalItem(1224), new IntervalItem(1233), new IntervalItem(1247), new IntervalItem(1239), new IntervalItem(1253),
	new IntervalItem(1256), new IntervalItem(1261), new IntervalItem(1267), new IntervalItem(1273), new IntervalItem(1281), new IntervalItem(1299), new IntervalItem(1319), new IntervalItem(1333), new IntervalItem(1335), new IntervalItem(1345), new IntervalItem(1348), new IntervalItem(1359), new IntervalItem(1373), new IntervalItem(1377), new IntervalItem(1411), new IntervalItem(1432), new IntervalItem(1440), new IntervalItem(1442), new IntervalItem(1514, 1, 29), new IntervalItem(1515, 12, 108), new IntervalItem(2460), new IntervalItem(2459), new IntervalItem(2490), new IntervalItem(2648), new IntervalItem(2739), new IntervalItem(2, 10, 20), new IntervalItem(1077), new IntervalItem(995, 1, 999999), new IntervalItem(985), new IntervalItem(987), new IntervalItem(989), new IntervalItem(1061), new IntervalItem(6422, 1, 3000), new IntervalItem(6424, 1, 3000), new IntervalItem(6426, 1, 3000), new IntervalItem(6428, 1, 2000), new IntervalItem(6430, 1, 200), new IntervalItem(1540), new IntervalItem(1636), new IntervalItem(1649), new IntervalItem(1652), new IntervalItem(1653), new IntervalItem(1683), new IntervalItem(1706), new IntervalItem(1276), new IntervalItem(1437, 62, 300), new IntervalItem(5641, 14, 21), new IntervalItem(5697, 1, 3), new IntervalItem(5678), new IntervalItem(5700), new IntervalItem(5728), new IntervalItem(5730), new IntervalItem(7336), new IntervalItem(7338), new IntervalItem(7341), new IntervalItem(7342), new IntervalItem(7351), new IntervalItem(14600), new IntervalItem(811, 11, 81), new IntervalItem(2452), new IntervalItem(6432, 1, 800), new IntervalItem(6434, 1, 800), new IntervalItem(6346, 1, 1500), new IntervalItem(6561), new IntervalItem(6689, 1, 3), new IntervalItem(6703), new IntervalItem(6722), new IntervalItem(7056), new IntervalItem(7416), new IntervalItem(7592), new IntervalItem(7593), new IntervalItem(7594), new IntervalItem(7595), new IntervalItem(7596), new IntervalItem(7633), new IntervalItem(7673), new IntervalItem(7803), new IntervalItem(712), new IntervalItem(697), new IntervalItem(698), new IntervalItem(740), new IntervalItem(808, 12, 24), new IntervalItem(892, 100, 500), new IntervalItem(864, 100, 500), new IntervalItem(863, 100, 500), new IntervalItem(865, 100, 500), new IntervalItem(866, 100, 500), new IntervalItem(867, 100, 500), new IntervalItem(868, 100, 500), new IntervalItem(869, 100, 500), new IntervalItem(877, 10, 50), new IntervalItem(1620, 1, 10), new IntervalItem(1618, 1, 10), new IntervalItem(1622, 1, 10), new IntervalItem(1624, 1, 10), new IntervalItem(1512, 1, 10), new IntervalItem(1516, 1, 10), new IntervalItem(1518, 1, 10), new IntervalItem(1520, 1, 10), new IntervalItem(1522, 1, 10), new IntervalItem(561, 100, 250) };
	public static final IntervalItem[] EASY_CLUE_REWARDS = { new IntervalItem(1077), new IntervalItem(1167), new IntervalItem(1645), new IntervalItem(1621), new IntervalItem(1637), new IntervalItem(1718), new IntervalItem(1893), new IntervalItem(1511), new IntervalItem(1168), new IntervalItem(2631), new IntervalItem(2633), new IntervalItem(2635), new IntervalItem(2637), new IntervalItem(2583), new IntervalItem(2585), new IntervalItem(2587), new IntervalItem(2589), new IntervalItem(2591), new IntervalItem(2593), new IntervalItem(2595), new IntervalItem(2597), new IntervalItem(2579), new IntervalItem(2635), new IntervalItem(7329, 1, 17), new IntervalItem(7330, 1, 17), new IntervalItem(7331, 1, 17), new IntervalItem(7362), new IntervalItem(7364), new IntervalItem(7366), new IntervalItem(2631), new IntervalItem(7364), new IntervalItem(7362), new IntervalItem(7368), new IntervalItem(7366), new IntervalItem(7388), new IntervalItem(7370), new IntervalItem(7372), new IntervalItem(7374), new IntervalItem(7376), new IntervalItem(7378), new IntervalItem(7380), new IntervalItem(7382), new IntervalItem(7374), new IntervalItem(7376), new IntervalItem(7378), new IntervalItem(7380), new IntervalItem(7382), new IntervalItem(7384), new IntervalItem(7386), new IntervalItem(7388), new IntervalItem(7390), new IntervalItem(7392), new IntervalItem(7394), new IntervalItem(7396), new IntervalItem(7398), new IntervalItem(11710), new IntervalItem(11712), new IntervalItem(11714), new IntervalItem(10362), new IntervalItem(10364), new IntervalItem(10366), new IntervalItem(9977, 1, 15), new IntervalItem(803, 12, 15), new IntervalItem(802, 12, 19), new IntervalItem(813, 17, 23), new IntervalItem(814, 15, 22), new IntervalItem(815, 14, 25), new IntervalItem(10424), new IntervalItem(824, 1, 17), new IntervalItem(825, 1, 21), new IntervalItem(826, 1, 29), new IntervalItem(827, 12, 32), new IntervalItem(851), new IntervalItem(853), new IntervalItem(858), new IntervalItem(884, 1, 25), new IntervalItem(1033), new IntervalItem(1035), new IntervalItem(1119), new IntervalItem(1121), new IntervalItem(1129), new IntervalItem(1194), new IntervalItem(1191), new IntervalItem(1197), new IntervalItem(1211), new IntervalItem(1239), new IntervalItem(7366), new IntervalItem(7364), new IntervalItem(7356), new IntervalItem(10434), new IntervalItem(10412), new IntervalItem(10366), new IntervalItem(10392), new IntervalItem(2585), new IntervalItem(7392), new IntervalItem(7396), new IntervalItem(7331), new IntervalItem(2635), new IntervalItem(3472), new IntervalItem(7390), new IntervalItem(2633), new IntervalItem(2637) };
	public static final IntervalItem[] MEDIUM_CLUE_REWARDS = { new IntervalItem(2599), new IntervalItem(2601), new IntervalItem(2603), new IntervalItem(2605), new IntervalItem(2607), new IntervalItem(2609), new IntervalItem(2611), new IntervalItem(2613), new IntervalItem(7334), new IntervalItem(7340), new IntervalItem(7346), new IntervalItem(7352), new IntervalItem(7358), new IntervalItem(7319), new IntervalItem(7321), new IntervalItem(7323), new IntervalItem(7325), new IntervalItem(7327), new IntervalItem(7372), new IntervalItem(7370), new IntervalItem(7380), new IntervalItem(7378), new IntervalItem(2645), new IntervalItem(2647), new IntervalItem(2648), new IntervalItem(2577), new IntervalItem(2579), new IntervalItem(2613), new IntervalItem(2611), new IntervalItem(2609), new IntervalItem(2607), new IntervalItem(3475), new IntervalItem(2605), new IntervalItem(2603), new IntervalItem(2599), new IntervalItem(2601), new IntervalItem(3474), new IntervalItem(2580), new IntervalItem(2578), new IntervalItem(2645), new IntervalItem(2649), new IntervalItem(2647), new IntervalItem(10420), new IntervalItem(10422), new IntervalItem(10436), new IntervalItem(10438), new IntervalItem(10416), new IntervalItem(10418), new IntervalItem(10400), new IntervalItem(10402), new IntervalItem(10446), new IntervalItem(10448), new IntervalItem(10450), new IntervalItem(10452), new IntervalItem(10454), new IntervalItem(10456), new IntervalItem(7358), new IntervalItem(7352), new IntervalItem(7346), new IntervalItem(7340), new IntervalItem(7334), new IntervalItem(7319), new IntervalItem(7325), new IntervalItem(7327), new IntervalItem(7323), new IntervalItem(7321) };
	public static final IntervalItem[] HARD_CLUE_REWARDS = { new IntervalItem(7336), new IntervalItem(7342), new IntervalItem(7348), new IntervalItem(7354), new IntervalItem(7360), new IntervalItem(2619), new IntervalItem(2621), new IntervalItem(2617), new IntervalItem(2615), new IntervalItem(3476), new IntervalItem(2627), new IntervalItem(2629), new IntervalItem(2625), new IntervalItem(2623), new IntervalItem(3477), new IntervalItem(2657), new IntervalItem(2659), new IntervalItem(2655), new IntervalItem(2653), new IntervalItem(3478), new IntervalItem(2665), new IntervalItem(2667), new IntervalItem(2577), new IntervalItem(2581), new IntervalItem(2663), new IntervalItem(2661), new IntervalItem(3479), new IntervalItem(2673), new IntervalItem(2675), new IntervalItem(2671), new IntervalItem(2669), new IntervalItem(3480), new IntervalItem(10374), new IntervalItem(10370), new IntervalItem(10372), new IntervalItem(10368), new IntervalItem(10382), new IntervalItem(10378), new IntervalItem(10380), new IntervalItem(10376), new IntervalItem(10390), new IntervalItem(10386), new IntervalItem(10388), new IntervalItem(10384), new IntervalItem(2581), new IntervalItem(2651), new IntervalItem(2639), new IntervalItem(2641), new IntervalItem(2643), new IntervalItem(7400), new IntervalItem(7399), new IntervalItem(7398), new IntervalItem(10440), new IntervalItem(10442), new IntervalItem(10444), new IntervalItem(10446), new IntervalItem(10448), new IntervalItem(10450), new IntervalItem(10362), new IntervalItem(3486), new IntervalItem(3488), new IntervalItem(3483), new IntervalItem(3481), new IntervalItem(3485), new IntervalItem(3831), new IntervalItem(3832), new IntervalItem(3833), new IntervalItem(3834), new IntervalItem(3827), new IntervalItem(3828), new IntervalItem(3829), new IntervalItem(3830), new IntervalItem(3835), new IntervalItem(3836), new IntervalItem(3837), new IntervalItem(3838), new IntervalItem(4561, 10, 50), new IntervalItem(7331), new IntervalItem(7330), new IntervalItem(7329), new IntervalItem(1163), new IntervalItem(1201), new IntervalItem(108), new IntervalItem(1127), new IntervalItem(1093), new IntervalItem(1645), new IntervalItem(1691), new IntervalItem(1700), new IntervalItem(1644), new IntervalItem(990), new IntervalItem(1631), new IntervalItem(892, 50, 200), new IntervalItem(1615), new IntervalItem(1319), new IntervalItem(1333), new IntervalItem(1290), new IntervalItem(1303), new IntervalItem(860), new IntervalItem(862), new IntervalItem(10284), new IntervalItem(565, 10, 200), new IntervalItem(563, 10, 200) };
	public static final IntervalItem[] ELITE_CLUE_REWARDS = { new IntervalItem(1079), new IntervalItem(1093), new IntervalItem(1113), new IntervalItem(1333), new IntervalItem(1127), new IntervalItem(1359), new IntervalItem(1147), new IntervalItem(1373), new IntervalItem(1163), new IntervalItem(2491), new IntervalItem(1185), new IntervalItem(2497), new IntervalItem(1201), new IntervalItem(2503), new IntervalItem(1275), new IntervalItem(861), new IntervalItem(1303), new IntervalItem(859), new IntervalItem(1319), new IntervalItem(2581), new IntervalItem(2577), new IntervalItem(2651), new IntervalItem(3486), new IntervalItem(3488), new IntervalItem(3483), new IntervalItem(3481), new IntervalItem(3485), new IntervalItem(3831), new IntervalItem(3832), new IntervalItem(3833), new IntervalItem(3834), new IntervalItem(3827), new IntervalItem(3828), new IntervalItem(3829), new IntervalItem(3830), new IntervalItem(3835), new IntervalItem(3836), new IntervalItem(3837), new IntervalItem(3838), new IntervalItem(10374), new IntervalItem(10370), new IntervalItem(10372), new IntervalItem(10368), new IntervalItem(10382), new IntervalItem(10378), new IntervalItem(10380), new IntervalItem(10376), new IntervalItem(10390), new IntervalItem(10386), new IntervalItem(10388), new IntervalItem(10384), new IntervalItem(2581), new IntervalItem(4561, 10, 50), new IntervalItem(7331), new IntervalItem(7330), new IntervalItem(7329), new IntervalItem(1163), new IntervalItem(1201), new IntervalItem(1080), new IntervalItem(1127), new IntervalItem(1093), new IntervalItem(1645), new IntervalItem(1691), new IntervalItem(1700), new IntervalItem(1644), new IntervalItem(990), new IntervalItem(1631), new IntervalItem(1275), new IntervalItem(4587), new IntervalItem(892, 50, 200), new IntervalItem(1615), new IntervalItem(1319) };

	public static final IntervalItem[] ULTRA_RARE = { new IntervalItem(15586), new IntervalItem(10350), new IntervalItem(10348), new IntervalItem(10346), new IntervalItem(10352), new IntervalItem(10342), new IntervalItem(10338), new IntervalItem(10340), new IntervalItem(10344), new IntervalItem(10334), new IntervalItem(10330), new IntervalItem(10332), new IntervalItem(10336), };

	public static boolean calculateDrop(Player player, Npc npc, boolean always) {
		if (player.getItems().playerOwnsAnyItems(ClueDifficulty.getClueIds()) || player.bossDifficulty != null)
			return false;
		if ((Math.round(Utility.RANDOM.nextDouble() * 100.0) / 100.0) <= CLUE_DROP_RATE || always) {
			Optional<ClueDifficulty> clueScroll = ClueDifficulty.determineClue(player, npc);
			if (!clueScroll.isPresent())
				return false;
			Item item = new Item(clueScroll.get().clueId);
			if (npc.npcId == 494) {
				GroundItemHandler.createGroundItem(new GroundItem(new Item(item.getId(), item.getAmount()), player.getX(), player.getY(), player.getZ(), player));
			} else {
				GroundItemHandler.createGroundItem(new GroundItem(new Item(item.getId(), item.getAmount()), npc.getX(), npc.getY(), npc.heightLevel, player));
			}
			player.write(new SendMessagePacket("<col=255> You see a clue scroll drop on the floor.."));
			return true;
		}
		return false;
	}
	

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
			if (Chance.UNCOMMON.successful(Utility.r)) {
				IntervalItem item = Utility.randomElement(c.rewards).clone();
				items.add(new Item(item.id, item.amount));
			} else {
				IntervalItem item = Utility.randomElement(ClueScrollHandler.DUMMY_CLUE_REWARDS).clone();
				items.add(new Item(item.id, item.amount));
			}
		}

		return Iterables.toArray(items, Item.class);
	}

	public static boolean npcDrop(Player player, Npc npc) {
		if (player.clueContainer != null && npc.spawnedBy == player.getIndex() && npc.forClue && player.bossDifficulty != null) {
			StringBuilder builder = new StringBuilder("The boss drops a casket, ");
			if (player.getItems().getFreeSlots() > 0) {
				player.getItems().addItem(2714, 1);
				builder.append("it is added to your inventory!");
			} else if (player.getItems().freeBankSlots() > 0) {
				player.getItems().sendItemToAnyTab(2714, 1);
				builder.append("it is added to your bank!");
			} else {
				GroundItemHandler.createGroundItem(new GroundItem(new Item(2714), npc.getX(), npc.getY(), npc.heightLevel, player));
				builder.append("it is dropped on the floor!");
			}
			player.write(new SendMessagePacket(builder.toString()));
			player.clueContainer = null;
			return true;
		}
		return false;
	}

	public static boolean giveReward(Player player) {
		if (player.clueContainer != null) {
			StringBuilder builder = new StringBuilder("You dig and find a casket, ");
			if (player.getItems().getFreeSlots() > 0) {
				player.getItems().addItem(2714, 1);
				builder.append("it is added to your inventory!");
			} else if (player.getItems().freeBankSlots() > 0) {
				player.getItems().sendItemToAnyTab(2714, 1);
				builder.append("it is added to your bank!");
			} else {
				GroundItemHandler.createGroundItem(new GroundItem(new Item(2714), player.getX(), player.getY(), player.getZ(), player));
				builder.append("it is dropped on the floor!");
			}
			player.write(new SendMessagePacket(builder.toString()));
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
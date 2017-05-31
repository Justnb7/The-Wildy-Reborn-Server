package com.model.game.character.player.content.teleport;

import java.text.NumberFormat;
import java.util.HashMap;

import com.model.game.character.player.Player;
import com.model.game.item.Item;
import com.model.game.location.Location;

public class TeleportHandler {

	public enum TeleportationTypes {
		SKILLING,
		PVP,
		PVM,
		MINIGAME;
	}

	public enum TeleportData {

		/* Skilling */
		WOODCUTTING(TeleportationTypes.SKILLING, 222219, 57054, "Woodcutting", new Location(2726, 3490), 0, "---", "Safe", false),
		MINING(TeleportationTypes.SKILLING, 222223, 57058, "Mining", new Location(3253, 3421, 0), 0, "---", "<col=ff0000>Safe", true),
		FISHING_AND_COOKING(TeleportationTypes.SKILLING, 222227, 57062, "Fishing & Cooking", new Location(2809, 3440, 0), 0, "---", "<col=ff0000>Danger", false),
		AGILITY(TeleportationTypes.SKILLING, 222231, 57066, "Agility", new Location(3222, 3218, 0), 0, "---", "<col=ff0000>Danger", true),
		FARMING(TeleportationTypes.SKILLING, 222235, 57070, "Farming", new Location(2964, 3378, 0), 0, "---", "<col=ff0000>Danger <col=ff7000>+ Multi", false),

		/* Player Vs Player */
		WILDERNESS(TeleportationTypes.PVP, 222219, 57054, "Wilderness", new Location(3093, 3523), 0, "---", "---", false),
		MAGE_BANK(TeleportationTypes.PVP, 222223, 57058, "Mage Bank", new Location(2540, 4717, 0), 0, "---", "---", false),
		EAST_DRAGONS(TeleportationTypes.PVP, 222227, 57062, "East Dragons", new Location(3333, 3666, 0), 0, "---", "---", false),
		CASTLE(TeleportationTypes.PVP, 222231, 57066, "Castle", new Location(3002, 3626, 0), 0, "---", "---", false),

		/* Player Vs Monster */
		KING_BLACK_DRAGON(TeleportationTypes.PVM, 222219, 57054, "King Black Dragon", new Location(2997, 3849, 0), 0, "High combat", "40+ Wilderness", false),
		CHAOS_ELEMENT(TeleportationTypes.PVM, 222223, 57058, "Chaos Elemental", new Location(3284, 3913, 0), 0, "---", "50+ Combat", false),
		KRAKEN(TeleportationTypes.PVM, 222227, 57062, "Kraken", new Location(2481, 9799, 0), 0, "---", "---", false),
		CORPOREAL_BEAST(TeleportationTypes.PVM, 222231, 57066, "Corporeal Beast", new Location(2948, 4385, 2), 0, "High combat", "Team Based", false),
		CHAOS_FANATIC(TeleportationTypes.PVM, 222235, 57070, "Chaos Fanatic", new Location(2981, 3837, 0), 0, "High combat", "40+ Wild", false),
		CRAZY_ARCHAEOLOGIST(TeleportationTypes.PVM, 222239, 57074, "Crazy Archaeologist", new Location(2975, 3715, 0), 0, "High combat", "20+ Wild", false),
		CALLISTO(TeleportationTypes.PVM, 222243, 57078, "Callisto", new Location(3283, 3853, 0), 0, "High combat", "40+ Wild", false),
		VETION(TeleportationTypes.PVM, 222247, 57082, "Vet'ion", new Location(3210, 3780, 0), 0, "High combat", "30+ Wild", false),
		SCORPIA(TeleportationTypes.PVM, 222251, 57086, "Scorpia", new Location(3233, 3943, 0), 0, "High combat", "50+ Wild", false),
		VENENATIS(TeleportationTypes.PVM, 222255, 57090, "Venenatis", new Location(3334, 3741, 0), 0, "High combat", "40+ Wild", false),
		COMMANDER_ZILYANA(TeleportationTypes.PVM, 223003, 57094, "Commander Zilyana", new Location(2912, 5266, 0), 0, "High combat", "Team Based", false),
		GENERAL_GRAARDOR(TeleportationTypes.PVM, 223007, 57098, "General Graardor", new Location(2857, 5354, 2), 0, "High combat", "Team Based", false),
		KRIL_TSUTSAROTH(TeleportationTypes.PVM, 223011, 57102, "K'ril Tsutsaroth", new Location(2924, 5340, 2), 0, "High combat", "Team Based", false),
		KREE_ARRA(TeleportationTypes.PVM, 223015, 57106, "Kree'arra", new Location(2840, 5289, 2), 0, "High combat", "Team Based", false),
		BARRELCHEST(TeleportationTypes.PVM, 223019, 57110, "Barrelchest", new Location(3277, 3882, 0), 0, "High combat", "Team Based", false),
		ZOMBIE_CHAMPION(TeleportationTypes.PVM, 223023, 57114, "Zombie Champion", new Location(3020, 3631, 0), 0, "High combat", "Team Based", false),
		ROCK_CRABS(TeleportationTypes.PVM, 223027, 57118, "Rock Crabs", new Location(2671, 3712, 0), 0, "High combat", "Team Based", false),
		TAVERLY_DUNGEON(TeleportationTypes.PVM, 223031, 57122, "Taverly", new Location(2884, 9799, 0), 0, "High combat", "Team Based", false),
		RELEKKA_DUNGEON(TeleportationTypes.PVM, 223035, 57126, "Relekka", new Location(2806, 10000, 0), 0, "High combat", "Team Based", false),
		BRMIHAVE_DUNGEON(TeleportationTypes.PVM, 223039, 57130, "Brimhaven", new Location(2709, 9564, 0), 0, "High combat", "Team Based", false),
		SLAYER_TOWER(TeleportationTypes.PVM, 223043, 57134, "Slayer Tower", new Location(3428, 3538, 0), 0, "High combat", "Team Based", false),
		SLAYER_CAVE(TeleportationTypes.PVM, 223047, 57138, "Slayer Cave", new Location(2439, 9824, 0), 0, "High combat", "Team Based", false),
		
		/* Minigame */
		DUEL_ARENA(TeleportationTypes.MINIGAME, 222219, 57054, "Duel Arena", new Location(3365, 3265, 0), 0, "---", "---", false),
		FIGHT_CAVES(TeleportationTypes.MINIGAME, 222223, 57058, "Fight Caves", new Location(2439, 5171, 0), 0, "---", "---", false),
		PEST_CONTROL(TeleportationTypes.MINIGAME, 222227, 57062, "Pest Control", new Location(2662, 2650, 0), 0, "---", "---", false);

		private final TeleportationTypes teleportType;
		private final int buttonId;
		private final int stringId;
		private final String name;
		private final Location location;
		private final int cost;
		private final String requirement;
		private final String other;
		private final boolean special;

		private TeleportData(TeleportationTypes teleportType, int buttonId, int stringId, String name, Location location, int cost, String requirement, String other, boolean special) {
			this.teleportType = teleportType;
			this.buttonId = buttonId;
			this.stringId = stringId;
			this.name = name;
			this.location = location;
			this.cost = cost;
			this.requirement = requirement;
			this.other = other;
			this.special = special;
		}

		public TeleportationTypes getType() {
			return teleportType;
		}

		public int getButton() {
			return buttonId;
		}

		public int getString() {
			return stringId;
		}

		public String getName() {
			return name;
		}

		public final Location getLocation() {
			return location;
		}

		public int getCost() {
			return cost;
		}

		public String getRequirement() {
			return requirement;
		}

		public String getOther() {
			return other;
		}

		public boolean isSpecial() {
			return special;
		}

		public static HashMap<TeleportationTypes, TeleportData> teleportation = new HashMap<TeleportationTypes, TeleportData>();

		static {
			for (final TeleportData teleportation : TeleportData.values()) {
				TeleportData.teleportation.put(teleportation.teleportType, teleportation);
			}
		}
	}

	public static void display(Player player, TeleportationTypes type, int selected) {
		for (TeleportData data : TeleportData.values()) {
			if (data != null) {
				if (data.getType() == type) {
					String prefix = data.getButton() == selected ? "<col=ff7000>" : "";
					player.getActionSender().sendString(prefix + data.getName(), data.getString());
				}
			}
		}
	}

	public static void open(Player player, TeleportationTypes type) {
		switch (type) {

		case PVP:
			player.getActionSender().sendString("</col>Skilling", 57009);
			player.getActionSender().sendString("<col=ff7000>PvP", 57013);
			player.getActionSender().sendString("</col>PvM", 57017);
			player.getActionSender().sendString("</col>Minigames", 57021);
			player.getActionSender().sendConfig(977, 0);
			player.getActionSender().sendConfig(978, 1);
			player.getActionSender().sendConfig(979, 0);
			player.getActionSender().sendConfig(980, 0);
			player.getActionSender().sendScrollBar(57050, 225);

			break;

		case PVM:
			player.getActionSender().sendString("</col>Skilling", 57009);
			player.getActionSender().sendString("</col>PvP", 57013);
			player.getActionSender().sendString("<col=ff7000>PvM", 57017);
			player.getActionSender().sendString("</col>Minigames", 57021);
			player.getActionSender().sendConfig(977, 0);
			player.getActionSender().sendConfig(978, 0);
			player.getActionSender().sendConfig(979, 1);
			player.getActionSender().sendConfig(980, 0);
			player.getActionSender().sendScrollBar(57050, 490);
			break;

		case MINIGAME:
			player.getActionSender().sendString("</col>Skilling", 57009);
			player.getActionSender().sendString("</col>PvP", 57013);
			player.getActionSender().sendString("</col>PvM", 57017);
			player.getActionSender().sendString("<col=ff7000>Minigames", 57021);
			player.getActionSender().sendConfig(977, 0);
			player.getActionSender().sendConfig(978, 0);
			player.getActionSender().sendConfig(979, 0);
			player.getActionSender().sendConfig(980, 1);
			player.getActionSender().sendScrollBar(57050, 225);
			break;

		case SKILLING:
		default:
			player.getActionSender().sendString("<col=ff7000>Skilling", 57009);
			player.getActionSender().sendString("</col>PvP", 57013);
			player.getActionSender().sendString("</col>PvM", 57017);
			player.getActionSender().sendString("</col>Minigames", 57021);
			player.getActionSender().sendConfig(977, 1);
			player.getActionSender().sendConfig(978, 0);
			player.getActionSender().sendConfig(979, 0);
			player.getActionSender().sendConfig(980, 0);
			player.getActionSender().sendScrollBar(57050, 225);
			break;
		}
		clear(player);
		player.setTeleportationType(type);
		// player.setTeleportButton(0);
		display(player, type, 0);
		player.getActionSender().sendInterface(57000);
	}

	public static boolean select(Player player, int button) {

		TeleportData teleportation = TeleportData.teleportation.get(player.getTeleportationType());

		if (teleportation == null) {
			return false;
		}

		player.setTeleportButton(button);

		TeleportData currentData = null;

		for (TeleportData data : TeleportData.values()) {
			if (player.getTeleportationType() == data.getType()) {
				if (player.getTeleportButton() == data.getButton()) {
					currentData = data;
				}
			}
		}

		if (currentData != null) {
			player.getActionSender().sendString("</col>Selected: <col=ff7000>" + currentData.getName(), 57023);
			player.getActionSender().sendString("</col>Cost: <col=ff7000>" + (currentData.getCost() == 0 ? "Free" : NumberFormat.getInstance().format(currentData.getCost())), 57024);
			player.getActionSender().sendString("</col>Requirable(s): <col=ff7000>" + currentData.getRequirement(), 57025);
			player.getActionSender().sendString("</col>Other: <col=ff7000>" + currentData.getOther(), 57026);
			display(player, player.getTeleportationType(), button);
		}

		return true;
	}

	public static void teleport(Player player) {

		if (player.getTeleportButton() == 0) {
			player.getActionSender().sendMessage("Please select a teleport location first.");
			return;
		}

		TeleportData teleportation = TeleportData.teleportation.get(player.getTeleportationType());

		if (teleportation == null) {
			return;
		}

		TeleportData currentData = null;

		for (TeleportData data : TeleportData.values()) {
			if (player.getTeleportationType() == data.getType()) {
				if (player.getTeleportButton() == data.getButton()) {
					currentData = data;
				}
			}
		}

		if (currentData == null) {
			return;
		}

		if (currentData.isSpecial()) {
			handleSpecial(player, currentData);
			return;
		}

		boolean can = false;

		if (currentData.getCost() != 0) {
			if (player.getInventory().contains(995, currentData.getCost())) {
				player.getInventory().remove(new Item(995, currentData.getCost()));
				player.getActionSender().sendMessage("You have paid a fee of " + NumberFormat.getInstance().format(currentData.getCost()) + ".");
				can = true;
			} else {
				player.getActionSender().sendMessage("You do not have enough coins to do this!");
			}
		} else {
			player.getActionSender().sendMessage("You did not have to pay a fee as the teleport was free.");
			can = true;
		}

		if (can) {
			TeleportExecutor.teleport(player, currentData.getLocation());
			player.getActionSender().sendMessage("You have teleported to " + currentData.getName() + ".");
		}
	}

	public static void clear(Player player) {
		player.getActionSender().sendString("---", 57054);
		player.getActionSender().sendString("---", 57058);
		player.getActionSender().sendString("---", 57062);
		player.getActionSender().sendString("---", 57066);
		player.getActionSender().sendString("---", 57070);
		player.getActionSender().sendString("---", 57074);
		player.getActionSender().sendString("---", 57078);
		player.getActionSender().sendString("---", 57082);
		player.getActionSender().sendString("---", 57086);
		player.getActionSender().sendString("---", 57090);
		player.getActionSender().sendString("</col>Selected: <col=ff7000>---", 57023);
		player.getActionSender().sendString("</col>Cost: <col=ff7000>---", 57024);
		player.getActionSender().sendString("</col>Requirable(s): <col=ff7000>---", 57025);
		player.getActionSender().sendString("</col>Other: <col=ff7000>---", 57026);
	}

	private static void handleSpecial(Player player, TeleportData data) {
		switch (data) {
		case MINING:
			player.dialogue().start("MINING_TELEPORTS");
			break;
		case AGILITY:
			player.dialogue().start("AGILITY_TELEPORTS");
			break;
		default:
			break;
		}
	}

}
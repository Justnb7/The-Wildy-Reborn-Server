package com.venenatis.game.content.teleportation;

import java.text.NumberFormat;
import java.util.HashMap;

import com.venenatis.game.content.teleportation.Teleport.TeleportTypes;
import com.venenatis.game.location.Location;
import com.venenatis.game.model.entity.player.Player;

public class TeleportHandler {

	public enum TeleportationTypes {
		SKILLING,
		PVP,
		PVM,
		MINIGAME;
	}

	public enum TeleportData {

		/* Skilling */
		WOODCUTTING(TeleportationTypes.SKILLING, 226195, 58054, "Woodcutting", new Location(2726, 3490), 0, "---", "---", false),
		MINING(TeleportationTypes.SKILLING, 226199, 58058, "Mining", new Location(-1, -1, 0), 0, "---", "---", true),
		FISHING_AND_COOKING(TeleportationTypes.SKILLING, 226203, 58062, "Fishing & Cooking", new Location(2876, 3336, 0), 0, "---", "---", false),
		AGILITY(TeleportationTypes.SKILLING, 226207, 58066, "Agility", new Location(-1, -1, 0), 0, "---", "---", true),
		
		/* Player Vs Player */
		WILDERNESS(TeleportationTypes.PVP, 226195, 58054, "Wilderness", new Location(3093, 3523), 0, "---", "---", false),
		MAGE_BANK(TeleportationTypes.PVP, 226199, 58058, "Mage Bank", new Location(2540, 4717, 0), 0, "---", "---", false),
		EAST_DRAGONS(TeleportationTypes.PVP, 226203, 58062, "East Dragons", new Location(3333, 3666, 0), 0, "---", "19+ Wilderness", false),
		CASTLE(TeleportationTypes.PVP, 226207, 58066, "Castle", new Location(3002, 3626, 0), 0, "---", "14+ Wilderness", false),

		/* Player Vs Monster */
		SLAYER_LOCATIONS(TeleportationTypes.PVM, 226195, 58054, "Slayer locations", new Location(-1, -1, 0), 0, "---", "---", true),
		TRAINING_LOCATIONS(TeleportationTypes.PVM, 226199, 58058, "Training locations", new Location(-1, -1, 0), 0, "---", "---", true),
		KING_BLACK_DRAGON(TeleportationTypes.PVM, 226203, 58062, "King black dragon", new Location(2997, 3849, 0), 0, "High combat", "40+ Wilderness", false),
		CHAOS_ELEMENTAL(TeleportationTypes.PVM, 226207, 58066, "Chaos elemental", new Location(3284, 3913, 0), 0, "High combat", "50+ Wilderness", false),
		DAGANNOTHS(TeleportationTypes.PVM, 226211, 58070, "Dagannoth kings", new Location(1912, 4367, 0), 0, "---", "---", false),
		DAGANNOTH_MOTHER(TeleportationTypes.PVM, 226215, 58074, "Dagannoth mother", new Location(2530, 4649, 0), 0, "High combat", "20+ Wild", false),
		ZOMBIES_CHAMPION(TeleportationTypes.PVM, 226219, 58078, "Zombies champion", new Location(3022, 3632, 0), 0, "High combat", "20+ Wild", false),
		GIANT_MOLE(TeleportationTypes.PVM, 226223, 58082, "Giant mole", new Location(1761, 5186, 0), 0, "High combat", "20+ Wild", false),
		GODWARS(TeleportationTypes.PVM, 226227, 58086, "Godwars dungeon", new Location(-1, -1, 0), 0, "High combat", "20+ Wild", true),
		CORPOREAL_BEAST(TeleportationTypes.PVM, 226231, 58090, "Corporeal beast", new Location(2967, 4383, 2), 0, "High combat", "---", false),
		LIZARDMAN_SHAMAN(TeleportationTypes.PVM, 226235, 58094, "Lizardman shaman", new Location(1442, 3695, 0), 0, "High combat", "---", false),
		ZULRAH(TeleportationTypes.PVM, 226239, 58098, "Zulrah", new Location(-1, -1, 0), 0, "High combat", "---", true),

		/* Minigame */
		DUEL_ARENA(TeleportationTypes.MINIGAME, 226195, 58054, "Duel Arena", new Location(3365, 3265, 0), 0, "---", "---", false),
		FIGHT_CAVES(TeleportationTypes.MINIGAME, 226199, 58058, "Fight Caves", new Location(2439, 5171, 0), 0, "---", "---", false);

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
			player.getActionSender().sendString("</col>Skilling", 58009);
			player.getActionSender().sendString("<col=ff7000>PvP", 58013);
			player.getActionSender().sendString("</col>PvM", 58017);
			player.getActionSender().sendString("</col>Minigames", 58021);
			player.getActionSender().sendConfig(677, 0);
			player.getActionSender().sendConfig(678, 1);
			player.getActionSender().sendConfig(679, 0);
			player.getActionSender().sendConfig(680, 0);
			player.getActionSender().sendScrollBar(58050, 0);

			break;

		case PVM:
			player.getActionSender().sendString("</col>Skilling", 58009);
			player.getActionSender().sendString("</col>PvP", 58013);
			player.getActionSender().sendString("<col=ff7000>PvM", 58017);
			player.getActionSender().sendString("</col>Minigames", 58021);
			player.getActionSender().sendConfig(677, 0);
			player.getActionSender().sendConfig(678, 0);
			player.getActionSender().sendConfig(679, 1);
			player.getActionSender().sendConfig(680, 0);
			player.getActionSender().sendScrollBar(58050, 375);
			break;

		case MINIGAME:
			player.getActionSender().sendString("</col>Skilling", 58009);
			player.getActionSender().sendString("</col>PvP", 58013);
			player.getActionSender().sendString("</col>PvM", 58017);
			player.getActionSender().sendString("<col=ff7000>Minigames", 58021);
			player.getActionSender().sendConfig(677, 0);
			player.getActionSender().sendConfig(678, 0);
			player.getActionSender().sendConfig(679, 0);
			player.getActionSender().sendConfig(680, 1);
			player.getActionSender().sendScrollBar(58050, 0);
			break;

		case SKILLING:
		default:
			player.getActionSender().sendString("<col=ff7000>Skilling", 58009);
			player.getActionSender().sendString("</col>PvP", 58013);
			player.getActionSender().sendString("</col>PvM", 58017);
			player.getActionSender().sendString("</col>Minigames", 58021);
			player.getActionSender().sendConfig(677, 1);
			player.getActionSender().sendConfig(678, 0);
			player.getActionSender().sendConfig(679, 0);
			player.getActionSender().sendConfig(680, 0);
			player.getActionSender().sendScrollBar(58050, 0);
			break;
		}
		clear(player);
		player.setTeleportationType(type);
		// player.setTeleportButton(0);
		display(player, type, 0);
		player.getActionSender().sendInterface(58000);
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
			player.getActionSender().sendString("</col>Selected: <col=ff7000>" + currentData.getName(), 58023);
			player.getActionSender().sendString("</col>Cost: <col=ff7000>" + (currentData.getCost() == 0 ? "Free" : NumberFormat.getInstance().format(currentData.getCost())), 58024);
			player.getActionSender().sendString("</col>Requirable(s): <col=ff7000>" + currentData.getRequirement(), 58025);
			player.getActionSender().sendString("</col>Other: <col=ff7000>" + currentData.getOther(), 58026);
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
				player.getInventory().remove(995, currentData.getCost(), true);
				player.getActionSender().sendMessage("You have paid a fee of " + NumberFormat.getInstance().format(currentData.getCost()) + ".");
				can = true;
			} else {
				player.getActionSender().sendMessage("You do not have enough coins to do this!");
			}
		} else {
			can = true;
		}

		if (can) {
			player.getTeleportAction().teleport(currentData.getLocation(), TeleportTypes.SPELL_BOOK, false);
			if(currentData.getName().equalsIgnoreCase("Giant Mole")) {
				player.getActionSender().sendMessage("You seem to have dropped down into a network of mole tunnels.");
			} else {
				player.getActionSender().sendMessage("You have teleported to " + currentData.getName() + ".");
			}
			player.setTeleportationType(null);
		}
	}

	public static void clear(Player player) {
		player.getActionSender().sendString("---", 58054);
		player.getActionSender().sendString("---", 58058);
		player.getActionSender().sendString("---", 58062);
		player.getActionSender().sendString("---", 58066);
		player.getActionSender().sendString("---", 58070);
		player.getActionSender().sendString("---", 58074);
		player.getActionSender().sendString("---", 58078);
		player.getActionSender().sendString("---", 58082);
		player.getActionSender().sendString("---", 58086);
		player.getActionSender().sendString("---", 58090);
		player.getActionSender().sendString("</col>Selected: <col=ff7000>---", 58023);
		player.getActionSender().sendString("</col>Cost: <col=ff7000>---", 58024);
		player.getActionSender().sendString("</col>Requirable(s): <col=ff7000>---", 58025);
		player.getActionSender().sendString("</col>Other: <col=ff7000>---", 58026);
	}

	private static void handleSpecial(Player player, TeleportData data) {
		switch (data) {
		case SLAYER_LOCATIONS:
			player.getDialogueManager().start("SLAYER_TELEPORTS");
			break;
		case TRAINING_LOCATIONS:
			player.getDialogueManager().start("TRAINING_TELEPORTS");
			break;
		case MINING:
			player.getDialogueManager().start("MINING_TELEPORTS");
			break;
		case AGILITY:
			player.getDialogueManager().start("AGILITY_TELEPORTS");
			break;
		case GODWARS:
			player.getDialogueManager().start("GODWARS_TELEPORTS");
			break;
		case ZULRAH:
			player.getActionSender().sendMessage("@red@[Server]: Zulrah is work in progress, and shall be released shortly.");
			break;
		default:
			break;
		}
	}

}
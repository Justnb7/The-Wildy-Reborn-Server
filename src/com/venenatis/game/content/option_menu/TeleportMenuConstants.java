package com.venenatis.game.content.option_menu;

import java.util.HashMap;

import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.action.impl.actions.TeleportAction;
import com.venenatis.game.location.Location;

/**
 * The constants for the Teleport Menu.
 * 
 * @author Lennard
 *
 */
public class TeleportMenuConstants {

	public static HashMap<Integer, OptionMenu> getDefaultTeleports(final Player player) {
		final HashMap<Integer, OptionMenu> teleportOptions = new HashMap<Integer, OptionMenu>();

		teleportOptions.put(0,
				new OptionMenu("Taverley Dungeon",
						new String[] { "Blue Dragon", "Chaos Druid", "Baby Blue Dragon", "Black Demon", "Black Dragon",
								"Hellhound", "Hill Giant", "Lesser Demon", "Skeleton", "Ghost", "Chaos Dwarf", "Dungeon", "Talvery","Slayer" },
						new TeleportAction(player, new Location(2884, 9798))));

		teleportOptions.put(1, new OptionMenu("Edgeville Dungeon",
				new String[] { "Chaos Druid", "Black Demon", "Rat", "Giant Rat", "Giant Spider", "Zombie", "Skeleton",
						"Hobgoblin", "Hill Giant", "Moss Giant", "Thug", "Deadly Red Spider", "Earth Warrior", "Dungeon",
						"Poison Spider","Slayer" },
				new TeleportAction(player, new Location(3097, 9868))));

		teleportOptions.put(2,
				new OptionMenu("Slayer Tower",
						new String[] { "Crawling Hand", "Banshee", "Infernal mage", "Bloodveld", "Aberrant Spectre",
								"Gargoyle", "Nechryael", "Abyssal Demon", "Train","Slayer" },
						new TeleportAction(player, new Location(3097, 9868))));

		teleportOptions.put(3, new OptionMenu("Mourmer Tunnel", new String[] { "Dark Beast" },
				new TeleportAction(player, new Location(2029, 4636))));

		teleportOptions.put(4,
				new OptionMenu("Asgarnian Ice Dungeon",
						new String[] { "Skeletal Wyvern", "Ice Giant", "Ice warrior", "Pirate", "Hobgoblin", "Train", "Dungeon","Slayer", "train" },
						new TeleportAction(player, new Location(3007, 9550))));

		teleportOptions.put(5,
				new OptionMenu("Brimhaven Dungeon",
						new String[] { "Wild Dog", "Moss Giant", "Fire Giant", "Greater Demon", "Black Demon",
								"Baby Green Dragon", "Baby Red dragon", "Red dragon", "Bronze Dragon", "Iron Dragon", "Dungeon",
								"Steel Dragon","Slayer" },
						new TeleportAction(player, new Location(2713, 9564))));

		teleportOptions.put(6,
				new OptionMenu("Lumbridge Swamp Caves",
						new String[] { "Cave Crawler", "Cave Slime", "Cave Bug", "Rockslug", "Wall Beast", "Dungeon","Slayer" },
						new TeleportAction(player, new Location(3168, 9572))));

		teleportOptions.put(7,
				new OptionMenu("Stronghold Slayer Cave",
						new String[] { "Aberrant Spectre", "Abyssal Demon", "Ankou", "Baby Black Dragon", "Black Demon",
								"Black Dragon", "Bloodveld", "Blue Dragon", "Bronze Dragon", "Fire Giant", "Gargoyle",
								"Greater Demon", "Hellhound", "Iron Dragon", "Kalphite", "Nechryael", "Steel Dragon",
								"Waterfiend", "Dungeon","Slayer" },
						new TeleportAction(player, new Location(2444, 9825))));

		teleportOptions.put(8,
				new OptionMenu("Ancient Cavern", new String[] { "Waterfiend", "Brutal Green Dragon", "Mithril Dragon", "Train" },
						new TeleportAction(player, new Location(1746, 5327, 0))));

		teleportOptions.put(9, new OptionMenu("Waterfall Dungeon", new String[] { "Fire Giant" },
				new TeleportAction(player, new Location(2575, 9863))));

		teleportOptions.put(10, new OptionMenu("Experiment Cave", new String[] { "Experiment" },
				new TeleportAction(player, new Location(3577, 9927))));

		teleportOptions.put(11,
				new OptionMenu(
						"Fremennik Slayer Dungeon", new String[] { "Cave Crawler", "Rockslug", "Cockatrice",
								"Pyrefiend", "Basilisk", "Jelly", "Turoth", "Kurask", "Train" },
						new TeleportAction(player, new Location(2807, 10002))));

		teleportOptions.put(12, new OptionMenu("Lighthouse", new String[] { "Dagannoth" },
				new TeleportAction(player, new Location(2509, 3634))));

		teleportOptions.put(13, new OptionMenu("Rock Crab", new String[] { "Rock Crab", "Train","Waterbirth" },
				new TeleportAction(player, new Location(2550, 3758))));

		teleportOptions.put(14,
				new OptionMenu("Smoke Dungeon", new String[] { "Dust Devil", "Fire Giant", "Pyrefiend", "Train" },
						new TeleportAction(player, new Location(3206, 9379))));

		teleportOptions.put(15,
				new OptionMenu("Home", new String[] { "Trade", "Trading", "Market", "Pking", "Edgeville", "Train" },
						new TeleportAction(player, new Location(3087, 3500, 0))));

		teleportOptions.put(16, new OptionMenu("<img=28>Graveyard", new String[] { "Wilderness", "Graveyard","Wild" },
				new TeleportAction(player, new Location(2974, 3748))));

		teleportOptions.put(17, new OptionMenu("Varrock Multi", new String[] { "Wilderness", "Varrock Multi" },
				new TeleportAction(player, new Location(3243, 3518))));

		teleportOptions.put(18, new OptionMenu("Edgeville", new String[] { "Wilderness", "Edgeville" },
				new TeleportAction(player, new Location(3087, 3500, 0))));

		teleportOptions.put(19, new OptionMenu("Mage Bank", new String[] { "Wilderness", "Mage Bank" },
				new TeleportAction(player, new Location(2539, 4716))));

		teleportOptions.put(20,
				new OptionMenu("<img=28>West Dragons",
						new String[] { "Wilderness", "Low Risk", "Green Dragon", "Varrock West","Train" },
						new TeleportAction(player, new Location(2982, 3603))));

		teleportOptions.put(21,
				new OptionMenu("<img=28>East Dragons",
						new String[] { "Wilderness", "high Risk", "Green Dragon", "Varrock East" },
						new TeleportAction(player, new Location(3366, 3634))));

		teleportOptions.put(22, new OptionMenu("Corporeal Beast Lair", new String[] { "Boss", "Corporal Beast" },
				new TeleportAction(player, new Location(2945, 4384, 2))));

		teleportOptions.put(23,
				new OptionMenu("Rogue's Castle", new String[] { "Boss", "Wilderness", "Chaos Elemental" },
						new TeleportAction(player, new Location(3307, 3916, 0))));

		teleportOptions.put(24, new OptionMenu("Abyssal Nexus", new String[] { "Boss", "Abyssal Sire" },
				new TeleportAction(player, new Location(3039, 4772, 0))));

		teleportOptions.put(25, new OptionMenu("Cerberus Lair", new String[] { "Boss", "Cerberus" },
				new TeleportAction(player, new Location(1240, 1226, 0))));

		teleportOptions.put(26,
				new OptionMenu("Godwars Dungeon",
						new String[] { "Boss", "gwd", "Godwars", "Bandos", "Saradomin", "Zamorak", "Armadyl" },
						new TeleportAction(player, new Location(2881, 5310, 2))));

		teleportOptions.put(27, new OptionMenu("Mole Lair", new String[] { "Boss", "Giant Mole" },
				new TeleportAction(player, new Location(1760, 5163))));

		teleportOptions.put(28, new OptionMenu("Kalphite Lair", new String[] { "Boss", "Kq", "Kalphite Queen" },
				new TeleportAction(player, new Location(3484, 9510, 2))));

		teleportOptions.put(29,
				new OptionMenu("Dagannoth Dungeon", new String[] { "Boss", "Dagannoth", "Rex", "Prime", "Supreme" },
						new TeleportAction(player, new Location(2442, 10147, 0))));

		teleportOptions.put(30,
				new OptionMenu("King Black Dragon Lair", new String[] { "Boss", "KBD", "King Black Dragon" },
						new TeleportAction(player, new Location(2998, 3850, 0))));

		teleportOptions.put(31, new OptionMenu("Kraken Cave", new String[] { "Boss", "Kraken" },
				new TeleportAction(player, new Location(2276, 9988, 0))));

		teleportOptions.put(32, new OptionMenu("Smoke Devil Cave", new String[] { "Boss", "Thermonuclear Smoke Devil" },
				new TeleportAction(player, new Location(3723, 5798, 0))));

		teleportOptions.put(33, new OptionMenu("Zulrah's Island", new String[] { "Boss", "Zulrah" },
				new TeleportAction(player, new Location(2267, 3069, 0))));

		teleportOptions.put(34, new OptionMenu("Duel Arena", new String[] { "Minigames", "Duel Arena", "Staking" },
				new TeleportAction(player, new Location(3365, 3265, 0))));

		teleportOptions.put(35, new OptionMenu("Barrows", new String[] { "Minigames", "Barrows" },
				new TeleportAction(player, new Location(3565, 3316, 0))));

		teleportOptions.put(36, new OptionMenu("Fight Caves", new String[] { "Minigames", "Fight Caves", "Fire Cape" },
				new TeleportAction(player, new Location(2480, 5175, 0))));
//
		teleportOptions.put(37,
				new OptionMenu("Warrior's Guild", new String[] { "Minigames", "Warrior Guild", "Defender" },
						new TeleportAction(player, new Location(2841, 3538, 0))));

		teleportOptions.put(39, new OptionMenu("Pest Control", new String[] { "Minigames", "Pest Control", "Void" },
				new TeleportAction(player, new Location(2658, 2649, 0))));

		teleportOptions.put(40, new OptionMenu("Brimhaven Agility", new String[] { "Minigames", "Brimhaven Agility" },
				new TeleportAction(player, new Location(0, 0))));

		//teleportOptions.put(41, new OptionMenu("Fishing Trawler", new String[] { "Minigames", "Fishing Trawler" },
		//		new TeleportAction(player, new Location(0, 0))));
		
		teleportOptions.put(41, new OptionMenu("Varrock", new String[] { "Cities", "Varrock" },
				new TeleportAction(player, new Location(3110, 3424))));

		teleportOptions.put(42, new OptionMenu("Falador", new String[] { "Cities", "Falador" },
				new TeleportAction(player, new Location(2965, 3380))));
		
		teleportOptions.put(43, new OptionMenu("Lumbridge", new String[] { "Cities", "Lumbridge" },
				new TeleportAction(player, new Location(3223, 3218))));
		
		teleportOptions.put(44, new OptionMenu("Draynor", new String[] { "Cities", "Draynor" },
				new TeleportAction(player, new Location(3093, 3244))));
		
		teleportOptions.put(45, new OptionMenu("Camelot", new String[] { "Cities", "Camelot" },
				new TeleportAction(player, new Location(2757, 3477))));
		
		teleportOptions.put(46, new OptionMenu("Wintertodt", new String[] { "Wintertodt", "Minigames", "Firemaking" },
				new TeleportAction(player, new Location(1630, 3947))));
		
		teleportOptions.put(47, new OptionMenu("Lleyta", new String[] { "Cities", "Elvs", "Crystal Bow", "Elf", "train" },
				new TeleportAction(player, new Location(1630, 3947))));
		teleportOptions.put(47, new OptionMenu("Waterbirth", new String[] { "waterbirth", "rock crab", "dagganoth", "dks", "train" },
				new TeleportAction(player, new Location(2250, 3757))));
		return teleportOptions;
	}

}
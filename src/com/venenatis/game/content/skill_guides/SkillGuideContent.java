package com.venenatis.game.content.skill_guides;

public enum SkillGuideContent {

	ATTACK_WEAPONS(new int[] {
			1, 1, 5, 10, 20, 30, 30, 40, 40, 40, 50, 60, 60, 60, 65, 70, 70, 70, 70, 70, 70, 70, 70, 70, 70, 75, 75, 75, 75, 75, 75, 75
		},
		new int[] {
				1205, 1203, 1207, 1217, 1209, 1211, 1391, 1213, 11037, 1381, 4153, 1215, 10887, 6523, 12426, 13080, 11838, 11824, 4151, 13263, 4710, 4718, 4747, 4755, 4726, 19675, 11802, 11791, 12006, 12808, 21003, 21015 
		},
		new String[] {
			"Bronze", "Iron", "Steel", "Black", "Mirthil", "Adamant", "Battlestaves (with 30 Magic)", "Rune", "Brine sabre", "Mystic staves (with 40 Magic)", "Granite maul", "Dragon",
			"Barrelchest Anchor", "Obsidian weapons", "3rd age weapons", "Crystal weaponry", "Saradomin sword", "Zamorak spear", "Abyssal whip & dagger", "Abyssal bludgeon",
			"Ahrim's staff", "Dharok's greataxe", "Torag's hammers", "Verac's flail", "Guthan's warspear", "Arclight", "Godswords", "Staff of the Dead", "Abyssal tentacle", "Blessed Saradomin sword", "Elder maul", "Dinh's bulwark"
		},
		new String[] {
			"", "", "", "", "", "", "", "", "", "", "(with 50 Strength)", "",
			"", "", "", "(with 50 Agility)", "", "", "", "(with 70 Strength)",
			"(with 70 Magic)", "(with 70 Strength)", "(with 70 Strength)", "", "", "", "", "(with 75 Magic)", "", " ", "(with 75 Strength)", "(with 75 Defence)"
		}
	),
	ATTACK_ARMOUR(new int[] {
			42
		},
		new int[] {
				8839
		},
		new String[] {
			"Void Knight equipment"
		},
		new String[] {
			"(with 42 combat stats and 22 Prayer)"
		}
	),
	ATTACK_SALAMANDERS(new int[] {
			30, 50, 60, 70
		},
		new int[] {
			10149, 10146, 10147, 10148
		},
		new String[] {
			"Swamp lizard", "Orange Salamander", "Red salamander", "Black salamander"
		},
		new String[] {
			"(30 Attack, Ranged, & Magic)", "(50 Attack, Ranged, & Magic)", "(60 Attack, Ranged, & Magic)", "(70 Attack, Ranged, & Magic)"
		}
	),
	DEFENCE_ARMOUR(new int[] {
			1, 1, 5, 10, 10, 10, 10, 20, 20, 20, 20, 30, 30, 35, 40, 40, 45, 45, 50, 55, 60, 60, 60, 65, 65, 65, 70, 70, 70, 70, 75, 75, 75, 75, 75, 75, 75, 75, 75, 75, 75, 75
		},
		new int[] {
			1139, 1137, 1141, 1151, 6621, 11864, 13385, 1143, 10822, 5574, 13374, 1145, 9672, 20035, 1147, 6128, 8839, 3751, 12829, 3122, 10828, 1149, 6524, 11926, 21301, 11832, 10350, 21021, 4745, 4224, 11826, 12831, 11283, 21633, 12931, 12817, 12825, 13239, 13235, 13237, 21000, 21015
		},
		new String[] {
			"Bronze", "Iron", "Steel", "Black", "White", "Slayer helm", "Xerician armour", "Mithril", "Yak-hide", "Initiate armour", "Shayzien armour", "Adamantite", "Proselyte armour", "Samurai armour", "Rune", "Rock-shell armour", "Vod Knight equipment", "Fremennik helmets", "Spirit shield", 
			"Granite", "Helm of Neitiznot", "Dragon", "Toktz-Ket-xil", "Odium & Malediction wards", "Obsidian armour", "Bandos armour", "3rd age fighter armour", "Ancestral robes", "Barrows armour", "Elf crystal (with 50 Agility)", "Armadyl armour (with 70", "Blessed spirit shield", "Dragonfire shield", "Ancient Wyvern shield", "Serpentine helm", "Elysian spirit shield",
			"Arcane & Spectral spirit shields", "Primordial boots", "Eternal boots", "Pegasian boots", "Twisted buckler", "Dinh's bulwark"
		},
		new String[] {
		    "", "", "", "", "", "", "(with 20 Magic)", "", "", "(with 10 Prayer)", "", "", "(with 20 Prayer)", "", "", "(after Fremennik Trials)", "(with 42 combat stats and 22 Prayer)", "(after Fremennik Trials)", "(with 55 Prayer)", "(with 50 Strength)", "(after Fremennik Isles)", "", "", "", "", "", "", "(with 75 Magic)", "", "", "Ranged)", "(with 60 Prayer)", "", "", "", "(with 75 Prayer)", "(with 70 Prayer and 65 Magic)", "(with 75 Strength)", "(with 75 Magic)", "(with 75 Ranged)", "(with 75 Ranged)", "(with 75 Attack)"
		}
	),
	DEFENCE_PENANCE(new int[] {
			40, 40, 40, 40, 45, 45, 45, 45
		},
		new int[] {
			10551, 10552, 10553, 10555, 10548, 10550, 10547, 10549
		},
		new String[] {
			"Fighter torso", "Runner boots", "Penance gloves", "Penance skirt", "Fighter hat", "Ranger hat", "Healer hat", "Runner hat"
		},
		new String[] {
			"", "", "", "(with 60 Ranged)", "", "", "", "", 
		}
	),
	DEFENCE_PRAYERS(new int[] {
			70, 70
		},
		new int[] {
			3258, 3258
		},
		new String[] {
			"Rigour", "Augury"
		},
		new String[] {
			"(with 74 Prayer)", "(with 77 Prayer)", 
		}
	),
	STRENGTH_WEAPONS(new int[] {
			5, 10, 15, 20, 30, 40, 42, 50, 50, 60, 70, 70, 70, 75
		},
		new int[] {
				3196, 3198, 3200, 3202, 3204, 10887, 8841, 4153, 21646, 6528, 4718, 4747, 13263, 21003
		},
		new String[] {
				"Black halberd", "Mithril halberd", "Adamant halberd", "Rune halberd", "Dragon halberd", "Barrelchest Anchor", "Granite armour", "Granite maul", "Tzhaar-Ket-Om", "Dharok's greataxe", "Torag's hammers", "Abyssal bludgeon", "Primordial boots", "Elder maul"
		},
		
		new String[] {
				"(with 10 Attack)", "(with 20 Attack)", "(with 30 Attack)", "(with 40 Attack)", "(with 60 Attack)", "(with 60 Attack)", "(with 42 combat stats and 22 Prayer)", "(with 50 Attack)", "(with 50 Attack)", "", "(with 70 Attack)", "(with 70 Attack)", "(with 70 Attack)","(with 75 Attack)"
		}
	),
	STRENGTH_ARMOUR(new int[] {
			42, 50, 75
		},
		new int[] {
				8839, 10589, 13239
		},
		new String[] {
				"Void Knight equipment", "Granite armour", "Primordial boots"
		},
		
		new String[] {
				"(with 42 combat stats and 22 Prayer)", "(with 50 Defence)", "(with 75 Defence)"
		}
	),
	STRENGTH_SHORTCUTS(new int[] {
			19, 21, 22, 35, 37, 38, 70,
		},
		new int[] {
			6515, 6515, 6515, 6517, 6517, 6517, 6515, -1
		},
		new String[] {
			"Cross the River Lum to Al Kharid", "Karamja", "Escape from water obelisk island", "Scale the Catherby cliff", "Scale Falador wall", "Scale Yanille wall", "Cross cave, south of Dorgesh-Kaan", ""
		},
		new String[] {
			"(with 8 Agility and 37 Ranged)", "(with 53 Agility and 42 Ranged)", "(with 36 Agility and 39 Ranged)", "(with 32 Agility and 35 Ranged)", "(with 11 Agility and 19 Ranged)", "(with 39 Agility and 21 Ranged)", "(with 70 Agility and 70 strength, after\\ncompletng Death to the Dorgeshuun)\\nand access the Armadyl area of the God\\nWars Dungeon"
		}
	),
	STRENGTH_AREAS(new int[] {
			60, 70
		},
		new int[] {
			11793, 11793
		},
		new String[] {
			"Access the God Wars Dungeon via the", "Enter the Bandos area of the God Wars"
		},
		new String[] {
			"Strength route", "Dungeon"
		}
	),
	STRENGTH_BARBARIAN(new int[] {
			14, 30, 35, 45, 50, 76
		},
		new int[] {
			11328, 11330, 359, 11332, 371, 383
		},
		new String[] {
			/*"To start fishing like a Barbarian, talk to\\nOtto Godblessed when you have at least\\nlevel 48 Fishing and level 15 Agility.",*/ "Leaping trout", "Leaping salmon", "Tuna", "Leaping sturgeon", "Swordfish", "Shark"
		},
		new String[] {
			"(with 15 Agility & 48 Fishing)", "(with 30 Agility & 58 Fishing)", "(with 55 Fishing)", "(with 45 Agility & 70 Fishing)", "(with 70 Fishing)", "(with 96 Fishing)"
		}
	),
	HITPOINTS(new int[] {
			0
		},
		new int[] {
			-1
		},
		new String[] {
			"Hitpoints are used to tell you how\\nhealthy your character is. A character\\nwho reaches 0 Hitpoints has died, but\\nwill reappear in their chosen respawn\\nlocation (normally Lumbridge).\\n\\nIf you see any red 'hit splats' during\\ncombat, the number shown corresponds\\nto the number of Hitpoints lost as a\\nresult of that strike.\\n\\nBlue hit splats mean no damage has\\nbeen dealt.\\n\\nGreen hit splats are poison damage.\\n(Members)\\n\\nDark green hit splats are venom\\ndamage. (Members)\\n\\nOrange hit splats are disease damage.\\n(Members)"
		},
		
		new String[] {
			""
		}
	),
	HEALING(new int[] {
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
		},
		new int[] {
			4561, 319, 315, 2140, 325, 2142, 2309, 347, 3228, 355, 19662, 3381, 333, 339, 7223, 5003, 351, 329, 2325, 361, 7518, 7530, 1993, 2327, 2149, 7178, 7188, 1891, 379, 365, 373, 2289, 2323, 6703, 7054, 1897, 7946, 7198, 2293, 6705, 7056, 3144, 2297, 1883, 385, 7058, 397, 391, 11936, 7060, 7208, 7218, 2301, 13441, -1, 6687, -1
		},
		new String[] {
			"Purple Sweets: Restores 1-3 Hitpoints", "Anchovies: Restores 1 Hitpoint", "Shrimp: Restores 3 Hitpoints", "Cooked chicken: Restores 3 Hitpoints", "Sardine: Restores 3 Hitpoints", "Cooked meat: Restores 3 Hitpoints", "Bread: Restores 5 Hitpoints",
			"Herring: Restores 5 Hitpoints", "Cooked Rabbit: Restores 5 Hitpoints", "Mackerel: Restores 6 Hitpoints", "Botanical Pie: Restores 6 Hitpoints", "Slimy Eel: Restores 6-10 Hitpoints", "Trout: Restores 7 Hitpoints", "Cod: Restores 7 Hitpoints", "Roast Rabbit: Restores 7 Hitpoints",
			"Cave Eel: Restores 7-11 Hitpoints", "Pike: Restores 8 Hitpoints", "Salmon: Restores 9 Hitpoints", "Redberry pie: Restores 9 Hitpoints", "Tuna: Restores 10 Hitpoints", "Crab meat: Restores 10 Hitpoints", "Cooked fishcake: Restores 11 Hitpoints",
			"Jug of wine: Restores 11 Hitpoints", "Meat pie: Restores 11 Hitpoints", "Lava Eel: Restores 11 Hitpoints", "Garden pie: Restores 12 Hitpoints", "Fish pie: Restores 12 Hitpoints", "Cake: Restores 12 Hitpoints", "Lobster: Restores 12 Hitpoints", "Bass: Restores 13 Hitpoints",
			"Swordfish: Restores 14 Hitpoints", "Plain pizza: Restores 14 Hitpoints", "Apple pie: Restores 14 Hitpoints", "Potato with butter: Restores 14\\nHitpoints", "Chilli Potato: Restores 14 Hitpoints", "Chocolate Cake: 15 Hitpoints", "Monkfish: Restores 16 Hitpoints", "Admiral pie: Restores 16 Hitpoints",
			"Meat pizza: Restores 16 Hitpoints", "Potato with cheese: Restores 16 Hitpoints", "Egg Potato: Restores 16 Hitpoints", "Cooked karambwan: Restores 18\\nHitpoints", "Anchovy pizza: Restores 18 Hitpoints", "Ugthanki kebab: Restores 18 Hitpoints", "Shark: Restores 20 Hitpoints",
			"Mushroom Potato: Restores 20 Hitpoints", "Sea Turtle: Restores 21 Hitpoints", "Manta Ray: Restores 22 Hitpoints", "Dark Crab: Restores 22 Hitpoints", "Tuna Potato: Restores 22 Hitpoints", "Wild pie: Restores 22 Hitpoints", "Summer pie: Restores 22 Hitpoints", "Pineapple pizza: Restores 22 Hitpoints", "Anglerfish: Restores Hitpoints based on\\nyour Hitpoints level up to a maximum of\\n22 - can boost beyond your level", "", "Saradomin brew: Restore 15% of your\\nHitpoints level plus 2- can boost\\nbeyond your level"
		},
		
		new String[] {
			"",	
		}
	),
	HITPOINTS_EQUIPMENT(new int[] {
			42, 75
		},
		new int[] {
			8839, 19550
		},
		new String[] {
			"Void Knight equipment", "Enchanted zenty jewellry"
		},
		
		new String[] {
			"(with 42 combat stats and 22 Prayer)"
		}
	),
	RANGING_BOWS(new int[] {
			1, 5, 20, 30, 30, 40, 50, 50, 60, 65, 70, 75
		},
		new int[] {
			841, 843, 849, 853, 4827, 857, 861, 6724, 11235, 12424, 4212, 20997
		},
		new String[] {
			"Standard bows", "Oak bows", "Willow bows", "Maple bows", "Ogre composite bows", "Yew bows", "Magic bows", "Seerculls", "Dark bows", "3rd age bow", "Crystal bows (with 50 Agility)", "Twisted bow"
		},
		new String[] {
			"Ammo: Arrows up to iron", "Ammo: Arrows up to steel", "Ammo: Arrows up to mithril", "Ammo: Arrows up to adamant", "Ammo: 'Brutal' arrows up to rune", "Ammo: Arrows up to rune", "Ammo: Arrows up to amethyst", "Ammo: Arrows up to amethyst", "Ammo: Arrows up to dragon", "Ammo: Arrows up to dragon", "Ammo: None", "Ammo: Arrows up to dragon"
		}
	),
	RANGING_THROWN(new int[] {
			1, 1, 5, 10, 20, 30, 40, 45, 55, 60, 60, 61, 65, 75
		},
		new int[] {
			864, 863, 865, 869, 866, 867, 868, 9976, 9977, 11230, 6522, 20849, 11959, 12926
		},
		new String[] {
			"Bronze items", "Iron items", "Steel items", "Black items", "Mithril items", "Adamanite items", "Rune items", "Chinchompas", "Carnivorous chinchompas", "Dragon darts", "TokTz-Xil-Ul", "Dragon thrownaxes", "Black chinchompas", "Toxic blowpipe"
		},
		new String[] {
			
		}
	),
	RANGING_CROSSBOWS(new int[] {
		1, 1, 20, 20, 20, 25, 30, 30, 40, 40, 40, 40, 40, 40, 40, 40, 42, 50, 50, 50, 50, 60, 60, 60, 60, 65, 70, 70, 70, 70, 70, 70, 75, 75
	},
	new int[] {
		1, 1, 20, 20, 20, 25, 30, 30, 40, 40, 40, 40, 40, 40, 40, 40, 42, 50, 50, 50, 50, 60, 60, 60, 60, 65, 70, 70, 70, 70, 70, 70, 75, 75
	},
	new String[] {
		"Crossbow", "Phoenix crossbow", "Bronze crossbow", "Blurite crossbow", "Iron crossbow", "Dorgeshuun crossbow", "Steel crossbow", "Mithril crossbow", "Adamantite crossbow", "Hunter's crossbow", "Runite crossbow", "Dragon hunter crossbow", "Armadyl crossbow", "Karils crossbow"
	},
	new String[] {
		"Ammo: Bronze crossbow bolts", "Ammo: Bronze crossbow bolts", "Ammo: Bronze crossbow bolts", "Ammo: Bolts up to blurite", "Ammo: Bolts up to iron", "Ammo: Bolts up to iron", "Ammo: Bolts up to steel", "Ammo: Bolts up to mithril", "Ammo: Bolts up to adamant", "Ammo: Kebbit bolts", "Ammo: Bolts up to runite", "Ammo: Bolts up to runite", "Ammo: Bolts up to runite"
	}
	),
	RANGING_ARMOUR(new int[] {
			1, 1, 20, 20, 20, 25, 30, 30, 40, 40, 40, 40, 40, 40, 40, 40, 42, 50, 50, 50, 50, 60, 60, 60, 60, 65, 70, 70, 70, 70, 70, 70, 75, 75
		},
		new int[] {
			1129, 1131, 1133, 1097, 1169, 10954, 6322, 10498, 2577, 2581, 12596, 19994, 6133, 1065, 1099, 1135, 8839, 10499, 2487, 2493, 2499, 10555, 2489, 2495, 2501, 10330, 2491, 2497, 2503, 10370, 11826, 4736, 13237, 21000
		},
		new String[] {
			"Plain leather items", "Hard leather body", "Studded leather body", "Studded leather chaps", "Coif", "Frog-leather", "Snakeskin armour", "Ava's attractor", "Ranger boots", "Robin Hood hat", "Ranger's tunic", "Ranger gloves", "Spinned armour", "Green dragonhide vambraces", "Green dragonhide chaps", "Green dragonhide body", "Void Knight equipment", "Ava's accumulator", "Blue dragonhide vambraces", "Blue dragonhide chaps", "Blue dragonhide body", "Penance skirt", "Red dragonhide vambraces", "Red dragonhide chaps", "Red dragonhide body", "3rd age range armour", "Black dragonhide vambraces", "Black dragonhide chaps", "Black dragonhide body", "God dragonhide armour", "Armadyl armour", "Karils leather armour", "Pegasian boots", "Twisted buckler"
		},
		new String[] {
			"", "(with 10 Defence)", "(with 20 Defence)", "", "", "(with 25 Defence)", "(with 30 Defence)", "", "", "", "", "", "(with 40 Defence)", "", "", "(with 40 Defence)", "(with 42 combat stats and 22 Prayer)", "", "", "", "(with 40 Defence)", "(with 40 Defence)", "", "", "(with 40 Defence)", "(with 45 Defence)", "", "", "(with 40 Defence)", "(with 70 Defence)", "(with 70 Defence)", "(with 70 Defence)", "(with 75 Defence)", "(with 75 Defence)"
		}
	),
	RANGING_MISCELLANEOUS(new int[] {
			42, 65, 75
		},
		new int[] {
			8841, 19478, 19481
		},
		new String[] {
			"Void Knight equipment", "Light ballista", "Heavy ballista"
		},
		new String[] {
			"(with 42 combat stats and 22 Prayer)", "Ammo: All Javalins", "Ammo: All Javalins"
		}
	),
	RANGING_SHORTCUTS(new int[] {
			19, 21, 35, 37, 39, 42, 70
		},
		new int[] {
			6517, 6517, 6517, 6515, 6515, 6515, 6515
		},
		new String[] {
			"Scale Falador wall", "Scale Yanille wall", "Scale the Catherby cliff", "Cross the River Lim to Al Kharid", "Escape from the water obelisk island", "Karamja, south of the volcano", "Cross cave south of Dorgesh-Kaan"
		},
		new String[] {
			"(with 11 Agility and 37 Strength)", "(with 39 Agility and 38 Strength)", "(with 32 Agility and 35 Strength)", "(with 8 Agility and 19 Strength)", "(with 36 Agility and 22 Strength)", "(with 53 Agility and 21 Strength)", "(with 70 Agility and 70 Strength)"
		}
	),
	RANGING_SALAMANDERS(new int[] {
			30, 50, 60, 70
		},
		new int[] {
			10149, 10146, 10147, 10148
		},
		new String[] {
			"Swamp lizard", "Orange salamander", "Red salamander", "Black salamander"
		},
		new String[] {
			"(with 30 Attack, 30 Ranged & 30 Magic)", "(with 50 Attack, 50 Ranged & 50 Magic)", "(with 60 Attack, 60 Ranged & 60 Magic)", "(with 70 Attack, 70 Ranged & 70 Magic)"
		}
	),
	PRAYERS(new int[] {
			1, 4, 7, 8, 9, 10, 13, 16, 19, 22, 25, 26, 27, 28, 31, 34, 37, 40, 43, 44, 45, 46, 49, 52, 55, 60, 70, 74, 77
		},
		new int[] {
			3258, 3258, 3258, 3258, 3258, 3258, 3258, 3258, 3258, 3258, 3258, 3258, 3258, 3258, 3258, 3258, 3258, 3258, 3258, 3258, 3258, 3258, 3258, 3258, 3258, 3258, 3258, 3258, 3258
		},
		new String[] {
			"Thick Skin", "Burst of Strength", "Clarity of Thought", "Sharp Eye", "Mystic Will", "Rock Skin", "Superhuman Strength", "Improved Reflexes", "Rapid Restore", "Rapid Heal", "Protect Item", "Hawk Eye", "Mystic Lore", "Steel Skin", "Ultimate Strength", "Incredible Reflexes", "Protect from Magic", "Protect from Missiles", "Protect from Melee", "Eagle Eye", "Mystic Might", "Retribution", "Redemption", "Smite", "Preserve", "Chivalry", "Piety", "Rigour", "Augury"
		},
		new String[] {
				"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "(with 70 Defence)", "(with 70 Defence)"
		}
	),
	PRAYER_EQUIPMENT(new int[] {
			10, 20, 20, 20, 22, 31, 31, 40, 40, 50, 50, 55, 60, 60, 60, 70, 75
		},
		new int[] {
			5574, 9672, 10458, 10464, 8839, 19997, 12598, 10446, 10452, 1718, 20747, 12829, 10470, 10440, 12831, 12825, 12817
		},
		new String[] {
			"Initiate armour", "Proselyte armour", "Vestment robe top", "Vestment robe legs", "Void Knight equipment", "Holy wraps", "Holy sandals", "Vestment cloak", "Vestment mitre", "Use completed prayer books", "Use bologa's grape blessings.", "Spirit shield", "Vestment stole", "Crozier", "Blessed spirit shield", "Arcane & Spectral spirit shields", "Elysian spirit shield"
		},
		new String[] {
			"(with 20 Defence)", "(with 30 Defence)", "", "", "(with 42 combat stats)", "", "", "", "", "to bless holy and unholy symbols.", "", "(with 45 Defence)", "", "", "(with 70 Defence)", "(with 75 Defence and 65 Magic)", "(with 75 Defence)"
		}
	),
	NORMAL_SPELLS(new int[] {
			1, 3, 5, 7, 9, 11, 13, 15, 17, 19, 20, 21, 23, 25, 27, 29, 31, 33, 35, 37, 39, 40, 41, 43, 45, 47, 49, 50, 50, 51, 53, 55, 56, 57, 58, 59, 60, 60, 60, 60, 61, 62, 63, 64, 65, 66, 66, 68, 69, 70, 73, 74, 75, 79, 80, 80, 82, 85, 85, 87, 90, 93
		},
		new int[] {
			1391, 1391, 1391, 1391, 1391, 1391, 1391, 1391, 1391, 1391, 1391, 1391, 1391, 1391, 1391, 1391, 1391, 1391, 1391, 1391, 1391, 1391, 1391, 1391, 1391, 1391, 1391, 1391, 1391, 1391, 1391, 1391, 1391, 1391, 1391, 1391, 1391, 1391, 1391, 1391, 1391, 1391, 1391, 1391, 1391, 1391, 1391, 1391, 1391, 1391, 1391, 1391, 1391, 1391, 1391, 1391, 1391, 1391, 1391, 1391, 1391, 1391
		},
		new String[] {
			"Wind strike", "Confusion", "Water strike", "Level 1 enchant", "Earth strike", "Weaken", "Fire strike", "Bones to bananas", "Wind bolt", "Curse", "Bind", "Low level alchemy", "Water bolt", "Varrock teleport", "Level 2 enchant", "Earth bolt", "Lumbridge teleport", "Telekinetic grab", "Fire bolt", "Falador teleport", "Crumble undead", "Teleport to house", "Wind blast", "Superheat item", "Camelot teleport", "Water blast", "Level 3 enchant", "Iban blast", "Snare", "Magic dart", "Ardougne teleport", "Earth blast", "High level alchemy", "Charge water orb", "Level 4 enchant", "Watchtower teleport", "Fire blast", "Charge Earth orb", "Bones to peaches", "God spells", "Trollheim teleport", "Wind wave", "Charge fire orb", "Teleport to Ape Atoll", "Water wave", "Charge air orb", "Vulnerability", "Level 5 enchant", "Teleport to Kourend", "Earth wave", "Enfeeble", "Teleother Lumbridge", "Fire wave", "Entangle", "Stun", "Charge", "Teleother Falador", "Tele block", "Teleport to Bounty Target", "Level 6 enchant", "Teleother Camelot", "Level 7 enchant"
		},
		new String[] {
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "Camelot teleport", "", "", "(after Underground Pass)", "", "(with 55 Slayer)", "(after Plague City)", "", "", "", "", "(after Watchtower)", "", "", "", "(after Mage Arena)", "(after Eadgar's Ruse)", "", "", "(after Recipe for Disaster)", "", "", "", "", "(after unlocking the spell)", "", "", "", "", "", "", "(after Mage Arena)", "", "", "", "", "", ""
		}
	),
	ANCIENT_MAGICKS(new int[] {
			50, 52, 54, 56, 58, 60, 62, 64, 66, 68, 70, 72, 74, 76, 78, 80, 82, 84, 85, 86, 88, 90, 92, 94, 96
		},
		new int[] {
			4675, 4675, 4675, 4675, 4675, 4675, 4675, 4675, 4675, 4675, 4675, 4675, 4675, 4675, 4675, 4675, 4675, 4675, 4675, 4675, 4675, 4675, 4675, 4675, 4675
		},
		new String[] {
			"Smoke rush", "Shadow rush", "Paddewwa teleport", "Blood rush", "Ice rush", "Senntisten teleport", "Smoke burst", "Shadow burst", "Kharyll teleport", "Blood burst", "Ice burst", "Lassar teleport", "Smoke blitz", "Shadow blitz", "Dareeyak teleport", "Blood blitz", "Ice blitz", "Carrallanger teleport", "Teleport to Bounty Target", "Smoke barrage", "Shadow barrage", "Annakarl teleport", "Blood barrage", "Ice barrage", "Ghorrock teleport"
	    },
		new String[] {
		}
	),
	LUNAR_SPELLS(new int[] {
			65, 65, 66, 66, 67, 68, 68, 69, 70, 71, 71, 71, 72, 73, 74, 75, 75, 76, 76, 77, 78, 78, 79, 79, 80, 81, 82, 83, 84, 85, 85, 86, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96
		},
		new int[] {
			9084, 9084, 9084, 9084, 9084, 9084, 9084, 9084, 9084, 9084, 9084, 9084, 9084, 9084, 9084, 9084, 9084, 9084, 9084, 9084, 9084, 9084, 9084, 9084, 9084, 9084, 9084, 9084, 9084, 9084, 9084, 9084, 9084, 9084, 9084, 9084, 9084, 9084, 9084, 9084, 9084, 9084, 9084
		},
		new String[] {
			"Bake pie", "Geomancy", "Cure plant", "Monster examine", "NPC Contact", "Cure other", "Humidify", "Moonclan teleport", "Tele group Moonclan", "Cure me", "Hunter kit", "Ourania teleport", "Waterbirth teleport", "Tele group Waterbirth", "Cure group", "Stat spy", "Barbarian teleport", "Tele group Barbarian", "Spin Flax", "Superglass make", "Tan Leather", "Khazard teleport", "Tele group Khazard", "Dream", "String jewellery", "Stat restore pot share", "Magic imbue", "Fertile soil", "Boost potion share", "Fishing guild teleport", "Teleport to Bounty Target", "Tele group fishing guild", "Plank make", "Catherby teleport", "Tele group Catherby", "Recharge Dragonstone", "Ice plateau teleport", "Tele group Ice plateau", "Energy transfer", "Heal other", "Vengeance other", "Vengeance", "Heal group", "Spellbook swap"
	    },
		new String[] {
		}
	),
	ARCEUUS_SPELLS(new int[] {
			3, 6, 7, 12, 16, 17, 19, 21, 22, 26, 28, 30, 34, 37, 40, 40, 43, 46, 48, 52, 57, 61, 62, 65, 69, 71, 72, 78, 78, 83, 85, 90, 93
		},
		new int[] {
			13447, 13578, 13450, 13453, 13456, 13578, 13459, 13462, 13465, 13468, 13578, 13471, 13578, 13474, 13477, 13578, 13480, 13483, 13578, 13486, 13489, 13492, 13495, 13578, 13498, 13578, 13501, 13504, 5370, 13578, 13507, 13578, 13510
		},
		new String[] {
			"Reanimate goblins", "Lumbridge Graveyard teleport", "Reanimate monkeys", "Reanimate imps", "Reanimate minotaurs", "Draynor Manor teleport", "Reanimate scorpions", "Reanimate bears", "Reanimate unicorns", "Reanimate dogs", "Mind Altar teleport", "Reanimate chaos druids", "Respawn teleport", "Reanimate giants", " Sakve Graveyard teleport", "Reanimate ogres", "Reanimate elves", "Reanimate trolls", "Fenkenstrains's Castle teleport", "Reanimate horros", "Reanimate kalphite", "Reanimate dagannoth", "Reanimate bloodveld", "Harmony Island teleport", "Reanimate TzHaar", "Cemetery teleport", "Reanimate demons", "Reanimate aviantese", "Resurrect crops", "Barrows teleport", "Reanimate abyssal demons", "Ape Atoll teleport", "Reanimate dragons"
	    },
		new String[] {
		}
	),
	MAGIC_ARMOUR(new int[] {
			20, 20, 40, 40, 40, 40, 40, 40, 42, 50, 60, 65, 65, 70, 70, 75, 75
		},
		new int[] {
			2579, 13385, 20517, 4091, 7398, 20131, 3387, 6139, 8839, 6916, 2413, 9097, 10338, 12002, 4712, 13235, 21021
		},
		new String[] {
			"Wizard boots", "Xerician armour", "Elder chaos druid robes", "Mystic robes", "Enchanted robes", "Robes of darkness", "Splitbark armour", "Skeletal armour", "Void Knight equipment", "Infinity robes", "God capes", "Lunar armour", "3rd age robes", "Occult necklace", "Ahrims robes", "Eternal boots", "Ancestral robes"
	    },
		new String[] {
			"", "(with 10 Defence)", "", "(with 20 Defence)", "(with 20 Defence)", "(with 20 Defence)", "(with 40 Defence)", "(with 40 Defence)", "(with 42 combat stats and 22 Prayer)", "(with 25 Defence)", "(after Mage Arena)", "(with 40 Defence)", "(with 30 Defence)", "", "(with 70 Defence)", "(with 75 Defence)", "(with 75 Defence)"
		}
	),
	MAGIC_BOLTS(new int[] {
			1, 7, 14, 24, 27, 29, 49, 57, 68, 87
		},
		new int[] {
			9236, 9240, 9237, 9238, 9241, 9239, 9242, 9243, 9244, 9245
		},
		new String[] {
			"Opal-tipped bronze crossbow bolt", "Saphire-tipped mithril crossbow bolt", "Jade-tipped blurite crossbow bolt", "Pearl-tipped iron crossbow bolt", "Emerald-tipped mithril crossbow bolt", "Red topaz-tipped steel crossbow bolt", "Ruby-tipped adamant crossbow bolt", "Diamond-tipped adamant crossbow bolt", "Dragonstone-tipped rune crossbow bolt", "Onyx-tipped rune crossbow bolt"
	    },
		new String[] {
		}
	),
	MAGIC_WEAPONS(new int[] {
			30, 40, 42, 45, 50, 50, 50, 50, 55, 60, 60, 60, 65, 70, 75, 75, 75, 75
		},
		new int[] {
			1391, 1405, 8841, 6908, 6910, 4675, 4170, 6912, 6914, 2416, 6526, 12422, 4710, 11791, 11905, 21006, 21255
		},
		new String[] {
			"Battlestaves (with 30 Attack)", "Mystic staves (with 40 Attack)", "Void Knight equipment", "Beginner wand", "Apprentice wand", "Ancient staff", "Slayer's staff", "Teacher wand", "Master wand", "God staves", "Toktz-Mej-Tal", "3rd age wand", "Ahrim's staff", "Staff of the Dead (with 75 Attack)", "Trident of the Seas", "Kodai Wand", "Slayer's staff (e)"	
	    },
		new String[] {
			"", "", "(with 42 combat stats and 22 Prayer)", "", "", "(after Desert Treasure, with 50 Attack)", "(with 55 Slayer)", "", "", "(after Mage Arena)", "(with 60 Attack)", "", "(with 70 Attack)", "", "", "", "(with 55 Slayer)"
		}
	),
	MAGIC_EQUIPMENT(new int[] {
			50, 55, 70
		},
		new int[] {
			20714, 21633, 21633
		},
		new String[] {
			"Tome of Fire", "Make Ancient Wyvern shield", "Wield Ancient Wyvern shield"	
	    },
		new String[] {
		}
	),
	MAGIC_SALAMANDERS(new int[] {
			30, 50, 60, 70
		},
		new int[] {
			10149, 10146, 10147, 10148
		},
		new String[] {
			"Swamp lizard", "Orange salamander", "Red salamander", "Black salamander"
		},
		new String[] {
			"(with 30 Attack, 30 Ranged & 30 Magic)", "(with 50 Attack, 50 Ranged & 50 Magic)", "(with 60 Attack, 60 Ranged & 60 Magic)", "(with 70 Attack, 70 Ranged & 70 Magic)"
		}
	),
	RUNECRAFTING_RUNES(new int[] {
			1, 2, 5, 6, 9, 10, 13, 14, 15, 19, 20, 23, 27, 35, 40, 44, 54, 65, 77, 90
		},
		new int[] {
			556, 558, 555, 4695, 557, 4695, 4698, 554, 4697, 4694, 559, 4699, 564, 562, 9075, 561, 563, 560, 565, 566
		},
		new String[] {
			"Air runes", "Mind runes", "Water runes", "Mist runes", "Eart runes", "Dust runes", "Mud runes", "Fire runes", "Smoke runes", "Steam runes", "Body runes", "Lava runes", "Cosmic runes", "Chaos runes", "Astral runes", "Nature runes", "Law runes", "Death runes", "Blood runes", "Soul runes"
		},
		new String[] {
		}
	),
	RUNECRAFTING_MULTIPLE_RUNES(new int[] {
			11, 14, 19, 22, 26, 28, 33, 35, 38, 42, 44, 46, 52, 55, 56, 57, 59, 66, 70, 70, 74, 76, 77, 78, 82, 84, 88, 91, 92, 95, 95, 98, 99, 99
		},
		new int[] {
			556, 558, 555, 556, 557, 558, 556, 554, 555, 558, 556, 559, 557, 556, 558, 555, 564, 556, 558, 554, 562, 555, 556, 557, 9075, 558, 556, 561, 559, 555, 563, 558, 556, 560
		},
		new String[] {
			"2 Air runes per essence", "2 Mind runes per essence", "2 Water runes per essence", "3 Air runes per essence", "2 Earth runes per essence", "3 Mind runes per essence", "4 Air runes per essence", "2 Fire runes per essence", "3 Water runes per essence", "4 Mind runes per essence", "5 Air runes per essence", "2 Body runes per essence", "3 Earth runes per essence", "6 Air runes per essence", "5 Mind runes per essence", "4 Water runes per essence", "2 Cosmic runes per essence", "7 Air runes per essence", "6 Mind runes per essence", "3 Fire runes per essence", "2 Chaos runes per essence", "5 Water runes per essence", "8 Air runes per essence", "4 Earth runes per essence", "2 Astral runes per essence", "7 Mind runes per essence", "9 Air runes per essence", "2 Nature runes per essence", "3 Body runes per essence", "6 Water runes per essence", "2 Law runes per essence", "8 Mind runes per essence", "10 Air runes per essence", "2 Death runes per essence"
		},
		new String[] {
		}
	),
	RUNECRAFTING_RUNE_POUCHES(new int[] {
			11, 14, 19, 22
		},
		new int[] {
			5509, 5510, 5512, 5514
		},
		new String[] {
			"Small pouch: Holds 3 extra essence", "Medium poch: Hold 6 extra essence", "Large poch: Hold 9 extra essence", "Giant poch: Hold 12 extra essence"
		},
		new String[] {
		}
	),
	RUNECRAFTING_INFUSING(new int[] {
			60, 60, 60
		},
		new int[] {
			13235, 13237, 13239
		},
		new String[] {
			"Eternal boots (with 60 Magic)", "Pegasian boots (with 60 Magic)", "Primordial boots (with 60 Magic)"
		},
		new String[] {
		}
	),
	CONSTRUCTION_ROOMS(new int[] {
			1, 1, 5, 10, 15, 20, 25, 30, 32, 35, 37, 40, 42, 45, 50, 55, 60, 65, 65, 70, 75, 80
		},
		new int[] {
			8170, 8395, 8396, 8397, 8406, 8398, 8401, 8399, 8400, 8403, 12725, 8407, 9842, 8405, 8408, 8416, 8409, 8410, 20653, 8411, 8414, 20654
		},
		new String[] {
			"Garden", "Parlour", "Kitchen", "Dining room", "Workshop", "Bedroom", "Hall (skill trophies)", "Games room", "Combat room", "Hall (quest trophies)", "Menagerie", "Study", "Costume room", "Chapel", "Portal chamber", "Formal garden", "Throne room", "Oubliette" ,"Superior garden", "Dungeon", "Treasure room", "Achievement Gallery"
		},
		new String[] {
		}
	),
	THIEVING_PICKPOCKET(new int[] {
			1, 10, 15, 20, 25, 32, 36, 38, 40, 45, 45, 53, 55, 55, 65, 65, 70, 75, 80, 85
		},
		new int[] {
			3241, 3243, 4295, 4297, 3245, 3247, 10998, 5068, 3249, 3686, 6781, 4625, 3251, 6782, 3253, 6780, 3255, 3257, 3259, 6105
		},
		new String[] {
			"Man", "Farmer", "Female H.A.M. follower", "Male H.A.M. follower", "Warrior", "Rogue", "Cave goblin", "Master farmer", "Guard", "Fremennik",
			"Bearded Pollnivnian bandit", "Desert bandit", "Knight", "Pollnivnian bandit", "Watchman", "Menaphite thug", "Paladin", "Gnome", "Hero", "Elf"
		},
		new String[] {
			
		}
	),;
	
	private int[] levels;
	private int[] itemIds;
	private String[] descriptions1;
	private String[] descriptions2;

	private SkillGuideContent(int[] levels, int[] itemIds, String[] descriptions1, String[] descriptions2) {
		this.levels = levels;
		this.itemIds = itemIds;
		this.descriptions1 = descriptions1;
		this.descriptions2 = descriptions2;
	}

	public int[] getLevels() {
		return levels;
	}

	public int[] getItemIds() {
		return itemIds;
	}

	public String[] getDescriptions1() {
		return descriptions1;
	}
	
	public String[] getDescriptions2() {
		return descriptions2;
	}
	
}

package com.model.game.character.player.skill.smithing;

import com.model.game.character.player.Player;
import com.model.game.character.player.Skills;
import com.model.game.character.player.packets.encode.impl.SendActionInterface;
import com.model.game.character.player.packets.encode.impl.SendString;
import com.model.game.character.player.packets.encode.impl.SendInterface;



public class SmithingInterface {

	Player player;

	public SmithingInterface(Player player) {
		this.player = player;
	}

	public void showSmithInterface(int itemId) {
		if (itemId == 2349)
			makeBronzeInterface(player);
		else if (itemId == 2351)
			makeIronInterface(player);
		else if (itemId == 2353)
			makeSteelInterface(player);
		else if (itemId == 2359)
			makeMithInterface(player);
		else if (itemId == 2361)
			makeAddyInterface(player);
		else if (itemId == 2363)
			makeRuneInterface(player);

	}

	private void makeRuneInterface(Player player) {
		String fiveb = GetForBars(2363, 5, player);
		String threeb = GetForBars(2363, 3, player);
		String twob = GetForBars(2363, 2, player);
		String oneb = GetForBars(2363, 1, player);
		player.write(new SendString(fiveb + "5 Bars" + fiveb, 1112));
		player.write(new SendString(threeb + "3 Bars" + threeb, 1109));
		player.write(new SendString(threeb + "3 Bars" + threeb, 1110));
		player.write(new SendString(threeb + "3 Bars" + threeb, 1118));
		player.write(new SendString(threeb + "3 Bars" + threeb, 1111));
		player.write(new SendString(threeb + "3 Bars" + threeb, 1095));
		player.write(new SendString(threeb + "3 Bars" + threeb, 1115));
		player.write(new SendString(threeb + "3 Bars" + threeb, 1090));
		player.write(new SendString(twob + "2 Bars" + twob, 1113));
		player.write(new SendString(twob + "2 Bars" + twob, 1116));
		player.write(new SendString(twob + "2 Bars" + twob, 1114));
		player.write(new SendString(twob + "2 Bars" + twob, 1089));
		player.write(new SendString(twob + "2 Bars" + twob, 8428));
		player.write(new SendString(oneb + "1 Bar" + oneb, 1124));
		player.write(new SendString(oneb + "1 Bar" + oneb, 1125));
		player.write(new SendString(oneb + "1 Bar" + oneb, 1126));
		player.write(new SendString(oneb + "1 Bar" + oneb, 1127));
		player.write(new SendString(oneb + "1 Bar" + oneb, 1128));
		player.write(new SendString(oneb + "1 Bar" + oneb, 1129));
		player.write(new SendString(oneb + "1 Bar" + oneb, 1130));
		player.write(new SendString(oneb + "1 Bar" + oneb, 1131));
		player.write(new SendString(oneb + "1 Bar" + oneb, 13357));
		player.write(new SendString(oneb + "1 Bar" + oneb, 11459));
		player.write(new SendString(
				GetForlvl(88, player) + "Plate Body" + GetForlvl(18, player), 1101));
		player.write(new SendString(
				GetForlvl(99, player) + "Plate Legs" + GetForlvl(16, player), 1099));
		player.write(new SendString(
				GetForlvl(99, player) + "Plate Skirt" + GetForlvl(16, player), 1100));
		player.write(new SendString(
				GetForlvl(99, player) + "2 Hand Sword" + GetForlvl(14, player), 1088));
		player.write(new SendString(
				GetForlvl(97, player) + "Kite Shield" + GetForlvl(12, player), 1105));
		player.write(new SendString(
				GetForlvl(96, player) + "Chain Body" + GetForlvl(11, player), 1098));
		player.write(new SendString(
				GetForlvl(95, player) + "Battle Axe" + GetForlvl(10, player), 1092));
		player.write(new SendString(
				GetForlvl(94, player) + "Warhammer" + GetForlvl(9, player), 1083));
		player.write(new SendString(
				GetForlvl(93, player) + "Square Shield" + GetForlvl(8, player), 1104));
		player.write(new SendString(
				GetForlvl(92, player) + "Full Helm" + GetForlvl(7, player), 1103));
		player.write(new SendString(
				GetForlvl(92, player) + "Throwing Knives" + GetForlvl(7, player), 1106));
		player.write(new SendString(
				GetForlvl(91, player) + "Long Sword" + GetForlvl(6, player), 1086));
		player.write(new SendString(GetForlvl(90, player) + "Scimitar" + GetForlvl(5, player),
				1087));
		player.write(new SendString(
				GetForlvl(90, player) + "Arrowtips" + GetForlvl(5, player), 1108));
		player.write(new SendString(GetForlvl(89, player) + "Sword" + GetForlvl(4, player),
				1085));
		player.write(new SendString(GetForlvl(89, player) + "Bolts" + GetForlvl(4, player),
				9144));
		player.write(new SendString(GetForlvl(89, player) + "Nails" + GetForlvl(4, player),
				13358));
		player.write(new SendString(
				GetForlvl(88, player) + "Medium Helm" + GetForlvl(3, player), 1102));
		player.write(new SendString(GetForlvl(87, player) + "Mace" + GetForlvl(2, player),
				1093));
		player.write(new SendString(GetForlvl(85, player) + "Dagger" + GetForlvl(1, player),
				1094));
		player.write(new SendString(GetForlvl(86, player) + "Axe" + GetForlvl(1, player), 1091));
		player.write(new SendActionInterface(1213, 0, 1119, 1)); // dagger
		player.write(new SendActionInterface(1359, 0, 1120, 1)); // axe
		player.write(new SendActionInterface(1113, 0, 1121, 1)); // chain body
		player.write(new SendActionInterface(1147, 0, 1122, 1)); // med helm
		player.write(new SendActionInterface(9144, 0, 1123, 10)); // Bolts
		player.write(new SendActionInterface(1289, 1, 1119, 1)); // s-sword
		player.write(new SendActionInterface(1432, 1, 1120, 1)); // mace
		player.write(new SendActionInterface(1079, 1, 1121, 1)); // platelegs
		player.write(new SendActionInterface(1163, 1, 1122, 1)); // full helm
		player.write(new SendActionInterface(44, 1, 1123, 15)); // arrowtips
		player.write(new SendActionInterface(1333, 2, 1119, 1)); // scimmy
		player.write(new SendActionInterface(1347, 2, 1120, 1)); // warhammer
		player.write(new SendActionInterface(1093, 2, 1121, 1)); // plateskirt
		player.write(new SendActionInterface(1185, 2, 1122, 1)); // Sq. Shield
		player.write(new SendActionInterface(868, 2, 1123, 5)); // throwing-knives
		player.write(new SendActionInterface(1303, 3, 1119, 1)); // longsword
		player.write(new SendActionInterface(1373, 3, 1120, 1)); // battleaxe
		player.write(new SendActionInterface(1127, 3, 1121, 1)); // platebody
		player.write(new SendActionInterface(1201, 3, 1122, 1)); // kiteshield
		player.write(new SendActionInterface(1319, 4, 1119, 1)); // 2h sword
		player.write(new SendActionInterface(4824, 4, 1122, 15)); // nails
		player.write(new SendActionInterface(-1, 3, 1123, 1));
		player.write(new SendString("", 1135));
		player.write(new SendString("", 1134));
		player.write(new SendString("", 11461));
		player.write(new SendString("", 11459));
		player.write(new SendString("", 1132));
		player.write(new SendString("", 1096));
		player.write(new SendInterface(994));
	}

	private void makeAddyInterface(Player player) {
		String fiveb = GetForBars(2361, 5, player);
		String threeb = GetForBars(2361, 3, player);
		String twob = GetForBars(2361, 2, player);
		String oneb = GetForBars(2361, 1, player);
		player.write(new SendString(fiveb + "5 Bars" + fiveb, 1112));
		player.write(new SendString(threeb + "3 Bars" + threeb, 1109));
		player.write(new SendString(threeb + "3 Bars" + threeb, 1110));
		player.write(new SendString(threeb + "3 Bars" + threeb, 1118));
		player.write(new SendString(threeb + "3 Bars" + threeb, 1111));
		player.write(new SendString(threeb + "3 Bars" + threeb, 1095));
		player.write(new SendString(threeb + "3 Bars" + threeb, 1115));
		player.write(new SendString(threeb + "3 Bars" + threeb, 1090));
		player.write(new SendString(twob + "2 Bars" + twob, 1113));
		player.write(new SendString(twob + "2 Bars" + twob, 1116));
		player.write(new SendString(twob + "2 Bars" + twob, 1114));
		player.write(new SendString(twob + "2 Bars" + twob, 1089));
		player.write(new SendString(twob + "2 Bars" + twob, 8428));
		player.write(new SendString(oneb + "1 Bar" + oneb, 1124));
		player.write(new SendString(oneb + "1 Bar" + oneb, 1125));
		player.write(new SendString(oneb + "1 Bar" + oneb, 1126));
		player.write(new SendString(oneb + "1 Bar" + oneb, 1127));
		player.write(new SendString(oneb + "1 Bar" + oneb, 1128));
		player.write(new SendString(oneb + "1 Bar" + oneb, 1129));
		player.write(new SendString(oneb + "1 Bar" + oneb, 1130));
		player.write(new SendString(oneb + "1 Bar" + oneb, 1131));
		player.write(new SendString(oneb + "1 Bar" + oneb, 13357));
		player.write(new SendString(oneb + "1 Bar" + oneb, 11459));
		player.write(new SendString(
				GetForlvl(88, player) + "Plate Body" + GetForlvl(18, player), 1101));
		player.write(new SendString(
				GetForlvl(86, player) + "Plate Legs" + GetForlvl(16, player), 1099));
		player.write(new SendString(
				GetForlvl(86, player) + "Plate Skirt" + GetForlvl(16, player), 1100));
		player.write(new SendString(
				GetForlvl(84, player) + "2 Hand Sword" + GetForlvl(14, player), 1088));
		player.write(new SendString(
				GetForlvl(82, player) + "Kite Shield" + GetForlvl(12, player), 1105));
		player.write(new SendString(
				GetForlvl(81, player) + "Chain Body" + GetForlvl(11, player), 1098));
		player.write(new SendString(
				GetForlvl(80, player) + "Battle Axe" + GetForlvl(10, player), 1092));
		player.write(new SendString(
				GetForlvl(79, player) + "Warhammer" + GetForlvl(9, player), 1083));
		player.write(new SendString(
				GetForlvl(78, player) + "Square Shield" + GetForlvl(8, player), 1104));
		player.write(new SendString(
				GetForlvl(77, player) + "Full Helm" + GetForlvl(7, player), 1103));
		player.write(new SendString(
				GetForlvl(77, player) + "Throwing Knives" + GetForlvl(7, player), 1106));
		player.write(new SendString(
				GetForlvl(76, player) + "Long Sword" + GetForlvl(6, player), 1086));
		player.write(new SendString(GetForlvl(75, player) + "Scimitar" + GetForlvl(5, player),
				1087));
		player.write(new SendString(
				GetForlvl(75, player) + "Arrowtips" + GetForlvl(5, player), 1108));
		player.write(new SendString(GetForlvl(74, player) + "Sword" + GetForlvl(4, player),
				1085));
		player.write(new SendString(GetForlvl(74, player) + "Bolts" + GetForlvl(4, player),
				9143));
		player.write(new SendString(GetForlvl(74, player) + "Nails" + GetForlvl(4, player),
				13358));
		player.write(new SendString(
				GetForlvl(73, player) + "Medium Helm" + GetForlvl(3, player), 1102));
		player.write(new SendString(GetForlvl(72, player) + "Mace" + GetForlvl(2, player),
				1093));
		player.write(new SendString(GetForlvl(70, player) + "Dagger" + GetForlvl(1, player),
				1094));
		player.write(new SendString(GetForlvl(71, player) + "Axe" + GetForlvl(1, player), 1091));
		player.write(new SendActionInterface(1211, 0, 1119, 1)); // dagger
		player.write(new SendActionInterface(1357, 0, 1120, 1)); // axe
		player.write(new SendActionInterface(1111, 0, 1121, 1)); // chain body
		player.write(new SendActionInterface(1145, 0, 1122, 1)); // med helm
		player.write(new SendActionInterface(9143, 0, 1123, 10)); // Bolts
		player.write(new SendActionInterface(1287, 1, 1119, 1)); // s-sword
		player.write(new SendActionInterface(1430, 1, 1120, 1)); // mace
		player.write(new SendActionInterface(1073, 1, 1121, 1)); // platelegs
		player.write(new SendActionInterface(1161, 1, 1122, 1)); // full helm
		player.write(new SendActionInterface(43, 1, 1123, 15)); // arrowtips
		player.write(new SendActionInterface(1331, 2, 1119, 1)); // scimmy
		player.write(new SendActionInterface(1345, 2, 1120, 1)); // warhammer
		player.write(new SendActionInterface(1091, 2, 1121, 1)); // plateskirt
		player.write(new SendActionInterface(1183, 2, 1122, 1)); // Sq. Shield
		player.write(new SendActionInterface(867, 2, 1123, 5)); // throwing-knives
		player.write(new SendActionInterface(1301, 3, 1119, 1)); // longsword
		player.write(new SendActionInterface(1371, 3, 1120, 1)); // battleaxe
		player.write(new SendActionInterface(1123, 3, 1121, 1)); // platebody
		player.write(new SendActionInterface(1199, 3, 1122, 1)); // kiteshield
		player.write(new SendActionInterface(1317, 4, 1119, 1)); // 2h sword
		player.write(new SendActionInterface(4823, 4, 1122, 15)); // nails
		player.write(new SendActionInterface(-1, 3, 1123, 1));
		player.write(new SendString("", 1135));
		player.write(new SendString("", 1134));
		player.write(new SendString("", 11461));
		player.write(new SendString("", 11459));
		player.write(new SendString("", 1132));
		player.write(new SendString("", 1096));
		player.write(new SendInterface(994));
	}

	private void makeMithInterface(Player player) {
		String fiveb = GetForBars(2359, 5, player);
		String threeb = GetForBars(2359, 3, player);
		String twob = GetForBars(2359, 2, player);
		String oneb = GetForBars(2359, 1, player);
		player.write(new SendString(fiveb + "5 Bars" + fiveb, 1112));
		player.write(new SendString(threeb + "3 Bars" + threeb, 1109));
		player.write(new SendString(threeb + "3 Bars" + threeb, 1110));
		player.write(new SendString(threeb + "3 Bars" + threeb, 1118));
		player.write(new SendString(threeb + "3 Bars" + threeb, 1111));
		player.write(new SendString(threeb + "3 Bars" + threeb, 1095));
		player.write(new SendString(threeb + "3 Bars" + threeb, 1115));
		player.write(new SendString(threeb + "3 Bars" + threeb, 1090));
		player.write(new SendString(twob + "2 Bars" + twob, 1113));
		player.write(new SendString(twob + "2 Bars" + twob, 1116));
		player.write(new SendString(twob + "2 Bars" + twob, 1114));
		player.write(new SendString(twob + "2 Bars" + twob, 1089));
		player.write(new SendString(twob + "2 Bars" + twob, 8428));
		player.write(new SendString(oneb + "1 Bar" + oneb, 1124));
		player.write(new SendString(oneb + "1 Bar" + oneb, 1125));
		player.write(new SendString(oneb + "1 Bar" + oneb, 1126));
		player.write(new SendString(oneb + "1 Bar" + oneb, 1127));
		player.write(new SendString(oneb + "1 Bar" + oneb, 1128));
		player.write(new SendString(oneb + "1 Bar" + oneb, 1129));
		player.write(new SendString(oneb + "1 Bar" + oneb, 1130));
		player.write(new SendString(oneb + "1 Bar" + oneb, 1131));
		player.write(new SendString(oneb + "1 Bar" + oneb, 13357));
		player.write(new SendString(oneb + "1 Bar" + oneb, 11459));
		player.write(new SendString(
				GetForlvl(68, player) + "Plate Body" + GetForlvl(18, player), 1101));
		player.write(new SendString(
				GetForlvl(66, player) + "Plate Legs" + GetForlvl(16, player), 1099));
		player.write(new SendString(
				GetForlvl(66, player) + "Plate Skirt" + GetForlvl(16, player), 1100));
		player.write(new SendString(
				GetForlvl(64, player) + "2 Hand Sword" + GetForlvl(14, player), 1088));
		player.write(new SendString(
				GetForlvl(62, player) + "Kite Shield" + GetForlvl(12, player), 1105));
		player.write(new SendString(
				GetForlvl(61, player) + "Chain Body" + GetForlvl(11, player), 1098));
		player.write(new SendString(
				GetForlvl(60, player) + "Battle Axe" + GetForlvl(10, player), 1092));
		player.write(new SendString(
				GetForlvl(59, player) + "Warhammer" + GetForlvl(9, player), 1083));
		player.write(new SendString(
				GetForlvl(58, player) + "Square Shield" + GetForlvl(8, player), 1104));
		player.write(new SendString(
				GetForlvl(57, player) + "Full Helm" + GetForlvl(7, player), 1103));
		player.write(new SendString(
				GetForlvl(57, player) + "Throwing Knives" + GetForlvl(7, player), 1106));
		player.write(new SendString(
				GetForlvl(56, player) + "Long Sword" + GetForlvl(6, player), 1086));
		player.write(new SendString(GetForlvl(55, player) + "Scimitar" + GetForlvl(5, player),
				1087));
		player.write(new SendString(
				GetForlvl(55, player) + "Arrowtips" + GetForlvl(5, player), 1108));
		player.write(new SendString(GetForlvl(54, player) + "Sword" + GetForlvl(4, player),
				1085));
		player.write(new SendString(GetForlvl(54, player) + "Bolts" + GetForlvl(4, player),
				9142));
		player.write(new SendString(GetForlvl(54, player) + "Nails" + GetForlvl(4, player),
				13358));
		player.write(new SendString(
				GetForlvl(53, player) + "Medium Helm" + GetForlvl(3, player), 1102));
		player.write(new SendString(GetForlvl(52, player) + "Mace" + GetForlvl(2, player),
				1093));
		player.write(new SendString(GetForlvl(50, player) + "Dagger" + GetForlvl(1, player),
				1094));
		player.write(new SendString(GetForlvl(51, player) + "Axe" + GetForlvl(1, player), 1091));
		player.write(new SendActionInterface(1209, 0, 1119, 1)); //dagger
		player.write(new SendActionInterface(1355, 0, 1120, 1)); // axe
		player.write(new SendActionInterface(1109, 0, 1121, 1)); // chain body
		player.write(new SendActionInterface(1143, 0, 1122, 1)); // med helm
		player.write(new SendActionInterface(9142, 0, 1123, 10)); // Bolts
		player.write(new SendActionInterface(1285, 1, 1119, 1)); // s-sword
		player.write(new SendActionInterface(1428, 1, 1120, 1)); // mace
		player.write(new SendActionInterface(1071, 1, 1121, 1)); // platelegs
		player.write(new SendActionInterface(1159, 1, 1122, 1)); // full helm
		player.write(new SendActionInterface(42, 1, 1123, 15)); // arrowtips
		player.write(new SendActionInterface(1329, 2, 1119, 1)); // scimmy
		player.write(new SendActionInterface(1343, 2, 1120, 1)); // warhammer
		player.write(new SendActionInterface(1085, 2, 1121, 1)); // plateskirt
		player.write(new SendActionInterface(1181, 2, 1122, 1)); // Sq. Shield
		player.write(new SendActionInterface(866, 2, 1123, 5)); // throwing-knives
		player.write(new SendActionInterface(1299, 3, 1119, 1)); // longsword
		player.write(new SendActionInterface(1369, 3, 1120, 1)); // battleaxe
		player.write(new SendActionInterface(1121, 3, 1121, 1)); // platebody
		player.write(new SendActionInterface(1197, 3, 1122, 1)); // kiteshield
		player.write(new SendActionInterface(1315, 4, 1119, 1)); // 2h sword
		player.write(new SendActionInterface(4822, 4, 1122, 15)); // nails
		player.write(new SendActionInterface(-1, 3, 1123, 1));
		player.write(new SendString("", 1135));
		player.write(new SendString("", 1134));
		player.write(new SendString("", 11461));
		player.write(new SendString("", 11459));
		player.write(new SendString("", 1132));
		player.write(new SendString("", 1096));
		player.write(new SendInterface(994));
	}

	private void makeSteelInterface(Player player) {
		String fiveb = GetForBars(2353, 5, player);
		String threeb = GetForBars(2353, 3, player);
		String twob = GetForBars(2353, 2, player);
		String oneb = GetForBars(2353, 1, player);
		player.write(new SendString(fiveb + "5 Bars" + fiveb, 1112));
		player.write(new SendString(threeb + "3 Bars" + threeb, 1109));
		player.write(new SendString(threeb + "3 Bars" + threeb, 1110));
		player.write(new SendString(threeb + "3 Bars" + threeb, 1118));
		player.write(new SendString(threeb + "3 Bars" + threeb, 1111));
		player.write(new SendString(threeb + "3 Bars" + threeb, 1095));
		player.write(new SendString(threeb + "3 Bars" + threeb, 1115));
		player.write(new SendString(threeb + "3 Bars" + threeb, 1090));
		player.write(new SendString(twob + "2 Bars" + twob, 1113));
		player.write(new SendString(twob + "2 Bars" + twob, 1116));
		player.write(new SendString(twob + "2 Bars" + twob, 1114));
		player.write(new SendString(twob + "2 Bars" + twob, 1089));
		player.write(new SendString(twob + "2 Bars" + twob, 8428));
		player.write(new SendString(oneb + "1 Bar" + oneb, 1124));
		player.write(new SendString(oneb + "1 Bar" + oneb, 1125));
		player.write(new SendString(oneb + "1 Bar" + oneb, 1126));
		player.write(new SendString(oneb + "1 Bar" + oneb, 1127));
		player.write(new SendString(oneb + "1 Bar" + oneb, 1128));
		player.write(new SendString(oneb + "1 Bar" + oneb, 1129));
		player.write(new SendString(oneb + "1 Bar" + oneb, 1130));
		player.write(new SendString(oneb + "1 Bar" + oneb, 1131));
		player.write(new SendString(oneb + "1 Bar" + oneb, 13357));
		player.write(new SendString(oneb + "1 Bar" + oneb, 1132));
		player.write(new SendString(oneb + "1 Bar" + oneb, 1135));
		player.write(new SendString("", 11459));
		player.write(new SendString(
				GetForlvl(48, player) + "Plate Body" + GetForlvl(18, player), 1101));
		player.write(new SendString(
				GetForlvl(46, player) + "Plate Legs" + GetForlvl(16, player), 1099));
		player.write(new SendString(
				GetForlvl(46, player) + "Plate Skirt" + GetForlvl(16, player), 1100));
		player.write(new SendString(
				GetForlvl(44, player) + "2 Hand Sword" + GetForlvl(14, player), 1088));
		player.write(new SendString(
				GetForlvl(42, player) + "Kite Shield" + GetForlvl(12, player), 1105));
		player.write(new SendString(
				GetForlvl(41, player) + "Chain Body" + GetForlvl(11, player), 1098));
		player.write(new SendString("", 11461));
		player.write(new SendString(
				GetForlvl(40, player) + "Battle Axe" + GetForlvl(10, player), 1092));
		player.write(new SendString(
				GetForlvl(39, player) + "Warhammer" + GetForlvl(9, player), 1083));
		player.write(new SendString(
				GetForlvl(38, player) + "Square Shield" + GetForlvl(8, player), 1104));
		player.write(new SendString(
				GetForlvl(37, player) + "Full Helm" + GetForlvl(7, player), 1103));
		player.write(new SendString(
				GetForlvl(37, player) + "Throwing Knives" + GetForlvl(7, player), 1106));
		player.write(new SendString(
				GetForlvl(36, player) + "Long Sword" + GetForlvl(6, player), 1086));
		player.write(new SendString(GetForlvl(35, player) + "Scimitar" + GetForlvl(5, player),
				1087));
		player.write(new SendString(
				GetForlvl(35, player) + "Arrowtips" + GetForlvl(5, player), 1108));
		player.write(new SendString(GetForlvl(34, player) + "Sword" + GetForlvl(4, player),
				1085));
		player.write(new SendString(GetForlvl(34, player) + "Bolts" + GetForlvl(4, player),
				9141));
		player.write(new SendString(GetForlvl(34, player) + "Nails" + GetForlvl(4, player),
				13358));
		player.write(new SendString(
				GetForlvl(33, player) + "Medium Helm" + GetForlvl(3, player), 1102));
		player.write(new SendString(GetForlvl(32, player) + "Mace" + GetForlvl(2, player),
				1093));
		player.write(new SendString(GetForlvl(30, player) + "Dagger" + GetForlvl(1, player),
				1094));
		player.write(new SendString(GetForlvl(31, player) + "Axe" + GetForlvl(1, player), 1091));
		player.write(new SendString(
				GetForlvl(35, player) + "Cannon Ball" + GetForlvl(35, player), 1096));
		player.write(new SendString(GetForlvl(36, player) + "Studs" + GetForlvl(36, player),
				1134));
		player.write(new SendActionInterface(1207, 0, 1119, 1));
		player.write(new SendActionInterface(1353, 0, 1120, 1));
		player.write(new SendActionInterface(1105, 0, 1121, 1));
		player.write(new SendActionInterface(1141, 0, 1122, 1));
		player.write(new SendActionInterface(9141, 0, 1123, 10));
		player.write(new SendActionInterface(1281, 1, 1119, 1));
		player.write(new SendActionInterface(1424, 1, 1120, 1));
		player.write(new SendActionInterface(1069, 1, 1121, 1));
		player.write(new SendActionInterface(1157, 1, 1122, 1));
		player.write(new SendActionInterface(41, 1, 1123, 15));
		player.write(new SendActionInterface(1325, 2, 1119, 1));
		player.write(new SendActionInterface(1339, 2, 1120, 1));
		player.write(new SendActionInterface(1083, 2, 1121, 1));
		player.write(new SendActionInterface(1177, 2, 1122, 1));
		player.write(new SendActionInterface(865, 2, 1123, 5));
		player.write(new SendActionInterface(1295, 3, 1119, 1));
		player.write(new SendActionInterface(1365, 3, 1120, 1));
		player.write(new SendActionInterface(1119, 3, 1121, 1));
		player.write(new SendActionInterface(1193, 3, 1122, 1));
		player.write(new SendActionInterface(1311, 4, 1119, 1));
		player.write(new SendActionInterface(1539, 4, 1122, 15));
		player.write(new SendActionInterface(2, 3, 1123, 4));
		player.write(new SendActionInterface(2370, 4, 1123, 1));
		player.write(new SendInterface(994));
	}

	private void makeIronInterface(Player player) {
		String fiveb = GetForBars(2351, 5, player);
		String threeb = GetForBars(2351, 3, player);
		String twob = GetForBars(2351, 2, player);
		String oneb = GetForBars(2351, 1, player);
		player.write(new SendString(fiveb + "5 Bars" + fiveb, 1112));
		player.write(new SendString(threeb + "3 Bars" + threeb, 1109));
		player.write(new SendString(threeb + "3 Bars" + threeb, 1110));
		player.write(new SendString(threeb + "3 Bars" + threeb, 1118));
		player.write(new SendString(threeb + "3 Bars" + threeb, 1111));
		player.write(new SendString(threeb + "3 Bars" + threeb, 1095));
		player.write(new SendString(threeb + "3 Bars" + threeb, 1115));
		player.write(new SendString(threeb + "3 Bars" + threeb, 1090));
		player.write(new SendString(twob + "2 Bars" + twob, 1113));
		player.write(new SendString(twob + "2 Bars" + twob, 1116));
		player.write(new SendString(twob + "2 Bars" + twob, 1114));
		player.write(new SendString(twob + "2 Bars" + twob, 1089));
		player.write(new SendString(twob + "2 Bars" + twob, 8428));
		player.write(new SendString(oneb + "1 Bar" + oneb, 1124));
		player.write(new SendString(oneb + "1 Bar" + oneb, 1125));
		player.write(new SendString(oneb + "1 Bar" + oneb, 1126));
		player.write(new SendString(oneb + "1 Bar" + oneb, 1127));
		player.write(new SendString(oneb + "1 Bar" + oneb, 1128));
		player.write(new SendString(oneb + "1 Bar" + oneb, 1129));
		player.write(new SendString(oneb + "1 Bar" + oneb, 1130));
		player.write(new SendString(oneb + "1 Bar" + oneb, 1131));
		player.write(new SendString(oneb + "1 Bar" + oneb, 13357));
		player.write(new SendString(oneb + "1 Bar" + oneb, 11459));
		player.write(new SendString(
				GetForlvl(33, player) + "Plate Body" + GetForlvl(18, player), 1101));
		player.write(new SendString(
				GetForlvl(31, player) + "Plate Legs" + GetForlvl(16, player), 1099));
		player.write(new SendString(
				GetForlvl(31, player) + "Plate Skirt" + GetForlvl(16, player), 1100));
		player.write(new SendString(
				GetForlvl(29, player) + "2 Hand Sword" + GetForlvl(14, player), 1088));
		player.write(new SendString(
				GetForlvl(27, player) + "Kite Shield" + GetForlvl(12, player), 1105));
		player.write(new SendString(
				GetForlvl(26, player) + "Chain Body" + GetForlvl(11, player), 1098));
		player.write(new SendString(
				GetForlvl(26, player) + "Oil Lantern Frame" + GetForlvl(11, player),
				11461));
		player.write(new SendString(
				GetForlvl(25, player) + "Battle Axe" + GetForlvl(10, player), 1092));
		player.write(new SendString(
				GetForlvl(24, player) + "Warhammer" + GetForlvl(9, player), 1083));
		player.write(new SendString(
				GetForlvl(23, player) + "Square Shield" + GetForlvl(8, player), 1104));
		player.write(new SendString(
				GetForlvl(22, player) + "Full Helm" + GetForlvl(7, player), 1103));
		player.write(new SendString(
				GetForlvl(21, player) + "Throwing Knives" + GetForlvl(7, player), 1106));
		player.write(new SendString(
				GetForlvl(21, player) + "Long Sword" + GetForlvl(6, player), 1086));
		player.write(new SendString(GetForlvl(20, player) + "Scimitar" + GetForlvl(5, player),
				1087));
		player.write(new SendString(
				GetForlvl(20, player) + "Arrowtips" + GetForlvl(5, player), 1108));
		player.write(new SendString(GetForlvl(19, player) + "Sword" + GetForlvl(4, player),
				1085));
		player.write(new SendString(GetForlvl(19, player) + "Bolts" + GetForlvl(4, player),
				9140));
		player.write(new SendString(GetForlvl(19, player) + "Nails" + GetForlvl(4, player),
				13358));
		player.write(new SendString(
				GetForlvl(18, player) + "Medium Helm" + GetForlvl(3, player), 1102));
		player.write(new SendString(GetForlvl(17, player) + "Mace" + GetForlvl(2, player),
				1093));
		player.write(new SendString(GetForlvl(15, player) + "Dagger" + GetForlvl(1, player),
				1094));
		player.write(new SendString(GetForlvl(16, player) + "Axe" + GetForlvl(1, player), 1091));
		player.write(new SendActionInterface(1203, 0, 1119, 1));
		player.write(new SendActionInterface(1349, 0, 1120, 1));
		player.write(new SendActionInterface(1101, 0, 1121, 1));
		player.write(new SendActionInterface(1137, 0, 1122, 1));
		player.write(new SendActionInterface(9140, 0, 1123, 10));
		player.write(new SendActionInterface(1279, 1, 1119, 1));
		player.write(new SendActionInterface(1420, 1, 1120, 1));
		player.write(new SendActionInterface(1067, 1, 1121, 1));
		player.write(new SendActionInterface(1153, 1, 1122, 1));
		player.write(new SendActionInterface(40, 1, 1123, 15));
		player.write(new SendActionInterface(1323, 2, 1119, 1));
		player.write(new SendActionInterface(1335, 2, 1120, 1));
		player.write(new SendActionInterface(1081, 2, 1121, 1));
		player.write(new SendActionInterface(1175, 2, 1122, 1));
		player.write(new SendActionInterface(863, 2, 1123, 5));
		player.write(new SendActionInterface(1293, 3, 1119, 1));
		player.write(new SendActionInterface(1363, 3, 1120, 1));
		player.write(new SendActionInterface(1115, 3, 1121, 1));
		player.write(new SendActionInterface(1191, 3, 1122, 1));
		player.write(new SendActionInterface(1309, 4, 1119, 1));
		player.write(new SendActionInterface(4820, 4, 1122, 15));
		player.write(new SendActionInterface(4540, 4, 1121, 1));
		player.write(new SendActionInterface(-1, 3, 1123, 1));
		player.write(new SendString("", 1135));
		player.write(new SendString("", 1134));
		player.write(new SendString("", 1132));
		player.write(new SendString("", 1096));
		player.write(new SendInterface(994));
	}

	private void makeBronzeInterface(Player player) {
		String fiveb = GetForBars(2349, 5, player);
		String threeb = GetForBars(2349, 3, player);
		String twob = GetForBars(2349, 2, player);
		String oneb = GetForBars(2349, 1, player);
		player.write(new SendString(fiveb + "5 Bars" + fiveb, 1112));
		player.write(new SendString(threeb + "3 Bars" + threeb, 1109));
		player.write(new SendString(threeb + "3 Bars" + threeb, 1110));
		player.write(new SendString(threeb + "3 Bars" + threeb, 1118));
		player.write(new SendString(threeb + "3 Bars" + threeb, 1111));
		player.write(new SendString(threeb + "3 Bars" + threeb, 1095));
		player.write(new SendString(threeb + "3 Bars" + threeb, 1115));
		player.write(new SendString(threeb + "3 Bars" + threeb, 1090));
		player.write(new SendString(twob + "2 Bars" + twob, 1113));
		player.write(new SendString(twob + "2 Bars" + twob, 1116));
		player.write(new SendString(twob + "2 Bars" + twob, 1114));
		player.write(new SendString(twob + "2 Bars" + twob, 1089));
		player.write(new SendString(twob + "2 Bars" + twob, 8428));
		player.write(new SendString(oneb + "1 Bar" + oneb, 1124));
		player.write(new SendString(oneb + "1 Bar" + oneb, 1125));
		player.write(new SendString(oneb + "1 Bar" + oneb, 1126));
		player.write(new SendString(oneb + "1 Bar" + oneb, 1127));
		player.write(new SendString(oneb + "1 Bar" + oneb, 1128));
		player.write(new SendString(oneb + "1 Bar" + oneb, 1129));
		player.write(new SendString(oneb + "1 Bar" + oneb, 1130));
		player.write(new SendString(oneb + "1 Bar" + oneb, 1131));
		player.write(new SendString(oneb + "1 Bar" + oneb, 13357));
		player.write(new SendString(oneb + "1 Bar" + oneb, 11459));
		player.write(new SendString(
				GetForlvl(18, player) + "Plate Body" + GetForlvl(18, player), 1101));
		player.write(new SendString(
				GetForlvl(16, player) + "Plate Legs" + GetForlvl(16, player), 1099));
		player.write(new SendString(
				GetForlvl(16, player) + "Plate Skirt" + GetForlvl(16, player), 1100));
		player.write(new SendString(
				GetForlvl(14, player) + "2 Hand Sword" + GetForlvl(14, player), 1088));
		player.write(new SendString(
				GetForlvl(12, player) + "Kite Shield" + GetForlvl(12, player), 1105));
		player.write(new SendString(
				GetForlvl(11, player) + "Chain Body" + GetForlvl(11, player), 1098));
		player.write(new SendString(
				GetForlvl(10, player) + "Battle Axe" + GetForlvl(10, player), 1092));
		player.write(new SendString(GetForlvl(9, player) + "Warhammer" + GetForlvl(9, player),
				1083));
		player.write(new SendString(
				GetForlvl(8, player) + "Square Shield" + GetForlvl(8, player), 1104));
		player.write(new SendString(GetForlvl(7, player) + "Full Helm" + GetForlvl(7, player),
				1103));
		player.write(new SendString(
				GetForlvl(7, player) + "Throwing Knives" + GetForlvl(7, player), 1106));
		player.write(new SendString(
				GetForlvl(6, player) + "Long Sword" + GetForlvl(6, player), 1086));
		player.write(new SendString(GetForlvl(5, player) + "Scimitar" + GetForlvl(5, player),
				1087));
		player.write(new SendString(GetForlvl(5, player) + "Arrowtips" + GetForlvl(5, player),
				1108));
		player.write(new SendString(GetForlvl(4, player) + "Sword" + GetForlvl(4, player),
				1085));
		player.write(new SendString(GetForlvl(4, player) + "Bolts" + GetForlvl(4, player),
				1107));
		player.write(new SendString(GetForlvl(4, player) + "Nails" + GetForlvl(4, player),
				13358));
		player.write(new SendString(
				GetForlvl(3, player) + "Medium Helm" + GetForlvl(3, player), 1102));
		player.write(new SendString(GetForlvl(2, player) + "Mace" + GetForlvl(2, player), 1093));
		player.write(new SendString(GetForlvl(1, player) + "Dagger" + GetForlvl(1, player),
				1094));
		player.write(new SendString(GetForlvl(1, player) + "Axe" + GetForlvl(1, player), 1091));
		player.write(new SendActionInterface(1205, 0, 1119, 1));
		player.write(new SendActionInterface(1351, 0, 1120, 1));
		player.write(new SendActionInterface(1103, 0, 1121, 1));
		player.write(new SendActionInterface(1139, 0, 1122, 1));
		player.write(new SendActionInterface(819, 0, 1123, 10));
		player.write(new SendActionInterface(1277, 1, 1119, 1));
		player.write(new SendActionInterface(1422, 1, 1120, 1));
		player.write(new SendActionInterface(1075, 1, 1121, 1));
		player.write(new SendActionInterface(1155, 1, 1122, 1));
		player.write(new SendActionInterface(39, 1, 1123, 15));
		player.write(new SendActionInterface(1321, 2, 1119, 1));
		player.write(new SendActionInterface(1337, 2, 1120, 1));
		player.write(new SendActionInterface(1087, 2, 1121, 1));
		player.write(new SendActionInterface(1173, 2, 1122, 1));
		player.write(new SendActionInterface(864, 2, 1123, 5));
		player.write(new SendActionInterface(1291, 3, 1119, 1));
		player.write(new SendActionInterface(1375, 3, 1120, 1));
		player.write(new SendActionInterface(1117, 3, 1121, 1));
		player.write(new SendActionInterface(1189, 3, 1122, 1));
		player.write(new SendActionInterface(1307, 4, 1119, 1));
		player.write(new SendActionInterface(4819, 4, 1122, 15));
		player.write(new SendActionInterface(-1, 3, 1123, 1));
		player.write(new SendString("", 1135));
		player.write(new SendString("", 1134));
		player.write(new SendString("", 11461));
		player.write(new SendString("", 11459));
		player.write(new SendString("", 1132));
		player.write(new SendString("", 1096));
		player.write(new SendInterface(994));
	}

	private String GetForlvl(int i, Player player) {
		if (player.getSkills().getLevel(Skills.SMITHING) >= i)
			return "@whi@";

		return "@bla@";
	}

	private String GetForBars(int i, int j, Player player) {
		if (player.getItems().playerHasItem(i, j))
			return "@gre@";

		return "@red@";
	}

}
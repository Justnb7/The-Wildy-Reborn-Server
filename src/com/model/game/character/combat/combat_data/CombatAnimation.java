package com.model.game.character.combat.combat_data;

import com.model.game.character.player.Player;

/**
 * 
 * @author Patrick van Elderen
 * @date 13-4-2016
 *
 */

public class CombatAnimation {

	/**
	 * Get's the defend animation for the attacker.
	 */
	public static int getDefendAnimation(Player player) {
		String shieldName = player.getItems().getItemName(player.playerEquipment[player.getEquipment().getShieldId()]).toLowerCase();
		String name = player.getItems().getItemName(player.playerEquipment[player.getEquipment().getWeaponId()]).toLowerCase();
		int weapon = player.playerEquipment[player.getEquipment().getWeaponId()];
		boolean gods = false;

		if (shieldName.endsWith("defender")) { // Defenders
			return 4177;
		}
		if (shieldName.contains("toktz-ket-xil")) {
			return 1156;
		}
		if (shieldName != null) {
			if (shieldName.contains("shield") || shieldName.contains("kite") || shieldName.contains("ward")) {
				return 1156;
			}
		}
		if (name.toLowerCase().contains("elder maul")) {
			return 7517;
		}
		if (name.toLowerCase().contains("chinchompa")) {
			return 3176;
		}
		if (weapon <= 0) {
			return 424;
		}
		if (name.contains("zamorakian hasta")) {
			return 1709;
		}
		if (name.contains("ahrims staff")) {
			return 2079;
		}
		if (name.contains("torags hammers")) {
			return 424;
		}
		if (name.contains("dharok")) {
			return 424;
		}
		if (name.contains("xil-ul") || name.endsWith("crossbow")) {
			return 424;
		}
		if (name.endsWith("throwing axe") || name.contains("javelin") || name.contains("knife") || name.contains("dart")
				|| name.contains("shortbow") || name.contains("longbow") || name.contains("karil")
				|| name.contains("crystal") || name.contains("dark bow") || name.contains("seercull")) {
			return 424;
		}
		if (name.contains("abyssal whip") || name.contains("abyssal tentacle")) {
			return 1659;
		}
		if (name.contains("granite maul") || name.contains("tzhaar-ket-om") || name.contains("chaotic maul")) {
			return 1666;
		}
		if (name.contains("flail")) {
			return 2063;
		}
		if (name.contains("2h") || name.contains("godsword") || name.contains("saradomin sword")
				|| name.contains("saradomin's blessed sword")) {
			return 7056;
		}
		if (weapon == 6526 || name.contains("wand") || name.contains("staff") || name.contains("halberd")
				|| name.contains("warspear") || name.contains("spear") || name.contains("cane")
				|| name.contains("banner")) {
			if (gods)
				return 403;
			return 420;// staff block
		}
		if (name.contains("claws")) {
			return 397;
		}
		if (name.contains("longsword") || name.endsWith("_sword") || name.contains("battleaxe") || name.contains("mace")
				|| name.contains("axe") || name.contains("warhammer") || name.contains("dagger")) {
			return 397;
		}
		if (weapon == 4031) {// monkey greegree
			return 221;
		}
		if (name.contains("barrelchest anchor")) {
			return 5866;
		}
		if (name.contains("ballista")) {
			return 7219;
		}
		return 404;
	}

	/**
	 * Get's the attack animations for weapons.
	 */
	public static int getAttackAnimation(Player player, String name) {
		int animation = 0;

		if (player.playerEquipment[player.getEquipment().getWeaponId()] <= 0) {
			switch (player.getAttackStyle()) {
			case 0: // Punch
				return animation = 422;

			case 1: // Kick
				return animation = 423;

			case 2: // Block
				return animation = 422;
			}
		}
		if (name.toLowerCase().contains("elder maul")) {
			return 7516;
		}
		if (name.contains("barrelchest anchor")) {
			return 5865;
		}
		if (name.contains("ballista")) {
			return 7218;
		}
		if (name.contains("dagger")) {
			switch (player.getAttackStyle()) {
			case 3:
				return animation = 451;
			case 1:
				return animation = 400;
			default:
				return animation = 402;
			}
		}
		if (name.contains("abyssal dagger")) {
			return animation = 3297;
		}
		if (name.contains("abyssal bludgeon")) {
			return animation = 3298;
		}
		if (name.contains("staff") || name.contains("Staff") || name.contains("master wand")
				|| name.contains("3rd age wand")) {
			return animation = 419;
		}
		if (name.endsWith("longsword")) {
			switch (player.getAttackStyle()) {

			case 2:
				return animation = 412;
			default:
				return animation = 451;
			}
		}
		if (name.contains("2h")) {
			switch (player.getAttackStyle()) {

			case 3:
				return animation = 407;
			default:
				return animation = 406;
			}
		}

		if (name.contains("godsword") || name.contains("saradomin sword") || name.contains("saradomin's blessed sword")) {
			switch (player.getAttackStyle()) {
			// ACCURATE = 0, AGGRESSIVE = 1, DEFENSIVE = 2, CONTROLLED = 3
			case 0:// attack
				return animation = 7045;
			case 2:// def
				return animation = 7055;
			case 1:// str
				return animation = 7045;
			case 3:// crush
				return animation = 7054;
			}
		}
		if (name.contains("whip") || name.contains("abyssal tentacle")) {
			return animation = 1658;
		}
		if (name.contains("granite maul")) {
			return animation = 1665;
		}
		if (name.endsWith("greataxe")) {
			switch (player.getAttackStyle()) {
			case 0:// attack
				return animation = 2067;
			case 2:// str
				return animation = 2067;
			case 1:// def
				return animation = 2067;
			case 3:// crush
				return animation = 2066;
			}
		}
		if (name.endsWith("crossbow") && !name.contains("karil's")) {
			return 4230;
		}
		if (name.endsWith("xil-ul")) {
			return 2614;
		}
		if (name.contains("hammers")) {
			return 2068;
		}
		if (name.contains("flail")) {
			return 2062;
		}
		if (name.contains("guthan") || name.contains("spear")) {
			return 2080;
		}
		if (name.endsWith("spear")) {
			switch (player.getAttackStyle()) {
			case 3:
				return animation = 429;
			case 1:
				return animation = 440;
			default:
				return animation = 428;
			}
		}
		if (name.endsWith("battleaxe") || (name.endsWith(" axe"))) {
			switch (player.getAttackStyle()) {
			case 3:
				return animation = 401;
			default:
				return animation = 395;
			}
		}
		if (name.endsWith("warhammer")) {
			return animation = 401;
		}
		if (name.endsWith("pickaxe")) {
			switch (player.getAttackStyle()) {

			case 2:
				return animation = 400;
			case 3:
				return animation = 401;
			default:
				return animation = 395;
			}
		}
		if (name.endsWith("mace")) {
			switch (player.getAttackStyle()) {

			case 2:
				return animation = 400;
			default:
				return animation = 401;
			}
		}
		if (name.contains("scimitar") || name.startsWith("Korasi's")) {
			switch (player.getAttackStyle()) {
			case 2:
				return animation = 386;
			default:
				return animation = 390;
			}
		}
		if (name.contains("halberd")) {
			switch (player.getAttackStyle()) {
			case 2:
				return animation = 440;
			default:
				return animation = 428;
			}
		}
		if (name.contains("spear")) {
			switch (player.getAttackStyle()) {

			case 2:
				return animation = 2081;
			case 0:
				return animation = 2082;
			case 1:
				return animation = 2080;
			}
		}
		if (name.endsWith(" sword")) {
			switch (player.getAttackStyle()) {

			case 3:
				return animation = 386;
			default:
				return animation = 390;
			}
		}
		if (name.equals("Granite maul")) {
			return animation = 1665;
		}
		if (name.contains("dart")) {
			return animation = 6600;
		}
		if (name.contains("knife") || name.contains("javelin") || name.contains("thrownaxe")) {
			return animation = 806;
		}
		if (name.endsWith("blowpipe")) {
			return animation = 5061;
		}
		if (name.endsWith("claws")) {
			switch (player.getAttackStyle()) {

			case 3:
				return animation = 1067;
			default:
				return animation = 393;
			}
		}
		if (name.endsWith("rapier")) {
			return animation = 386;
		}
		if (name.startsWith("Barrelchest")) {
			switch (player.getAttackStyle()) {
			case 1:
				return animation = 5866;
			default:
				return animation = 5865;
			}
		}
		if (name.contains("staff of the dead") || name.equals("dragon cane")) {
			return 440;
		}
		if (name.toLowerCase().contains("chinchompa")) {
			return 2779;
		}
		if (name.contains("ahrims staff")) {
			return 2078;
		}
		if (name.contains("tzhaar-ket-om") || name.contains("chaotic maul")) {
			return 2661;
		}
		if (name.contains("zamorakian hasta")) {
			switch (player.getAttackStyle()) {
			case 0:
				return animation = 1710;
			case 2:
				return animation = 1712;
			default:
				return animation = 1711;
			}
		}
		if (name.equalsIgnoreCase("scythe")) {
			return animation = 408;
		}
		/*if (name.contains("shortbow") || name.contains("longbow") || name.contains("crystal")
				|| name.equalsIgnoreCase("Dark bow") || name.contains("seercull") || name.contains("3rd age bow")) {
			return animation = 426;
		}*/
		if (name.contains("bow")) {
			if (name.contains("cross")) {
				return animation = 2075;
			}
			return animation = 426;
		}
		if (name.contains("thrownaxe")) {
			return 385;
		}
		// ACCURATE = 0, AGGRESSIVE = 1, DEFENSIVE = 2, CONTROLLED = 3

		if (animation == 0) {
			player.getActionSender().sendMessage("Attack Animation not supported. Please contact Mod Patrick.");
		}
		return animation;
	}
	
	public static void itemAnimations(Player player) {
		String shieldName = player.getItems().getItemName(player.playerEquipment[player.getEquipment().getShieldId()]).toLowerCase();
		String name = player.getItems().getItemName(player.playerEquipment[player.getEquipment().getWeaponId()]).toLowerCase();
		@SuppressWarnings("unused")
		int weapon = player.playerEquipment[player.getEquipment().getWeaponId()];
		if (player.isPlayerTransformed()) {
			return;
		}
		boolean gods = false;
		gods = name.startsWith("saradomin") || name.startsWith("zamorak") || name.startsWith("guthix");

		player.standTurnAnimation = 808;
		player.turnAnimation = 823;
		player.walkAnimation = 819;
		player.turn180Animation = 0x334;
		player.turn90ClockWiseAnimation = 0x335;
		player.turn90CounterClockWiseAnimation = 0x336;
		player.runAnimation = 824;

		if (name.contains("staff") || name.contains("wand") || name.contains("hasta") || name.contains("spear")
				|| name.contains("cane") || name.contains("trident")) {
			if (gods && !name.contains("spear")) {
				player.standTurnAnimation = 813;
			}
			player.standTurnAnimation = 813;
			player.walkAnimation = 1205;
			player.runAnimation = 1210;
			return;
		}
		
		if(shieldName.contains("Dinh's bulwark")) {
			player.standTurnAnimation = 7508;
			player.walkAnimation = 7510;
			player.runAnimation = 7509;
			return;
		}
		
		if (name.contains("blowpipe")) {
			player.standTurnAnimation = 809;
			player.walkAnimation = 1146;
			player.runAnimation = 1210;
			return;
		}
		if (name.equals("dharok's greataxe")) {
			player.standTurnAnimation = 2065;
			player.walkAnimation = 2064;
			return;
		}

		if (name.contains("abyssal dagger")) {
			player.standTurnAnimation = 3296;
			return;
		}

		if (name.contains("abyssal bludgeon")) {
			player.walkAnimation = 3293;
			player.standTurnAnimation = 2074;
			player.runAnimation = 2077;
			return;
		}

		if (name.contains("zamorakian hasta")) {
			player.standTurnAnimation = 1713;
			player.walkAnimation = 1663;
			player.runAnimation = 1707;
			return;
		}
		if (name.contains("elder maul")) {
			player.standTurnAnimation = 7518;
			player.walkAnimation = 7520;
			player.runAnimation = 7519;
			return;
		}
		if (name.equals("granite maul") || name.contains("chaotic maul")) {
			player.standTurnAnimation = 1662;
			player.walkAnimation = 1663;
			player.runAnimation = 1664;
			return;
		}
		if (name.endsWith("crossbow")/* shadowy/jak */ && !name.contains("karil's")) {
			player.standTurnAnimation = 4591;
			return;
		}

		if (name.equals("verac's flail")) {
			player.standTurnAnimation = 2061;
			player.walkAnimation = 2060;
			player.runAnimation = 1831;
			return;
		}

		if (name.equals("karil's crossbow")) {
			player.standTurnAnimation = 2074;
			player.walkAnimation = 2076;
			player.runAnimation = 2077;
			return;
		}

		if (name.endsWith("longsword")) {
			player.standTurnAnimation = 809;
			return;
		}

		if (name.contains("banner")) {
			player.standTurnAnimation = 1421;
			player.walkAnimation = 1422;
			player.runAnimation = 1427;
			return;
		}

		if (name.contains("tzhaar-ket-om")) {
			player.standTurnAnimation = 2065;
			return;
		}

		if (name.contains("barrelchest anchor")) {
			player.standTurnAnimation = 5869;
			player.walkAnimation = 5867;
			player.runAnimation = 5868;
			return;
		}

		if (name.contains("abyssal whip") || name.contains("tentacle")) {
			player.standTurnAnimation = 808;
			player.walkAnimation = 1660;
			player.runAnimation = 1661;
			return;
		}

		if (name.contains("godsword") || name.contains("saradomin sword") || name.contains("saradomin's blessed sword")) {
			player.standTurnAnimation = 7053;
			player.walkAnimation = 7052;
			player.runAnimation = 7043;
			player.turnAnimation = 7044;
			player.turn180Animation = 7044;
			player.turn90ClockWiseAnimation = 7044;
			player.turn90CounterClockWiseAnimation = 7044;
			return;
		}

		if (name.endsWith("2h sword")) {
			player.standTurnAnimation = 2561;
			player.walkAnimation = 2562;
			player.runAnimation = 2563;
			return;
		}

		if (name.contains("ballista")) {
			player.standTurnAnimation = 7220;
			player.walkAnimation = 7223;
			player.runAnimation = 7221;
			return;
		}
	}

}

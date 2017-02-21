package com.model.game.character.combat.combat_data;

import com.model.game.character.combat.weapon.AttackStyle;
import com.model.game.character.player.Player;

public class CombatData {

	public static int getAttackDelay(Player player, String weapon) {
		
		if (player.usingMagic) {
			switch (player.MAGIC_SPELLS[player.getSpellId()][0]) {
			case 12871: // ice blitz
			case 13023: // shadow barrage
			case 12891: // ice barrage
				return 5;

			default:
				return 5;
			}
		}
		
		if (player.playerEquipment[player.getEquipment().getWeaponId()] == -1)
			return 4;// unarmed
		
		switch (player.playerEquipment[player.getEquipment().getWeaponId()]) {
		
		case 11235:
			return 9;
			
		case 11730:
		case 18349:
		case 15662:
		case 3757:
			return 4;
			
		case 6528:
		case 18353:
		case 13399:
			return 7;
			
		case 10033:
		case 10034:
		case 18351:
		case 15574:
			return 5;
			
		}
		
		if (weapon.contains("blowpipe")) {
			if (player.playerIndex > 0) { // we're attacking a player
				return player.getAttackStyle() == AttackStyle.AGGRESSIVE ? 3 : 4;
			} else if (player.npcIndex > 0) { // we're attacking an npc
				// accurate = 4, rapid = accurate-1
				return player.getAttackStyle() == AttackStyle.AGGRESSIVE ? 2 : 3;
			}
			return 4;
		}
		
		if (weapon.endsWith("greataxe"))
			return 7;
		
		else if (weapon.equals("torags hammers"))
			return 5;
		
		else if (weapon.equals("barrelchest anchor"))
			return 7;
		
		else if (weapon.equals("guthans warspear"))
			return 5;
		
		else if (weapon.equals("veracs flail"))
			return 5;
		
		else if (weapon.equals("ahrims staff"))
			return 6;
		
		else if (weapon.contains("staff")) {
			if (weapon.contains("zamarok") || weapon.contains("guthix") || weapon.contains("saradomian") || weapon.contains("slayer") || weapon.contains("ancient"))
				return 4;
			else
				return 5;
			
		} else if (weapon.contains("bow")) {
			if (weapon.contains("composite") || weapon.equals("seercull"))
				return 5;
			
			else if (weapon.contains("aril"))
				return 4;
			
			else if (weapon.contains("Ogre"))
				return 8;
			
			else if (weapon.contains("short") || weapon.contains("hunt") || weapon.contains("sword"))
				return 4;
			
			else if (weapon.contains("long") || weapon.contains("crystal"))
				return 6;
			
			else if (weapon.contains("crossbow"))
				if (player.getAttackStyle() == 2) {
					return 7;
				} else if (player.getAttackStyle() == 2) {
					return 4;
				} else {
					return 7;
				}
		} else if (weapon.contains("dagger"))
			return 4;
		
		else if (weapon.contains("godsword") || weapon.contains("2h"))
			return 6;
		
		else if (weapon.contains("longsword"))
			return 5;
		
		else if (weapon.contains("sword"))
			return 4;
		
		else if (weapon.contains("scimitar"))
			return 4;
		
		else if (weapon.contains("mace"))
			return 5;
		
		else if (weapon.contains("battleaxe"))
			return 6;
		
		else if (weapon.contains("pickaxe"))
			return 5;
		
		else if (weapon.contains("thrownaxe"))
			return 5;
		
		else if (weapon.contains("axe"))
			return 5;
		
		else if (weapon.contains("warhammer"))
			return 6;
		
		else if (weapon.contains("2h"))
			return 7;
		
		else if (weapon.contains("spear"))
			return 5;
		
		else if (weapon.contains("claw"))
			return 4;
		
		else if (weapon.contains("halberd"))
			return 7;
		
		else if (weapon.equals("granite maul"))
			return 7;
		
		else if (weapon.equals("toktz-xil-ak"))// sword
			return 4;
		
		else if (weapon.equals("tzhaar-ket-em"))// mace
			return 5;
		
		else if (weapon.equals("tzhaar-ket-om"))// maul
			return 7;
		
		else if (weapon.equals("toktz-xil-ek"))// knife
			return 4;
		
		else if (weapon.equals("toktz-xil-ul"))// rings
			return 4;
		
		else if (weapon.equals("toktz-mej-tal"))// staff
			return 6;
		
		else if (weapon.contains("whip") || weapon.contains("abyssal bludgeon"))
			return 4;
		
		else if (weapon.contains("dart"))
			return 3;
		
		else if (weapon.contains("knife"))
			return 3;
		
		else if (weapon.contains("javelin"))
			return 6;
		
		return 5;
	}

	public static int getHitDelay(Player player, String weaponName) {
		if (player.usingMagic) {
			switch (player.MAGIC_SPELLS[player.getSpellId()][0]) {
			
			case 12891:
				return 4;
				
			case 12871:
				return 6;
				
			default:
				return 4;
			}
		} else {
			if (weaponName.contains("dart")) {
				return 3;
			}
			
			if (weaponName.contains("knife") || weaponName.contains("javelin") || weaponName.contains("thrownaxe")) {
				return 3;
			}
			
			if (weaponName.contains("cross") || weaponName.contains("c'bow")) {
				return 4;
			}
			
			if (weaponName.contains("bow") && !player.dbowSpec) {
				return 4;
			} else if (player.dbowSpec) {
				return 4;
			}

			switch (player.playerEquipment[player.getEquipment().getWeaponId()]) {
			
			case 6522:
				return 3;
				
			case 10887:
				return 3;
				
			case 10034:
			case 10033:
				return 3;
				
			default:
				return 2;
			}
		}
	}
	
	/**
	 * Checks wether the player has equiped a halberd.
	 * @param player
	 *            The {@link Player} who has the halberd equiped.
	 * @return We are checking for an equiped halberd.
	 */
	public static boolean usingHalberd(Player player) {
		String weapon = player.getItems().getItemName(player.playerEquipment[player.getEquipment().getWeaponId()]).toLowerCase();
		
		if (weapon.contains("halberd")) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * Determines the distance required before you can attack the other player
	 * 
	 * @param player
	 *            The {@link Player} who is following/attacking the other player
	 * @param victim
	 *            The {@link Player} we are following/attacking
	 * @param follow
	 *            Determine if you are following the player
	 * @return We are within distance to interact with the other player
	 */
	public static int calculateAttackDistance(Player player, Player victim, boolean follow) {
		int distance = 1;
		if (player.throwingAxe && !player.usingBow && !player.usingMagic) {
			distance = 4;
		} else if (!player.throwingAxe && usingHalberd(player) && !player.usingBow && !player.usingMagic) {
			distance = 2;
		} else if (player.usingBow) {
			distance = 7;
		} else if(player.playerEquipment[player.getEquipment().getWeaponId()] == 11785) {
			distance = 9;
		} else if (player.getEquipment().wearingBallista(player)) {
			distance = 11;
		} else if (player.getEquipment().wearingBlowpipe(player)) {
			distance = 5;
		} else if (player.usingMagic) {
			distance = 10;
		} else if (!player.throwingAxe && !usingHalberd(player) && !player.usingBow && !player.usingMagic) {
			if (player.getX() != victim.getX() && player.getY() != victim.getY()
					&& player.distanceToPoint(victim.getX(), victim.getY()) < 2) {
				distance = 2;
			} else {
				distance = CombatRequirements.getRequiredDistance(player);
			}
		}
		if (victim.getMovementHandler().isMoving() && !follow) {
			distance += 2;
		}
		return distance;
	}
	
	/**
	 * Checks for attack distance.
	 * @param player
	 * @param victim
	 * @return
	 */
	public static boolean isWithinAttackDistance(Player player, Player victim) {
		return player.goodDistance(player.getX(), player.getY(), victim.getX(), victim.getY(), calculateAttackDistance(player, victim, false));
	}

	/**
	 * Checks for follow distance.
	 * @param player
	 * @param victim
	 * @return
	 */
	public static boolean isWithinAttackDistanceForStopFollow(Player player, Player victim) {
		return player.goodDistance(player.getX(), player.getY(), victim.getX(), victim.getY(),
				calculateAttackDistance(player, victim, true));
	}

}

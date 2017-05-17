package com.model.game.character.combat.combat_data;

import com.model.game.character.Entity;
import com.model.game.character.combat.weapon.AttackStyle;
import com.model.game.character.player.Player;
import com.model.game.item.container.impl.Equipment;

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
		
		if (player.getEquipment().getId(Equipment.WEAPON_SLOT) == -1)
			return 4;// unarmed
		
		switch (player.getEquipment().getId(Equipment.WEAPON_SLOT)) {
		
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
			return player.getAttackStyle() == AttackStyle.AGGRESSIVE ? player.getCombatState().getTarget().isPlayer() ? 3 : 2 : player.getCombatState().getTarget().isPlayer() ? 4 : 3;
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
			
			if (weaponName.contains("bow")) {
				return 4;
			}

			switch (player.getEquipment().getId(Equipment.WEAPON_SLOT)) {
			
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
}

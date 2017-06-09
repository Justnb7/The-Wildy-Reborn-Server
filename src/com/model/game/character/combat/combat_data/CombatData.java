package com.model.game.character.combat.combat_data;

import com.model.game.character.player.Player;
import com.model.game.item.Item;
import com.model.game.item.container.impl.equipment.EquipmentConstants;

public class CombatData {

	public static int getHitDelay(Player player, String weaponName) {
		if (player.getCombatType() == CombatStyle.MAGIC) {
			switch (player.MAGIC_SPELLS[player.spellId][0]) {
			
			case 12891:
				return 4;
				
			case 12871:
				return 6;
				
			default:
				return 4;
			}
		} else {
			if (weaponName == null || weaponName.length() == 0) return 1;
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

			Item wep = player.getEquipment().get(EquipmentConstants.WEAPON_SLOT);
			if (wep == null) return 1;
			switch (wep.getId()) {
			
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

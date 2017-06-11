package com.model.game.character.combat.effect;

import com.model.game.character.Graphic;
import com.model.game.character.player.Player;
import com.model.game.item.Item;
import com.model.game.item.container.impl.equipment.EquipmentConstants;
import com.model.utility.Utility;

/**
 * This class is used for the damage effects of barrows equipment.
 * @author Patrick van Elderen
 * @date 13-4-2016
 *
 */
public class BarrowsEffect {
	
	/**
	 * Applies the veracs flail effect to the victim.
	 */
	private static void applyVeracsFlailEffect(Player attacker, int damage) {
		boolean isWearingGear = EquipmentConstants.isWearingVeracs(attacker);
		boolean randomEffect = Utility.getRandom(2) == 0;
		
		if (isWearingGear && randomEffect) {
			damage = (int) damage;
		}
	}

	/**
	 * Applies the guthan warspear effect to the victim.
	 */
	private static void applyWarspearEffect(Player attacker, Player victim, int damage) {
		boolean isWearingGear = EquipmentConstants.isWearingGuthans(attacker);
		boolean randomEffect = Utility.getRandom(2) == 0;

		if (isWearingGear) {
			if (attacker.getSkills().getLevel(3) + damage > attacker.getSkills().getLevelForExperience(3)) {
				damage = attacker.getSkills().getLevel(3);
			} else if (randomEffect) {
				attacker.getSkills().addLevel(3, damage);
				victim.playGraphics(Graphic.create(398));
			}
		}
	}

	/**
	 * Applies the torag's hammer effect on hit.
	 */
	private static void applyHammerEffect(Player attacker, Player victim, int damage) {
		boolean isWearingGear = EquipmentConstants.isWearingTorags(attacker);
		boolean randomEffect = Utility.getRandom(10) == 5;

		if (isWearingGear && randomEffect) {
			victim.playGraphics(Graphic.create(399));
			if (!victim.isNPC()) {
				// TODO: Implement running energy then subtract energy from the
				// victim (player).
			}
		}
	}

	/**
	 * Applies a random effect from a weapon like guthan's warpsear etc.
	 */
	public static void applyRandomEffect(Player attacker, Player victim, int damage) {
		if (!attacker.isNPC()) {
			Item weapon = attacker.getEquipment().get(EquipmentConstants.WEAPON_SLOT);

			if (weapon == null) {
				return;
			}
			if (attacker.getEquipment().get(EquipmentConstants.TORSO_SLOT) == null || attacker.getEquipment().get(EquipmentConstants.LEGS_SLOT) == null || attacker.getEquipment().get(EquipmentConstants.HELM_SLOT) == null) {
				return;
			}
			switch (weapon.getId()) {

			case 4726:
				applyWarspearEffect(attacker, victim, damage);
				break;
				
			case 4747:
				applyHammerEffect(attacker, victim, damage);
				break;
				
			case 4755:
				applyVeracsFlailEffect(attacker, damage);
				break;
			}
		}
	}
}
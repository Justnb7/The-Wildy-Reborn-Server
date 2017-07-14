package com.venenatis.game.model.combat.range;

import com.venenatis.game.constants.EquipmentConstants;
import com.venenatis.game.model.Item;
import com.venenatis.game.model.combat.Combat;
import com.venenatis.game.model.entity.player.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Holds the requirements for ammo
 * 
 * @author Arithium | Patrick van Elderen
 * 
 */
public enum AmmoRequirements {
	
	BRONZE_ARROW(882, 1),
	BRONZE_BOLT(877, 1),
	IRON_ARROW(884, 1),
	IRON_BOLT(9140, 26),
	STEEL_ARROW(886, 5),
	STEEL_BOLT(9141, 31),
	MITHRIL_ARROW(888, 20),
	MITHRIL_BOLT(9142, 36),
	ADAMANT_ARROW(890, 30),
	ADAMANT_BOLT(9143, 46),
	DIAMOND_BOLT(9340, 46),
	DIAMOND_BOLT_E(9243, 46),
	RUBY_BOLT(9339, 46),
	RUBY_BOLT_E(9242, 46),
	EMERALD_BOLT(9338, 46),
	EMERALD_BOLT_E(9241, 46),
	RUNE_ARROW(892, 40),
	RUNITE_BOLT(9144, 61),
	DRAGON_BOLT(9341, 61),
	DRAGON_BOLT_E(9244, 61),
	ONYX_BOLT(9342, 61),
	ONYX_BOLT_E(9245, 61),
	DRAGON_ARROW(11212, 60),
	BOLT_RACK(4740, 70);

	private int ammo_id, level_requirement;

	private AmmoRequirements(int ammo, int level) {
		this.ammo_id = ammo;
		this.level_requirement = level;
	}

	private static Map<Integer, AmmoRequirements> ammo = new HashMap<Integer, AmmoRequirements>();

	static {
		for (AmmoRequirements def : values()) {
			ammo.put(def.ammo_id, def);
		}
	}

	public int getAmmo() {
		return ammo_id;
	}

	public int getLevelRequired() {
		return level_requirement;
	}

	public static AmmoRequirements forId(int id) {
		return ammo.get(id);
	}

	/**
	 * Determines if the player can use the ammo with the required bow
	 * 
	 * @param player
	 *            The player trying to shoot ammo
	 */
	public static boolean canUseArrowWithBow(Player player) {
		Item bow = player.getEquipment().get(EquipmentConstants.WEAPON_SLOT);
		Item ammo = player.getEquipment().get(EquipmentConstants.AMMO_SLOT);

		if (ammo == null) {
			player.message("You have no ammo left in your quiver.");
			Combat.resetCombat(player);
			return false;
		}
		if (bow != null && ammo != null) {
			AmmoRequirements arrowReq = forId(ammo.getId());
			BowRequirements bowReq = BowRequirements.forId(bow.getId());

			if (arrowReq != null && bowReq != null) {
				if (arrowReq.getLevelRequired() > bowReq.getLevelRequired()) {
					player.message("You cannot use " + ammo.getName().toLowerCase() + "s with a " + bow.getName().toLowerCase() + ".");
					Combat.resetCombat(player);
					return false;
				} else {
					return true;
				}
			}
		}
		return true;
	}
}
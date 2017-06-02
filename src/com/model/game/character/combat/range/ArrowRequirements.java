package com.model.game.character.combat.range;

import java.util.HashMap;
import java.util.Map;

import com.model.game.character.combat.Combat;
import com.model.game.character.player.Player;
import com.model.game.item.Item;
import com.model.game.item.container.impl.equipment.EquipmentConstants;

/**
 * Holds the requirements for arrows
 * 
 * @author Arithium
 * 
 */
public enum ArrowRequirements {
	
	BRONZE_ARROW(882, 1), 
	IRON_ARROW(884, 1), 
	STEEL_ARROW(886, 5), 
	MITHRIL_ARROW(888, 20), 
	ADAMANT_ARROW(890, 30), 
	RUNE_ARROW(892, 40), 
	DRAGON_ARROW(11212, 61), 
	BOLT_RACK(4740, 70);

	private int arrowId, levelRequired;

	private ArrowRequirements(int arrowId, int levelRequired) {
		this.arrowId = arrowId;
		this.levelRequired = levelRequired;
	}

	private static Map<Integer, ArrowRequirements> arrows = new HashMap<Integer, ArrowRequirements>();

	static {
		for (ArrowRequirements def : values()) {
			arrows.put(def.arrowId, def);
		}
	}

	public int getArrowId() {
		return arrowId;
	}

	public int getLevelRequired() {
		return levelRequired;
	}

	public static ArrowRequirements forId(int id) {
		return arrows.get(id);
	}

	/**
	 * Determines if the entity can use an arrow with a bow
	 * 
	 * @param player
	 * @return
	 */
	public static boolean canUseArrowWithBow(Player player) {
		Item bow = player.getEquipment().get(EquipmentConstants.WEAPON_SLOT);
		Item arrow = player.getEquipment().get(EquipmentConstants.AMMO_SLOT);

		if (arrow == null) {
			player.getActionSender().sendMessage("You have no arrows in your quiver.");
			Combat.resetCombat(player);
			return false;
		}
		if (bow != null && arrow != null) {
			ArrowRequirements arrowReq = forId(arrow.getId());
			BowRequirements bowReq = BowRequirements.forId(bow.getId());

			if (arrowReq != null && bowReq != null) {
				if (arrowReq.getLevelRequired() <= bowReq.getLevelRequired()) {
					return true;
				} else {
					player.getActionSender().sendMessage("You cannot use " + arrow.getName().toLowerCase() + "s with a " + bow.getName().toLowerCase() + ".");
					Combat.resetCombat(player);
					return false;
				}
			}
		}
		return false;
	}
}
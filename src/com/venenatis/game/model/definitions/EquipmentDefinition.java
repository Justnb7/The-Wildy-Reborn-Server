package com.venenatis.game.model.definitions;

import java.util.HashMap;
import java.util.Map;

import com.venenatis.game.content.skills.SkillRequirement;

/**
 * Represents an in-game equipped item.
 * 
 * @author SeVen
 */
public class EquipmentDefinition {

	/**
	 * The enumerated types of a players equipped item slots.
	 */
	public enum EquipmentType {
		/**
		 * The item that cannot be equipped.
		 */
		NOT_WIELDABLE(-1),

		/**
		 * The item that can be worn as a hat.
		 */
		HAT(0),

		/**
		 * The item that can be worn as a cape.
		 */
		CAPE(1),

		/**
		 * The item that can be used as a shield.
		 */
		SHIELD(5),

		/**
		 * The item that can be worn as a gloves.
		 */
		GLOVES(9),

		/**
		 * The item that can be worn as a boots.
		 */
		BOOTS(10),

		/**
		 * The item that can be worn as a necklace.
		 */
		AMULET(2),

		/**
		 * The item that can be worn as a ring.
		 */
		RING(12),

		/**
		 * The item that can be used as arrows.
		 */
		ARROWS(13),

		/**
		 * The item that can be worn as a body.
		 */
		BODY(4),

		/**
		 * The item that can be worn as legs.
		 */
		LEGS(7),

		/**
		 * The item that can be wielded.
		 */
		WEAPON(3);

		private final int slot;

		EquipmentType(final int slot) {
			this.slot = slot;
		}

		public int getSlot() {
			return slot;
		}

	}

	/**
	 * The equipment definitions stored on startup that are mapped to their item
	 * ids.
	 */
	public static final Map<Integer, EquipmentDefinition> EQUIPMENT_DEFINITIONS = new HashMap<>();

	public static EquipmentDefinition get(int id) {
		return EQUIPMENT_DEFINITIONS.get(id);
	}

	/**
	 * The id of the item.
	 */
	private final int id;

	/**
	 * The name of the item.
	 */
	private final String name;

	/**
	 * The type of equipment also called slot.
	 */
	private final EquipmentType type;

	/**
	 * The requirements to equip this item.
	 */
	private final SkillRequirement[] requirements;

	/**
	 * The bonuses for this item.
	 */
	private final int[] bonuses;

	/**
	 * Creates a new {@link EquipmentDefinition}.
	 * 
	 * @param id
	 *            The id of the item being equipped.
	 * 
	 * @param name
	 *            The name of the item.
	 * 
	 * @param type
	 *            The slot of the item.
	 * 
	 * @param requirements
	 *            The requirements of the item.
	 * 
	 * @param bonuses
	 *            The bonuses of the item.
	 */
	public EquipmentDefinition(int id, String name, EquipmentType type, SkillRequirement[] requirements, int[] bonuses) {
		this.id = id;
		this.name = name;
		this.type = type;
		this.requirements = requirements;
		this.bonuses = bonuses;
	}

	/**
	 * @return the bonuses
	 */
	public int[] getBonuses() {
		return bonuses;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the requirements
	 */
	public SkillRequirement[] getRequirements() {
		return requirements;
	}

	/**
	 * @return the type
	 */
	public EquipmentType getType() {
		return type;
	}

}
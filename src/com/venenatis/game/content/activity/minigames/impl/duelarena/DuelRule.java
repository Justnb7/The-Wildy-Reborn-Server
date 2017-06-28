package com.venenatis.game.content.activity.minigames.impl.duelarena;

import java.util.Optional;

import com.venenatis.game.constants.EquipmentConstants;
import com.venenatis.game.model.Item;
import com.venenatis.game.model.combat.data.CombatStyle;
import com.venenatis.game.model.definitions.WeaponDefinition;
import com.venenatis.game.model.entity.player.Player;

/**
 * The enumerated types of rules for dueling.
 *
 * @author Seven
 */
public enum DuelRule {
	RANGED(121058, 631, new RuleCondition()  {
		@Override
		public boolean canSelect(Player player, DuelRules rules) {
			if (rules.get(DuelRule.MELEE) && rules.get(DuelRule.MAGIC)) {
				player.getActionSender().sendMessage("You must have at least one combat type checked.");
				return false;
			}
			return true;
		}

	}),

	MELEE(121059, 632, new RuleCondition() {
		@Override
		public boolean canSelect(Player player, DuelRules rules) {
			if (rules.get(DuelRule.MAGIC) && rules.get(DuelRule.RANGED)) {
				player.getActionSender().sendMessage("You must have at least one combat type checked.");
				return false;
			}

			if (rules.get(DuelRule.WHIP_DDS)) {
				player.getActionSender().sendMessage("You cannot use whip and dragon dagger if melee is off.");
				return false;
			}
			return true;
		}
	}),

	MAGIC(121060, 633, new RuleCondition() {

		@Override
		public boolean canSelect(Player player, DuelRules rules) {
			if (rules.get(DuelRule.MELEE) && rules.get(DuelRule.RANGED)) {
				player.getActionSender().sendMessage("You must have at least one combat type checked.");
				return false;
			}
			return true;
		}
	}),
	SPECIAL_ATTACKS(121061, 634, (player, rules) -> true),
	FUN_WEAPONS(121062, 635, (player, rules) -> true),
	FORFEIT(121063, 636, new RuleCondition() {

		@Override
		public boolean canSelect(Player player, DuelRules rules) {
			if (rules.get(DuelRule.MOVEMENT)) {
				player.getActionSender().sendMessage("You cannot have no-movement and no-forfeit on at the same time.");
				return false;
			}
			return true;
		}
	}),
	PRAYER(121064, 637, (player, rules) -> true),
	DRINKS(121065, 638, (player, rules) -> true),
	FOOD(121066, 639, (player, rules) -> true),
	MOVEMENT(121067, 640, new RuleCondition() {
		@Override
		public boolean canSelect(Player player, DuelRules rules) {
			if (rules.get(DuelRule.OBSTACLES)) {
				player.getActionSender().sendMessage("You cannot have no-movement and obstacles on at the same time.");
				return false;
			}
			if (rules.get(DuelRule.FORFEIT)) {
				player.getActionSender().sendMessage("You cannot have no-movement and no-forfeit on at the same time.");
				return false;
			}
			return true;
		}
	}),
	OBSTACLES(121068, 641,  new RuleCondition() {
		@Override
		public boolean canSelect(Player player, DuelRules rules) {
			if (rules.get(DuelRule.MOVEMENT)) {
				player.getActionSender().sendMessage("You cannot have no-movement and obstacles on at the same time.");
				return false;
			}
			return true;
		}
	}),
	WHIP_DDS(121080, 642, new RuleCondition() {

		@Override
		public boolean canSelect(Player player, DuelRules rules) {
			if (rules.get(DuelRule.WEAPON)) {
				player.getActionSender().sendMessage("How can you use whip and dds if you disable melee?");
				return false;
			}
			return true;
		}
	}),
	HEAD(53245, 16384, EquipmentConstants.HELM_SLOT, (player, rules) -> true),
	CAPE(53246, 32768, EquipmentConstants.CAPE_SLOT, (player, rules) -> true),
	NECKLACE(53247, 65536, EquipmentConstants.NECKLACE_SLOT, (player, rules) -> true),
	AMMO(53248, 134217728, EquipmentConstants.AMMO_SLOT, (player, rules) -> true),
	WEAPON(53249, 131072, EquipmentConstants.WEAPON_SLOT, new RuleCondition() {
		@Override
		public boolean canSelect(Player player, DuelRules rules) {
			if (rules.get(DuelRule.WHIP_DDS)) {
				player.getActionSender().sendMessage("You cannot disable melee while whip and dds only is selected.");
				return false;
			}
			return true;
		}
	}),
	BODY(53250, 262144, EquipmentConstants.TORSO_SLOT,(player, rules) -> true),
	SHIELD(53251, 524288, EquipmentConstants.SHIELD_SLOT, (player, rules) -> true),
	LEGS(53252, 2097152, EquipmentConstants.LEGS_SLOT, (player, rules) -> true),
	GLOVES(53255, 8388608, EquipmentConstants.GLOVES_SLOT, (player, rules) -> true),
	BOOTS(53254, 16777216, EquipmentConstants.BOOTS_SLOT, (player, rules) -> true),
	RINGS(53253, 67108864, EquipmentConstants.RING_SLOT, (player, rules) -> true);

	/**
	 * The button for this rule.
	 */
	private final int button;

	/**
	 * The value for the config.
	 */
	private final int value;

	/**
	 * The equipment slot for this rule.
	 */
	private final int slot;

	/**
	 * The condition for this rule.
	 */
	private final RuleCondition condition;

	/**
	 * Creates a new {@link DuelRule}.
	 *
	 * @param button    The button for this rule.
	 * @param value     The config id for this rule.
	 * @param condition The condition for this rule.
	 */
	private DuelRule(int button, int value, RuleCondition condition) {
		this(button, value, -1, condition);
	}

	/**
	 * Creates a new {@link DuelRule}.
	 *
	 * @param button    The button for this rule.
	 * @param value     The config id for this rule.
	 * @param slot      The equipment slot for this rule.
	 * @param condition The condition for this rule.
	 */
	private DuelRule(int button, int value, int slot, RuleCondition condition) {
		this.button = button;
		this.value = value;
		this.slot = slot;
		this.condition = condition;
	}

	/**
	 * Gets the {@link Optional} of a {@DuelRule} for a specified button.
	 *
	 * @param button The id for this button.
	 * @return The optional.
	 */
	static final Optional<DuelRule> forButton(int button) {
		for (DuelRule rules : values()) {
			if (rules.getButtonId() == button) {
				return Optional.of(rules);
			}
		}
		return Optional.empty();
	}

	/**
	 * Determines if a rule is set.
	 *
	 * @param player
	 *            The player whos rule to check.
	 *
	 * @param rules
	 *            The flags for this rule.
	 *
	 * @param rule
	 *            The rule.
	 *
	 * @param condition
	 *            The condition for this rule.
	 *
	 * @return {@code true} If this flag is set. {@code false} Otherwise.
	 */
	static boolean set(final Player player, final DuelRules rules, DuelRule rule, RuleCondition condition) {
		boolean meets = condition.canSelect(player, rules);

		Optional<Player> other = player.getDuelArena().getOther();

		if (meets) {
			if (inventorySlotsRequired(player, rule) > player.getInventory().getFreeSlots()) {
				player.getActionSender().sendMessage("You do not have enough inventory space.");
				return false;
			}

			if (inventorySlotsRequired(other.get(), rule) > other.get().getInventory().getFreeSlots()) {
				player.getActionSender().sendMessage("The other player does not have enough space.");
				return false;
			}

			rules.alternate(rule);
			other.get().getDuelArena().setRules(rules);

			if (rule.getButtonId() >= 31034 && rule.getButtonId() <= 31056) {
				player.getActionSender().sendConfig(rule.getValue(), rules.get(rule) ? 1 : 0);
				other.get().getActionSender().sendConfig(rule.getValue(), rules.get(rule) ? 1 : 0);
			} else {
				if (rules.get(rule)) {
					rules.incrementValue(rule.getValue());
				} else {
					rules.decrementValue(rule.getValue());
				}

				player.getActionSender().sendToggle(286, rules.getConfigValue());
				other.get().getActionSender().sendToggle(286, rules.getConfigValue());
			}
			player.getDuelArena().setAccepted(false);
			other.get().getDuelArena().setAccepted(false);

			player.getActionSender().sendString("", 31009);
			other.get().getActionSender().sendString("", 31009);
		}
		return meets;
	}

	static int inventorySlotsRequired(Player player, DuelRule rule) {
		int required = 0;

		DuelRules tempRules = new DuelRules();

		tempRules.setFlags(player.getDuelArena().getRules().getFlags());

		tempRules.alternate(rule);

		if (player.getEquipment().hasHead()) {
			if (tempRules.get(DuelRule.HEAD)) {
				required++;
			}
		}

		if (player.getEquipment().hasNecklace()) {
			if (tempRules.get(DuelRule.NECKLACE)) {
				required++;
			}
		}

		if (player.getEquipment().hasWeapon()) {
			Item item = player.getEquipment().get(EquipmentConstants.WEAPON_SLOT);
			if (item != null) {
				String name = item.getName().toLowerCase();

				CombatStyle type = WeaponDefinition.get(item.getId()).getCombatType();

				if (tempRules.get(DuelRule.MELEE)) {
					if (type == CombatStyle.MELEE) {
						required++;
					}
				}

				if (tempRules.get(DuelRule.MAGIC)) {
					if (type == CombatStyle.MAGIC) {
						required++;
					}
				}

				if (tempRules.get(DuelRule.RANGED)) {
					if (type == CombatStyle.RANGE) {
						required++;
					}
				}

				if (tempRules.get(DuelRule.WEAPON)) {
					required++;
				}
				if (tempRules.get(DuelRule.WHIP_DDS)) {

					if (!name.contains("whip") && !name.contains("abyssal tentacle") && !name.contains("dragon dagger")) {
						required++;
					}
				}
			}
		}

		if (player.getEquipment().hasCape()) {
			if (tempRules.get(DuelRule.CAPE)) {
				required++;
			}
		}

		if (player.getEquipment().hasAmmo()) {
			if (tempRules.get(DuelRule.AMMO)) {
				required++;
			}
		}

		if (player.getEquipment().hasTorso()) {
			if (tempRules.get(DuelRule.BODY)) {
				required++;
			}
		}

		if (tempRules.get(DuelRule.SHIELD)) {

			if (player.getEquipment().hasShield()) {
				required++;
			}

			Item weapon = player.getEquipment().get(EquipmentConstants.WEAPON_SLOT);

			if (player.getEquipment().hasWeapon()) {
				if (weapon != null) {
					if (WeaponDefinition.get(weapon.getId()).isTwoHanded()) {
						required++;
					}
				}
			}
		}

		if (player.getEquipment().hasLegs()) {
			if (tempRules.get(DuelRule.LEGS)) {
				required++;
			}
		}

		if (player.getEquipment().hasGloves()) {
			if (tempRules.get(DuelRule.GLOVES)) {
				required++;
			}
		}

		if (player.getEquipment().hasBoots()) {
			if (tempRules.get(DuelRule.BOOTS)) {
				required++;
			}
		}

		if (player.getEquipment().hasRing()) {
			if (tempRules.get(DuelRule.RINGS)) {
				required++;
			}
		}

		tempRules.alternate(rule);
		return required;
	}

	/**
	 * Determines if a rule is set.
	 *
	 * @param player The player whos rule to check.
	 * @return {@code true} If this flag is set. {@code false} Otherwise.
	 */
	boolean set(final Player player) {
		return set(player, player.getDuelArena().getRules(), this, condition);
	}

	/**
	 * @return the buttonId
	 */
	public int getButtonId() {
		return button;
	}

	/**
	 * @return the value
	 */
	public int getValue() {
		return value;
	}

	/**
	 * @return the slot
	 */
	public int getSlot() {
		return slot;
	}

	/**
	 * @return the condition
	 */
	public RuleCondition getCondition() {
		return condition;
	}

}
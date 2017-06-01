package com.model.utility.json.definitions;

import java.util.HashMap;
import java.util.Map;

import com.model.game.character.combat.weapon.AttackStyle;
import com.model.game.character.player.Player;
import com.model.game.item.Item;
import com.model.game.item.container.container.impl.EquipmentContainer;

/**
 * The container class that represents all the weapon definitions.
 *
 * @author <a href="http://www.rune-server.org/members/_Patrick_/">Patrick van Elderen</a>
 */
public class WeaponDefinition {
	
	/**
     * The hash collection of our weapon definitions.
     */
	private static final Map<Integer, WeaponDefinition> DEFINITIONS = new HashMap<>();

	/**
	 * Grabs the definition by id
	 * @param id
	 *        The id
	 * @return the definition
	 */
	public static WeaponDefinition get(int id) {
		return DEFINITIONS.get(id);
	}

	/**
	 * @return the weaponDefinitions
	 */
	public static Map<Integer, WeaponDefinition> getWeaponDefinitions() {
		return DEFINITIONS;
	}

	/**
	 * The identifier
	 */
	private final int id;
	
	/**
	 * The name
	 */
	private final String name;
	
	/**
	 * the attack speed
	 */
	private final int attackSpeed;
	
	/**
	 * The equipment animations
	 */
	private WeaponAnimation equipmentAnimations;
	
	/**
	 * The block animation
	 */
	private final int blockAnimation;
	
	/**
	 * The attack animations
	 */
	private final int[] attackAnimations;

	/**
	 * Creates a new {@link WeaponDefinition}.
	 * 
	 * @param id
	 *            the identifier
	 * @param name
	 *            the weapon name
	 * @param attackSpeed
	 *            the attack speed
	 * @param equipmentAnimation
	 *            the equipment animations
	 * @param blockAnimation
	 *            the block animation
	 * @param attackAnimations
	 *            the attack animations
	 */
	public WeaponDefinition(int id, String name, int attackSpeed, WeaponAnimation equipmentAnimation, int blockAnimation, int[] attackAnimations) {
		this.id = id;
		this.name = name;
		this.attackSpeed = attackSpeed;
		this.equipmentAnimations = equipmentAnimation;
		this.blockAnimation = blockAnimation;
		this.attackAnimations = attackAnimations;
	}
	
	/**
	 * The item identifier
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * The name of the weapon
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * The weapons attack speed
	 * @return the attackSpeed
	 */
	public int getAttackSpeed() {
		return attackSpeed;
	}
	
	/**
	 * The equipment animations of the weapon
	 * @return the equipmentAnimations
	 */
	public WeaponAnimation getEquipmentAnimations() {
		return equipmentAnimations;
	}
	
	/**
	 * Gets the weapons block animation
	 * @return the blockAnimation
	 */
	public int getBlockAnimation() {
		return blockAnimation;
	}
	
	/**
	 * Gets the weapons attack animations
	 * @return the attackAnimations
	 */
	public int[] getAttackAnimations() {
		return attackAnimations;
	}
	
	/**
	 * Sends the attack speed for each weapon/ magic spell during combat.
	 * 
	 * @param player
	 *            The player wielding the weapon/ performing a magic spell.
	 * @return the attack speed
	 */
	public static int sendAttackSpeed(Player player) {
		Item weapon = new Item(player.getEquipment().get(EquipmentContainer.WEAPON_SLOT));
		
		if (player.usingMagic) {
			switch (player.MAGIC_SPELLS[player.getSpellId()][0]) {
			default:
				return 5;
			}
		}

		if (weapon.getId() <= 0) {
			return 4;
		} else if (weapon.getId() == 12926) {
			return player.getAttackStyle() == AttackStyle.AGGRESSIVE ? player.getCombatState().getTarget().isPlayer() ? 3 : 2 : player.getCombatState().getTarget().isPlayer() ? 4 : 3;
		} else {
			return WeaponDefinition.get(weapon.getId()).getAttackSpeed();
		}
	}

	/**
	 * Sends the players block animation during combat.
	 * 
	 * @param player
	 *            The player doing the block animation.
	 * @return the block animation
	 */
	public static int sendBlockAnimation(Player player) {
		//weapon instance
		Item weapon = player.getEquipment().get(EquipmentContainer.WEAPON_SLOT);
		
		//shield instance
		Item shield = player.getEquipment().get(EquipmentContainer.SHIELD_SLOT);

		//grab by name
		String shieldName = shield.getName().toLowerCase();

		if (shieldName != null) {
			if (shieldName.contains("shield") || shieldName.contains("kite") || shieldName.contains("ward")) {
				return 1156;
			}
			if (shieldName.endsWith("defender")) {
				return 4177;
			}

			if (shieldName.contains("toktz-ket-xil")) {
				return 1156;
			}
		}
		if (weapon.getId() == -1) // empty hands
			return 424;
		else
			return WeaponDefinition.get(weapon.getId()).getBlockAnimation(); // wep anim
	}
	
}

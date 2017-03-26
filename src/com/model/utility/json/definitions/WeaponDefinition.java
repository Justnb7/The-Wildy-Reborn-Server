package com.model.utility.json.definitions;

import java.util.HashMap;
import java.util.Map;

import com.model.game.character.player.Player;
import com.model.game.item.Item;

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

	public int sendBlockAnimation(Player player) {
		Item shield = new Item(player.playerEquipment[player.getEquipment().getShieldId()]);
		
		String byName = shield.getName().toLowerCase();
		
		if (byName != null) {
			if (byName.contains("shield") || byName.contains("kite") || byName.contains("ward")) {
				return 1156;
			}
			if (byName.endsWith("defender")) {
				return 4177;
			}
			
			if (byName.contains("toktz-ket-xil")) {
				return 1156;
			}
		} else {
			return getBlockAnimation();
		}
		
		return 424;
	}
	
}

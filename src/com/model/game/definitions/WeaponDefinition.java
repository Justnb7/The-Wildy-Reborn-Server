package com.model.game.definitions;

import java.util.HashMap;
import java.util.Map;

import com.model.game.character.combat.combat_data.CombatStyle;
import com.model.game.character.combat.weapon.AttackStyle;
import com.model.game.character.player.Player;
import com.model.game.item.Item;
import com.model.game.item.container.impl.equipment.EquipmentConstants;

/**
 * The definition for all weapons.
 * 
 * @author SeVen
 */
public class WeaponDefinition {

	/**
	 * Represents the enumerated types of ammo.
	 */
	public static enum AmmoType {
		SHOT,
		DOUBLE_SHOT,
		THROWN
	}

	// TODO: Finish the weapon types
	public static enum WeaponType {
		DEFAULT(5855, 5857, new SpecialAttackDefinition(7749, 7761, 7737)),
		THROWN(4446, 4449, new SpecialAttackDefinition(7649, 7661, 7637)),
		BOW(1764, 1767, new SpecialAttackDefinition(7549, 7561, 7548)),
		WHIP(12290, 12293, new SpecialAttackDefinition(12323, 12335, 12322)),
		WARHAMMER_OR_MAUL(425, 428, new SpecialAttackDefinition(7474, 7486, 7473)),
		SPEAR(4679, 4682, new SpecialAttackDefinition(7674, 7686, 7662)),
		STAFF(6103, 6132, new SpecialAttackDefinition(6117, 6129, 6104)),
		MAGIC_STAFF(328, 355, new SpecialAttackDefinition(18566, 18569, 340)),
		HALBERD(8460, 8463, new SpecialAttackDefinition(8493, 8505, 8481)),
		SWORD_OR_DAGGER(2276, 2279, new SpecialAttackDefinition(7574, 7586, 7562)),
		TWO_HANDED(4705, 4708, new SpecialAttackDefinition(7699, 7711, 7687)),
		LONGSWORD_OR_SCIMITAR(2423, 2426, new SpecialAttackDefinition(7599, 7611, 7587)),
		PICKAXE(5570, 5573, new SpecialAttackDefinition(7724, 7736, 7723)),
		BATTLEAXE(1698, 1701, new SpecialAttackDefinition(7499, 7591, 7498)),
		CLAWS(7762, 7765, new SpecialAttackDefinition(7800, 7812, 7788)),
		MACE(3796, 3799, new SpecialAttackDefinition(7624, 7636, 7623));

		private final int interfaceId;
		private final int stringId;
		private final SpecialAttackDefinition special;
		
		private WeaponType(int interfaceId, int stringId, SpecialAttackDefinition special) {
			this.interfaceId = interfaceId;
			this.stringId = stringId;
			this.special = special;
		}
		
		public int getInterfaceId() {
			return interfaceId;
		}
		
		public int getStringId() {
			return stringId;
		}
		
		public int getLayerId() {
			return special.getLayerId();
		}
		
		public int getSpecialStringId() {
			return special.getStringId();
		}
		
		public int getButton() {
			return special.getButton();
		}
		
		public int getSound() {
			return 0;
		}
	}

	public static class RangedWeaponDefinition {
		private final AmmoType type;
		private final int[] arrowsAllowed;

		public RangedWeaponDefinition(int[] allowed, AmmoType type) {
			this.type = type;
			this.arrowsAllowed = allowed;
		}

		public int[] getArrowsAllowed() {
			return arrowsAllowed;
		}

		public AmmoType getType() {
			return type;
		}

	}

	public static class SpecialAttackDefinitions {

		private final int barId1;
		private final int barId2;
		private final int buttonId;
		private final int soundId;
		private final double amount;

		public SpecialAttackDefinitions(int barId1, int barId2, int buttonId, int soundId, double amount) {
			this.barId1 = barId1;
			this.barId2 = barId2;
			this.buttonId = buttonId;
			this.soundId = -1;
			this.amount = amount;
		}

		public double getAmount() {
			return amount;
		}

		public int getBarId1() {
			return barId1;
		}

		public int getBarId2() {
			return barId2;
		}

		public int getButton() {
			return buttonId;
		}

		public int getSoundId() {
			return soundId;
		}

	}

	public static class SpecialAttackDefinition {
		private final int layerId;
		private final int stringId;
		private final int buttonId;

		public SpecialAttackDefinition(int layerId, int stringId, int buttonId) {
			this.layerId = layerId;
			this.stringId = stringId;
			this.buttonId = buttonId;
		}

		public int getLayerId() {
			return layerId;
		}

		public int getStringId() {
			return stringId;
		}

		public int getButton() {
			return buttonId;
		}
	}

	private static final Map<Integer, WeaponDefinition> DEFINITIONS = new HashMap<>();

	public static WeaponDefinition get(int id) {
		return DEFINITIONS.get(id);
	}

	/**
	 * @return the weaponDefinitions
	 */
	public static Map<Integer, WeaponDefinition> getWeaponDefinitions() {
		return DEFINITIONS;
	}

	private final int id;
	private final String name;
	private WeaponType type;
	private final CombatStyle combatType;
	private final RangedWeaponDefinition rangedWeapon;
	private final boolean twoHanded;
	private final int blockAnimation;
	private final int standAnimation;
	private final int walkAnimation;
	private final int runAnimation;
	private final int attackSpeed;
	private final int[] animations;

	public WeaponDefinition(int id, String name, WeaponType type, CombatStyle combatType, RangedWeaponDefinition rangedWeaponDefinition, boolean twoHanded, int blockAnimation, int standAnimation, int walkAnimation, int runAnimation, int attackSpeed, int[] animations) {
		this.id = id;
		this.name = name;
		this.type = type;
		this.combatType = combatType;
		this.rangedWeapon = rangedWeaponDefinition;
		this.twoHanded = twoHanded;
		this.blockAnimation = blockAnimation;
		this.standAnimation = standAnimation;
		this.walkAnimation = walkAnimation;
		this.runAnimation = runAnimation;
		this.attackSpeed = attackSpeed;
		this.animations = animations;
	}

	public WeaponType getType() {
		return type;
	}

	public int[] getAnimations() {
		return animations;
	}

	/**
	 * @return the attack speed
	 */
	public int getAttackSpeed() {
		return attackSpeed;
	}

	/**
	 * @return the blockAnimation
	 */
	public int getBlockAnimation() {
		return blockAnimation;
	}

	/**
	 * @return the attackType
	 */
	public CombatStyle getCombatType() {
		return combatType;
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
	 * @return the rangedWeapon
	 */
	public RangedWeaponDefinition getRangedWeapon() {
		return rangedWeapon;
	}

	/**
	 * @return the runAnimation
	 */
	public int getRunAnimation() {
		return runAnimation;
	}

	/**
	 * @return the standAnimation
	 */
	public int getStandAnimation() {
		return standAnimation;
	}

	/**
	 * @return the walkAnimation
	 */
	public int getWalkAnimation() {
		return walkAnimation;
	}

	/**
	 * @return the twoHanded
	 */
	public boolean isTwoHanded() {
		return twoHanded;
	}
	
	/**
     * The method executed when weapon {@code item} is equipped that assigns a
     * weapon animation to {@code player}.
     *
     * @param player
     *            the player equipping the item.
     * @param item
     *            the item the player is equipping.
     */
    public static void execute(Player player, Item item) {
    	final WeaponDefinition def = WeaponDefinition.get(item.getId());
    	if(def == null || item == null) {
    		return;
    	}
        player.setStandAnimation(def.getStandAnimation());
        player.setWalkAnimation(def.getWalkAnimation());
        player.setRunAnimation(def.getRunAnimation());
    }
	
	/**
	 * Sends the attack speed for each weapon/ magic spell during combat.
	 * 
	 * @param player
	 *            The player wielding the weapon/ performing a magic spell.
	 * @return the attack speed
	 */
	public static int sendAttackSpeed(Player player) {
		Item weapon = new Item(player.getEquipment().get(EquipmentConstants.WEAPON_SLOT));
		
		if (player.getCombatType() == CombatStyle.MAGIC) {
			return 5;
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
		Item weapon = player.getEquipment().get(EquipmentConstants.WEAPON_SLOT);
		
		//shield instance
		Item shield = player.getEquipment().get(EquipmentConstants.SHIELD_SLOT);

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

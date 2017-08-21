package com.venenatis.game.model.container.impl.equipment;

import com.venenatis.game.constants.EquipmentConstants;
import com.venenatis.game.model.Item;
import com.venenatis.game.model.combat.Combat;
import com.venenatis.game.model.container.Container;
import com.venenatis.game.model.container.impl.InterfaceConstants;
import com.venenatis.game.model.definitions.EquipmentDefinition;
import com.venenatis.game.model.definitions.EquipmentDefinition.EquipmentType;
import com.venenatis.game.model.definitions.WeaponDefinition;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.equipment.EquipmentRequirement;
import com.venenatis.game.model.masks.UpdateFlags.UpdateFlag;

import java.util.Arrays;

/**
 * Holds the player's equipment items.
 * 
 * @author Michael | Chex
 */
public class EquipmentContainer extends Container {

	private final Player player;

	public EquipmentContainer(Player player) {
		super(14, ContainerType.ALWAYS_STACK);
		this.player = player;
	}

	@Override
	public void clear(boolean refresh) {
		super.clear(refresh);

		Arrays.fill(player.getBonuses(), 0);
	}

	public boolean isWearingItems() {
		if (getTakenSlots() > 0) {
			return true;
		}
		return false;
	}

	/**
	 * Gets the weapon this player is wielding.
	 * 
	 * @return The weapon.
	 */
	public Item getWeapon() {
		return this.get(EquipmentConstants.WEAPON_SLOT);
	}

	/**
	 * Determines if a {@link Player} has a weapon.
	 * 
	 * @return {@code true} If there is a weapon in the weapon slot.
	 *         {@code false} otherwise.
	 */
	public boolean hasWeapon() {
		return this.get(EquipmentConstants.WEAPON_SLOT) != null;
	}

	public boolean hasHead() {
		return this.get(EquipmentConstants.HELM_SLOT) != null;
	}

	public boolean hasCape() {
		return this.get(EquipmentConstants.CAPE_SLOT) != null;
	}

	public boolean hasNecklace() {
		return this.get(EquipmentConstants.NECKLACE_SLOT) != null;
	}

	public boolean hasAmmo() {
		return this.get(EquipmentConstants.AMMO_SLOT) != null;
	}

	public boolean hasTorso() {
		return this.get(EquipmentConstants.TORSO_SLOT) != null;
	}

	public boolean hasShield() {
		return this.get(EquipmentConstants.SHIELD_SLOT) != null;
	}

	public boolean hasLegs() {
		return this.get(EquipmentConstants.LEGS_SLOT) != null;
	}

	public boolean hasGloves() {
		return this.get(EquipmentConstants.GLOVES_SLOT) != null;
	}

	public boolean hasBoots() {
		return this.get(EquipmentConstants.BOOTS_SLOT) != null;
	}

	public boolean hasRing() {
		return this.get(EquipmentConstants.RING_SLOT) != null;
	}

	@Override
	public void refresh() {
		player.getActionSender().sendItemOnInterface(InterfaceConstants.EQUIPMENT, stack);
		player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
		updateWieldItemName();
	}

	@Override
	public void refresh(int... slots) {
		for (final int slot : slots) {
			player.getActionSender().sendItemOnInterfaceSlot(InterfaceConstants.EQUIPMENT, stack[slot], slot);
		}
		player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
	}

	/** Gets the item player.getBonuses() from item_definitions.json */
	public void setBonus() {
		
		EquipmentContainer.calcBonuses(player);
		
		if(player.getEquipment().contains(12926)) {
			
			int chargedWith = player.getToxicBlowpipeAmmo();
			EquipmentDefinition chargeDef = EquipmentDefinition.get(chargedWith);
			player.getBonuses()[4] += chargeDef.getBonuses()[4];// ranged attack of toxic blowpipe + ranged attack of the loaded dart
			player.getBonuses()[11] += chargeDef.getBonuses()[11];// ranged strength of toxic blowpipe + ranged strength of the loaded dart
		}
		
		//Bonuses sent to the original interface frames
        for (int i = 0; i < 10; i++) {
        	player.getActionSender().sendString(Combat.BONUS_NAMES[i] + ": " + (player.getBonuses()[i] >= 0 ? "+" : "") + player.getBonuses()[i], (1675 + i));
        }
        //Bonuses sent to the custom made frames
        for (int bonus = 10; bonus < 16; bonus++) {
			if (bonus == 12 || bonus == 14 || bonus == 15) {
				player.getActionSender().sendString(Combat.BONUS_NAMES[bonus] + ": " + (player.getBonuses()[bonus] >= 0 ? "+" : "") + player.getBonuses()[bonus] + "%", (15115 + bonus - 10));
			} else {
				player.getActionSender().sendString(Combat.BONUS_NAMES[bonus] + ": " + (player.getBonuses()[bonus] >= 0 ? "+" : "") + player.getBonuses()[bonus], (15115 + bonus - 10));
			}
			
			//Debug
			//player.getActionSender().sendMessage(Combat.BONUS_NAMES[bonus]+" VS "+ (bonuses[bonus] >= 0 ? "+" : "")+ " VS "+ (15115 + bonus - 10));
		}
	}

	private static void calcBonuses(Player player) {
		calcBonuses(player, false);
	}
	public static void calcBonuses(Player player, boolean ignoreArrows) {
		Arrays.fill(player.getBonuses(), 0);
		for (int index = 0; index < player.getBonuses().length; index++) {
			if (index == EquipmentConstants.AMMO_SLOT && ignoreArrows)
				continue; 
			final Item item = player.getEquipment().get(index);
			
			if (item != null) {
				EquipmentDefinition def = EquipmentDefinition.EQUIPMENT_DEFINITIONS.get(item.getId());

				for (int slot = 0; slot < Math.min(player.getBonuses().length, def.getBonuses().length); slot++) {
					player.getBonuses()[slot] += def.getBonuses()[slot];
				}
			}
		}
	}

	/**
	 * Calculates and writes the players weight to the equipment equipment
	 * sidebar interface.
	 */
    public double calculateWeight() {
    	double weight = 0;
    	
    	Item[] inv_items = player.getInventory().toNonNullArray();
		for (Item inventory : inv_items) {
			 weight += inventory.getWeight();
			 //player.debug("inventory: "+inventory.getWeight());
		}
		
		Item[] equipment_items = player.getEquipment().toNonNullArray();
		for(Item equipment : equipment_items) {
			weight += equipment.getWeight();
			//player.debug("equipment: "+weight);
		}
		
		int weightToInt = (int) weight;
		player.getActionSender().sendString(weightToInt+" kg", 15122);
    	return weight;
    }

	/** Unequips an item. */
	public void unequip(int slot) {
		final int added = player.getInventory().add(get(slot));
		if (added > 0) {
			setSlot(slot, null);
			if (slot == EquipmentConstants.WEAPON_SLOT) {
				updateWeapon();
				player.setUsingSpecial(false);
			}
			player.setDefaultAnimations();
            player.getWeaponInterface().restoreWeaponAttributes();
			player.getCombatState().reset();
			player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
			setBonus();
		}
	}

	public void updateWeapon() {
		updateWieldItemName();
        player.getWeaponInterface().restoreWeaponAttributes();
        player.setSpellId(-1);
        player.setAutocastId(-1);
        player.autoCast = false;
	}

	/**
	 * Updates the item name that is being wielded on the special attack tab.
	 */
	public void updateWieldItemName() {
		final Item item = player.getEquipment().getWeapon();
		String name = "Unarmed";
		int sidebarId = 5855;

		if (item != null && WeaponDefinition.get(item.getId()) != null) {
			final WeaponDefinition def = WeaponDefinition.get(item.getId());
			name = def.getName();
			sidebarId = def.getType().getInterfaceId();
			WeaponDefinition.execute(player, item);
		}

		player.getActionSender().sendString(name, EquipmentConstants.getTextIdForInterface(sidebarId));
		player.getActionSender().sendSidebarInterface(0, sidebarId);
	}

	public boolean wear(int clickSlot, boolean check, boolean refresh, Item... items) {
		boolean success = true;

		for (final Item item : items) {
			if (item == null) {
				continue;
			}
			success &= wear(item, clickSlot, false, check);
		}

		if (refresh) {
			refresh();
		}

		return success;
	}

	/** Wielding items. */
	public boolean wear(Item item, int clickSlot) {
		return wear(item, clickSlot, true);
	}

	public boolean wear(Item item, int clickSlot,  boolean check) {
		return wear(item, clickSlot, true, check);
	}

	public boolean wear(Item item, boolean refresh, boolean check) {
		if (check && !player.getInventory().contains(i -> i.getId() == item.getId() && i.getAmount() >= item.getAmount())) {
			return false;
		}

		final EquipmentDefinition def = EquipmentDefinition.EQUIPMENT_DEFINITIONS.get(item.getId());

		if (def == null || def.getType() == EquipmentType.NOT_WIELDABLE) {
			return false;
		}

		if (!EquipmentRequirement.canEquip(player, item.getId())) {
			return false;
		}

		if (def.getType() == EquipmentType.WEAPON || def.getType() == EquipmentType.SHIELD && get(EquipmentConstants.WEAPON_SLOT) != null) {
			final Item weapon = def.getType() == EquipmentType.WEAPON ? new Item(def.getId(), item.getAmount()) : get(EquipmentConstants.WEAPON_SLOT);
			final Item shield = def.getType() == EquipmentType.SHIELD ? new Item(def.getId(), item.getAmount()) : get(EquipmentConstants.SHIELD_SLOT);

			final WeaponDefinition weaponDef = WeaponDefinition.get(weapon.getId());
			if (weaponDef != null && weaponDef.isTwoHanded()) {
				int required = 0;

				if (def.getType() == EquipmentType.WEAPON && get(EquipmentConstants.SHIELD_SLOT) != null && get(EquipmentConstants.WEAPON_SLOT) != null) {
					required++;
				}

				if (player.getInventory().getFreeSlots() < required) {
					player.getActionSender().sendMessage("You don't have enough inventory space to wield that.");
					return false;
				}

				if (def.getType() == EquipmentType.WEAPON) {
					player.getInventory().setSlot(player.getInventory().indexOf(item), get(EquipmentConstants.WEAPON_SLOT), refresh);
					setSlot(EquipmentConstants.SHIELD_SLOT, null, refresh);
					setSlot(EquipmentConstants.WEAPON_SLOT, weapon, refresh);
					player.getInventory().add(shield, refresh);
				} else {
					setSlot(EquipmentConstants.WEAPON_SLOT, null, refresh);
					setSlot(EquipmentConstants.SHIELD_SLOT, shield, refresh);
					player.getInventory().setSlot(player.getInventory().indexOf(item), weapon, refresh);
				}

				if (refresh) {
					updateWeapon();
					WeaponDefinition.execute(player, weapon);
					player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
				}
				calculateWeight();
				setBonus();
				player.getCombatState().reset();
				player.setUsingSpecial(false);
				
				return true;
			}
		}

		final int slot = def.getType().getSlot();

		if (item.isStackable() && contains(item.getId())) {
			final int added = add(item, refresh);
			player.getInventory().removeSlot(player.getInventory().indexOf(item), added, refresh);
		} else {
			final Item current = get(slot);
			setSlot(slot, item, refresh);

			if (check) {
				player.getInventory().setSlot(player.getInventory().indexOf(item), current, refresh);
			}
		}

		if (slot == EquipmentConstants.WEAPON_SLOT) {
			if (refresh) {
				updateWeapon();
				player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
			}
			player.setSpellId(-1);
			player.setAutocastId(-1);
	        player.autoCast = false;
			player.setUsingSpecial(false);
		}

		setBonus();
		player.getCombatState().reset();
		return true;
	}
	
	public boolean wear(Item item, int clickSlot, boolean refresh, boolean check) {
		if (check && !player.getInventory().contains(i -> i.getId() == item.getId() && i.getAmount() >= item.getAmount())) {
			return false;
		}

		final EquipmentDefinition def = EquipmentDefinition.EQUIPMENT_DEFINITIONS.get(item.getId());

		if (def == null || def.getType() == EquipmentType.NOT_WIELDABLE) {
			return false;
		}

		if (!EquipmentRequirement.canEquip(player, item.getId())) {
			return false;
		}

		if (def.getType() == EquipmentType.WEAPON || def.getType() == EquipmentType.SHIELD && get(EquipmentConstants.WEAPON_SLOT) != null) {
			final Item weapon = def.getType() == EquipmentType.WEAPON ? new Item(def.getId(), item.getAmount()) : get(EquipmentConstants.WEAPON_SLOT);
			final Item shield = def.getType() == EquipmentType.SHIELD ? new Item(def.getId(), item.getAmount()) : get(EquipmentConstants.SHIELD_SLOT);

			final WeaponDefinition weaponDef = WeaponDefinition.get(weapon.getId());
			if (weaponDef != null && weaponDef.isTwoHanded()) {
				int required = 0;
				
				if (def.getType() == EquipmentType.WEAPON && get(EquipmentConstants.SHIELD_SLOT) != null && get(EquipmentConstants.WEAPON_SLOT) != null) {
					required++;
				}
				
				if (player.getInventory().getFreeSlots() < required) {
					player.getActionSender().sendMessage("You don't have enough inventory space to wield that.");
					return false;
				}
				
				if (def.getType() == EquipmentType.WEAPON) {
					player.getInventory().setSlot(clickSlot, get(EquipmentConstants.WEAPON_SLOT), refresh);
					setSlot(EquipmentConstants.SHIELD_SLOT, null, refresh);
					setSlot(EquipmentConstants.WEAPON_SLOT, weapon, refresh);
					player.getInventory().add(shield, refresh);
				} else {
					setSlot(EquipmentConstants.WEAPON_SLOT, null, refresh);
					setSlot(EquipmentConstants.SHIELD_SLOT, shield, refresh);
					player.getInventory().setSlot(clickSlot, weapon, refresh);
				}

				if (refresh) {
					updateWeapon();
					WeaponDefinition.execute(player, weapon);
					player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
				}
				
				setBonus();
				player.getCombatState().reset();
				player.setUsingSpecial(false);
				return true;
			}
		}

		final int slot = def.getType().getSlot();

		if (item.isStackable() && contains(item.getId())) {
			final int added = add(item, refresh);
			player.getInventory().removeSlot(clickSlot, added, refresh);
		} else {
			final Item current = get(slot);
			setSlot(slot, item, refresh);

			if (check) {
				player.getInventory().setSlot(clickSlot, current, refresh);
			}
		}

		if (slot == EquipmentConstants.WEAPON_SLOT) {
			if (refresh) {
				updateWeapon();
				player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
			}

			player.setSpellId(-1);
			player.setAutocastId(-1);
	        player.autoCast = false;
			player.setUsingSpecial(false);
		}

		setBonus();
		player.getCombatState().reset();
		return true;
	}

}
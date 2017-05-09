package com.model.game.item.container.impl;

import com.model.UpdateFlags.UpdateFlag;
import com.model.game.character.player.Player;
import com.model.game.item.Item;
import com.model.game.item.container.Container;
import com.model.game.item.container.ItemContainerPolicy;
import com.model.utility.json.definitions.Requirement;
import com.model.utility.json.definitions.WeaponAnimation;

/**
 * The container that manages the equipment for a player.
 *
 * @author lare96 <http://github.com/lare96>
 */
public final class Equipment extends Container {
	
	/**
	 * The size of the equipment container.
	 */
	public static final int SIZE = 14;

    /**
     * The head identification equipment slot.
     */
    public static final int HEAD_SLOT = 0;

    /**
     * The cape identification equipment slot.
     */
    public static final int CAPE_SLOT = 1;

    /**
     * The amulet identification equipment slot.
     */
    public static final int AMULET_SLOT = 2;

    /**
     * The weapon identification equipment slot.
     */
    public static final int WEAPON_SLOT = 3;

    /**
     * The chest identification equipment slot.
     */
    public static final int CHEST_SLOT = 4;

    /**
     * The shield identification equipment slot.
     */
    public static final int SHIELD_SLOT = 5;

    /**
     * The legs identification equipment slot.
     */
    public static final int LEGS_SLOT = 7;

    /**
     * The hands identification equipment slot.
     */
    public static final int HANDS_SLOT = 9;

    /**
     * The feet identification equipment slot.
     */
    public static final int FEET_SLOT = 10;

    /**
     * The ring identification equipment slot.
     */
    public static final int RING_SLOT = 12;

    /**
     * The arrows identification equipment slot.
     */
    public static final int ARROWS_SLOT = 13;

    /**
     * The player who's equipment is being managed.
     */
    private final Player player;

    /**
     * Creates a new {@link Equipment}.
     *
     * @param player
     *            the player who's equipment is being managed.
     */
    public Equipment(Player player) {
        super(ItemContainerPolicy.NORMAL, 14);
        this.player = player;
    }

    /**
     * Refreshes the contents of this equipment container to the interface.
     */
    public void refresh() {
        refresh(player, 1688);
        player.sendBonus();
    }

    /**
     * Equips the item in {@code inventorySlot} to the equipment container.
     *
     * @param inventorySlot
     *            the slot to equip the item on.
     * @return {@code true} if the item was equipped, {@code false} otherwise.
     */
    public boolean equipItem(int inventorySlot) {
    	//System.out.println("Enter");
        Item item = player.getInventory().get(inventorySlot);
        if (!Item.valid(item)) {
        	//System.out.println("Oops");
            return false;
        }
        if (!Requirement.canEquip(player, item)) {
        	//System.out.println("Can we even equip");
            return false;
        }
        if (item.getDefinition().isStackable()) {
        	//System.out.println("Enter next part");
            int designatedSlot = item.getDefinition().getEquipmentSlot();
            Item equipItem = get(designatedSlot);
            if (used(designatedSlot)) {
            	//System.out.println("Enter part 3");
                if (item.getId() == equipItem.getId()) {
                    set(designatedSlot, new Item(item.getId(), item.getAmount() + equipItem.getAmount()));
                    System.out.println("Set equipment");
                } else {
                    player.getInventory().set(inventorySlot, equipItem);
                    player.getInventory().refresh();
                    set(designatedSlot, item);
                   // System.out.println("Don't know what this does");
                }
            } else {
                set(designatedSlot, item);
                //System.out.println("Same here");
            }
            player.getInventory().remove(item, inventorySlot);
        } else {
            int designatedSlot = item.getDefinition().getEquipmentSlot();
            if (designatedSlot == Equipment.WEAPON_SLOT && item.getDefinition().isTwoHanded() && used(Equipment.SHIELD_SLOT)) {
                if (!unequipItem(Equipment.SHIELD_SLOT, true)) {
                    //System.out.println("Uneqip?");
                    return false;
                }
            }
            if (designatedSlot == Equipment.SHIELD_SLOT && used(Equipment.WEAPON_SLOT)) {
                if (get(Equipment.WEAPON_SLOT).getDefinition().isTwoHanded()) {
                    if (!unequipItem(Equipment.WEAPON_SLOT, true)) {
                        //System.out.println("2h?");
                        return false;
                    }
                }
            }
            if (used(designatedSlot)) {
                Item equipItem = get(designatedSlot);
                if (!equipItem.getDefinition().isStackable()) {
                    player.getInventory().set(inventorySlot, equipItem);
                } else {
                    player.getInventory().set(inventorySlot, null);
                    player.getInventory().add(equipItem, inventorySlot);
                }
                player.getInventory().refresh();
            } else {
                player.getInventory().remove(item, inventorySlot);
            }
            set(designatedSlot, new Item(item.getId(), item.getAmount()));
        }
        if (item.getDefinition().getEquipmentSlot() == Equipment.WEAPON_SLOT) {
            WeaponAnimation.execute(player, item);
            player.getWeaponInterface().restoreWeaponAttributes();
            player.spellId = -1;
            player.autocastId = -1;
            player.autoCast = false;
            player.getActionSender().sendConfig(108, 0);
            player.getActionSender().sendConfig(301, 0);
            player.setUsingSpecial(false);
        }
        refresh();
        player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
        //System.out.println("Finally update");
        return true;
    }

    /**
     * Unequips the item in {@code equipmentSlot} from the equipment container.
     *
     * @param equipmentSlot
     *            the slot to unequip the item on.
     * @param addItem
     *            if the unequipped item should be added to the inventory.
     * @return {@code true} if the item was unequipped, {@code false} otherwise.
     */
    public boolean unequipItem(int equipmentSlot, boolean addItem) {
        if (free(equipmentSlot))
            return false;
        Item item = get(equipmentSlot);
        if (!player.getInventory().spaceFor(item)) {
            player.getActionSender().sendMessage("You do not have enough space in " + "your inventory!");
            return false;
        }
        super.remove(item, equipmentSlot);
        if (addItem)
            player.getInventory().add(new Item(item.getId(), item.getAmount()));
        if (equipmentSlot == Equipment.WEAPON_SLOT) {
            player.getWeaponInterface().sendWeapon(-1, "Unarmed");
            player.spellId = -1;
            player.autocastId = -1;
            player.autoCast = false;
            player.getActionSender().sendConfig(108, 0);
            player.setDefaultAnimations();
            player.getActionSender().sendConfig(301, 0);
            player.setUsingSpecial(false);
        }
        refresh();
        player.getInventory().refresh();
        player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
        return true;
    }

    /**
     * Unequips {@code item} from the equipment container.
     *
     * @param item
     *            the item to unequip from this container.
     * @param addItem
     *            if the unequipped item should be added to the inventory.
     * @return {@code true} if the item was unequipped, {@code false} otherwise.
     */
    public boolean unequipItem(Item item, boolean addItem) {
        int slot = super.searchSlot(item.getId());
        if (slot == -1)
            return false;
        return unequipItem(slot, addItem);
    }

    /**
     * This method is not supported by this container implementation.
     *
     * @throws UnsupportedOperationException
     *             if this method is invoked by default, this method will always
     *             throw an exception.
     */
    @Override
    public boolean add(Item item, int slot) {
        throw new UnsupportedOperationException("This method is not supported" + " by this container implementation!");
    }

    /**
     * This method is not supported by this container implementation.
     *
     * @throws UnsupportedOperationException
     *             if this method is invoked by default, this method will always
     *             throw an exception.
     */
    @Override
    public boolean remove(Item item, int slot) {
        throw new UnsupportedOperationException("This method is not supported by this container implementation!");
    }
	
	public boolean usingCrystalBow(Player player) {
		return player.getEquipment().getId(Equipment.WEAPON_SLOT) == 4222;
	}
	
	public boolean wearingBlowpipe(Player player) {
		return player.getEquipment().getId(Equipment.WEAPON_SLOT) == 12926;
	}
	
	public boolean wearingBallista(Player player) {
		return player.getEquipment().getId(Equipment.WEAPON_SLOT) == 19481;
	}
	
	public boolean isCrossbow(Player player) {
		switch(player.getEquipment().getId(Equipment.WEAPON_SLOT)) {
		case 11785:
		case 9185:
		case 18357:
		case 21012:
			return true;
		}
		return false;
	}
	
	public boolean isThrowingWeapon(Player player) {
		switch (player.getEquipment().getId(Equipment.WEAPON_SLOT)) {
		// Javalins
		case 825:
		case 826:
		case 827:
		case 828:
		case 829:
		case 830:
			// Chinchompas
		case 10033:
		case 10034:
			// Thrownaxe
		case 800:
		case 801:
		case 802:
		case 803:
		case 804:
		case 805:
		case 6522:
			// Darts
		case 806:
		case 807:
		case 808:
		case 809:
		case 810:
		case 811:
			// Knives
		case 863:
		case 864:
		case 865:
		case 866:
		case 867:
		case 868:
		case 869:
			return true;
		}
		return false;
	}

	public boolean isBow(Player player) {
		switch (player.getEquipment().getId(Equipment.WEAPON_SLOT)) {
		case 839:
		case 841:
		case 843:
		case 845:
		case 847:
		case 849:
		case 851:
		case 853:
		case 855:
		case 857:
		case 859:
		case 861:
		case 4222:
		case 9705:
		case 12424:
		case 11235:
		case 6724:
		case 4734:
		case 20997:
			return true;
		}
		return false;
	}
	
	public boolean isArrow(Player player) {
		switch (player.getEquipment().getId(Equipment.WEAPON_SLOT)) {
		case 882:
		case 884:
		case 886:
		case 888:
		case 890:
		case 892:
		case 11212:
		case 78:
		case 4740:
			return true;
		}
		return false;
	}
	
	public boolean isBolt(Player player) {
		switch (player.getEquipment().getId(Equipment.ARROWS_SLOT)) {
		case 9140: 
		case 9141:
		case 4142: 
		case 9143: 
		case 9144: 
		case 9240: 
		case 9241: 
		case 9242: 
		case 9243:
		case 9244: 
		case 9245: 
		case 9706:
		return true;
		}
		
		return false;
	}
	
	public boolean usingRange(Player player) {
		if(wearingBlowpipe(player) || usingCrystalBow(player) || isCrossbow(player) || isThrowingWeapon(player) || isBow(player) || wearingBallista(player))
			return true;
		return false;
	}

	public final int[] VENEMOUS_WEPS = {12926, 12904, 12899, 12904};
	public final int[] VENEMOUS_HELMS = {13197, 13199, 12931};
	
	public boolean canInfect(Player player){
		for(int i : VENEMOUS_WEPS){
			if(player.getEquipment().getId(Equipment.WEAPON_SLOT) == i){
				return true;
			}
		}
		for(int i : VENEMOUS_HELMS){
			if(player.getEquipment().getId(Equipment.HEAD_SLOT) == i){
				return true;
			}
		}
		return false;
	}
}
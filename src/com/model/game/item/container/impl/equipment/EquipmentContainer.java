package com.model.game.item.container.impl.equipment;

import java.util.Arrays;

import com.model.UpdateFlags.UpdateFlag;
import com.model.game.Constants;
import com.model.game.character.player.Player;
import com.model.game.definitions.EquipmentDefinition;
import com.model.game.definitions.EquipmentDefinition.EquipmentType;
import com.model.game.definitions.ItemDefinition;
import com.model.game.definitions.WeaponDefinition;
import com.model.game.item.Item;
import com.model.game.item.ItemConstants;
import com.model.game.item.container.Container;
import com.model.game.item.container.InterfaceConstants;

/**
 * The container that manages the equipment for a player.
 *
 * @author lare96 <http://github.com/lare96>
 */
public final class EquipmentContainer extends Container {
	
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
     * Creates a new {@link EquipmentContainer}.
     *
     * @param player
     *            the player who's equipment is being managed.
     */
    public EquipmentContainer(Player player) {
        super(SIZE, ContainerType.DEFAULT);
        this.player = player;
    }
    
    @Override
	public void clear(boolean refresh) {
		super.clear(refresh);
		Arrays.fill(player.getBonuses(), 0);
	}
    
    @Override
	public void refresh() {
		player.getActionSender().sendUpdateItems(InterfaceConstants.EQUIPMENT, stack);
		player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
	}

	@Override
	public void refresh(int... slots) {
		for (final int slot : slots) {
			player.getActionSender().sendItemOnInterfaceSlot(InterfaceConstants.EQUIPMENT, stack[slot], slot);
		}
		player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
		player.setBonus();
	}

    /**
     * Equips the item in {@code inventorySlot} to the equipment container.
     *
     * @param inventorySlot
     *            the slot to equip the item on.
     * @return {@code true} if the item was equipped, {@code false} otherwise.
     */
    public boolean equipItem(int inventorySlot) {
    	Item item = player.getInventory().get(inventorySlot);
    	final EquipmentDefinition def = EquipmentDefinition.EQUIPMENT_DEFINITIONS.get(item.getId());
        if (!Item.valid(item)) {
        	//System.out.println("Oops");
            return false;
        }
        if (def == null || def.getType() == EquipmentType.NOT_WIELDABLE) {
        	return false;
		}
        if (!ItemConstants.canWear(item, player)) {
            return false;
        }
        if (item.isStackable()) {
        	//System.out.println("Enter next part");
        	
            int designatedSlot = def.getType().getSlot();
            Item equipItem = get(designatedSlot);
            if (used(designatedSlot)) {
            	//System.out.println("Enter part 3");
                if (item.getId() == equipItem.getId()) {
                	setSlot(designatedSlot, new Item(item.getId(), item.getAmount() + equipItem.getAmount()));
                    //System.out.println("Set equipment");
                } else {
                    player.getInventory().setSlot(inventorySlot, equipItem);
                    player.getInventory().refresh();
                    setSlot(designatedSlot, item);
                   // System.out.println("Don't know what this does");
                }
            } else {
                setSlot(designatedSlot, item);
                //System.out.println("Same here");
            }
            player.getInventory().remove(inventorySlot, item.getId(), true);
        } else {
            int designatedSlot = def.getType().getSlot();
            if (designatedSlot == EquipmentContainer.WEAPON_SLOT && WeaponDefinition.get(item.getId()).isTwoHanded() && used(EquipmentContainer.SHIELD_SLOT)) {
                if (!unequipItem(EquipmentContainer.SHIELD_SLOT, true)) {
                    //System.out.println("Uneqip?");
                    return false;
                }
            }
            if (designatedSlot == EquipmentContainer.SHIELD_SLOT && used(EquipmentContainer.WEAPON_SLOT)) {
                if (WeaponDefinition.get(item.getId()).isTwoHanded()) {
                    if (!unequipItem(EquipmentContainer.WEAPON_SLOT, true)) {
                        //System.out.println("2h?");
                        return false;
                    }
                }
            }
            if (used(designatedSlot)) {
                Item equipItem = get(designatedSlot);
                if (!equipItem.isStackable()) {
                    player.getInventory().setSlot(inventorySlot, equipItem);
                } else {
                    player.getInventory().setSlot(inventorySlot, null);
                    player.getInventory().add(equipItem, true);
                }
                player.getInventory().refresh();
            } else {
                player.getInventory().removeSlot(inventorySlot, item.getId(), true);
            }
            setSlot(designatedSlot, new Item(item.getId(), item.getAmount()));
        }
        player.debug("1212122");
        if (def.getType().getSlot() == EquipmentContainer.WEAPON_SLOT) {
        	player.debug("asasas");
            WeaponDefinition.execute(player, item);
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
        if (!player.getInventory().hasSpaceFor(item)) {
            player.getActionSender().sendMessage("You do not have enough space in " + "your inventory!");
            return false;
        }
        super.remove(item);
        //super.remove(item, equipmentSlot);
        if (addItem)
            player.getInventory().add(new Item(item.getId(), item.getAmount()));
        if (equipmentSlot == EquipmentContainer.WEAPON_SLOT) {
            player.getWeaponInterface().sendWeapon(null, "Unarmed");
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
	
	public boolean usingCrystalBow(Player player) {
		return player.getEquipment().get(EquipmentContainer.WEAPON_SLOT).getId() == 4222;
	}
	
	public boolean wearingBlowpipe(Player player) {
		return player.getEquipment().get(EquipmentContainer.WEAPON_SLOT).getId() == 12926;
	}
	
	public boolean wearingBallista(Player player) {
		return player.getEquipment().get(EquipmentContainer.WEAPON_SLOT).getId() == 19481;
	}
	
	public boolean isCrossbow(Player player) {
		Item weapon = player.getEquipment().get(EquipmentContainer.WEAPON_SLOT);
		switch(weapon.getId()) {
		case 11785:
		case 9185:
		case 18357:
		case 21012:
			return true;
		}
		return false;
	}
	
	public boolean isThrowingWeapon(Player player) {
		Item weapon = player.getEquipment().get(EquipmentContainer.WEAPON_SLOT);
		switch(weapon.getId()) {
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
		Item weapon = player.getEquipment().get(EquipmentContainer.WEAPON_SLOT);
		switch(weapon.getId()) {
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
		Item weapon = player.getEquipment().get(EquipmentContainer.WEAPON_SLOT);
		switch(weapon.getId()) {
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
		Item weapon = player.getEquipment().get(EquipmentContainer.ARROWS_SLOT);
		switch(weapon.getId()) {
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

	public final Item[] VENEMOUS_WEPS = {new Item(12926), new Item(12904), new Item(12899), new Item(12904)};
	public final Item[] VENEMOUS_HELMS = {new Item(13197), new Item(13199), new Item(12931)};
	
	public boolean canInfect(Player player){
		for(Item i : VENEMOUS_WEPS){
			if(player.getEquipment().get(EquipmentContainer.WEAPON_SLOT) == i){
				return true;
			}
		}
		for(Item i : VENEMOUS_HELMS){
			if(player.getEquipment().get(EquipmentContainer.HEAD_SLOT) == i){
				return true;
			}
		}
		return false;
	}

	private static final boolean[] HAS_BODY = new boolean[Constants.ITEM_LIMIT];

	private static final boolean[] HAS_HEAD = new boolean[Constants.ITEM_LIMIT];

	private static final boolean[] HAS_JAW = new boolean[Constants.ITEM_LIMIT];

	public static void declare() {
		for (int index = 0; index < Constants.ITEM_LIMIT; index++) {
			HAS_BODY[index] = true;
			HAS_HEAD[index] = true;
			HAS_JAW[index] = true;
		}

		final String[] remove_head = {
				"helm", "coif", "sallet", "hood", "hat"
		};

		final String[] remove_body = {
				"blouse", "chestplate", "top", "shirt", "brassard",
				"torso", "hauberk", "platebody", "robe", "splitbark body", "jacket"
		};

		final String[] has_jaw = {
				"mask", "med helm", "coif", "hood", "neitiznot", "armadyl helmet",
				"berserker helm", "archer helm", "farseer helm", "warrior helm",
				"void", "bandana", "bearhead", "dharok's helm", "hat"
		};

		for (final ItemDefinition def : ItemDefinition.DEFINITIONS) {
			if (def != null) {
				final String itemName = def.getName().trim().toLowerCase();
				for (final String name : remove_head) {
					if (itemName.contains(name) && !itemName.equals("robin hood hat")) {
						HAS_HEAD[def.getId()] = false;
						HAS_JAW[def.getId()] = false;
					}
				}

				for (final String name : remove_body) {
					if (itemName.contains(name) && !itemName.equals("angler top") && !itemName.contains("Farmer's shirt")) {
						HAS_BODY[def.getId()] = false;
					}
				}

				for (final String name : has_jaw) {
					if (itemName.contains(name)) {
						HAS_JAW[def.getId()] = true;
					}
				}
				if (itemName.contains("full ")) {
					HAS_JAW[def.getId()] = false;
					HAS_HEAD[def.getId()] = true;
				}
			}
		}
	}
	
	public static boolean hasBody(int id) {
		return HAS_BODY[id];
	}
	
	public static boolean hasHead(int id) {
		return HAS_HEAD[id];
	}

	public static boolean hasJaw(int id) {
		return HAS_JAW[id];
	}
}
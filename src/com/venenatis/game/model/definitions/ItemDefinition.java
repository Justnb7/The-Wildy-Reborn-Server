package com.venenatis.game.model.definitions;

/**
 * Represents all of an in-game Item's attributes.
 * 
 * @author SeVen
 */
public class ItemDefinition {

	/**
	 * The maximum amount of item definitions in #150 old school.
	 */
	public static final int ITEM_LIMIT = 21_394 + 1000;//We have custom items range 22000

	public static final ItemDefinition[] DEFINITIONS = new ItemDefinition[ITEM_LIMIT];

	public static ItemDefinition get(int id) {
		if (id < 0 || id > ItemDefinition.DEFINITIONS.length || DEFINITIONS[id] == null)
            return new ItemDefinition(id, "", "", false, false, -1, -1, false, false, false, false, false, -1, -1, -1, false, false, 0.0, id, false, false, false);
        return DEFINITIONS[id];
	}

	/**
	 * @return the definitions
	 */
	public static ItemDefinition[] getDefinitions() {
		return DEFINITIONS;
	}
	
	private final int id;

	private final String name;

	private final String examine;

	private final boolean noted;

	private final boolean noteable;

	private final int parentId;

	private final int notedId;

	private final boolean stackable;

	private final boolean destroyable;

	private final boolean tradeable;

	private final boolean members;

	private final boolean questItem;

	private final int value;

	private final int highAlch;

	private final int lowAlch;

	private final boolean equipable;

	private final boolean weapon;

	private final double weight;
	
	private final int special_value;
	
	/**
     * The flag that determines if this item is a full helmet.
     */
    private final boolean fullHelm;
    
    /**
     * The flag denoting whether or not a helmet is a full mask, which hides the beard.
     */
    private final boolean fullMask;

    /**
     * The flag that determines if this item is a platebody.
     */
    private final boolean platebody;

	public ItemDefinition(int id, String name, String examine, boolean noted, boolean noteable, int parentId, int notedId, boolean stackable, boolean destroyable, boolean tradeable, boolean members, boolean questItem, int value, int highAlch, int lowAlch, boolean equipable, boolean weapon, double weight, int special_value, boolean fullHelm, boolean fullMask, boolean platebody) {
		this.id = id;
		this.name = name;
		this.examine = examine;
		this.noted = noted;
		this.noteable = noteable;
		this.parentId = parentId;
		this.notedId = notedId;
		this.stackable = stackable;
		this.destroyable = destroyable;
		this.tradeable = tradeable;
		this.members = members;
		this.questItem = questItem;
		this.value = value;
		this.highAlch = highAlch;
		this.lowAlch = lowAlch;
		this.equipable = equipable;
		this.weapon = weapon;
		this.weight = weight;
		this.special_value = special_value;
		this.fullHelm = fullHelm;
        this.fullMask = fullMask;
        this.platebody = platebody;
	}

	/**
	 * @return the examine
	 */
	public String getExamine() {
		return examine;
	}

	/**
	 * @return the highAlch
	 */
	public int getHighAlch() {
		return highAlch;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the lowAlch
	 */
	public int getLowAlch() {
		return lowAlch;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the notedId
	 */
	public int getNotedId() {
		return notedId;
	}

	/**
	 * @return the parentId
	 */
	public int getParentId() {
		return parentId;
	}

	/**
	 * @return the value
	 */
	public int getValue() {
		return value;
	}

	/**
	 * @return the weight
	 */
	public double getWeight() {
		return weight;
	}

	/**
	 * @return the destroyable
	 */
	public boolean isDestroyable() {
		return destroyable;
	}

	/**
	 * @return the equipable
	 */
	public boolean isEquipable() {
		return equipable;
	}

	/**
	 * @return the members
	 */
	public boolean isMembers() {
		return members;
	}

	/**
	 * @return the noteable
	 */
	public boolean isNoteable() {
		return noteable;
	}

	/**
	 * @return the noted
	 */
	public boolean isNoted() {
		return noted;
	}

	/**
	 * @return the questItem
	 */
	public boolean isQuestItem() {
		return questItem;
	}

	/**
	 * @return the stackable
	 */
	public boolean isStackable() {
		return stackable;
	}

	/**
	 * @return the tradeable
	 */
	public boolean isTradeable() {
		return tradeable;
	}

	/**
	 * @return the weapon
	 */
	public boolean isWeapon() {
		return weapon;
	}
	
	public int getSpecialValue() {
		return special_value;
	}

	/**
     * Determines if this item is a full helmet or not.
     *
     * @return {@code true} if this item is a full helmet, {@code false} otherwise.
     */
    public boolean isFullHelm() {
        return fullHelm;
    }
    
    /**
     * Determines if this item is a full mask or not.
     *
     * @return {@code true} if this item is a full mask, {@code false} otherwise.
     */
    public boolean isFullMask() {
    	return fullMask;
    }

    /**
     * Determines if this item is a platebody or not.
     *
     * @return {@code true} if this item is a platebody, {@code false} otherwise.
     */
    public boolean isPlatebody() {
        return platebody;
    }
	
	@Override
	public String toString() {
		return "ITEM[" + id + "," + name + "]";
	}

	public static int getMaximumItems() {
		return DEFINITIONS.length;
	}

}
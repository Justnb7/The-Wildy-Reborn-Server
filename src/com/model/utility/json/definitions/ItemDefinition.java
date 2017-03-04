package com.model.utility.json.definitions;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Predicate;

/**
 * The container that represents an item definition.
 *
 * @author lare96 <http://github.com/lare96>
 */
public final class ItemDefinition {

    /**
     * The array that contains all of the item definitions.
     */
    public static final ItemDefinition[] DEFINITIONS = new ItemDefinition[25000];

    /**
     * The identifier for the item.
     */
    private final int id;

    /**
     * The proper name of the item.
     */
    private final String name;

    /**
     * The description of the item.
     */
    private final String description;

    /**
     * The equipment slot of this item.
     */
    private final int equipmentSlot;

    /**
     * The flag that determines if the item is noteable.
     */
    private final boolean noteable;

    /**
     * The flag that determines if the item is stackable.
     */
    private final boolean stackable;

    /**
     * The special store price of this item.
     */
    private final int specialPrice;

    /**
     * The general store price of this item.
     */
    private final int generalPrice;

    /**
     * The low alch value of this item.
     */
    private final int lowAlchValue;

    /**
     * The high alch value of this item.
     */
    private final int highAlchValue;

    /**
     * The weight value of this item.
     */
    private final double weight;

    /**
     * The array of bonuses for this item.
     */
    private final int[] bonus;

    /**
     * The flag that determines if this item is two-handed.
     */
    private final boolean twoHanded;

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

    /**
     * The flag that determines if this item is tradeable.
     */
    private final boolean tradeable;

    /**
     * Creates a new {@link ItemDefinition}.
     *
     * @param id
     *            the identifier for the item.
     * @param name
     *            the proper name of the item.
     * @param description
     *            the description of the item.
     * @param equipmentSlot
     *            the equipment slot of this item.
     * @param noteable
     *            the flag that determines if the item is noteable.
     * @param stackable
     *            the flag that determines if the item is stackable.
     * @param specialPrice
     *            the special store price of this item.
     * @param generalPrice
     *            the general store price of this item.
     * @param lowAlchValue
     *            the low alch value of this item.
     * @param highAlchValue
     *            the high alch value of this item.
     * @param weight
     *            the weight value of this item.
     * @param bonus
     *            the array of bonuses for this item.
     * @param twoHanded
     *            the flag that determines if this item is two-handed.
     * @param fullHelm
     *            the flag that determines if this item is a full helmet.
     * @param platebody
     *            the flag that determines if this item is a platebody.
     * @param fullMask
     *            the flag denoting whether or not a helmet is a full mask, which hides the beard.
     * @param tradeable
     *            the flag that determines if this item is tradeable.
     */
    public ItemDefinition(int id, String name, String description, int equipmentSlot, boolean noteable, boolean stackable,
        int specialPrice, int generalPrice, int lowAlchValue, int highAlchValue, double weight, int[] bonus, boolean twoHanded,
        boolean fullHelm, boolean fullMask, boolean platebody, boolean tradeable) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.equipmentSlot = equipmentSlot;
        this.noteable = noteable;
        this.stackable = stackable;
        this.specialPrice = specialPrice;
        this.generalPrice = generalPrice;
        this.lowAlchValue = lowAlchValue;
        this.highAlchValue = highAlchValue;
        this.weight = weight;
        this.bonus = bonus;
        this.twoHanded = twoHanded;
        this.fullHelm = fullHelm;
        this.fullMask = fullMask;
        this.platebody = platebody;
        this.tradeable = tradeable;
    }

    /**
     * Adds a new {@link ItemDefinition} to the memory.
     *
     * @param index
     *         the index to add the definition on.
     * @param def
     *         the definition to add.
     */
    public static void add(int index, ItemDefinition def) {
        DEFINITIONS[index] = def;
    }

    /**
     * Gets the item definition in memory for {@code id}.
     *
     * @param id
     *         the item identifier to get the item definition of.
     * @return the item definition in memory for the item identifier.
     */
    public static ItemDefinition forId(int id) {
        if (id < 0 || id > ItemDefinition.DEFINITIONS.length || DEFINITIONS[id] == null) {
        	 return new ItemDefinition(id, "NULL_ITEM", "NULL_DESCRIPTION", 0, false, false, 0, 500, 100, 200, 0.0, new int[12], false, false, false, false, true);
        	 
        	 //throw new IllegalStateException("Item definition is not initialised for id " + id);
        }
        return DEFINITIONS[id];
    }


    /**
     * Gets the item identifier in memory for {@code name}.
     *
     * @param name
     *         the name to get the item identifier of.
     * @return the item identifier in memory for the name, or {@code -1} if none were
     * found.
     */
    public static OptionalInt get(String name) {
        for (ItemDefinition def : DEFINITIONS) {
            if (def == null)
                continue;
            if (def.getName().equals(name))
                return OptionalInt.of(def.getId());
        }
        return OptionalInt.empty();
    }

    /**
     * Gets the item definition in memory taht matches the given predicate.
     *
     * @param filter
     *         the predicate to get the item definition of.
     * @return the item definition in memory for the predicate.
     */
    public static Optional<ItemDefinition> get(Predicate<String> filter) {
        for (ItemDefinition def : DEFINITIONS) {
            if (def == null)
                continue;
            if (filter.test(def.getName()))
                return Optional.of(def);
        }
        return Optional.empty();
    }

    /**
     * Gets the identifier for the item.
     *
     * @return the identifier.
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the proper name of the item.
     *
     * @return the proper name.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the description of the item.
     *
     * @return the description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the equipment slot of this item.
     *
     * @return the equipment slot.
     */
    public int getEquipmentSlot() {
        return equipmentSlot;
    }

    /**
     * Determines if the item is noted or not.
     *
     * @return {@code true} if the item is noted, {@code false} otherwise.
     */
    public boolean isNoted() {
        return description.equals("Swap this note at any bank for the " + "equivalent item.");
    }

    /**
     * Determines if the item is noteable or not.
     *
     * @return {@code true} if the item is noteable, {@code false} otherwise.
     */
    public boolean isNoteable() {
        return noteable;
    }

    /**
     * Determines if the item is stackable or not.
     *
     * @return {@code true} if the item is stackable, {@code false} otherwise.
     */
    public boolean isStackable() {
        return stackable;
    }

    /**
     * Gets the special store price of this item.
     *
     * @return the special price.
     */
    public int getSpecialPrice() {
        return specialPrice;
    }

    /**
     * Gets the general store price of this item.
     *
     * @return the general price.
     */
    public int getGeneralPrice() {
        return generalPrice;
    }

    /**
     * Gets the low alch value of this item.
     *
     * @return the low alch value.
     */
    public int getLowAlchValue() {
        return lowAlchValue;
    }

    /**
     * Gets the high alch value of this item.
     *
     * @return the high alch value.
     */
    public int getHighAlchValue() {
        return highAlchValue;
    }

    /**
     * Gets the weight value of this item.
     *
     * @return the weight value.
     */
    public double getWeight() {
        return weight;
    }

    /**
     * Gets the array of bonuses for this item.
     *
     * @return the array of bonuses.
     */
    public int[] getBonus() {
        return bonus;
    }

    /**
     * Determines if this item is two-handed or not.
     *
     * @return {@code true} if this item is two-handed, {@code false} otherwise.
     */
    public boolean isTwoHanded() {
        return twoHanded;
    }

    /**
     * Determines if this item is a full helmet or not.
     *
     * @return {@code true} if this item is a full helmet, {@code false}
     *         otherwise.
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
     * @return {@code true} if this item is a platebody, {@code false}
     *         otherwise.
     */
    public boolean isPlatebody() {
        return platebody;
    }
    
    /**
     * Determines if this item is tradeable.
     * 
     * @return {@code true} if this item is tradeable, {@code false} otherwise.
     */
    public boolean isTradeable() {
        return tradeable;
    }
    
    public static Map<Integer, ItemDefinition> getDefinitions() {
		return definitions;
	}

	public static void setDefinitions(Map<Integer, ItemDefinition> definitions) {
		ItemDefinition.definitions = definitions;
	}

	/**
     * The definitions.
     */
    private static Map<Integer, ItemDefinition> definitions = new HashMap<>();

}
package com.venenatis.game.model;

import java.util.Comparator;
import java.util.Objects;

import com.venenatis.game.model.definitions.EquipmentDefinition;
import com.venenatis.game.model.definitions.ItemDefinition;

public class Item {
	
	public static enum ItemComparator implements Comparator<Item> {
		HIGH_ALCH_COMPARATOR,
		LOW_ALCH_COMPARATOR,
		ITEM_WEIGHT_COMPARATOR,
		SHOP_VALUE_COMPARATOR,
		ITEM_ID_COMPARATOR,
		ITEM_AMOUNT_COMPARATOR;

		@Override
		public int compare(Item first, Item second) {
			double firstValue = 0;
			double secondValue = 0;

			if (first == null && second == null) {
				return 0;
			}

			if (first == null) {
				return 1;
			}

			if (second == null) {
				return -1;
			}

			switch (this) {

			case HIGH_ALCH_COMPARATOR:
				firstValue = first.getHighAlch();
				secondValue = second.getHighAlch();
				break;

			case ITEM_AMOUNT_COMPARATOR:
				firstValue = first.getAmount();
				secondValue = second.getAmount();
				break;

			case ITEM_ID_COMPARATOR:
				firstValue = first.getId();
				secondValue = second.getId();
				break;

			case ITEM_WEIGHT_COMPARATOR:
				firstValue = first.getWeight();
				secondValue = second.getWeight();
				break;

			case LOW_ALCH_COMPARATOR:
				firstValue = first.getLowAlch();
				secondValue = second.getLowAlch();
				break;

			case SHOP_VALUE_COMPARATOR:
				firstValue = first.getValue();
				secondValue = second.getValue();
				break;

			}

			return Integer.signum((int) (secondValue - firstValue));
		}
	}

	public int id;

	public int amount;

	public Item(int id, int amount) {
		this.id = id;
		this.amount = amount;
	}
	
	public Item(Item item) {
		id = ((short) item.getId());
		amount = item.getAmount();
	}

	public Item(int id) {
		this(id, 1);
	}
	
	/**
	 * Creates a new item.
	 * @param id
	 * @param amount
	 * @return
	 */
	public static Item create(int id, int amount) {
		return new Item(id, amount);
	}
	
	public void add(int amount) {
		this.amount += amount;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Item) {
			Item item = (Item) obj;

			if (id == item.id && amount == item.amount) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Determines if {@code item} is valid. In other words, determines if
	 * {@code item} is not {@code null} and the {@link Item#id} and
	 * {@link Item#amount} are above {@code 0}.
	 *
	 * @param item
	 *            the item to determine if valid.
	 * @return {@code true} if the item is valid, {@code false} otherwise.
	 */
	public static boolean valid(Item item) {
		return item != null && item.id > 0 && item.amount > 0;
	}
	
	/**
     * Gets the identification of this item.
     *
     * @return the identification.
     */
    public final int getId() {
        return id;
    }

    /**
     * Sets the identification of this item.
     *
     * @param id
     *            the new identification of this item.
     */
    public final void setId(int id) {
        this.id = id;
    }

    public void remove(int amount) {
		this.amount -= amount;
	}
    
	public void setAmount(int i) {
		this.amount = i;
	}

	public int getAmount() {
		return amount;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(id, amount);
	}

	@Override
	public Item clone() {
		return new Item(id, amount);
	}

	@Override
	public String toString() {
		return "ITEM[id= " + id + ", count= " + amount + "]";
	}

	/**
	 * Increment the count by 1.
	 */
	public void incrementAmount() {
		if ((amount + 1) > Integer.MAX_VALUE) {
			return;
		}
		amount++;
	}
	
	/**
	 * Increment amount by given number
	 * @param amount
	 *        The amount we increment
	 */
	public void incrementAmount(int amount) {
		this.amount += amount;
	}

	/**
	 * Decrement the count by 1.
	 */
	public void decrementAmount() {
		if ((amount - 1) < 0) {
			return;
		}
		amount--;
	}

	/**
	 * Increment the count by the argued count.
	 *
	 * @param count
	 *            the count to increment by.
	 */
	public void incrementAmountBy(int count) {
		if ((this.amount + count) > Integer.MAX_VALUE) {
			this.amount = Integer.MAX_VALUE;
		} else {
			this.amount += count;
		}
	}

	/**
	 * Decrement the count by the argued count.
	 *
	 * @param count
	 *            the count to decrement by.
	 */
	public void decrementAmountBy(int count) {
		if ((this.amount - count) < 1) {
			this.amount = 0;
		} else {
			this.amount -= count;
		}
	}

	/**
	 * Gets the high alchemy value.
	 * 
	 * @return The high alchemy value.
	 */
	public int getHighAlch() {
		final ItemDefinition def = ItemDefinition.getDefinitions()[getId()];

		if (def == null) {
			return 0;
		}

		return def.getHighAlch();
	}

	/**
	 * Gets the low alchemy value.
	 * 
	 * @return The low alchemy value.
	 */
	public int getLowAlch() {
		final ItemDefinition def = ItemDefinition.getDefinitions()[getId()];

		if (def == null) {
			return 0;
		}

		return def.getLowAlch();
	}

	/**
	 * Gets an item name from the ItemDefinitions.json
	 */
	public String getName() {
		final ItemDefinition def = ItemDefinition.getDefinitions()[getId()];
		return def == null ? "Unarmed" : def.getName();
	}

	public int getValue() {
		final ItemDefinition def = ItemDefinition.getDefinitions()[getId()];

		if (def == null) {
			return 0;
		}

		return def.getValue();
	}

	/**
	 * Gets the weight of the item.
	 * 
	 * @return The weight.
	 */
	public double getWeight() {
		if (id >= ItemDefinition.getDefinitions().length) {
			System.err.println("Warning: item ID too high - not supported by ItemDefs");
			return 0.0;
		}
		
		final ItemDefinition def = ItemDefinition.getDefinitions()[getId()];

		if (def == null) {
			return 0.0;
		}

		return def.getWeight();
	}

	/**
	 * Determines if the item is destroyable.
	 * 
	 * @return The items destroyability.
	 */
	public boolean isDestroyable() {
		final ItemDefinition def = ItemDefinition.getDefinitions()[getId()];
		return def != null && def.isDestroyable();
	}

	/**
	 * Determines if the item is equipable.
	 * 
	 * @return The items equipability.
	 */
	public boolean isEquipable() {
		final ItemDefinition def = ItemDefinition.getDefinitions()[getId()];
		return def != null && def.isEquipable();
	}

	/**
	 * Determines if the item is a members object.
	 * 
	 * @return The items memberability.
	 */
	public boolean isMembers() {
		final ItemDefinition def = ItemDefinition.getDefinitions()[getId()];
		return def != null && def.isMembers();
	}

	/**
	 * Determines if the item is a quest item.
	 * 
	 * @return The items questability.
	 */
	public boolean isQuestItem() {
		final ItemDefinition def = ItemDefinition.getDefinitions()[getId()];
		return def != null && def.isQuestItem();
	}

	/**
	 * Determines if the item is stackable.
	 * 
	 * @return The items stackability.
	 */
	public boolean isStackable() {
		final ItemDefinition def = ItemDefinition.getDefinitions()[getId()];
		return def != null && (def.isStackable() || def.isNoted());
	}
	
	/**
	 * Determines if the item is noted.
	 * 
	 * @return The items noteability.
	 */
	public boolean isNoted() {
		final ItemDefinition def = ItemDefinition.getDefinitions()[getId()];
		return def != null && def.isNoted();
	}

	/**
	 * Determines if the item is tradable.
	 * 
	 * @return The items tradability.
	 */
	public boolean isTradeable() {
		final ItemDefinition def = ItemDefinition.getDefinitions()[getId()];
		return def != null && def.isTradeable();
	}

	public Item noted() {
		final ItemDefinition def = ItemDefinition.getDefinitions()[getId()];
		return def.getNotedId() != -1 ? new Item(def.getNotedId(), amount) : this;
	}

	public Item unnoted() {
		final ItemDefinition def = ItemDefinition.getDefinitions()[getId()];
		return def != null && def.getParentId() != -1 ? new Item(def.getParentId(), amount) : this;
	}
	
	/**
	 * Gets the definition of this item.
	 * 
	 * @return The definition.
	 */
	public ItemDefinition getDefinition() {
		return ItemDefinition.get(id);
	}
	
	public static ItemDefinition getDefinition(int id) {
		return ItemDefinition.get(id);
	}
	
	public EquipmentDefinition getEquipmentDefinition() {
        return EquipmentDefinition.get(id);
    }
	
	public Item copy() {
		return new Item(id, amount);
	}

	
}
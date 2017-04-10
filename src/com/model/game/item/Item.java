package com.model.game.item;

import com.model.utility.json.definitions.ItemDefinition;

public class Item {

	public int id;

	public int amount;

	public Item(int id) {
		this(id, 1);
	}

	public Item(int id, int count) {
		this.id = id;
		this.amount = count;
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

	public int getId() {
		return id;
	}

	public void setCount(int i) {
		this.amount = i;
	}

	public int getAmount() {
		return amount;
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

	public ItemDefinition getDefinition() {
		return ItemDefinition.forId(id);
	}

	public Item copy() {
		return new Item(getId(), getAmount());
	}
	
	public int getValue() {
		final ItemDefinition def = ItemDefinition.forId(getId());

		if (def == null) {
			return 0;
		}

		return def.getGeneralPrice();
	}

	/**
	 * Gets the weight of the item.
	 * 
	 * @return The weight.
	 */
	public double getWeight() {
		final ItemDefinition def = ItemDefinition.forId(getId());

		if (def == null) {
			return 0.0;
		}

		return def.getWeight();
	}
	
	/**
	 * Gets the high alchemy value.
	 * 
	 * @return The high alchemy value.
	 */
	public int getHighAlch() {
		final ItemDefinition def = ItemDefinition.forId(getId());

		if (def == null) {
			return 0;
		}

		return def.getHighAlchValue();
	}
	
	/**
	 * Gets the low alchemy value.
	 * 
	 * @return The low alchemy value.
	 */
	public int getLowAlch() {
		final ItemDefinition def = ItemDefinition.forId(getId());

		if (def == null) {
			return 0;
		}

		return def.getLowAlchValue();
	}

	/**
	 * Gets an item name from the ItemDefinitions.json
	 */
	public String getName() {
		final ItemDefinition def = ItemDefinition.forId(getId());
		return def == null ? "Unarmed" : def.getName();
	}

	/**
	 * Determines if the item is stackable.
	 * 
	 * @return The items stackability.
	 */
	public boolean isStackable() {
		final ItemDefinition def = ItemDefinition.forId(getId());
		return def != null && (def.isStackable() || def.isNoted());
	}

}
package com.venenatis.game.model.container;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import com.venenatis.game.model.Item;
import com.venenatis.game.model.combat.PrayerHandler;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.util.Utility;
import com.venenatis.game.world.ground_item.GroundItem;
import com.venenatis.game.world.ground_item.GroundItemHandler;

/**
 * The class <code>Container</code> represents a container that can hold an
 * array of objects.
 * 
 * @author Michael | Chex
 */
public abstract class Container {

	/**
	 * Represents a type of container.
	 * 
	 * @author Michael | Chex
	 */
	public static enum ContainerType {
		/**
		 * All container items will have their own slot regardless if they are
		 * stackable.
		 */
		NEVER_STACK,

		/** All container items will stack. */
		ALWAYS_STACK,

		/**
		 * All container items will have their own slot unless they are
		 * stackable, then they will stack.
		 */
		DEFAULT
	}

	/** Holds the type of the container. */
	private final ContainerType type;

	/** The size of the container. */
	private final int capacity;

	/** The amount of used slots in the stack. */
	private int takenSlots;

	/** The array that holds the objects of the type specified. */
	protected final Item[] stack;

	private final boolean allowZero;

	/**
	 * Constructs a new empty Container object.
	 * 
	 * @param capacity
	 *            The size of the container.
	 */
	public Container(int capacity, boolean allowZero, ContainerType type) {
		this.capacity = capacity;
		this.allowZero = allowZero;
		this.type = type;
		this.stack = new Item[capacity];
	}

	/**
	 * Constructs a new empty Container object.
	 * 
	 * @param capacity
	 *            The size of the container.
	 */
	public Container(int capacity, ContainerType type) {
		this(capacity, false, type);
	}

	/**
	 * Adds an item to the container.
	 * 
	 * @param item
	 *            The item to add.
	 * @param amount
	 *            The amount of the item to add.
	 *
	 */
	public int add(int item, int amount) {
		return add(item, amount, true);
	}

	/**
	 * Adds an item with a given amount to the container.
	 * 
	 * @param item
	 *            The item to add.
	 * @param amount
	 *            The amount of the item to add.
	 * @param refresh
	 *            Whether or not the container should refresh the interface.
	 * 
	 */
	public int add(int item, int amount, boolean refresh) {
		return add(new Item(item, amount), refresh);
	}

	/**
	 * Adds multiple items to the container.
	 * 
	 * @param items
	 *            The items to add.
	 */
	public int add(Item... items) {
		if (items == null) {
			return 0;
		}

		int added = 0;

		// here you can have an optimisation: instead of refreshing (sending packet to update items) after each individual add
		// do it afterwards
		for (final Item item : items) {
			if (item != null)
				added += add(item.getId(), item.getAmount(), false);
		}
		refresh();

		return added;
	}

	/**
	 * Adds an item to the container.
	 * 
	 * @param item
	 *            The item to add.
	 * 
	 */
	public int add(Item item) {
		if (item == null) {
			return 0;
		}
		
		return add(item.getId(), item.getAmount(), true);
	}

	/**
	 * Adds an item to the container.
	 * 
	 * @param item
	 *            The item to add.
	 * @param refresh
	 *            Whether or not the container should refresh the interface.
	 * 
	 */
	public int add(Item item, boolean refresh) {
		if (item == null) {
			return 0;
		}
		
		int succesfullAdds = 0;
		int free = getFreeSlots();
		final boolean stackable = item.isStackable();

		if (item.getAmount() <= 0) {
			return 0;
		}

		if (free == 0 && !stackable) {
			onFillContainer();
			return 0;
		}

		int slot;

		if ((type != ContainerType.NEVER_STACK && stackable) || (type == ContainerType.ALWAYS_STACK && !stackable)) {
			slot = indexOfOrNull(item);
			
			if (slot == -1) {
				return succesfullAdds;
			}

			if (stack[slot] == null) {
				stack[slot] = item.copy();
				takenSlots++;
				succesfullAdds += item.getAmount();
			} else {
				final int temp = stack[slot].getAmount();
				final long newAmount = (long) temp + (long) item.getAmount();

				if (newAmount > Integer.MAX_VALUE) {
					System.err.println("MAX STACK");
					stack[slot].setAmount(Integer.MAX_VALUE);
					onMaxStack();
				} else {
					stack[slot].setAmount((int) newAmount);
				}

				succesfullAdds += stack[slot].getAmount() - temp;
			}
		} else {
			slot = nextSlot();
			int toAdd = item.getAmount();
			Item copy = item.copy();
			copy.setAmount(1);
			for (int index = slot; index < capacity && toAdd > 0; index++) {
				if (stack[index] == null) {
					stack[index] = copy;
					takenSlots++;
					succesfullAdds++;
					toAdd--;
					free--;

					if (free == 0 && toAdd > 0) {
						onFillContainer();
						break;
					}
				}
			}
		}

		if (succesfullAdds > 0 && refresh) {
			refresh();
		}
		return succesfullAdds;
	}

	/**
	 * Adds an item to the container if the predicate is valid.
	 * 
	 * @param condition
	 *            The test condition.
	 * @param refresh
	 *            Whether or not the container should refresh the interface.
	 * @param items
	 *            The list of items to add..
	 */
	public int addIf(Predicate<Item> condition, boolean refresh, Item... items) {
		if (items == null) {
			return 0;
		}

		int added = 0;

		for (final Item item : items) {
			added += addIf(condition, item, refresh);
		}

		return added;
	}

	/**
	 * Adds an item to the container if the predicate is valid.
	 * 
	 * @param condition
	 *            The test condition.
	 * @param items
	 *            The list of items to add..
	 */
	public int addIf(Predicate<Item> condition, Item... items) {
		return addIf(condition, true, items);
	}

	/**
	 * Adds an item to the container if the predicate is valid.
	 * 
	 * @param condition
	 *            The test condition.
	 * @param items
	 *            The list of items to add..
	 * 
	 */
	public int addIf(Predicate<Item> condition, Item item) {
		return addIf(condition, item, true);
	}

	/**
	 * Adds an item to the container if the predicate is valid.
	 * 
	 * @param condition
	 *            The test condition.
	 * @param item
	 *            The item to add..
	 * @param refresh
	 *            Whether or not the container should refresh the interface.
	 * 
	 */
	public int addIf(Predicate<Item> condition, Item item, boolean refresh) {
		if (item == null || !condition.test(item)) {
			return 0;
		}

		return add(item, refresh);
	}
	
	/**
     * Retrieves the maximum amount of items that can be in this container.
     *
     * @return the maximum amount of items.
     */
    public int capacity() {
        return capacity;
    }

	/**
	 * Clears the container.
	 */
	public void clear(boolean refresh) {
		for (int index = 0; index < capacity; index++) {
			stack[index] = null;
		}

		takenSlots = 0;

		if (refresh) {
			refresh();
		}
	}

	/**
	 * Gets the total worth of the container using the item's values.
	 * 
	 * @return The total container worth.
	 */
	public long containerValue() {
		long value = 0;
		final Item[] trimmed = toNonNullArray();

		if (trimmed == null) {
			return 0;
		}

		for (final Item item : trimmed) {
			if (value >= Long.MAX_VALUE - item.getValue() * item.getAmount()) {
				return Long.MAX_VALUE;
			}

			value += item.getValue() * item.getAmount();
		}

		return value;
	}
	
	/**
	 * Gets the total worth of the container using the item's high alch values.
	 * 
	 * @return The total container worth.
	 */
	public long containerHighAlchValue() {
		long value = 0;
		final Item[] trimmed = toNonNullArray();

		if (trimmed == null) {
			return 0;
		}

		for (final Item item : trimmed) {
			if (value >= Long.MAX_VALUE - item.getHighAlch() * item.getAmount()) {
				return Long.MAX_VALUE;
			}

			value += item.getHighAlch() * item.getAmount();
		}

		return value;
	}

	public boolean hasAllItems(Item...toCheck) {
		Item[] checkClone = new Item[toCheck.length];
		
		for (int i = 0; i < checkClone.length; i++) {
			checkClone[i] = new Item(toCheck[i]);
		}
		
		for (Item check : checkClone) {
			if (check == null) {
				continue;
			}
			
			boolean stackable = isStackable(check);
	
			for (Item item : stack) {
				if (item != null && item.getId() == check.getId() && check.getAmount() > 0) {
					if (stackable) {
						if (item.getAmount() >= check.getAmount()) {
							check.remove(item.getAmount());
						}
					} else if (item.getAmount() > 0) {
						check.remove(1);
					}
				}
			}
		}
		
		for (Item item : checkClone) {
			if (item.getAmount() > 0) {
				checkClone = null;
				return false;
			}
		}
		
		checkClone = null;

		return true;
	}
	
	
	/**
	 * Checks if the container has the specified item.
	 * 
	 * @param item
	 *            The item to check for.
	 * @return <code>True</code> if the container contains the item;
	 *         <code>False</code> otherwise.
	 * @see #contains(Predicate)
	 */
	public boolean contains(int id) {
		return contains(e -> e.getId() == id);
	}

	/**
	 * Checks if the container has the specified item.
	 * 
	 * @param item
	 *            The item to check for.
	 * @return <code>True</code> if the container contains the item;
	 *         <code>False</code> otherwise.
	 * @see #contains(Predicate)
	 */
	public boolean contains(int id, int amount) {
		return contains(e -> e.getId() == id && e.getAmount() >= amount);
	}

	/**
	 * Checks if the container has the specified items.
	 * 
	 * @param items
	 *            The items to check for.
	 * @return <code>True</code> if the container contains the item;
	 *         <code>False</code> otherwise.
	 * @see #contains(Predicate)
	 */
	public boolean contains(Item... items) {
		if (items == null) {
			return false;
		}

		for (final Item item : items) {
			if (item == null) {
				continue;
			}

			if (!contains(item.getId(), item.getAmount())) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks if the container has the specified item.
	 * 
	 * @param item
	 *            The item to check for.
	 * @return <code>True</code> if the container contains the item;
	 *         <code>False</code> otherwise.
	 * @see #contains(Predicate)
	 */
	public boolean contains(Item item) {
		if (item == null) {
			return false;
		}

		return contains(item.getId(), item.getAmount());
	}

	/**
	 * Checks if the container contains an item, based on the requirements of
	 * the given filter.
	 *
	 * @param filter
	 *            The {@link Predicate} used for validation.
	 * @return Whether or not any of the items pass validation.
	 * @see #contains(Object)
	 */
	public boolean contains(Predicate<Item> filter) {
		for (final Item item : stack) {
			if (Objects.nonNull(item) && filter.test(item)) {
				return true;
			}
		}

		return false;
	}

	public long count(Predicate<Item> condition) {
		long found = 0;
		for (final Item item : stack) {
			if (Objects.nonNull(item) && condition.test(item)) {
				found += item.getAmount();
			}
		}

		return found;
	}

	/**
	 * Checks if the container contains any of the specified items.
	 * 
	 * @param items
	 *            The items to check for.
	 * @return <code>True</code> if the container contains the item;
	 *         <code>False</code> otherwise.
	 * @see #contains(Predicate)
	 */
	public boolean containsAny(int... items) {
		if (items == null) {
			return false;
		}

		for (final int item : items) {
			if (contains(item)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if the container contains any of the specified items.
	 * 
	 * @param items
	 *            The items to check for.
	 * @return <code>True</code> if the container contains the item;
	 *         <code>False</code> otherwise.
	 * @see #contains(Predicate)
	 */
	public boolean containsAny(Item... items) {
		if (items == null) {
			return false;
		}

		for (final Item item : items) {
			if (contains(item)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if the container has the specified items.
	 * 
	 * @param items
	 *            The items to check for.
	 * @return <code>True</code> if the container contains the item;
	 *         <code>False</code> otherwise.
	 * @see #contains(Predicate)
	 */
	public List<Item> find(Predicate<Item> filter) {
		List<Item> list = new ArrayList<Item>();

		for (final Item item : stack) {
			if (Objects.nonNull(item) && filter.test(item)) {
				list.add(item);
			}
		}
		
		return list;
	}

	/**
	 * Checks if this container's stack is equal to a specified array.
	 * 
	 * @param other
	 *            The array of the same item type.
	 * @return <code>True</code> if the container's stack is equal to the
	 *         specified array; <code>False</code> otherwise.
	 */
	public boolean equalsStack(Item[] other) {
		if (other.length != this.stack.length) {
			return false;
		}

		return Arrays.equals(this.stack, other);
	}

	/**
	 * Gets an item from the container.
	 * 
	 * @param slot
	 *            The index of the container to get.
	 * @return The item at the specified index.
	 * @see #indexOf(Object)
	 */
	public Item get(int slot) {
		if (slot >= stack.length || slot < 0) {
			return null;
		}

		return stack[slot];
	}
	
	/**
     * Retrieves the identifier for the item located on {@code slot}.
     *
     * @param slot
     *         the slot to get the item identifier on.
     * @return the item identifier on the slot, or {@code -1} if no item exists on
     * the slot.
     */
    public int getId(int slot) {
        if (stack[slot] == null)
            return -1;
        return stack[slot].getId();
    }
    
    /**
	 * Gets a slot by id.
	 * 
	 * @param id
	 *            The id.
	 * @return The slot, or <code>-1</code> if it could not be found.
	 */
	public int getSlotById(int id) {
		for (int i = 0; i < stack.length; i++) {
			if (stack[i] == null) {
				continue;
			}
			if (stack[i].getId() == id) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Gets an item from the container.
	 * 
	 * @param item
	 *            The item in the container to get.
	 * @return The item at the specified index.
	 * @see #indexOf(Object)
	 */
	public Item get(Item item) {
		final int slot = indexOf(item);

		if (slot >= stack.length || slot < 0) {
			return null;
		}

		return stack[slot];
	}
	
	/**
	 * Gets an item.
	 * 
	 * @param index
	 *            The position in the container.
	 * @return The item.
	 */
	public Item fromSlot(int index) {
		return stack[index];
	}

	public boolean hasItemAmount(int id, int amount) {
		return hasItemAmount(new Item(id, amount));
	}
	
	public boolean hasItemAmount(Item item) {
		boolean stackable = isStackable(item);

		for (Item i : stack) {
			if (i != null && i.getId() == item.getId()) {
				if (stackable) {
					return i.getAmount() >= item.getAmount();
				}
				item.remove(1);

				if (item.getAmount() <= 0) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Gets the amount of the specified item in the container.
	 * 
	 * @param id
	 *            The item id to count.
	 * @return The number of the item in the container. If the item is not
	 *         present, the amount will be 0.
	 */
	public int getAmount(int id) {
		int amount = 0;

		if (!contains(id)) {
			return amount;
		}

		for (int index = 0; index < capacity; index++) {
			final Item item = stack[index];
			if (item != null && item.getId() == id) {
				amount += item.getAmount();
			}
		}

		return amount;
	}

	/**
	 * Gets the amount of the specified item in the container.
	 * 
	 * @param item
	 *            The item to count.
	 * @return The number of the item in the container. If the item is not
	 *         present, the amount will be 0.
	 */
	public int getAmount(Item item) {
		if (item == null) {
			return 0;
		}

		return getAmount(item.getId());
	}

	/**
	 * Gets the free slots left in the container.
	 * 
	 * @return The free slots left.
	 * @see #getSize()
	 * @see #getTakenSlots()
	 */
	public int getFreeSlots() {
		return Utility.sumNullElements(stack);
	}

	/**
	 * Gets the size of the container.
	 * 
	 * @return The container size.
	 * @see #getFreeSlots()
	 * @see #getTakenSlots()
	 */
	public int getSize() {
		return capacity;
	}

	/**
	 * Gets the amount of slots taken in the container.
	 * 
	 * @return The amount of taken slots.
	 * @see #getSize()
	 * @see #getFreeSlots()
	 * 
	 */
	public int getTakenSlots() {
		return Utility.sumNonNullElements(stack);
	}

	/**
	 * Checks if the container can hold the specified items.
	 * 
	 * @param items
	 *            The items to check.
	 * @return <code>True</code> if the container can hold the items;
	 *         <code>False</code> otherwise.
	 */
	public boolean hasSpaceFor(Item... items) {
		int free = getFreeSlots();

		for (final Item item : items) {

			if (item == null) {
				continue;
			}

			int empty = -1;
			boolean added = false;
			int amount = item.getAmount();
			final boolean stackable = item.isStackable();

			if (free == 0 && !stackable) {
				return false;
			}

			for (int i = 0; i < capacity; i++) {
				if (stack[i] == null) {
					if (stackable) {
						if (empty == -1) {
							empty = i;
						}
					} else {
						added = true;
						amount--;
						i--;

						if (amount == 0) {
							break;
						}

						free--;

						if (free == 0) {
							break;
						}
					}
				} else if (type != ContainerType.NEVER_STACK && stackable && stack[i].getId() == item.getId()) {
					added = true;
					break;
				} else if (type == ContainerType.ALWAYS_STACK && !stackable && stack[i].getId() == item.getId()) {
					added = true;
					break;
				}

			}

			if (empty != -1 && !added) {
				added = true;
			}

			if (!added) {
				return false;
			}
		}

		return true;
	}
	
	/**
     * Retrieves the slot of the first item found with {@code id}.
     *
     * @param id
     *         the identifier to search this container for.
     * @return the slot of the item with the identifier.
     */
    public int searchSlot(int id) {
        for (int i = 0; i < stack.length; i++) {
            if (stack[i] == null || stack[i].getId() != id)
                continue;
            return i;
        }
        return -1;
    }

	/**
	 * Gets the index of the specified item in the container.
	 * 
	 * @param item
	 *            The item to get the index for.
	 * @return The index of the item.
	 * @see #get(int)
	 * @see #indexOf(int)
	 * @see #indexOf(Item)
	 */
	public int indexOf(int item) {
		for (int index = 0; index < capacity; index++) {
			if (stack[index] != null && stack[index].getId() == item) {
				return index;
			}
		}
		return -1;
	}

	/**
	 * Gets the index of the specified item in the container.
	 * 
	 * @param item
	 *            The item to get the index for.
	 * @return The index of the item.
	 * @see #get(int)
	 */
	public int indexOf(Item item) {
		if (item == null) {
			return 0;
		}

		return indexOf(item.getId());
	}

	/**
	 * Gets the index of the specified item in the container. If the item is not
	 * in the container, the return value will be the first null slot in the
	 * container.
	 * 
	 * @param item
	 *            The item to get the index for.
	 * @return The index of the item.
	 * @see #get(int)
	 */
	public int indexOfOrNull(int item) {
		int nullSlot = -1;
		for (int index = 0; index < capacity; index++) {
			if (stack[index] == null) {
				if (nullSlot == -1) {
					nullSlot = index;
				}
			} else if (stack[index].getId() == item) {
				return index;
			}
		}

		return nullSlot;
	}

	/**
	 * Gets the index of the specified item in the container. If the item is not
	 * in the container, the return value will be the first null slot in the
	 * container.
	 * 
	 * @param item
	 *            The item to get the index for.
	 * @return The index of the item.
	 * @see #get(int)
	 */
	public int indexOfOrNull(Item item) {
		if (item == null) {
			return 0;
		}

		return indexOfOrNull(item.getId());
	}

	/**
	 * Inserts an item from an initial slot to a final slot.
	 * 
	 * @param from
	 *            The initial slot.
	 * @param to
	 *            The final slot.
	 * @see #swap(int, int)
	 */
	public void insert(int from, int to) {
		if (from != to && from < capacity && from >= 0 && to < capacity && to >= 0) {
			int index = from;

			if (from > to) {
				while (index != to) {
					final Item item = stack[index - 1];
					stack[index - 1] = stack[index];
					stack[index] = item;
					index--;
				}
			} else if (from < to) {
				while (index != to) {
					final Item item = stack[index + 1];
					stack[index + 1] = stack[index];
					stack[index] = item;
					index++;
				}
			}
		}
	}

	/**
	 * Gets the index of the next null slot. If there is no next slot, the
	 * return value will be -1.
	 * 
	 * @return The next null slot.
	 */
	public int nextSlot() {
		if (getFreeSlots() == 0) {
			return -1;
		}

		for (int index = 0; index < capacity; index++) {
			if (stack[index] == null) {
				return index;
			}
		}

		return -1;
	}

	/**
	 * Fired when the container is filled.
	 */
	public void onFillContainer() {
	}

	/**
	 * Fired when an item in the container reaches the max stack.
	 */
	public void onMaxStack() {
	}

	/**
	 * Sends the container contents to the interface.
	 */
	public abstract void refresh();

	/**
	 * Sends certain slots of the container contents to the interface.
	 */
	public abstract void refresh(int... slots);

	/**
	 * Removes a given amount of an item in the container matching the specified
	 * item.
	 * 
	 * @param id
	 *            The item id to remove from the container.
	 * @param amount
	 *            The amount of the item to remove.
	 */
	public int remove(int id, int amount) {
		return remove(id, amount, true);
	}

	/**
	 * Removes a given amount of an item in the container matching the specified
	 * item.
	 * 
	 * @param id
	 *            The item id to remove from the container.
	 * @param amount
	 *            The amount of the item to remove.
	 * @param refresh
	 *            Whether or not the container should refresh the interface.
	 */
	public int remove(int id, int amount, boolean refresh) {
		if (!contains(id) || amount <= 0) {
			return 0;
		}

		int removed = 0;

		for (int index = 0; index < capacity && removed < amount; index++) {
			final Item item = stack[index];
			if (item != null && item.getId() == id) {
				if (item.isStackable() && type != ContainerType.NEVER_STACK || type == ContainerType.ALWAYS_STACK) {
					if (item.getAmount() > amount) {
						item.setAmount(item.getAmount() - amount);
						removed += amount;
						break;
					} else {
						removed += item.getAmount();
						stack[index] = allowZero ? new Item(id, 0) : null;
						takenSlots--;
					}

					if (refresh) {
						refresh(index);
					}
					break;
				} else {
					stack[index] = allowZero ? new Item(id, 0) : null;
					removed++;
					takenSlots--;

					if (refresh) {
						refresh(index);
					}
				}
			}
		}

		return removed;
	}

	/**
	 * Removes a given amount of an item in the container matching the specified
	 * item.
	 * 
	 * @param item
	 *            The container item to remove.
	 */
	public int remove(Item item) {
		if (item == null) {
			return 0;
		}
		return remove(item.getId(), item.getAmount(), true);
	}

	/**
	 * Removes a given amount of an item in the container matching the specified
	 * item.
	 * 
	 * @param items
	 *            The container items to remove.
	 */
	public int remove(Item... items) {
		if (items == null) {
			return 0;
		}

		int removed = 0;

		for (final Item item : items) {

			if (item == null) {
				continue;
			}

			removed += remove(item.getId(), item.getAmount(), false);
		}

		refresh();
		return removed;
	}

	/**
	 * Removes a given amount of an item in the container matching the specified
	 * item.
	 * 
	 * @param item
	 *            The container item to remove.
	 * @param refresh
	 *            Whether or not the container should refresh the interface.
	 */
	public int remove(Item item, boolean refresh) {
		if (item == null) {
			return 0;
		}

		return remove(item.getId(), item.getAmount(), refresh);
	}

	/**
	 * Removes all the items in the container matching the specified item.
	 * 
	 * @param item
	 *            The item to remove from the container.
	 * @param refresh
	 *            Whether or not the container should refresh the interface.
	 */
	public void removeAll(Item item, boolean refresh) {
		if (item == null) {
			return;
		}

		for (int index = 0; index < capacity; index++) {
			if (stack[index] != null && stack[index].getId() == item.getId()) {
				stack[index] = null;
				takenSlots--;
			}
		}

		if (refresh) {
			refresh();
		}
	}
	
	public int removeFromSlot(int slot, int expectedId, int amount) {
		if ((stack[slot] == null) || (stack[slot].getId() != expectedId)) {
			return 0;
		}

		if (stack[slot].getAmount() <= amount) {
			int am = stack[slot].getAmount();
			stack[slot] = null;
			return am;
		}

		stack[slot].remove(amount);
		return amount;
	}

	/**
	 * Removes a given amount of an item in the container matching the specified
	 * item.
	 * 
	 * @param slot
	 *            The slot of the item.
	 * @param amount
	 *            The amount to remove.
	 * @param refresh
	 *            Whether or not the container should refresh the interface.
	 */
	public void removeSlot(int slot, int amount, boolean refresh) {
		if (get(slot) == null) {
			return;
		}

		final Item item = get(slot).copy();

		item.setAmount(item.getAmount() - amount);
		
		if (item.getAmount() < 0) {
			item.setAmount(0);
		}

		setSlot(slot, item.getAmount() > 0 ? item : allowZero ? item : null, refresh);
	}
	
	/**
	 * Replaces an item in the container with another item.
	 * 
	 * @param first
	 *            The item to replace.
	 * @param second
	 *            The item replacing the first item.
	 */
	public void replace(Item first, Item second) {
		final int slot = indexOf(first);
		remove(first);
		setSlot(slot, second);
	}


	/**
	 * Sets an item in the specified slot.
	 * 
	 * @param slot
	 *            The slot.
	 * @param item
	 *            The item id.
	 * @param amount
	 *            The amount of the item.
	 */
	public void setSlot(int slot, int item, int amount) {
		setSlot(slot, new Item(item, amount), true);
	}

	/**
	 * Sets an item in the specified slot.
	 * 
	 * @param slot
	 *            The slot.
	 * @param item
	 *            The item id.
	 * @param amount
	 *            The amount of the item.
	 * @param refresh
	 *            Whether or not the slot will refresh.
	 */
	public void setSlot(int slot, int item, int amount, boolean refresh) {
		setSlot(slot, new Item(item, amount), refresh);
	}

	/**
	 * Sets an item in the specified slot.
	 * 
	 * @param slot
	 *            The slot.
	 * @param item
	 *            The item.
	 */
	public void setSlot(int slot, Item item) {
		setSlot(slot, item, true);
	}

	/**
	 * Sets an item in the specified slot.
	 * 
	 * @param slot
	 *            The slot.
	 * @param item
	 *            The item.
	 * @param refresh
	 *            Whether or not the slot will refresh.
	 */
	public void setSlot(int slot, Item item, boolean refresh) {
		if (slot < capacity && slot >= 0) {

			if (stack[slot] == null) {
				if (item != null) {
					takenSlots++;
				}
			} else {
				if (item == null && takenSlots > 0) {
					takenSlots--;
				}
			}

			stack[slot] = item;
		}

		if (refresh) {
			refresh(slot);
		}
	}
	
	 /**
     * Determines if {@code slot} does not have an item on it.
     *
     * @param slot
     *         the to determine if free or not.
     * @return {@code true} if the slot is free, {@code false} otherwise.
     */
    public boolean free(int slot) {
        return stack[slot] == null;
    }
	
	 /**
     * Determines if {@code slot} does have an item on it.
     *
     * @param slot
     *         the to determine if used or not.
     * @return {@code true} if the slot is used, {@code false} otherwise.
     */
    public boolean used(int slot) {
        return !free(slot);
    }
	
	public boolean isStackable(Item id) {
		return (type == ContainerType.ALWAYS_STACK) || ((id != null) && (id.isStackable()) && (type == ContainerType.DEFAULT));
	}

	/**
	 * Removes any null slots in the stack and places them at the end.
	 * 
	 * @param refresh
	 *            Whether or not the container should refresh the interface.
	 */
	public void shift(boolean refresh) {
		if (takenSlots == 0) {
			refresh();
			return;
		}

		List<Item> all = new ArrayList<Item>();

		for (int index = 0; index < capacity; index++) {
			if (stack[index] != null) {
				all.add(stack[index]);
				stack[index] = null;
			}
		}

		int index = 0;
		for (final Item item : all) {
			stack[index] = item;
			index++;
		}

		all.clear();
		all = null;

		if (refresh) {
			refresh();
		}
	}

	/**
	 * Sorts the container with a given comparator.
	 * 
	 * @param comparator
	 *            The comparator to sort with.
	 */
	public void sort(Comparator<Item> comparator) {
		Arrays.sort(stack, (first, second) -> {
			if (first == null || second == null) {
				return 0;
			}
			
			return comparator.compare(first, second);
		});
	}

	/**
	 * Swaps an item from a slot with an item from another slot.
	 * 
	 * @param from
	 *            The initial slot.
	 * @param to
	 *            The final slot
	 * @see #insert(int, int)
	 */
	public void swap(int from, int to) {
		if (from < capacity && from >= 0 && to < capacity && to >= 0) {
			final Item first = stack[from];
			final Item second = stack[to];
			setSlot(from, second, false);
			setSlot(to, first, false);
		}
	}

	public boolean isEmpty() {
		return takenSlots == 0;
	}

	/**
	 * Returns the container's stack.
	 * 
	 * @return The container's stack.
	 */
	public Item[] toArray() {
		return stack;
	}

	public int nextElement() {
		for(int index = 0; index < stack.length; index++) {
			if (stack[index] != null) {
				return index;
			}
		}
		return -1;
	}

	public int nextNullElement() {
		for(int index = 0; index < stack.length; index++) {
			if (stack[index] == null) {
				return index;
			}
		}
		return -1;
	}
	
	/**
	 * Returns the container's stack without null values.
	 * 
	 * @return The container's stack.
	 */
	public Item[] toNonNullArray() {
		return Arrays.stream(stack).filter(Objects::nonNull).toArray(size -> new Item[size]);
	}

	@Override
	public String toString() {
		return "size=" + getSize() + ", takenSlots=" + getTakenSlots() + ", freeSlots=" + getFreeSlots() + ", stack=" + Arrays.toString(stack);
	}

	/**
	 * Returns the container's stack, with null values trimmed at the end of the
	 * stack.
	 * 
	 * @return The container's stack.
	 */
	public Item[] toTrimmedArray() {
		if (takenSlots == 0) {
			return null;
		}

		int lastIndex = 0;
		for (int index = capacity - 1; index >= 0; index--) {
			if (stack[index] != null) {
				lastIndex = index;
				break;
			}
		}

		return Arrays.copyOf(stack, lastIndex + 1);
	}
	
	public Item[] getItems() {
		return stack;
	}
	
	public void addOrCreateGroundItem(Player player, Item item) {
		if (getFreeSlots() > 0) {
			add(new Item(item.getId(), item.getAmount()));
		} else if ((item.getAmount() > 1) && (!item.isStackable())) {
			for (int i = 0; i < item.getAmount(); i++)
				GroundItemHandler.createGroundItem(new GroundItem(new Item(item.getId(), item.getAmount()), player.getLocation(), player));
			player.getActionSender().sendMessage("Inventory full item placed underneath you.");
		} else {
			GroundItemHandler.createGroundItem(new GroundItem(new Item(item.getId(), item.getAmount()), player.getLocation(), player));
			player.getActionSender().sendMessage("Inventory full item placed underneath you.");
		}
	}
	
	public void addOrSentToBank(Player player, Item item) {
		if (getFreeSlots() > 0) {
			add(new Item(item.getId(), item.getAmount()));
		} else if ((item.getAmount() > 1) && (!item.isStackable())) {
			for (int i = 0; i < item.getAmount(); i++)
				player.getBank().add(item);
			player.getActionSender().sendMessage("Inventory full item sent to the bank.");
		} else {
			player.getBank().add(item);
			player.getActionSender().sendMessage("Inventory full item sent to the bank.");
		}
	}

	/**
	 * Calculates the players current wealth, checks for armour and inventory.
	 * 
	 * @param player
	 *            The player whose current wealth we're checking
	 */
	public static long getWealth(Player player) {
		LinkedList<Item> all = new LinkedList<>();
		
		Item[] inv_items = player.getInventory().toNonNullArray();
		for (Item inventory : inv_items) {
			all.add(inventory);
		}
		
		Item[] equipment_items = player.getEquipment().toNonNullArray();
		for(Item equipment : equipment_items) {
			all.addLast(equipment);
		}

		int finalamount = player.isSkulled() ? 0 : 3;
		if (PrayerHandler.isActivated(player, PrayerHandler.PROTECT_ITEM))
			finalamount++;
		int amount = finalamount;
		if (amount > 0) {
			all.sort(Collections.reverseOrder((one, two) -> Double.compare(one.getValue(), two.getValue())));
			for (Iterator<Item> it = all.iterator(); it.hasNext();) {
				Item next = it.next();
				if (amount == 0) {
					break;
				}
				if (next.isStackable() && next.getAmount() > 1) {
					next.amount -= finalamount == 0 ? 1 : finalamount;
				} else {
					it.remove();
				}
				amount--;
			}
		}
		long wealth = 0;
		for (Item totalWealth : all) {
			wealth += (totalWealth.getValue() * totalWealth.amount);
		}
		return wealth;
	}
}
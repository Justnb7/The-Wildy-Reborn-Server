package com.model.utility.json.definitions;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Predicate;

/**
 * 
 * @author Patrick van Elderen
 * @date 19/04/2016
 */
public final class Requirements {
	
	/**
     * The array that contains all of the item definitions.
     */
    public static final Requirements[] DEFINITIONS = new Requirements[25000];

    /**
     * The identifier for the item.
     */
    private final int id;

    /**
     * The array of bonuses for this item.
     */
    private final int[] requirements;
    
    
    public Requirements(int id, int[] requirements) {
        this.id = id;
        this.requirements = requirements;
    }

    /**
     * Adds a new {@link Requirements} to the memory.
     *
     * @param index
     *         the index to add the definition on.
     * @param def
     *         the definition to add.
     */
    public static void add(int index, Requirements def) {
        DEFINITIONS[index] = def;
    }

    /**
     * Gets the item definition in memory for {@code id}.
     *
     * @param id
     *         the item identifier to get the item definition of.
     * @return the item definition in memory for the item identifier.
     */
    public static Requirements forId(int id) {
        if (id < 0 || id > Requirements.DEFINITIONS.length || DEFINITIONS[id] == null)
            return new Requirements(id, new int[20]);
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
        for (Requirements def : DEFINITIONS) {
            if (def == null)
                continue;
        }
        return OptionalInt.empty();
    }

    /**
     * Gets the item definition in memory that matches the given predicate.
     *
     * @param filter
     *         the predicate to get the item definition of.
     * @return the item definition in memory for the predicate.
     */
    public static Optional<Requirements> get(Predicate<String> filter) {
        for (Requirements def : DEFINITIONS) {
            if (def == null)
                continue;
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
     * Gets the array of bonuses for this item.
     *
     * @return the array of bonuses.
     */
    public int[] getRequirement() {
        return requirements;
    }
    
    public static Map<Integer, Requirements> getDefinitions() {
		return definitions;
	}

	public static void setDefinitions(Map<Integer, Requirements> definitions) {
		Requirements.definitions = definitions;
	}

	/**
     * The definitions.
     */
    private static Map<Integer, Requirements> definitions = new HashMap<>();

}

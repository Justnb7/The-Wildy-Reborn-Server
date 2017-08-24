package com.venenatis.game.model.combat.magic;

import java.util.HashMap;
import java.util.Map;

public enum SpellBook {
	MODERN_MAGICS(0, 218),
	
	ANCIENT_MAGICKS(1, 193),
	
	LUNAR_MAGICS(2, 430);
	
	/**
	 * A map of spell book IDs.
	 */
	private static Map<Integer, SpellBook> spellBooks = new HashMap<Integer, SpellBook>();
	
	
	/**
	 * Gets a spell book by its ID.
	 * @param spellBook The Spell book id.
	 * @return The spell book, or <code>null</code> if the id is not a spell book.
	 */
	public static SpellBook forId(int spellBook) {
		return spellBooks.get(spellBook);
	}

	/**
	 * Populates the spell book map.
	 */
	static {
		for(SpellBook spellBook : SpellBook.values()) {
			spellBooks.put(spellBook.id, spellBook);
		}
	}

	/**
	 * The id of this spell book.
	 */
	private int id;

	/**
	 * The interface id of this spell book.
	 */
	private int interfaceId;

	/**
	 * Creates the spell book.
	 * @param id The spellBook id.
	 * @return 
	 */
	private SpellBook(int id, int interfaceId) {
		this.id = id;
		this.interfaceId = interfaceId;
	}

	/**
	 * Gets the spellBook id.
	 * @return The spellBook id.
	 */
	public int getSpellBookId() {
		return id;
	}

	/**
	 * @return The interfaceId.
	 */
	public int getInterfaceId() {
		return interfaceId;
	}
}
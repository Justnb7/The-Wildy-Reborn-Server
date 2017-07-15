package com.venenatis.game.model.combat.magic.spell;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.venenatis.game.model.Item;
import com.venenatis.game.model.combat.magic.spell.combat_spells.WindStrikeScript;

/**
 * Represents spells.
 * 
 * @author Michael Bull
 * @author Mack
 *
 */
public class Spells {

	public enum Spell {

		/**
		 * Wind Strike.
		 */
		WIND_STRIKE(2, 1, -1, SpellBook.MODERN_MAGICS, new Item[] { new Item(AIR_RUNE, 1), new Item(MIND_RUNE, 1) }, new WindStrikeScript(), null);

		/**
		 * A map of spell IDs.
		 */
		private static List<Spell> spells = new ArrayList<Spell>();

		/**
		 * A {@link Set} of entries that store the spells within the enum.
		 */
		private static final ImmutableSet<Spell> VALUES = Sets.immutableEnumSet(EnumSet.allOf(Spell.class));

		/**
		 * Gets a spell by its ID.
		 * 
		 * @param spell
		 *            The Spell id.
		 * @return The spell, or <code>null</code> if the id is not a spell.
		 */
		public static Spell forId(int spellId, SpellBook spellBook) {
			for (Spell spell : spells) {
				if (spell.getSpellBook().getSpellBookId() == spellBook.getSpellBookId() && spell.getSpellId() == spellId) {
					return spell;
				}
			}
			return null;
		}

		/**
		 * Looks up and returns any match based on the respective parameters.
		 * 
		 * @param id
		 * @param spellbook
		 * @return
		 */
		public static Optional<Spell> lookup(int id, SpellBook spellbook) {
			return VALUES.stream().filter(spell -> spell.getSpellBook().getSpellBookId() == spellbook.getSpellBookId() && spell.getSpellId() == id).findAny();
		}

		/**
		 * Populates the prayer map.
		 */
		static {
			for (Spell spell : Spell.values()) {
				spells.add(spell);
			}
		}

		/**
		 * The id of this spell.
		 */
		private int id;

		/**
		 * The level required to use this spell.
		 */
		private int levelRequired;

		/**
		 * The config used for the autocast interface
		 */
		private int autocastConfig;

		/**
		 * The spellbook this spell is on.
		 */
		private SpellBook spellBook;

		/**
		 * The runes required for this spell.
		 */
		private Item[] runes;

		/**
		 * The spell's name for script parsing..
		 */
		private AbstractSpellScript spellScript;

		/**
		 * The item required to cast this spell.
		 */
		private Item requiredItem;

		/**
		 * Creates the spell.
		 * 
		 * @param id
		 *            The spell id.
		 * @return
		 */
		private Spell(int id, int levelRequired, int autocastConfig, SpellBook spellBook, Item[] runes, AbstractSpellScript spellScript, Item requiredItem) {
			this.id = id;
			this.levelRequired = levelRequired;
			this.autocastConfig = autocastConfig;
			this.spellBook = spellBook;
			this.runes = runes;
			this.spellScript = spellScript;
			this.requiredItem = requiredItem;
		}

		/**
		 * Gets the spell id.
		 * 
		 * @return The spell id.
		 */
		public int getSpellId() {
			return id;
		}

		/**
		 * Gets the level required to use this spell.
		 * 
		 * @return The level required to use this spell.
		 */
		public int getLevelRequired() {
			return levelRequired;
		}

		/**
		 * Gets the config required to display on the autocast interface
		 * 
		 * @return
		 */
		public int getAutocastConfig() {
			return autocastConfig;
		}

		/**
		 * Gets the spell book this spell is on.
		 * 
		 * @return The spell book this spell is on.
		 */
		public SpellBook getSpellBook() {
			return spellBook;
		}

		/**
		 * Gets the runes required for this spell.
		 * 
		 * @return The runes required for this spell.
		 */
		public Item[] getRunes() {
			return runes;
		}

		/**
		 * Gets the rune required for this spell by its index.
		 * 
		 * @return The rune required for this spell by its index.
		 */
		public Item getRune(int index) {
			return runes[index];
		}

		/**
		 * Gets the spell's name for script parsing.
		 * 
		 * @return the spellName
		 */
		public AbstractSpellScript getSpellScript() {
			return spellScript;
		}

		/**
		 * Gets the required item to cast this spell.
		 * 
		 * @return The required item to cast this spell.
		 */
		public Item getRequiredItem() {
			return requiredItem;
		}
	}

	public static final int FIRE_RUNE = 554, WATER_RUNE = 555, AIR_RUNE = 556, EARTH_RUNE = 557, MIND_RUNE = 558,
			BODY_RUNE = 559, DEATH_RUNE = 560, NATURE_RUNE = 561, CHAOS_RUNE = 562, LAW_RUNE = 563, COSMIC_RUNE = 564,
			BLOOD_RUNE = 565, SOUL_RUNE = 566, ASTRAL_RUNE = 9075;
}

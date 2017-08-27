package com.venenatis.game.model.combat.magic;

import com.venenatis.game.constants.EquipmentConstants;
import com.venenatis.game.model.Item;
import com.venenatis.game.model.Skills;
import com.venenatis.game.model.combat.magic.spell.impl.BonesToBananas;
import com.venenatis.game.model.combat.magic.spell.impl.BonesToPeaches;
import com.venenatis.game.model.combat.magic.spell.impl.Vengeance;
import com.venenatis.game.model.definitions.ItemDefinition;
import com.venenatis.game.model.entity.player.Player;

public class Magic {
	
	/**
	 * The player.
	 */
	private final Player player;

	/**
	 * The Magic skill.
	 * 
	 * @param player
	 */
	public Magic(Player player) {
		this.player = player;
	}

	/**
	 * Last vengeance timer.
	 */
	private long lastVengeance = 0L;

	/**
	 * Spells Ids that can be used on another player and is safe.
	 */
	public final static int[] SAFE_SPELLS = { 30298 };
	
	/**
	 * The item being used.
	 */
	private Item itemUsed;

	/**
	 * The delay of spell.
	 */
	private long delay;
	
	/**
	 * Casts the spell.
	 * 
	 * @param spell
	 */
	public void cast(MagicSpell spell, boolean deleteFromRunePouch) {

		// Return if player does not meet the level required.
		if (player.getSkills().getLevel(Skills.MAGIC) < spell.getLevel()) {
			player.getActionSender().sendMessage("You need a Magic level of " + spell.getLevel() + " to do this!");
			return;
		}

		if (spell.getRunes() != null) {
			
			boolean runes_in_pouch = player.getRunePouch().contains(spell.getRunes());
			
			//Check if we have the runes in our rune pouch
			if(runes_in_pouch) {
				//Do we delete the runes from the pouch?
				if(deleteFromRunePouch) {
					player.getRunePouch().remove(spell.getRunes());
				}
			}
			
			// Return if the player does not have the runes required.
			if (!player.getInventory().contains(spell.getRunes()) && !runes_in_pouch) {
				player.getActionSender().sendMessage("You do not have the required runes to do this!");
				return;
			}
		}

		// Execute the spell.
		if (spell.execute(player)) {
			player.getInventory().remove(spell.getRunes());
			player.getSkills().addExperience(Skills.MAGIC, spell.getExperience());
		}

	}

	public boolean handleButton(Player player, int button) {
		switch(button) {
		
		/* Bones to Bananas */
		case 1159:
			player.getMagic().cast(new BonesToBananas(), true);
			break;

		/* Bones to Peaches */
		case 15877:
			player.getMagic().cast(new BonesToPeaches(), true);
			break;

		/* Vengeance */
		case 118098:
			player.getMagic().cast(new Vengeance(), true);
			break;
		}
		return false;
	}
	
	
	public int requirement(int spell_index) {
		return player.MAGIC_SPELLS[spell_index][1];
	}
	
	public Item[] runes(int spell_index) {
		int[] spelldata = player.MAGIC_SPELLS[spell_index];
		int[] rd = new int[] {spelldata[8], spelldata[10], spelldata[12], spelldata[14]};
		int count = 0;
		for (int a : rd)
			if (a > 0)
				count++;
		Item[] r = new Item[count];
		if (count > 0) {
			r[0] = new Item(spelldata[8], spelldata[9]);
			if (count > 1) {
				r[1] = new Item(spelldata[10], spelldata[11]);
				if (count > 2) {
					r[2] = new Item(spelldata[12], spelldata[13]);
					if (count > 4)
						r[3] = new Item(spelldata[14], spelldata[15]);
				}
			}
		}
		return r;
	}

	public int getStartGfxHeight(Player player) {
		switch (player.MAGIC_SPELLS[player.getSpellId()][0]) {
		case 12871:
		case 12891:
			return 0;

		default:
			return 100;
		}
	}

	public int getEndGfxHeight(Player player) {
		switch (player.MAGIC_SPELLS[player.getSpellId()][0]) {
		case 12987:
		case 12901:
		case 12861:
		case 12445:
		case 1192:
		case 13011:
		case 12919:
		case 12881:
		case 12999:
		case 12911:
		case 12871:
		case 13023:
		case 12929:
		case 12891:
			return 0;

		default:
			return 100;
		}
	}

	public int getStartDelay(Player player) {
		switch (player.MAGIC_SPELLS[player.getSpellId()][0]) {
		case 1539:
			return 60;
		default:
			return 53;
		}
	}

	public int getEndHeight(Player player) {
		switch (player.MAGIC_SPELLS[player.getSpellId()][0]) {
		case 1562: // stun
			return 10;

		case 12939: // smoke rush
			return 20;

		case 12987: // shadow rush
			return 28;

		case 12861: // ice rush
			return 10;

		case 12951: // smoke blitz
			return 28;

		case 12999: // shadow blitz
			return 15;

		case 12911: // blood blitz
			return 10;

		default:
			return 31;
		}
	}

	public int getStartHeight(Player player) {
		switch (player.MAGIC_SPELLS[player.getSpellId()][0]) {
		case 1562: // stun
			return 25;

		case 12939:// smoke rush
			return 35;

		case 12987: // shadow rush
			return 38;

		case 12861: // ice rush
			return 15;

		case 12951: // smoke blitz
			return 38;

		case 12999: // shadow blitz
			return 25;

		case 12911: // blood blitz
			return 25;

		default:
			return 43;
		}
	}
	
	public final boolean checkRunes(Player player, boolean delete, Item... runes) {
		int weaponId = player.getEquipment().get(EquipmentConstants.WEAPON_SLOT) == null ? -1 : player.getEquipment().get(EquipmentConstants.WEAPON_SLOT).getId();
		int shieldId = player.getEquipment().get(EquipmentConstants.SHIELD_SLOT) == null ? -1 : player.getEquipment().get(EquipmentConstants.SHIELD_SLOT).getId();
		int runesCount = 0;
		boolean has = false;
		for (Item i : runes) {
			if (i == null)
				continue; // safety
			if (hasInfiniteRunes(player, i.getId(), i.getAmount(), weaponId, shieldId, true)) {
				//Checks for rune pouch or staff.
			}
			else if (!player.getInventory().contains(i.getId(), i.getAmount())) {
				player.message("You do not have enough " + ItemDefinition.get(i.getId()).getName().replace("rune", "Rune") + "s to cast this spell.");
				return false;
			}
			// at this point you have the required amount. if you've met all requirements (length of RUNES paramater)
			if (++runesCount == runes.length) {
				has = true;
			}
		}
		// only delete if you've got em all
		if (has && delete) {
			runesCount = 0;
			for (Item i : runes) {
				if (i == null) continue; // safety
				if (hasInfiniteRunes(player, i.getId(), i.getAmount(), weaponId, shieldId, false))
					continue;
				player.getInventory().remove(new Item(i.getId(), i.getAmount()));
			}
		}
		return has;
	}
	
	private static final int AIR_RUNE = 556, WATER_RUNE = 555,
			EARTH_RUNE = 557, FIRE_RUNE = 554;
	
	public final boolean hasInfiniteRunes(Player player, int runeId, int amount, int weaponId, int shieldId, boolean deleteFromRunePouch) {
		if (runeId == AIR_RUNE) {
			if (weaponId == 1381) // air staff
				return true;
		} else if (runeId == WATER_RUNE) {
			if (weaponId == 1383 || shieldId == 18346) // water staff
				return true;
		} else if (runeId == EARTH_RUNE) {
			if (weaponId == 1385) // earth staff
				return true;
		} else if (runeId == FIRE_RUNE) {
			if (weaponId == 1387) // fire staff
				return true;
		}
		if(player.getRunePouch().contains(new Item(runeId, amount))) {
			if(deleteFromRunePouch) {
				player.getRunePouch().deleteFromPouch(runeId, amount);	
			}
			return true;
		}
		return false;
	}

	public long getLastVengeance() {
		return lastVengeance;
	}

	public void setLastVengeance(long lastVengeance) {
		this.lastVengeance = lastVengeance;
	}
	
	public long getDelay() {
		return delay;
	}
	
	public void setDelay(long delay) {
		this.delay = delay;
	}
	
	public Item getItemUsed() {
		return itemUsed;
	}

	public void setItemUsed(Item itemUsed) {
		this.itemUsed = itemUsed;
	}

}
package com.venenatis.game.model.combat.magic;

import com.venenatis.game.constants.EquipmentConstants;
import com.venenatis.game.content.achievements.AchievementHandler;
import com.venenatis.game.content.achievements.AchievementList;
import com.venenatis.game.model.Item;
import com.venenatis.game.model.Skills;
import com.venenatis.game.model.combat.magic.spell.impl.BonesToBananas;
import com.venenatis.game.model.combat.magic.spell.impl.BonesToPeaches;
import com.venenatis.game.model.combat.magic.spell.impl.Vengeance;
import com.venenatis.game.model.definitions.ItemDefinition;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.model.masks.Graphic;
import com.venenatis.game.util.Utility;

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
	public void cast(MagicSpell spell) {

		// Return if player does not meet the level required.
		if (player.getSkills().getLevel(Skills.MAGIC) < spell.getLevel()) {
			player.getActionSender().sendMessage("You need a Magic level of " + spell.getLevel() + " to do this!");
			return;
		}

		// Return if the player does not have the runes required.
		if (spell.getRunes() != null) {
			if (!player.getInventory().contains(spell.getRunes())) {
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

	public static boolean handleButton(Player player, int button) {
		switch(button) {
		
		/* Bones to Bananas */
		case 1159:
			player.getMagic().cast(new BonesToBananas());
			break;

		/* Bones to Peaches */
		case 15877:
			player.getMagic().cast(new BonesToPeaches());
			break;

		/* Vengeance */
		case 118098:
			player.getMagic().cast(new Vengeance());
			break;
		}
		return false;
	}
	
	
	public static int requirement(int spell_index) {
		return Player.MAGIC_SPELLS[spell_index][1];
	}
	
	public static Item[] runes(int spell_index) {
		int[] spelldata = Player.MAGIC_SPELLS[spell_index];
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

	public static int getStartGfxHeight(Player c) {
		switch (c.MAGIC_SPELLS[c.getSpellId()][0]) {
		case 12871:
		case 12891:
			return 0;

		default:
			return 100;
		}
	}

	public static int getEndGfxHeight(Player c) {
		switch (c.MAGIC_SPELLS[c.getSpellId()][0]) {
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

	public static boolean godSpells(Player c) {
		switch (c.MAGIC_SPELLS[c.getSpellId()][0]) {
		case 1190:
			return true;

		case 1191:
			return true;

		case 1192:
			return true;

		default:
			return false;
		}
	}

	public static int getStaffNeeded(Player c) {
		switch (c.MAGIC_SPELLS[c.getSpellId()][0]) {
		case 1539:
			return 1409;
		case 12037:
			return 4170;
		case 1190:
			return 2415;
		case 1191:
			return 2416;
		case 1192:
			return 2417;
		default:
			return 0;
		}
	}

	public static int getStartDelay(Player c) {
		switch (c.MAGIC_SPELLS[c.getSpellId()][0]) {
		case 1539:
			return 60;
		default:
			return 53;
		}
	}

	public static int getEndHeight(Player c) {
		switch (c.MAGIC_SPELLS[c.getSpellId()][0]) {
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

	public static int getStartHeight(Player c) {
		switch (c.MAGIC_SPELLS[c.getSpellId()][0]) {
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
	
	public static final boolean checkRunes(Player player, boolean delete, Item... runes) {
		int weaponId = player.getEquipment().get(EquipmentConstants.WEAPON_SLOT) == null ? -1 : player.getEquipment().get(EquipmentConstants.WEAPON_SLOT).getId();
		int shieldId = player.getEquipment().get(EquipmentConstants.SHIELD_SLOT) == null ? -1 : player.getEquipment().get(EquipmentConstants.SHIELD_SLOT).getId();
		int runesCount = 0;
		boolean has = false;
		for (Item i : runes) {
			if (i == null)
				continue; // safety
			if (hasInfiniteRunes(player, i.getId(), i.getAmount(), weaponId, shieldId, false)) {
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
	
	private static void castVengeance(Player player) {
		if (vengeanceRequirements(player)) {
			player.playAnimation(Animation.create(4410));
			player.playGraphics(Graphic.create(726, 0, 0));
			player.getSkills().addExperience(Skills.MAGIC, 112);
			player.setVengeance(true);
			player.lastCast = System.currentTimeMillis();
			player.message("You cast a vengeance.");
			player.getInventory().refresh();
			AchievementHandler.activate(player, AchievementList.TASTE_ME, 1);
			player.getActionSender().sendWidget(2, 30);
		}
	}
	
	
	
	private static boolean vengeanceRequirements(Player player) {
		
		//Checking for already casted vengeance
		if (player.hasVengeance()) {
			player.message("You already have vengeance casted.");
			return false;
		}
		
		//Level requirement check
		if (player.getSkills().getLevel(Skills.MAGIC) < 94) {
			player.message("Your Magic level is not high enough for this spell.");
			return false;
		} else if (player.getSkills().getLevel(Skills.DEFENCE) < 40) {
			player.message("You need a Defence level of 40 for this spell");
			return false;
		}
		
		//Runes check
		if (!checkRunes(player, true, new Item(ASTRAL_RUNE, 4), new Item(DEATH_RUNE, 2), new Item(EARTH_RUNE, 10)) && player.getTotalAmountDonated() < 100) {
			return false;
		}
		
		//Checking duration
		if (player.lastVeng != null && Utility.currentTimeMillis() - player.lastCast < 30000) {
			player.message("Players may only cast vengeance once every 30 seconds.");
			return false;
		}
		return true;
		
	}
	
	private static final int AIR_RUNE = 556, WATER_RUNE = 555,
			EARTH_RUNE = 557, FIRE_RUNE = 554, DEATH_RUNE = 560,
			ASTRAL_RUNE = 9075;
	
	public static final boolean hasInfiniteRunes(Player player, int runeId, int amount, int weaponId, int shieldId, boolean deleteFromRunePouch) {
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
				player.getRunePouch().remove(new Item(runeId, amount));
				player.getRunePouch().refresh();
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
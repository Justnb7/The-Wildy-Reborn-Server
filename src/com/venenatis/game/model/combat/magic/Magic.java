package com.venenatis.game.model.combat.magic;

import com.venenatis.game.constants.EquipmentConstants;
import com.venenatis.game.model.Item;
import com.venenatis.game.model.Skills;
import com.venenatis.game.model.definitions.ItemDefinition;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.model.masks.Graphic;
import com.venenatis.game.util.Utility;

public class Magic {

	public static boolean handleButton(Player player, int button) {
		switch(button) {
		case 118098:
			castVengeance(player);
			return true;
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

}
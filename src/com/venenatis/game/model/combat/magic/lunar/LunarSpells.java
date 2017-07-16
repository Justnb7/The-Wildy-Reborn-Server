package com.venenatis.game.model.combat.magic.lunar;

import com.venenatis.game.constants.EquipmentConstants;
import com.venenatis.game.model.Item;
import com.venenatis.game.model.Skills;
import com.venenatis.game.model.definitions.ItemDefinition;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.model.masks.Graphic;
import com.venenatis.game.util.Utility;

public class LunarSpells {
	
	private final Player player;

	public LunarSpells(Player player) {
		this.player = player;
	}
	
	private static final int AIR_RUNE = 556, WATER_RUNE = 555,
			EARTH_RUNE = 557, FIRE_RUNE = 554, DEATH_RUNE = 560,
			ASTRAL_RUNE = 9075;
	
	public final void processLunarSpell(int buttonId) {
		switch (buttonId) {
		
		case 118098:
			castVengeance();
			break;
		}
	}

	public void castVengeance() {
		if (vengeanceRequirements()) {
			player.playAnimation(Animation.create(4410));
			player.playGraphics(Graphic.create(726, 0, 0));
			player.getSkills().addExperience(Skills.MAGIC, 112);
			player.setVengeance(true);
			player.lastCast = System.currentTimeMillis();
			player.message("You cast a vengeance.");
			player.getInventory().refresh();
		}
	}
	
	private boolean vengeanceRequirements() {
		
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

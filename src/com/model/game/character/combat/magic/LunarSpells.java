package com.model.game.character.combat.magic;

import com.model.game.character.Animation;
import com.model.game.character.Graphic;
import com.model.game.character.player.Player;
import com.model.game.character.player.Skills;
import com.model.game.character.player.content.teleport.TeleportExecutor;
import com.model.game.item.Item;
import com.model.game.item.container.impl.Equipment;
import com.model.game.location.Location;
import com.model.utility.Utility;
import com.model.utility.json.definitions.ItemDefinition;

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
		
		case 117048:
			castHomeTeleport();
			break;
		
		case 118098:
			castVengeance();
			break;
		}
	}
	
	private void castHomeTeleport() {
		TeleportExecutor.teleport(player, new Location(3096, 3503, 0));
	}

	public void castVengeance() {
		if (vengeanceRequirements()) {
			player.playAnimation(Animation.create(4410));
			player.playGraphics(Graphic.create(726, 0, 0));
			player.getSkills().addExperience(Skills.MAGIC, 1000);
			player.setVengeance(true);
			player.lastCast = System.currentTimeMillis();
			player.getActionSender().sendMessage("You cast a vengeance.");
		}
	}
	
	private boolean vengeanceRequirements() {
		
		//Checking for already casted vengeance
		if (player.hasVengeance()) {
			player.getActionSender().sendMessage("You already have vengeance casted.");
			return false;
		}
		
		//Level requirement check
		if (player.getSkills().getLevel(Skills.MAGIC) < 94) {
			player.getActionSender().sendMessage("Your Magic level is not high enough for this spell.");
			return false;
		} else if (player.getSkills().getLevel(Skills.DEFENCE) < 40) {
			player.getActionSender().sendMessage("You need a Defence level of 40 for this spell");
			return false;
		}
		
		//Runes check
		if (!checkRunes(player, true, new Item(ASTRAL_RUNE, 4), new Item(DEATH_RUNE, 2), new Item(EARTH_RUNE, 10)) && player.getTotalAmountDonated() < 100) {
			return false;
		}
		
		//Checking duration
		if (player.lastVeng != null && Utility.currentTimeMillis() - player.lastCast < 30000) {
			player.getActionSender().sendMessage("Players may only cast vengeance once every 30 seconds.");
			return false;
		}
		return true;
		
	}
	
	private final boolean checkRunes(Player player, boolean delete, Item... runes) {
		int weaponId = player.getEquipment().getId(Equipment.WEAPON_SLOT);
		int shieldId = player.getEquipment().getId(Equipment.SHIELD_SLOT);
		int runesCount = 0;
		boolean has = false;
		for (Item i : runes) {
			if (i == null)
				continue; // safety
			if (hasInfiniteRunes(i.getId(), i.getAmount(), weaponId, shieldId, false)) {
				//Checks for rune pouch or staff.
			}
			else if (!player.getInventory().playerHasItem(i.getId(), i.getAmount())) {
				player.getActionSender().sendMessage("You do not have enough " + ItemDefinition.forId(i.getId()).getName().replace("rune", "Rune") + "s to cast this spell.");
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
				if (hasInfiniteRunes(i.getId(), i.getAmount(), weaponId, shieldId, false))
					continue;
				player.getInventory().remove(new Item(i.getId(), i.getAmount()));
			}
		}
		return has;
	}
	
	public final boolean hasInfiniteRunes(int runeId, int amount, int weaponId, int shieldId, boolean deleteFromRunePouch) {
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
				player.getRunePouch().refresh(player, 41710);
			}
			return true;
		}
		return false;
	}

}

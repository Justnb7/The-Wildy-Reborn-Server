package com.model.game.character.combat.magic;

import com.model.game.character.player.Player;
import com.model.game.character.player.Skills;
import com.model.game.character.player.packets.encode.impl.SendMessagePacket;
import com.model.game.item.Item;

public class MagicRequirements extends MagicData {

	public static boolean hasRunes(Player player, int[] runes, int[] amount) {
		for(int i = 0; i < runes.length; i++) {
			if(player.getItems().playerHasItem(runes[i], amount[i]) || player.getRights().getValue() > 0) {
				return true;
			}
		}
		player.write(new SendMessagePacket("You don't have enough required runes to cast this spell!"));
		return false;
	}

	public static boolean hasRequiredLevel(Player player, int i) {
		return player.getSkills().getLevel(Skills.MAGIC) >= i;
	}

	public static boolean wearingStaff(Player player, int runeId, int amount, boolean deleteFromRunePouch) {
		int wep = player.playerEquipment[player.getEquipment().getWeaponId()];
		switch (runeId) {
		case 554:
			if (wep == 1387 || wep == 12796 || wep == 11789 || wep == 12000)
				return true;
			break;
		case 555:
			if (wep == 1385 || wep == 12796 || wep == 11789)
				return true;
			break;
		case 556:
			if (wep == 1381 || wep == 12000)
				return true;
			break;
		case 557:
			if (wep == 1385)
				return true;
			break;
		}

		if(player.getRunePouchContainer().contains(new Item(runeId, amount))) {
			if(deleteFromRunePouch) {
				player.getRunePouchContainer().remove(new Item(runeId, amount));
				player.getRunePouchContainer().refresh(player, 41710);
			}
			return true;
		}
		return false;
	}

	public static boolean checkMagicReqs(Player player, int spell) {
		if(player.usingMagic) { // check for runes
			if((!player.getItems().playerHasItem(player.MAGIC_SPELLS[spell][8], player.MAGIC_SPELLS[spell][9]) && !wearingStaff(player, player.MAGIC_SPELLS[spell][8], player.MAGIC_SPELLS[spell][9], false)) ||
					(!player.getItems().playerHasItem(player.MAGIC_SPELLS[spell][10], player.MAGIC_SPELLS[spell][11]) && !wearingStaff(player, player.MAGIC_SPELLS[spell][10],  player.MAGIC_SPELLS[spell][11], false)) ||
					(!player.getItems().playerHasItem(player.MAGIC_SPELLS[spell][12], player.MAGIC_SPELLS[spell][13]) && !wearingStaff(player, player.MAGIC_SPELLS[spell][12], player.MAGIC_SPELLS[spell][13], false)) ||
					(!player.getItems().playerHasItem(player.MAGIC_SPELLS[spell][14], player.MAGIC_SPELLS[spell][15]) && !wearingStaff(player, player.MAGIC_SPELLS[spell][14], player.MAGIC_SPELLS[spell][15], false))){
				player.write(new SendMessagePacket("You don't have the required runes to cast this spell."));
				return false;
			} 
		}

		int staffRequired = getStaffNeeded(player);
		if(player.usingMagic && staffRequired > 0) { // staff required
			if(player.playerEquipment[player.getEquipment().getWeaponId()] != staffRequired) {
				player.write(new SendMessagePacket("You need a "+player.getItems().getItemName(staffRequired).toLowerCase()+" to cast this spell."));
				return false;
			}
		}

		if(player.usingMagic) { // check magic level
			if(player.getSkills().getLevel(Skills.MAGIC) < player.MAGIC_SPELLS[spell][1]) {
				player.write(new SendMessagePacket("You need to have a magic level of " +player.MAGIC_SPELLS[spell][1]+" to cast this spell."));
				return false;
			}
		}
		if(player.usingMagic) {
			if(player.MAGIC_SPELLS[spell][8] > 0) { // deleting runes
				if (!wearingStaff(player, player.MAGIC_SPELLS[spell][8], player.MAGIC_SPELLS[spell][9], true))
					player.getItems().deleteItem(player.MAGIC_SPELLS[spell][8], player.getItems().getItemSlot(player.MAGIC_SPELLS[spell][8]), player.MAGIC_SPELLS[spell][9]);
			}
			if(player.MAGIC_SPELLS[spell][10] > 0) {
				if (!wearingStaff(player, player.MAGIC_SPELLS[spell][10], player.MAGIC_SPELLS[spell][11], true))
					player.getItems().deleteItem(player.MAGIC_SPELLS[spell][10], player.getItems().getItemSlot(player.MAGIC_SPELLS[spell][10]), player.MAGIC_SPELLS[spell][11]);
			}
			if(player.MAGIC_SPELLS[spell][12] > 0) {
				if (!wearingStaff(player, player.MAGIC_SPELLS[spell][12], player.MAGIC_SPELLS[spell][13], true))
					player.getItems().deleteItem(player.MAGIC_SPELLS[spell][12], player.getItems().getItemSlot(player.MAGIC_SPELLS[spell][12]), player.MAGIC_SPELLS[spell][13]);
			}
			if(player.MAGIC_SPELLS[spell][14] > 0) {
				if (!wearingStaff(player, player.MAGIC_SPELLS[spell][14], player.MAGIC_SPELLS[spell][15], true))
					player.getItems().deleteItem(player.MAGIC_SPELLS[spell][14], player.getItems().getItemSlot(player.MAGIC_SPELLS[spell][14]), player.MAGIC_SPELLS[spell][15]);
			}
		}
		return true;
	}
}
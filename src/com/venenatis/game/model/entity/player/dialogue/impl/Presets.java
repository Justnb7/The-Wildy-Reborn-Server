package com.venenatis.game.model.entity.player.dialogue.impl;

import com.venenatis.game.constants.EquipmentConstants;
import com.venenatis.game.content.achievements.AchievementHandler;
import com.venenatis.game.content.achievements.AchievementList;
import com.venenatis.game.model.entity.player.dialogue.Dialogue;
import com.venenatis.game.model.entity.player.dialogue.DialogueType;
import com.venenatis.game.model.entity.player.save.*;
import com.venenatis.game.model.entity.player.save.PlayerSave.Type;

public class Presets extends Dialogue {

	@Override
	protected void start(Object... parameters) {
		send(DialogueType.CHOICE, "Select Option", "Save", "Cancel");
		setPhase(0);
	}
	
	@Override
	protected void select(int index) {
		if(getPhase() == 0) {
			switch(index) {
			case 1:
				upload(index);
				break;
			case 2:
				player.getPresets().open(index);
				break;
			}
		}
	}
	
	public void upload(int preset) {
		player.getPresets().getPresetSpellbook()[preset] = player.getSpellBook();
		player.getPresets().getPresetEquipment()[preset][1] = player.getEquipment().get(EquipmentConstants.HELM_SLOT);
		player.getPresets().getPresetEquipment()[preset][3] = player.getEquipment().get(EquipmentConstants.CAPE_SLOT);
		player.getPresets().getPresetEquipment()[preset][4] = player.getEquipment().get(EquipmentConstants.NECKLACE_SLOT);
		player.getPresets().getPresetEquipment()[preset][5] = player.getEquipment().get(EquipmentConstants.AMMO_SLOT);
		player.getPresets().getPresetEquipment()[preset][6] = player.getEquipment().get(EquipmentConstants.WEAPON_SLOT);
		player.getPresets().getPresetEquipment()[preset][7] = player.getEquipment().get(EquipmentConstants.TORSO_SLOT);
		player.getPresets().getPresetEquipment()[preset][8] = player.getEquipment().get(EquipmentConstants.SHIELD_SLOT);
		player.getPresets().getPresetEquipment()[preset][10] = player.getEquipment().get(EquipmentConstants.LEGS_SLOT);
		player.getPresets().getPresetEquipment()[preset][12] = player.getEquipment().get(EquipmentConstants.GLOVES_SLOT);
		player.getPresets().getPresetEquipment()[preset][13] = player.getEquipment().get(EquipmentConstants.BOOTS_SLOT);
		player.getPresets().getPresetEquipment()[preset][14] = player.getEquipment().get(EquipmentConstants.RING_SLOT);
		for (int index = 0; index < player.getInventory().getSize(); index++) {
			if (player.getInventory().get(index) == null) {
				player.getPresets().getPresetInventory()[preset][index] = null;
				continue;
			}
			player.getPresets().getPresetInventory()[preset][index] = player.getInventory().get(index).copy();
		}
		for (int index = 0; index < 7; index++) {
			player.getPresets().getPresetSkill()[preset][index] = player.getSkills().getMaxLevel(index);
		}
		player.getPresets().open(preset);
		AchievementHandler.activate(player, AchievementList.OOH_FANCY, 1);
		PlayerSave.save(player, Type.PRESETS);
	}
}

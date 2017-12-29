package com.venenatis.game.net.packet.in.commands.impl;

import java.util.Optional;

import com.venenatis.game.constants.EquipmentConstants;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.Rights;
import com.venenatis.game.net.packet.in.commands.Command;

public class SetSpecialPowerCommand extends Command {

	@Override
	protected void executeCommand(Player player, String command) {
		String[] args = command.split(" ");
		int specialPower = Integer.valueOf(args[1]);
		player.setSpecialAmount(specialPower);
		player.getWeaponInterface().sendSpecialBar(player.getEquipment().get(EquipmentConstants.WEAPON_SLOT));
		player.getWeaponInterface().refreshSpecialAttack();
	}

	@Override
	protected Optional<Rights[]> allowedRanks() {
		// TODO Auto-generated method stub
		return Optional.of(new Rights[] { Rights.OWNER });
	}

}
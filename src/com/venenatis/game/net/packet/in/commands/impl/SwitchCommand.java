package com.venenatis.game.net.packet.in.commands.impl;

import java.util.Optional;

import com.venenatis.game.constants.Constants;
import com.venenatis.game.model.combat.magic.SpellBook;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.Rights;
import com.venenatis.game.net.packet.in.commands.Command;

/**
 * Switches through the available spell books.
 * 
 * @author Lennard
 *
 */
public class SwitchCommand extends Command {

	@Override
	protected void executeCommand(Player player, String command) {
		if (player.getSpellBook() == SpellBook.ANCIENT_MAGICKS) {
			player.message("You switch to modern magic.");
			player.getActionSender().sendSidebarInterface(Constants.MAGIC_TAB, 1151);
		} else if (player.getSpellBook() == SpellBook.MODERN_MAGICS) {
			player.message("You switch to ancient magic.");
			player.getActionSender().sendSidebarInterface(Constants.MAGIC_TAB, 12855);
		} else {
			player.message("You switch to lunar magic");
			player.getActionSender().sendSidebarInterface(Constants.MAGIC_TAB, 29999);
		}
	}

	@Override
	protected Optional<Rights[]> allowedRanks() {
		return Optional.of(new Rights[] { Rights.OWNER });
	}

}
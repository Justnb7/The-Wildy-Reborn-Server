package com.venenatis.game.net.packet.in.commands.impl;

import java.util.Optional;

import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.Rights;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.net.packet.in.commands.Command;

/**
 * Makes the player perform the given animation.
 * 
 * @author Lennard
 *
 */
public class AnimationCommand extends Command {

	@Override
	protected void executeCommand(Player player, String command) {
		final String[] args = command.split(" ");
		int anim = Integer.parseInt(args[1]);
		player.playAnimation(Animation.create(anim));
	}

	@Override
	protected Optional<Rights[]> allowedRanks() {
		return Optional.of(new Rights[] { Rights.OWNER });
	}

}
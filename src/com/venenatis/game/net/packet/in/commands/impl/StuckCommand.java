package com.venenatis.game.net.packet.in.commands.impl;

import java.util.Optional;

import com.venenatis.game.location.Location;
import com.venenatis.game.model.combat.Combat;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.Rights;
import com.venenatis.game.net.packet.in.commands.Command;
import com.venenatis.game.task.Task;
import com.venenatis.game.world.World;
import com.venenatis.server.Server;

public class StuckCommand extends Command {

	@Override
	protected void executeCommand(Player player, String command) {
		World.getWorld().sendMessageToStaff(player.getUsername() + " Has just used ::stuck");
		World.getWorld().sendMessageToStaff("Player Location: X: " + player.getX() + " Player Y: " + player.getY());
		player.getActionSender().sendMessage("<col=255>You have requested to be sent home assuming you are stuck</col>");
		player.getActionSender().sendMessage("<col=255>You will be sent home in 30 seconds unless you are attacked</col>");
		player.getActionSender().sendMessage("<col=255>The Teleport manager is calculating your area.. abusing this is bannable!</col>");

		Server.getTaskScheduler().schedule(new Task(1) {

			int timer = 0;

			@Override
			public void execute() {
				
				if (Combat.incombat(player)) {
					stop();
					player.getActionSender().sendMessage("Your requested teleport has being cancelled.");
				}
				if (player.isBusy()) {
					player.getActionSender().sendMessage("Your requested teleport has being cancelled.");
					stop();
				}
				if (player.getCombatState().isTeleblocked()) {
					stop();
					player.getActionSender().sendMessage("You are teleblocked, You can't use this command!");
				}
				if (++timer >= 50) {
					player.setTeleportTarget(new Location(3094, 3473, 0));
					player.getActionSender().sendMessage("<col=255>You feel strange.. You magically end up home..</col>");
					this.stop();
				}
			}
		}.attach(player));
	}

	@Override
	protected Optional<Rights[]> allowedRanks() {
		return Optional.empty();
	}

}
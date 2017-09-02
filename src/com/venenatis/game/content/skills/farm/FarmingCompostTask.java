package com.venenatis.game.content.skills.farm;

import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.dialogue.SimpleDialogues;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.task.Task;

public class FarmingCompostTask extends Task {
	
	private final Player player;

	public FarmingCompostTask(Player player, int ticks) {
		super(player, ticks);
		this.player = player;
	}

	@Override
	public void execute() {
		if (player == null || !player.isActive()) {
			this.stop();
			return;
		}
		if (!player.getInventory().contains(995, 250)) {
			//player.getDH().sendDialogues(662, 3257);
			this.stop();
			return;
		}
		if (!player.getInventory().contains(1925)) {
			SimpleDialogues.sendStatement(player, "You have run out of buckets to fill.");
			this.stop();
			return;
		}
		player.playAnimation(new Animation(2283));
		player.getInventory().remove(1925, 1);
		player.getInventory().remove(995, 250);
		player.getInventory().add(FarmingConstants.COMPOST, 1);
	}

	@Override
	public void stop() {
		super.stop();
		if (player == null || !player.isActive()) {
			return;
		}
		player.playAnimation(new Animation(65535));
	}

}
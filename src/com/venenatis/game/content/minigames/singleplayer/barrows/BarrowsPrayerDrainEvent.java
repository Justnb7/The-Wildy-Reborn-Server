package com.venenatis.game.content.minigames.singleplayer.barrows;

import java.util.ArrayList;
import java.util.Random;

import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.task.Task;
import com.venenatis.game.world.World;

public class BarrowsPrayerDrainEvent extends Task {

	private Player player;

	public BarrowsPrayerDrainEvent(Player player) {
		super(8); // every 8 ticks
		this.player = player;
		if (player.hasAttribute("barrowdraintask")) {
			this.stop();
			return;
		}
		player.setAttribute("barrowdraintask", this);
	}

	@Override
	public void execute() {
		if (!BarrowsHandler.getSingleton().withinCrypt(player)) {
			player.removeAttribute("barrowdraintask");
			stop();
			return;
		}

		boolean[] brothers = player.getBarrowsDetails().getBrothersKilled();
		ArrayList<Integer> killedBrotherHeads = new ArrayList<Integer>();
		for (int i = 0; i < brothers.length; i++) {
			if (brothers[i]) {
				killedBrotherHeads.add(BarrowsInformation.values()[i].getPurpleHead());
			}
		}

		player.getPrayerHandler().drainPrayer(player, 8 + killedBrotherHeads.size());

		if (!killedBrotherHeads.isEmpty()) {
			Random rand = new Random();
			int position = 4537 + rand.nextInt(6);
			int head = killedBrotherHeads.get(rand.nextInt(killedBrotherHeads.size()));

			player.getActionSender().sendItemOnInterface(position, 100,
					player.getLocation().getZ() == 0 ? head + 1 : head);
			player.getActionSender().sendInterfaceAnimation(position, new Animation(2085));

			World.getWorld().schedule(new Task(3) {

				@Override
				public void execute() {
					player.getActionSender().sendItemOnInterface(position, 100, 65535);
					stop();
				}

			});
		}

	}

}
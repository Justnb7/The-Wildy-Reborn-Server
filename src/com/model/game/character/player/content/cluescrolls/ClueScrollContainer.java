package com.model.game.character.player.content.cluescrolls;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Optional;
import java.util.Queue;

import com.model.game.character.player.Player;
import com.model.game.character.player.packets.out.SendInterface;
import com.model.game.character.player.packets.out.SendMessagePacket;
import com.model.utility.Chance;
import com.model.utility.Utility;

public final class ClueScrollContainer {

	private final Player player;
	public final Queue<ClueScroll> stages = new ArrayDeque<>();

	public ClueScrollContainer(Player player, ClueScroll... stages) {
		this.player = player;
		Collections.addAll(this.stages, stages);
	}

	public void current(int id) {
		ClueScroll c = stages.peek();
		if (c == null) {
			player.clueContainer = null;
			player.getItems().deleteItem(id);
			return;
		}
		player.write(new SendInterface(c.interfaceId()));
	}

	public void next(int id) {
		Optional<ClueDifficulty> clueScroll = ClueDifficulty.getDifficulty(id);
		clueScroll.ifPresent(c -> {
			if (player.getItems().playerHasItem(id)) {
				player.getItems().deleteItem(id);
				stages.poll();
				if (stages.peek() == null) {
					if (Chance.COMMON.successful(Utility.r)) {
						c.createBoss(player);
					} else {
						player.bossDifficulty = c;
						ClueScrollHandler.giveReward(player);
					}
					return;
				}
				player.getItems().addItem(id, 1);
				player.write(new SendMessagePacket("You dig for treasure and find another clue!"));
			}
		});
	}
}

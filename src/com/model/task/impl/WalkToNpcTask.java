package com.model.task.impl;

import com.model.Server;
import com.model.game.character.npc.NPC;
import com.model.game.character.player.Player;
import com.model.game.character.player.content.clicking.npc.NpcInteraction;
import com.model.game.location.Position;
import com.model.task.ScheduledTask;

/**
 * Handles walking towards npcs
 * 
 * @author Patrick van Elderen
 *
 */
public class WalkToNpcTask extends ScheduledTask {
	
	private final NPC npc;
	private final Player player;
	private final int clickType;
	
	public WalkToNpcTask(Player player, NPC npc, int clickType) {
		super(1, false);
		this.npc = npc;
		this.player = player;
		this.clickType = clickType;
	}

	@Override
	public void execute() {
		
		switch (clickType) {
		case 1:
			if (player.getPosition().isWithinInteractionDistance(new Position(npc.getX(), npc.getY()))) {
				NpcInteraction.firstOption(player, npc);
			} else {
				Server.getTaskScheduler().schedule(new ScheduledTask(1) {
					@Override
					public void execute() {
						if (!player.isActive()) {
							stop();
							return;
						}
						if (npc != null) {
							if (player.getPosition().isWithinInteractionDistance(new Position(npc.getX(), npc.getY()))) {
								NpcInteraction.firstOption(player, npc);
								stop();
							}
						}
					}
				});
			}
			break;
		case 2:
			if (player.getPosition().isWithinInteractionDistance(new Position(npc.getX(), npc.getY()))) {
				NpcInteraction.secondOption(player, npc);
			} else {
				Server.getTaskScheduler().schedule(new ScheduledTask(1) {

					@Override
					public void execute() {
						if (npc != null) {
							if (player.getPosition().isWithinInteractionDistance(new Position(npc.getX(), npc.getY()))) {
								NpcInteraction.secondOption(player, npc);
								stop();
							}
						}
					}
				});
			}
			break;
		case 3:
			if (player.getPosition().isWithinInteractionDistance(new Position(npc.getX(), npc.getY()))) {
				NpcInteraction.thirdOption(player, npc);
			} else {
				Server.getTaskScheduler().schedule(new ScheduledTask(1) {
					@Override
					public void execute() {
						if (npc != null) {
							if (player.getPosition().isWithinInteractionDistance(new Position(npc.getX(), npc.getY()))) {
								NpcInteraction.thirdOption(player, npc);
								stop();
							}
						}
					}
				});
			}
			break;
		case 4:
			if (player.getPosition().isWithinInteractionDistance(new Position(npc.getX(), npc.getY()))) {
				NpcInteraction.fourthOption(player, npc);
			} else {
				Server.getTaskScheduler().schedule(new ScheduledTask(1) {
					@Override
					public void execute() {
						if (!player.isActive()) {
							stop();
							return;
						}
						if (npc != null) {
							if (player.getPosition().isWithinInteractionDistance(new Position(npc.getX(), npc.getY()))) {
								NpcInteraction.fourthOption(player, npc);
								stop();
							}
						}
					}
				});
			}
			break;
		}
		player.setFollowing(null);
		player.face(player, npc.getPosition());
		npc.face(npc, player.getPosition());
		this.stop();
	}

}

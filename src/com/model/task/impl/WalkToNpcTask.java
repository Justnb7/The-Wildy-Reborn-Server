package com.model.task.impl;

import com.model.Server;
import com.model.game.character.npc.NPC;
import com.model.game.character.player.Player;
import com.model.game.character.player.content.clicking.npc.NpcInteraction;
import com.model.game.location.Location;
import com.model.task.ScheduledTask;

/**
 * Handles walking towards npcs
 * 
 * @author Patrick van Elderen
 *
 */
public class WalkToNpcTask extends ScheduledTask {

	/**
	 * The npc we're interacting with
	 * 
	 */
	private final NPC npc;

	/**
	 * The player interacting with the npc
	 */
	private final Player player;

	/**
	 * The option
	 */
	private final int clickType;

	/**
	 * Create a new {@link WalkToNpcTask}.
	 * 
	 * @param npc
	 *            the npc that we're interacting with.
	 * @param player
	 *            the player that is interacting with the npc.
	 * @param clickType
	 *            the click option, npcs have 4 click options
	 */
	public WalkToNpcTask(Player player, NPC npc, int clickType) {
		super(1, false);
		this.npc = npc;
		this.player = player;
		this.clickType = clickType;
	}

	@Override
	public void execute() {

		if (player.getPosition().isWithinInteractionDistance(new Location(npc.getX(), npc.getY()))) {
			switch (clickType) {
			case 1:
				NpcInteraction.firstOption(player, npc);
				break;
			case 2:
				NpcInteraction.secondOption(player, npc);
				break;
			case 3:
				NpcInteraction.thirdOption(player, npc);
				break;
			case 4:
				NpcInteraction.fourthOption(player, npc);
				break;
			}
		} else {
			Server.getTaskScheduler().schedule(new ScheduledTask(1) {
				@Override
				public void execute() {
					if (!player.isActive()) {
						stop();
						return;
					}
					if (npc != null) {
						switch (clickType) {
						case 1:
							NpcInteraction.firstOption(player, npc);
							break;
						case 2:
							NpcInteraction.secondOption(player, npc);
							break;
						case 3:
							NpcInteraction.thirdOption(player, npc);
							break;
						case 4:
							NpcInteraction.fourthOption(player, npc);
							break;
						}
					}
				}
			});
		}
		player.setFollowing(null);
		player.face(player, npc.getPosition());
		npc.face(npc, player.getPosition());
		this.stop();
	}
}
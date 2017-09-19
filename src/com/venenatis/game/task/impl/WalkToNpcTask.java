package com.venenatis.game.task.impl;

import com.venenatis.game.content.clicking.npcs.NpcInteraction;
import com.venenatis.game.location.Location;
import com.venenatis.game.model.entity.npc.NPC;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.task.Task;

/**
 * Handles walking towards npcs
 * 
 * @author Patrick van Elderen
 *
 */
public class WalkToNpcTask extends Task {

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
		player.faceEntity(npc);
	}
	
	@Override
	public void execute() {
		if (!player.isActive()) {
			stop();
			return;
		}

		if (player.getLocation().isWithinInteractionDistance(new Location(npc.getX(), npc.getY()))) {
			// in distance. interact and stop cycle.
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
			stop();
			// reached target. face coords.
			player.setFollowing(null);
			player.face(npc.getLocation());
			npc.face(player.getLocation());
		} else {
			// do nothing this cycle. try again next time this Task is executed.
		}
	}
}
package com.model.task.impl;

import com.model.game.World;
import com.model.game.character.npc.Npc;
import com.model.game.character.player.Player;
import com.model.task.EventListener;

/**
 * An {@link EventListener} implementation that will remove an npc from the
 * world if it is not in distance of a certain player.
 * 
 * @author lare96
 */
public abstract class NpcDistanceEventListener extends EventListener {

	/** The npc that will be "tracked". */
	private Npc npc;

	/** The player that the npc must be close to. */
	private Player player;

	/**
	 * Create a new {@link NpcDistanceEventListener}.
	 * 
	 * @param npc
	 *            the npc that will be "tracked".
	 * @param player
	 *            the player that the npc must be close to.
	 */
	public NpcDistanceEventListener(Npc npc, Player player) {
		this.npc = npc;
		this.player = player;
	}

	@Override
	public boolean listenFor() {
		if (npc.isDead || World.getWorld().getNpcs().get(npc.getIndex()) == null) {
			stop();
			return true;
		}

		return npc.getLocation().withinDistance(player.getLocation(), 7) && player.isActive();
	}
}
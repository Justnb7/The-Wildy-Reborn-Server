package com.model.game.character.player.content.cluescrolls;

import com.model.game.character.npc.Npc;
import com.model.game.character.player.Player;
import com.model.game.character.player.packets.encode.impl.SendMessagePacket;
import com.model.task.impl.NpcDistanceEventListener;

/**
 * @author lare96 <http://github.com/lare96>
 */
public class ClueNpcDistanceEventListener extends NpcDistanceEventListener {

	private Npc npc;
	private Player player;

	public ClueNpcDistanceEventListener(Npc npc, Player player) {
		super(npc, player);
		this.npc = npc;
		this.player = player;
	}

	@Override
	public void run() {
		npc.remove();

		if (!player.isActive()) {
			player.clueContainer = null;
			player.write(new SendMessagePacket("You wandered too far off! The boss left and he has taken the reward with him."));
		}
	}
}
package com.model.game.character.player.content.cluescrolls;

import com.model.game.character.npc.NPC;
import com.model.game.character.player.Player;
import com.model.game.character.player.packets.out.SendMessagePacket;
import com.model.task.impl.NpcDistanceEventListener;

/**
 * @author lare96 <http://github.com/lare96>
 */
public class ClueNpcDistanceEventListener extends NpcDistanceEventListener {

	private NPC npc;
	private Player player;

	public ClueNpcDistanceEventListener(NPC npc, Player player) {
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
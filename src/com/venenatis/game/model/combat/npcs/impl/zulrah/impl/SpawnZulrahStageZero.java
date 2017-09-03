package com.venenatis.game.model.combat.npcs.impl.zulrah.impl;

import com.venenatis.game.event.CycleEventContainer;
import com.venenatis.game.location.Location;
import com.venenatis.game.model.combat.data.CombatStyle;
import com.venenatis.game.model.combat.npcs.impl.zulrah.Zulrah;
import com.venenatis.game.model.combat.npcs.impl.zulrah.ZulrahLocation;
import com.venenatis.game.model.combat.npcs.impl.zulrah.ZulrahStage;
import com.venenatis.game.model.entity.npc.NPC;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.server.Server;

public class SpawnZulrahStageZero extends ZulrahStage {

	public SpawnZulrahStageZero(Zulrah zulrah, Player player) {
		super(zulrah, player);
	}

	@Override
	public void execute(CycleEventContainer container) {
		if (container.getOwner() == null || zulrah == null || player == null || player.getCombatState().isDead() || zulrah.getInstancedZulrah() == null) {
			container.stop();
			return;
		}
		int cycle = container.getTotalTicks();
		if (cycle == 8) {
			player.getActionSender().sendScreenFade("Welcome to Zulrah's shrine", -1, 4);
			player.setTeleportTarget(new Location(2268, 3069, zulrah.getInstancedZulrah().getHeight()));
		}
		if (cycle == 13) {
			Server.npcHandler.spawn(player, 2042, new Location(2266, 3072, zulrah.getInstancedZulrah().getHeight()), -1, false, false);
			NPC npc = NPC.getNpc(2042, 2266, 3072, zulrah.getInstancedZulrah().getHeight());
			if (npc == null) {
				player.getActionSender().sendMessage("Something went wrong, please contact staff.");
				container.stop();
				return;
			}
			zulrah.setNpc(npc);
			npc.setFacePlayer(false);
			npc.faceEntity(player);
			npc.playAnimation(new Animation(5073));
		}
		if (cycle == 18) {
			zulrah.changeStage(1, CombatStyle.RANGE, ZulrahLocation.NORTH);
			container.stop();
		}
	}

}
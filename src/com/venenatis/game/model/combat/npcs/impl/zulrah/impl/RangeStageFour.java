package com.venenatis.game.model.combat.npcs.impl.zulrah.impl;

import java.util.Arrays;

import com.venenatis.game.event.CycleEventContainer;
import com.venenatis.game.event.CycleEventHandler;
import com.venenatis.game.model.combat.data.CombatStyle;
import com.venenatis.game.model.combat.npcs.impl.zulrah.DangerousEntity;
import com.venenatis.game.model.combat.npcs.impl.zulrah.DangerousLocation;
import com.venenatis.game.model.combat.npcs.impl.zulrah.SpawnDangerousEntity;
import com.venenatis.game.model.combat.npcs.impl.zulrah.Zulrah;
import com.venenatis.game.model.combat.npcs.impl.zulrah.ZulrahLocation;
import com.venenatis.game.model.combat.npcs.impl.zulrah.ZulrahStage;
import com.venenatis.game.model.entity.player.Player;

public class RangeStageFour extends ZulrahStage {

	public RangeStageFour(Zulrah zulrah, Player player) {
		super(zulrah, player);
	}

	@Override
	public void execute(CycleEventContainer container) {
		if (container.getOwner() == null || zulrah == null || zulrah.getNpc() == null || zulrah.getNpc().getCombatState().isDead() || player == null || player.getCombatState().isDead() || zulrah.getInstancedZulrah() == null) {
			container.stop();
			return;
		}
		int ticks = container.getTotalTicks();
		if (ticks == 4) {
			zulrah.getNpc().setFacePlayer(false);
			CycleEventHandler.getSingleton().addEvent(player, new SpawnDangerousEntity(zulrah, player, Arrays.asList(DangerousLocation.EAST, DangerousLocation.SOUTH_EAST, DangerousLocation.SOUTH_WEST), DangerousEntity.TOXIC_SMOKE, 40), 1);
		} else if (ticks == 16) {
			CycleEventHandler.getSingleton().addEvent(player, new SpawnDangerousEntity(zulrah, player, Arrays.asList(DangerousLocation.SOUTH_EAST, DangerousLocation.SOUTH_WEST), DangerousEntity.MINION_NPC), 1);
		} else if (ticks == 26) {
			zulrah.getNpc().setFacePlayer(true);
			zulrah.changeStage(5, CombatStyle.MAGIC, ZulrahLocation.SOUTH);
			zulrah.getNpc().totalAttacks = 0;
			container.stop();
		}
	}

}
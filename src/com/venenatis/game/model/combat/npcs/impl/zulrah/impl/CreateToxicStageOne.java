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

public class CreateToxicStageOne extends ZulrahStage {

	public CreateToxicStageOne(Zulrah zulrah, Player player) {
		super(zulrah, player);
	}

	@Override
	public void execute(CycleEventContainer container) {
		if (container.getOwner() == null || zulrah == null || zulrah.getNpc() == null || zulrah.getNpc().getCombatState().isDead() || player == null || player.getCombatState().isDead()
				|| zulrah.getInstancedZulrah() == null) {
			container.stop();
			return;
		}
		if (container.getTotalTicks() == 1) {
			zulrah.getNpc().setFacePlayer(false);
			CycleEventHandler.getSingleton().addEvent(player, new SpawnDangerousEntity(zulrah, player, Arrays.asList(DangerousLocation.values()), DangerousEntity.TOXIC_SMOKE, 40), 1);
		} else if (container.getTotalTicks() >= 19) {
			zulrah.getNpc().totalAttacks = 0;
			zulrah.changeStage(2, CombatStyle.MELEE, ZulrahLocation.NORTH);
			container.stop();
		}
	}

}
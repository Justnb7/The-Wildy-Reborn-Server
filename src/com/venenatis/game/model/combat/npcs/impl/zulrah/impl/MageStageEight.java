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



public class MageStageEight extends ZulrahStage {

	private int finishedAttack;

	public MageStageEight(Zulrah zulrah, Player player) {
		super(zulrah, player);
	}

	@Override
	public void execute(CycleEventContainer container) {
		if (container.getOwner() == null || zulrah == null || zulrah.getNpc() == null || zulrah.getNpc().getCombatState().isDead() || player == null || player.getCombatState().isDead()
				|| zulrah.getInstancedZulrah() == null) {
			container.stop();
			return;
		}
		int ticks = container.getTotalTicks();
		if (zulrah.getNpc().totalAttacks >= 5 && finishedAttack == 0) {
			finishedAttack = ticks;
			zulrah.getNpc().getCombatState().setAttackDelay(18);
			CycleEventHandler.getSingleton().addEvent(player, new SpawnDangerousEntity(zulrah, player, Arrays.asList(DangerousLocation.SOUTH_EAST, DangerousLocation.SOUTH_WEST), DangerousEntity.MINION_NPC), 1);
		}
		if (finishedAttack > 0) {
			zulrah.getNpc().setFacePlayer(false);
			if (ticks - finishedAttack == 2) {
				CycleEventHandler.getSingleton().addEvent(player, new SpawnDangerousEntity(zulrah, player, Arrays.asList(DangerousLocation.SOUTH_EAST, DangerousLocation.SOUTH_WEST, DangerousLocation.EAST), DangerousEntity.TOXIC_SMOKE, 35), 1);
			} else if (ticks - finishedAttack == 16) {
				zulrah.getNpc().setFacePlayer(true);
				zulrah.getNpc().totalAttacks = 0;
				zulrah.changeStage(9, CombatStyle.RANGE, ZulrahLocation.WEST);
				container.stop();
			}
		}
	}
}
package com.venenatis.game.model.combat.npcs.impl.zulrah.impl;

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

public class MageStageThree extends ZulrahStage {

	public MageStageThree(Zulrah zulrah, Player player) {
		super(zulrah, player);
	}

	@Override
	public void execute(CycleEventContainer container) {
		if (container.getOwner() == null || zulrah == null || zulrah.getNpc() == null || zulrah.getNpc().getCombatState().isDead() || player == null || player.getCombatState().isDead()
				|| zulrah.getInstancedZulrah() == null) {
			container.stop();
			return;
		}
		zulrah.getNpc().setFacePlayer(true);
		if (zulrah.getNpc().totalAttacks > 5) {
			player.getZulrahEvent().changeStage(4, CombatStyle.RANGE, ZulrahLocation.WEST);
			zulrah.getNpc().totalAttacks = 0;
			container.stop();
			return;
		}
	}

}
package com.venenatis.game.model.entity.player.instance.impl;

import com.venenatis.game.model.combat.npcs.impl.zulrah.Zulrah;
import com.venenatis.game.model.entity.Boundary;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.instance.SingleInstancedArea;
import com.venenatis.game.world.World;
import com.venenatis.server.Server;

public class SingleInstancedZulrah extends SingleInstancedArea {

	public SingleInstancedZulrah(Player player, Boundary boundary, int height) {
		super(player, boundary, height);
	}

	@Override
	public void onDispose() {
		Zulrah zulrah = player.getZulrahEvent();
		if (zulrah.getNpc() != null) {
			zulrah.getNpc().kill(zulrah.getNpc().getId(), height);
		}
		Server.getGlobalObjects().remove(11700, height);
		zulrah.getNpc().kill(Zulrah.SNAKELING, height);
	}

}
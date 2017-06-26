package com.venenatis.game.net.packet.in;

import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.net.packet.PacketType;
import com.venenatis.game.task.impl.DistancedActionTask;
import com.venenatis.game.world.World;


public class TradePacketHandler implements PacketType {

	@Override
	public void handle(Player player, int packetType, int packetSize) {
		
        int otherPlayerTradeIndex = player.getInStream().readSignedWordBigEndian();

		/*if ((Boolean) player.getAttributes().get("trading")) {
			return;
		}*/

		if (otherPlayerTradeIndex == player.getIndex()) {
			return;
		}

		Player other = (Player) World.getWorld().getPlayers().get(otherPlayerTradeIndex);

		if (!other.isRegistered() || other.isTeleporting() || other.isDead()) {
			return;
		}

		player.setDistancedTask(new DistancedActionTask() {
			@Override
			public void onReach() {
				player.setOtherPlayerTradeIndex(otherPlayerTradeIndex);
				player.getTradeSession().requestTrade(player, other);
				stop();
			}

			@Override
			public boolean reached() {
				player.face(player, other.getLocation());
				return player.distanceToPoint(other.getX(), other.getY()) < 2;
			}

		});
	}

}

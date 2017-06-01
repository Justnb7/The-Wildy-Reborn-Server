package com.model.net.packet.in;

import com.model.game.World;
import com.model.game.character.player.Player;
import com.model.net.packet.PacketType;
import com.model.task.impl.DistancedActionTask;


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

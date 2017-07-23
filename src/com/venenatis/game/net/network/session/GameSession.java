package com.venenatis.game.net.network.session;

import java.util.LinkedList;
import java.util.Queue;

import com.venenatis.game.constants.Constants;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.net.packet.Packet;
import com.venenatis.game.net.packet.PacketHandler;

import io.netty.channel.Channel;

public class GameSession extends Session {

	private final Player player;
	private final Queue<Packet> queuedPackets = new LinkedList<>();

	public GameSession(Player player, Channel channel) {
		super(channel, player);
		this.player = player;
	}

	@Override
	public void receiveMessage(Object object) {
		if (queuedPackets.size() < 10) {
			queuedPackets.add((Packet) object);
		}
	}
	
	/**
	 * Processes incoming packets for the player
	 */
	public void processQueuedPackets() {
		Packet p = null;
		int processed = 0;
		while ((p = queuedPackets.poll()) != null) {
			//Only allow 20 packets sent otherwise someone floods the server.
			if (processed > Constants.MAX_INCOMING_PACKETS_PER_CYCLE) {
				break;
			}
			player.getInStream().offset = 0;
			player.getInStream().buffer = p.getPayload().array();
			if (p.getOpcode() > 0) {
				PacketHandler.processPacket(player, p.getOpcode(), p.getPayload().readableBytes());
				processed++;
			}
		}
	}
	
	public Player getPlayer() {
		return player;
	}

	public void close() {
		if (getChannel().isOpen()) {
			// getChannel().close();
		}
	}

}

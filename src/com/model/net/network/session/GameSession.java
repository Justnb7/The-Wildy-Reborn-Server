package com.model.net.network.session;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.model.game.character.player.Player;
import com.model.net.network.Packet;
import com.model.net.packet.PacketHandler;

import io.netty.channel.Channel;

public class GameSession extends Session {

	private final Player player;
	private final Queue<Packet> queuedPackets = new LinkedList<>();
	private Queue<Packet> queuedSubPackets = new ConcurrentLinkedQueue<>();
	public GameSession(Player player, Channel channel) {
		super(channel, player);
		this.player = player;
	}

	@Override
	public void receiveMessage(Object object) {
		if (queuedPackets.size() < 10 && ((Packet) object).getOpcode() != 41) {
			queuedPackets.add((Packet) object);
		} else {
			if (((Packet) object).getOpcode() == 41) {
				queuedSubPackets.add((Packet) object);
			}
		}
	}

	/**
	 * Processes incoming packets for the player
	 */
	public void processQueuedPackets() {
		for(int count = 0; count < 10; count++) {
			Packet p = queuedPackets.poll();
			if(p == null) break;
			player.getInStream().offset = 0;
			player.getInStream().buffer = p.getBuffer().array();
			if (p.getOpcode() > 0) { //processes all packets
				PacketHandler.processPacket(player, p.getOpcode(), p.getBuffer().readableBytes());
			}
		}
	}
	
	public void processSubQueuedPackets() {
		for(int count = 0; count < 10; count++) {
			Packet p = queuedSubPackets.poll();
			if(p == null) break;
			player.getInStream().offset = 0;
			player.getInStream().buffer = p.getBuffer().array();
			if(p.getOpcode() == 122) {
				continue;
			}
			if (p.getOpcode() > 0) { //processing packet 41 only (wearItems)
				PacketHandler.processSubPacket(player, p.getOpcode(), p.getBuffer().readableBytes());
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

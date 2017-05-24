package com.model.net.packet.in;

import com.model.Server;
import com.model.game.character.player.Player;
import com.model.game.location.Location;
import com.model.net.packet.PacketType;
import com.model.task.impl.WalkToObjectTask;

/**
 * Object option packet handler.
 * 
 * @author Patrick van Elderen
 * 
 */
public class ObjectOptionPacketHandler implements PacketType {
	
	/**
	 * Option 1 opcode.
	 */
	private final int OPTION_1 = 132; 
	
	/**
	 * Option 2 opcode.
	 */
	private final int OPTION_2 = 252;
	
	/**
	 * Option 3 opcode.
	 */
	private final int OPTION_3 = 70;

	@Override
	public void handle(final Player player, int packet, int size) {
		switch (packet) {
		case OPTION_1:
			handleOption1(player, packet);
			break;
		case OPTION_2:
			handleOption2(player, packet);
			break;
		case OPTION_3:
			handleOption3(player, packet);
			break;
		}

	}

	/**
	 * The first object action opcode 132
	 * @param player
	 *        The player using the first object action
	 * @param packet
	 *        The packetId
	 */
	private void handleOption1(Player player, int packet) {
		int x = player.getInStream().readSignedWordBigEndianA();
		int id = player.getInStream().readUnsignedWord();
		int y = player.getInStream().readUnsignedWordA();
		Location position = Location.create(x, y, player.getLocation().getZ());
		
		//Safety check
		if (player.isTeleporting()) {
			return;
		}
		
		Server.getTaskScheduler().schedule(new WalkToObjectTask(player, position, id, 1));

	}
	
	/**
	 * The second object action opcode 252
	 * @param player
	 *        The player using the second object action
	 * @param packet
	 *        The packetId
	 */
	private void handleOption2(Player player, int packet) {
		int id = player.getInStream().readUnsignedWordBigEndianA();
		int y = player.getInStream().readSignedWordBigEndian();
		int x = player.getInStream().readUnsignedWordA();
		Location position = Location.create(x, y, player.getLocation().getZ());
		
		// Safety check
		if (player.isTeleporting()) {
			return;
		}

		Server.getTaskScheduler().schedule(new WalkToObjectTask(player, position, id, 2));
	}
	
	/**
	 * The third object action opcode 70
	 * @param player
	 *        The player using the third object action
	 * @param packet
	 *        The packetId
	 */
	private void handleOption3(Player player, int packet) {
		int x = player.getInStream().readSignedWordBigEndian();
		int y = player.getInStream().readUnsignedWord();
		int id = player.getInStream().readUnsignedWordBigEndianA();
		Location position = Location.create(x, y, player.getLocation().getZ());
		
		//Safety check
		if (player.isTeleporting()) {
			return;
		}
		
		Server.getTaskScheduler().schedule(new WalkToObjectTask(player, position, id, 3));
	}
}

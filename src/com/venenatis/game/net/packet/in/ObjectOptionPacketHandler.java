package com.venenatis.game.net.packet.in;

import com.venenatis.game.location.Location;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.net.packet.IncomingPacketListener;
import com.venenatis.game.task.impl.WalkToObjectTask;
import com.venenatis.server.Server;

/**
 * Object option packet handler.
 * 
 * @author Patrick van Elderen
 * 
 */
public class ObjectOptionPacketHandler implements IncomingPacketListener {
	
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
		
		//Safety check
		if (player.getTeleportAction().isTeleporting() || player.hasAttribute("busy")) {
			player.debug("stop, cuz of random circumstances");
			return;
		}
		
		//Reset all stored actions
		player.getActionQueue().clearAllActions();
		
		//Close any open interface
		player.getActionSender().removeAllInterfaces();
		
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
		int id = player.getInStream().readUnsignedShort();
		int y = player.getInStream().readUnsignedWordA();
		Location location = Location.create(x, y, player.getLocation().getZ());
		
		Server.getTaskScheduler().schedule(new WalkToObjectTask(player, location, id, 1));
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
		int y = player.getInStream().readUnsignedShort();
		int id = player.getInStream().readUnsignedWordBigEndianA();
		Location position = Location.create(x, y, player.getLocation().getZ());
		
		Server.getTaskScheduler().schedule(new WalkToObjectTask(player, position, id, 3));
	}
}

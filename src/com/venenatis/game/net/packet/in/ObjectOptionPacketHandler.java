package com.venenatis.game.net.packet.in;

import com.venenatis.game.location.Location;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.net.packet.IncomingPacketListener;
import com.venenatis.game.task.impl.WalkToObjectTask;
import com.venenatis.game.world.object.GameObject;
import com.venenatis.game.world.pathfinder.impl.DefaultPathFinder;
import com.venenatis.game.world.pathfinder.impl.ObjectPathFinder;
import com.venenatis.game.world.pathfinder.region.RegionStoreManager;
import com.venenatis.server.Server;

/**
 * Object option packet handler.
 * 
 * @author Patrick van Elderen
 * 
 */
public class ObjectOptionPacketHandler implements IncomingPacketListener {
	
	/**
	 * The last interaction that player made that is recorded in milliseconds
	 */
	private long lastInteraction;
	
	/**
	 * The constant delay that is required inbetween interactions
	 */
	private static final long INTERACTION_DELAY = 2_000L;
	
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
		//Setting here is correct right? ye i delayed clicks by 2 secs
		player.debug("Click");
		lastInteraction = System.currentTimeMillis();
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
		Location location = Location.create(x, y, player.getLocation().getZ());
		
		System.out.println(String.format("[ObjectInteraction first option] - location: %s object: %d ", location, id));
		
		// Client isnt very happy with this shit so we have to hard call it
		if (id == 10357 && x == 3318 && y == 3166) {
			final GameObject obj = RegionStoreManager.get().getGameObject(new Location(x, y, player.getZ()), id);
			ObjectPathFinder.find(player, obj);
		}
		
		if (id == 10777 && x == 3191 && y == 3415) {
			final GameObject obj = RegionStoreManager.get().getGameObject(new Location(x, y, player.getZ()), id);
			ObjectPathFinder.find(player, obj);
		}
		
		if (id == 10355 && x == 3269 && y == 3166 && player.getZ() == 3) {
			player.doPath(new DefaultPathFinder(), player, null, 3265, 3166, false, true);
		}
		
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
		int y = player.getInStream().readUnsignedWord();
		int id = player.getInStream().readUnsignedWordBigEndianA();
		Location position = Location.create(x, y, player.getLocation().getZ());
		
		Server.getTaskScheduler().schedule(new WalkToObjectTask(player, position, id, 3));
	}
}

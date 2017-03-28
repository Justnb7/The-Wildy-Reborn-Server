package com.model.game.character.player.packets.in;

import com.model.game.character.player.Player;
import com.model.game.character.player.content.clicking.object.ObjectInteraction;
import com.model.game.character.player.packets.PacketType;
import com.model.game.location.Position;

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
	private static final int OPTION_1 = 132; 
	
	/**
	 * Option 2 opcode.
	 */
	private static final int OPTION_2 = 252;
	
	/**
	 * Option 3 opcode.
	 */
	private static final int OPTION_3 = 70;

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
		Position position = Position.create(x, y, player.getPosition().getZ());
		
		//Safety check
		if (player.isTeleporting()) {
			return;
		}
		
		//Check if we've reached destination
		if (player.getPosition().isWithinInteractionDistance(position)) {
			player.face(player, position);
			ObjectInteraction.handleFirstClickAction(player, position, id);
		}
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
		Position position = Position.create(x, y, player.getPosition().getZ());
		
		// Safety check
		if (player.isTeleporting()) {
			return;
		}

		// Check if we've reached destination
		if (player.getPosition().isWithinInteractionDistance(position)) {
			player.face(player, position);
			ObjectInteraction.handleSecondClickAction(player, position, id);
		}
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
		Position position = Position.create(x, y, player.getPosition().getZ());
		
		//Safety check
		if (player.isTeleporting()) {
			return;
		}
		
		//Check if we've reached destination
		if (player.getPosition().isWithinInteractionDistance(position)) {
			player.face(player, position);
			ObjectInteraction.handleThirdClickAction(player, position, id);
		}
		
	}
}

package com.model.game.character.player.packets.out;

import com.model.game.character.player.Player;
import com.model.game.character.player.packets.PacketEncoder;
import com.model.game.item.container.impl.Trade;

public class SendSidebarInterfacePacket implements PacketEncoder {

	/**
	 * The opcode for the link to send
	 */
	private static final int OPCODE = 71;
	
	private final int menu;
	
	private final int id;

	/**
	 * Sends a link to the client to open a url
	 * 
	 * @param link
	 *            The link to the url to open up
	 */
	public SendSidebarInterfacePacket(int menu, int id) {
		this.menu = menu;
		this.id = id;
	}

	@Override
	public void encode(Player player) {
		if (player != null) {
			player.stopSkillTask();
			if (Trade.inTrade(player)) {
				Trade.declineTrade(player);
			}
			if (player.getOutStream() != null) {
				player.outStream.writeFrame(OPCODE);
				player.outStream.writeShort(id);
				player.outStream.putByteA(menu);
			}
			player.flushOutStream();
		}
	}

}
package com.model.net.packet.in;

import com.model.UpdateFlags.UpdateFlag;
import com.model.game.character.player.Player;
import com.model.net.packet.PacketType;

/**
 * The packet responsible for changing a players appearance.
 * 
 * @author Patrick van Elderen
 */
public class SetAppearancePacketHandler implements PacketType {

	@Override
	public void handle(final Player player, final int packetId, final int packetSize) {
		int gender = player.getInStream().readSignedByte();
		int head = player.getInStream().readSignedByte();
		final int beard = player.getInStream().readSignedByte();
		final int chest = player.getInStream().readSignedByte();
		final int arms = player.getInStream().readSignedByte();
		final int hands = player.getInStream().readSignedByte();
		final int legs = player.getInStream().readSignedByte();
		final int feet = player.getInStream().readSignedByte();
		final int hairColour = player.getInStream().readSignedByte();
		final int torsoColour = player.getInStream().readSignedByte();
		final int legColour = player.getInStream().readSignedByte();
		final int feetColour = player.getInStream().readSignedByte();
		final int skinColour = player.getInStream().readSignedByte();
		
		int look[] = new int[13];
		look[0] = gender;

		look[6] = head;
		look[7] = chest;
		look[8] = arms;
		look[9] = hands;
		look[10] = legs;
		look[11] = feet;
		look[12] = beard;

		look[1] = hairColour;
		look[2] = torsoColour;
		look[3] = legColour;
		look[4] = feetColour;
		look[5] = skinColour;
		
		player.getAppearance().setLook(look);
		player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
		player.getActionSender().removeAllInterfaces();
			
	}

}
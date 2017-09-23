package com.venenatis.game.net.packet.in;

import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.UpdateFlags.UpdateFlag;
import com.venenatis.game.net.packet.IncomingPacketListener;

/**
 * The packet responsible for changing a players appearance.
 * 
 * @author Patrick van Elderen
 */
public class SetAppearancePacketHandler implements IncomingPacketListener {

	@Override
	public void handle(final Player player, final int packetId, final int packetSize) {
		final int gender = player.getInStream().readSignedByte();
		
		int head = player.getInStream().readSignedByte();
		if(head<0)
			head += 256;
		int beard = player.getInStream().readSignedByte();
		if(beard<0)
			beard += 256;
		int chest = player.getInStream().readSignedByte();
		if(chest<0)
			chest += 256;
		int arms = player.getInStream().readSignedByte();
		if(arms<0)
			arms += 256;
		int hands = player.getInStream().readSignedByte();
		if(hands<0)
			hands += 256;
		int legs = player.getInStream().readSignedByte();
		if(legs<0)
			legs += 256;
		int feet = player.getInStream().readSignedByte();
		if(feet<0)
			feet += 256;
		
		final int hairColour = player.getInStream().readSignedByte();
		final int torsoColour = player.getInStream().readSignedByte();
		final int legColour = player.getInStream().readSignedByte();
		final int feetColour = player.getInStream().readSignedByte();
		final int skinColour = player.getInStream().readSignedByte();
		
        boolean female = gender == 1;
		
		if (gender < 0 || gender > 1) {
			return;
		}
		
		if (female) {
			beard = 1000;
		}
		
		player.getAppearance().setGender(gender);
		player.getAppearance().setColour(0, hairColour);
		player.getAppearance().setColour(1, torsoColour);
		player.getAppearance().setColour(2, legColour);
		player.getAppearance().setColour(3, feetColour);
		player.getAppearance().setColour(4, skinColour);
		player.getAppearance().setLook(0, head);
		player.getAppearance().setLook(1, beard);
		player.getAppearance().setLook(2, chest);
		player.getAppearance().setLook(3, arms);
		player.getAppearance().setLook(4, hands);
		player.getAppearance().setLook(5, legs);
		player.getAppearance().setLook(6, feet);
		player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
		player.getActionSender().removeAllInterfaces();
			
	}

}
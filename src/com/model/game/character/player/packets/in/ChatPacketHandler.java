package com.model.game.character.player.packets.in;

import com.model.game.character.player.Player;
import com.model.game.character.player.packets.PacketType;
import com.model.game.character.player.packets.encode.impl.SendMessagePacket;
import com.model.utility.Utility;
import com.model.utility.logging.PlayerLogging;
import com.model.utility.logging.PlayerLogging.LogType;

/**
 * Chat
 **/
public class ChatPacketHandler implements PacketType {

	@Override
	public void processPacket(Player player, int packetType, int packetSize) {
		player.setChatTextEffects(player.getInStream().readUnsignedByteS());
		player.setChatTextColor(player.getInStream().readUnsignedByteS());
		player.setChatTextSize((byte) (packetSize - 2));
		player.inStream.readBytes_reverseA(player.getChatText(), player.getChatTextSize(), 0);
		String term = Utility.textUnpack(player.getChatText(), packetSize - 2).toLowerCase();
		PlayerLogging.write(LogType.PUBLIC_CHAT, player, "Spoke = "+term);
		if (player.isMuted) {
			player.write(new SendMessagePacket("Sorry, your account is still muted, please appeal on our forums."));
			return;
		}

		if(player.getBankPin().isLocked() && player.getBankPin().getPin().trim().length() > 0) {
			player.write(new SendMessagePacket("You need to enter your pin before you can speak"));
			return;
		}
		if(term.contains("on the percentile dice") || term.contains(" just rolled") || term.contains("just rolled")){
			player.write(new SendMessagePacket("@red@Your message was blocked because it is similar to the ::dice message."));
			return;
		}
		if (!player.getController().canTalk(player)) {
			return;
		}
		player.chatTextUpdateRequired = true;
		player.updateRequired = true;

	}
}

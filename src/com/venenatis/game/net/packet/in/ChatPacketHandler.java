package com.venenatis.game.net.packet.in;

import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.UpdateFlags.UpdateFlag;
import com.venenatis.game.net.packet.PacketType;
import com.venenatis.game.util.Utility;
import com.venenatis.game.util.logging.PlayerLogging;
import com.venenatis.game.util.logging.PlayerLogging.LogType;

/**
 * Handles public chat messages.
 * @author Patrick van Elderen
 *
 */
public class ChatPacketHandler implements PacketType {

	@Override
	public void handle(Player player, int packetType, int packetSize) {
		player.setChatTextEffects(player.getInStream().readUnsignedByteS());
		player.setChatTextColor(player.getInStream().readUnsignedByteS());
		player.setChatTextSize((byte) (packetSize - 2));
		player.inStream.readBytes_reverseA(player.getChatText(), player.getChatTextSize(), 0);
		
		String term = Utility.textUnpack(player.getChatText(), packetSize - 2).toLowerCase();
		
		if (player.isMuted) {
			player.getActionSender().sendMessage("Sorry, your account is still muted, please appeal on our forums.");
			return;
		}
		
		if (!player.getController().canTalk(player)) {
			return;
		}
		
		if(term.contains("on the percentile dice") || term.contains(" just rolled") || term.contains("just rolled")){
			player.getActionSender().sendMessage("@red@Your message was blocked because it is similar to the ::dice message.");
			return;
		}
		
		PlayerLogging.write(LogType.PUBLIC_CHAT, player, "Spoke = "+term);

		player.getUpdateFlags().flag(UpdateFlag.CHAT);

	}
}

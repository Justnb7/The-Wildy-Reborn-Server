package com.model.game.character.player;

import com.model.game.character.player.packets.out.SendSidebarInterface;
import com.model.game.character.player.packets.out.SendSkillPacket;

/**
 * A utility class for sending packets.
 *
 * @author Graham Edgecombe
 */
public class ActionSender {
	
	/**
     * The player.
     */
    private Player player;

    /**
     * Creates an action sender for the specified player.
     *
     * @param player The player to create the action sender for.
     */
    public ActionSender(Player player) {
        this.player = player;
    }

    /**
     * Sends the player's skills.
     *
     * @return The action sender instance, for chaining.
     */
	public ActionSender sendSkills() {
		for (int i = 0; i < Skills.SKILL_COUNT; i++) {
			player.write(new SendSkillPacket(i));
		}
		return this;
	}
	
	public ActionSender hideAllSideBars() {
		for (int i = 0; i < 14; i++)
			player.write(new SendSidebarInterface(i, -1));
		return this;
	}
	
	public ActionSender addClanMember(String username) {
		if (player.getOutStream() != null) {
            player.getOutStream().putFrameVarByte(216);
            int offset = player.getOutStream().offset;
            player.getOutStream().putRS2String(username);
            player.getOutStream().putFrameSizeByte(offset);
        }
        return this;
	}
	
	public ActionSender sendClanMessage(String member, String message, String clan, int rights) {
		if (player.getOutStream() != null) {
            player.getOutStream().putFrameVarShort(217);
            int offset = player.getOutStream().offset;
            player.getOutStream().putRS2String(member);
            player.getOutStream().putRS2String(message);
            player.getOutStream().putRS2String(clan);
            player.getOutStream().writeShort(rights);
            player.getOutStream().putFrameSizeShort(offset);
        }
		return this;
	}
	
	public ActionSender sendString(String message, int interfaceId) {
		/*if (!player.checkPacket126Update(message, interfaceId) && interfaceId != 56306 && interfaceId != 39507) {
			return;
		}*/
		if (player.getOutStream() != null) {
			player.getOutStream().putFrameVarShort(126);
			int offset = player.getOutStream().offset;
			player.getOutStream().putRS2String(message == null ? "" : message);
			player.getOutStream().writeWordA(interfaceId);
			player.getOutStream().putFrameSizeShort(offset);
		}
		player.flushOutStream();
		return this;
	}
	
}

package com.model.game.character.player;

import com.model.game.character.player.packets.encode.impl.SendSidebarInterface;
import com.model.game.character.player.packets.encode.impl.SendSkillPacket;

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
	
}

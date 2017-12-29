package com.venenatis.game.net.packet.in;

import com.venenatis.game.model.definitions.ItemDefinition;
import com.venenatis.game.model.definitions.NPCDefinitions;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.net.packet.IncomingPacketListener;
import com.venenatis.game.util.Utility;

/**
 * Handles the incomming examine option packet.
 * 
 * @author Lennard
 *
 */
public class ExamineOptionHandler implements IncomingPacketListener {

	@Override
	public void handle(Player player, int id, int size) {
		final int examineType = player.getInStream().readUnsignedByte();
		final int examineId = player.getInStream().readUnsignedShort();

		sendExamine(player, examineType, examineId);
	}

	public static void sendExamine(final Player player, final int examineType, final int examineId) {
		switch (examineType) {

		// Examine Item
		case 0:
			final ItemDefinition def = ItemDefinition.get(examineId);
			if (def == null || def.getExamine() == null) {
				return;
			}
			String description = def.getExamine();
			if (description.length() == 0) {
				description = "It's a " + def.getName() + ".";
			}
			player.getActionSender().sendMessage(description);
			if (def.getName() == null) {
				return;
			}
			player.getActionSender().sendMessage("Item: @or3@" + def.getName() + "@bla@  Value: @or3@" + Utility.formatNumbers(def.getValue()) + "@bla@  High Alch: @or3@" + Utility.formatNumbers(def.getHighAlch()) + "@bla@.");
			break;
			
		case 1:
			final NPCDefinitions npcDef = NPCDefinitions.get(examineId);
			if (npcDef == null || npcDef.getDescription() == null) {
				return;
			}
			String npcExamine = npcDef.getDescription();
			if (npcExamine.length() == 0) {
				description = "It's a " + npcDef.getName() + ".";
			}
			player.getActionSender().sendMessage(npcExamine);
			break;

		default:
			break;

		}
	}

}
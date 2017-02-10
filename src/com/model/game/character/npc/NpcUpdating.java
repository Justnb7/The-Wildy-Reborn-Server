package com.model.game.character.npc;

import java.util.Iterator;

import com.model.game.World;
import com.model.game.character.player.Player;
import com.model.net.network.rsa.GameBuffer;
import com.model.utility.Utility;

/**
 * Handles updating all of the npcs for a single {@link Player}
 * 
 * @author Mobster
 *
 */
public class NpcUpdating {

	/**
	 * Updates local npcs for a player
	 * 
	 * @param player
	 *            The {@link Player} to update npcs for
	 * @param buffer
	 *            The {@link GameBuffer} to write data too
	 */
	public static void updateNPC(Player player, GameBuffer buffer) {
		GameBuffer updateBlock = new GameBuffer(new byte[5000]);
		
		buffer.putFrameVarShort(65);
		int start = buffer.offset;
		buffer.initBitAccess();

		buffer.writeBits(8, player.localNpcs.size());

		Iterator<Npc> $it = player.localNpcs.iterator();
		while ($it.hasNext()) {
			Npc npc = $it.next();
			if (World.getWorld().getNpcs().get(npc.getIndex()) != null && npc.isVisible() && player.withinDistance(npc) && !npc.getAttribute("teleporting", false)) {
				updateNPCMovement(npc, buffer);
				appendNPCUpdateBlock(npc, updateBlock);
			} else {
				buffer.writeBits(1, 1);
				buffer.writeBits(2, 3);
				$it.remove();
			}
		}

		int added = 0;

		for (Npc npc : World.getWorld().getNpcs()) {

			if (player.localNpcs.size() >= 255) {
				break;
			}

			if (npc == null || player.localNpcs.contains(npc) || !npc.isVisible()) {
				continue;
			}

			if (player.withinDistance(npc)) {
				player.localNpcs.add(npc);
				npc.handleFacing();
				addNewNPC(player, npc, buffer);
				appendNPCUpdateBlock(npc, updateBlock);
				added++;
			}

			if (added >= 25) {
				break;
			}
		}

		if (updateBlock.offset > 0) {
			buffer.writeBits(14, 16383);
			buffer.finishBitAccess();
			buffer.writeBytes(updateBlock.buffer, updateBlock.offset, 0);
		} else {
			buffer.finishBitAccess();
		}
		
		buffer.putFrameSizeShort(start);
		player.flushOutStream();
	}

	/**
	 * Appends an npcs update block
	 * 
	 * @param npc
	 *            The {@link Npc} we are updating the block for
	 * @param buffer
	 *            The {@link GameBuffer} to write the data on
	 */
	private static void appendNPCUpdateBlock(Npc npc, GameBuffer buffer) {
		
		if (!npc.updateRequired)
			return;
		int updateMask = 0;
		if (npc.animUpdateRequired)
			updateMask |= 0x10;
		if (npc.hitUpdateRequired2)
			updateMask |= 8;
		if (npc.gfxUpdateRequired)
			updateMask |= 0x80;
		if (npc.dirUpdateRequired)
			updateMask |= 0x20;
		if (npc.forcedChatRequired)
			updateMask |= 1;
		if (npc.hitUpdateRequired)
			updateMask |= 0x40;
		if (npc.transformUpdateRequired)
			updateMask |= 2;
		if (npc.FocusPointX != -1)
			updateMask |= 4;

		buffer.writeByte(updateMask);

		if (npc.animUpdateRequired)
			appendAnimUpdate(npc, buffer);
		if (npc.hitUpdateRequired2)
			appendHitUpdate2(npc, buffer);
		if (npc.gfxUpdateRequired)
			appendMask80Update(npc, buffer);
		if (npc.dirUpdateRequired)
			appendFaceEntity(npc, buffer);
		if (npc.forcedChatRequired) {
			buffer.putRS2String(npc.forcedText);
		}
		if (npc.hitUpdateRequired)
			appendHitUpdate(npc, buffer);
		if (npc.transformUpdateRequired)
			appendTransformUpdate(npc, buffer);
		if (npc.FocusPointX != -1)
			appendSetFocusDestination(npc, buffer);

	}

	/**
	 * Updates an npcs movement for a player
	 * 
	 * @param str
	 *            The {@link GameBuffer} to write data on
	 */
	private static void updateNPCMovement(Npc npc, GameBuffer str) {
		if (npc.direction == -1) {
			if (npc.updateRequired) {
				str.writeBits(1, 1);
				str.writeBits(2, 0);
			} else {
				str.writeBits(1, 0);
			}
		} else {
			str.writeBits(1, 1);
			str.writeBits(2, 1);
			str.writeBits(3, npc.direction);
			if (npc.updateRequired) {
				str.writeBits(1, 1);
			} else {
				str.writeBits(1, 0);
			}
		}
	}

	/**
	 * Adds a new npc to the map
	 * 
	 * @param player
	 * @param npc
	 * @param buffer
	 */
	private static void addNewNPC(Player player, Npc npc, GameBuffer buffer) {
		buffer.writeBits(14, npc.getIndex());
		int yPos = npc.getY() - player.getY();
		int xPos = npc.getX() - player.getX();
		buffer.writeBits(5, yPos);
		buffer.writeBits(5, xPos);
		buffer.writeBits(1, 0);
		buffer.writeBits(14, npc.npcId);
		buffer.writeBits(npc.updateRequired);
	}

	private static void appendHitUpdate(Npc npc, GameBuffer str) {
		str.writeByteC(npc.primary.getDamage());
		str.writeByteS(npc.primary.getType().getId());
		str.writeByteS(Utility.getCurrentHP(npc.currentHealth, npc.maximumHealth, 100));
		str.writeByteC(100);
	}

	private static void appendFaceEntity(Npc npc, GameBuffer str) {
		str.writeShort(npc.face);
	}

	private static void appendSetFocusDestination(Npc npc, GameBuffer str) {
		str.writeWordBigEndian(npc.FocusPointX * 2 + 1);
		str.writeWordBigEndian(npc.FocusPointY * 2 + 1);
	}

	private static void appendAnimUpdate(Npc npc, GameBuffer str) {
		str.writeWordBigEndian(npc.animNumber);
		str.writeByte(1);
	}

	private static void appendMask80Update(Npc npc, GameBuffer str) {
		str.writeShort(npc.gfx.getId());
		str.putInt(npc.gfx.getDelay() + (65536 * npc.gfx.getHeight()));
	}

	private static void appendHitUpdate2(Npc npc, GameBuffer str) {
		str.putByteA(npc.secondary.getDamage());
		str.writeByteC(npc.secondary.getType().getId());
		str.putByteA(Utility.getCurrentHP(npc.currentHealth, npc.maximumHealth, 100));
		str.writeByte(100);
	}

	private static void appendTransformUpdate(Npc npc, GameBuffer str) {
		str.writeWordBigEndianA(npc.transformId);
	}

}

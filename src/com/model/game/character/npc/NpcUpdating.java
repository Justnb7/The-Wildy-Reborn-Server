package com.model.game.character.npc;

import java.util.Iterator;

import com.model.UpdateFlags;
import com.model.UpdateFlags.UpdateFlag;
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

		buffer.writeBits(8, player.getLocalNPCs().size());

		Iterator<NPC> $it = player.getLocalNPCs().iterator();
		while ($it.hasNext()) {
			NPC npc = $it.next();
			if (World.getWorld().getNPCs().get(npc.getIndex()) != null && npc.isVisible() && player.withinDistance(npc) && !npc.getAttribute("teleporting", false)) {
				updateNPCMovement(npc, buffer);
				appendNPCUpdateBlock(npc, updateBlock);
			} else {
				buffer.writeBits(1, 1);
				buffer.writeBits(2, 3);
				$it.remove();
			}
		}

		int added = 0;

		for (NPC npc : World.getWorld().getNPCs()) {

			if (player.getLocalNPCs().size() >= 255) {
				break;
			}

			if (npc == null || player.getLocalNPCs().contains(npc) || !npc.isVisible()) {
				continue;
			}

			if (player.withinDistance(npc)) {
				player.getLocalNPCs().add(npc);
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
	 *            The {@link NPC} we are updating the block for
	 * @param buffer
	 *            The {@link GameBuffer} to write the data on
	 */
	private static void appendNPCUpdateBlock(NPC npc, GameBuffer buffer) {
		
		/*
		 * Calculate the mask.
		 */
		int updateMask = 0;
		final UpdateFlags flags = npc.getUpdateFlags();
		
		if(flags.get(UpdateFlag.ANIMATION)) {
			updateMask |= 0x10;
		}
		if (npc.hitUpdateRequired2)
			updateMask |= 8;
		if(flags.get(UpdateFlag.GRAPHICS)) {
			updateMask |= 0x80;
		}
		if (npc.faceUpdateRequired)
			updateMask |= 0x20;
		if(flags.get(UpdateFlag.FORCED_CHAT)) {
			updateMask |= 0x1;
		}
		if (npc.hitUpdateRequired)
			updateMask |= 0x40;
		if (npc.transformUpdateRequired)
			updateMask |= 2;
		if (npc.faceTileX != -1)
			updateMask |= 4;

		buffer.writeByte(updateMask);

		if(flags.get(UpdateFlag.ANIMATION)) {
			if (npc.getCurrentAnimation() != null) {
				buffer.writeWordBigEndian(npc.getCurrentAnimation().getId());
				buffer.writeByte(npc.getCurrentAnimation().getDelay());
			}
		}
		
		if (npc.hitUpdateRequired2)
			appendHitUpdate2(npc, buffer);
		if(flags.get(UpdateFlag.GRAPHICS)) {
			buffer.writeShort(npc.getCurrentGraphic().getId());
			buffer.putInt(npc.getCurrentGraphic().getDelay() + (65536 * npc.getCurrentGraphic().getHeight()));
		}
		if (npc.faceUpdateRequired)
			appendFaceEntity(npc, buffer);
		if(flags.get(UpdateFlag.FORCED_CHAT)) {
			buffer.putRS2String(npc.getUpdateFlags().getForcedMessage());
		}
		if (npc.hitUpdateRequired)
			appendHitUpdate(npc, buffer);
		if (npc.transformUpdateRequired)
			appendTransformUpdate(npc, buffer);
		if (npc.faceTileX != -1)
			appendSetFocusDestination(npc, buffer);

	}

	/**
	 * Updates an npcs movement for a player
	 * 
	 * @param str
	 *            The {@link GameBuffer} to write data on
	 */
	private static void updateNPCMovement(NPC npc, GameBuffer str) {
		if (npc.direction == -1) {
			if (npc.getUpdateFlags().isUpdateRequired()) {
				str.writeBits(1, 1);
				str.writeBits(2, 0);
			} else {
				str.writeBits(1, 0);
			}
		} else {
			str.writeBits(1, 1);
			str.writeBits(2, 1);
			str.writeBits(3, npc.direction);
			if (npc.getUpdateFlags().isUpdateRequired()) {
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
	private static void addNewNPC(Player player, NPC npc, GameBuffer buffer) {
		buffer.writeBits(14, npc.getIndex());
		int yPos = npc.getY() - player.getY();
		int xPos = npc.getX() - player.getX();
		buffer.writeBits(5, yPos);
		buffer.writeBits(5, xPos);
		buffer.writeBits(1, 0);
		buffer.writeBits(14, npc.getId());
		buffer.writeBits(npc.getUpdateFlags().isUpdateRequired());
	}

	private static void appendHitUpdate(NPC npc, GameBuffer str) {
		str.writeByteC(npc.primary.getDamage());
		str.writeByteS(npc.primary.getType().getId());
		str.writeByteS(Utility.getCurrentHP(npc.currentHealth, npc.maximumHealth, 100));
		str.writeByteC(100);
	}

	private static void appendFaceEntity(NPC npc, GameBuffer str) {
		str.writeShort(npc.entityFaceIndex);
	}

	private static void appendSetFocusDestination(NPC npc, GameBuffer str) {
		str.writeWordBigEndian(npc.faceTileX * 2 + 1);
		str.writeWordBigEndian(npc.faceTileY * 2 + 1);
	}

	private static void appendHitUpdate2(NPC npc, GameBuffer str) {
		str.putByteA(npc.secondary.getDamage());
		str.writeByteC(npc.secondary.getType().getId());
		str.putByteA(Utility.getCurrentHP(npc.currentHealth, npc.maximumHealth, 100));
		str.writeByte(100);
	}

	private static void appendTransformUpdate(NPC npc, GameBuffer str) {
		str.writeWordBigEndianA(npc.transformId);
	}

}

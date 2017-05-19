package com.model.game.character.npc;

import java.util.Iterator;

import com.model.UpdateFlags;
import com.model.UpdateFlags.UpdateFlag;
import com.model.game.World;
import com.model.game.character.player.Player;
import com.model.net.network.rsa.GameBuffer;

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
		
		/*
		 * The update block holds the update masks and data, and is written
		 * after the main block.
		 */
		GameBuffer updateBlock = new GameBuffer(new byte[5000]);
		
		/*
		 * The main packet holds information about adding, moving and removing
		 * NPCs.
		 */
		buffer.putFrameVarShort(65);
		int start = buffer.offset;
		buffer.initBitAccess();

		/*
		 * Write the current size of the npc list.
		 */
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

		/*
		 * Loop through all NPCs in the world.
		 */
		for (NPC npc : World.getWorld().getNPCs()) {
			/*
			 * Check if there is room left in the local list.
			 */
			if (player.getLocalNPCs().size() >= 255) {
				/*
				 * There is no more room left in the local list. We cannot add
				 * more NPCs, so we just ignore the extra ones. They will be
				 * added as other NPCs get removed.
				 */
				break;
			}

			/*
			 * If they should not be added ignore them.
			 */
			if (npc == null || player.getLocalNPCs().contains(npc) || !npc.isVisible()) {
				continue;
			}

			/*
			 * Add the npc to the local list if it is within distance.
			 */
			if (player.withinDistance(npc)) {
				/*
				 * Add the npc in the packet.
				 */
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

		/*
		 * Check if the update block isn't empty.
		 */
		if (updateBlock.offset > 0) {
			/*
			 * If so, put a flag indicating that an update block follows.
			 */
			buffer.writeBits(14, 16383);
			buffer.finishBitAccess();
			/*
			 * And append the update block.
			 */
			buffer.writeBytes(updateBlock.buffer, updateBlock.offset, 0);
		} else {
			/*
			 * Terminate the packet normally.
			 */
			buffer.finishBitAccess();
		}
		
		/*
		 * Write the packet.
		 */
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
		
		if (!npc.getUpdateFlags().isUpdateRequired())
			return;
		
		/*
		 * Calculate the mask.
		 */
		int updateMask = 0;
		final UpdateFlags flags = npc.getUpdateFlags();
		
		if(flags.get(UpdateFlag.ANIMATION)) {
			updateMask |= 0x10;
		}
		if(flags.get(UpdateFlag.HIT)) {
			updateMask |= 8;
		}
		if(flags.get(UpdateFlag.GRAPHICS)) {
			updateMask |= 0x80;
		}
		if(flags.get(UpdateFlag.FACE_ENTITY)) {
			updateMask |= 0x20;
		}
		if(flags.get(UpdateFlag.FORCED_CHAT)) {
			updateMask |= 0x1;
		}
		if(flags.get(UpdateFlag.HIT_2)) {
			updateMask |= 0x40;
		}
		if (flags.get(UpdateFlag.TRANSFORM)) {
			updateMask |= 2;
		}
		if (flags.get(UpdateFlag.FACE_COORDINATE)) {
			updateMask |= 4;
		}

		buffer.writeByte(updateMask);

		if(flags.get(UpdateFlag.ANIMATION)) {
			if (npc.getCurrentAnimation() != null) {
				buffer.writeWordBigEndian(npc.getCurrentAnimation().getId());
				buffer.writeByte(npc.getCurrentAnimation().getDelay());
			}
		}
		
		if(flags.get(UpdateFlag.HIT)) {
			appendHitUpdate1(npc, buffer);
		}
		if(flags.get(UpdateFlag.GRAPHICS)) {
			buffer.writeShort(npc.getCurrentGraphic().getId());
			buffer.putInt(npc.getCurrentGraphic().getDelay() + (65536 * npc.getCurrentGraphic().getHeight()));
		}
		if(flags.get(UpdateFlag.FACE_ENTITY)) {
			appendFaceEntity(npc, buffer);
		}
		if(flags.get(UpdateFlag.FORCED_CHAT)) {
			buffer.putRS2String(npc.getUpdateFlags().getForcedMessage());
		}
		if(flags.get(UpdateFlag.HIT_2)) {
			buffer.writeByteC(npc.getUpdateFlags().secondary.getDamage());
			buffer.writeByteS(npc.getUpdateFlags().secondary.getType().getId());
			buffer.writeByteS((byte) npc.getHitpoints());
			buffer.writeByteC(npc.getMaxHitpoints());
		}
		if (flags.get(UpdateFlag.TRANSFORM)) {
			appendTransformUpdate(npc, buffer);
		}
		if (flags.get(UpdateFlag.FACE_COORDINATE)) {
			appendSetFocusDestination(npc, buffer);
		}

	}

	/**
	 * Updates an npcs movement for a player
	 * 
	 * @param str
	 *            The {@link GameBuffer} to write data on
	 */
	private static void updateNPCMovement(NPC npc, GameBuffer str) {
		if (npc.direction == -1) {
			if(npc.getUpdateFlags().isUpdateRequired()) {
				str.writeBits(1, 1);
				str.writeBits(2, 0);
			} else {
				str.writeBits(1, 0);
			}
		} else {
			str.writeBits(1, 1);
			str.writeBits(2, 1);
			str.writeBits(3, npc.direction);
			if(npc.getUpdateFlags().isUpdateRequired()) {
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
		buffer.writeBits(1, npc.getUpdateFlags().isUpdateRequired() ? 1 : 0);
	}

	private static void appendFaceEntity(NPC npc, GameBuffer str) {
		str.writeShort(npc.entityFaceIndex);
	}

	private static void appendSetFocusDestination(NPC npc, GameBuffer str) {
		str.writeWordBigEndian(npc.faceTileX * 2 + 1);
		str.writeWordBigEndian(npc.faceTileY * 2 + 1);
	}
	
	private static void appendHitUpdate1(NPC npc, GameBuffer str) {
		str.putByteA(npc.getUpdateFlags().primary.getDamage());
		str.writeByteC(npc.getUpdateFlags().primary.getType().getId());
		str.putByteA(npc.getHitpoints());
		str.writeByte((byte) npc.getMaxHitpoints());
	}

	private static void appendTransformUpdate(NPC npc, GameBuffer str) {
		str.writeWordBigEndianA(npc.transformId);
	}

}

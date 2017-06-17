package com.model.game.character.player;

import com.model.Appearance;
import com.model.UpdateFlags;
import com.model.UpdateFlags.UpdateFlag;
import com.model.game.World;
import com.model.game.item.container.impl.equipment.EquipmentConstants;
import com.model.game.location.Location;
import com.model.net.network.rsa.GameBuffer;
import com.model.utility.Utility;

import java.util.Iterator;
import java.util.Optional;

/**
 * Handles all of the player updating needs
 * 
 * @author Mobster
 *
 */
public class PlayerUpdating {

	/**
	 * Gets an {@link Optional} by the name of the player
	 * 
	 * @param name
	 *            The name of the player
	 * @return
	 */
	public static Optional<Player> getPlayer(String name) {
		return World.getWorld().getPlayers().search(p -> p.getName().equalsIgnoreCase(name));
	}

	/**
	 * Gets a player by their username
	 * 
	 * @param name
	 *            The name of the player
	 * @return The player by their username
	 */
	public static Player getPlayers(String name) {
		return getPlayerByName(name);
	}

	/**
	 * Gets the total amount of players online
	 * 
	 * @return The total amount of players online
	 */
	public static int getPlayerCount() {
		return World.getWorld().getPlayers().size();
	}

	/**
	 * Gets the total amount of staff online
	 * 
	 * @return The total amount of staff online
	 */
	public static int getStaffCount() {
		int count = 0;
		for (Player player : World.getWorld().getPlayers()) {
			if (player != null && player.rights.isStaff()) {
				count++;
			}
		}
		return count;
	}

	/**
	 * Checks if a player is online
	 * 
	 * @param playerName
	 *            The name of the player
	 * @return If the player is online
	 */
	public static boolean isPlayerOn(String playerName) {
		return getPlayer(playerName).isPresent();
	}

	/**
	 * Updates a {@link Player} registered to the {@link World}
	 * 
	 * @param player
	 *            The {@link Player} to update the world for
	 * @param buffer
	 *            The buffer to write data too
	 */
	public static void updatePlayer(Player player, GameBuffer buffer) {
		
		//long startTime = System.currentTimeMillis();
		
		/*
		 * If the map region changed send the new one. We do this immediately as
		 * the client can begin loading it before the actual packet is received.
		 */
		if (player.isMapRegionChanging()) {
			player.getActionSender().sendMapRegionPacket();
		}

		/*
		 * The update block packet holds update blocks and is send after the
		 * main packet.
		 */
		GameBuffer updateBlock = new GameBuffer(new byte[5000]);

		/*
		 * Send our server update
		 */
		if (World.updateRunning && !World.updateAnnounced) {
			buffer.writeFrame(114);
			buffer.writeWordBigEndian(World.updateSeconds * 50 / 30);
		}
		
		/*
		 * The main packet is written in bits instead of bytes and holds
		 * information about the local list, players to add and remove, movement
		 * and which updates are required.
		 */
		buffer.putFrameVarShort(81);
		int start = buffer.offset;
		buffer.initBitAccess();

		/*
		 * Updates this player.
		 */
		updateThisPlayerMovement(player, buffer);
		updatePlayer(player, updateBlock, false, player, true);

		/*
		 * Write the current size of the player list.
		 */
		buffer.writeBits(8, player.getLocalPlayers().size());

		/*
		 * Iterate through the local player list.
		 */
		for (Iterator<Player> it$ = player.getLocalPlayers().iterator(); it$.hasNext();) {
			/*
			 * Get the next player.
			 */
			Player otherPlayer = it$.next();
			
			/*
			 * If the player should still be in our list.
			 */
			if (World.getWorld().getPlayers().contains(otherPlayer) && otherPlayer.isVisible() && otherPlayer.isActive() && !otherPlayer.isTeleporting() && player.withinDistance(otherPlayer)) {
				/*
				 * Update the movement.
				 */
				updatePlayerMovement(otherPlayer, buffer);
				
				/*
				 * Check if an update is required, and if so, send the update.
				 */
				updatePlayer(otherPlayer, updateBlock, false, otherPlayer, player.getFAI().hasIgnored(otherPlayer.usernameHash));
			} else {
				/*
				 * Otherwise, remove the player from the list.
				 */
				it$.remove();

				/*
				 * Tell the client to remove the player from the list.
				 */
				buffer.writeBits(1, 1);
				buffer.writeBits(2, 3);
			}
		}

		int amount = 0;

		/*
		 * Loop through online players and add close players and update them
		 */
		for (Player otherPlayer : World.getWorld().getPlayers()) {
			if (amount == 15) {
				break;
			}
			/*
			 * Check if there is room left in the local list.
			 */
			if (player.getLocalPlayers().size() >= 255) {
				/*
				 * There is no more room left in the local list. We cannot add
				 * more players, so we just ignore the extra ones. They will be
				 * added as other players get removed.
				 */
				break;
			}
			/*
			 * If they should not be added ignore them.
			 */
			if (otherPlayer == null || otherPlayer.equals(player) || !otherPlayer.isActive() || player.getLocalPlayers().contains(otherPlayer)
					|| !otherPlayer.isVisible()) {
				continue;
			}

			if (player.withinDistance(otherPlayer)) {
				/*
				 * Add the player to the local list if it is within distance.
				 */
				player.getLocalPlayers().add(otherPlayer);

				/*
				 * Add the player in the packet.
				 */
				addNewPlayer(player, otherPlayer, buffer);
				
				/*
				 * Update the player, forcing the appearance flag.
				 */
				updatePlayer(otherPlayer, updateBlock, true, otherPlayer, player.getFAI().hasIgnored(otherPlayer.usernameHash));
				amount++;
			}
		}

		if (updateBlock.offset > 0) {
			/*
			 * Write a magic id indicating an update block follows.
			 */
			buffer.writeBits(11, 2047);
			buffer.finishBitAccess();
			/*
			 * Add the update block at the end of this packet.
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
		
		//long endTime = System.currentTimeMillis() - startTime; 
		//System.out.println(endTime + " : " + World.getWorld().getPlayers().size());

	}

	/**
	 * Writes the players update block to the {@link GameBuffer}
	 * 
	 * @param player
	 *            The {@link Player}s update blocks to write to the buffer
	 * @param buffer
	 *            The {@link GameBuffer} to write data too
	 * @param forceAppearance
	 *            Forces the player to update appearance
	 */
	private static void updatePlayer(Player player, GameBuffer buffer, boolean forceAppearance, Player target, boolean noChat) {
		
		/*
		 * If no update is required and we don't have to force an appearance
		 * update, don't write anything.
		 */
		if (!player.getUpdateFlags().isUpdateRequired() && !forceAppearance) {
			return;
		}

		/*
		 * Calculate the bitmask.
		 */
		int updateMask = 0;
		final UpdateFlags flags = player.getUpdateFlags();
		boolean samePlayer = player.usernameHash == target.usernameHash;
		
		/*
		 * We can used the cached update block!
		 */
		if (player.getUpdateBlock() != null && !samePlayer && !forceAppearance && !noChat) {
			buffer.writeBytes(player.getUpdateBlock().buffer, player.getUpdateBlock().offset);
			return;
		}
		
		if (flags.get(UpdateFlag.FORCE_MOVEMENT)) {
			updateMask |= 0x400;
		}
		
		if (flags.get(UpdateFlag.GRAPHICS)) {
			updateMask |= 0x100;
		}
		
		if (flags.get(UpdateFlag.ANIMATION) && player.getCurrentAnimation() != null) {
			updateMask |= 0x8;
		}
		
		if (flags.get(UpdateFlag.FORCED_CHAT)) {
			updateMask |= 0x4;
		}
		if (flags.get(UpdateFlag.CHAT) && !noChat) {
			updateMask |= 0x80;
		}
		if(flags.get(UpdateFlag.FACE_ENTITY)) {
			updateMask |= 0x1;
		}
		if (flags.get(UpdateFlag.APPEARANCE) || forceAppearance) {
			updateMask |= 0x10;
		}
		if (flags.get(UpdateFlag.FACE_COORDINATE)) {
			updateMask |= 0x2;
		}
		
		if (flags.get(UpdateFlag.HIT)) {
			updateMask |= 0x20;
		}
		
		if (flags.get(UpdateFlag.HIT_2)) {
			updateMask |= 0x200;
		}

		GameBuffer updateBlock = new GameBuffer(new byte[500]);

		if (updateMask >= 0x100) {
			updateMask |= 0x40;
			updateBlock.writeByte(updateMask & 0xFF);
			updateBlock.writeByte(updateMask >> 8);
		} else {
			updateBlock.writeByte(updateMask);
		}

		// now writing the various update blocks itself - note that their order
		// crucial
		
		if (flags.get(UpdateFlag.FORCE_MOVEMENT)) {
			appendForceMovement(player, updateBlock);
		}
		
		if (flags.get(UpdateFlag.GRAPHICS)) {
			appendGraphicsUpdate(player, updateBlock);
		}
		if (flags.get(UpdateFlag.ANIMATION)) {
			appendAnimationUpdate(player, updateBlock);
		}
		if (flags.get(UpdateFlag.FORCED_CHAT)) {
			updateBlock.putRS2String(player.getUpdateFlags().getForcedMessage());
		}
		if (flags.get(UpdateFlag.CHAT) && !noChat) {
			appendPlayerChatText(player, updateBlock);
		}
		if(flags.get(UpdateFlag.FACE_ENTITY)) {
			appendFaceUpdate(player, updateBlock);
		}
		if (flags.get(UpdateFlag.APPEARANCE) || forceAppearance) {
			appendPlayerAppearanceUpdate(player, updateBlock);
		}
		if (flags.get(UpdateFlag.FACE_COORDINATE)) {
			appendSetFocusDestination(player, updateBlock);
		}
		if (flags.get(UpdateFlag.HIT)) {
			appendHitUpdate(player, updateBlock);
		}
		if (flags.get(UpdateFlag.HIT_2)) {
			appendHitUpdate2(player, updateBlock);
		}
		if (!samePlayer && !forceAppearance && !noChat) {
			player.setUpdateBlock(updateBlock);
		}
		buffer.writeBytes(updateBlock.buffer, updateBlock.offset, 0);
	}

	/**
	 * Adds a new player to the game world for the player
	 * 
	 * @param myPlayer
	 *            Your player thats having players added too
	 * @param otherPlayer
	 *            The other player your player is adding
	 * @param buffer
	 *            The {@link GameBuffer} to write data too
	 */
	private static void addNewPlayer(Player myPlayer, Player otherPlayer, GameBuffer buffer) {
		/*
		 * Write the player index.
		 */
		buffer.writeBits(11, otherPlayer.getIndex());
		
		/*
		 * Write two flags here: the first indicates an update is required (this
		 * is always true as we add the appearance after adding a player) and
		 * the second to indicate we should discard client-side walk queues.
		 */
		buffer.writeBits(true);
		buffer.writeBits(true);
		
		/*
		 * Calculate the x and y offsets.
		 */
		int yPos = otherPlayer.getY() - myPlayer.getY();
		int xPos = otherPlayer.getX() - myPlayer.getX();
		
		/*
		 * Write the x and y offsets.
		 */
		buffer.writeBits(5, yPos);
		buffer.writeBits(5, xPos);
	}

	/**
	 * Updates this players movement
	 * 
	 * @param player
	 *            The {@link Player} to update the movement for
	 * @param buffer
	 *            The {@link GameBuffer} to write the data on
	 */
	private static void updateThisPlayerMovement(Player player, GameBuffer buffer) {
		/*
		 * Check if the player is teleporting.
		 */
		if (player.isTeleporting()/* || player.isMapRegionChanging()*/) {
			/*
			 * They are, so an update is required.
			 */
			buffer.writeBits(true);
			/*
			 * This value indicates the player teleported.
			 */
			buffer.writeBits(2, 3);
			/*
			 * This is the new player height.
			 */
			buffer.writeBits(2, player.getZ());
			/*
			 * This flag indicates if an update block is appended.
			 */
			buffer.writeBits(true);
			buffer.writeBits(1, player.getUpdateFlags().isUpdateRequired() ? 1 : 0);
			/*
			 * Direction moved this cycle in x/y axis
			 */
			buffer.writeBits(7, player.teleLocalY);
			buffer.writeBits(7, player.teleLocalX);
			System.out.println("GPI "+player.teleLocalX+","+player.teleLocalY);
		} else {
			/*
			 * Otherwise, check if the player moved.
			 */
			if (player.getMovementHandler().getWalkingDirection() == -1) {
				/*
				 * The player didn't move. Check if an update is required.
				 */
				if (player.getUpdateFlags().isUpdateRequired()) {
					/*
					 * Signifies an update is required.
					 */
					buffer.writeBits(true);
					/*
					 * But signifies that we didn't move.
					 */
					buffer.writeBits(2, 0);
				} else {
					/*
					 * Signifies that nothing changed.
					 */
					buffer.writeBits(1, 0);
				}
			} else {
				buffer.writeBits(true);
				/*
				 * Check if the player was running.
				 */
				if (player.getMovementHandler().getRunningDirection() == -1) {
					/*
					 * This indicates the player only walked.
					 */
					buffer.writeBits(2, 1);
					/*
					 * This is the player's walking direction.
					 */
					buffer.writeBits(3, player.getMovementHandler().getWalkingDirection());
					/*
					 * This flag indicates an update block is appended.
					 */
					if (player.getUpdateFlags().isUpdateRequired()) {
						buffer.writeBits(true);
					} else {
						buffer.writeBits(1, 0);
					}
				} else {
					/*
					 * This indicates the player ran.
					 */
					buffer.writeBits(2, 2);
					/*
					 * This is the walking direction.
					 */
					buffer.writeBits(3, player.getMovementHandler().getWalkingDirection());
					/*
					 * And this is the running direction.
					 */
					buffer.writeBits(3, player.getMovementHandler().getRunningDirection());
					/*
					 * This flag indicates an update block is appended.
					 */
					if (player.getUpdateFlags().isUpdateRequired()) {
						buffer.writeBits(true);
					} else {
						buffer.writeBits(1, 0);
					}
				}
			}
		}
	}

	/**
	 * Updates another players movement
	 * 
	 * @param player
	 *            The {@link Player} to update movement for
	 * @param str
	 *            The {@link GameBuffer} To write data on
	 */
	private static void updatePlayerMovement(Player player, GameBuffer str) {
		if (player.getMovementHandler().getWalkingDirection() == -1) {
			if (player.getUpdateFlags().isUpdateRequired()) {
				str.writeBits(1, 1);
				str.writeBits(2, 0);
			} else {
				str.writeBits(1, 0);
			}
		} else if (player.getMovementHandler().getRunningDirection() == -1) {
			str.writeBits(1, 1);
			str.writeBits(2, 1);
			str.writeBits(3, player.getMovementHandler().getWalkingDirection());
			str.writeBits(1, player.getUpdateFlags().isUpdateRequired() ? 1 : 0);
		} else {
			str.writeBits(1, 1);
			str.writeBits(2, 2);
			str.writeBits(3, player.getMovementHandler().getWalkingDirection());
			str.writeBits(3, player.getMovementHandler().getRunningDirection());
			str.writeBits(1, player.getUpdateFlags().isUpdateRequired() ? 1 : 0);
		}
	}

	/**
	 * Updates the graphics mask
	 * 
	 * @param player
	 *            The {@link Player} to update the mask for
	 * @param str
	 *            The {@link GameBuffer} to write data on
	 */
	private static void appendGraphicsUpdate(Player player, GameBuffer str) {
		str.writeWordBigEndian(player.getCurrentGraphic().getId());
		str.putInt(player.getCurrentGraphic().getDelay() + (65536 * player.getCurrentGraphic().getHeight()));
	}

	/**
	 * Updates the public chat mask
	 * 
	 * @param player
	 *            The {@link Player} to update the mask for
	 * @param str
	 *            The {@link GameBuffer} to write data on
	 */
	private static void appendPlayerChatText(Player player, GameBuffer str) {
		str.writeWordBigEndian(((player.getChatTextColor() & 0xFF) << 8) + (player.getChatTextEffects() & 0xFF));
		str.writeByte(player.getRights().getValue());
		str.writeByteC(player.getChatTextSize());
		str.writeBytes_reverse(player.getChatText(), player.getChatTextSize(), 0);
	}

	/**
	 * Updates the players appearance mask
	 * 
	 * @param player
	 *            The {@link Player} to update the mask for
	 * @param str
	 *            The {@link GameBuffer} to write data on
	 */
	private static void appendPlayerAppearanceUpdate(Player player, GameBuffer str) {
		player.getPlayerProps().offset = 0;
		
		Appearance app = player.getAppearance();
		
		//Update the appearance gender, 0 = male, 1 = female
		player.getPlayerProps().writeByte(app.getGender());
		
		//Update the head icon
		player.getPlayerProps().writeByte(player.getPrayerIcon());
		
		//Update the PK skull
		player.getPlayerProps().writeByte(player.isSkulled() ? player.getSkullType().getId() : -1);
		
		//Update the player infection 0 = healthy, 1 poison and 2 is venomed
		player.getPlayerProps().writeByte(player.infection);
		
		// Update Equipment
		if (!player.isPlayerTransformed()) {
			// helm or hat
			if (player.getEquipment().get(EquipmentConstants.HELM_SLOT) != null) {
				player.getPlayerProps().writeShort(0x200 + player.getEquipment().get(EquipmentConstants.HELM_SLOT).getId());
			} else {
				player.getPlayerProps().writeByte(0);
			}

			// cape
			if (player.getEquipment().get(EquipmentConstants.CAPE_SLOT) != null) {
				player.getPlayerProps().writeShort(0x200 + player.getEquipment().get(EquipmentConstants.CAPE_SLOT).getId());
			} else {
				player.getPlayerProps().writeByte(0);
			}

			// amulet
			if (player.getEquipment().get(EquipmentConstants.NECKLACE_SLOT) != null) {
				player.getPlayerProps().writeShort(0x200 + player.getEquipment().get(EquipmentConstants.NECKLACE_SLOT).getId());
			} else {
				player.getPlayerProps().writeByte(0);
			}

			// weapon
			if (player.getEquipment().get(EquipmentConstants.WEAPON_SLOT) != null) {
				player.getPlayerProps().writeShort(0x200 + player.getEquipment().get(EquipmentConstants.WEAPON_SLOT).getId());
			} else {
				player.getPlayerProps().writeByte(0);
			}

			// torso
			if (player.getEquipment().get(EquipmentConstants.TORSO_SLOT) != null) {
				player.getPlayerProps().writeShort(0x200 + player.getEquipment().get(EquipmentConstants.TORSO_SLOT).getId());
			} else {
				player.getPlayerProps().writeShort(0x100 + player.getAppearance().getChest());
			}

			// shield
			if (player.getEquipment().get(EquipmentConstants.SHIELD_SLOT) != null) {
				player.getPlayerProps().writeShort(0x200 + player.getEquipment().get(EquipmentConstants.SHIELD_SLOT).getId());
			} else {
				player.getPlayerProps().writeByte(0);
			}

			// full body
			if (player.getEquipment().get(EquipmentConstants.TORSO_SLOT) != null
					&& !EquipmentConstants.hasBody(player.getEquipment().get(EquipmentConstants.TORSO_SLOT).getId())) {
				player.getPlayerProps().writeByte(0);
			} else {
				player.getPlayerProps().writeShort(0x100 + player.getAppearance().getArms());
			}

			// legs
			if (player.getEquipment().get(EquipmentConstants.LEGS_SLOT) != null) {
				player.getPlayerProps().writeShort(0x200 + player.getEquipment().get(EquipmentConstants.LEGS_SLOT).getId());
			} else {
				player.getPlayerProps().writeShort(0x100 + player.getAppearance().getLegs());
			}

			// full helm
			if (player.getEquipment().get(EquipmentConstants.HELM_SLOT) != null
					&& EquipmentConstants.hasHead(player.getEquipment().get(EquipmentConstants.HELM_SLOT).getId())) {
				player.getPlayerProps().writeByte(0);
			} else {
				player.getPlayerProps().writeShort(0x100 + player.getAppearance().getHead());
			}

			// hands
			if (player.getEquipment().get(EquipmentConstants.GLOVES_SLOT) != null) {
				player.getPlayerProps().writeShort(0x200 + player.getEquipment().get(EquipmentConstants.GLOVES_SLOT).getId());
			} else {
				player.getPlayerProps().writeShort(0x100 + player.getAppearance().getHands());
			}

			// feet
			if (player.getEquipment().get(EquipmentConstants.BOOTS_SLOT) != null) {
				player.getPlayerProps().writeShort(0x200 + player.getEquipment().get(EquipmentConstants.BOOTS_SLOT).getId());
			} else {
				player.getPlayerProps().writeShort(0x100 + player.getAppearance().getFeet());
			}

			// full helm
			if (player.getAppearance().getGender() == 1) {
				player.getPlayerProps().writeByte(0);
			} else if (player.getEquipment().get(EquipmentConstants.HELM_SLOT) != null
					&& !EquipmentConstants.hasJaw(player.getEquipment().get(EquipmentConstants.HELM_SLOT).getId())) {
				player.getPlayerProps().writeByte(0);
			} else {
				player.getPlayerProps().writeShort(0x100 + player.getAppearance().getBeard());
			}

		} else {
			player.getPlayerProps().writeShort(-1);// Tells client that were
													// being a npc
			player.getPlayerProps().writeShort(player.getPnpc());// send NpcID
		}
		
		//Update appearance
		player.getPlayerProps().writeByte(app.getHairColour());
		player.getPlayerProps().writeByte(app.getTorsoColour());
		player.getPlayerProps().writeByte(app.getLegColour());
		player.getPlayerProps().writeByte(app.getFeetColour());
		player.getPlayerProps().writeByte(app.getSkinColour());
		
		//Update character animations
		player.getPlayerProps().writeShort((short) player.getStandAnimation()); // stand
		player.getPlayerProps().writeShort((short) player.getStandTurnAnimation()); // stand turn
		player.getPlayerProps().writeShort((short) player.getWalkAnimation()); // walk
		player.getPlayerProps().writeShort((short) player.getTurn180Animation()); // turn 180
		player.getPlayerProps().writeShort((short) player.getTurn90ClockwiseAnimation()); // turn 90 cw
		player.getPlayerProps().writeShort((short) player.getTurn90CounterClockwiseAnimation()); // turn 90 ccw
		player.getPlayerProps().writeShort((short) player.getRunAnimation()); // run
		
		//Sent the username to the client
		player.getPlayerProps().putLong(Utility.playerNameToInt64(player.getName()));
		
		//Custom title
		StringBuilder sb = new StringBuilder(player.getCurrentTitle());
		if (player.getCurrentTitle().equalsIgnoreCase("None")) {
			sb.delete(0, sb.length());
		}
		player.getPlayerProps().putRS2String(sb.toString());
		
		//Custom title color
		sb = new StringBuilder(player.getCurrentTitleColor());
		if (player.getCurrentTitle().equalsIgnoreCase("None")) {
			sb.delete(0, sb.length());
		}
		player.getPlayerProps().putRS2String(sb.toString());
		
		//Sent the combat level to the client
		player.getPlayerProps().writeByte((byte) player.getSkills().getCombatLevel());
		
		//Update the player rights
		player.getPlayerProps().writeByte(player.rights.getValue());
		
		//And lastly update the appearanceHash
		str.writeByteC(player.getPlayerProps().offset);
		str.writeBytes(player.getPlayerProps().buffer, player.getPlayerProps().offset, 0);
	}

	/**
	 * Updates the face entity mask
	 * 
	 * @param player
	 *            The {@link Player} to update the mask for
	 * @param str
	 *            The {@link GameBuffer} to write data on
	 */
	private static void appendFaceUpdate(Player player, GameBuffer str) {
		str.writeWordBigEndian(player.entityFaceIndex);
	}

	/**
	 * Updates the animation mask
	 * 
	 * @param player
	 *            The {@link Player} to update the mask for
	 * @param str
	 *            The {@link GameBuffer} to write data on
	 */
	private static void appendAnimationUpdate(Player player, GameBuffer str) {
        str.writeWordBigEndian((player.getCurrentAnimation().getId() == -1) ? 65535 : player.getCurrentAnimation().getId());
        str.writeByteC(player.getCurrentAnimation().getDelay());
	}

	/**
	 * Updates the face position mask
	 * 
	 * @param player
	 *            The {@link Player} to update the mask for
	 * @param str
	 *            The {@link GameBuffer} to write data on
	 */
	private static void appendSetFocusDestination(Player player, GameBuffer str) {
		str.writeWordBigEndianA(player.faceTileX);
		str.writeWordBigEndian(player.faceTileY);
	}

	/**
	 * Updates the first hit mask
	 * 
	 * @param player
	 *            The {@link Player} to update the mask for
	 * @param updateBlock
	 *            The {@link GameBuffer} to write data on
	 */
	private static void appendHitUpdate(Player player, GameBuffer updateBlock) {
		updateBlock.writeByte(player.getUpdateFlags().primary.getDamage());
		updateBlock.putByteA(player.getUpdateFlags().primary.getType().getId());
		updateBlock.writeByteC(player.getSkills().getLevel(3));
		updateBlock.writeByte(player.getSkills().getLevelForExperience(3));
	}

	/**
	 * Updates the second hit mask
	 * 
	 * @param player
	 *            The {@link Player} to update the mask for
	 * @param updateBlock
	 *            The {@link GameBuffer} to write data on
	 */
	private static void appendHitUpdate2(Player player, GameBuffer updateBlock) {
		updateBlock.writeByte(player.getUpdateFlags().secondary.getDamage());
		updateBlock.writeByteS(player.getUpdateFlags().secondary.getType().getId());
		updateBlock.writeByte(player.getSkills().getLevel(3));
		updateBlock.writeByteC(player.getSkills().getLevelForExperience(3));
	}
	
	private static void appendForceMovement(Player player, final GameBuffer packet) {
		Location myLocation = player.getLastKnownRegion();
		Location location = player.getLocation();
		packet.writeByteS((byte) (location.getLocalX(myLocation) + player.getForceWalk()[0]));
		packet.writeByteS((byte) (location.getLocalY(myLocation) + player.getForceWalk()[1]));
		packet.writeByteS((byte) (location.getLocalX(myLocation) + player.getForceWalk()[2]));
		packet.writeByteS((byte) (location.getLocalY(myLocation) + player.getForceWalk()[3]));
																				
		packet.writeWordBigEndianA(player.getForceWalk()[4]);
		packet.writeWordA(player.getForceWalk()[5]);
		packet.writeByteS((byte) player.getForceWalk()[6]);
		
	}

	public static void sendMessageToStaff(String message) {
		for (Player player : World.getWorld().getPlayers()) {
			if (player != null) {
				if (player.getRights().getValue() > 0 && player.getRights().getValue() < 3) {
					Player client = player;
					client.getActionSender().sendMessage("<col=255>[STAFF MESSAGE] " + message + "</col>");
				}
			}
		}
	}

	/**
	 * Announces a message to all online players
	 * 
	 * @param message
	 */
	public static void executeGlobalMessage(String message) {
		World.getWorld().getPlayers().forEach(p -> p.getActionSender().sendMessage(message));
	}

	/**
	 * Gets a player by their username
	 * 
	 * @param playerName
	 *            The name of the player to look for
	 * @return
	 */
	public static Player getPlayerByName(String playerName) {
		return World.getWorld().getPlayers().search(p -> p.getName().equalsIgnoreCase(playerName)).orElse(null);
	}
}
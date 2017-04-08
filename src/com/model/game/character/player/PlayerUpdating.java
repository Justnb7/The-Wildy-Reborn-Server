package com.model.game.character.player;

import java.util.Iterator;
import java.util.Optional;

import com.model.UpdateFlags;
import com.model.UpdateFlags.UpdateFlag;
import com.model.game.World;
import com.model.net.network.rsa.GameBuffer;
import com.model.utility.Utility;
import com.model.utility.json.definitions.ItemDefinition;

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

	public static Optional<Player> getPlayer2(long name) {
		return World.getWorld().getPlayers().search(p -> p.usernameHash == name);
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
			player.getOutStream().writeFrame(73);
			player.getOutStream().writeWordA(player.mapRegionX + 6);
			player.getOutStream().writeShort(player.mapRegionY + 6);
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
		appendPlayerUpdateBlock(player, updateBlock, false, player, true);

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
				appendPlayerUpdateBlock(otherPlayer, updateBlock, false, otherPlayer, player.getFAI().hasIgnored(otherPlayer.usernameHash));
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
				appendPlayerUpdateBlock(otherPlayer, updateBlock, true, otherPlayer, player.getFAI().hasIgnored(otherPlayer.usernameHash));
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
	 * @param samePlayer
	 *            Don't update if its the same player
	 */
	private static void appendPlayerUpdateBlock(Player player, GameBuffer buffer, boolean forceAppearance, Player target, boolean noChat) {
		if (!player.getUpdateFlags().isUpdateRequired() && !forceAppearance) {
			return;
		}

		/*
		 * Calculate the bitmask.
		 */
		int updateMask = 0;
		final UpdateFlags flags = player.getUpdateFlags();
		boolean samePlayer = player.usernameHash == target.usernameHash;
		if (player.getUpdateBlock() != null && !samePlayer && !forceAppearance && !noChat) {
			buffer.writeBytes(player.getUpdateBlock().buffer, player.getUpdateBlock().offset);
			return;
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
		buffer.writeBits(11, otherPlayer.getIndex());
		buffer.writeBits(true);
		buffer.writeBits(true);
		int yPos = otherPlayer.getY() - myPlayer.getY();
		int xPos = otherPlayer.getX() - myPlayer.getX();
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
		if (player.isTeleporting() || player.isMapRegionChanging()) {
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
			buffer.writeBits(2, player.heightLevel);
			/*
			 * This flag indicates if an update block is appended.
			 */
			buffer.writeBits(true);
			buffer.writeBits(1, player.getUpdateFlags().isUpdateRequired() ? 1 : 0);
			/*
			 * These are the positions.
			 */
			buffer.writeBits(7, player.currentY);
			buffer.writeBits(7, player.currentX);
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
		player.getPlayerProps().writeByte(player.playerAppearance[0]); // gender
		player.getPlayerProps().writeByte(player.getPrayerIcon());
		player.getPlayerProps().writeByte(player.infection);
		player.getPlayerProps().writeByte(player.skullIcon);
		player.getPlayerProps().writeByte(player.getRights().getValue());
		StringBuilder sb = new StringBuilder(player.getCurrentTitle());
		if (player.getCurrentTitle().equalsIgnoreCase("None")) {
			sb.delete(0, sb.length());
		}
		player.getPlayerProps().putRS2String(sb.toString());
		sb = new StringBuilder(player.getCurrentTitleColor());
		if (player.getCurrentTitle().equalsIgnoreCase("None")) {
			sb.delete(0, sb.length());
		}
		player.getPlayerProps().putRS2String(sb.toString());
		//get ur client up
		if (!player.isPlayerTransformed()) {
			if (player.playerEquipment[player.getEquipment().getHelmetId()] > 1) {
				player.getPlayerProps().writeShort(0x200 + player.playerEquipment[player.getEquipment().getHelmetId()]);
			} else {
				player.getPlayerProps().writeByte(0);
			}

			if (player.playerEquipment[player.getEquipment().getCapeId()] > 1) {
				player.getPlayerProps().writeShort(0x200 + player.playerEquipment[player.getEquipment().getCapeId()]);
			} else {
				player.getPlayerProps().writeByte(0);
			}

			if (player.playerEquipment[player.getEquipment().getAmuletId()] > 1) {
				player.getPlayerProps().writeShort(0x200 + player.playerEquipment[player.getEquipment().getAmuletId()]);
			} else {
				player.getPlayerProps().writeByte(0);
			}

			if (player.playerEquipment[player.getEquipment().getWeaponId()] > 1) {
				player.getPlayerProps().writeShort(0x200 + player.playerEquipment[player.getEquipment().getWeaponId()]);
			} else {
				player.getPlayerProps().writeByte(0);
			}

			if (player.playerEquipment[player.getEquipment().getChestId()] > 1) {
				player.getPlayerProps().writeShort(0x200 + player.playerEquipment[player.getEquipment().getChestId()]);
			} else {
				player.getPlayerProps().writeShort(0x100 + player.playerAppearance[2]);
			}

			if (player.playerEquipment[player.getEquipment().getShieldId()] > 1) {
				player.getPlayerProps().writeShort(0x200 + player.playerEquipment[player.getEquipment().getShieldId()]);
			} else {
				player.getPlayerProps().writeByte(0);
			}

			if (!ItemDefinition.forId(player.playerEquipment[player.getEquipment().getChestId()]).isPlatebody()) {
				player.getPlayerProps().writeShort(0x100 + player.playerAppearance[3]);
			} else {
				player.getPlayerProps().writeByte(0);
			}

			if (player.playerEquipment[player.getEquipment().getLegsId()] > 1) {
				player.getPlayerProps().writeShort(0x200 + player.playerEquipment[player.getEquipment().getLegsId()]);
			} else {
				player.getPlayerProps().writeShort(0x100 + player.playerAppearance[5]);
			}

			ItemDefinition def = ItemDefinition.forId(player.playerEquipment[player.getEquipment().getHelmetId()]);
			if (!def.isFullMask() && !def.isFullHelm()) {
				player.getPlayerProps().writeShort(0x100 + player.playerAppearance[1]);
			} else {
				player.getPlayerProps().writeByte(0);
			}

			if (player.playerEquipment[player.getEquipment().getGlovesId()] > 1) {
				player.getPlayerProps().writeShort(0x200 + player.playerEquipment[player.getEquipment().getGlovesId()]);
			} else {
				player.getPlayerProps().writeShort(0x100 + player.playerAppearance[4]);
			}

			if (player.playerEquipment[player.getEquipment().getBootsId()] > 1) {
				player.getPlayerProps().writeShort(0x200 + player.playerEquipment[player.getEquipment().getBootsId()]);
			} else {
				player.getPlayerProps().writeShort(0x100 + player.playerAppearance[6]);
			}

			if (!def.isFullMask() && player.playerAppearance[0] != 1) {
				player.getPlayerProps().writeShort(0x100 + player.playerAppearance[7]);
			} else {
				player.getPlayerProps().writeByte(0);
			}

		} else {
			player.getPlayerProps().writeShort(-1);// Tells client that were
													// being a npc
			player.getPlayerProps().writeShort(player.getPnpc());// send NpcID
		}
		player.getPlayerProps().writeByte(player.playerAppearance[8]);
		player.getPlayerProps().writeByte(player.playerAppearance[9]);
		player.getPlayerProps().writeByte(player.playerAppearance[10]);
		player.getPlayerProps().writeByte(player.playerAppearance[11]);
		player.getPlayerProps().writeByte(player.playerAppearance[12]);
		player.getPlayerProps().writeShort(player.getWeaponAnimation() == null || player.getWeaponAnimation().getStanding() == -1 ? 0x328 : player.getWeaponAnimation().getStanding());
		player.getPlayerProps().writeShort(player.getWeaponAnimation() == null || player.getWeaponAnimation().getStanding() == -1 ? 0x337 : player.getWeaponAnimation().getTurn());
		player.getPlayerProps().writeShort(player.getWeaponAnimation() == null || player.getWeaponAnimation().getWalking() == -1 ? 0x333 : player.getWeaponAnimation().getWalking());
		player.getPlayerProps().writeShort(player.getWeaponAnimation() == null || player.getWeaponAnimation().getWalking() == -1 ? 0x334 : player.getWeaponAnimation().turn180());
		player.getPlayerProps().writeShort(player.getWeaponAnimation() == null || player.getWeaponAnimation().getWalking() == -1 ? 0x335 : player.getWeaponAnimation().turn90CW());
		player.getPlayerProps().writeShort(player.getWeaponAnimation() == null || player.getWeaponAnimation().getWalking() == -1 ? 0x336 : player.getWeaponAnimation().turn90CCW());
		player.getPlayerProps().writeShort(player.getWeaponAnimation() == null || player.getWeaponAnimation().getRunning() == -1 ? 0x338 : player.getWeaponAnimation().getRunning());
		
		player.getPlayerProps().putRS2String(player.loyaltyTitle); // loyaltytitle
		
		player.getPlayerProps().putLong(Utility.playerNameToInt64(player.getName()));
		player.getPlayerProps().writeByte((byte) player.getSkills().getCombatLevel()); // combat level
		player.getPlayerProps().writeByte(player.rights.getValue());
		player.getPlayerProps().writeShort(0);
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
	 * @param str
	 *            The {@link GameBuffer} to write data on
	 */
	private static void appendHitUpdate(Player player, GameBuffer str) {
		if (player.getUpdateFlags().primary == null)
			return;
		str.writeByte(player.getUpdateFlags().primary.getDamage());
		str.putByteA(player.getUpdateFlags().primary.getType().getId());
		str.writeByteC(player.getSkills().getLevel(3));
		str.writeByte(player.getSkills().getLevelForExperience(3));
	}

	/**
	 * Updates the second hit mask
	 * 
	 * @param player
	 *            The {@link Player} to update the mask for
	 * @param str
	 *            The {@link GameBuffer} to write data on
	 */
	private static void appendHitUpdate2(Player player, GameBuffer str) {
		if (player.getUpdateFlags().secondary == null)
			return;
		str.writeByte(player.getUpdateFlags().secondary.getDamage());
		str.writeByteS(player.getUpdateFlags().secondary.getType().getId());
		str.writeByte(player.getSkills().getLevel(3));
		str.writeByteC(player.getSkills().getLevelForExperience(3));
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
package com.model.game.character.player;

import java.util.Iterator;
import java.util.Optional;

import com.model.game.World;
import com.model.game.character.player.packets.encode.impl.SendMessagePacket;
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
	 * Announces a message to all online players
	 * 
	 * @param message
	 */
	public static void executeGlobalMessage(String message) {
		World.getWorld().getPlayers().forEach(p -> p.write(new SendMessagePacket(message)));
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

		/*
		 * Reset the update block offset
		 */
		GameBuffer updateBlock = new GameBuffer(new byte[5000]);

		/*
		 * Send our server update
		 */
		if (World.updateRunning && !World.updateAnnounced) {
			buffer.writeFrame(114);
			buffer.writeWordBigEndian(World.updateSeconds * 50 / 30);
		}

		if (player.mapRegionDidChange) {
			player.getOutStream().writeFrame(73);
			player.getOutStream().writeWordA(player.mapRegionX + 6);
			player.getOutStream().writeShort(player.mapRegionY + 6);
		}

		buffer.putFrameVarShort(81);
		int start = buffer.offset;
		buffer.initBitAccess();

		/*
		 * Update our own player
		 */
		updateThisPlayerMovement(player, buffer);
		appendPlayerUpdateBlock(player, updateBlock, false, player, true);

		/*
		 * Write our local player size size
		 */
		buffer.writeBits(8, player.localPlayers.size());

		Iterator<Player> $it = player.localPlayers.iterator();
		/*
		 * Iterate through our local players and update them if we can, else
		 * remove them
		 */
		while ($it.hasNext()) {
			Player target = $it.next();
			if (target.isVisible() && target.isActive() && !player.didTeleport && !target.didTeleport
					&& player.withinDistance(target)) {
				updatePlayerMovement(target, buffer);
				appendPlayerUpdateBlock(target, updateBlock, false, target,
						player.getFAI().hasIgnored(target.usernameHash));
			} else {
				buffer.writeBits(1, 1);
				buffer.writeBits(2, 3);
				$it.remove();
			}
		}

		int amount = 0;

		/*
		 * Loop through online players and add close players and update them
		 */
		for (Player target : World.getWorld().getPlayers()) {
			if (amount == 15) {
				break;
			}
			if (player.localPlayers.size() >= 255) {
				break;
			}
			if (target == null || target.equals(player) || !target.isActive() || player.localPlayers.contains(target)
					|| !target.isVisible()) {
				continue;
			}

			if (player.withinDistance(target)) {
				player.localPlayers.add(target);
				addNewPlayer(player, target, buffer);
				appendPlayerUpdateBlock(target, updateBlock, true, target,
						player.getFAI().hasIgnored(target.usernameHash));
				amount++;
			}
		}

		if (updateBlock.offset > 0) {
			buffer.writeBits(11, 2047);
			buffer.finishBitAccess();
			buffer.writeBytes(updateBlock.buffer, updateBlock.offset, 0);
		} else {
			buffer.finishBitAccess();
		}

		buffer.putFrameSizeShort(start);

		player.flushOutStream();

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
	private static void appendPlayerUpdateBlock(Player player, GameBuffer buffer, boolean forceAppearance,
			Player target, boolean noChat) {
		if (!player.updateRequired && !forceAppearance) {
			return;
		}

		int updateMask = 0;
		boolean samePlayer = player.usernameHash == target.usernameHash;
		if (player.getUpdateBlock() != null && !samePlayer && !forceAppearance && !noChat) {
			buffer.writeBytes(player.getUpdateBlock().buffer, player.getUpdateBlock().offset);
			return;
		}
		if (player.gfxUpdateRequired) {
			updateMask |= 0x100;
		}
		if (player.animUpdateRequired) {
			updateMask |= 0x8;
		}
		if (player.forcedChatUpdateRequired) {
			updateMask |= 0x4;
		}
		if (player.isChatTextUpdateRequired() && !noChat) {
			updateMask |= 0x80;
		}
		if (player.isFaceUpdateRequired()) {
			updateMask |= 0x1;
		}
		if (player.isAppearanceUpdateRequired() || forceAppearance) {
			updateMask |= 0x10;
		}
		if (player.FocusPointX != -1) {
			updateMask |= 0x2;
		}
		if (player.isHitUpdateRequired()) {
			updateMask |= 0x20;
		}

		if (player.hitUpdateRequired2) {
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
		if (player.gfxUpdateRequired) {
			appendMask100Update(player, updateBlock);
		}
		if (player.animUpdateRequired) {
			appendAnimationRequest(player, updateBlock);
		}
		if (player.forcedChatUpdateRequired) {
			appendForcedChat(player, updateBlock);
		}
		if (player.isChatTextUpdateRequired() && !noChat) {
			appendPlayerChatText(player, updateBlock);
		}
		if (player.isFaceUpdateRequired()) {
			appendFaceUpdate(player, updateBlock);
		}
		if (player.isAppearanceUpdateRequired() || forceAppearance) {
			appendPlayerAppearance(player, updateBlock);
		}
		if (player.FocusPointX != -1) {
			appendSetFocusDestination(player, updateBlock);
		}
		if (player.isHitUpdateRequired()) {
			appendHitUpdate(player, updateBlock);
		}
		if (player.hitUpdateRequired2) {
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
	private static void updateThisPlayerMovement(Player player, GameBuffer buffer) {// valid

		if (player.didTeleport) {
			buffer.writeBits(true);
			buffer.writeBits(2, 3);
			buffer.writeBits(2, player.heightLevel);
			buffer.writeBits(true);
			buffer.writeBits(player.updateRequired);
			buffer.writeBits(7, player.currentY);
			buffer.writeBits(7, player.currentX);
		} else {
			if (player.getMovementHandler().getWalkingDirection() == -1) {
				if (player.updateRequired) {
					buffer.writeBits(true);
					buffer.writeBits(2, 0);
				} else {
					buffer.writeBits(1, 0);
				}
			} else {
				buffer.writeBits(true);
				if (player.getMovementHandler().getRunningDirection() == -1) {
					buffer.writeBits(2, 1);
					buffer.writeBits(3, player.getMovementHandler().getWalkingDirection());
					if (player.updateRequired) {
						buffer.writeBits(true);
					} else {
						buffer.writeBits(1, 0);
					}
				} else {
					buffer.writeBits(2, 2);
					buffer.writeBits(3, player.getMovementHandler().getWalkingDirection());
					buffer.writeBits(3, player.getMovementHandler().getRunningDirection());
					if (player.updateRequired) {
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
	private static void updatePlayerMovement(Player player, GameBuffer str) {// valid
		if (player.getMovementHandler().getWalkingDirection() == -1) {
			if (player.updateRequired || player.isChatTextUpdateRequired()) {

				str.writeBits(1, 1);
				str.writeBits(2, 0);
			} else {
				str.writeBits(1, 0);
			}
		} else if (player.getMovementHandler().getRunningDirection() == -1) {
			str.writeBits(1, 1);
			str.writeBits(2, 1);
			str.writeBits(3, player.getMovementHandler().getWalkingDirection());
			str.writeBits(1, (player.updateRequired || player.isChatTextUpdateRequired()) ? 1 : 0);
		} else {
			str.writeBits(1, 1);
			str.writeBits(2, 2);
			str.writeBits(3, player.getMovementHandler().getWalkingDirection());
			str.writeBits(3, player.getMovementHandler().getRunningDirection());
			str.writeBits(1, (player.updateRequired || player.isChatTextUpdateRequired()) ? 1 : 0);
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
	private static void appendMask100Update(Player player, GameBuffer str) {
		str.writeWordBigEndian(player.gfx.getId());
		str.putInt(player.gfx.getDelay() + (65536 * player.gfx.getHeight()));

	}

	/**
	 * Updates the forced chat mask
	 * 
	 * @param player
	 *            The {@link Player} to update the mask for
	 * @param str
	 *            The {@link GameBuffer} to write data on
	 */
	private static void appendForcedChat(Player player, GameBuffer str) {
		str.putRS2String(player.getForcedChatMessage());
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
	private static void appendPlayerAppearance(Player player, GameBuffer str) {
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
		player.getPlayerProps().writeShort(player.standTurnAnimation); // standAnimIndex
		player.getPlayerProps().writeShort(player.turnAnimation); // standTurnAnimIndex
																// er
		player.getPlayerProps().writeShort(player.walkAnimation); // walkAnimIndex
		player.getPlayerProps().writeShort(player.turn180Animation); // turn180AnimIndex
		player.getPlayerProps().writeShort(player.turn90ClockWiseAnimation); // turn90CWAnimIndex
		player.getPlayerProps().writeShort(player.turn90CounterClockWiseAnimation); // turn90CCWAnimIndex
		player.getPlayerProps().writeShort(player.runAnimation); // runAnimIndex
		
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
		str.writeWordBigEndian(player.face);
	}

	/**
	 * Updates the animation mask
	 * 
	 * @param player
	 *            The {@link Player} to update the mask for
	 * @param str
	 *            The {@link GameBuffer} to write data on
	 */
	private static void appendAnimationRequest(Player player, GameBuffer str) {
		str.writeWordBigEndian((player.anim.getId() == -1) ? 65535 : player.anim.getId());
		str.writeByteC(player.anim.getDelay());
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
		str.writeWordBigEndianA(player.FocusPointX);
		str.writeWordBigEndian(player.FocusPointY);
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
		if (player.primary == null)
			return;
		str.writeByte(player.primary.getDamage());
		str.putByteA(player.primary.getType().getId());
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
		if (player.secondary == null)
			return;
		str.writeByte(player.secondary.getDamage());
		str.writeByteS(player.secondary.getType().getId());
		str.writeByte(player.getSkills().getLevel(3));
		str.writeByteC(player.getSkills().getLevelForExperience(3));
	}

	public static void sendMessageToStaff(String message) {
		for (Player player : World.getWorld().getPlayers()) {
			if (player != null) {
				if (player.getRights().getValue() > 0 && player.getRights().getValue() < 3) {
					Player client = player;
					client.write(new SendMessagePacket("<col=255>[STAFF MESSAGE] " + message + "</col>"));
				}
			}
		}
	}

}
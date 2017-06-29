package com.venenatis.game.content;

import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.Rights;
import com.venenatis.game.net.packet.PacketType;
import com.venenatis.game.util.Utility;
import com.venenatis.game.util.logging.PlayerLogging;
import com.venenatis.game.util.logging.PlayerLogging.LogType;
import com.venenatis.game.world.World;

public class PrivateMessaging implements PacketType {

	public final int ADD_FRIEND = 188, SEND_PM = 126, REMOVE_FRIEND = 215, CHANGE_PM_STATUS = 95, REMOVE_IGNORE = 74, ADD_IGNORE = 133;

	@Override
	public void handle(final Player player, final int packetType, final int packetSize) {
		switch (packetType) {

		case ADD_FRIEND:
			final long friendToAdd = player.getInStream().readQWord();
			player.getFAI().addFriend(friendToAdd);
			break;
		case SEND_PM:
			final long usernameHash = player.getInStream().readQWord();
			final byte chatMessage[] = new byte[100];
			final int size = (byte) (packetSize - 8);
			byte[] data = chatMessage;
			player.getInStream().readBytes(chatMessage, size, 0);
			if (player.isMuted) {
	            player.getActionSender().sendMessage("You are muted for breaking a rule.");
				return;
			}
			/*
			 * logging the pm regardless of whether or not it was sent
			 */
			String message = Utility.textUnpack(data, size);
			PlayerLogging.write(LogType.PRIVATE_CHAT, player, "Recipient = " + Utility.longToPlayerName(usernameHash) + ", message = " + message);

			/*
			 * Fetch our player we are sending a message too
			 */
			Player target = World.getWorld().getPlayerByNameHash(usernameHash);

			boolean offline = false;

			/*
			 * The player is offline
			 */
			if (target == null) {
				offline = true;
			}
			
			if(player.isMuted) {
				player.getActionSender().sendMessage("You are muted and therefore cannot private message players");
				return;
			}

			/*
			 * Provide checks for non staff members
			 */
			if (!Rights.isPrivileged(player) && !offline) {
				if (target.getFAI().hasIgnored(player.usernameHash)) {
					offline = true;
				}
				if (target.privateChat == FriendAndIgnoreList.OFFLINE) {
					offline = true;
				}
				if (target.privateChat == FriendAndIgnoreList.FRIENDS_ONLY && !target.getFAI().hasFriend(player.usernameHash)) {
					offline = true;
				}
			}

			if (offline) {
				player.getActionSender().sendMessage("That player is currently offline.");
				return;
			}

			/*
			 * verify we have the person added
			 */
			if (player.getFAI().hasFriend(target.usernameHash) || (player.rights.getCrown() >= 1 && player.rights.getCrown() <= 5)) {
				target.getActionSender().sendPm(player.usernameHash, player.rights.getCrown(), chatMessage, size);
			}
			break;

		case REMOVE_FRIEND:
			final long friendToRemove = player.getInStream().readQWord();
			player.getFAI().removeFriend(friendToRemove);
			break;

		case REMOVE_IGNORE:
			final long ignore = player.getInStream().readQWord();
			player.getFAI().removeIgnore(ignore);
			break;

		case CHANGE_PM_STATUS:
			player.getInStream().readUnsignedByte();
			player.privateChat = player.getInStream().readUnsignedByte();
			player.getInStream().readUnsignedByte();
			for (Player p : World.getWorld().getPlayers()) {
				if (p != null) {
					/*
					 * Don't update ourself
					 */
					if (p.usernameHash == player.usernameHash)
						continue;

					/*
					 * Check if the person has us added and we aren't ignoring
					 * them.
					 */
					if (p.getFAI().hasFriend(player.usernameHash) && !player.getFAI().hasIgnored(p.usernameHash)) {
						int world = FriendAndIgnoreList.WORLD;

						/*
						 * If our rights are less then 3, check if the person is
						 * offline, check if our status is friends only and we
						 * don't have them added. If so send us as offline
						 */
						if (!Rights.isPrivileged(p)) {
							if ((player.privateChat == FriendAndIgnoreList.OFFLINE || (player.privateChat == FriendAndIgnoreList.FRIENDS_ONLY && !player.getFAI().hasFriend(p.usernameHash)))) {
								world = 0;
							}
						}
						/*
						 * Send our online status to the player
						 */
						p.getActionSender().sendFriend(player.usernameHash, world);
					}
				}
			}
			break;

		case ADD_IGNORE:
			final long ignoreAdd = player.getInStream().readQWord();
			player.getFAI().addIgnore(ignoreAdd);
			break;
		}
	}

}

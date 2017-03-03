package com.model.game.character.player.content;

import java.util.LinkedList;
import java.util.List;

import com.model.game.World;
import com.model.game.character.player.Player;
import com.model.game.character.player.content.clan.ClanManager;
import com.model.game.character.player.packets.out.SendFriendPacket;
import com.model.game.character.player.packets.out.SendMessagePacket;
import com.model.utility.Utility;

/**
 * Handles all of the friend and ignore list needs
 * 
 * @author Mobster
 *
 */
public class FriendAndIgnoreList {

	/**
	 * The players private messaging status is set to online
	 */
	public static final int ONLINE = 0;

	/**
	 * The players private messaging status is set to friends only
	 */
	public static final int FRIENDS_ONLY = 1;

	/**
	 * The players private messaging status is set to offline
	 */
	public static final int OFFLINE = 2;

	/**
	 * The world id we are currently in
	 */
	public static final int WORLD = 10;

	/**
	 * Our player instance for this class
	 */
	private final Player player;

	/**
	 * Constructs a new {@link FriendAndIgnoreList} instance
	 * 
	 * @param player
	 *            The {@link Player} who owns these lists
	 */
	public FriendAndIgnoreList(Player player) {
		this.player = player;
	}

	/**
	 * A {@link List} of friends
	 */
	private List<Long> friends = new LinkedList<>();

	/**
	 * A {@link List} of ignores
	 */
	private List<Long> ignores = new LinkedList<>();

	/**
	 * Adds a player to the friends list
	 * 
	 * @param username
	 *            The username hash of the player
	 */
	public void addFriend(Long username) {
		
		if (username == player.usernameHash) {
			player.write(new SendMessagePacket("You cannot add yourself."));
			return;
		}
		int size = player.rights.getValue() > 0 ? 200 : 100;
		if (getFriendsList().size() >= size) {
			return;
		}
		
		/*
		 * Check if our friends list already has the player
		 */
		if (hasFriend(username)) {
			player.write(new SendMessagePacket(Utility.longToPlayerName(username) + " is already on your friends list."));
			return;
		}

		/*
		 * Check if our ignores list already contains the player
		 */
		if (hasIgnored(username)) {
			player.write(new SendMessagePacket("Please remove from your ignore list before adding."));
			return;
		}

		/*
		 * Add the player to our list
		 */
		friends.add(username);
		
		/*
		 * Updates the clan's friends list.
		 */
		ClanManager.updateFriendsList(player);

		/*
		 * Find our target if hes online
		 */
		Player target = World.getWorld().getPlayerByNameHash(username);

		/*
		 * Set him as online if his status is online or he is friends with us
		 */
		
		if (target != null && target.isActive()) {
			if (target.privateChat == ONLINE || (target.privateChat == FRIENDS_ONLY && target.getFAI().hasFriend(player.usernameHash))) {
				player.write(new SendFriendPacket(username, WORLD));
			}
			if (target.getFAI().hasFriend(player.usernameHash)) {
				target.write(new SendFriendPacket(player.usernameHash, player.privateChat == OFFLINE ? 0 : WORLD));
			}
		}
	}

	/**
	 * Removes a friends username hash from your friends list
	 * 
	 * @param username
	 *            The username hash of the friend
	 */
	public void removeFriend(long username) {
		if (friends.remove(username)) {
			
			/*
			 * Updates the clan's friends list.
			 */
			ClanManager.updateFriendsList(player);
			
			Player target = World.getWorld().getPlayerByNameHash(username);
			if (target != null) {
				/*
				 * Since we are removing, check if we are online before sending our online status.
				 * If we aren't, let them know we're offline
				 */
				if (target.getFAI().hasFriend(player.usernameHash)) {
					target.write(new SendFriendPacket(player.usernameHash, player.privateChat == ONLINE ? WORLD : 0));
				}
			}
		}
	}

	/**
	 * Adds a username hash to the ignore list
	 * 
	 * @param username
	 *            The username hash to ignore
	 */
	public void addIgnore(long username) {
		
		if (username == player.usernameHash) {
			player.write(new SendMessagePacket("You cannot add yourself."));
			return;
		}
		
		if (getIgnoreList().size() >= 100) {
			player.write(new SendMessagePacket("You cannot ignore more then 100 people at a time."));
			return;
		}
		
		/*
		 * check if our ignore list contains the username first
		 */
		if (hasIgnored(username)) {
			player.write(new SendMessagePacket(Utility.longToPlayerName(username) + " is already on your ignore list."));
			return;
		}

		/*
		 * Check if our friends list contains the username first
		 */
		if (hasFriend(username)) {
			player.write(new SendMessagePacket("Please remove " + Utility.longToPlayerName(username) + " from  your friends list first."));
			return;
		}

		/*
		 * Add the username hash to our ignore list
		 */
		ignores.add(username);

		/*
		 * Find our target if hes online
		 */
		Player target = World.getWorld().getPlayerByNameHash(username);

		/*
		 * If hes online, tell him we're offline
		 */
		if (target != null && target.getFAI().hasFriend(player.usernameHash)) {
			target.write(new SendFriendPacket(player.usernameHash, 0));
		}
	}

	/**
	 * Removes a username hash from the ignore list
	 * 
	 * @param username
	 *            The username hash to remove from the ignore list
	 */
	public void removeIgnore(long username) {
		if (ignores.remove(username)) {
			Player target = World.getWorld().getPlayerByNameHash(username);
			if (target != null) {
				if (player.privateChat == 0 && target.getFAI().hasFriend(player.usernameHash)) {
					target.write(new SendFriendPacket(player.usernameHash, WORLD));
				}
			}
		}
	}

	/**
	 * Handles the updates on login
	 */
	public void handleLogin() {
		player.getPA().sendFriendServerStatus(2);
		sendIgnoreList();
		sendFriendsList();
		
		/*
		 * Updates all players online that we are now online
		 */
		for (Player target : World.getWorld().getPlayers()) {
			if (target != null) {
				/*
				 * Validate our target has us added, and we dont have him/her on our ignore list so we dont let them know we're online
				 */
				if (target.getFAI().hasFriend(player.usernameHash) && !player.getFAI().hasIgnored(target.usernameHash)) {
					target.write(new SendFriendPacket(player.usernameHash, WORLD));
				}
			}
		}
	}

	/**
	 * Sends the ignore list to the client
	 */
	public void sendIgnoreList() {
		player.getOutStream().putFrameVarShort(214);
		int offset = player.getOutStream().offset;
		for (Long usernameHash : ignores) {
			player.getOutStream().putLong(usernameHash);
		}
		player.getOutStream().putFrameSizeShort(offset);
		player.flushOutStream();
	}
	
	/**
	 * Sends the players friend list
	 */
	public void sendFriendsList() {
		/*
		 * Write all of our friends
		 */
		for (long hash : friends) {
			Player target = World.getWorld().getPlayerByNameHash(hash);
			/*
			 * Don't send them online if they are null, they have us ignored, they dont have us added and have friends only, or they have their private chat off
			 */
			boolean offline = (target == null || target.getFAI().hasIgnored(player.usernameHash) || target.privateChat == OFFLINE || (target.privateChat == FRIENDS_ONLY && !target.getFAI().hasFriend(player.usernameHash)));
			player.write(new SendFriendPacket(hash, offline ? 1 : WORLD));
		}
	}

	/**
	 * Checks if the player has the username hash added
	 * 
	 * @param usernameHash
	 *            The username has to check for
	 * @return If the player has the username hash added
	 */
	public boolean hasFriend(long usernameHash) {
		return friends.contains(usernameHash);
	}

	/**
	 * Checks if the player has the username hash ignored
	 * 
	 * @param usernameHash
	 *            THe username hash to check for
	 * @return If the player has the username hash ignored
	 */
	public boolean hasIgnored(long usernameHash) {
		return ignores.contains(usernameHash);
	}

	/**
	 * Gets the ignore list containing all of the ignored players user name
	 * hashes
	 * 
	 * @return A {@link List} of ignored users
	 */
	public List<Long> getIgnoreList() {
		return ignores;
	}

	/**
	 * Gets the friends list containing all of the friends user name hashes
	 * 
	 * @return A {@link List} of friends
	 */
	public List<Long> getFriendsList() {
		return friends;
	}

}

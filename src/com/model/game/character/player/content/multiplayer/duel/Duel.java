package com.model.game.character.player.content.multiplayer.duel;

import java.util.Arrays;
import java.util.Objects;

import com.model.Server;
import com.model.game.World;
import com.model.game.character.player.Player;
import com.model.game.character.player.content.multiplayer.Multiplayer;
import com.model.game.character.player.content.multiplayer.MultiplayerSessionStage;
import com.model.game.character.player.content.multiplayer.MultiplayerSessionType;



public class Duel extends Multiplayer {

	public Duel(Player player) {
		super(player);
	}

	@Override
	public boolean requestable(Player requested) {
		
		if (Server.getMultiplayerSessionListener().requestAvailable(requested, player, MultiplayerSessionType.DUEL) != null) {
			player.getActionSender().sendMessage("You have already sent a request to this player.");
			return false;
		}
		if (World.updateRunning) {
			player.getActionSender().sendMessage("You cannot request or accept a duel request at this time.");
			player.getActionSender().sendMessage("The server is currently being updated.");
			return false;
		}
		if (player.distanceToPoint(requested.getX(), requested.getY()) > 3) {
			player.getActionSender().sendMessage("You are not close enough to the other player to request or accept.");
			return false;
		}
		if (!player.getArea().inDuelArena()) {
			player.getActionSender().sendMessage("You must be in the duel arena area to do this.");
			return false;
		}
		if (!requested.getArea().inDuelArena()) {
			player.getActionSender().sendMessage("The challenger must be in the duel arena area to do this.");
			return false;
		}
		if (Server.getMultiplayerSessionListener().inAnySession(player)) {
			player.getActionSender().sendMessage("You cannot request a duel whilst in a session.");
			return false;
		}
		if (Server.getMultiplayerSessionListener().inAnySession(requested)) {
			player.getActionSender().sendMessage("This player is currently is a session with another player.");
			return false;
		}
		if (player.teleTimer > 0 || requested.teleTimer > 0) {
			player.getActionSender().sendMessage("You cannot request or accept whilst you, or the other player are teleporting.");
			return false;
		}
		return true;
	}

	@Override
	public void request(Player requested) {
		
		if (Objects.isNull(requested)) {
			player.getActionSender().sendMessage("The player cannot be found, try again shortly.");
			return;
		}
		if (Objects.equals(player, requested)) {
			player.getActionSender().sendMessage("You cannot trade yourself.");
			return;
		}
		player.faceEntity(requested);
		//System.out.println("lol");
		DuelSession session = (DuelSession) Server.getMultiplayerSessionListener().requestAvailable(player, requested, MultiplayerSessionType.DUEL);
		if (session != null) {
			session.getStage().setStage(MultiplayerSessionStage.OFFER_ITEMS);
			session.populatePresetItems();
			session.updateMainComponent();
			//session.sendDuelEquipment();
			Server.getMultiplayerSessionListener().removeOldRequests(player);
			Server.getMultiplayerSessionListener().removeOldRequests(requested);
			session.getStage().setAttachment(null);
		} else {
			session = new DuelSession(Arrays.asList(player, requested), MultiplayerSessionType.DUEL);
			if (Server.getMultiplayerSessionListener().appendable(session)) {
				player.getActionSender().sendMessage("Sending duel request...");
				requested.getActionSender().sendMessage(player.getName() + ":duelreq:");
				session.getStage().setAttachment(player);
				Server.getMultiplayerSessionListener().add(session);
			}
		}
	}

}

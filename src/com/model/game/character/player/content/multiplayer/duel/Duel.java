package com.model.game.character.player.content.multiplayer.duel;

import java.util.Arrays;
import java.util.Objects;

import com.model.Server;
import com.model.game.World;
import com.model.game.character.player.Player;
import com.model.game.character.player.content.multiplayer.Multiplayer;
import com.model.game.character.player.content.multiplayer.MultiplayerSessionStage;
import com.model.game.character.player.content.multiplayer.MultiplayerSessionType;
import com.model.game.character.player.packets.encode.impl.SendMessagePacket;



public class Duel extends Multiplayer {

	public Duel(Player player) {
		super(player);
	}

	@Override
	public boolean requestable(Player requested) {
		if (Server.getMultiplayerSessionListener().requestAvailable(requested, player, MultiplayerSessionType.DUEL) != null) {
			player.write(new SendMessagePacket("You have already sent a request to this player."));
			return false;
		}
		if (World.updateRunning) {
			player.write(new SendMessagePacket("You cannot request or accept a duel request at this time."));
			player.write(new SendMessagePacket("The server is currently being updated."));
			return false;
		}
		if (player.distanceToPoint(requested.getX(), requested.getY()) > 3) {
			player.write(new SendMessagePacket("You are not close enough to the other player to request or accept."));
			return false;
		}
		if (!player.getArea().inDuelArena()) {
			player.write(new SendMessagePacket("You must be in the duel arena area to do this."));
			return false;
		}
		if (!requested.getArea().inDuelArena()) {
			player.write(new SendMessagePacket("The challenger must be in the duel arena area to do this."));
			return false;
		}
		if (Server.getMultiplayerSessionListener().inAnySession(player)) {
			player.write(new SendMessagePacket("You cannot request a duel whilst in a session."));
			return false;
		}
		if (Server.getMultiplayerSessionListener().inAnySession(requested)) {
			player.write(new SendMessagePacket("This player is currently is a session with another player."));
			return false;
		}
		if (player.teleTimer > 0 || requested.teleTimer > 0) {
			player.write(new SendMessagePacket("You cannot request or accept whilst you, or the other player are teleporting."));
			return false;
		}
		return true;
	}

	@Override
	public void request(Player requested) {
		if (Objects.isNull(requested)) {
			player.write(new SendMessagePacket("The player cannot be found, try again shortly."));
			return;
		}
		if (Objects.equals(player, requested)) {
			player.write(new SendMessagePacket("You cannot trade yourself."));
			return;
		}
		player.faceUpdate(requested.getIndex());
		//System.out.println("lol");
		DuelSession session = (DuelSession) Server.getMultiplayerSessionListener().requestAvailable(player, requested, MultiplayerSessionType.DUEL);
		if (session != null) {
			session.getStage().setStage(MultiplayerSessionStage.OFFER_ITEMS);
			session.populatePresetItems();
			session.updateMainComponent();
			session.sendDuelEquipment();
			Server.getMultiplayerSessionListener().removeOldRequests(player);
			Server.getMultiplayerSessionListener().removeOldRequests(requested);
			session.getStage().setAttachment(null);
		} else {
			session = new DuelSession(Arrays.asList(player, requested), MultiplayerSessionType.DUEL);
			if (Server.getMultiplayerSessionListener().appendable(session)) {
				player.write(new SendMessagePacket("Sending duel request..."));
				requested.write(new SendMessagePacket(player.getName() + ":duelreq:"));
				session.getStage().setAttachment(player);
				Server.getMultiplayerSessionListener().add(session);
			}
		}
	}

}

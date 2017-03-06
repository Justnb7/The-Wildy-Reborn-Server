package com.model.game.character.player.packets.in;

import java.util.Objects;

import com.model.game.Constants;
import com.model.game.World;
import com.model.game.character.player.Boundary;
import com.model.game.character.player.Player;
import com.model.game.character.player.packets.PacketType;
import com.model.game.character.player.packets.out.SendMessagePacket;

/**
 * Challenging packet, for dueling.
 * @author Patrick van Elderen
 *
 */
public class ChallengePlayer implements PacketType {

	@Override
	public void handle(Player player, int packet, int size) {
		//System.out.println("Called packet "+packet+" size "+size);
		switch (packet) {
		
		case 128:
			int answerPlayer = player.getInStream().readUnsignedWord();

			if (answerPlayer >= Constants.MAX_PLAYERS || answerPlayer < 0) {
				//player.message("Bad player id.");
				return;
			}
			//player.debug("my pid "+player.getIndex()+" opp pid "+answerPlayer+" list copy size "+World.getWorld().getPlayers().size());

			// getPlayer constructs a new list
			if (answerPlayer >= Constants.MAX_PLAYERS || World.getWorld().getPlayers().get(answerPlayer) == null) {
				//player.message("Unable to find player.");
				return;
			}
			
			Player requested = World.getWorld().getPlayers().get(answerPlayer);
			
			if (Objects.isNull(requested)) {
				//player.message("Unable to find partner.");
				return;
			}
			
			//We can't sent a request when we're already dueling
			if (Boundary.isIn(player, Boundary.DUEL_ARENAS) || Boundary.isIn(requested, Boundary.DUEL_ARENAS)) {
				player.write(new SendMessagePacket("You cannot do this inside of the duel arena."));
				return;
			}
			
			//We passed all the checks we can go ahead and send the request
			if (player.getDuel().requestable(requested)) {
				player.getDuel().request(requested);
			}
			break;
		}
	}
}

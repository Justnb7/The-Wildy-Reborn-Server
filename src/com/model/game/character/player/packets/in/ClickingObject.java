package com.model.game.character.player.packets.in;

import com.model.Server;
import com.model.game.character.combat.Combat;
import com.model.game.character.player.Player;
import com.model.game.character.player.packets.PacketType;
import com.model.game.location.Position;
import com.model.utility.Utility;

/**
 * Click Object
 */
public class ClickingObject implements PacketType {

	public static final int FIRST_CLICK = 132, SECOND_CLICK = 252, THIRD_CLICK = 70;

	@Override
	public void handle(final Player player, int packetType, int packetSize) {

		player.clickObjectType = 0;
		player.objectX = 0;
		player.objectId = 0;
		player.objectY = 0;
		player.getPA().resetFollow();
		Combat.resetCombat(player);
		player.getPA().requestUpdates();
		
		switch (packetType) {
		case FIRST_CLICK:
			player.objectX = player.getInStream().readSignedWordBigEndianA();
			player.objectId = player.getInStream().readUnsignedWord();
			player.objectY = player.getInStream().readUnsignedWordA();
			player.objectDistance = 2;
			player.objectDistance = player.objectDistance < 1 ? 1 : player.objectDistance;
			/*if (player.objectId != 9357 && player.objectId != 409 && player.objectId != 10083 && player.objectId != 18258 && player.objectId != 13291 && player.objectId != 6552 && player.objectId != 26273 && player.objectId != 13641 && player.objectId != 4545 && player.objectId != 4546 && player.objectId != 4485 && player.objectId != 4413 && player.objectId != 11748 && player.objectId != 11834 && player.objectId != 11833) {
				if (!exists(player.objectId, player.objectX, player.objectY, player.heightLevel)) {
					player.write(new SendMessagePacket("Warning: The object could not be verified by the server. If you feel this is"));
					player.write(new SendMessagePacket("incorrect, please contact a staff member to have this resolved."));
					return;
				}
			}*/
			if (player.teleTimer > 0 || player.isTeleporting()) {
				return;
			}
			if (Math.abs(player.getX() - player.objectX) > 25 || Math.abs(player.getY() - player.objectY) > 25) {
				player.getMovementHandler().resetWalkingQueue();
				break;
			}
			switch (player.objectId) {
			
			case 26562:
				player.objectDistance = 2;
				break;
			
			case 26380:
			case 677:
			case 538:
				player.objectDistance = 9;
				break;
			
			case 13641:
				player.objectDistance = 2;
				break;
			
			case 24600:
				player.objectDistance = 2;
				break;
			
			case 16671:
				player.getPA().movePlayer(2840, 3539, 2);
				break;
				
			case 3044:
			case 21764:
			case 17010:
			case 2561:
			case 2562:
			case 2563:
			case 2564:
			case 2565:
				player.objectDistance = 6;
				break;
			case 11833:
			case 11834:
				player.objectDistance = 2;
				break;
			case 7407:
				if (player.absX == 3007 && player.absY == 3849) {
					 player.getPA().movePlayer1(3008, 3849);
				} else if (player.absX == 3008 && player.absY == 3849) {
						player.getPA().movePlayer1(3007, 3849);
					}
				break;
			case 7408:
				if (player.absX == 3007 && player.absY == 3850) {
					 player.getPA().movePlayer1(3008, 3850);
				} else if (player.absX == 3008 && player.absY == 3850) {
						player.getPA().movePlayer1(3007, 3850);
				}
				break;
			case 23566:
				player.getPA().movePlayer1(3120, 9970);
				break;
			case 5088:
				player.getPA().movePlayer1(2686, 9506);
				break;
			case 2114:
			case 8929:
			case 24009:
			case 14898:
			case 14907:
				player.objectDistance = 4;
				break;
			case 20720:
			case 20771:
			case 20772:
			case 11764:
			case 11775:
			case 11756:
			case 11762:
			case 1276:
			case 14897:
			case 14900:
			case 6552:
			case 14899:
			case 14901:
			case 2693:
			case 11755:
			case 23271:
				player.objectDistance = 2;
				break;
			case 20770:
			case 20672:
			case 20671:
			case 2484:
			case 2483:
			case 21725:
			case 2485:
			case 2488:
			case 4031:
			case 14903:
			case 14902:
			case 14904:
			case 11758:
			case 2119:
			case 2120:
				player.objectDistance = 3;
				break;
			}
			if (player.destinationReached()) {
				player.face(new Position(player.objectX, player.objectY));
				player.getActions().firstClickObject(player.objectId, player.objectX, player.objectY);
			} else {
				player.clickObjectType = 1;
			}
			break;

		case SECOND_CLICK:
			player.objectId = player.getInStream().readUnsignedWordBigEndianA();
			player.objectY = player.getInStream().readSignedWordBigEndian();
			player.objectX = player.getInStream().readUnsignedWordA();
			player.objectDistance = player.objectDistance < 1 ? 1 : player.objectDistance;
			/*if (player.objectId != 409 && player.objectId != 10083 && player.objectId != 18258 && player.objectId != 11744 && player.objectId != 13291 && player.objectId != 6552 && player.objectId != 26273 && player.objectId != 13641 && player.objectId != 4545 && player.objectId != 4546 && player.objectId != 4485 && player.objectId != 4413 && player.objectId != 11748 && player.objectId != 11834 && player.objectId != 11833) {
				if (!exists(player.objectId, player.objectX, player.objectY, player.heightLevel)) {
					player.write(new SendMessagePacket("Warning: The object could not be verified by the server. If you feel this is"));
					player.write(new SendMessagePacket("incorrect, please contact a staff member to have this resolved."));
					return;
				}
			}*/
			if (player.isTeleporting()) {
				return;
			}
			switch(player.objectId) {
			case 24009:
				player.objectDistance = 4;
				break;
			case 11730:
			case 11731:
			case 11732:
			case 11733:
			case 11734:
			case 14011:
				player.objectDistance = 2;
				break;
			}
			
			if (player.destinationReached()) {
				player.getActions().secondClickObject(player.objectId, player.objectX, player.objectY);
			} else {
				player.clickObjectType = 2;
			}
			break;

		case THIRD_CLICK:
			player.objectX = player.getInStream().readSignedWordBigEndian();
			player.objectY = player.getInStream().readUnsignedWord();
			player.objectId = player.getInStream().readUnsignedWordBigEndianA();
			player.objectDistance = player.objectDistance < 1 ? 1 : player.objectDistance;
			/*if (player.objectId != 409 && player.objectId != 10083 && player.objectId != 18258 && player.objectId != 11744 && player.objectId != 13291 && player.objectId != 6552 && player.objectId != 26273 && player.objectId != 13641 && player.objectId != 4545 && player.objectId != 4546 && player.objectId != 4485 && player.objectId != 4413 && player.objectId != 11748 && player.objectId != 11834 && player.objectId != 11833) {
				if (!exists(player.objectId, player.objectX, player.objectY, player.heightLevel)) {
					player.write(new SendMessagePacket("Warning: The object could not be verified by the server. If you feel this is"));
					player.write(new SendMessagePacket("incorrect, please contact a staff member to have this resolved."));
					return;
				}
			}*/
			if (player.inDebugMode()) {
				Utility.println("objectId: " + player.objectId + "  ObjectX: " + player.objectX + "  objectY: " + player.objectY + " Xoff: " + (player.getX() - player.objectX) + " Yoff: " + (player.getY() - player.objectY));
			}
			if (player.destinationReached()) {
				player.getActions().thirdClickObject(player.objectId, player.objectX, player.objectY);
			} else {
				player.clickObjectType = 3;
			}
			break;
		}

	}
	
	public boolean exists(int id, int x, int y, int h) {
		if (Server.getGlobalObjects().exists(id, x, y, h)) {
			return true;
		}
		return false;
	}
}

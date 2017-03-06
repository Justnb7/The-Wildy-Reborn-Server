package com.model.game.character.player.packets.in;

import com.model.Server;
import com.model.game.World;
import com.model.game.character.npc.Npc;
import com.model.game.character.player.Player;
import com.model.game.character.player.packets.PacketType;
import com.model.game.character.player.packets.out.SendMessagePacket;
import com.model.task.ScheduledTask;

/**
 * Click NPC
 */
public class NpcInteractionPacketHandler implements PacketType {
	public static final int ATTACK_NPC = 72, MAGE_NPC = 131, FIRST_CLICK = 155, SECOND_CLICK = 17, THIRD_CLICK = 21, FOURTH_CLICK = 18;

	@Override
	public void handle(final Player player, int packetType, int packetSize) {
		player.npcIndex = 0;
		player.npcClickIndex = 0;
		player.playerIndex = 0;
		player.clickNpcType = 0;
		player.getPA().resetFollow();
		if (player.isPlayerTransformed() || player.isTeleporting()) {
			return;
		}

		switch (packetType) {
		/**
		 * Attack npc melee or range
		 **/
		case ATTACK_NPC:
			player.npcIndex = player.getInStream().readUnsignedWordA();
			Npc npc = World.getWorld().getNpcs().get(player.npcIndex);
			if (npc == null) {
				break;
			}
			if (World.getWorld().getNpcs().get(player.npcIndex).maximumHealth == 0 && npc.npcId != 493) {
				player.npcIndex = 0;
				break;
			}
            if (!npc.getDefinition().isAttackable())
                return;
			if (player.autocastId > 0) {
				player.autoCast = true;
			}
			if (!player.autoCast && player.spellId > 0) {
				player.spellId = 0;
			}

			player.faceUpdate(player.npcIndex);
			player.usingMagic = false;
			boolean usingBow = player.getEquipment().isBow(player);
			boolean throwingWeapon = player.getEquipment().isThrowingWeapon(player);
			boolean usingCross = player.getEquipment().isCrossbow(player);
			
			if ((usingBow || usingCross || player.autoCast) && player.goodDistance(player.getX(), player.getY(), World.getWorld().getNpcs().get(player.npcIndex).getX(), World.getWorld().getNpcs().get(player.npcIndex).getY(), 7)) {
				player.stopMovement();
			}
			if (throwingWeapon && player.goodDistance(player.getX(), player.getY(), World.getWorld().getNpcs().get(player.npcIndex).getX(), World.getWorld().getNpcs().get(player.npcIndex).getY(), 4)) {
				player.stopMovement();
			}

			if (player.playerFollowIndex > 0) {
				player.getPA().resetFollow();
			}
			if (player.attackDelay <= 0) {
				player.getCombat().attackNpc(player.npcIndex);
				player.attackDelay++;
			}
			break;

		/**
		 * Attack npc with magic
		 **/
		case MAGE_NPC:
			player.npcIndex = player.getInStream().readSignedWordBigEndianA();
			int castingSpellId = player.getInStream().readSignedWordA();
			player.usingMagic = false;
            npc = World.getWorld().getNpcs().get(player.npcIndex);
			if (World.getWorld().getNpcs().get(player.npcIndex) == null) {
				return;
			}

            if (!npc.getDefinition().isAttackable())
                return;
			if (World.getWorld().getNpcs().get(player.npcIndex).maximumHealth == 0 || World.getWorld().getNpcs().get(player.npcIndex).npcId == 944) {
				player.write(new SendMessagePacket("You can't attack this npc."));
				break;
			}
			for (int i = 0; i < player.MAGIC_SPELLS.length; i++) {
				if (castingSpellId == player.MAGIC_SPELLS[i][0]) {
					player.setSpellId(i);
					player.usingMagic = true;
					break;
				}
			}
			
			if (player.autoCast) {
				player.autoCast = false;
			}
			if (player.usingMagic) {
				if (player.goodDistance(player.getX(), player.getY(), World.getWorld().getNpcs().get(player.npcIndex).getX(), World.getWorld().getNpcs().get(player.npcIndex).getY(), 6)) {
					player.stopMovement();
				}
				if (player.attackDelay <= 0) {
					player.getCombat().attackNpc(player.npcIndex);
					player.attackDelay++;
				}
			}
			break;

		case FIRST_CLICK:
			player.npcClickIndex = player.inStream.readSignedWordBigEndian();
			player.distance = 1;
			if (World.getWorld().getNpcs().get(player.npcClickIndex) == null) {
				return;
			}
			player.npcType = World.getWorld().getNpcs().get(player.npcClickIndex).npcId;
			if (World.getWorld().getNpcs().get(player.npcClickIndex) == null) {
				return;
			}
			
			switch (player.npcType) {
			case 394:
			case 306:
				player.distance = 3;
				break;
			}
			if (player.goodDistance(World.getWorld().getNpcs().get(player.npcClickIndex).getX(), World.getWorld().getNpcs().get(player.npcClickIndex).getY(), player.getX(), player.getY(), player.distance)) {
				player.turnPlayerTo(World.getWorld().getNpcs().get(player.npcClickIndex).getX(), World.getWorld().getNpcs().get(player.npcClickIndex).getY());
				World.getWorld().getNpcs().get(player.npcClickIndex).faceLocation(player.getX(), player.getY());
			   // NPCHandler.npcs[c.npcClickIndex].facePlayer(c.getIndex());
				player.getActions().firstClickNpc(World.getWorld().getNpcs().get(player.npcClickIndex));
			} else {
				player.clickNpcType = 1;
				Server.getTaskScheduler().schedule(new ScheduledTask(1) {
					@Override
					public void execute() {
						if (!player.isActive()) {
							stop();
							return;
						}
						if ((player.clickNpcType == 1) && World.getWorld().getNpcs().get(player.npcClickIndex) != null) {
							if (player.goodDistance(player.getX(), player.getY(), World.getWorld().getNpcs().get(player.npcClickIndex).getX(), World.getWorld().getNpcs().get(player.npcClickIndex).getY(), 1)) {
								player.turnPlayerTo(World.getWorld().getNpcs().get(player.npcClickIndex).getX(), World.getWorld().getNpcs().get(player.npcClickIndex).getY());
								World.getWorld().getNpcs().get(player.npcClickIndex).faceLocation(player.getX(), player.getY());
								//NPCHandler.npcs[c.npcClickIndex].facePlayer(c.getIndex());
								player.getActions().firstClickNpc(World.getWorld().getNpcs().get(player.npcClickIndex));
								stop();
							}
						}
						if (player.clickNpcType == 0 || player.clickNpcType > 1)
							stop();
					}

					@Override
					public void onStop() {
						player.clickNpcType = 0;
					}
				});
			}
			break;

		case SECOND_CLICK:
			player.npcClickIndex = player.inStream.readUnsignedWordBigEndianA(); // NPC INDEX from the client
			player.npcType = World.getWorld().getNpcs().get(player.npcClickIndex).npcId;
			player.distance = 1;
			
			// distance for certain npcs.. like bankers can be done over a bank booth
			switch(player.npcType) {
				case 394:
					player.distance = 3;
					break;
			}
			
			// if within distance, handle
			if (player.goodDistance(World.getWorld().getNpcs().get(player.npcClickIndex).getX(), World.getWorld().getNpcs().get(player.npcClickIndex).getY(), player.getX(), player.getY(), player.distance)) {
				player.turnPlayerTo(World.getWorld().getNpcs().get(player.npcClickIndex).getX(), World.getWorld().getNpcs().get(player.npcClickIndex).getY());
				World.getWorld().getNpcs().get(player.npcClickIndex).faceLocation(player.getX(), player.getY());
				//NPCHandler.npcs[c.npcClickIndex].facePlayer(c.getIndex());
				player.getActions().secondClickNpc(World.getWorld().getNpcs().get(player.npcClickIndex));
				// PI's terrible design
			} else {
				// we're not in distance. run towards then interact when close enough.
				player.clickNpcType = 2;
				Server.getTaskScheduler().schedule(new ScheduledTask(1) {

					@Override
					public void execute() {
						if ((player.clickNpcType == 2) && World.getWorld().getNpcs().get(player.npcClickIndex) != null) {
							if (player.goodDistance(player.getX(), player.getY(), World.getWorld().getNpcs().get(player.npcClickIndex).getX(), World.getWorld().getNpcs().get(player.npcClickIndex).getY(), 1)) {
								player.turnPlayerTo(World.getWorld().getNpcs().get(player.npcClickIndex).getX(), World.getWorld().getNpcs().get(player.npcClickIndex).getY());
								World.getWorld().getNpcs().get(player.npcClickIndex).faceLocation(player.getX(), player.getY());
								//NPCHandler.npcs[c.npcClickIndex].facePlayer(c.getIndex());
								player.getActions().secondClickNpc(World.getWorld().getNpcs().get(player.npcClickIndex));
								stop();
							}
						}
						if (player.clickNpcType < 2 || player.clickNpcType > 2)
							stop();
					}

					@Override
					public void onStop() {
						player.clickNpcType = 0;

					}
				});
			}
			break;

		case THIRD_CLICK:
			player.npcClickIndex = player.inStream.readSignedWord();
			player.npcType = World.getWorld().getNpcs().get(player.npcClickIndex).npcId;

			if (player.goodDistance(World.getWorld().getNpcs().get(player.npcClickIndex).getX(), World.getWorld().getNpcs().get(player.npcClickIndex).getY(), player.getX(), player.getY(), 1)) {
				player.turnPlayerTo(World.getWorld().getNpcs().get(player.npcClickIndex).getX(), World.getWorld().getNpcs().get(player.npcClickIndex).getY());
				World.getWorld().getNpcs().get(player.npcClickIndex).faceLocation(player.getX(), player.getY());
				//NPCHandler.npcs[c.npcClickIndex].facePlayer(c.getIndex());
				player.getActions().thirdClickNpc(player.npcType);
			} else {
				player.clickNpcType = 3;
				Server.getTaskScheduler().schedule(new ScheduledTask(1) {
					@Override
					public void execute() {
						if ((player.clickNpcType == 3) && World.getWorld().getNpcs().get(player.npcClickIndex) != null) {
							if (player.goodDistance(player.getX(), player.getY(), World.getWorld().getNpcs().get(player.npcClickIndex).getX(), World.getWorld().getNpcs().get(player.npcClickIndex).getY(), 1)) {
								player.turnPlayerTo(World.getWorld().getNpcs().get(player.npcClickIndex).getX(), World.getWorld().getNpcs().get(player.npcClickIndex).getY());
								World.getWorld().getNpcs().get(player.npcClickIndex).faceLocation(player.getX(), player.getY());
								//NPCHandler.npcs[c.npcClickIndex].facePlayer(c.getIndex());
								player.getActions().thirdClickNpc(player.npcType);
								stop();
							}
						}
						if (player.clickNpcType < 3)
							stop();
					}

					@Override
					public void onStop() {
						player.clickNpcType = 0;

					}
				});
			}
			break;
		case FOURTH_CLICK:
			player.npcClickIndex = player.inStream.readSignedWordBigEndian();
			if (World.getWorld().getNpcs().get(player.npcClickIndex) == null) {
				return;
			}
			player.npcType = World.getWorld().getNpcs().get(player.npcClickIndex).npcId;
			if (World.getWorld().getNpcs().get(player.npcClickIndex) == null) {
				return;
			}

			if (player.goodDistance(World.getWorld().getNpcs().get(player.npcClickIndex).getX(), World.getWorld().getNpcs().get(player.npcClickIndex).getY(), player.getX(), player.getY(), 1)) {
				player.turnPlayerTo(World.getWorld().getNpcs().get(player.npcClickIndex).getX(), World.getWorld().getNpcs().get(player.npcClickIndex).getY());
				World.getWorld().getNpcs().get(player.npcClickIndex).faceLocation(player.getX(), player.getY());
				//NPCHandler.npcs[c.npcClickIndex].facePlayer(c.getIndex());
				player.getActions().fourthClickNpc(player.npcType);
			} else {
				player.clickNpcType = 4;
				Server.getTaskScheduler().schedule(new ScheduledTask(1) {
					@Override
					public void execute() {
						if (!player.isActive()) {
							stop();
							return;
						}
						if ((player.clickNpcType == 4) && World.getWorld().getNpcs().get(player.npcClickIndex) != null) {
							if (player.goodDistance(player.getX(), player.getY(), World.getWorld().getNpcs().get(player.npcClickIndex).getX(), World.getWorld().getNpcs().get(player.npcClickIndex).getY(), 1)) {
								player.turnPlayerTo(World.getWorld().getNpcs().get(player.npcClickIndex).getX(), World.getWorld().getNpcs().get(player.npcClickIndex).getY());
								World.getWorld().getNpcs().get(player.npcClickIndex).faceLocation(player.getX(), player.getY());
								//NPCHandler.npcs[c.npcClickIndex].facePlayer(c.getIndex());
								player.getActions().firstClickNpc(World.getWorld().getNpcs().get(player.npcClickIndex));
								stop();
							}
						}
						if (player.clickNpcType < 4)
							stop();
					}

					@Override
					public void onStop() {
						player.clickNpcType = 0;
					}
				});
			}
			break;
		}

	}
}

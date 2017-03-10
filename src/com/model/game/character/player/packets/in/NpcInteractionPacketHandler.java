package com.model.game.character.player.packets.in;

import com.model.Server;
import com.model.game.World;
import com.model.game.character.npc.Npc;
import com.model.game.character.player.Player;
import com.model.game.character.player.packets.PacketType;
import com.model.game.character.player.packets.out.SendMessagePacket;
import com.model.game.location.Position;
import com.model.task.ScheduledTask;

/**
 * Click NPC
 */
public class NpcInteractionPacketHandler implements PacketType {
	public static final int ATTACK_NPC = 72, MAGE_NPC = 131, FIRST_CLICK = 155, SECOND_CLICK = 17, THIRD_CLICK = 21, FOURTH_CLICK = 18;

	@Override
	public void handle(final Player player, int packetType, int packetSize) {
		player.getCombat().reset();
		player.npcClickIndex = 0;
		player.getCombat().reset();
		player.clickNpcType = 0;
		player.setFollowing(null);
		if (player.isPlayerTransformed() || player.isTeleporting()) {
			return;
		}

		switch (packetType) {
		/**
		 * Attack npc melee or range
		 **/
		case ATTACK_NPC:
			int pid = player.getInStream().readUnsignedWordA();
			Npc npc = World.getWorld().getNpcs().get(pid);
			if (npc == null) {
				break;
			}
			if (npc.maximumHealth == 0 && npc.npcId != 493) {
				player.getCombat().reset();
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

			player.faceEntity(npc);
			player.usingMagic = false;
			boolean usingBow = player.getEquipment().isBow(player);
			boolean throwingWeapon = player.getEquipment().isThrowingWeapon(player);
			boolean usingCross = player.getEquipment().isCrossbow(player);
			
			if ((usingBow || usingCross || player.autoCast) && player.goodDistance(player.getX(), player.getY(), npc.getX(), npc.getY(), 7)) {
				player.stopMovement();
			}
			if (throwingWeapon && player.goodDistance(player.getX(), player.getY(), npc.getX(), npc.getY(), 4)) {
				player.stopMovement();
			}

			player.getCombat().setTarget(npc);
			break;

		/**
		 * Attack npc with magic
		 **/
		case MAGE_NPC:
			pid = player.getInStream().readSignedWordBigEndianA();
			int castingSpellId = player.getInStream().readSignedWordA();
			player.usingMagic = false;
            npc = World.getWorld().getNpcs().get(pid);
			if (npc == null) {
				return;
			}

            if (!npc.getDefinition().isAttackable())
                return;
			if (npc.maximumHealth == 0 || npc.npcId == 944) {
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
				if (player.goodDistance(player.getX(), player.getY(), npc.getX(), npc.getY(), 6)) {
					player.stopMovement();
				}
				player.getCombat().setTarget(npc);
			}
			break;

		case FIRST_CLICK:
			player.npcClickIndex = player.inStream.readSignedWordBigEndian();
			Npc first_click_npc = World.getWorld().getNpcs().get(player.npcClickIndex);
			player.distance = 1;

			if (first_click_npc == null) {
				return;
			}
			
			switch (first_click_npc.npcId) {
			case 394:
			case 306:
				player.distance = 3;
				break;
			}
			if (player.goodDistance(first_click_npc.getX(), first_click_npc.getY(), player.getX(), player.getY(), player.distance)) {
				player.face(player, new Position(first_click_npc.getX(), first_click_npc.getY()));
				first_click_npc.face(first_click_npc, new Position(player.getX(), player.getY()));
				player.getActions().firstClickNpc(first_click_npc);
			} else {
				player.clickNpcType = 1;
				Server.getTaskScheduler().schedule(new ScheduledTask(1) {
					@Override
					public void execute() {
						if (!player.isActive()) {
							stop();
							return;
						}
						if ((player.clickNpcType == 1) && first_click_npc != null) {
							if (player.goodDistance(player.getX(), player.getY(), first_click_npc.getX(), first_click_npc.getY(), 1)) {
								player.face(player, new Position(first_click_npc.getX(), first_click_npc.getY()));
								first_click_npc.face(first_click_npc, new Position(player.getX(), player.getY()));
								player.getActions().firstClickNpc(first_click_npc);
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
			Npc second_click_npc = World.getWorld().getNpcs().get(player.npcClickIndex);
			player.distance = 1;
			
			if(second_click_npc == null) {
				return;
			}
			
			// distance for certain npcs.. like bankers can be done over a bank booth
			switch(second_click_npc.npcId) {
				case 394:
					player.distance = 3;
					break;
			}
			
			// if within distance, handle
			if (player.goodDistance(second_click_npc.getX(), second_click_npc.getY(), player.getX(), player.getY(), player.distance)) {
				player.face(player, new Position(second_click_npc.getX(), second_click_npc.getY()));
				second_click_npc.face(second_click_npc, new Position(player.getX(), player.getY()));
				player.getActions().secondClickNpc(World.getWorld().getNpcs().get(player.npcClickIndex));
				// PI's terrible design
			} else {
				// we're not in distance. run towards then interact when close enough.
				player.clickNpcType = 2;
				Server.getTaskScheduler().schedule(new ScheduledTask(1) {

					@Override
					public void execute() {
						if ((player.clickNpcType == 2) && second_click_npc != null) {
							if (player.goodDistance(player.getX(), player.getY(), second_click_npc.getX(), second_click_npc.getY(), 1)) {
								player.face(player, new Position(second_click_npc.getX(), second_click_npc.getY()));
								second_click_npc.face(second_click_npc, new Position(player.getX(), player.getY()));
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
			Npc thrid_click_npc = World.getWorld().getNpcs().get(player.npcClickIndex);
			player.distance = 1;
			
			if(thrid_click_npc == null) {
				return;
			}

			if (player.goodDistance(thrid_click_npc.getX(), thrid_click_npc.getY(), player.getX(), player.getY(), 1)) {
				player.face(player, new Position(thrid_click_npc.getX(), thrid_click_npc.getY()));
				thrid_click_npc.face(thrid_click_npc, new Position(player.getX(), player.getY()));
				player.getActions().thirdClickNpc(thrid_click_npc);
			} else {
				player.clickNpcType = 3;
				Server.getTaskScheduler().schedule(new ScheduledTask(1) {
					@Override
					public void execute() {
						if ((player.clickNpcType == 3) && thrid_click_npc != null) {
							if (player.goodDistance(player.getX(), player.getY(), thrid_click_npc.getX(), thrid_click_npc.getY(), 1)) {
								player.face(player, new Position(thrid_click_npc.getX(), thrid_click_npc.getY()));
								thrid_click_npc.face(thrid_click_npc, new Position(player.getX(), player.getY()));
								player.getActions().thirdClickNpc(thrid_click_npc);
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
			Npc fourth_click_npc = World.getWorld().getNpcs().get(player.npcClickIndex);
			player.distance = 1;
			
			if (fourth_click_npc == null) {
				return;
			}

			if (player.goodDistance(fourth_click_npc.getX(), fourth_click_npc.getY(), player.getX(), player.getY(), 1)) {
				player.face(player, new Position(fourth_click_npc.getX(), fourth_click_npc.getY()));
				fourth_click_npc.face(fourth_click_npc, new Position(player.getX(), player.getY()));
				player.getActions().fourthClickNpc(fourth_click_npc);
			} else {
				player.clickNpcType = 4;
				Server.getTaskScheduler().schedule(new ScheduledTask(1) {
					@Override
					public void execute() {
						if (!player.isActive()) {
							stop();
							return;
						}
						if ((player.clickNpcType == 4) && fourth_click_npc != null) {
							if (player.goodDistance(player.getX(), player.getY(), fourth_click_npc.getX(), fourth_click_npc.getY(), 1)) {
								player.face(player, new Position(fourth_click_npc.getX(), fourth_click_npc.getY()));
								fourth_click_npc.face(fourth_click_npc, new Position(player.getX(), player.getY()));
								player.getActions().fourthClickNpc(fourth_click_npc);
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

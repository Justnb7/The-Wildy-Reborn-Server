package com.model.game.character.player.packets.in;

import com.model.Server;
import com.model.game.World;
import com.model.game.character.npc.NPC;
import com.model.game.character.player.Player;
import com.model.game.character.player.content.clicking.npc.NpcInteraction;
import com.model.game.character.player.packets.PacketType;
import com.model.game.location.Position;
import com.model.task.ScheduledTask;

/**
 * Handles all npc packet interaction
 * 
 * @author Patrick van Elderen
 * 
 */
public class NpcInteractionPacketHandler implements PacketType {

	/**
	 * Attacking npc opcode.
	 */
	private final int ATTACK_NPC = 72;

	/**
	 * Maging npc opcode.
	 */
	private final int MAGE_NPC = 131;

	/**
	 * Option 1 opcode.
	 */
	private final int FIRST_CLICK = 155;

	/**
	 * Option 2 opcode.
	 */
	private final int SECOND_CLICK = 17;

	/**
	 * Option 3 opcode.
	 */
	private final int THIRD_CLICK = 21;

	/**
	 * Option 4 opcode.
	 */
	private final int FOURTH_CLICK = 18;

	@Override
	public void handle(final Player player, int packet, int size) {
		player.getCombat().reset();
		player.setFollowing(null);
		if (player.isPlayerTransformed() || player.isTeleporting()) {
			return;
		}

		switch (packet) {
		case ATTACK_NPC:
			handleAttackNpcPacket(player, packet);
			break;
		case MAGE_NPC:
			handleMagicOnNpcPacket(player, packet);
			break;

		case FIRST_CLICK:
			handleFirstClickPacket(player, packet);
			break;

		case SECOND_CLICK:
			handleSecondClickPacket(player, packet);
			break;

		case THIRD_CLICK:
			handleThirdClickPacket(player, packet);
			break;
		case FOURTH_CLICK:
			handleFourthClickPacket(player, packet);
			break;
		}

	}

	private void handleAttackNpcPacket(Player player, int packet) {
		int pid = player.getInStream().readUnsignedWordA();
		NPC npc = World.getWorld().getNPCs().get(pid);
		if (npc == null) {
			return;
		}
		if (npc.maximumHealth == 0 && npc.npcId != 493) {
			player.getCombat().reset();
			return;
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

		if ((usingBow || usingCross || player.autoCast)
				&& player.goodDistance(player.getX(), player.getY(), npc.getX(), npc.getY(), 7)) {
			player.stopMovement();
		}
		if (throwingWeapon && player.goodDistance(player.getX(), player.getY(), npc.getX(), npc.getY(), 4)) {
			player.stopMovement();
		}

		player.getCombat().setTarget(npc);
	}

	private void handleMagicOnNpcPacket(Player player, int packet) {
		int pid = player.getInStream().readSignedWordBigEndianA();
		int castingSpellId = player.getInStream().readSignedWordA();
		player.usingMagic = false;
		NPC npc = World.getWorld().getNPCs().get(pid);
		if (npc == null) {
			return;
		}

		if (!npc.getDefinition().isAttackable())
			return;
		if (npc.maximumHealth == 0 || npc.npcId == 944) {
			player.getActionSender().sendMessage("You can't attack this npc.");
			return;
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
	}

	private void handleFirstClickPacket(Player player, int packet) {
		int index = player.inStream.readSignedWordBigEndian();
		NPC npc = (NPC) World.getWorld().getNPCs().get(index);

		int distance = 1;
		if (player.inDebugMode()) {
			System.out.println(String.format("[NpcInteractionPacketHandler] - npc: %d ", npc.getDefinition().getId()));
		}
		if (npc == null) {
			return;
		}
		switch (npc.npcId) {
		case 394:
		case 306:
			distance = 3;
			break;
		}
		if (player.goodDistance(npc.getX(), npc.getY(), player.getX(), player.getY(), distance)) {
			player.face(player, new Position(npc.getX(), npc.getY()));
			npc.face(npc, new Position(player.getX(), player.getY()));
			NpcInteraction.firstOption(player, npc);
		} else {
			Server.getTaskScheduler().schedule(new ScheduledTask(1) {
				@Override
				public void execute() {
					if (!player.isActive()) {
						stop();
						return;
					}
					if (npc != null) {
						if (player.goodDistance(player.getX(), player.getY(), npc.getX(), npc.getY(), 1)) {
							player.face(player, new Position(npc.getX(), npc.getY()));
							npc.face(npc, new Position(player.getX(), player.getY()));
							NpcInteraction.firstOption(player, npc);
							stop();
						}
					}
				}

				@Override
				public void onStop() {
				}
			});
		}
	}

	private void handleSecondClickPacket(Player player, int packet) {
		int index = player.inStream.readUnsignedWordBigEndianA();
		NPC npc = (NPC) World.getWorld().getNPCs().get(index);
		int distance = 1;
		if (player.inDebugMode()) {
			System.out.println(String.format("[NpcInteractionPacketHandler second option] - npc: %d ", npc.getDefinition().getId()));
		}
		if (npc == null) {
			return;
		}
		// distance for certain npcs.. like bankers can be done over a bank
		// booth
		switch (npc.npcId) {
		case 394:
			distance = 3;
			break;
		}

		// if within distance, handle
		if (player.goodDistance(npc.getX(), npc.getY(), player.getX(), player.getY(), distance)) {
			player.face(player, new Position(npc.getX(), npc.getY()));
			npc.face(npc, new Position(player.getX(), player.getY()));
			NpcInteraction.secondOption(player, npc);
			// PI's terrible design
		} else {
			// we're not in distance. run towards then interact when close
			// enough.
			Server.getTaskScheduler().schedule(new ScheduledTask(1) {

				@Override
				public void execute() {
					if (npc != null) {
						if (player.goodDistance(player.getX(), player.getY(), npc.getX(), npc.getY(), 1)) {
							player.face(player, new Position(npc.getX(), npc.getY()));
							npc.face(npc, new Position(player.getX(), player.getY()));
							NpcInteraction.secondOption(player, npc);
							stop();
						}
					}
				}

				@Override
				public void onStop() {
				}
			});
		}
	}

	private void handleThirdClickPacket(Player player, int packet) {
		int index = player.inStream.readSignedWord();
		NPC npc = (NPC) World.getWorld().getNPCs().get(index);
		int distance = 1;
		if (player.inDebugMode()) {
			System.out.println(String.format("[NpcInteractionPacketHandler second option] - npc: %d ", npc.getDefinition().getId()));
		}
		if (npc == null) {
			return;
		}
		if (player.goodDistance(npc.getX(), npc.getY(), player.getX(), player.getY(), distance)) {
			player.face(player, new Position(npc.getX(), npc.getY()));
			npc.face(npc, new Position(player.getX(), player.getY()));
			NpcInteraction.thirdOption(player, npc);
		} else {
			Server.getTaskScheduler().schedule(new ScheduledTask(1) {
				@Override
				public void execute() {
					if (npc != null) {
						if (player.goodDistance(player.getX(), player.getY(), npc.getX(), npc.getY(), 1)) {
							player.face(player, new Position(npc.getX(), npc.getY()));
							npc.face(npc, new Position(player.getX(), player.getY()));
							NpcInteraction.thirdOption(player, npc);
							stop();
						}
					}
				}

				@Override
				public void onStop() {
				}
			});
		}
	}

	private void handleFourthClickPacket(Player player, int packet) {
		int index = player.inStream.readSignedWordBigEndian();
		NPC npc = (NPC) World.getWorld().getNPCs().get(index);
		int distance = 1;
		if (player.inDebugMode()) {
			System.out.println(String.format("[NpcInteractionPacketHandler second option] - npc: %d ", npc.getDefinition().getId()));
		}
		if (npc == null) {
			return;
		}
		if (player.goodDistance(npc.getX(), npc.getY(), player.getX(), player.getY(), distance)) {
			player.face(player, new Position(npc.getX(), npc.getY()));
			npc.face(npc, new Position(player.getX(), player.getY()));
			NpcInteraction.fourthOption(player, npc);
		} else {
			Server.getTaskScheduler().schedule(new ScheduledTask(1) {
				@Override
				public void execute() {
					if (!player.isActive()) {
						stop();
						return;
					}
					if (npc != null) {
						if (player.goodDistance(player.getX(), player.getY(), npc.getX(), npc.getY(), 1)) {
							player.face(player, new Position(npc.getX(), npc.getY()));
							npc.face(npc, new Position(player.getX(), player.getY()));
							NpcInteraction.fourthOption(player, npc);
							stop();
						}
					}
				}

				@Override
				public void onStop() {
				}
			});
		}
	}
}

package com.model.game.character.player.packets.in;

import com.model.Server;
import com.model.game.World;
import com.model.game.character.npc.NPC;
import com.model.game.character.player.Player;
import com.model.game.character.player.packets.PacketType;
import com.model.task.impl.WalkToNpcTask;

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
		if (npc.getMaxHitpoints() == 0 && npc.getId() != 493) {
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
		if (npc.getMaxHitpoints() == 0 || npc.getId() == 944) {
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

		if (player.inDebugMode()) {
			System.out.println(String.format("[NpcInteractionPacketHandler] - npc: %d ", npc.getDefinition().getId()));
		}
		if (npc == null) {
			return;
		}
		Server.getTaskScheduler().schedule(new WalkToNpcTask(player, npc, 1));
	}

	private void handleSecondClickPacket(Player player, int packet) {
		int index = player.inStream.readUnsignedWordBigEndianA();
		NPC npc = (NPC) World.getWorld().getNPCs().get(index);
		
		if (player.inDebugMode()) {
			System.out.println(String.format("[NpcInteractionPacketHandler second option] - npc: %d ", npc.getDefinition().getId()));
		}
		if (npc == null) {
			return;
		}
		Server.getTaskScheduler().schedule(new WalkToNpcTask(player, npc, 2));
	}

	private void handleThirdClickPacket(Player player, int packet) {
		int index = player.inStream.readSignedWord();
		NPC npc = (NPC) World.getWorld().getNPCs().get(index);
		
		if (player.inDebugMode()) {
			System.out.println(String.format("[NpcInteractionPacketHandler third option] - npc: %d ", npc.getDefinition().getId()));
		}
		if (npc == null) {
			return;
		}
		Server.getTaskScheduler().schedule(new WalkToNpcTask(player, npc, 3));
	}

	private void handleFourthClickPacket(Player player, int packet) {
		int index = player.inStream.readSignedWordBigEndian();
		NPC npc = (NPC) World.getWorld().getNPCs().get(index);

		if (player.inDebugMode()) {
			System.out.println(String.format("[NpcInteractionPacketHandler fourth option] - npc: %d ", npc.getDefinition().getId()));
		}
		if (npc == null) {
			return;
		}
		Server.getTaskScheduler().schedule(new WalkToNpcTask(player, npc, 4));
	}
}

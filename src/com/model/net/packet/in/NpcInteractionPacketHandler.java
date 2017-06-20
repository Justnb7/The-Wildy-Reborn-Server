package com.model.net.packet.in;

import com.model.game.World;
import com.model.game.character.combat.combat_data.CombatStyle;
import com.model.game.character.npc.NPC;
import com.model.game.character.player.Player;
import com.model.game.item.container.impl.equipment.EquipmentConstants;
import com.model.net.packet.PacketType;
import com.model.server.Server;
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
		player.getCombatState().reset();
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
		player.debug("enter");
		NPC npc = World.getWorld().getNPCs().get(pid);
		if (npc == null) {
			return;
		}
		if (npc.getHitpoints() == 0) {
			player.debug("stop");
			player.getCombatState().reset();
			return;
		}
		if (!npc.getDefinition().isAttackable()) {
			return;
		}
		if (player.autocastId > 0) {
			player.autoCast = true;
		}
		if (!player.autoCast && player.spellId > 0) {
			player.spellId = 0;
		}

		player.faceEntity(npc);
		player.setCombatType(null);
		boolean usingBow = EquipmentConstants.isBow(player);
		boolean throwingWeapon = EquipmentConstants.isThrowingWeapon(player);
		boolean usingCross = EquipmentConstants.isCrossbow(player);

		if ((usingBow || usingCross || player.autoCast)
				&& player.goodDistance(player.getX(), player.getY(), npc.getX(), npc.getY(), 7)) {
			player.getWalkingQueue().reset();
		}
		if (throwingWeapon && player.goodDistance(player.getX(), player.getY(), npc.getX(), npc.getY(), 4)) {
			player.getWalkingQueue().reset();
		}

		player.getCombatState().setTarget(npc);
	}

	private void handleMagicOnNpcPacket(Player player, int packet) {
		int pid = player.getInStream().readSignedWordBigEndianA();
		int castingSpellId = player.getInStream().readSignedWordA();
		player.setCombatType(null);
		player.spellId = -1;
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
				player.spellId = i;
				player.setCombatType(CombatStyle.MAGIC);
				break;
			}
		}

		if (player.autoCast) {
			player.autoCast = false;
		}
		if (player.getCombatType() == CombatStyle.MAGIC) {
			if (player.goodDistance(player.getX(), player.getY(), npc.getX(), npc.getY(), 6)) {
				player.getWalkingQueue().reset();
			}
			player.getCombatState().setTarget(npc);
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

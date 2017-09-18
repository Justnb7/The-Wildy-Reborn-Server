package com.venenatis.game.net.packet.in;

import com.venenatis.game.model.combat.data.CombatStyle;
import com.venenatis.game.model.entity.npc.NPC;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.net.packet.PacketType;
import com.venenatis.game.task.impl.WalkToNpcTask;
import com.venenatis.game.world.World;
import com.venenatis.server.Server;

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
		if (player.isPlayerTransformed() || player.getTeleportAction().isTeleporting()) {
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
		if (npc.getHitpoints() == 0) {
			player.getCombatState().reset();
			return;
		}
		if (!npc.getDefinition().isAttackable()) {
			return;
		}

		player.face(npc.getLocation());
		player.setCombatType(null);
		player.setInteractingEntity(npc);


		if ((player.getCombatType() != CombatStyle.MELEE) && player.goodDistance(player.getX(), player.getY(), npc.getX(), npc.getY(), 7)) {
			player.getWalkingQueue().reset();
		}

		player.getCombatState().setTarget(npc);
	}

	private void handleMagicOnNpcPacket(Player player, int packet) {
		int pid = player.getInStream().readSignedWordBigEndianA();
		int castingSpellId = player.getInStream().readSignedWordA();
		player.setCombatType(null);
		player.setSpellId(-1);
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
		for (int spell = 0; spell < player.MAGIC_SPELLS.length; spell++) {
			if (castingSpellId == player.MAGIC_SPELLS[spell][0]) {
				player.setSpellId(spell);
				player.setCombatType(CombatStyle.MAGIC);
				break;
			}
		}

		if (player.getCombatType() == CombatStyle.MAGIC) {
			if (player.goodDistance(player.getX(), player.getY(), npc.getX(), npc.getY(), 6)) {
				player.getWalkingQueue().reset();
			}
			player.getCombatState().setTarget(npc);
		}
		
		player.setInteractingEntity(npc);
		player.getWalkingQueue().reset();
	}

	private void handleFirstClickPacket(Player player, int packet) {
		int index = player.inStream.readSignedWordBigEndian();
		NPC npc = (NPC) World.getWorld().getNPCs().get(index);

		player.debug(String.format("[NpcInteractionPacketHandler] - npc: %d ", npc.getDefinition().getId()));
		
		Server.getTaskScheduler().schedule(new WalkToNpcTask(player, npc, 1));
	}

	private void handleSecondClickPacket(Player player, int packet) {
		int index = player.inStream.readUnsignedWordBigEndianA();
		NPC npc = (NPC) World.getWorld().getNPCs().get(index);
		
		player.debug(String.format("[NpcInteractionPacketHandler second option] - npc: %d ", npc.getDefinition().getId()));

		Server.getTaskScheduler().schedule(new WalkToNpcTask(player, npc, 2));
	}

	private void handleThirdClickPacket(Player player, int packet) {
		int index = player.inStream.readSignedWord();
		NPC npc = (NPC) World.getWorld().getNPCs().get(index);
		
		player.debug(String.format("[NpcInteractionPacketHandler third option] - npc: %d ", npc.getDefinition().getId()));
		
		Server.getTaskScheduler().schedule(new WalkToNpcTask(player, npc, 3));
	}

	private void handleFourthClickPacket(Player player, int packet) {
		int index = player.inStream.readSignedWordBigEndian();
		NPC npc = (NPC) World.getWorld().getNPCs().get(index);

		player.debug(String.format("[NpcInteractionPacketHandler fourth option] - npc: %d ", npc.getDefinition().getId()));
		
		Server.getTaskScheduler().schedule(new WalkToNpcTask(player, npc, 4));
	}
}

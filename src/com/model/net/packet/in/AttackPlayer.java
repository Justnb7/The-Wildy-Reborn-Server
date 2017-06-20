package com.model.net.packet.in;

import com.model.game.World;
import com.model.game.character.Entity;
import com.model.game.character.combat.Combat;
import com.model.game.character.combat.combat_data.CombatStyle;
import com.model.game.character.player.Player;
import com.model.net.packet.PacketType;

/**
 * Attack Player
 **/
public class AttackPlayer implements PacketType {

	public static final int ATTACK_PLAYER = 73, MAGE_PLAYER = 249;

	@Override
	public void handle(Player player, int packetType, int packetSize) {
		player.getCombatState().reset();
		if (player.isPlayerTransformed() || player.isTeleporting()) {
			return;
		}
		switch (packetType) {
		/**
		 * Attack player
		 **/
		case ATTACK_PLAYER:
			int targetIndex = player.getInStream().readSignedWordBigEndian();
			Entity targ = World.getWorld().getPlayers().get(targetIndex);
			player.getCombatState().reset();
			if (targ == null) {
				break;
			}
			if (targetIndex < 0 || player.getIndex() < 0 || player.isDead()) {
				System.out.println("index below 0 or player dead");
				player.getCombatState().reset();
				return;
			}
			player.getCombatState().setTarget(targ);
			break;

		/**
		 * Attack player with magic
		 **/
		case MAGE_PLAYER:
			int targetIdx = player.getInStream().readSignedWordA();
			int spellId = player.getInStream().readSignedWordBigEndian();

			targ = World.getWorld().getPlayers().get(targetIdx);

			if (targ == null || player.isDead()) {
				player.getCombatState().reset();
				break;
			}

			for (int i = 0; i < player.MAGIC_SPELLS.length; i++) {
				if (spellId == player.MAGIC_SPELLS[i][0]) {
					player.spellId = i;
					player.setCombatType(CombatStyle.MAGIC);
					break;
				}
			}
			
			if (!player.teleblock.elapsed(player.teleblockLength) && player.MAGIC_SPELLS[player.spellId][0] == 12445) {
				player.getActionSender().sendMessage("That player is already affected by this spell.");
				player.getWalkingQueue().reset();
				Combat.resetCombat(player);
			}
			if (player.getCombatType() == CombatStyle.MAGIC) {
				player.getCombatState().setTarget(targ);
			} else {
				System.err.println("Unsupported combat situation, is the spell you're using supported?");
			}
		}

	}
}

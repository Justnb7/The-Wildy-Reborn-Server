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
		player.getCombat().reset();
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
			if (targ == null) {
				break;
			}
			
			if (targetIndex < 0 || player.getIndex() < 0 || player.isDead()) {
				System.out.println("index below 0 or player dead");
				player.getCombat().reset();
				return;
			}
			player.getCombat().setTarget(targ);
			break;

		/**
		 * Attack player with magic
		 **/
		case MAGE_PLAYER:
			int targetIdx = player.getInStream().readSignedWordA();
			int spellId = player.getInStream().readSignedWordBigEndian();

			targ = World.getWorld().getPlayers().get(targetIdx);

			if (targ == null || player.isDead()) {
				player.getCombat().reset();
				break;
			}

			for (int i = 0; i < player.MAGIC_SPELLS.length; i++) {
				if (spellId == player.MAGIC_SPELLS[i][0]) {
					player.setSpellId(i);
					player.usingMagic = true;
					player.setCombatType(CombatStyle.MAGIC);
					break;
				}
			}
			
			if (!player.teleblock.elapsed(player.teleblockLength) && player.MAGIC_SPELLS[player.getSpellId()][0] == 12445) {
				player.getActionSender().sendMessage("That player is already affected by this spell.");
				player.usingMagic = false;
				player.getMovementHandler().stopMovement();
				Combat.resetCombat(player);
			}
			if (player.usingMagic) {
				player.getCombat().setTarget(targ);
			} else {
				System.err.println("Unsupported combat situation, is the spell you're using supported?");
			}
		}

	}
}

package com.model.game.character.player.packets.in;

import com.model.game.World;
import com.model.game.character.combat.Combat;
import com.model.game.character.combat.combat_data.CombatRequirements;
import com.model.game.character.combat.combat_data.CombatType;
import com.model.game.character.player.Player;
import com.model.game.character.player.packets.PacketType;
import com.model.game.character.player.packets.out.SendMessagePacket;

/**
 * Attack Player
 **/
public class AttackPlayer implements PacketType {

	public static final int ATTACK_PLAYER = 73, MAGE_PLAYER = 249;

	@Override
	public void handle(Player player, int packetType, int packetSize) {
		player.playerIndex = 0;
		player.npcIndex = 0;
		if (player.isPlayerTransformed() || player.isTeleporting()) {
			return;
		}
		switch (packetType) {
		/**
		 * Attack player
		 **/
		case ATTACK_PLAYER:
			player.playerIndex = player.getInStream().readSignedWordBigEndian();
			if (World.getWorld().getPlayers().get(player.playerIndex) == null) {
				break;
			}
			
			if (player.getIndex() < 0 || player.isDead()) {
				System.out.println("index below 0 or player dead");
				return;
			}

			player.setSpellId(0);
			player.mageFollow = player.usingMagic = player.usingBow = player.throwingAxe = player.usingArrows = false;
			player.usingCross = player.getEquipment().isCrossbow(player);
			player.setCombatType(player.usingCross ? CombatType.RANGED : CombatType.MELEE);

			if (player.autocastId > 0) {
				player.autoCast = true;
				player.setCombatType(CombatType.MAGIC);
			}
			if (!player.autoCast && player.spellId > 0) {
				player.spellId = 0;
			}

			if ((!player.throwingAxe && (player.getCombatType() == CombatType.RANGED || player.getCombatType() == CombatType.MAGIC)) && player.goodDistance(player.getX(), player.getY(), World.getWorld().getPlayers().get(player.playerIndex).getX(), World.getWorld().getPlayers().get(player.playerIndex).getY(), 7)) {
				player.usingBow = true;
				player.stopMovement();
			}

			if (player.throwingAxe && player.goodDistance(player.getX(), player.getY(), World.getWorld().getPlayers().get(player.playerIndex).getX(), World.getWorld().getPlayers().get(player.playerIndex).getY(), 3)) {
				player.throwingAxe = true;
				player.stopMovement();
			}

			if (CombatRequirements.canAttackVictim(player)) {
				player.followId = player.playerIndex;
				if (!player.usingMagic && !player.usingBow && !player.throwingAxe) {
					player.followDistance = 1;
					player.getPA().followPlayer(true);
				}
				if (player.attackDelay <= 0) {
				}
			}
			break;

		/**
		 * Attack player with magic
		 **/
		case MAGE_PLAYER:
			player.playerIndex = player.getInStream().readSignedWordA();
			player.castingSpellId = player.getInStream().readSignedWordBigEndian();
			player.usingMagic = false;
			if (World.getWorld().getPlayers().get(player.playerIndex) == null) {
				player.playerIndex = 0;
				break;
			}

			if (player.isDead()) {
				break;
			}

			for (int i = 0; i < player.MAGIC_SPELLS.length; i++) {
				if (player.castingSpellId == player.MAGIC_SPELLS[i][0]) {
					player.setSpellId(i);
					player.usingMagic = true;
					player.setCombatType(CombatType.MAGIC);
					break;
				}
			}

			if (player.autoCast) {
				player.autoCast = false;
			}
			
			if (!player.teleblock.elapsed(player.teleblockLength) && player.MAGIC_SPELLS[player.getSpellId()][0] == 12445) {
				player.write(new SendMessagePacket("That player is already affected by this spell."));
				player.usingMagic = false;
				player.setCombatType(CombatType.MELEE);
				player.stopMovement();
				Combat.resetCombat(player);
			}
			if (player.usingMagic) {
				if (player.goodDistance(player.getX(), player.getY(), World.getWorld().getPlayers().get(player.playerIndex).getX(), World.getWorld().getPlayers().get(player.playerIndex).getY(), 7)) {
					player.stopMovement();

					if (player.getSpellId() > 0 && player.autocastId <= 0) {
						player.getPA().resetFollow();
					}
				}

				if (CombatRequirements.canAttackVictim(player)) {
					player.mageFollow = true;
				}
			}
		}

	}
}

package com.model.game.character.player.packets.in;

import com.model.game.World;
import com.model.game.character.combat.Combat;
import com.model.game.character.combat.combat_data.CombatRequirements;
import com.model.game.character.combat.combat_data.CombatType;
import com.model.game.character.player.Player;
import com.model.game.character.player.packets.PacketType;
import com.model.game.character.player.packets.encode.impl.SendMessagePacket;

/**
 * Attack Player
 **/
public class AttackPlayer implements PacketType {

	public static final int ATTACK_PLAYER = 73, MAGE_PLAYER = 249;

	@Override
	public void processPacket(Player player, int packetType, int packetSize) {
		player.playerIndex = 0;
		player.npcIndex = 0;
		player.walkingToObject = false;
		if (player.isPlayerTransformed() || player.teleporting) {
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
				return;
			}
			
			if (player.getBankPin().requiresUnlock()) {
				Combat.resetCombat(player);
				player.getBankPin().open(2);
				return;
			}

			Player target = World.getWorld().getPlayers().get(player.playerIndex);
			
			if (target.isUnattackable()) {
				player.write(new SendMessagePacket("You cannot attack this player."));
				Combat.resetCombat(player);
				return;
			}

			player.setSpellId(0);
			player.mageFollow = player.usingMagic = player.usingBow = player.usingOtherRangeWeapons = player.usingArrows = false;
			player.usingCross = player.playerEquipment[player.getEquipment().getWeaponId()] == 9185 || player.playerEquipment[player.getEquipment().getWeaponId()] == 11785 || player.playerEquipment[player.getEquipment().getWeaponId()] == 18357;
			player.setCombatType(player.usingCross ? CombatType.RANGED : CombatType.MELEE);

			if (player.autocastId > 0) {
				player.autoCast = true;
				player.setCombatType(CombatType.MAGIC);
			}
			if (!player.autoCast && player.spellId > 0) {
				player.spellId = 0;
			}
			
			if (player.getCombatType() != CombatType.MAGIC) {
				for (int bowId : Player.BOWS) {
					if (player.playerEquipment[player.getEquipment().getWeaponId()] == bowId) {
						player.usingBow = true;
						player.setCombatType(CombatType.RANGED);
						break;
					}
				}
				if (player.getCombatType() == CombatType.RANGED) {
					for (int arrowId : Player.ARROWS) {
						if (player.playerEquipment[player.getEquipment().getQuiverId()] == arrowId) {
							player.usingArrows = true;
							break;
						}
					}
				}
				for (int otherRangeId : Player.OTHER_RANGE_WEAPONS) {
					if (player.playerEquipment[player.getEquipment().getWeaponId()] == otherRangeId) {
						player.usingOtherRangeWeapons = true;
						player.setCombatType(CombatType.RANGED);
						break;
					}
				}
			}

			if ((!player.usingOtherRangeWeapons && (player.getCombatType() == CombatType.RANGED || player.getCombatType() == CombatType.MAGIC)) && player.goodDistance(player.getX(), player.getY(), World.getWorld().getPlayers().get(player.playerIndex).getX(), World.getWorld().getPlayers().get(player.playerIndex).getY(), 7)) {
				player.usingBow = true;
				player.stopMovement();
			}

			if (player.usingOtherRangeWeapons && player.goodDistance(player.getX(), player.getY(), World.getWorld().getPlayers().get(player.playerIndex).getX(), World.getWorld().getPlayers().get(player.playerIndex).getY(), 3)) {
				player.usingRangeWeapon = true;
				player.stopMovement();
			}

			if (!player.usingCross && !player.usingArrows && player.usingBow && player.playerEquipment[player.getEquipment().getWeaponId()] < 4212 && player.playerEquipment[player.getEquipment().getWeaponId()] > 4223 && !player.usingCross) {
				player.write(new SendMessagePacket("There is no ammo left in your quiver."));
				break;
			}
			if(player.getCombat().correctBowAndArrows() < player.playerEquipment[player.getEquipment().getQuiverId()] && player.usingBow && !player.getCombat().usingCrystalBow() && player.playerEquipment[player.getEquipment().getWeaponId()] != 9185 && player.playerEquipment[player.getEquipment().getWeaponId()] != 11785 && player.playerEquipment[player.getEquipment().getWeaponId()] != 18357) {
				player.write(new SendMessagePacket("You can't use "+player.getItems().getItemName(player.playerEquipment[player.getEquipment().getQuiverId()]).toLowerCase()+"s with a "+player.getItems().getItemName(player.playerEquipment[player.getEquipment().getWeaponId()]).toLowerCase()+"."));
				player.stopMovement();
				player.npcIndex = 0;
				return;
			}
			if ((player.playerEquipment[player.getEquipment().getWeaponId()] == 9185 || player.playerEquipment[player.getEquipment().getWeaponId()] == 11785 || player.playerEquipment[player.getEquipment().getWeaponId()] == 18357) && !player.getCombat().properBolts()) {
				player.write(new SendMessagePacket("There is no ammo left in your quiver."));
				player.stopMovement();
				Combat.resetCombat(player);
				return;				
			}
			if (CombatRequirements.canAttackVictim(player)) {
				player.followId = player.playerIndex;
				if (!player.usingMagic && !player.usingBow && !player.usingOtherRangeWeapons) {
					player.followDistance = 1;
					player.usingMelee(true);
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
			if (!player.mageAllowed) {
				player.mageAllowed = true;
				break;
			}
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
			
			for (int r = 0; r < player.REDUCE_SPELLS.length; r++) { // reducing
				if (World.getWorld().getPlayers().get(player.playerIndex).REDUCE_SPELLS[r] == player.MAGIC_SPELLS[player.getSpellId()][0]) {
					if ((System.currentTimeMillis() - World.getWorld().getPlayers().get(player.playerIndex).reduceSpellDelay[r]) < World.getWorld().getPlayers().get(player.playerIndex).REDUCE_SPELL_TIME[r]) {
						player.write(new SendMessagePacket("That player is currently immune to this spell."));
						player.usingMagic = false;
						player.setCombatType(CombatType.MELEE);
						player.stopMovement();
						Combat.resetCombat(player);
					}
					break;
				}
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

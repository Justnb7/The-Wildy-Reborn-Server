package com.model.game.character.combat;


import com.model.game.World;
import com.model.game.character.Entity;
import com.model.game.character.combat.magic.CombatSpells;
import com.model.game.character.combat.magic.MagicCalculations;
import com.model.game.character.combat.magic.MagicData;
import com.model.game.character.combat.magic.MagicExtras;
import com.model.game.character.combat.magic.MagicRequirements;
import com.model.game.character.combat.melee.MeleeCalculations;
import com.model.game.character.combat.pvm.PlayerVsNpcCombat;
import com.model.game.character.combat.pvp.PlayerVsPlayerCombat;
import com.model.game.character.combat.range.RangeData;
import com.model.game.character.combat.range.RangeExtras;
import com.model.game.character.combat.range.RangedCalculations;
import com.model.game.character.player.Player;
import com.model.game.character.player.Skills;
import com.model.game.character.player.packets.encode.impl.SendMessagePacket;
import com.model.game.item.Item;

public class CombatAssistant {

	private Player player;

	public CombatAssistant(Player player) {
		this.player = player;
	}
	
	public int[][] slayerRequirements = { {433, 30}, {419, 25}, {420, 20}, {459, 20}, {406, 10}, {480, 17}, { 493, 87 }, {496, 87}, {5534, 87}, { 6296, 56 }, { 3209, 58 }, { 448, 5 },
			{ 414, 15 }, {481, 7}, { 443, 45 }, { 484, 50 }, { 423, 65 }, { 412, 75 }, { 11, 80 }, { 415, 85 }, { 4005, 90 },
			{ 498, 93 }, { 3, 60 }, };

	public boolean goodSlayer(int npcId) {
		for (int j = 0; j < slayerRequirements.length; j++) {
			if (slayerRequirements[j][0] == npcId) {
				if (slayerRequirements[j][1] > player.getSkills().getLevel(Skills.SLAYER)) {
					player.write(new SendMessagePacket("You need a slayer level of " + slayerRequirements[j][1] + " to harm this NPC."));
					return false;
				}
				if(npcId == 5534 && player.getSlayerTask() != 494) {
					player.write(new SendMessagePacket("You must have Kraken's as a slayer-task to disturb these whirlpools."));
					return false;
				}
				if (npcId == 493 && player.getSlayerTask() != 492 || npcId == 496 && player.getSlayerTask() != 494) {
					player.write(new SendMessagePacket("You must have cave krakens as a slayer-task to attack"));
					return false;
				}
			}
		}
		return true;
	}

	public boolean checkMultiBarrageReqs(int i) {
		return MagicExtras.checkMultiBarrageReqs(player, i);
	}

	public void multiSpellEffectNPC(int npcId, int damage) {
		MagicExtras.multiSpellEffectNPC(player, npcId, damage);
	}

	public boolean checkMultiBarrageReqsNPC(int i) {
		return MagicExtras.checkMultiBarrageReqsNPC(i);
	}

	public void appendMultiBarrageNPC(int npcId, boolean splashed) {
		MagicExtras.appendMultiBarrageNPC(player, npcId, splashed);
	}

	public void attackNpc(int i) {
		PlayerVsNpcCombat.attackNpc(player, i);
	}

	public void delayedHit(final Player c, final int i, Item item) {
		PlayerVsNpcCombat.applyNpcHit(c, i, item);
	}

	public void attackPlayer(int i) {
		PlayerVsPlayerCombat.attackPlayer(player, i);
	}

	public void playerDelayedHit(final Player c, final int i, Item item) {
		PlayerVsPlayerCombat.applyPlayerHit(c, i, item);
	}

	public boolean multis() {
		return MagicData.multiSpells(player);
	}

	public void appendMultiBarrage(int playerId, boolean splashed) {
		MagicExtras.appendMultiBarrage(player, playerId, splashed);
	}

	public void multiSpellEffect(int playerId, int damage) {
		MagicExtras.multiSpellEffect(player, playerId, damage);
	}

	public void applySmite(Player defender, int damage) {
		PrayerHandler.handleSmite(player, defender, damage);
	}

	public boolean usingDbow() {
		return player.playerEquipment[player.getEquipment().getWeaponId()] == 11235;
	}

	/**
	 * 
	 * @param attacker The person who is hitting someone. The target is the one with veng active.
	 */
	public void vengeance(Entity attacker, int damage, int delay) {
		CombatSpells.vengeance(player, attacker, damage, delay);
	}

	public int getRangeStr(int i) {
		return RangeData.getRangeStr(i);
	}

	public int getRangeStartGFX() {
		return RangeData.getRangeStartGFX(player);
	}

	public int getRangeProjectileGFX() {
		return RangeData.getRangeProjectileGFX(player);
	}

	public int correctBowAndArrows() {
		return RangeData.correctBowAndArrows(player);
	}

	public int getProjectileShowDelay() {
		return RangeData.getProjectileShowDelay(player);
	}

	public int getProjectileSpeed() {
		return RangeData.getProjectileSpeed(player);
	}

	public void crossbowSpecial(Player c, int i) {
		RangeExtras.crossbowSpecial(c, i);
	}

	public void appendMutliChinchompa(int npcId) {
		RangeExtras.appendMutliChinchompa(player, npcId);
	}

	public boolean properBolts() {
		return usingBolts(player.playerEquipment[player.getEquipment().getQuiverId()]);
	}

	public boolean properDarts() {
		return usingDarts(player.playerEquipment[player.getEquipment().getQuiverId()]);
	}

	public boolean properBoltRacks() {
		return usingRackBolts(player.playerEquipment[player.getEquipment().getQuiverId()]);
	}

	public void castOtherVengeance() {
		// CastOnOther.castOtherVengeance(c);
	}

	public boolean usingRackBolts(int i) {
		return (i == 4740);
	}

	public boolean usingBolts(int i) {
		return (i >= 9140 && i <= 9145) || i >= 9334 && i <= 9344 || (i >= 9236 && i <= 9245) || i == 11875;
	}

	public boolean usingDarts(int i) {
		return (i >= 806 && i <= 817);
	}

	public boolean checkMagicReqs(int spell) {
		return MagicRequirements.checkMagicReqs(player, spell);
	}

	public int getFreezeTime() {
		return MagicData.getFreezeTime(player);
	}

	public int getStartHeight() {
		return MagicData.getStartHeight(player);
	}

	public int getEndHeight() {
		return MagicData.getEndHeight(player);
	}

	public int getStartDelay() {
		return MagicData.getStartDelay(player);
	}

	public int getStaffNeeded() {
		return MagicData.getStaffNeeded(player);
	}

	public boolean godSpells() {
		return MagicData.godSpells(player);
	}

	public int getEndGfxHeight() {
		return MagicData.getEndGfxHeight(player);
	}

	public int getStartGfxHeight() {
		return MagicData.getStartGfxHeight(player);
	}

	public void fireProjectileNpc() {
		RangeData.fireProjectileNpc(player);
	}

	public void fireProjectilePlayer() {
		RangeData.fireProjectilePlayer(player);
	}
	
	public int calculateMeleeMaxHit() {
		return MeleeCalculations.calculateMeleeMaxHit(player, (player.playerIndex > 0 ? World.getWorld().getPlayers().get(player.playerIndex) : World.getWorld().getNpcs().get(player.npcIndex)));
	}
	
	public int calculateRangeMaxHit() {
		return RangedCalculations.calculateRangedMaxHit(player, (player.playerIndex > 0 ? World.getWorld().getPlayers().get(player.playerIndex) : World.getWorld().getNpcs().get(player.npcIndex)));
	}
	
	public int calculateMagicMaxHit() {
		return 0;
	}
	
	public int calculateMagicDefence() {
		return MagicCalculations.calculateMagicDefence(player);
	}
	
	public int calculateRangeDefence() {
		return RangedCalculations.calculateRangeDefence(player);
	}
	
	public int getDefensiveCalculation() {
		return MeleeCalculations.calculateMeleeDefence(player);
	}
	
	public int getAttackCalculation() {
		return MeleeCalculations.calculateMeleeAttack(player, false);
	}
	
	public boolean usingCrystalBow() {
		return player.playerEquipment[player.getEquipment().getWeaponId()] >= 4212 && player.playerEquipment[player.getEquipment().getWeaponId()] <= 4223;
	}
}

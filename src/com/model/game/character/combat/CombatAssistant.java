package com.model.game.character.combat;


import com.model.game.character.Entity;
import com.model.game.character.combat.magic.CombatSpells;
import com.model.game.character.combat.magic.MagicData;
import com.model.game.character.combat.magic.MagicRequirements;
import com.model.game.character.combat.range.RangeData;
import com.model.game.character.player.Player;

public class CombatAssistant {

	private Player player;

	public Entity target;

	public CombatAssistant(Player player) {
		this.player = player;
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
	
	public boolean properJavalins() {
		return usingJavalins(player.playerEquipment[player.getEquipment().getQuiverId()]);
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
	
	public boolean usingJavalins(int javalin) {
		return (javalin >= 825 && javalin <= 830) || javalin == 19484;
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

	public void fireProjectileAtTarget() {
		RangeData.fireProjectileAtTarget(player);
	}
	
	public int calculateMeleeMaxHit() {
		return CombatFormulae.calculateMeleeMaxHit(player, player.getCombat().target);
	}
	
	public int calculateRangeMaxHit() {
		return CombatFormulae.calculateRangeMaxHit(player, player.getCombat().target);
	}

	public void reset() {
		// Nullify target
		target = null;
		// Reset all styles
		player.usingMagic = player.usingBow = false;
		player.setCombatType(null);
	}

	public void setTarget(Entity target) {
		this.target = target;
	}

	public boolean noTarget() {
		return target == null;
	}
}

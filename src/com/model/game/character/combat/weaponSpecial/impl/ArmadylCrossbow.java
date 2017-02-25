package com.model.game.character.combat.weaponSpecial.impl;

import com.model.Server;
import com.model.game.World;
import com.model.game.character.Animation;
import com.model.game.character.Entity;
import com.model.game.character.Graphic;
import com.model.game.character.combat.combat_data.CombatType;
import com.model.game.character.combat.weaponSpecial.SpecialAttack;
import com.model.game.character.player.Player;
import com.model.task.ScheduledTask;

public class ArmadylCrossbow implements SpecialAttack {

	@Override
	public int[] weapons() {
		return new int[] { 11785 };
	}

	@Override
	public void handleAttack(Player player, Entity target) {
		Entity entity = player.playerIndex > 0 ? World.getWorld().getPlayers().get(player.playerIndex) : World.getWorld().getNpcs().get(player.npcIndex);
		player.setCombatType(CombatType.RANGED);
		player.playAnimation(Animation.create(4230));
		player.acbSpec = true;
		player.rangeItemUsed = player.playerEquipment[player.getEquipment().getQuiverId()];
		player.getItems().deleteArrow();
		
		if (player.playerIndex > 0) {
			player.getItems().dropArrowPlayer();
		} else if (player.npcIndex > 0) {
			player.getItems().dropArrowNpc();
		}
		
		player.hitDelay = 2;
		player.setCombatType(CombatType.RANGED);
		
		player.playGraphics(Graphic.create(player.getCombat().getRangeStartGFX(), 0, 0));
		
		if (player.playerIndex > 0) {
			player.getCombat().fireProjectilePlayer();
		} else if (player.npcIndex > 0) {
			player.getCombat().fireProjectileNpc();
		}
		
		if (player.npcIndex > 0)
			npcSpecialHitDelay(player, entity.getIndex(), player.hitDelay);
		else
			playerSpecialHitDelay(player, entity.getIndex(), player.hitDelay);
		player.acbSpec = false;
	}
	
	private void npcSpecialHitDelay(final Player c, final int i, int hitDelay) {
		Server.getTaskScheduler().schedule(new ScheduledTask(hitDelay - 1) {
			public void execute() {
				c.getCombat().delayedHit(c, i, null);
				this.stop();
			}
		});
	}
	
	private void playerSpecialHitDelay(final Player c, final int i, int hitDelay) {
		Server.getTaskScheduler().schedule(new ScheduledTask(hitDelay - 1) {
			public void execute() {
				c.getCombat().playerDelayedHit(c, i, null);
				this.stop();
			}
		});
	}

	@Override
	public int amountRequired() {
		return 40;
	}

	@Override
	public boolean meetsRequirements(Player player, Entity target) {
		return true;
	}

	@Override
	public double getAccuracyMultiplier() {
		return 2;
	}

	@Override
	public double getMaxHitMultiplier() {
		return 1;
	}

}

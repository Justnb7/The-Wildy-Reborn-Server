package com.model.game.character.combat.weaponSpecial.impl;

import com.model.Server;
import com.model.game.World;
import com.model.game.character.Animation;
import com.model.game.character.Entity;
import com.model.game.character.Graphic;
import com.model.game.character.combat.combat_data.CombatData;
import com.model.game.character.combat.combat_data.CombatType;
import com.model.game.character.combat.weaponSpecial.SpecialAttack;
import com.model.game.character.player.Player;
import com.model.task.ScheduledTask;

public class MagicShortbow implements SpecialAttack {

	@Override
	public int[] weapons() {
		return new int[] { 861, 12788, 859 };
	}

	@Override
	public void handleAttack(final Player player, final Entity target) {
		Entity entity = player.playerIndex > 0 ? World.getWorld().getPlayers().get(player.playerIndex) : World.getWorld().getNpcs().get(player.npcIndex);
		player.setCombatType(CombatType.RANGED);
		player.usingBow = true;			
		player.bowSpecShot = 1;
		player.rangeItemUsed = player.playerEquipment[player.getEquipment().getQuiverId()];
		player.getItems().deleteArrow();
		player.getItems().deleteArrow();
		player.lastWeaponUsed = player.playerEquipment[player.getEquipment().getWeaponId()];
		player.playAnimation(Animation.create(1074));
		player.setCombatType(CombatType.RANGED);
		if (player.playerIndex > 0)
			player.getCombat().fireProjectilePlayer();
		else
			player.getCombat().fireProjectileNpc();
		Server.getTaskScheduler().schedule(new ScheduledTask(1) {
			public void execute() {
				player.playAnimation(Animation.create(1074));
				player.playGraphics(Graphic.highGraphic(256));
				this.stop();
			}
		});
		Server.getTaskScheduler().schedule(new ScheduledTask(1) {
			public void execute() {
				if (player.playerIndex > 0)
					player.getCombat().fireProjectilePlayer();
				else
					player.getCombat().fireProjectileNpc();
				player.playGraphics(Graphic.create(256));
				this.stop();
			}
		});
		
		player.hitDelay = CombatData.getHitDelay(player, player.getItems().getItemName(player.playerEquipment[player.getEquipment().getWeaponId()]).toLowerCase());
		if (player.npcIndex > 0)
			npcSpecialHitDelay(player, entity.getIndex(), player.hitDelay);
		else 
			playerSpecialHitDelay(player, entity.getIndex(), player.hitDelay);
	}

	@Override
	public int amountRequired() {
		return 50;
	}

	@Override
	public boolean meetsRequirements(Player player, Entity victim) {
		if (player.usingBow) {
			return true;
		}
		return false;
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
	public double getAccuracyMultiplier() {
		return 1;
	}

	@Override
	public double getMaxHitMultiplier() {
		return 1;
	}
}
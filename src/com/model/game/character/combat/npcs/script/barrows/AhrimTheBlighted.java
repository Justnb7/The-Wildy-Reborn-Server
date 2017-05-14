package com.model.game.character.combat.npcs.script.barrows;

import com.model.game.character.Animation;
import com.model.game.character.Entity;
import com.model.game.character.Graphic;
import com.model.game.character.Hit;
import com.model.game.character.combat.Combat;
import com.model.game.character.combat.Projectile;
import com.model.game.character.combat.combat_data.CombatStyle;
import com.model.game.character.combat.npcs.AbstractBossCombat;
import com.model.game.character.npc.NPC;
import com.model.game.character.player.Player;
import com.model.utility.Utility;

public class AhrimTheBlighted extends AbstractBossCombat {

	@Override
	public void execute(Entity attacker, Entity victim) {
		if(!attacker.isNPC()) {
			return;
		}
		
		int clientSpeed;
		int gfxDelay;
		if(attacker.getLocation().isWithinDistance(attacker, victim, 1)) {
			clientSpeed = 70;
			gfxDelay = 80;
		} else if(attacker.getLocation().isWithinDistance(attacker, victim, 5)) {
			clientSpeed = 90;
			gfxDelay = 100;
		} else if(attacker.getLocation().isWithinDistance(attacker, victim, 8)) {
			clientSpeed = 110;
			gfxDelay = 120;
		} else {
			clientSpeed = 130;
			gfxDelay = 140;
		}
		int delay = (gfxDelay / 20) - 1;
		attacker.playAnimation(Animation.create(727));
		attacker.playGraphics(Graphic.create(155, 0, 0));
		attacker.playProjectile(Projectile.create(attacker.getLocation(), victim.getCentreLocation(), 156, 45, 50, clientSpeed, 43, 31, victim.getProjectileLockonIndex(), 0, 36));
		int randomHit = Utility.random(25);
		victim.playGraphics(randomHit <= 0 ? Graphic.create(85, gfxDelay, 100) : Graphic.create(157, gfxDelay, 100));
		
		 Hit hitInfo = victim.take_hit(attacker, randomHit, CombatStyle.MAGIC, false);

         Combat.hitEvent(attacker, victim, delay, hitInfo, CombatStyle.MAGIC);
         
         ((NPC)attacker).attackTimer = 5;
	}

	@Override
	public int distance(Entity attacker) {
		return 4;
	}

	@Override
	public void dropLoot(Player player, NPC npc) {
		
	}

}

package com.venenatis.game.model.combat.npcs.impl.barrows;

import com.venenatis.game.content.minigames.singleplayer.barrows.BarrowsDetails;
import com.venenatis.game.content.minigames.singleplayer.barrows.BarrowsHandler;
import com.venenatis.game.model.Projectile;
import com.venenatis.game.model.combat.data.CombatStyle;
import com.venenatis.game.model.combat.npcs.AbstractBossCombat;
import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.entity.npc.NPC;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.util.Utility;

public class KarilTheTainted extends AbstractBossCombat {

	@Override
	public void execute(Entity attacker, Entity victim) {
		if(!attacker.isNPC()) {
			return; //this should be an NPC!
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
		int damage = Utility.random(20);
		attacker.playAnimation(Animation.create(2075));
		attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), victim.getCentreLocation(), 27, 45, 50, clientSpeed, 43, 35, victim.getProjectileLockonIndex(), 10, 48));
		
		victim.take_hit(attacker, damage, CombatStyle.RANGE).send(delay);
		
		attacker.getCombatState().setAttackDelay(3);
	}

	@Override
	public int distance(Entity attacker) {
		return 4;
	}

	@Override
	public void dropLoot(Player player, NPC npc) {		
		BarrowsDetails details = player.getBarrowsDetails();
		details.setCryptKillCount(details.getCryptKillCount() + 1);
		details.setCryptCombatKill(details.getCryptCombatKill() + npc.getDefinition().getCombatLevel());
		BarrowsHandler.getSingleton().updateOverlayInterface(player);
 	}

}

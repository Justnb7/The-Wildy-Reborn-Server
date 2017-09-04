package com.venenatis.game.model.combat.npcs.impl.zulrah.attack;

import java.util.Random;

import com.venenatis.game.model.Projectile;
import com.venenatis.game.model.combat.data.CombatStyle;
import com.venenatis.game.model.combat.npcs.AbstractBossCombat;
import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.entity.npc.NPC;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.util.Utility;

public class RangeAndMageZulrah extends AbstractBossCombat {
	
	/**
	 * The random number generator.
	 */
	private final Random random = new Random();

	@Override
	public void execute(Entity attacker, Entity victim) {
		if (!attacker.isNPC()) {
			return;
		}
		
		// The npc instance
		NPC npc = (NPC) attacker;
		
		Player pVictim = (Player)victim;
		
		npc.setFacePlayer(true);
		
		Animation anim = Animation.create(npc.getAttackAnimation());
		attacker.playAnimation(anim);
		
		int randomHit = Utility.getRandom(npc.getDefinition().getMaxHit());
		
		CombatStyle style = CombatStyle.RANGE;
		
		switch(random.nextInt(3)) {
		case 0:
		case 1:
			style = CombatStyle.RANGE;	
			break;
		case 2:
			style = CombatStyle.MAGIC;
			break;
		}
		
		switch (style) {
		case MAGIC:
			attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), victim.getCentreLocation(), 1046, 30, 50, 100, 60, 25, 10, 48, pVictim.getProjectileLockonIndex()));
			victim.take_hit(attacker, randomHit, style).send(3);
			attacker.getCombatState().setSpellDelay(4);
			//Projectile 1046
			break;
			
		default:
		case RANGE:
			//Projectile 1044
			attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), victim.getCentreLocation(), 1044, 30, 50, 100, 60, 25, 10, 48, pVictim.getProjectileLockonIndex()));
			victim.take_hit(attacker, randomHit, style).send(3);
			break;
		}
		attacker.getCombatState().setAttackDelay(7);
	}

	@Override
	public int distance(Entity attacker) {
		return 8;
	}

	@Override
	public void dropLoot(Player player, NPC npc) {
		
	}

}
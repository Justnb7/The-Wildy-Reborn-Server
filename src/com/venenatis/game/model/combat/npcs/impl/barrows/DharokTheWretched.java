package com.venenatis.game.model.combat.npcs.impl.barrows;

import com.venenatis.game.model.combat.data.CombatStyle;
import com.venenatis.game.model.combat.npcs.AbstractBossCombat;
import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.entity.npc.NPC;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.Animation;

public class DharokTheWretched extends AbstractBossCombat {
	
	private final int DHAROK_STRENGTH = 100;

	@Override
	public void execute(Entity attacker, Entity victim) {
		if(!attacker.isNPC()) {
			return; //this should be an NPC!
		}
		
		NPC npc = (NPC)attacker;
		
		int startHp = 100;
		attacker.playAnimation(Animation.create(2066));
		double dharokMultiplier = (startHp - npc.getMaxHitpoints() / 2);
		double base = 0;
		base += 1.05D + (double) (DHAROK_STRENGTH * 121.77) * 0.00175D;
		base += (double) 121.77 * 0.09D;
		base += dharokMultiplier;
		int finalDamage = (int) base;
		attacker.getCombatState().setAttackDelay(5);
		
		victim.take_hit(attacker, finalDamage, CombatStyle.MELEE).send(1);
	}

	@Override
	public int distance(Entity attacker) {
		return 1;
	}

	@Override
	public void dropLoot(Player player, NPC npc) {
		// TODO Auto-generated method stub
		
	}

}

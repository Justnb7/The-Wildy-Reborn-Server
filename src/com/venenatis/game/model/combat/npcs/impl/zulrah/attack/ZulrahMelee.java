package com.venenatis.game.model.combat.npcs.impl.zulrah.attack;

import com.venenatis.game.model.combat.data.CombatStyle;
import com.venenatis.game.model.combat.npcs.AbstractBossCombat;
import com.venenatis.game.model.entity.Boundary;
import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.entity.npc.NPC;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.util.Location3D;
import com.venenatis.game.util.Utility;

public class ZulrahMelee extends AbstractBossCombat {

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
		
		npc.targetedLocation = new Location3D(pVictim.getX(), pVictim.getY(), pVictim.getZ());
		
		/**
		 * Zulrah
		 */
		if (npc.getId() == 2043 && pVictim.getZulrahEvent().getNpc() != null && pVictim.getZulrahEvent().getNpc().equals(npc)) {
			Boundary boundary = new Boundary(npc.targetedLocation.getX(), npc.targetedLocation.getY(), npc.targetedLocation.getX(), npc.targetedLocation.getY());
			if (!Boundary.isIn(pVictim, boundary)) {
				return;
			}
			randomHit = 20 + Utility.random(25);
		}
		
		victim.take_hit(attacker, randomHit, CombatStyle.MELEE).send(12);
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

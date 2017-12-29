package com.venenatis.game.model.combat.npcs.impl.dagannoths;

import com.venenatis.game.model.Item;
import com.venenatis.game.model.Projectile;
import com.venenatis.game.model.Skills;
import com.venenatis.game.model.combat.data.CombatStyle;
import com.venenatis.game.model.combat.npcs.AbstractBossCombat;
import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.entity.npc.NPC;
import com.venenatis.game.model.entity.npc.pet.Follower;
import com.venenatis.game.model.entity.npc.pet.Pet;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.model.masks.Graphic;
import com.venenatis.game.task.Task;
import com.venenatis.game.util.Utility;
import com.venenatis.game.world.World;
import com.venenatis.server.Server;

public class Melee_Dagganoth extends AbstractBossCombat {

	@Override
	public void execute(Entity attacker, Entity victim) {
		if(!attacker.isNPC()) {
			return; //this should be an NPC!
		}
		
		NPC npc = (NPC) attacker;
		
		Player player = (Player) victim;
		
		CombatStyle style = CombatStyle.MAGIC;
		
		int maxHit;
		int randomHit;
		int hitDelay;
		final int hit;
		
		switch(style) {
		default:
			
		case MELEE:
			Animation anim = Animation.create(npc.getAttackAnimation());
			attacker.playAnimation(anim);
			hitDelay = 1;
			maxHit = Utility.random(8);
			victim.take_hit(attacker, maxHit, style).send(hitDelay);
			break;
		}
		
		attacker.getCombatState().setAttackDelay(6);
	}

	@Override
	public int distance(Entity attacker) {
		return 1;
	}

	@Override
	public void dropLoot(Player player, NPC npc) {
	
	}
}

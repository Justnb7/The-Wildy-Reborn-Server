package com.venenatis.game.model.combat.npcs.impl;

import com.venenatis.game.model.Projectile;
import com.venenatis.game.model.combat.data.CombatStyle;
import com.venenatis.game.model.combat.npcs.AbstractBossCombat;
import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.entity.npc.NPC;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.task.Task;
import com.venenatis.game.util.Utility;
import com.venenatis.game.world.World;

public class DagannothMother extends AbstractBossCombat {
	
	private final int MELEE_FORM = 6361;
	
	private final int RANGE_FORM = 6365;
	
	private final int MAGE_FORM = 6362;
	
	private void changeForm(NPC npc) {
		int roll = Utility.getRandom(30);
		if (npc.getId() == MELEE_FORM) {
			if (roll >= 0 && roll <= 15) {
				npc.requestTransform(RANGE_FORM);
			} else {
				npc.requestTransform(MAGE_FORM);
			}
		} else if (npc.getId() == RANGE_FORM) {
			if (roll >= 0 && roll <= 15) {
				npc.requestTransform(MELEE_FORM);
			} else {
				npc.requestTransform(MAGE_FORM);
			}
		} else if (npc.getId() == MAGE_FORM) {
			if (roll >= 0 && roll <= 15) {
				npc.requestTransform(MELEE_FORM);
			} else {
				npc.requestTransform(RANGE_FORM);
			}
		}

	}

	@Override
	public void execute(Entity attacker, Entity victim) {
		if(!attacker.isNPC()) {
			return; //this should be an NPC!
		}
		
		NPC npc = (NPC) attacker;
        int maxHit = 50;
        int clientSpeed;
		int gfxDelay;
		npc.playAnimation(Animation.create(npc.getAttackAnimation()));
		
		if (!npc.hasAttribute("trans")) {
			Task t;
			World.getWorld().schedule(t = new Task(25, false) {
				@Override
				public void execute() {
					if (npc.getIndex() == -1 || npc.getHitpoints() < 1 || npc.getCombatState().isDead()) {
						this.stop(); // only stop if dead otherwise keep going forever
						return;
					}
					changeForm(npc);
				}
			});
			npc.setAttribute("trans", t);
		}
		
		
		switch (npc.getId()) {
        case MELEE_FORM:
			int randomHit = Utility.getRandom(maxHit);
			victim.take_hit(attacker, randomHit, CombatStyle.MELEE).send();
			break;
		case RANGE_FORM:
			randomHit = Utility.random(maxHit);
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
			npc.playProjectile(Projectile.create(npc.getCentreLocation(), victim, 856, 25, 50, clientSpeed, 43, 36, 10, 48));
			victim.getActionSender().stillGfx(755, victim.getLocation());
			victim.take_hit(attacker, randomHit, CombatStyle.RANGE).send(delay);
			break;
		case MAGE_FORM:
			randomHit = Utility.random(maxHit);
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
			delay = (gfxDelay / 20) - 1;
			npc.playProjectile(Projectile.create(npc.getCentreLocation(), victim, 986, 25, 50, clientSpeed, 43, 36, 10, 48));
			victim.getActionSender().stillGfx(775, victim.getLocation());
			victim.take_hit(attacker, randomHit, CombatStyle.MAGIC).send(delay);
			break;
		default:
			break;
        }
		
		attacker.getCombatState().setAttackDelay(npc.getDefinition().getAttackSpeed());
	}

	@Override
	public int distance(Entity attacker) {
		return 10;
	}

	@Override
	public void dropLoot(Player player, NPC npc) {
		//This is a special death case
	}

}

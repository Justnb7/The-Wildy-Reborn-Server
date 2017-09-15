package com.venenatis.game.model.combat.npcs.impl.wilderness;

import java.util.Random;

import com.venenatis.game.model.combat.data.CombatStyle;
import com.venenatis.game.model.combat.npcs.AbstractBossCombat;
import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.entity.npc.NPC;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.equipment.PoisonType;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.task.Task;
import com.venenatis.game.task.impl.NPCDeathTask;
import com.venenatis.game.util.Utility;
import com.venenatis.game.world.World;

public class Scorpia extends AbstractBossCombat {
	
	/**
	 * The random number generator.
	 */
	private final Random random = new Random();

	@Override
	public void execute(Entity attacker, Entity victim) {
		if (!attacker.isNPC()) {
            return; //this should be an NPC!
        }
		
		NPC npc = (NPC)attacker;
		
		int randomHit = Utility.random(16);
		attacker.playAnimation(Animation.create(npc.getAttackAnimation()));
		
		/*if(victim.getPoisonDamage().get() < 1 && random.nextInt(10) < 7 && !victim.isPoisoned()) {
			victim.poison(PoisonType.SCORPIA, attacker);
		}*/
		
		victim.take_hit(attacker, randomHit, CombatStyle.MELEE).send();
		
		attacker.getCombatState().setAttackDelay(6);
	}

	@Override
	public int distance(Entity attacker) {
		return 3;
	}

	@Override
	public void dropLoot(Player player, NPC npc) {
		
	}
	
	public static void heal_scorpia(NPC boss, NPC minion) {
		minion.faceEntity(boss);
		minion.setFollowing(boss);
		World.getWorld().schedule(new Task(2) {
			@Override
			public void execute() {
				// stop the task when it's no longer valid
				if (!minion.isVisible() || minion.getCombatState().isDead() || boss.getCombatState().isDead() || !boss.hasAttribute("scorpia_minion")) {
					minion.despawn();
					this.stop();
					return;
				}
				/*if (minion.distanceToPoint(boss.getX(), boss.getY()) > 10) {
					despawn(minion);
					this.stop();
					return;
				}*/
				//security, then heal
				if (minion.getId() == 6617 && !minion.getCombatState().isDead()) {
					for (Player player : World.getWorld().getPlayers()) {
						if (player == null) 
							continue;
						
						int pX = minion.getX(); // start
						int pY = minion.getY();
						int oX = boss.getX(); // end
						int oY = boss.getY();
						int offX = (pY - oY) * -1;
						int offY = (pX - oX) * -1;
						player.getActionSender().shoot(pY, pX, offY, offX, 109, 53, 31, 100, boss);
					}
					// heal the boss
					if (boss.getId() == 6615 && boss.getHitpoints() < boss.getMaxHitpoints()) {
						boss.setHitpoints(boss.getHitpoints() + 1);
						//minion.forceChat("healed");
					}
				}
			}
		});
	}

}

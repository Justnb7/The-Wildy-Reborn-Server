package com.model.game.character.npc.combat.combat_scripts;

import com.model.Server;
import com.model.game.World;
import com.model.game.character.combat.PrayerHandler.Prayers;
import com.model.game.character.npc.Npc;
import com.model.game.character.npc.combat.Boss;
import com.model.game.character.player.Player;
import com.model.task.ScheduledTask;
import com.model.task.impl.NPCDeathTask;
import com.model.utility.Utility;

public class Scorpia extends Boss {

	public Scorpia(int npcId) {
		super(npcId);
	}

	@Override
	public void execute(Npc npc, Player player) {
		npc.attackStyle = 0;

		int poisonDamage = 1 + Utility.getRandom(19);
		if (poisonDamage > 0 && player.isSusceptibleToPoison() && Utility.getRandom(10) == 1) {
			player.setPoisonDamage((byte) poisonDamage);
		}

	}
	
	@Override
	public int getProtectionDamage(Player player, int damage) {
		if (player.isActivePrayer(Prayers.PROTECT_FROM_MELEE)) {
			return damage /2;
		}
		return damage;
	}

	@Override
	public int getMaximumDamage(int attackType) {
		return 16;
	}

	@Override
	public int getAttackEmote(Npc npc) {
		return npc.getDefinition().getAttackAnimation();
	}

	@Override
	public int getAttackDelay(Npc npc) {
		return npc.getDefinition().getAttackSpeed();
	}

	@Override
	public int getHitDelay(Npc npc) {
		return 4;
	}

	@Override
	public boolean canMultiAttack(Npc npc) {
		return false;
	}

	@Override
	public boolean switchesAttackers() {
		return true;
	}

	@Override
	public int distanceRequired(Npc npc) {
		return 2;
	}

	@Override
	public int offSet(Npc npc) {
		return 0;
	}

	@Override
	public boolean damageUsesOwnImplementation() {
		return false;
	}

	public static void heal_scorpia(Npc boss, Npc minion) {
		minion.faceEntity(boss);
		minion.setFollowing(null);
		Server.getTaskScheduler().schedule(new ScheduledTask(2) {
			@Override
			public void execute() {
				// stop the task when it's no longer valid
				if (!minion.isVisible() || minion.isDead || boss.isDead || !boss.spawnedScorpiaMinions) {
					despawn(minion);
					this.stop();
					return;
				}
				if (minion.distanceToPoint(boss.getX(), boss.getY()) > 10) {
					despawn(minion);
					this.stop();
					return;
				}
				//security, then heal
				if (minion.npcId == 6617 && !minion.isDead) {
					for (Player player : World.getWorld().getPlayers()) {
						if (player == null) 
							continue;
						
						int pX = minion.getX(); // start
						int pY = minion.getY();
						int oX = boss.getX(); // end
						int oY = boss.getY();
						int offX = (pY - oY) * -1;
						int offY = (pX - oX) * -1;
						player.getProjectile().shoot(pY, pX, offY, offX, 109, 53, 31, 100, boss);
					}
					// heal the boss
					if (boss.npcId == 6615 && boss.currentHealth < boss.maximumHealth) {
						boss.currentHealth++;
						//minion.forceChat("healed");
					}
				}
			}
		});
	}
	
	public static void despawn(Npc n) {
		if (!n.isVisible()) {
			// already despawned
			return;
		}
		NPCDeathTask.reset(n);
        n.removeFromTile();
        NPCDeathTask.setNpcToInvisible(n);
	}
}

package com.model.game.character.npc.combat.combat_scripts;

import com.model.game.character.Animation;
import com.model.game.character.Hit;
import com.model.game.character.HitType;
import com.model.game.character.npc.NPCHandler;
import com.model.game.character.npc.Npc;
import com.model.game.character.npc.combat.Boss;
import com.model.game.character.npc.combat.ProtectionPrayer;
import com.model.game.character.player.Player;
import com.model.task.events.CycleEvent;
import com.model.task.events.CycleEventContainer;
import com.model.task.events.CycleEventHandler;
import com.model.task.impl.NPCDeathTask;
import com.model.utility.Utility;

public class Lizardman extends Boss {

	public Lizardman(int npcId) {
		super(npcId);
	}

	@Override
	public void execute(Npc npc, Player player) {
		int roll = Utility.random(100);
		System.out.println(roll);
		int offX = (npc.getY() - player.getY()) * -1;
		int offY = (npc.getX() - player.getX()) * -1;
		if (roll >= 0 && roll < 45) {
			npc.attackStyle = 0;
		} else if(roll >= 45 && roll <= 75) {
			player.getProjectile().createPlayersProjectile(npc.getX() + 1, npc.getY() + 1, offX, offY, 50, 75, 1293, 31, 31, -player.getId() - 1, 55, 0);
			npc.attackStyle = 2;
			npc.endGfx = 1294;
		} else if (roll > 90) {
			minions(npc, player);
			System.out.println("minion");
		} else if (roll > 75 && roll < 90) {
			System.out.println("jump");
			jump(npc, player);
		}
	}

	@Override
	public int getProtectionDamage(ProtectionPrayer protectionPrayer, int damage) {
		return damage / 2;
	}

	@Override
	public int getMaximumDamage(int attackType) {
		return attackType == 0 ? 31 : attackType == 1 ? 10 : 15;
	}

	@Override
	public int getAttackEmote(Npc npc) {
		return npc.attackStyle == 0 ? 7192 : 7193;
	}

	@Override
	public int getAttackDelay(Npc npc) {
		return 5;
	}

	@Override
	public int getHitDelay(Npc npc) {
		return npc.attackStyle == 0 ? 3 : 4;
	}

	@Override
	public boolean canMultiAttack(Npc npc) {
		return false;
	}

	@Override
	public boolean switchesAttackers() {
		return false;
	}

	@Override
	public int distanceRequired(Npc npc) {
		return 5;
	}

	@Override
	public int offSet(Npc npc) {
		return 0;
	}

	@Override
	public boolean damageUsesOwnImplementation() {
		return false;
	}
	
	public static void jump(Npc npc, Player player) {
		try {
			CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {

				@Override
				public void execute(CycleEventContainer container) {
					if (npc != null) {
						npc.playAnimation(Animation.create(7152));
						npc.storeLastHealth = npc.currentHealth;
						player.lastX = player.getX();
						player.lastY = player.getY();
					}
					container.stop();
				}
				public void stop() {
					endJump(npc, player);
					
				}
			}, 10);
		} catch (Exception e) {
		}
	}
	
	public static void endJump(Npc npc, Player player) {
		try {
			if(player == null) {
				npc.resetCombat();
				return;
			}
			player.lastX = player.getX();
			player.lastY = player.getY();
			CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {

				@Override
				public void execute(CycleEventContainer container) {
					if (npc != null) {
						npc.playAnimation(Animation.create(6946));
						npc.absX = player.lastX;
						npc.absY = player.lastY;
						npc.heightLevel = player.heightLevel;
						npc.updateRequired = true;
						if (player.absX == player.lastX || player.absY == player.lastY) {
							player.damage(new Hit(Utility.random(30), HitType.NORMAL));
							player.message("The Lizard has fallen from the sky and landed on you!");
						} else {
							player.message("You only just avoided the Lizard Shaman!");
						}
					}
					container.stop();
				}
			}, 5);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void minions(Npc npc, Player player) {
		if (player == null) {
			return;
		}
		int x = player.getX() + 2;
		int y = player.getY();
		NPCHandler.spawnNpc(player, 6768, x, y, 0, 0, 100, 10, 300, 0, 0, 0, true, false);
		applyDamage(npc, player);
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
	
	public static void applyDamage(Npc npc, Player player) {
		try {
			CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {

				@Override
				public void execute(CycleEventContainer container) {
					if (npc != null) {
						player.getProjectile().createPlayersStillGfx(305, npc.getX(), npc.getY(), 0, 1);
					}
					container.stop();
				}

				public void stop() {
					if (player.goodDistance(player.getX(), player.getY(), npc.getX(), npc.getY(), 4)) {
						player.damage(new Hit(Utility.random(35), HitType.NORMAL));
						despawn(npc);
					}
				}
			}, 6);
		} catch (Exception e) {

		}
	}

}

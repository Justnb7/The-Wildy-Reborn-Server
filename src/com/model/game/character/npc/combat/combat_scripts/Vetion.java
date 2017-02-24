package com.model.game.character.npc.combat.combat_scripts;

import java.util.ArrayList;

import com.model.Server;
import com.model.game.character.combat.nvp.NPCCombatData;
import com.model.game.character.npc.Npc;
import com.model.game.character.npc.combat.Boss;
import com.model.game.character.npc.combat.ProtectionPrayer;
import com.model.game.character.player.Player;
import com.model.task.ScheduledTask;
import com.model.utility.Utility;

public class Vetion extends Boss {

	public Vetion(int npcId) {
		super(npcId);
	}

	@Override
	public void execute(Npc npc, Player player) {
		int chance = Utility.getRandom(100);
		if (chance < 25) {
			npc.attackStyle = 2;
			npc.attackTimer = 7;
			npc.hitDelayTimer = 4;
			createVetionSpell(npc, player);
		} else if (chance > 90 && System.currentTimeMillis() - npc.lastSpecialAttack > 15_000) {
			npc.attackStyle = 4;
			npc.attackTimer = 5;
			npc.hitDelayTimer = 2;
			npc.lastSpecialAttack = System.currentTimeMillis();
		} else {
			npc.attackStyle = 0;
			npc.attackTimer = 5;
			npc.hitDelayTimer = 2;
		}
	}

	@Override
	public int getProtectionDamage(ProtectionPrayer protectionPrayer, int damage) {
		switch (protectionPrayer) {
		case MELEE:
		case MAGE:
			return 0;
		case RANGE:
			break;
		default:
			break;
		}
		
		return damage;
	}

	@Override
	public int getMaximumDamage(int attackType) {
		return attackType == 0 ? 30 : attackType == 2 ? 34 : 46;
	}

	@Override
	public int getAttackEmote(Npc npc) {
		return npc.getDefinition().getAttackAnimation();
	}

	@Override
	public int getAttackDelay(Npc npc) {
		return npc.attackStyle == 5 ? 6 : 5;
	}

	@Override
	public int getHitDelay(Npc npc) {
		return npc.attackStyle == 2 ? 3 : 2;
	}

	@Override
	public boolean canMultiAttack(Npc npc) {
		return npc.attackStyle == 2 || npc.attackStyle == 4 ? true : false;
	}

	@Override
	public boolean switchesAttackers() {
		return true;
	}

	@Override
	public int distanceRequired(Npc npc) {
		return npc.attackStyle == 4 ? 8 : 3;
	}
	
	@Override
	public int offSet(Npc npc) {
		return 3;
	}
	
    public static ArrayList<int[]> vetionSpellCoordinates = new ArrayList<>(3);
	
	private static void createVetionSpell(Npc npc, Player player) {
		if (player == null) {
			return;
		}
		int x = player.getX();
		int y = player.getY();
		vetionSpellCoordinates.add(new int[] {x, y});
		for (int i = 0; i < 2; i++) {
			vetionSpellCoordinates.add(new int[] {(x - 1) + Utility.getRandom(3), (y - 1) + Utility.getRandom(3)});
		}
		for (int[] point : vetionSpellCoordinates) {
			int nX = npc.absX + 2;
			int nY = npc.absY + 2;
			int x1 = point[0] + 1;
			int y1 = point[1] + 2;
			int offY = (nX - x1) * -1;
			int offX = (nY - y1) * -1;
			player.getProjectile().createPlayersProjectile(nX, nY, offX, offY, 40, NPCCombatData.getProjectileSpeed(npc), 280, 31, 0, -1, 5);
			
		}
		Server.getTaskScheduler().schedule(new ScheduledTask(4) {
			@Override
			public void execute() {
				for (int[] point : vetionSpellCoordinates) {
					int x2 = point[0];
					int y2 = point[1];
					player.getProjectile().createPlayersStillGfx(281, x2, y2, 0, 5);
				}
				vetionSpellCoordinates.clear();
				this.stop();
			}
		});
	}


	@Override
	public boolean damageUsesOwnImplementation() {
		return false;
	}
}
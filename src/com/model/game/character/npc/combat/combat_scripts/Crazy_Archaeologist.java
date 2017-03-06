package com.model.game.character.npc.combat.combat_scripts;

import java.util.ArrayList;
import java.util.Random;

import com.model.Server;
import com.model.game.character.combat.PrayerHandler.Prayers;
import com.model.game.character.npc.Npc;
import com.model.game.character.npc.combat.Boss;
import com.model.game.character.player.Player;
import com.model.task.ScheduledTask;
import com.model.utility.Utility;

public class Crazy_Archaeologist extends Boss {
	
	private static Random r = new Random();

	public Crazy_Archaeologist(int npcId) {
		super(npcId);
	}
	
	private static final String[] MESSAGES = { "You belong in a museum!", "I'm Bellock - respect me!", "These ruins are mine!",
			"Get off my site!",
			"Taste my knowledge!",
			"No-one messes with Bellock's dig!" };

	@Override
	public void execute(Npc npc, Player player) {

		npc.forceChat(MESSAGES[(int) (Math.random() * MESSAGES.length)]);
		npc.forcedChatRequired = true;
		npc.updateRequired = true;
		
		int chance = r.nextInt(40);
		int offX = (npc.getY() - player.getY()) * -1;
		int offY = (npc.getX() - player.getX()) * -1;
		int distance = npc.distanceTo(player.getX(), player.getY());
		int speed = 75 + (distance * 5);
		
		if (chance < 25) {
			npc.attackStyle = 1;
			player.getProjectile().createPlayersProjectile(npc.getX(), npc.getY(), offX, offY, 50, speed, 1259, 43, 31, -player.getId() - 1, 65, 0, 36);
		} else if (chance > 25 && chance < 40) {
			npc.forceChat("Rain of knowledge!");
			npc.forcedChatRequired = true;
			npc.updateRequired = true;
			npc.attackStyle = 2;
			npc.attackTimer = 7;
			npc.hitDelayTimer = 4;
			npc.endGfx = -1;
			createSpell(npc, player);
		}
	}

	@Override
	public int getProtectionDamage(Player player, int damage) {
		if (player.isActivePrayer(Prayers.PROTECT_FROM_MISSILE)) {
			return damage /2;
		} else if (player.isActivePrayer(Prayers.PROTECT_FROM_MAGIC)) {
			return damage /2;
		}
		return damage;
	}

	@Override
	public int getMaximumDamage(int attackType) {
		return 31;
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
		return npc.attackStyle == 1 ? 4 : 5;
	}

	@Override
	public boolean canMultiAttack(Npc npc) {
		if (npc.attackStyle == 2) {
			return true;
		}
		return false;
	}

	@Override
	public boolean switchesAttackers() {
		return true;
	}

	@Override
	public int distanceRequired(Npc npc) {
		return 6;
	}
	
	@Override
	public int offSet(Npc npc) {
		return 3;
	}
	
    public static ArrayList<int[]> spell_coordinates = new ArrayList<>(3);
	
	private static void createSpell(Npc npc, Player player) {
		if (player == null) {
			return;
		}
		int x = player.getX();
		int y = player.getY();
		spell_coordinates.add(new int[] {x, y});
		for (int i = 0; i < 2; i++) {
			spell_coordinates.add(new int[] {(x - 1) + Utility.getRandom(3), (y - 1) + Utility.getRandom(3)});
		}
		for (int[] point : spell_coordinates) {
			int nX = npc.absX + 2;
			int nY = npc.absY + 2;
			int x1 = point[0] + 1;
			int y1 = point[1] + 2;
			int offY = (nX - x1) * -1;
			int offX = (nY - y1) * -1;
			player.getProjectile().createPlayersProjectile(nX, nY, offX, offY, 50, 95, 1260, 43, 0, 0, 45, 30, 36);
			
		}
		Server.getTaskScheduler().schedule(new ScheduledTask(1) {
			@Override
			public void execute() {
				for (int[] point : spell_coordinates) {
					int x2 = point[0];
					int y2 = point[1];
					player.getProjectile().createPlayersStillGfx(157, x2, y2, 0, 95);
				}
				spell_coordinates.clear();
				this.stop();
			}
		});
	}


	@Override
	public boolean damageUsesOwnImplementation() {
		return false;
	}
}
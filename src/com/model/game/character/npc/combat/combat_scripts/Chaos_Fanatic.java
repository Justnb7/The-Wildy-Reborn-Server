package com.model.game.character.npc.combat.combat_scripts;

import java.util.ArrayList;
import java.util.Random;

import com.model.Server;
import com.model.game.character.Graphic;
import com.model.game.character.npc.Npc;
import com.model.game.character.npc.combat.Boss;
import com.model.game.character.npc.combat.ProtectionPrayer;
import com.model.game.character.player.Player;
import com.model.task.ScheduledTask;
import com.model.utility.Utility;

public class Chaos_Fanatic extends Boss {
	
	private static Random r = new Random();

	public Chaos_Fanatic(int npcId) {
		super(npcId);
	}
	
	private static final String[] MESSAGES = {
            "BURN!",
            "WEUGH!",
            "Develish Oxen Roll!",
            "All your wilderness are belong to them!",
            "AhehHeheuhHhahueHuUEehEahAH",
            "I shall call him squidgy and he shall be my squidgy!"};

	@Override
	public void execute(Npc npc, Player player) {

		npc.forceChat(MESSAGES[(int) (Math.random() * MESSAGES.length)]);
		npc.forcedChatRequired = true;
		npc.updateRequired = true;
		
		int chance = Utility.getRandom(40);
		int offX = (npc.getY() - player.getY()) * -1;
		int offY = (npc.getX() - player.getX()) * -1;
		if (chance < 25) {
			npc.attackStyle = 2;
			player.getProjectile().createPlayersProjectile(npc.getX(), npc.getY(), offX, offY, 50, 65, 554, 43, 31, -player.getId() - 1, 45, 0, 36);
		} else if (chance > 25 && chance < 35) {
			npc.attackStyle = 2;
			npc.attackTimer = 7;
			npc.hitDelayTimer = 4;
			createChaosFanaticSpell(npc, player);
		} else {
			player.playGraphics(Graphic.create(552, 0, 100));
			int itemToRemove = r.nextInt(player.playerEquipment().length - 1);
			if (player.playerEquipment()[itemToRemove] > 0 && player.getItems().getFreeSlots() > 0) {
				player.getItems().removeEquipment(player.playerEquipment()[itemToRemove], itemToRemove);
			}
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
		return 31;
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
		return npc.attackStyle == 2 ? 4 : npc.attackStyle == 4 ? 5 : 4;
	}

	@Override
	public boolean canMultiAttack(Npc npc) {
		return npc.attackStyle == 4 ? true : false;
	}

	@Override
	public boolean switchesAttackers() {
		return false;
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
	
	private static void createChaosFanaticSpell(Npc npc, Player player) {
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
			player.getProjectile().createPlayersProjectile(nX, nY, offX, offY, 50, 95, 551, 43, 0, 0, 45, 30, 36);
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
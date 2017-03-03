package com.model.game.character.npc.combat.combat_scripts;

import com.model.game.character.Animation;
import com.model.game.character.combat.Combat;
import com.model.game.character.combat.PrayerHandler.Prayers;
import com.model.game.character.npc.Npc;
import com.model.game.character.npc.combat.Boss;
import com.model.game.character.player.Player;
import com.model.game.character.player.packets.out.SendMessagePacket;
import com.model.task.events.CycleEvent;
import com.model.task.events.CycleEventContainer;
import com.model.task.events.CycleEventHandler;
import com.model.utility.Utility;

public class Callisto extends Boss {

	public Callisto(int npcId) {
		super(npcId);
	}

	@Override
	public void execute(Npc npc, Player player) {
        int chance = Utility.getRandom(20);
        boolean distance = !player.goodDistance(npc.absX, npc.absY, player.getX(), player.getY(), 5);
        //System.out.println(chance);
        if (chance <= 13 && !distance) {
            npc.attackStyle = 0;
        } else if (chance > 13 && chance <= 20 || distance) {
        	npc.attackStyle = 2;
        	int offX = (npc.getY() - player.getY()) * -1;
            int offY = (npc.getX() - player.getX()) * -1;
            player.getProjectile().createPlayersProjectile(npc.getX() + 1, npc.getY() + 1, offX, offY, 50, 96, 395, 43, 31, -player.getId() - 1, 66, 0);
			player.write(new SendMessagePacket("Callisto's fury sends an almighty shockwave through you."));
        } else {
        	npc.attackStyle = 4;
            callistoRoar(player, npc.absX, npc.absY);
        }
		
	}

	private static int coordsX;
	private static int coordsY;

	private static void callistoRoar(Player player, int otherX, int otherY) {
		int x = player.absX - otherX;
		int y = player.absY - otherY;
		Combat.resetCombat(player);
		player.attackDelay += 2;
		player.playAnimation(Animation.create(2109));
		coordsX = player.absX;
		coordsY = player.absY;
		if (x > 0) {
			player.setForceMovement(player.localX(), player.localY(), player.localX() + 3, player.localY(), 10, 60, 1);
			coordsX = player.absX + 3;
			coordsY = player.absY;
		} else if (x < 0) {
			player.setForceMovement(player.localX(), player.localY(), player.localX() - 3, player.localY(), 10, 60, -1);
			coordsX = player.absX - 3;
			coordsY = player.absY;
		}
		if (y > 0) {
			player.setForceMovement(player.localX(), player.localY(), player.localX(), player.localY() + 3, 10, 60, 1);
			coordsX = player.absX;
			coordsY = player.absY + 3;
		} else if (y < 0) {
			player.setForceMovement(player.localX(), player.localY(), player.localX(), player.localY() - 3, 10, 60, -1);
			coordsX = player.absX;
			coordsY = player.absY - 3;
		}

		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {

			@Override
			public void execute(CycleEventContainer container) {
				player.getPA().movePlayer(coordsX, coordsY, 0);
				container.stop();
			}

		}, 2);
	}

	@Override
	public int getProtectionDamage(Player player, int damage) {
		if (player.isActivePrayer(Prayers.PROTECT_FROM_MELEE)) {
			return 0;
		} else if (player.isActivePrayer(Prayers.PROTECT_FROM_MAGIC)) {
			return 0;
		}
		return damage;
	}

	@Override
	public int getMaximumDamage(int attackType) {
		return attackType == 0 ? 40 : attackType == 2 ? 60 : 3;
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
		return npc.attackStyle == 2 ? 5 : 2;
	}
	
	@Override
	public int offSet(Npc npc) {
		return 0;
	}

	@Override
	public boolean canMultiAttack(Npc npc) {
		if (npc.attackStyle == 4)
			return true;
		else
			return false;
	}

	@Override
	public boolean switchesAttackers() {
		return true;
	}

	@Override
	public int distanceRequired(Npc npc) {
		return 10;
	}

	@Override
	public boolean damageUsesOwnImplementation() {
		return false;
	}

}
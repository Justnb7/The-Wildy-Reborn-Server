package com.model.game.character.npc.combat.combat_scripts;

import com.model.game.character.Animation;
import com.model.game.character.npc.Npc;
import com.model.game.character.npc.combat.Boss;
import com.model.game.character.npc.combat.MobAttackType;
import com.model.game.character.npc.combat.ProtectionPrayer;
import com.model.game.character.player.Player;
import com.model.utility.Utility;

public class Zombie_Champion extends Boss {

	private final Animation ATTACK_ANIMATION = Animation.create(5581);

	public Zombie_Champion(int npcId) {
		super(npcId);
	}

	@Override
	public void execute(Npc npc, Player player) {
		int nX = npc.getX();
		int nY = npc.getY();
		int pX = player.getX();
		int pY = player.getY();
		int offX = (nY - pY) * -1;
		int offY = (nX - pX) * -1;
		int distance = npc.distanceTo(player.getX(), player.getY());
		int speed = 55 + (distance * 5);
		
		//if player is 1 tile away from the Zombies champion he uses melee
		if (player.distanceToPoint(npc.absX, npc.absY) < 2) {
			npc.attackStyle = MobAttackType.MELEE;
			//player.write(new SendMessagePacket("[Debug] Melee distance, using melee attack."));
		} 
		// if more then 3 tiles way, uses ranged or magic attacks.
		else if (player.distanceToPoint(npc.absX, npc.absY) > 2) {
			int magic_or_range = Utility.random(25);
			if(magic_or_range > 10 && magic_or_range < 20) {
				npc.attackStyle = MobAttackType.RANGE;
				player.getProjectile().createPlayersProjectile(nX, nY, offX, offY, 50, speed, 60, 53, 33, -player.getId() - 1, 45, 15, 36);
				npc.endGfx = 1180;
				//player.write(new SendMessagePacket("[Debug] Range dinstance, using range attack."));
			} else {
				npc.attackStyle = MobAttackType.MAGIC;
				player.getProjectile().createPlayersProjectile(nX, nY, offX, offY, 50, speed, 1155, 53, 33, -player.getId() - 1, 45, 15, 36);
				npc.endGfx = 1154;
				//player.write(new SendMessagePacket("[Debug] Magic dinstance, using magic attack."));
			}
		}
	}

	@Override
	public int getProtectionDamage(ProtectionPrayer protectionPrayer, int damage) {
		
		//Damage will be decreased by 50% when using protection prayers.
		System.out.println("reduce damage by 50%");
		return damage / 2;
	}

	@Override
	public int getMaximumDamage(int attackType) {
		//Melee attacks can hit up to 40, range up to 50 and magic 45.
		return attackType == 0 ? 40 : attackType == 1 ? 50 : 45;
	}

	@Override
	public int getAttackEmote(Npc npc) {
		return ATTACK_ANIMATION.getId();
	}

	@Override
	public int getAttackDelay(Npc npc) {
		return npc.attackStyle == 0 ? 4 : npc.attackStyle == 1 ? 6 : 5;
	}

	@Override
	public int getHitDelay(Npc npc) {
		return npc.attackStyle == 0 ? 2 : 4;
	}

	@Override
	public boolean canMultiAttack(Npc npc) {
		//Range and magic attacks will hit multiple targets.
		if(npc.attackStyle == 1 || npc.attackStyle == 2) {
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
		return 25;
	}

	@Override
	public int offSet(Npc npc) {
		return 0;
	}

	@Override
	public boolean damageUsesOwnImplementation() {
		return false;
	}

}

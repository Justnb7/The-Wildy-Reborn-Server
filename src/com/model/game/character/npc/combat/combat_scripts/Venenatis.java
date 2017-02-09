package com.model.game.character.npc.combat.combat_scripts;

import com.model.game.character.Graphic;
import com.model.game.character.npc.Npc;
import com.model.game.character.npc.combat.Boss;
import com.model.game.character.npc.combat.ProtectionPrayer;
import com.model.game.character.player.Player;
import com.model.game.character.player.Skills;
import com.model.utility.Utility;

public class Venenatis extends Boss {

	public Venenatis(int npcId) {
		super(npcId);
	}

	@Override
	public void execute(Npc npc, Player player) {
		int chance = Utility.getRandom(150);
		int poisonChance = Utility.getRandom(20);
		int prayerAttack = Utility.getRandom(20);
		int offX = (npc.getY() - player.getY()) * -1;
		int offY = (npc.getX() - player.getX()) * -1;
        boolean distance = !player.goodDistance(npc.absX, npc.absY, player.getX(), player.getY(), 3);
        if (chance < 50 && !distance) {
            npc.attackStyle = 0;
            //player.sendGameMessage("[Debug]: Venenatis melee attack player is in melee range.");
            npc.projectileId = -1;
        } else if (chance >= 50 && chance <= 140 || distance) {
			npc.attackStyle = 2;
			npc.playGraphics(Graphic.create(164, 0, 100));
			player.getProjectile().createPlayersProjectile(npc.getX() + 1, npc.getY() + 1, offX, offY, 50, 96, 165, 43, 31, -player.getId() - 1, 66, 0);
			player.playGraphics(Graphic.create(166, 80, 0));
			//player.sendGameMessage("[Debug]: Venenatis magic attack because player is out of range.");
		} else if (chance >= 140 && chance <= 150 || distance) {
			npc.attackStyle = 4;
			npc.playGraphics(Graphic.create(164, 0, 100));
            player.getProjectile().createPlayersProjectile(npc.getX() + 1, npc.getY() + 1, offX, offY, 50, 96, 165, 43, 31, -player.getId() - 1, 66, 0);
			player.playGraphics(Graphic.create(166, 80, 0));
			//player.sendGameMessage("[Debug]: Venenatis performed his deadly special attack.");
		}
        if (poisonChance >= 15 && poisonChance <= 20 && npc.attackStyle == 2) {
        	player.setPoisonDamage((byte) 8);
        	//player.sendGameMessage("[Debug]: Poison was supported you are now poisoned.");
        }
        if (prayerAttack >= 15 && prayerAttack <= 20) {
        	int current = player.getSkills().getLevel(Skills.PRAYER);
        	int newLevel = current / 2;
        	player.getSkills().setLevel(Skills.PRAYER, newLevel);
        	//player.sendGameMessage("[Debug]: Prayer attack performed.");
        }
	}

	@Override
	public int getProtectionDamage(ProtectionPrayer protectionPrayer, int damage) {
		switch (protectionPrayer) {
		case MAGE:
			return damage *= .7;
		case MELEE:
			return damage *= .7;
		case RANGE:
			break;
		default:
			break;
		
		}
		return damage;
	}

	@Override
	public int getMaximumDamage(int attackType) {
		return attackType == 2 ? 30 : attackType == 0 ? 30 : 50;
	}

	@Override
	public int getAttackEmote(Npc npc) {
		if (npc.attackStyle == 2) {
			return 5322;
		} else {
			return npc.getDefinition().getAttackAnimation();
		}
	}

	@Override
	public int getAttackDelay(Npc npc) {
		return npc.getDefinition().getAttackSpeed();
	}

	@Override
	public int getHitDelay(Npc npc) {
		return npc.attackStyle == 2 ? 4 : 2;
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
		return 8;
	}

	@Override
	public int offSet(Npc npc) {
		return 2;
	}

	@Override
	public boolean damageUsesOwnImplementation() {
		return false;
	}

}
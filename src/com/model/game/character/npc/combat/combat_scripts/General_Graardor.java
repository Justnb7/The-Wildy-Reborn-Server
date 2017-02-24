package com.model.game.character.npc.combat.combat_scripts;

import java.util.Random;

import com.model.game.character.npc.Npc;
import com.model.game.character.npc.combat.Boss;
import com.model.game.character.npc.combat.ProtectionPrayer;
import com.model.game.character.player.Player;
import com.model.game.character.player.Skills;
import com.model.utility.Utility;

public class General_Graardor extends Boss {

	public General_Graardor(int npcId) {
		super(npcId);
	}
	
	private static Random r = new Random();
	
	private final String[] MESSAGES = { "CHAAARGE!", "Death to our enemies!",
			"Split their skulls!", "Break their bones!",
			"Crush them underfoot!", "All glory to Bandos!", "GRRRAAAAAR!",
			"FOR THE GLORY OF THE BIG HIGH WAR GOD!",
			"For the glory of Bandos!", "Brargh!",
			"We feast on the bones of our enemies tonight!" };

	@Override
	public void execute(Npc npc, Player player) {
		if (player.getSkills().getLevel(Skills.HITPOINTS) <= 0 || player.isDead || player == null) {
			return;
		}
		
		int randomMessage = r.nextInt(5);
		
		if (randomMessage == 1) {
			npc.forceChat(MESSAGES[(int) (Math.random() * MESSAGES.length)]);
			npc.forcedChatRequired = true;
			npc.updateRequired = true;
		}
		
		int offX = (npc.getY() - player.getY()) * -1;
		int offY = (npc.getX() - player.getX()) * -1;
		int distance = npc.distanceTo(player.getX(), player.getY());
		int speed = 75 + (distance * 5);
		int attack = Utility.getRandom(20);
		
		if (attack <= 15) {
			npc.attackStyle = 0;
		} else {
			player.getProjectile().createPlayersProjectile(npc.getX() + 1, npc.getY() + 1, offX, offY, 50, speed, 1202, 1, 0, -player.getId() - 1, 65, 0, 36);
			npc.attackStyle = 1;
		}
	}

	@Override
	public int getProtectionDamage(ProtectionPrayer protectionPrayer, int damage) {
		switch (protectionPrayer) {
		case RANGE:
		case MELEE:
			return 0;
		case MAGE:
			break;
		default:
			break;
		}
		return damage;
	}

	@Override
	public int getMaximumDamage(int attackType) {
		return attackType == 0 ? 65 : 35;
	}

	@Override
	public int getAttackEmote(Npc npc) {
		return  npc.attackStyle == 0 ? 7018 : 7021;
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
	public int getAttackDelay(Npc npc) {
		return npc.getDefinition().getAttackSpeed();
	}

	@Override
	public int getHitDelay(Npc npc) {
		return 3;
	}
	
	@Override
	public int offSet(Npc npc) {
		return 0;
	}

	@Override
	public int distanceRequired(Npc npc) {
		return  npc.attackStyle == 0 ? npc.getSize() : 6;
	}

	@Override
	public boolean damageUsesOwnImplementation() {
		return false;
	}
}
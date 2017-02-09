package com.model.game.character.npc.combat.combat_scripts;

import com.model.game.character.Graphic;
import com.model.game.character.npc.Npc;
import com.model.game.character.npc.combat.Boss;
import com.model.game.character.npc.combat.ProtectionPrayer;
import com.model.game.character.player.Player;
import com.model.utility.Utility;

public class Dragon extends Boss {

	public Dragon(int npcId) {
		super(npcId);
	}

	@Override
	public void execute(Npc npc, Player player) {
		int combatType = 0;
		if (Utility.getRandom(2) == 0) {
			npc.attackStyle = combatType = 3;
		} else {
			npc.attackStyle = combatType = 0;
		}

		if (combatType == 3) {
			npc.playGraphics(Graphic.create(1, 0, 0));
		}
	}

	@Override
	public int getProtectionDamage(ProtectionPrayer protectionPrayer, int damage) {
		return 0;
	}

	@Override
	public int getMaximumDamage(int attackType) {
		return attackType == 0 ? 12 : 65;
	}

	@Override
	public int getAttackEmote(Npc npc) {
		return npc.attackStyle == 0 ? 91: 81;
	}

	@Override
	public int getAttackDelay(Npc npc) {
		return npc.getDefinition().getAttackSpeed();
	}

	@Override
	public int getHitDelay(Npc npc) {
		return npc.attackStyle == 0 ? 2 : 4;
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
		return 3;
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

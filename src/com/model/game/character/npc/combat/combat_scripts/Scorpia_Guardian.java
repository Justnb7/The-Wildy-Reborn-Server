package com.model.game.character.npc.combat.combat_scripts;

import com.model.game.character.npc.Npc;
import com.model.game.character.npc.combat.Boss;
import com.model.game.character.player.Player;

public class Scorpia_Guardian extends Boss {

	public Scorpia_Guardian(int npcId) {
		super(npcId);
	}

	@Override
	public void execute(Npc npc, Player player) {
		npc.attackStyle = 1;
		int offX = (npc.getY() - player.getY()) * -1;
        int offY = (npc.getX() - player.getX()) * -1;
        player.getProjectile().createPlayersProjectile(npc.getX() + 1, npc.getY() + 1, offX, offY, 50, 96, 663, 1, 15, -player.getId() - 1, 16, 0);
	}

	@Override
	public int getProtectionDamage(Player player, int damage) {
		return 0;
	}

	@Override
	public int getMaximumDamage(int attackType) {
		return 4;
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
		return 30;
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

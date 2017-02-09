package com.model.game.character.npc.combat.combat_scripts;

import com.model.game.World;
import com.model.game.character.Animation;
import com.model.game.character.npc.Npc;
import com.model.game.character.npc.combat.Boss;
import com.model.game.character.npc.combat.ProtectionPrayer;
import com.model.game.character.player.Player;
import com.model.utility.Utility;
import com.model.utility.cache.map.Region;

public class Kree_Arra extends Boss {

	public Kree_Arra(int npcId) {
		super(npcId);
	}

	private static int projectileId;
	
	@Override
	public void execute(Npc npc, Player player) {
		 npc.playAnimation(Animation.create(6980));
	        int attack = Utility.getRandom(2);
	        if (attack == 0) {
	            projectileId = 1199;
	            npc.attackStyle = 0;
	        } else if (attack == 1) {
	        	npc.attackStyle = 1;
	            projectileId = 1198;
	        } else {
	            projectileId = 1200;
	            npc.attackStyle = 2;
	        }
	        for (int i = 0; i < World.getWorld().getPlayers().capacity(); i++) {
	            Player players = World.getWorld().getPlayers().get(i);
	            if (players == null)
	                continue;
	            if (Utility.doubleDistanceBetween(players, npc) < 16 && Region.canAttack(npc, players)) {
	                int offX = (npc.getY() - players.getY()) * -1;
	                int offY = (npc.getX() - players.getX()) * -1;
	                players.getProjectile().createPlayersProjectile(npc.getX() + 1, npc.getY() + 1, offX, offY, 50, 106, projectileId, 0, 0, -players.getId() - 1, 76, 0);
	            }
	        }
		
	}

	@Override
	public int getProtectionDamage(ProtectionPrayer protectionPrayer, int damage) {
		return 0;
	}

	@Override
	public int getMaximumDamage(int attackType) {
		return attackType == 0 ? 26 : attackType == 1 ? 71 : 26;
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
		return 8;
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

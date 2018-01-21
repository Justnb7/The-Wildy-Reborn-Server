package com.venenatis.game.model.combat.npcs.impl.barrows;

import com.venenatis.game.content.minigames.singleplayer.barrows.BarrowsDetails;
import com.venenatis.game.content.minigames.singleplayer.barrows.BarrowsHandler;
import com.venenatis.game.model.combat.data.CombatStyle;
import com.venenatis.game.model.combat.npcs.AbstractBossCombat;
import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.entity.npc.NPC;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.util.Utility;

public class BarrowsCryptNPC extends AbstractBossCombat {

	@Override
	public void execute(Entity attacker, Entity victim) {
		if(!attacker.isNPC()) {
			return; //this should be an NPC!
		}
		
		NPC npc = (NPC)attacker;
		
		// Each monster has a different attack animation because the range from
		// crypt npcs is between 1678 and 1688
		npc.playAnimation(Animation.create(npc.getDefinition().getAttackAnimation()));
		
		//The max hit is similar aswell
		int randomHit = Utility.random(npc.getDefinition().getMaxHit());
		
		victim.take_hit(attacker, randomHit, CombatStyle.MELEE).send(1);
		
		//And the attack speed
		attacker.getCombatState().setAttackDelay(npc.getDefinition().getAttackSpeed());
	}

	@Override
	public int distance(Entity attacker) {
		return 1;
	}

	@Override
	public void dropLoot(Player player, NPC npc) {
		BarrowsDetails details = player.getBarrowsDetails();
		details.setCryptKillCount(details.getCryptKillCount() + 1);
		details.setCryptCombatKill(details.getCryptCombatKill() + npc.getDefinition().getCombatLevel());
		BarrowsHandler.getSingleton().updateOverlayInterface(player);
	}

}
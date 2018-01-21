package com.venenatis.game.model.combat.npcs.impl.barrows;

import com.venenatis.game.content.minigames.singleplayer.barrows.BarrowsDetails;
import com.venenatis.game.content.minigames.singleplayer.barrows.BarrowsHandler;
import com.venenatis.game.model.combat.PrayerHandler;
import com.venenatis.game.model.combat.data.CombatStyle;
import com.venenatis.game.model.combat.npcs.AbstractBossCombat;
import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.entity.npc.NPC;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.util.Utility;

public class VeracTheDefiled extends AbstractBossCombat {

	@Override
	public void execute(Entity attacker, Entity victim) {
		if(!attacker.isNPC()) {
			return; //this should be an NPC!
		}
		
		Player pVictim = (Player)victim;
		
		attacker.playAnimation(Animation.create(2062));
		
		int randomHit = 0;
		if (Utility.random(3) == 3) {
			if (!PrayerHandler.isActivated(pVictim, PrayerHandler.PROTECT_FROM_MELEE) && Utility.random(10) > 6) {
				randomHit = Utility.random(25);
			} else if (PrayerHandler.isActivated(pVictim, PrayerHandler.PROTECT_FROM_MELEE) && Utility.random(10) > 3) {
				randomHit = Utility.random(25);
			}
		}
		victim.take_hit(attacker, randomHit, CombatStyle.MELEE).send(1);
		
		attacker.getCombatState().setAttackDelay(4);
		
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

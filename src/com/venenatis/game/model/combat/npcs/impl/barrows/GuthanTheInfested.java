package com.venenatis.game.model.combat.npcs.impl.barrows;

import com.venenatis.game.model.combat.PrayerHandler.Prayers;
import com.venenatis.game.model.combat.data.CombatStyle;
import com.venenatis.game.model.combat.npcs.AbstractBossCombat;
import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.entity.npc.NPC;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.model.masks.Graphic;
import com.venenatis.game.util.Utility;

public class GuthanTheInfested extends AbstractBossCombat {

	@Override
	public void execute(Entity attacker, Entity victim) {
		if(!attacker.isNPC()) {
			return; //this should be an NPC!
		}
		
		NPC npc = (NPC)attacker;
		
		Player pVictim = (Player)victim;
		
		attacker.playAnimation(Animation.create(2080));
		
		int damage = Utility.random(26);
		
		
		if (!pVictim.isActivePrayer(Prayers.PROTECT_FROM_MELEE) && Utility.random(7) == 3) {
			victim.playGraphics(Graphic.create(398, 0, 0));
			victim.getActionSender().sendMessage("Guthans heals himself...");
			npc.setHitpoints(npc.getHitpoints() + damage);
		}
		
		attacker.getCombatState().setAttackDelay(5);
		
		victim.take_hit(attacker, damage, CombatStyle.MAGIC).send(1);
	}

	@Override
	public int distance(Entity attacker) {
		return 1;
	}

	@Override
	public void dropLoot(Player player, NPC npc) {
		// TODO Auto-generated method stub
		
	}

}

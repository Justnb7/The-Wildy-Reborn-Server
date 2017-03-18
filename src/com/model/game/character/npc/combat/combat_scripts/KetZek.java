package com.model.game.character.npc.combat.combat_scripts;

import com.model.Server;
import com.model.game.character.Animation;
import com.model.game.character.Entity;
import com.model.game.character.Graphic;
import com.model.game.character.Hit;
import com.model.game.character.combat.Combat;
import com.model.game.character.combat.combat_data.CombatType;
import com.model.game.character.npc.Npc;
import com.model.game.character.npc.combat.Boss;
import com.model.game.character.player.Player;
import com.model.task.ScheduledTask;
import com.model.utility.Utility;

public class KetZek extends Boss {

	public KetZek(int npcId) {
		super(npcId);
	}

	@Override
	public void execute(Entity attacker, Entity victim) {
		
		if(!attacker.isNPC()) {
			return;
		}
		CombatType style = attacker.getPosition().distanceToEntity(attacker, victim) <= 1 ? CombatType.MELEE : CombatType.MAGIC;
		Npc npc = (Npc) attacker;
		Player vicPlayer = (Player) victim;
		
		int maxHit = style == CombatType.MAGIC ? 48 : 54;
		
		switch (style) {
		case MELEE:
			vicPlayer.debug("comb systeem melee");
			npc.playAnimation(Animation.create(npc.getDefinition().getAttackAnimation()));
			int randomHit = Utility.random(maxHit);
			
			// Set up a Hit instance
            Hit hitInfo = victim.take_hit(attacker, randomHit, CombatType.MELEE, false);

            // apply damage - call this, change the 'delay' param to whatever you want the delay to be
            // and the method submits the Event
            Combat.hitEvent(attacker, victim, 1, hitInfo, CombatType.MELEE);
			
			break;
		case MAGIC:
			vicPlayer.debug("comb systeem magic");
			npc.playAnimation(Animation.create(2647));
			int clientSpeed;
			int gfxDelay;
			if(attacker.getPosition().isWithinDistance(attacker, victim, 1)) {
				clientSpeed = 70;
				gfxDelay = 80;
			} else if(attacker.getPosition().isWithinDistance(attacker, victim, 5)) {
				clientSpeed = 90;
				gfxDelay = 100;
			} else if(attacker.getPosition().isWithinDistance(attacker, victim, 8)) {
				clientSpeed = 110;
				gfxDelay = 120;
			} else {
				clientSpeed = 130;
				gfxDelay = 140;
			}
			int offX = (attacker.getY() - victim.getY()) * -1;
			int offY = (attacker.getX() - victim.getX()) * -1;
			int delay = (gfxDelay / 20) - 1;
			vicPlayer.getProjectile().createPlayersProjectile(attacker.getX() + 1, attacker.getY() + 1, offX, offY, 50, clientSpeed, 445, 133, 31, -victim.getIndex() - 1, 76);

			// Right away: calculate a max hit.
			randomHit = Utility.random(maxHit);
			
			// Set up a Hit instance
            hitInfo = victim.take_hit(attacker, randomHit, CombatType.MAGIC, false);

            // apply damage - call this, change the 'delay' param to whatever you want the delay to be
            // and the method submits the Event
            Combat.hitEvent(attacker, victim, delay, hitInfo, CombatType.MAGIC);
            
            // now the 1 last addition which depends on the Npc is the graphic at the end
            // This can be a seperate Task
            Server.getTaskScheduler().schedule(new ScheduledTask(delay) {
				@Override
				public void execute() {
					this.stop();
					victim.playGraphics(Graphic.create(446));
				}
			});
			break;
		default:
			break;
		
		}
		((Npc)attacker).attackTimer = (style == CombatType.MAGIC ? 5 : 4);
	}

	@Override
	public int distance() {
		return 8;
	}

}

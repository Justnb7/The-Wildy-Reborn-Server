package com.venenatis.game.model.entity.following;

import com.venenatis.game.model.combat.npcs.AbstractBossCombat;
import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.entity.npc.NPC;

public class Following {

    public Following(Entity src) {
        this.src = src;
    }

    private Entity target, src;

    public void setFollowing(Entity target) {
        this.target = target;
    }

    public boolean hasFollowTarget() {
        return target != null;
    }
    public static int distanceRequired(Entity npc) {
		if (AbstractBossCombat.isBoss(npc.asNpc().getId())) {
			return AbstractBossCombat.get(npc.asNpc().getId()).distance(null);
		}
		if(npc.asNpc().getName().contains("Whirlpool"))
			return 8;
		return 1;
	}
    
	public static boolean goodDistance(int objectX, int objectY, int playerX, int playerY, int distance) {
		return ((objectX - playerX <= distance && objectX - playerX >= -distance)
				&& (objectY - playerY <= distance && objectY - playerY >= -distance));
	}
	
	
    // Do the following
    public void execute() {
        if (!hasFollowTarget())
            return;
        // We've got a target. Call the bulk of following code.
        if (src.isPlayer()) {
            PlayerFollowing.follow(src.asPlayer(), !src.asPlayer().getCombatState().noTarget(), target);
        } else {
        	
            NPCFollowing.attemptFollowEntity(src.asNpc(), target);
        } 
    }
}

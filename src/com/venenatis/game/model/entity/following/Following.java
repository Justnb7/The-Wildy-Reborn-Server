package com.venenatis.game.model.entity.following;

import com.venenatis.game.model.entity.Entity;

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

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
        if (src.isPlayer()) {
            if (!src.goodDistance(target.getX(), target.getY(), src.getX(), src.getY(), 1))
                PlayerFollowing.follow(src.asPlayer(), !src.asPlayer().getCombatState().noTarget(), target);
        } else {
            NPCFollowing.attemptFollowEntity(src.asNpc(), target);
        }
    }
}

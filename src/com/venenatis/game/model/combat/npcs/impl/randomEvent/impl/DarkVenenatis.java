package com.venenatis.game.model.combat.npcs.impl.randomEvent.impl;

import com.venenatis.game.location.Location;
import com.venenatis.game.model.entity.npc.NPC;
import com.venenatis.game.world.World;

public class DarkVenenatis extends NPC {
	
	public DarkVenenatis(Location spawn) {
		super(8017, spawn, 1);
		this.strollRange = 1;
		World.getWorld().register(this);
	}
	
}
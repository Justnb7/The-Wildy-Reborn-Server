package com.venenatis.game.util.parser.impl;

import com.google.gson.JsonObject;
import com.venenatis.game.location.Location;
import com.venenatis.game.model.entity.npc.NPC;
import com.venenatis.game.model.masks.forceMovement.Direction.FacingDirection;
import com.venenatis.game.util.parser.GsonParser;
import com.venenatis.game.world.World;

public class NpcSpawnDefinitionParser extends GsonParser {
	
	public NpcSpawnDefinitionParser() {
		super("def/mob/npc_spawns");
	}

	@Override
	public void parse(JsonObject data) {
		int id = data.get("id").getAsInt();
		int radius = data.get("radius").getAsInt();
		FacingDirection dir = builder.fromJson(data.get("direction"), FacingDirection.class);
		Location loc = builder.fromJson(data.get("position"), Location.class);
		NPC npc = new NPC(id, loc, radius);
		World.getWorld().register(npc);
	}
	
}
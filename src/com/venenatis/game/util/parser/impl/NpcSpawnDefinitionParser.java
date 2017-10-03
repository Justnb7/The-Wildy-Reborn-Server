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
		//TODO add support for facingDirection
		//int dir = 0;
		Location location = builder.fromJson(data.get("position"), Location.class);
		
		//TODO Jak add support for the enum rather then an int
		NPC npc = new NPC(id, location, 0);
		//npc.spawnDirection = dir;
		//npc.getWalkingQueue().lastDirectionFaced = dir;
		npc.setFace(dir);
		if (World.getWorld().register(npc)) {
			// successfully added to game world
			npc.handleForGroup();
		}
		World.getWorld().register(npc);
	}
	
}
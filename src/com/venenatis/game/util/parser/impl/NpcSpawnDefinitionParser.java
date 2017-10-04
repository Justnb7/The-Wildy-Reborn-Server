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
		FacingDirection dir = builder.fromJson(data.get("facing"), FacingDirection.class);
		//TODO add support for facingDirection
		Location location = builder.fromJson(data.get("position"), Location.class);

		int dirInt = 6;
		switch (dir) {
/*
NW -0
N 1
NE 2
E 4
SE 7
S 6
SW 5
W 3
 */
			case NORTH:
				dirInt = 1;
				break;
			case SOUTH:
				dirInt = 6;
				break;
			case EAST:
				dirInt = 4;
				break;
			case WEST:
				dirInt = 3;
				break;
		}

		NPC npc = new NPC(id, location, dirInt);
		npc.setFace(dir);
		npc.strollRange = radius;
		//if (id == 395)
		//	System.out.println("banker at "+location+" facing "+dir+" "+dirInt);
		if (World.getWorld().register(npc)) {
			// successfully added to game world
			npc.handleForGroup();
		}
		World.getWorld().register(npc);
	}
	
}
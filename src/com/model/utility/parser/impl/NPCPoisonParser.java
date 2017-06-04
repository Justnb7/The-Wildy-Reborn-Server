package com.model.utility.parser.impl;

import java.util.Objects;

import com.google.gson.JsonObject;
import com.model.game.character.PoisonType;
import com.model.task.impl.PoisonCombatTask;
import com.model.utility.parser.GsonParser;

/**
 * The {@link JsonLoader} implementation that loads all weapons that poison
 * players.
 * 
 * @author lare96 <http://www.rune-server.org/members/lare96/>
 */
public final class NPCPoisonParser extends GsonParser {

    /**
     * Creates a new {@link NPCPoisonParser}.
     */
    public NPCPoisonParser() {
    	super("def/misc/npc_poison.json");
    }

    @Override
	protected void parse(JsonObject data) {
		int id = data.get("npc-id").getAsInt();
        PoisonType type = Objects.requireNonNull(PoisonType.valueOf(data.get("type").getAsString()));
		PoisonCombatTask.NPC_TYPES.put(id, type);
    }
}
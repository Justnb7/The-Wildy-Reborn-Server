package com.venenatis.game.util.parser.impl;

import java.util.Objects;

import com.google.gson.JsonObject;
import com.venenatis.game.model.equipment.PoisonType;
import com.venenatis.game.task.impl.PoisonCombatTask;
import com.venenatis.game.util.parser.GsonParser;

/**
 * The {@link JsonLoader} implementation that loads all weapons that poison
 * players.
 * 
 * @author lare96 <http://www.rune-server.org/members/lare96/>
 */
public final class WeaponPoisonParser extends GsonParser {

    /**
     * Creates a new {@link WeaponPoisonParser}.
     */
    public WeaponPoisonParser() {
		super("def/misc/weapon_poison.json");
    }

    @Override
	protected void parse(JsonObject data) {
        int id = data.get("id").getAsInt();
        PoisonType type = Objects.requireNonNull(PoisonType.valueOf(data.get("type").getAsString()));
		PoisonCombatTask.WEAPON_TYPES.put(id, type);
    }
}
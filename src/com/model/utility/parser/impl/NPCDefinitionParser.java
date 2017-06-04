package com.model.utility.parser.impl;

import java.util.Objects;

import com.google.gson.JsonObject;
import com.model.game.definitions.NPCDefinitions;
import com.model.utility.parser.GsonParser;

/**
 * The {@link JsonLoader} implementation that loads all npc definitions.
 *
 * @author lare96 <http://github.com/lare96>
 */
public final class NPCDefinitionParser extends GsonParser {

    /**
     * Creates a new {@link NPCDefinitionParser}.
     */
    public NPCDefinitionParser() {
        super("def/mob/npc_definitions");
    }

    @Override
    protected void parse(JsonObject data) {
        int index = data.get("id").getAsInt();
        String name = Objects.requireNonNull(data.get("name").getAsString());
        String description = Objects.requireNonNull(data.get("examine").getAsString());
        int combatLevel = data.get("combat").getAsInt();
        int size = data.get("size").getAsInt();
        boolean attackable = data.get("attackable").getAsBoolean();
        boolean aggressive = data.get("aggressive").getAsBoolean();
        boolean retreats = data.get("retreats").getAsBoolean();
        boolean poisonous = data.get("poisonous").getAsBoolean();
        int respawnTime = data.get("respawn").getAsInt();
        int maxHit = data.get("maxHit").getAsInt();
        int hitpoints = data.get("hitpoints").getAsInt();
        int attackSpeed = data.get("attackSpeed").getAsInt();
        
        int attackAnim = data.get("attackAnim").getAsInt();
        int defenceAnim = data.get("defenceAnim").getAsInt();
        int deathAnim = data.get("deathAnim").getAsInt();
        int attackBonus = data.get("attackBonus").getAsInt();
        int meleeDefence = data.get("defenceMelee").getAsInt();
        int rangedDefence = data.get("defenceRange").getAsInt();
        int magicDefence = data.get("defenceMage").getAsInt();

        NPCDefinitions.getDefinitions()[index] = new NPCDefinitions(index, name, description, combatLevel, size, attackable, aggressive, retreats,
            poisonous, respawnTime, maxHit, hitpoints, attackSpeed, attackAnim, defenceAnim, deathAnim, attackBonus, meleeDefence,
            rangedDefence, magicDefence);
    }
}
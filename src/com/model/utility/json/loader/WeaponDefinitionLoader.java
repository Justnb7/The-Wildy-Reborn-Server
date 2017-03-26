package com.model.utility.json.loader;

import java.util.Objects;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.model.utility.json.JsonLoader;
import com.model.utility.json.definitions.WeaponAnimation;
import com.model.utility.json.definitions.WeaponDefinition;

/**
 * The {@link JsonLoader} implementation that loads all weapon definitions.
 *
 * @author <a href="http://www.rune-server.org/members/_Patrick_/">Patrick van Elderen</a>
 */
public final class WeaponDefinitionLoader extends JsonLoader {

    /**
     * Creates a new {@link WeaponDefinitionLoader}.
     */
    public WeaponDefinitionLoader() {
        super("./data/json/equipment/weapon_animations.json");
    }

    @Override
    public void load(JsonObject reader, Gson builder) {
    	final int id = reader.get("id").getAsInt();
    	final String name = reader.get("name").getAsString();
        final int attackSpeed = reader.get("attackSpeed").getAsInt();
        WeaponAnimation equipmentAnimations = Objects.requireNonNull(builder.fromJson(reader.get("equipmentAnimations"), WeaponAnimation.class));
        WeaponAnimation.ANIMATIONS.put(id, equipmentAnimations);
        final int blockAnimation = reader.get("blockAnimation").getAsInt();
        final int[] attackAnimations = reader.has("attackAnimations") ? builder.fromJson(reader.get("attackAnimations"), int[].class) : new int[] { 65535, 65535, 65535, 65535 };
       
        WeaponDefinition.getWeaponDefinitions().put(id, new WeaponDefinition(id, name, attackSpeed, equipmentAnimations, blockAnimation, attackAnimations));
    }
}
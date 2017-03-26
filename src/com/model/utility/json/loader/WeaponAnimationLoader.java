package com.model.utility.json.loader;

import java.util.Objects;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.model.utility.json.JsonLoader;
import com.model.utility.json.definitions.WeaponAnimation;

/**
 * The {@link JsonLoader} implementation that loads all weapon animations.
 *
 * @author lare96 <http://github.com/lare96>
 */
public final class WeaponAnimationLoader extends JsonLoader {

    /**
     * Creates a new {@link WeaponAnimationLoader}.
     */
    public WeaponAnimationLoader() {
        super("./data/json/equipment/weapon_animations.json");
    }

    @Override
    public void load(JsonObject reader, Gson builder) {
        int id = reader.get("id").getAsInt();
        WeaponAnimation animation = Objects.requireNonNull(builder.fromJson(reader.get("animation"), WeaponAnimation.class));
        WeaponAnimation.ANIMATIONS.put(id, animation);
    }
}
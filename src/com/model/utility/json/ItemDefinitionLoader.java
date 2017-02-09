package com.model.utility.json;

import java.util.Objects;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.model.game.item.equipment.EquipmentSlot;
import com.model.utility.json.definitions.ItemDefinition;

/**
 * The {@link JsonLoader} implementation that loads all item definitions.
 *
 * @author lare96 <http://github.com/lare96>
 */
public final class ItemDefinitionLoader extends JsonLoader {

    /**
     * Creates a new {@link ItemDefinitionLoader}.
     */
    public ItemDefinitionLoader() {
        super("./data/json/item_definitions.json");
    }

    @Override
    public void load(JsonObject reader, Gson builder) {
        int index = reader.get("id").getAsInt();
        String name = Objects.requireNonNull(reader.get("name").getAsString());
        String description = Objects.requireNonNull(reader.get("description").getAsString());
        EquipmentSlot equipmentSlot = EquipmentSlot.get(reader.get("equipment-slot").getAsString());
        boolean stackable = reader.get("stackable").getAsBoolean();
        int generalPrice = reader.get("shop-price").getAsInt();
        int highAlchValue = reader.get("high-alch").getAsInt();
        int lowAlchValue = reader.get("low-alch").getAsInt();
        int[] bonus = builder.fromJson(reader.get("bonus").getAsJsonArray(), int[].class);
        boolean twoHanded = reader.get("two-handed").getAsBoolean();
        boolean platebody = reader.get("platebody").getAsBoolean();
        boolean fullHelm = reader.get("full-helm").getAsBoolean();
        boolean fullMask = reader.has("full-mask") ? reader.get("full-mask").getAsBoolean() : false;
        boolean tradable = reader.get("tradable").getAsBoolean();
        double weight = reader.get("weight").getAsDouble();
        ItemDefinition.add(index, new ItemDefinition(index, name, description, equipmentSlot, stackable, generalPrice, lowAlchValue, highAlchValue, bonus, twoHanded, fullHelm, fullMask, platebody, tradable, weight));
    }
}
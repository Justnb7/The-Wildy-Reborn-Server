package com.model.utility.json;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.model.utility.json.definitions.Requirements;


/**
 * 
 * @author Patrick van Elderen
 * @date 19/04/2016
 *
 */
public final class RequirementsDefinitionLoader extends JsonLoader {

    /**
     * Creates a new {@link RequirementsDefinitionLoader}.
     */
    public RequirementsDefinitionLoader() {
        super("./data/json/item_requirement_definitions.json");
    }

    @Override
    public void load(JsonObject reader, Gson builder) {
        int index = reader.get("id").getAsInt();
        int[] requirements = builder.fromJson(reader.get("requirements").getAsJsonArray(), int[].class);
        Requirements.add(index, new Requirements(index, requirements));
    }
}
package com.venenatis.game.util.parser.impl;

import java.util.Objects;

import com.google.gson.JsonObject;
import com.venenatis.game.model.definitions.ItemDefinition;
import com.venenatis.game.util.parser.GsonParser;

/**
 * Parses through the item definitions file and creates {@link ItemDefinition}s
 * on startup.
 * 
 * @author SeVen
 */
public class ItemDefinitionParser extends GsonParser {

	public ItemDefinitionParser() {
		super("def/item/item_definitions");
	}

	@Override
	protected void parse(JsonObject data) {
		final int id = data.get("id").getAsInt();
		final String name = Objects.requireNonNull(data.get("name").getAsString());
		final String examine = data.get("examine").getAsString();
		final boolean noted = data.get("noted").getAsBoolean();
		final boolean noteable = data.get("noteable").getAsBoolean();
		final int parentId = data.get("parentId").getAsInt();
		final int notedId = data.get("notedId").getAsInt();
		final boolean stackable = data.get("stackable").getAsBoolean();
		final boolean destroyable = data.get("destroyable").getAsBoolean();
		final boolean tradeable = data.get("tradeable").getAsBoolean();
		final boolean members = data.get("members").getAsBoolean();
		final boolean questItem = data.get("questItem").getAsBoolean();
		final int value = data.get("value").getAsInt();
		final int highAlch = data.get("highAlch").getAsInt();
		final int lowAlch = data.get("lowAlch").getAsInt();
		final boolean equipable = data.get("equipable").getAsBoolean();
		final boolean weapon = data.get("weapon").getAsBoolean();
		final double weight = data.get("weight").getAsDouble();

		ItemDefinition.DEFINITIONS[id] = new ItemDefinition(id, name, examine, noted, noteable, parentId, notedId, stackable, destroyable, tradeable, members, questItem, value, highAlch, lowAlch, equipable, weapon, weight);
	}
}
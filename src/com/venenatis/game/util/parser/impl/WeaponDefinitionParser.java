package com.venenatis.game.util.parser.impl;

import com.google.gson.JsonObject;
import com.venenatis.game.model.combat.data.CombatStyle;
import com.venenatis.game.model.definitions.WeaponDefinition;
import com.venenatis.game.model.definitions.WeaponDefinition.RangedWeaponDefinition;
import com.venenatis.game.model.definitions.WeaponDefinition.WeaponType;
import com.venenatis.game.util.JsonSaver;
import com.venenatis.game.util.parser.GsonParser;

public class WeaponDefinitionParser extends GsonParser {

	public WeaponDefinitionParser() {
		super("def/item/weapon_definitions");
	}

	@Override
	protected void parse(JsonObject data) {
		final int id = data.get("id").getAsInt();
		final String name = data.get("name").getAsString();
		final boolean twoHanded = data.get("twoHanded").getAsBoolean();

		CombatStyle combatType = CombatStyle.MELEE;

		if (data.has("combatType")) {
			combatType = CombatStyle.valueOf(data.get("combatType").getAsString());
		}

		RangedWeaponDefinition rwd = null;

		if (data.has("rangeDefinition")) {
			rwd = builder.fromJson(data.get("rangeDefinition"), RangedWeaponDefinition.class);
		}

		WeaponType weaponType = WeaponType.DEFAULT;

		if (data.has("weaponType")) {
			weaponType = WeaponType.valueOf(data.get("weaponType").getAsString());
		}

		final int blockAnimation = data.get("blockAnimation").getAsInt();
		final int standAnimation = data.get("standAnimation").getAsInt();
		final int walkAnimation = data.get("walkAnimation").getAsInt();
		final int runAnimation = data.get("runAnimation").getAsInt();
		final int attackSpeed = data.get("attackSpeed").getAsInt();

		final int[] animations = data.has("animations") ? builder.fromJson(data.get("animations"), int[].class) : new int[] { 65535, 65535, 65535, 65535 };

		WeaponDefinition.getWeaponDefinitions().put(id, new WeaponDefinition(id, name, weaponType, combatType, rwd, twoHanded, blockAnimation, standAnimation, walkAnimation, runAnimation, attackSpeed, animations));
	}

	@SuppressWarnings("unused")
	private void write() {
		JsonSaver saver = new JsonSaver();

		for (WeaponDefinition def : WeaponDefinition.getWeaponDefinitions().values()) {

			saver.current().addProperty("id", def.getId());
			saver.current().addProperty("name", def.getName());
			saver.current().addProperty("twoHanded", def.isTwoHanded());
			saver.current().addProperty("blockAnimation", def.getBlockAnimation());
			saver.current().addProperty("standAnimation", def.getStandAnimation());
			saver.current().addProperty("walkAnimation", def.getWalkAnimation());
			saver.current().addProperty("runAnimation", def.getRunAnimation());
			saver.current().addProperty("attackSpeed", def.getAttackSpeed());
			saver.split();
		}

		saver.publish("./weapon_definitions.json");
	}

}
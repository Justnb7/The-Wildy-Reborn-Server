package com.venenatis.game.util.parser.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import com.google.gson.JsonObject;
import com.venenatis.game.content.skills.SkillData;
import com.venenatis.game.content.skills.SkillRequirement;
import com.venenatis.game.model.definitions.EquipmentDefinition;
import com.venenatis.game.model.definitions.EquipmentDefinition.EquipmentType;
import com.venenatis.game.util.JsonSaver;
import com.venenatis.game.util.parser.GsonParser;

/**
 * Parses through the equipment definitions file and creates equipment
 * definition objects.
 * 
 * @author SeVen
 */
public class EquipmentDefinitionParser extends GsonParser {

	public static final Logger logger = Logger.getLogger(EquipmentDefinitionParser.class.getName());

	public EquipmentDefinitionParser() {
		super("def/equipment/equipment_definitions");
	}

	@Override
	protected void parse(JsonObject data) {
		final int id = data.get("id").getAsInt();

		final String name = data.get("name").getAsString();

		final EquipmentType type = EquipmentType.valueOf(data.get("type").getAsString());

		SkillRequirement[] requirement = new SkillRequirement[] {};

		if (data.has("requirements")) {
			requirement = builder.fromJson(data.get("requirements"), SkillRequirement[].class);
		}

		int[] bonuses = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

		if (data.has("bonuses")) {
			bonuses = builder.fromJson(data.get("bonuses"), int[].class);
			if (bonuses.length < 14) {
				bonuses = Arrays.copyOfRange(bonuses, 0, 14);
			}
		}

		EquipmentDefinition.EQUIPMENT_DEFINITIONS.put(id, new EquipmentDefinition(id, name, type, requirement, bonuses));
	}

	public static void main(String[] args) {
		new EquipmentDefinitionParser();
		new EquipmentParser();
		List<EquipmentDefinition> toAdd = new ArrayList<>();

		
		for (EquipmentDefinition def : EquipmentDefinition.EQUIPMENT_DEFINITIONS.values()) {
			if (EquipmentParser.LOADED.get(def.getId()) == null) {
				
				String name = def.getName().toLowerCase();
				
				String first = name.split(" ")[0];
				String second = name.contains(" ") ? name.split(" ")[1] : "";
				String third = name.split(" ").length > 2 ? name.split(" ")[2] : "";
				
				int req = getReq(first, second, third, def.getType());
				
				if (req > -1) {
					toAdd.add(new EquipmentDefinition(def.getId(), def.getName(), def.getType(), new SkillRequirement[] { new SkillRequirement(req, SkillData.DEFENCE) }, def.getBonuses()));
				} else {
					System.out.println(def.getName());
					toAdd.add(def);
				}
				
				continue;
			}
			EquipmentDefinition eq = new EquipmentDefinition(def.getId(), def.getName(), def.getType(), EquipmentParser.LOADED.get(def.getId()).getReqs(), def.getBonuses());
			toAdd.add(eq);
		}
		
		toAdd.sort((first, second) -> first.getId() - second.getId());
		
		JsonSaver saver = new JsonSaver();

		for (EquipmentDefinition def : toAdd) {
			saver.current().addProperty("id", def.getId());
			saver.current().addProperty("type", def.getType().name());
			saver.current().addProperty("name", def.getName());
			saver.current().add("requirements", saver.serializer().toJsonTree(def.getRequirements()));
			saver.current().add("bonuses", saver.serializer().toJsonTree(def.getBonuses()));
			saver.split();
		}

		saver.publish("./equipment_definitions.json");
		System.err.println("Done");
	}
	
	static int getReq(String first, String second, String third, EquipmentType type) {
		switch (first) {
		case "bronze":
			if (type == EquipmentType.WEAPON || type == EquipmentType.ARROWS) {
				return -1;
			}
			return 1;
			
		case "iron":
			if (type == EquipmentType.WEAPON || type == EquipmentType.ARROWS) {
				return -1;
			}
			return 1;
			
		case "steel":
			if (type == EquipmentType.WEAPON || type == EquipmentType.ARROWS) {
				return -1;
			}
			return 10;
			
		case "mithril":
			if (type == EquipmentType.WEAPON || type == EquipmentType.ARROWS) {
				return -1;
			}
			return 20;
			
		case "adamant":
			if (type == EquipmentType.WEAPON || type == EquipmentType.ARROWS) {
				return -1;
			}
			return 30;
			
		case "rune":
			if (type == EquipmentType.WEAPON || type == EquipmentType.ARROWS) {
				return -1;
			}
			return 40;

		case "dragon":
		case "toktz-ket-xil":
			if (type == EquipmentType.WEAPON || type == EquipmentType.ARROWS) {
				return -1;
			}
			return 60;
			
		case "bandos":
			if (second.equals("mitre") || second.equals("robe")) {
				return -1;
			}
			
			if (second.startsWith("plate") || second.startsWith("full") || second.startsWith("kite")) {
				return 40;
			}
			if (type == EquipmentType.WEAPON) {
				return -1;
			}
			return 65;
			
		case "armadyl":
			if (second.equals("mitre") || second.equals("robe")) {
				return -1;
			}
			
			if (second.startsWith("plate") || second.startsWith("full") || second.startsWith("kite")) {
				return 40;
			}
			if (type == EquipmentType.WEAPON) {
				return -1;
			}
			return 70;
			
		case "dharok's":
		case "guthan's":
		case "torag's":
		case "verac's":
		case "karil's":
		case "ahrim's":
		case "barrows":
			if (type == EquipmentType.WEAPON) {
				return -1;
			}
			return 70;
			
		case "dragonfire":
			return 75;
			
		case "neitiznot":
			return 55;
			
		case "saradomin":
		case "guthix":
		case "zamorak":
			if (second.equals("mitre") || second.equals("robe")) {
				return -1;
			}
			if (type == EquipmentType.WEAPON) {
				return -1;
			}
			return 40;
			
		case "berserker":
		case "archer":
		case "farseer":
		case "warrior":
			if (type == EquipmentType.RING) {
				return -1;
			}
			return 45;

		case "green":
		case "red":
		case "blue":
		case "black":
			if (type != EquipmentType.BODY) {
				return -1;
			}
			
			return 40;

		case "hardleather":
			if (type != EquipmentType.BODY) {
				return -1;
			}
			
			return 10;
			
		case "void":
			return 42;

		case "snakeskin":
			return 30;
			
		case "3rd":
			return 45;
			
		case "penance":
		case "runner":
		case "fighter":
			if (type == EquipmentType.GLOVES) {
				return -1;
			}
			return 40;
			
		}
		
		return -1;
	}

}
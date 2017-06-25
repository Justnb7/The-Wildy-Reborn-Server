package com.venenatis.game.util.parser.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.google.gson.JsonObject;
import com.venenatis.game.content.skills.SkillRequirement;
import com.venenatis.game.util.parser.GsonParser;

public class EquipmentParser extends GsonParser {
	
	public static Map<Integer, Loader> LOADED = new HashMap<>();

	public static final Logger logger = Logger.getLogger(EquipmentParser.class.getName());

	public EquipmentParser() {
		super("equip");
	}

	@Override
	protected void parse(JsonObject data) {
		final int id = data.get("id").getAsInt();

		SkillRequirement[] requirement = new SkillRequirement[] {};

		if (data.has("requirements")) {
			requirement = builder.fromJson(data.get("requirements"), SkillRequirement[].class);
		}

		LOADED.put(id, new Loader(id, requirement));
	}

	static class Loader {
		private final int id;
		private final SkillRequirement[] reqs;
		
		public Loader(int id, SkillRequirement[] req) {
			this.id = id;
			this.reqs = req;
		}
		
		public int getId() {
			return id;
		}
		
		public SkillRequirement[] getReqs() {
			return reqs;
		}
	}
}
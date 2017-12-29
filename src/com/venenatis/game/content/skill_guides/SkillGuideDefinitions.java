package com.venenatis.game.content.skill_guides;

import java.util.HashMap;
import java.util.Map;

import com.venenatis.game.model.Skills;

public enum SkillGuideDefinitions {

	ATTACK(Skills.ATTACK, 
			new String[] {
					"Weapons", "Armour", "Salamanders"
			}, 
			new SkillGuideContent[] {
					SkillGuideContent.ATTACK_WEAPONS, SkillGuideContent.ATTACK_ARMOUR, SkillGuideContent.ATTACK_SALAMANDERS
			}),
	DEFENCE(Skills.DEFENCE, 
			new String[] {
					"Armour", "Penance", "Prayers"
			}, 
			new SkillGuideContent[] {
					SkillGuideContent.DEFENCE_ARMOUR, SkillGuideContent.DEFENCE_PENANCE, SkillGuideContent.DEFENCE_PRAYERS
			}),
	STRENGTH(Skills.STRENGTH, 
			new String[] {
					"Weapons", "Armour", "Shortcuts", "Areas", "Barbarian"
			}, 
			new SkillGuideContent[] {
					SkillGuideContent.STRENGTH_WEAPONS, SkillGuideContent.STRENGTH_ARMOUR, SkillGuideContent.STRENGTH_SHORTCUTS, SkillGuideContent.STRENGTH_AREAS, SkillGuideContent.STRENGTH_BARBARIAN
			}),
	HITPOINTS(Skills.HITPOINTS, 
			new String[] {
					"Hitpoints", "Healing", "Equipment"
			}, 
			new SkillGuideContent[] {
					SkillGuideContent.HITPOINTS, SkillGuideContent.HEALING,  SkillGuideContent.HITPOINTS_EQUIPMENT
			}),
	RANGED(Skills.RANGE, 
			new String[] {
					"Bows", "Thrown", "Crossbows", "Armour", "Miscellaneous", "Shortcuts", "Salamanders"
			}, 
			new SkillGuideContent[] {
					SkillGuideContent.RANGING_BOWS, SkillGuideContent.RANGING_THROWN, SkillGuideContent.RANGING_CROSSBOWS, SkillGuideContent.RANGING_ARMOUR, SkillGuideContent.RANGING_MISCELLANEOUS, SkillGuideContent.RANGING_SHORTCUTS, SkillGuideContent.RANGING_SALAMANDERS
			}),
	PRAYER(Skills.PRAYER, 
			new String[] {
					"Prayers", "Equipment"
			}, 
			new SkillGuideContent[] {
					SkillGuideContent.PRAYERS, SkillGuideContent.PRAYER_EQUIPMENT
			}),
	MAGIC(Skills.MAGIC, 
			new String[] {
					"Normal Spells", "Ancient Magicks", "Lunar Spells", "Arceuus Spells", "Armour", "Bolts", "Weapons", "Equipment", "Salamanders"
			}, 
			new SkillGuideContent[] {
					SkillGuideContent.NORMAL_SPELLS, SkillGuideContent.ANCIENT_MAGICKS, SkillGuideContent.LUNAR_SPELLS, SkillGuideContent.ARCEUUS_SPELLS, SkillGuideContent.MAGIC_ARMOUR, SkillGuideContent.MAGIC_BOLTS, SkillGuideContent.MAGIC_WEAPONS, SkillGuideContent.MAGIC_EQUIPMENT, SkillGuideContent.MAGIC_SALAMANDERS
			}),
	RUNECRAFTING(Skills.RUNECRAFTING, 
			new String[] {
					"Runes", "Multiple Runes", "Rune Pouches", "Infusing"
			}, 
			new SkillGuideContent[] {
					SkillGuideContent.RUNECRAFTING_RUNES, SkillGuideContent.RUNECRAFTING_MULTIPLE_RUNES, SkillGuideContent.RUNECRAFTING_RUNE_POUCHES, SkillGuideContent.RUNECRAFTING_INFUSING
			}),
	CONSTRUCTION(Skills.CONSTRUCTION, 
			new String[] {
					"Rooms", "Skills", "Surfaces", "Storage", "Decorative", "Trophies", "Games", "Garden", "Dungeon", "Chapel", "Raids", "Other", "Servants", "House Size"
			}, 
			new SkillGuideContent[] {
					SkillGuideContent.CONSTRUCTION_ROOMS
			}),
	THIEVING(Skills.THIEVING, 
			new String[] {
					"Pickpocket", "Stalls", "Chests", "Other"
			}, 
			new SkillGuideContent[] {
					SkillGuideContent.THIEVING_PICKPOCKET
			});
	
	private int skilId;
	private String[] options;
	private SkillGuideContent[] content;

	private SkillGuideDefinitions(int skillId, String[] options, SkillGuideContent[] content) {
		this.skilId = skillId;
		this.options = options;
		this.content = content;
	}
	
	public int getSkilId() {
		return skilId;
	}

	public String[] getOptions() {
		return options;
	}
	
	public SkillGuideContent[] getContent() {
		return content;
	}
	
	private static Map<Integer, SkillGuideDefinitions> skillIdMap = new HashMap<>();

	public static SkillGuideDefinitions getBySkillId(int skilId) {
		return skillIdMap.get(skilId);
	}

	static {
		for (SkillGuideDefinitions def : values()) {
			skillIdMap.put(def.getSkilId(), def);
		}
	}
	
}
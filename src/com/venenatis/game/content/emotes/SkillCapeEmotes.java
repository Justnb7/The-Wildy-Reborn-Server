package com.venenatis.game.content.emotes;

import java.util.HashMap;
import java.util.Map;

public enum SkillCapeEmotes {

	ATTACK_EMOTE(new int[] { 9747, 9748 }, 4959, 823),
	DEFENCE_EMOTE(new int[] { 9753, 9754 }, 4961, 824),
	STRENGTH_EMOTE(new int[] { 9750, 9751 }, 4981, 828),
	HITPOINTS_EMOTE(new int[] { 9768, 9769 }, 4971, 833),
	RANGED_EMOTE(new int[] { 9756, 9757 }, 4973, 832),
	MAGIC_EMOTE(new int[] { 9762, 9763 }, 4939, 813),
	PRAYER_EMOTE(new int[] { 9759, 9760 }, 4979, 829),
	COOKING_EMOTE(new int[] { 9801, 9802 }, 4955, 821),
	WOODCUTTING_EMOTE(new int[] { 9807, 9808 }, 4957, 822),
	FLETCHING_EMOTE(new int[] { 9783, 9784 }, 4937, 812),
	FISHING_EMOTE(new int[] { 9798, 9799 }, 4951, 819),
	FIREMAKING_EMOTE(new int[] { 9804, 9805 }, 4975, 831),
	CRAFTING_EMOTE(new int[] { 9780, 9781 }, 4949, 818),
	SMITHING_EMOTE(new int[] { 9795, 9796 }, 4943, 815),
	MINING_EMOTE(new int[] { 9792, 9793 }, 4941, 814),
	HERBLORE_EMOTE(new int[] { 9774, 9775 }, 4969, 835),
	AGILITY_EMOTE(new int[] { 9771, 9772 }, 4977, 830),
	THIEVING_EMOTE(new int[] { 9777, 9778 }, 4965, 826),
	SLAYER_EMOTE(new int[] { 9786, 9787 }, 4967, 827),
	FARMING_EMOTE(new int[] { 9810, 9811 }, 4963, 825),
	RUNECRAFTING_EMOTE(new int[] { 9765, 9766 }, 4947, 817),
	CONSTRUCTION_EMOTE(new int[] { 9789, 9790 }, 4953, 820),
	HUNTER_EMOTE(new int[] { 9948, 9949 }, 5158, 907),
	MAX_EMOTE(new int[] { 13280, 13329, 13331, 13333, 13335, 13337, 13342, 20760 }, 7121, 1286),
	QUEST_EMOTE(new int[] { 9813, 13068 }, 4945, 816),
	DIARY_EMOTE(new int[] { 13069, 19476 }, 2709, 323),
	CABBAGE_EMOTE(new int[] { 13679 }, 7209, -1);

	private int[] itemIds;
	private int animId;
	private int gfxId;

	private SkillCapeEmotes(int[] itemIds, int animId, int gfxId) {
		this.itemIds = itemIds;
		this.animId = animId;
		this.gfxId = gfxId;
	}

	private static Map<Integer, SkillCapeEmotes> skillCapeEmotesMap = new HashMap<>();

	static {
		for (SkillCapeEmotes def : values()) {
			for (int id : def.itemIds) {
				skillCapeEmotesMap.put(id, def);
			}
		}
	}
	
	public static SkillCapeEmotes getSkillCapeEmote(int itemId) {
		return skillCapeEmotesMap.get(itemId);
	}

	public int getAnimId() {
		return animId;
	}

	public int getGfxId() {
		return gfxId;
	}
}
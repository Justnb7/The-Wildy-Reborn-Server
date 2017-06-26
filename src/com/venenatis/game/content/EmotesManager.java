package com.venenatis.game.content;

import java.util.Arrays;
import java.util.Optional;

import com.venenatis.game.model.Item;
import com.venenatis.game.model.combat.Combat;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.model.masks.Graphic;

public final class EmotesManager {
	
	/**
	 * 
	 * @author Patrick van Elderen
	 *
	 */
	public enum EmoteData {
		YES(168, 855, -1, 2000),
		NO(169, 856, -1, 2000),
		BOW(164, 858, -1, 2000),
		ANGRY(165, 859, -1, 2000),
		THINK(162, 857, -1, 2000),
		WAVE(163, 863, -1, 2000),
		SHRUG(52058, 2113, -1, 2000),
		CHEER(171, 862, -1, 2000),
		BECKON(167, 864, -1, 2000),
		LAUGH(170, 861, -1, 2000),
		JUMP_FOR_JOY(52054, 2109, -1, 2000),
		YAWN(52056, 2111, -1, 2000),
		DANCE(166, 866, -1, 2000),
		JIG(52051, 2106, -1, 2000),
		SPIN(52052, 2107, -1, 2000),
		HEADBANG(52053, 2108, -1, 2000),
		CRY(161, 860, -1, 2000),
		BLOW_KISS(43092, 1374, 574, 2000),
		PANIC(52050, 2105, -1, 2000),
		RASPBERRY(52055, 2110, -1, 2000),
		CLAP(172, 865, -1, 2000),
		SALUTE(52057, 2112, -1, 2000),
		GOBLIN_BOW(52071, 0x84F, -1, 2000),
		GOBLIN_SALUTE(52072, 0x850, -1, 2000),
		GLASS_BOX(2155, 1131, -1, 2000),
		CLIMB_ROBE(25103, 1130, -1, 2000),
		LEAN_ON_AIR(25106, 1129, -1, 2000),
		GLASS_WALL(2154, 1128, -1, 2000),
		ZOMBIE_WALK(72032, 3544, -1, 2000),
		ZOMBIE_DANCE(72033, 3543, -1, 2000),
		ZOMBIE_HAND(88065, -1, -1, 3000),
		SCARED(59062, 2836, -1, 2000),
		RABBIT_HOP(72254, 6111, -1, 2000),
		SKILLCAPE(74108, -1, -1, 8000);
		
		private int buttonId, animation, graphic;
		
		private long emoteDuration;
		
		public int getEmoteAnimation() {
			return animation;
		}
		
		public int getButton() {
			return buttonId;
		}
		
		public int getEmoteGraphic() {
			return graphic;
		}
		
		public long getEmoteDuration() {
			return emoteDuration;
		}
		
		EmoteData(int button, int animation, int graphic, long time) {
			this.buttonId = button;
			this.animation = animation;
			this.graphic = graphic;
			this.emoteDuration = time;
		}
		
		public static void useBookEmote(Player player, int buttonId) {
			
			Optional<EmoteData> emotes = Arrays.asList(EmoteData.values()).stream().filter(x -> x.buttonId == buttonId).findAny();
			
			if (!emotes.isPresent()) {
				return;
			}

			EmoteData emote = emotes.get();
			
			if (Combat.incombat(player)) {
				player.getActionSender().sendMessage("You can't do this while you're under combat.");
				return;
			}
			
			long lastEmote = player.getAttribute("lastEmoteTime", System.currentTimeMillis() - (60*1000)); // grab the last time we did an emote
			// if the key isnt present, default to 60s ago
			
			//player.write(new SendGameMessage("emote dur = "+emote.emoteDuration);
			if (System.currentTimeMillis() - lastEmote < emote.emoteDuration) {
				player.getActionSender().sendMessage("You're already doing an emote!");
				return;
			}
			
			if (emote.buttonId == buttonId) {
				if (buttonId == 74108) {
					skillcapeEmote(player);
					// TODO skillCape Max cape
				} else {
					player.playAnimation(Animation.create(emote.getEmoteAnimation()));
					player.playGraphics(Graphic.create(emote.getEmoteGraphic(), 0, 0));
					player.setAttribute("lastEmoteTime", System.currentTimeMillis());
				}
			}
		}
		
	}
	
	static enum skillCapes {
		Attack(0, 823, 4959, new Item(9747), new Item(9748), new Item(9749)),
		Defence(1, 824, 4961, new Item(9753), new Item(9754), new Item(9755)),
		Strength(2, 828, 4981, new Item(9750), new Item(9751), new Item(9752)),
		Hitpoints(3, 833, 4971, new Item(9768), new Item(9769), new Item(9770)),
		Range(4, 832, 4973, new Item(9756), new Item(9757), new Item(9758)),
		Prayer(5, 829, 4979, new Item(9759), new Item(9760), new Item(9761)),
		Magic(6, 813, 4939, new Item(9762), new Item(9763), new Item(9764)),
		Cooking(7, 821, 4955, new Item(9801), new Item(9802), new Item(9803)),
		Woodcutting(8, 822, 4957, new Item(9807), new Item(9808), new Item(9809)),
		Fletching(9, 812, 4937, new Item(9783), new Item(9784), new Item(9785)),
		Fishing(10, 819, 4951, new Item(9798), new Item(9799), new Item(9800)),
		Firemaking(11, 831, 4975, new Item(9804), new Item(9805), new Item(9806)),
		Crafting(12, 818, 4949, new Item(9780), new Item(9781), new Item(9782)),
		Smithing(13, 815, 4943, new Item(9795), new Item(9796), new Item(9797)),
		Mining(14, 814, 4941, new Item(9792), new Item(9793), new Item(9794)),
		Herblore(15, 835, 4969, new Item(9774), new Item(9775), new Item(9776)),
		Agility(16, 830, 4977, new Item(9771), new Item(9772), new Item(9773)),
		Thieving(17, 826, 4965, new Item(9777), new Item(9778), new Item(9779)),
		Slayer(18, 827, 4967, new Item(9786), new Item(9787), new Item(9788)),
		Farming(19, 825, 4963, new Item(9810), new Item(9811), new Item(9812)),
		Runecrafting(20, 817, 4947, new Item(9765), new Item(9766), new Item(9767)),
		Hunter(21, 907, 5158, new Item(9948), new Item(9949), new Item(9950)),
		Construction(22, -820, 4953, new Item(9789), new Item(9790), new Item(9791)),
		Summoning(23, -1, -1, new Item(-1), new Item(-1)),
		Dungeoneering(24, -1, -1, new Item(-1), new Item(-1)),
		Quest_cape(-1, 816, 4945, new Item(9813), new Item(13068), new Item(9814)),
		Achievement_diary_cape(-1, -1, -1, new Item(13069), new Item(13070)),
		Max_cape(-1, 1286, 7121, new Item(13280), new Item(13281)),
		Fire_max_cape(-1, 1286, 7121, new Item(13329), new Item(13330)),
		Saradomin_max_cape(-1, 1286, 7121, new Item(13331), new Item(13332)),
		Zamorak_max_cape(-1, 1286, 7121, new Item(13333), new Item(13334)),
		Guthix_max_cape(-1, 1286, 7121, new Item(13335), new Item(13336)),
		Avas_max_cape(-1, 1286, 7121, new Item(13337), new Item(13338));

		/**
		 * The cape
		 */
		private Item wearingCape[];

		/**
		 * The skillId, levelRequirement. graphicId and emoteId
		 */
		private int skillId, gfx, animation;

		/**
		 * 
		 * @param skillId
		 * @param levelReq
		 * @param gfx
		 * @param animation
		 * @param wearingCape
		 */
		skillCapes(int skillId, int gfx, int animation, Item... wearingCape) {
			this.skillId = skillId;
			this.gfx = gfx;
			this.animation = animation;
			this.wearingCape = wearingCape;
		}

		/**
		 * check for the skill index {@link Attack - 0}
		 * 
		 * @return the skill index
		 */
		private int getSkillId() {
			return skillId;
		}

		/**
		 * grabs the gfxId from the enum.
		 * 
		 * @return the skillcape gfx
		 */
		private int getGfx() {
			return gfx;
		}

		/**
		 * grabs the emoteId from the enum.
		 * 
		 * @return the skillcape emote
		 */
		private int getAnimation() {
			return animation;
		}

		/**
		 * checks the player's equipment for the cape.
		 * 
		 * @return the skillcape
		 */
		private Item[] getCape() {
			return wearingCape;
		}
	}

	/**
	 * @param player
	 * @param itemId
	 * @return Were checking here if the player has the correct level
	 *         requirement... to wield the skillcape.
	 */
	public static boolean doesntHaveLevelReq(Player player, int itemId) {
		for (skillCapes cape : skillCapes.values()) {
			for (Item itemcape : cape.wearingCape) {
				if (itemcape == null)
					continue;
				int capeId = itemcape.id;
				if (capeId == itemId) {
					if (itemId == -1 || cape.getSkillId() == -1) {
						if (!player.isMaxed() && itemId == 13329 && itemId == 13330 && itemId == 13280 && itemId == 13281 && itemId == 13331 && itemId == 13332
						  && itemId == 13333 && itemId == 13334 && itemId == 13335 && itemId == 13336) {
							player.getActionSender().sendMessage("You are not yet maxed.");
							return true;
						}
					}
					else if(player.getSkills().getLevelForExperience(cape.getSkillId()) < 99) {
						player.getActionSender().sendMessage("You don't meet the skill requirement to wear that item.");
						return true;
					}
				}
			}
		}
		//TODO find out new config for skillcape
		/*if (wearingCape(player.playerEquipment[player.getEquipment().getCapeId()])) {
			player.write(new SendConfig(700, 0));
		} else {
			player.write(new SendConfig(700, 1));
		}*/
		return false;
	}
	
	public static boolean wearingCape(int cape) {
		int capes[] =
		{ 9747, 9748, 9750, 9751, 9753, 9754, 9756, 9757, 9759, 9760, 9762, 9763, 9765, 9766, 9768, 9769, 9771, 9772,
				9774, 9775, 9777, 9778, 9780, 9781, 9783, 9784, 9786, 9787, 9789, 9790, 9792, 9793, 9795, 9796, 9798,
				9799, 9801, 9802, 9804, 9805, 9807, 9808, 9810, 9811, 10662 };
		for (int i = 0; i < capes.length; i++)
		{
			if (capes[i] == cape)
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * First checks wether the player is wielding a Skillcape, if the player is
	 * wielding the cape then.. performs the skillcape's emote
	 * 
	 * @param player
	 *            {@linkplain gfxId} and {@linkplain emoteId}
	 */
	public static void skillcapeEmote(Player player) {
		for (skillCapes cape : skillCapes.values()) {
			for (Item item : cape.getCape()) {
				if (item.getId() == -1) {
					// TODO max cape - not related to one specific skill
				}

				else if (player.getEquipment().contains(item)) {
					player.playAnimation(Animation.create(cape.getAnimation()));
					player.playGraphics(Graphic.create(cape.getGfx(), 0, 0));
					player.setAttribute("lastEmoteTime", System.currentTimeMillis());
					//player.write(new SendGameMessage("skillcape anim for cape "+item.getId());
					return;
				}
			}
		}
		player.getActionSender().sendMessage("You need to wield a skillcape before you can perform the emote.");
	}
	
	public static void onLogin(Player player) {
		for (int i = 744; i <= 760; i++)
			player.getActionSender().sendConfig(i, 1);
	}
	
}

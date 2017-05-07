package com.model.game.character.player.content.consumable.potion;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.model.UpdateFlags.UpdateFlag;
import com.model.game.character.player.Player;
import com.model.game.character.player.Skills;
import com.model.net.packet.out.SendSkillPacket;



/**
 * Contains all of the potion data
 * @author Arithium
 *
 */
public enum PotionData {

	STRENGTH_POTION_4(113, 115, new PotionEffect() {
		@Override
		public void handle(Object... object) {
			Player player = (Player) object[0];
			enhanceStat(player, 2, false);
		}
	}),
	STRENGTH_POTION_3(115, 117, new PotionEffect() {
		@Override
		public void handle(Object... object) {
			Player player = (Player) object[0];
			enhanceStat(player, 2, false);
		}
	}),
	STRENGTH_POTION_2(117, 119, new PotionEffect() {
		@Override
		public void handle(Object... object) {
			Player player = (Player) object[0];
			enhanceStat(player, 2, false);
		}
	}),
	STRENGTH_POTION_1(119, 229, new PotionEffect() {
		@Override
		public void handle(Object... object) {
			Player player = (Player) object[0];
			enhanceStat(player, 2, false);
		}
	}),
	SUPER_STRENGTH_POTION_4(2440, 157, new PotionEffect() {
		@Override
		public void handle(Object... object) {
			Player player = (Player) object[0];
			enhanceStat(player, 2, true);
		}
	}),
	SUPER_STRENGTH_POTION_3(157, 159, new PotionEffect() {
		@Override
		public void handle(Object... object) {
			Player player = (Player) object[0];
			enhanceStat(player, 2, true);
		}
	}),
	SUPER_STRENGTH_POTION_2(159, 161, new PotionEffect() {
		@Override
		public void handle(Object... object) {
			Player player = (Player) object[0];
			enhanceStat(player, 2, true);
		}
	}),
	SUPER_STRENGTH_POTION_1(161, 229, new PotionEffect() {
		@Override
		public void handle(Object... object) {
			Player player = (Player) object[0];
			enhanceStat(player, 2, true);
		}
	}),
	ATTACK_POTION_4(2428, 121, new PotionEffect() {
		@Override
		public void handle(Object... object) {
			Player player = (Player) object[0];
			enhanceStat(player, 0, false);
		}
	}),
	ATTACK_POTION_3(121, 123, new PotionEffect() {
		@Override
		public void handle(Object... object) {
			Player player = (Player) object[0];
			enhanceStat(player, 0, false);
		}
	}),
	ATTACK_POTION_2(123, 125, new PotionEffect() {
		@Override
		public void handle(Object... object) {
			Player player = (Player) object[0];
			enhanceStat(player, 0, false);
		}
	}),
	ATTACK_POTION_1(125, 229, new PotionEffect() {
		@Override
		public void handle(Object... object) {
			Player player = (Player) object[0];
			enhanceStat(player, 0, false);
		}
	}),
	SUPER_ATTACK_POTION_4(2436, 145, new PotionEffect() {
		@Override
		public void handle(Object... object) {
			Player player = (Player) object[0];
			enhanceStat(player, 0, true);
		}
	}),
	SUPER_ATTACK_POTION_3(145, 147, new PotionEffect() {
		@Override
		public void handle(Object... object) {
			Player player = (Player) object[0];
			enhanceStat(player, 0, true);
		}
	}),
	SUPER_ATTACK_POTION_2(147, 149, new PotionEffect() {
		@Override
		public void handle(Object... object) {
			Player player = (Player) object[0];
			enhanceStat(player, 0, true);
		}
	}),
	SUPER_ATTACK_POTION_1(149, 229, new PotionEffect() {
		@Override
		public void handle(Object... object) {
			Player player = (Player) object[0];
			enhanceStat(player, 0, true);
		}
	}),
	DEFENCE_POTION_4(2432, 133, new PotionEffect() {
		@Override
		public void handle(Object... object) {
			Player player = (Player) object[0];
			enhanceStat(player, 1, false);
		}
	}),
	DEFENCE_POTION_3(133, 135, new PotionEffect() {
		@Override
		public void handle(Object... object) {
			Player player = (Player) object[0];
			enhanceStat(player, 1, false);
		}
	}),
	DEFENCE_POTION_2(135, 137, new PotionEffect() {
		@Override
		public void handle(Object... object) {
			Player player = (Player) object[0];
			enhanceStat(player, 1, false);
		}
	}),
	DEFENCE_POTION_1(137, 229, new PotionEffect() {
		@Override
		public void handle(Object... object) {
			Player player = (Player) object[0];
			enhanceStat(player, 1, false);
		}
	}),
	SUPER_DEFENCE_POTION_4(2442, 163, new PotionEffect() {
		@Override
		public void handle(Object... object) {
			Player player = (Player) object[0];
			enhanceStat(player, 1, true);
		}
	}),
	SUPER_DEFENCE_POTION_3(163, 165, new PotionEffect() {
		@Override
		public void handle(Object... object) {
			Player player = (Player) object[0];
			enhanceStat(player, 1, true);
		}
	}),
	SUPER_DEFENCE_POTION_2(165, 167, new PotionEffect() {
		@Override
		public void handle(Object... object) {
			Player player = (Player) object[0];
			enhanceStat(player, 1, true);
		}
	}),
	SUPER_DEFENCE_POTION_1(167, 229, new PotionEffect() {
		@Override
		public void handle(Object... object) {
			Player player = (Player) object[0];
			enhanceStat(player, 1, true);
		}
	}),
	SARADOMIN_BREW_4(6685, 6687, new PotionEffect() {
		@Override
		public void handle(Object... object) {
			Player player = (Player) object[0];
			doTheBrew(player);
		}
	}),
	SARADOMIN_BREW_3(6687, 6689, new PotionEffect() {
		@Override
		public void handle(Object... object) {
			Player player = (Player) object[0];
			doTheBrew(player);
		}
	}),
	SARADOMIN_BREW_2(6689, 6691, new PotionEffect() {
		@Override
		public void handle(Object... object) {
			Player player = (Player) object[0];
			doTheBrew(player);
		}
	}),
	SARADOMIN_BREW_1(6691, 229, new PotionEffect() {
		@Override
		public void handle(Object... object) {
			Player player = (Player) object[0];
			doTheBrew(player);
		}
	}),
	SUPER_RESTORE_POTION_4(3024, 3026, new PotionEffect() {
		@Override
		public void handle(Object... object) {
			Player player = (Player) object[0];
			drinkPrayerPot(player, true);
		}
	}),
	SUPER_RESTORE_POTION_3(3026, 3028, new PotionEffect() {
		@Override
		public void handle(Object... object) {
			Player player = (Player) object[0];
			drinkPrayerPot(player, true);
		}
	}),
	SUPER_RESTORE_POTION_2(3028, 3030, new PotionEffect() {
		@Override
		public void handle(Object... object) {
			Player player = (Player) object[0];
			drinkPrayerPot(player, true);
		}
	}),
	SUPER_RESTORE_POTION_1(3030, 229, new PotionEffect() {
		@Override
		public void handle(Object... object) {
			Player player = (Player) object[0];
			drinkPrayerPot(player, true);
		}
	}),
	PRAYER_POTION_4(2434, 139, new PotionEffect() {
		@Override
		public void handle(Object... object) {
			Player player = (Player) object[0];
			drinkPrayerPot(player, false);
		}
	}),
	PRAYER_POTION_3(139, 141, new PotionEffect() {
		@Override
		public void handle(Object... object) {
			Player player = (Player) object[0];
			drinkPrayerPot(player, false);
		}
	}),
	PRAYER_POTION_2(141, 143, new PotionEffect() {
		@Override
		public void handle(Object... object) {
			Player player = (Player) object[0];
			drinkPrayerPot(player, false);
		}
	}),
	PRAYER_POTION_1(143, 229, new PotionEffect() {
		@Override
		public void handle(Object... object) {
			Player player = (Player) object[0];
			drinkPrayerPot(player, false);
		}
	}),
	RANGING_POTION_4(2444, 169, new PotionEffect() {
		@Override
		public void handle(Object... object) {
			Player player = (Player) object[0];
			enhanceStat(player, 4, false);
		}
	}),
	RANGING_POTION_3(169, 171, new PotionEffect() {
		@Override
		public void handle(Object... object) {
			Player player = (Player) object[0];
			enhanceStat(player, 4, false);
		}
	}),
	RANGING_POTION_2(171, 173, new PotionEffect() {
		@Override
		public void handle(Object... object) {
			Player player = (Player) object[0];
			enhanceStat(player, 4, false);
		}
	}),
	RANGING_POTION_1(173, 229, new PotionEffect() {
		@Override
		public void handle(Object... object) {
			Player player = (Player) object[0];
			enhanceStat(player, 4, false);
		}
	}),
	SUPER_RANGING_POTION_4(11722, 11723, new PotionEffect() {
		@Override
		public void handle(Object... object) {
			Player player = (Player) object[0];
			enhanceStat(player, 4, false);
		}
	}),
	SUPER_RANGING_POTION_3(11723, 11724, new PotionEffect() {
		@Override
		public void handle(Object... object) {
			Player player = (Player) object[0];
			enhanceStat(player, 4, false);
		}
	}),
	SUPER_RANGING_POTION_2(11724, 11725, new PotionEffect() {
		@Override
		public void handle(Object... object) {
			Player player = (Player) object[0];
			enhanceStat(player, 4, false);
		}
	}),
	SUPER_RANGING_POTION_1(11725, 229, new PotionEffect() {
		@Override
		public void handle(Object... object) {
			Player player = (Player) object[0];
			enhanceStat(player, 4, false);
		}
	}),
	MAGIC_POTION_4(3040, 3042, new PotionEffect() {
		@Override
		public void handle(Object... object) {
			Player player = (Player) object[0];
			enchanceMagic(player, 6, false);
		}
	}),
	MAGIC_POTION_3(3042, 3044, new PotionEffect() {
		@Override
		public void handle(Object... object) {
			Player player = (Player) object[0];
			enchanceMagic(player, 6, false);
		}
	}),
	MAGIC_POTION_2(3044, 3046, new PotionEffect() {
		@Override
		public void handle(Object... object) {
			Player player = (Player) object[0];
			enchanceMagic(player, 6, false);
		}
	}),
	MAGIC_POTION_1(3046, 229, new PotionEffect() {
		@Override
		public void handle(Object... object) {
			Player player = (Player) object[0];
			enchanceMagic(player, 6, false);
		}
	}),
	SUPER_MAGIC_POTION_4(11726, 11727, new PotionEffect() {
		@Override
		public void handle(Object... object) {
			Player player = (Player) object[0];
			enchanceMagic(player, 6, true);
		}
	}),
	SUPER_MAGIC_POTION_3(11727, 11728, new PotionEffect() {
		@Override
		public void handle(Object... object) {
			Player player = (Player) object[0];
			enchanceMagic(player, 6, true);
		}
	}),
	SUPER_MAGIC_POTION_2(11728, 11729, new PotionEffect() {
		@Override
		public void handle(Object... object) {
			Player player = (Player) object[0];
			enchanceMagic(player, 6, true);
		}
	}),
	SUPER_MAGIC_POTION_1(11729, 229, new PotionEffect() {
		@Override
		public void handle(Object... object) {
			Player player = (Player) object[0];
			enchanceMagic(player, 6, true);
		}
	}),
	ANTIDOTE_PLUS_4(5943, 5945, new PotionEffect() {
		@Override
		public void handle(Object... object) {
			Player player = (Player) object[0];
			drinkAntiPoison(player, TimeUnit.MINUTES.toMillis(6));
		}
	}),
	ANTIDOTE_PLUS_3(5945, 5947, new PotionEffect() {
		@Override
		public void handle(Object... object) {
			Player player = (Player) object[0];
			drinkAntiPoison(player, TimeUnit.MINUTES.toMillis(6));
		}
	}),
	ANTIDOTE_PLUS_2(5947, 5949, new PotionEffect() {
		@Override
		public void handle(Object... object) {
			Player player = (Player) object[0];
			drinkAntiPoison(player, TimeUnit.MINUTES.toMillis(6));
		}
	}),
	ANTIDOTE_PLUS_1(5949, 229, new PotionEffect() {
		@Override
		public void handle(Object... object) {
			Player player = (Player) object[0];
			drinkAntiPoison(player, TimeUnit.MINUTES.toMillis(6));
		}
	}),
	ANTIDOTE_PLUS_PLUS_4(5952, 5954, new PotionEffect() {
		@Override
		public void handle(Object... object) {
			Player player = (Player) object[0];
			drinkAntiPoison(player, TimeUnit.MINUTES.toMillis(12));
		}
	}),
	ANTIDOTE_PLUS_PLUS_3(5954, 5956, new PotionEffect() {
		@Override
		public void handle(Object... object) {
			Player player = (Player) object[0];
			drinkAntiPoison(player, TimeUnit.MINUTES.toMillis(12));
		}
	}),
	ANTIDOTE_PLUS_PLUS_2(5956, 5958, new PotionEffect() {
		@Override
		public void handle(Object... object) {
			Player player = (Player) object[0];
			drinkAntiPoison(player, TimeUnit.MINUTES.toMillis(12));
		}
	}),
	ANTIDOTE_PLUS_PLUS_1(5958, 229, new PotionEffect() {
		@Override
		public void handle(Object... object) {
			Player player = (Player) object[0];
			drinkAntiPoison(player, TimeUnit.MINUTES.toMillis(12));
		}
	}),
	ANTI_VENOM_4(12905, 12907, new PotionEffect() {
		@Override
		public void handle(Object... object) {
			Player player = (Player) object[0];
			drinkAntiVenom(player, 0);
		}
	}),
	ANTI_VENOM_3(12907, 12909, new PotionEffect() {
		@Override
		public void handle(Object... object) {
			Player player = (Player) object[0];
			drinkAntiVenom(player, 0);
		}
	}),
	ANTI_VENOM_2(12909, 12911, new PotionEffect() {
		@Override
		public void handle(Object... object) {
			Player player = (Player) object[0];
			drinkAntiVenom(player, 0);
		}
	}),
	ANTI_VENOM_1(12911, 229, new PotionEffect() {
		@Override
		public void handle(Object... object) {
			Player player = (Player) object[0];
			drinkAntiVenom(player, 0);
		}
	}),
	ANTI_VENOM_PLUS_4(12913, 12915, new PotionEffect() {
		@Override
		public void handle(Object... object) {
			Player player = (Player) object[0];
			drinkAntiVenom(player, TimeUnit.MINUTES.toMillis(5));
		}
	}),
	ANTI_VENOM_PLUS_3(12915, 12917, new PotionEffect() {
		@Override
		public void handle(Object... object) {
			Player player = (Player) object[0];
			drinkAntiVenom(player, TimeUnit.MINUTES.toMillis(5));
		}
	}),
	ANTI_VENOM_PLUS_2(12917, 12919, new PotionEffect() {
		@Override
		public void handle(Object... object) {
			Player player = (Player) object[0];
			drinkAntiVenom(player, TimeUnit.MINUTES.toMillis(5));
		}
	}),
	ANTI_VENOM_PLUS_1(12919, 229, new PotionEffect() {
		@Override
		public void handle(Object... object) {
			Player player = (Player) object[0];
			drinkAntiVenom(player, TimeUnit.MINUTES.toMillis(5));
		}
	}),
	ANTIPOISON_4(2446, 175, new PotionEffect() {
		@Override
		public void handle(Object... object) {
			Player player = (Player) object[0];
			drinkAntiPoison(player, 0);
		}
	}),
	ANTIPOISON_3(175, 177, new PotionEffect() {
		@Override
		public void handle(Object... object) {
			Player player = (Player) object[0];
			drinkAntiPoison(player, 0);
		}
	}),
	ANTIPOISON_2(177, 179, new PotionEffect() {
		@Override
		public void handle(Object... object) {
			Player player = (Player) object[0];
			drinkAntiPoison(player, 0);
		}
	}),
	ANTIPOISON_1(179, 229, new PotionEffect() {
		@Override
		public void handle(Object... object) {
			Player player = (Player) object[0];
			drinkAntiPoison(player, 0);
		}
	}),
	SUPER_ANTIPOISON_4(2448, 181, new PotionEffect() {
		@Override
		public void handle(Object... object) {
			Player player = (Player) object[0];
			drinkAntiPoison(player, TimeUnit.MINUTES.toMillis(6));
		}
	}),
	SUPER_ANTIPOISON_3(181, 183, new PotionEffect() {
		@Override
		public void handle(Object... object) {
			Player player = (Player) object[0];
			drinkAntiPoison(player, TimeUnit.MINUTES.toMillis(6));
		}
	}),
	SUPER_ANTIPOISON_2(183, 185, new PotionEffect() {
		@Override
		public void handle(Object... object) {
			Player player = (Player) object[0];
			drinkAntiPoison(player, TimeUnit.MINUTES.toMillis(6));
		}
	}),
	SUPER_ANTIPOISON_1(185, 229, new PotionEffect() {
		@Override
		public void handle(Object... object) {
			Player player = (Player) object[0];
			drinkAntiPoison(player, TimeUnit.MINUTES.toMillis(6));
		}
	}),
	ANTIFIRE_4(2452, 2454, new PotionEffect() {
		@Override
		public void handle(Object... object) {
			Player player = (Player) object[0];
			drinkAntifire(player);//setting 6mins
		}
	}),
	ANTIFIRE_3(2454, 2456, new PotionEffect() {
		@Override
		public void handle(Object... object) {
			Player player = (Player) object[0];
			drinkAntifire(player);
		}
	}),
	ANTIFIRE_2(2456, 2458, new PotionEffect() {
		@Override
		public void handle(Object... object) {
			Player player = (Player) object[0];
			drinkAntifire(player);
		}
	}),
	ANTIFIRE_1(2458, 229, new PotionEffect() {
		@Override
		public void handle(Object... object) {
			Player player = (Player) object[0];
			drinkAntifire(player);//yh go for it
		}
	}),
	;
	int potionId, replacement;
	PotionEffect effect;
	private PotionData(int potionId, int replacement, PotionEffect effect) {
		this.potionId = potionId;
		this.effect = effect;
		this.replacement = replacement;
	}

	private static Map<Integer, PotionData> potions = new HashMap<Integer, PotionData>();

	static {
		for (PotionData data : values()) {
			potions.put(data.getPotionId(), data);
		}
	}

	public int getPotionId() {
		return potionId;
	}

	public int getReplacement() {
		return replacement;
	}

	public static PotionData forId(int id) {
		return potions.get(id);
	}

	public PotionEffect getPotionEffect() {
		return effect;
	}

	public static void enhanceStat(Player player, int skillID, boolean sup) {
		player.getSkills().setLevel(skillID, player.getSkills().getLevel(skillID) + getBoostedStat(player, skillID, sup));
		player.write(new SendSkillPacket(skillID));
	}

	public static int getBrewStat(Player player, int skill, double amount) {
		return (int) (player.getSkills().getLevelForExperience(skill) * amount);
	}

	public static int getBoostedStat(Player player, int skill, boolean sup) {
		int increaseBy = 0;
		if (sup)
			increaseBy = (int) (player.getSkills().getLevelForExperience(skill) * .20);
		else
			increaseBy = (int) (player.getSkills().getLevelForExperience(skill) * .13) + 1;
		if (player.getSkills().getLevel(skill) + increaseBy > player.getSkills().getLevelForExperience(skill) + increaseBy + 1) {
			return player.getSkills().getLevelForExperience(skill) + increaseBy - player.getSkills().getLevel(skill);
		}
		return increaseBy;
	}
	
	public static void drinkPrayerPot(Player player, boolean rest) {
		player.getSkills().setLevel(5, (int) (player.getSkills().getLevel(5) + (player.getSkills().getLevelForExperience(5) * .33)));
		if (rest)
			player.getSkills().setLevel(5, player.getSkills().getLevel(Skills.PRAYER) + 1);
		if (player.getSkills().getLevel(5) > player.getSkills().getLevelForExperience(5))
			player.getSkills().setLevel(5, player.getSkills().getLevelForExperience(5));
		player.write(new SendSkillPacket(5));
		if (rest)
			restoreStats(player);
	}
	
	public static void drinkAntiVenom(Player player, long duration) {
		player.getActionSender().sendMessage("You have been cured of venom.");
		player.setVenomImmunity(duration);
		player.infection = 0;
		player.setLastVenomCure(System.currentTimeMillis());
		player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
	}
	
	public static void drinkAntiPoison(Player player, long duration) {
		player.getActionSender().sendMessage("You have been cured of poison.");
		/*player.setPoisonDamage((byte) 0);
		player.setPoisonImmunity(duration);
		player.setLastPoisonCure(System.currentTimeMillis());*/
		player.infection = 0;
		player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
	}
	
	// shit naming of paramater - it's when you drank it in MS (sys.curtime) not how long it lasts
	public static void drinkAntifire(Player player) {
		player.getActionSender().sendMessage("You drink some of your antifire potion.");
		player.setAttribute("antiFire", System.currentTimeMillis());//setting duration? this one :D
		//so that would be better yes - hopping off back in 10 sure
	}
	
	public static void drinkExtendedAntifire(Player player, long duration) {
		player.getActionSender().sendMessage("You drink some of your extended antifire potion.");
		player.setAttribute("extended_antiFire", duration);//setting duration?
	}
	
	public static void enchanceMagic(Player player, int skillID, boolean sup) {
		player.getSkills().setLevel(skillID, player.getSkills().getLevel(skillID) + getBoostedMagic(player, skillID, sup));
		player.write(new SendSkillPacket(skillID));
	}

	public static int getBoostedMagic(Player player, int skill, boolean sup) {
		int increaseBy = 0;
		if (sup)
			increaseBy = (int) (player.getSkills().getLevelForExperience(skill) * .20);
		else
			increaseBy = (int) (player.getSkills().getLevelForExperience(skill) * .06);
		if (player.getSkills().getLevel(skill) + increaseBy > player.getSkills().getLevelForExperience(skill) + increaseBy + 1) {
			return player.getSkills().getLevelForExperience(skill) + increaseBy - player.getSkills().getLevel(skill);
		}
		return increaseBy;
	}
	
	public static void enchanceStat2(Player player, int skillID, boolean sup) {
		player.getSkills().setLevel(skillID, player.getSkills().getLevel(skillID) + getExtremeStat(player, skillID, sup));
	}

	public static int getExtremeStat(Player player, int skill, boolean sup) {
		int increaseBy = 0;
		if (sup)
			increaseBy = (int) (player.getSkills().getLevelForExperience(skill) * .25);
		else
			increaseBy = (int) (player.getSkills().getLevelForExperience(skill) * .25) + 1;
		if (player.getSkills().getLevel(skill) + increaseBy > player.getSkills().getLevelForExperience(skill) + increaseBy + 1) {
			return player.getSkills().getLevelForExperience(skill) + increaseBy - player.getSkills().getLevel(skill);
		}
		return increaseBy;
	}
	
	public static void drinkExtremePrayer(Player player, int itemId, int replaceItem, int slot, boolean rest) {
		player.getSkills().setLevel(5, (int) (player.getSkills().getLevel(5) + ((int) player.getSkills().getLevelForExperience(5) * .38)));
		if (rest)
			player.getSkills().setLevel(5, + 1);
		if (player.getSkills().getLevel(5) > player.getSkills().getLevelForExperience(5))
			player.getSkills().setLevel(5, player.getSkills().getLevelForExperience(5));
		if (rest)
			restoreStats(player);
	}
	
	public static void doTheBrew(Player player) {
			int[] toDecrease = { 0, 2, 4, 6 };

			for (int tD : toDecrease) {
				player.getSkills().setLevel(tD, player.getSkills().getLevel(tD) -getBrewStat(player, tD, .10));
				if (player.getSkills().getLevel(tD) < 0)
					player.getSkills().setLevel(tD, 1);
			}
			player.getSkills().setLevel(1, player.getSkills().getLevel(1) + getBrewStat(player, 1, .20));
			if (player.getSkills().getLevel(1) > (player.getSkills().getLevelForExperience(1) * 1.2 + 1)) {
				player.getSkills().setLevel(1, (int) (player.getSkills().getLevelForExperience(1) * 1.2));
			}
			double ammount = 1.17;
			player.getSkills().setLevel(3, player.getSkills().getLevel(3) + getBrewStat(player, 3, .15));
			if (player.getSkills().getLevel(3) > (player.getSkills().getLevelForExperience(3) * ammount + 1)) {
				player.getSkills().setLevel(3, (int) (player.getSkills().getLevelForExperience(3) * ammount));
			}
	}
	
	public static void restoreStats(Player player) {
		for (int j = 0; j <= 6; j++) {
			if (j == 5 || j == 3)
				continue;
			if (player.getSkills().getLevel(j) < player.getSkills().getLevelForExperience(j)) {
				player.getSkills().setLevel(j, (int) (player.getSkills().getLevel(j) + (player.getSkills().getLevelForExperience(j) * .33)));
				if (player.getSkills().getLevel(j) > player.getSkills().getLevelForExperience(j)) {
					player.getSkills().setLevel(j, player.getSkills().getLevelForExperience(j));
				}
			}
		}
	}

}
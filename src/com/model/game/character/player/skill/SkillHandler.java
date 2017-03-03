package com.model.game.character.player.skill;

import java.util.Arrays;

import com.model.game.character.player.Player;
import com.model.game.character.player.packets.out.SendMessagePacket;
import com.model.utility.Utility;

public class SkillHandler {
	
	public enum Skill {
		ATTACK(0), DEFENCE(1), STRENGTH(2), HITPOINTS(3), RANGED(4), PRAYER(5),
		MAGIC(6), COOKING(7), WOODCUTTING(8), FLETCHING(9), FISHING(10), FIREMAKING(11),
		CRAFTING(12), SMITHING(13), MINING(14), HERBLORE(15), AGILITY(16), THIEVING(17), 
		SLAYER(18), FARMING(19), RUNECRAFTING(20);
		
		private int id;
		
		private Skill(int id) {
			this.id = id;
		}
		
		public int getId() {
			return id;
		}
		
		@Override
		public String toString() {
			String name = name().toLowerCase();
			return Utility.capitalize(name);
		}
		
		public static Skill forId(int id) {
			return Arrays.asList(values()).stream().filter(s -> s.id == id).findFirst().orElse(null);
		}
	}


	public static boolean noInventorySpace(Player player, String skill) {
		if (player.getItems().getFreeSlots() == 0) {
			player.write(new SendMessagePacket("You don't have enough inventory space."));
			return false;
		}
		return true;
	}

	public static boolean hasRequiredLevel(final Player player, int id, int lvlReq, String skill, String event) {
		if (player.getSkills().getLevel(id) < lvlReq) {
			player.write(new SendMessagePacket("You at least need a " + skill + " level of " + lvlReq + " to " + event + "."));
			return false;
		}
		return true;
	}

	/**
	 * Returns if a skill is currently active
	 * 
	 * @param player
	 *            The {@link Player} To check if the skill is active for
	 * @param skill
	 *            The id of the skill to check for
	 * @return If the skill is currently active
	 */
	public static boolean isSkillActive(Player player, int skill) {
		return player.getAttribute("skill_" + skill + "_active", false);
	}

	/**
	 * Sets a skill active/inactive
	 * 
	 * @param player
	 *            The {@link Player} to set the skill active/inactive for
	 * @param skill
	 *            The id of the skill
	 * @param active
	 *            The active status of the skill
	 */
	public static void setSkillActive(Player player, int skill, boolean active) {
		player.setAttribute("skill_" + skill + "_active", active);
	}
}
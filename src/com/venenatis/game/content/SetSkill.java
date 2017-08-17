package com.venenatis.game.content;

import java.util.Arrays;
import java.util.Optional;

import com.venenatis.game.model.Skills;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.dialogue.SimpleDialogues;
import com.venenatis.game.model.entity.player.dialogue.input.InputAmount;
import com.venenatis.game.model.masks.UpdateFlags.UpdateFlag;

/**
 * Handles setting skills.
 * 
 * @author Daniel
 *
 */
public class SetSkill {

	public enum SkillData {
		ATTACK("Attack", Skills.ATTACK, 8654),
		STRENGTH("Strength", Skills.STRENGTH, 8657),
		DEFENCE("Defence", Skills.DEFENCE, 8660),
		RANGE("Range", Skills.RANGE, 8663),
		PRAYER("Prayer", Skills.PRAYER, 8666),
		MAGIC("Magic", Skills.MAGIC, 8669),
		HITPOINTS("Hitpoints", Skills.HITPOINTS, 8655);

		private final String name;
		private final int skill;
		private final int button;

		private SkillData(String name, int skill, int button) {
			this.name = name;
			this.skill = skill;
			this.button = button;
		}

		public String getName() {
			return name;
		}

		public int getSkill() {
			return skill;
		}
		
		public static Optional<SkillData> forId(int id) {
			return Arrays.stream(values()).filter(a -> a.button == id).findAny();
		}
	}

	public static boolean handle(Player player, int button) {
		if (player.getRights().isIron(player)) {
			player.getActionSender().sendMessage("Ironmen can't change their levels.");
			return false;
		}

		if (player.getDuelArena().isDueling()) {
			player.getActionSender().sendMessage("You cannot use this in the duel arena.");
			return false;
		}

		if (!SkillData.forId(button).isPresent()) {
			return false;
		}

		SkillData skill = SkillData.forId(button).get();

		if (player.getEquipment().getTakenSlots() != 0) {
			SimpleDialogues.sendStatement(player, "You must remove all your worn equipment before doing this!");
			return false;
		}

		player.getActionSender().sendInput((InputAmount) input -> {
			
			if (skill.getSkill() == Skills.HITPOINTS && input < 10) {
				input = 10;
			}
			
			if (input > 99) {
				input = 99;
			}

			player.getSkills().setMaxLevel(skill.getSkill(), input);
			player.setCombatLevel(player.getSkills().getCombatLevel());
			player.getActionSender().sendMessage("<col=255>Your " + skill.getName() + " level is now " + input + ". Combat level: " + player.getCombatLevel());
			player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
		});
		return true;
	}

}
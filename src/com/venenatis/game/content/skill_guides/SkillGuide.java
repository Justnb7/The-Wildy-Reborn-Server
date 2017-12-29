package com.venenatis.game.content.skill_guides;

import java.util.Arrays;

import com.venenatis.game.model.Item;
import com.venenatis.game.model.Skills;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.dialogue.SimpleDialogues;

public class SkillGuide {

	public static void openInterface(Player player, int skillId) {
		if(skillId == 22) {
			SimpleDialogues.sendStatement(player, "We do not have construction yet.");
			return;
		}
		player.skillGuide = skillId;
		//player.getAttribute("skill_guide", (int)skillId);
		player.getActionSender().sendString(Skills.SKILL_NAME[skillId], 56507);
		SkillGuideDefinitions def = SkillGuideDefinitions.getBySkillId(skillId);
		if(def == null) {
			System.err.println("No options for this skill guide! Skill -> "+skillId);
			return;
		}
		for (int index = 0; index < 14; index++) {
			player.getActionSender().sendString(index < def.getOptions().length ? def.getOptions()[index] : "", 56510 + index);
		}	
		
		openOption(player, 0);
		player.getActionSender().sendInterface(56500);
	}
	
	public static void openOption(Player player, int option) {
		SkillGuideDefinitions def = SkillGuideDefinitions.getBySkillId(player.skillGuide/*(int) player.getAttributes().get("skill_guide")*/);
		/*if(option == 0) {
			System.err.println("No options for this skill guide!");
			return;
		}*/
		if (option < def.getOptions().length) {
			SkillGuideContent content = def.getContent()[option];
			if (content != null) {
				player.getActionSender().sendString(def.getOptions()[option], 56508);
				Item[] items = new Item[content.getItemIds().length];
				
				for (int i = 0; i < content.getItemIds().length; i++) {
					items[i] = new Item(content.getItemIds()[i], 1);
				}
				
				player.getActionSender().sendItemsOnInterface(56525, Arrays.asList(items));
				player.getActionSender().setScrollPosition(56509, 0, content.getItemIds().length * 32 + 5);

				player.getActionSender().sendSkillGuideInterface(content, def.getOptions().length);
			}
		}
	}
	
}

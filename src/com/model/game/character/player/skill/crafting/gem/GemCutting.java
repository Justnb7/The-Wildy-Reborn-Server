package com.model.game.character.player.skill.crafting.gem;

import com.model.game.character.Animation;
import com.model.game.character.player.Player;
import com.model.game.character.player.Skills;
import com.model.game.character.player.packets.encode.impl.SendClearScreen;
import com.model.game.character.player.packets.encode.impl.SendMessagePacket;
import com.model.game.character.player.skill.SkillTask;
import com.model.task.Stackable;
import com.model.task.Walkable;

/**
 * 
 * @author Patrick van ELderen
 * @date 1-4-2016 00:27AM
 */
	public class GemCutting extends SkillTask {

		/**
		 * The id of a chisel
		 */
		private static final int CHISEL_ID = 1755;

		/**
		 * The data for the gem being cut
		 */
		private final Gems data;

		/**
		 * Creates an unwalkable task for the player
		 *
		 * @param player
		 *            The player cutting gems
		 * @param data
		 *            The data for the gem to be cut
		 */
		public GemCutting(Player player, Gems data) {
			super(player, 4, Walkable.NON_WALKABLE, Stackable.NON_STACKABLE, false);
			this.data = data;
		}

		/**
		 * Attempts to cut gems in your inventory
		 *
		 * @param player
		 *            The player attempting to cut a gem
		 * @param item1
		 *            The first item used
		 * @param item2
		 *            The second item used
		 * @return
		 */
		public static boolean attemptGemCutting(Player player, int item1, int item2) {
			int gemId = item1 == CHISEL_ID ? item2 : item1;

			Gems data = Gems.forId(gemId);
			if (!meetsRequirements(player, data)) {
				return false;
			}
			player.playAnimation(Animation.create(data.getAnimation()));
			player.write(new SendClearScreen());
			player.setSkillTask(new GemCutting(player, data));
			return true;
		}

		/**
		 * Meets the requirements to start cutting gems
		 *
		 * @param player
		 *            The player cutting the gem
		 * @param data
		 *            The data for the gem being cut
		 * @return The player can cut the gem
		 */
		private static boolean meetsRequirements(Player player, Gems data) {
			if (data == null) {
				return false;
			}
			if (player.getSkills().getLevel(Skills.CRAFTING) < data.getLevel()) {
				player.write(new SendMessagePacket("You need a crafting level of " + data.getLevel() + " to cut this gem."));
				return false;
			}
			return true;
		}

		@Override
		public void execute() {
			if (getPlayer() == null || !getPlayer().isActive()) {
				stop();
				return;
			}
			if (!getPlayer().getItems().playerHasItem(CHISEL_ID, 1)) {
				getPlayer().write(new SendMessagePacket("You do not have a chisel."));
				stop();
				return;
			}
			if (!getPlayer().getItems().playerHasItem(data.getUncutId(), 1)) {
				getPlayer().write(new SendMessagePacket("You have run out of gems."));
				stop();
				return;
			}
			getPlayer().playAnimation(Animation.create(data.getAnimation()));
			getPlayer().getItems().deleteItem(data.getUncutId(), 1);
			getPlayer().getItems().addItem(data.getCutId(), 1);
			getPlayer().getSkills().addExperience(Skills.CRAFTING, data.getExp());
		}
}

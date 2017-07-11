package com.venenatis.game.content.skills.prayer;

import java.util.HashMap;
import java.util.Map;

import com.venenatis.game.constants.Constants;
import com.venenatis.game.location.Location;
import com.venenatis.game.model.Item;
import com.venenatis.game.model.Skills;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.task.Task;
import com.venenatis.game.util.Utility;
import com.venenatis.server.Server;

/**
 * For prayer related activities.
 *
 * @author Patrick van Elderen
 */
public class Prayer {

	private Player player;

	/**
	 * Constructs a new prayer skill.
	 *
	 * @param player
	 *            For this player.
	 */
	public Prayer(Player player) {
		this.player = player;
	}

	/**
	 * Pray an altar to renew your prayer points
	 * @param loc
	 *         The location of the alter
	 */
	public void prayAltar(Location loc) {
		if (player.getRights().isDonator(player)) {
			if (player.getLastAltarPrayer() < 120000) {
				player.getActionSender().sendMessage("You can only use the altar to restore your special attack every 2 minutes");
			} else {
				player.setSpecialAmount(100);
				player.setLastAltarPrayer(System.currentTimeMillis());
				player.getSkills().increaseLevelToMaximum(Skills.HITPOINTS, player.getSkills().getLevelForExperience(Skills.HITPOINTS));
			}
        } else if (Utility.random(4) == 0) {
        	player.getActionSender().sendMessage("Did you know if you were a donator you'd restore special energy and hitpoints?");
        }
		if (player.getPrayerPoint() >= player.getSkills().getLevelForExperience(Skills.PRAYER)) {
			player.getActionSender().sendMessage("You already have full prayer points.");
			return;
		}
		player.getSkills().setLevel(Skills.PRAYER, player.getSkills().getLevelForExperience(Skills.PRAYER));
		player.getSkills().setPrayerPoints(player.getSkills().getLevelForExperience(Skills.PRAYER), true);
		if (player.getActionSender() != null) {
			player.getActionSender().sendSkills();
		}
		player.playAnimation(Animation.create(645));
		player.getSkills().setLevel(Skills.PRAYER, player.getSkills().getLevelForExperience(Skills.PRAYER));
		player.getActionSender().sendMessage("You pray at the altar...");
	}

	/**
	 * Burying the bone for prayer experience
	 * @param item
	 *         The bone we bury
	 * @param slot
	 *         The inventory slot we remove the bone from
	 */
	public void bury(final Item item, int slot) {
		Bone bone = Bone.forId(item.getId());
		if (bone == null)
			return;
		if (bone.getId() == item.getId()) {
			player.playAnimation(Animation.create(827));
			player.getActionSender().sendMessage("You dig a hole in the ground.");
			player.getInventory().removeSlot(slot, 1, true);
			Server.getTaskScheduler().schedule(new Task(1) {
				public void execute() {
					player.getSkills().addExperience(Skills.PRAYER, bone.getXp() * Constants.PRAYER_EXP_MODIFIER);
					player.getActionSender().sendMessage("You bury the bones...");
					stop();
				}
			});
		}
	}

	/**
	 * We use offer bones on an altar for more experience.
	 * @param item
	 *         The bone we offer
	 */
	public void bonesOnAltar(Item item) {
		Bone bone = Bone.forId(item.getId());
		if (bone == null)
			return;
		if (bone.getId() == item.getId()) {
			player.playAnimation(Animation.create(883, 10));
			player.getInventory().remove(item);
			player.getActionSender().sendMessage("You use the bones on the altar.");
			Server.getTaskScheduler().schedule(new Task(1) {
				public void execute() {
					/**
					 * The player is not active stop the task
					 */
					if (!player.isActive()) {
						this.stop();
					}
					
					/**
					 * We ran out of bones stop the task
					 */
					if (!player.getInventory().contains(bone.getId())) {
						player.getActionSender().sendMessage("You ran out of bones!");
						this.stop();
						return;
					}
					
					/**
					 * Offer the bones and reward experience
					 */
					player.getSkills().addExperience(Skills.PRAYER, bone.getXp() * Constants.PRAYER_EXP_MODIFIER * 2);
					this.stop();
				}

				@Override
				public void onStop() {
					player.playAnimation(Animation.create(65535));
				}
			});
		}
	}

	public enum Bone {

		NORMAL_BONES(526, 4), 
		BURNT_BONES(528, 4), 
		BAT_BONES(530, 5), 
		BIG_BONES(532, 15), 
		BABYDRAGON_BONES(534, 30), 
		DRAGON_BONES(536, 72), 
		LAVA_DRAGON_BONES(11943, 85), 
		WOLF_BONES(2859, 4), 
		JOGRE_BONES(3125, 15), 
		DAGANNOTH_BONES(6729, 72), 
		WYVERN_BONES(6812, 50), 
		SHAIKAHAN_BONES(3123, 25), 
		OURG_BONES(4834, 140);

		private int id, xp;

		public int getId() {
			return id;
		}

		public int getXp() {
			return xp;
		}

		/**
		 * A map that contains all the "bones"
		 */
		private static Map<Integer, Bone> bones = new HashMap<Integer, Bone>();

		static {
			for (Bone bone : Bone.values()) {
				bones.put(bone.getId(), bone);
			}
		}

		/**
		 * Grab the bone id
		 * 
		 * @param id
		 * @return
		 */
		public static Bone forId(int id) {
			return bones.get(id);
		}

		private Bone(int id, int xp) {
			this.id = id;
			this.xp = xp;
		}
	}

	/**
	 * The id for the bone crusher
	 */
	private final int BONE_CRUSHER_ID = 13116;

	/**
	 * Checks if the player has a bone crusher
	 * 
	 * @param player
	 *            The {@link Player} to check for a bone crusher
	 * @return If the player has a bone crusher
	 */
	public boolean isHoldingBoneCrusher(Player player) {
		if (player.getInventory().contains(BONE_CRUSHER_ID)) {
			return true;
		}
		return false;
	}
}

package com.model.game.character.player.skill.prayer;

import java.util.HashMap;
import java.util.Map;

import com.model.Server;
import com.model.game.character.Animation;
import com.model.game.character.player.Player;
import com.model.game.character.player.Skills;
import com.model.game.character.player.packets.out.SendSoundPacket;
import com.model.task.ScheduledTask;
import com.model.task.Stackable;
import com.model.task.Walkable;
import com.model.utility.Utility;
import com.model.utility.json.definitions.ItemDefinition;

/**
 * 
 * @author Patrick van Elderen
 * @date 28/03/2016
 *
 */

public class Burying {
	
	public enum Bone {
		NORMAL(526, 100),

		BURNT(528, 100),

		WOLF(2859, 100),

		MONKEY(3183, 125),

		BAT(530, 125),

		BIG(532, 200),

		JOGRE(3125, 200),

		ZOGRE(4812, 250),

		SHAIKAHAN(3123, 300),

		BABY(534, 350),

		WYVERN(6812, 400),

		DRAGON(536, 500),

		FAYRG(4830, 525),

		RAURG(4832, 550),

		DAGANNOTH(6729, 650),

		OURG(4834, 750),

		FROST_DRAGON(18830, 850);

		
		/**
		 * The bone item id
		 */
		private int id;
		
		/**
		 * The experience gained from burying this bone
		 */
		private int experience;

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
		 * @param id
		 * @return
		 */
		public static Bone forId(int id) {
			return bones.get(id);
		}

		private Bone(int id, int experience) {
			this.id = id;
			this.experience = experience;
		}

		/**
		 * Represents the bone iD of the bone we are burying
		 * @return the bone id
		 */
		public int getId() {
			return id;
		}

		/**
		 * Experience gained from the bone we burried
		 * @return experience
		 */
		public int getExperience() {
			return experience;
		}
		
		/**
		 * Bury animation
		 */
		private static int BURY_ANIMATION = 827;
		
		/**
		 * This method is used to bury the bone
		 * @param player
		 * @param itemId
		 * @return
		 */
		public static boolean bury(Player player, int itemId) {
			Bone bone = Bone.forId(itemId);

			if (bone == null)
				return false;
			if (player.getBoneDelay() > Utility.currentTimeMillis())
				return false;
			if (bone.getId() == itemId) {
				player.addBoneDelay(3000);
				player.write(new SendSoundPacket(380, 1, 24));
				player.playAnimation(Animation.create(BURY_ANIMATION));
				player.getActionSender().sendMessage("You dig a hole in the ground...");
				ItemDefinition itemDef = ItemDefinition.forId(itemId);
				Server.getTaskScheduler().schedule(new ScheduledTask(2) {
					@Override
					public void execute() {
						player.getActionSender().sendMessage("You bury the " + itemDef.getName().toLowerCase());
						player.getItems().deleteItem(itemId, 1);
						int xp = bone.getExperience();
						player.getSkills().addExperience(Skills.PRAYER, xp);
						//player.write(new SendGameMessage("prayer exp received: "+bone.getExperience());
						this.stop();
					}
				});
			}
			return true;
		}

		/**
		 * Using bones on altar animation
		 */
		private static final int ALTAR_ANIMATION = 896;
		
		/**
		 * Method used for using bones on the prayer altar
		 * @param player
		 * @param itemId
		 * @return
		 */
		public static boolean bonesOnAltar(Player player, int itemId) {
			Bone bone = Bone.forId(itemId);
			if (bone == null)
				return false;
			if (bone.getId() == itemId) {
				player.playAnimation(Animation.create(ALTAR_ANIMATION));
				Server.getTaskScheduler().schedule(new ScheduledTask(player, 4, Walkable.NON_WALKABLE, Stackable.NON_STACKABLE) {
					@Override
					public void execute() {
						if(!player.isActive()) {
							this.stop();
						}
						if (!player.getItems().playerHasItem(bone.getId())) {
							player.getActionSender().sendMessage("You ran out of bones!");
							this.stop();
							return;
						}
						player.playAnimation(Animation.create(ALTAR_ANIMATION));
						player.getItems().deleteItem(itemId, 1);
						player.getSkills().addExperience(Skills.PRAYER, bone.getExperience() * 2);
						player.getActionSender().sendMessage("You interact the bones with altar and gain x2 experience!");
					}

					@Override
					public void onStop() {
						player.playAnimation(Animation.create(65535));

					}
				});
			}
			return true;
		}
	}
	
	/**
	 * The id for the bone crusher
	 */
	private static final int BONE_CRUSHER_ID = 13116;

	/**
	 * Checks if the player has a bone crusher
	 * 
	 * @param player
	 *            The {@link Player} to check for a bone crusher
	 * @return If the player has a bone crusher
	 */
	public static boolean isHoldingBoneCrusher(Player player) {
		for (int i : player.playerInventory) {
			int id = i - 1;
			if (id == BONE_CRUSHER_ID) {
				return true;
			}
		}
		return false;
	}
	
}

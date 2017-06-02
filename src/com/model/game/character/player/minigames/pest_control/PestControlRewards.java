package com.model.game.character.player.minigames.pest_control;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.model.game.character.player.Player;
import com.model.game.character.player.Skills;
import com.model.game.definitions.ItemDefinition;
import com.model.game.item.Item;
import com.model.utility.Utility;

public class PestControlRewards {
	
	/**
	 * The player that will be managing their pest control rewards
	 */
	private Player player;
	
	/**
	 * The current reward the player has
	 */
	private Reward reward;
	
	/**
	 * The level required to purchase experience 
	 */
	static final int REQUIRED_LEVEL = 40;
	
	/**
	 * Creats a new class for the signified player. A new class
	 * is created for each player because there are members of the class
	 * that are required for each individual player.
	 * 
	 * @param player	the player this class is being created for.
	 */
	public PestControlRewards(Player player) {
		this.player = player;
	}
	
	/**
	 * Displays the reward interface
	 */
	public void showInterface() {
		player.getActionSender().moveComponent(0, - 100, 37084);
		player.getActionSender().sendString("Unselected", 37003);
		player.getActionSender().sendString(Utility.insertCommas(Integer.toString(player.getPestControlPoints())) + " Pts", 37007);
		player.getActionSender().sendInterface(37000);
	}
	
	public boolean click(int buttonId) {
		if (buttonId == 144138) {
			if (reward == null) {
				player.getActionSender().sendMessage("You must select an option first before confirming.");
			} else {
				reward.purchase(player);
				player.getActionSender().sendString(Utility.insertCommas(Integer.toString(player.getPestControlPoints())) + " Pts", 37007);
			}
			return true;
		}
		for (RewardButton button : RewardButton.values()) {
			if (button.buttonId == buttonId) {
				reward = button.reward;
				player.getActionSender().moveComponent(button.xOffset, button.yOffset, 37084);
				player.getActionSender().sendString(reward.getCost() + " " + (reward.getCost() == 1 ? "point" : "points"), 37003);
				return true;
				
			}
		}
		return false;
	}
	
	enum RewardButton {
		ATTACK_EXPERIENCE_1(144150, 0, 0, new ExperienceReward(1, Skills.ATTACK, 10_000)),
		ATTACK_EXPERIENCE_10(144151, 0, 0, new ExperienceReward(10, Skills.ATTACK, 10_000)),
		ATTACK_EXPERIENCE_100(144152, 0, 0, new ExperienceReward(100, Skills.ATTACK, 10_000)),
		DEFENCE_EXPERIENCE_1(144155, 0, 40, new ExperienceReward(1, Skills.DEFENCE, 10_000)),
		DEFENCE_EXPERIENCE_10(144156, 0, 40, new ExperienceReward(10, Skills.DEFENCE, 10_000)),
		DEFENCE_EXPERIENCE_100(144157, 0, 40, new ExperienceReward(100, Skills.DEFENCE, 10_000)),
		MAGIC_EXPERIENCE_1(144160, 0, 80, new ExperienceReward(1, Skills.MAGIC, 10_000)),
		MAGIC_EXPERIENCE_10(144161, 0, 80, new ExperienceReward(10, Skills.MAGIC, 10_000)),
		MAGIC_EXPERIENCE_100(144162, 0, 80, new ExperienceReward(100, Skills.MAGIC, 10_000)),
		PRAYER_EXPERIENCE_1(144165, 0, 120, new ExperienceReward(1, Skills.PRAYER, 1_000)),
		PRAYER_EXPERIENCE_10(144166, 0, 120, new ExperienceReward(10, Skills.PRAYER, 1_000)),
		PRAYER_EXPERIENCE_100(144167, 0, 120, new ExperienceReward(100, Skills.PRAYER, 1_000)),
		STRENGTH_EXPERIENCE_1(144170, 210, 0, new ExperienceReward(1, Skills.STRENGTH, 10_000)),
		STRENGTH_EXPERIENCE_10(144171, 210, 0, new ExperienceReward(10, Skills.STRENGTH, 10_000)),
		STRENGTH_EXPERIENCE_100(144172, 210, 0, new ExperienceReward(100, Skills.STRENGTH, 10_000)),
		RANGE_EXPERIENCE_1(144175, 210, 40, new ExperienceReward(1, Skills.RANGE, 10_000)),
		RANGE_EXPERIENCE_10(144176, 210, 40, new ExperienceReward(10, Skills.RANGE, 10_000)),
		RANGE_EXPERIENCE_100(144177, 210, 40, new ExperienceReward(100, Skills.RANGE, 10_000)),
		HITPOINTS_EXPERIENCE_1(144180, 210, 80, new ExperienceReward(1, Skills.HITPOINTS, 3_300)),
		HITPOINTS_EXPERIENCE_10(144181, 210, 80, new ExperienceReward(10, Skills.HITPOINTS, 3_300)),
		HITPOINTS_EXPERIENCE_100(144182, 210, 80, new ExperienceReward(100, Skills.HITPOINTS, 3_300)),
		HERB_PACK(144189, 0, 180, new PackReward(30, PackReward.HERB_PACK)),
		SEED_PACK(144192, 0, 220, new PackReward(15, PackReward.SEED_PACK)),
		MINERAL_PACK(144195, 210, 180, new PackReward(15, PackReward.MINERAL_PACK)),
		VOID_MACE(144198, 0, 280, new ItemReward(250, new Item(8841))),
		VOID_KNIGHT_ROBE(144201, 0, 320, new ItemReward(250, new Item(8840))),
		VOID_MAGE_HELM(144204, 0, 360, new ItemReward(200, new Item(11663))),
		VOID_MELEE_HELM(144207, 0, 400, new ItemReward(200, new Item(11665))),
		VOID_KNIGHT_TOP(144210, 210, 280, new ItemReward(250, new Item(8839))),
		VOID_KNIGHT_GLOVES(144213, 210, 320, new ItemReward(150, new Item(8842))),
		VOID_RANGE_HELM(144216, 210, 360, new ItemReward(200, new Item(11664)));
		
		/**
		 * The button for this reward
		 */
		private final int buttonId;
		
		/**
		 * The x and y offset of the button
		 */
		private final int xOffset, yOffset;
		
		/**
		 * The reward 
		 */
		private final Reward reward;
		
		/**
		 * The button with an id and reward associated with it
		 * @param buttonId	the button id
		 * @param reward	the reward
		 */
		private RewardButton(int buttonId, int xOffset, int yOffset, Reward reward) {
			this.buttonId = buttonId;
			this.xOffset = xOffset;
			this.yOffset = yOffset;
			this.reward = reward;
		}
		
	}
	
	static abstract class Reward { 
		
		/**
		 * The cost of the reward
		 */
		protected final int cost;
		
		/**
		 * Creates a new reward with an initial cost
		 * @param cost	the cost of the reward
		 */
		Reward(int cost) { 
			this.cost = cost;
		}
		
		/**
		 * The procedure for purchasing an item
		 */
		abstract void purchase(Player player);
		
		/**
		 * The cost of the purchase
		 * @return	the cost in points
		 */
		public int getCost() {
			return cost;
		}
	}
	
	static class ExperienceReward extends Reward {
		/**
		 * The skill id that will be given the experience
		 */
		private int skillId;
		
		/**
		 * The default experience for the reward
		 */
		private int experience;
		
		/**
		 * Creates a new reward with just a cost
		 * @param cost	the cost of the reward
		 */
		ExperienceReward(int cost) {
			super(cost);
		}
		
		/**
		 * Creates a new reward with a cost and skillId
		 * @param cost		the cost of the reward
		 * @param skillId	the skill obtaining the experience
		 */
		ExperienceReward(int cost, int skillId, int experience) {
			super(cost);
			this.skillId = skillId;
			this.experience = experience;
		}
		
		@Override
		public void purchase(Player player) {
			if (player.getPestControlPoints() < cost) {
				player.getActionSender().sendMessage("You do not have the pest control points to purcahse this experience.");
				return;
			}
			if (player.getSkills().getLevelForExperience(skillId) < REQUIRED_LEVEL) {
				player.getActionSender().sendMessage("You need a level of " + REQUIRED_LEVEL + " to purchase this experience.");
				return;
			}
			player.setPestControlPoints(player.getPestControlPoints() - cost);
			player.getSkills().addExperience(experience * cost, skillId);
			player.getActionSender().sendMessage("You have received " + (experience * cost) + " experience in exchange for "+ cost + " points.");
		}
	}
	
	static class ItemReward extends Reward {

		/**
		 * The item received as the reward
		 */
		private Item item;
		
		/**
		 * Creates a new item reward with an initial cost
		 * @param cost	the cost of the item reward
		 */
		ItemReward(int cost) {
			super(cost);
		}
		
		/**
		 * Creates a new item reward with an initial cost as well as the item given
		 * @param cost	the cost of the reward
		 * @param item	the item given to the player
		 */
		ItemReward(int cost, Item item) {
			super(cost);
			this.item = item;
		}

		@Override
		void purchase(Player player) {
			if (player.getPestControlPoints() < cost) {
				player.getActionSender().sendMessage("You do not have the pest control points to purcahse this experience.");
				return;
			}
			if (player.getInventory().getFreeSlots() == 0) {
				player.getActionSender().sendMessage("You need at least one free slot to purchase this item reward.");
				return;
			}
			player.setPestControlPoints(player.getPestControlPoints() - cost);
			player.getInventory().add(new Item(item.getId(), item.getAmount()));
			ItemDefinition itemDef = ItemDefinition.get(item.getId());
			String name = itemDef == null ? "a item" : itemDef.getName();
			player.getActionSender().sendMessage("You have received a " + name + " in exchange for " + cost + " pc points.");
		}
		
	}
 	
	static class PackReward extends Reward {
		
		/**
		 * An array of items that are obtainable from the herb pack
		 */
		static final Item[] HERB_PACK = {
			new Item(200, 10),
			new Item(202, 10),
			new Item(204, 10),
			new Item(206, 8),
			new Item(208, 5),
			new Item(210, 7),
			new Item(212, 6),
			new Item(214, 5),
			new Item(216, 5),
			new Item(218, 5),
			new Item(220, 3)
		};
		
		/**
		 * An array of items that are obtainable from the mineral pack
		 */
		static final Item[] MINERAL_PACK = {
			new Item(437, 50),
			new Item(439, 50),
			new Item(441, 40),
			new Item(445, 35),
			new Item(454, 30),
			new Item(448, 25),
			new Item(450, 10),
			new Item(452, 5)
		};
		
		static final Item[] SEED_PACK = {
			new Item(5291, 15),
			new Item(5292, 15),
			new Item(5293, 15),
			new Item(5294, 15),
			new Item(5295, 5),
			new Item(5296, 5),
			new Item(5297, 10),
			new Item(5298, 10),
			new Item(5299, 7),
			new Item(5300, 5),
			new Item(5301, 6),
			new Item(5302, 4),
			new Item(5303, 4),
			new Item(5304, 3),
		};
		
		/**
		 * The pack of items
		 */
		private Item[] pack;

		/**
		 * Creates a new reward with a determined cost
		 * @param cost	the cost of the reward
		 */
		PackReward(int cost) {
			super(cost);
		}
		
		/**
		 * Creates a new reward with an initial cost and pack of items
		 * @param cost	the cost of the reward
		 * @param pack	the pack fo items received from the reward
		 */
		PackReward(int cost, Item[] pack) {
			super(cost);
			this.pack = pack;
		}

		@Override
		void purchase(Player player) {
			if (player.getPestControlPoints() < cost) {
				player.getActionSender().sendMessage("You do not have the pest control points to purcahse this experience.");
				return;
			}
			if (player.getInventory().getFreeSlots() < 5) {
				player.getActionSender().sendMessage("You need at least 5 free slots to purchase this pack.");
				return;
			}
			player.setPestControlPoints(player.getPestControlPoints() - cost);
			int amount = 4 + Utility.random(1);
			List<Item> list = new ArrayList<>(Arrays.asList(pack));
			List<Item> receive = new ArrayList<>(amount);
			while (amount-- > 0) {
				Item item = list.get(Utility.random(list.size() - 1));
				item.setAmount(1 + Utility.random(item.getAmount()));
				receive.add(item);
				list.remove(item);
			}
			receive.forEach(item -> player.getInventory().add(new Item(item.getId(), item.getAmount())));
		}
	}

}
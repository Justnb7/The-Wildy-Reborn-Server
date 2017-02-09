package com.model.game.character.player.content;

import com.model.game.character.player.Player;
import com.model.game.character.player.PlayerUpdating;
import com.model.game.character.player.packets.encode.impl.SendClearScreen;
import com.model.game.character.player.packets.encode.impl.SendMessagePacket;
import com.model.game.item.Item;
import com.model.game.item.ground.GroundItem;
import com.model.game.item.ground.GroundItemHandler;
import com.model.utility.Utility;

public class Gamble {
	
	private final Player player;
	
	public Gamble(Player player) {
		this.player = player;
	}
	
	private static int[][] shiny_rewards = { {11840, 1}, {11836, 1}, {11770, 1}, {11771, 1}, {11772, 1}, {11759, 1}, {11773, 1}, {11804, 1}, {11806, 1}, {11808, 1}, {4152, 2}, {6586, 2}, {11836, 1}, {10362, 1}, {11283, 1}, {4224, 1}, {4212, 1}, {10926, 5}, {13899, 1}, {13902, 1} };
	
	private static int[] randomReward() {
		return shiny_rewards[(int) (Math.random() * shiny_rewards.length)];
	}
	
	public void shinyKeyChest() {
		int loot[] = randomReward();
		int item = loot[0], amount = loot[1];
		if (!player.getItems().playerHasItem(85))
			return;
		if (player.getItems().playerHasItem(85) && player.getItems().getFreeSlots() >= 1) {
				player.getItems().deleteItem(85);
				player.getItems().addItem(item, amount);
		} else {
			player.getItems().deleteItem(85);
			GroundItemHandler.createGroundItem(new GroundItem(new Item(item, amount), player.getX(), player.getY(), player.getZ(), player));
			player.write(new SendMessagePacket("Your "+player.getItems().getItemName(item)+" was dropped beneath you because you had no space."));
		}
		PlayerUpdating.executeGlobalMessage("<img=12>[@red@Server@bla@]: @dbl@"+player.getName()+"@red@ has received a @dbl@"+player.getItems().getItemName(item)+"@red@ from a Magic chest key.");
	}
	
	
private static int[][] magic_key_rewards = { {12422, 1}, {12424, 1}, {12426, 1}, {12437, 1}, {12806, 1}, {12807, 1}, {11824, 1}, {11826, 1}, {11828, 1}, {11830, 1}, {11832, 1}, {11834, 1}, {11836, 1}, {11864, 1}, {11865, 1}, {8921, 1}, {11784, 1}, {10362, 1}, {11283, 1}, {4224, 1}, {4212, 1}, {13899, 1}, {13902, 1}, {10330, 1}, {10332, 1}, {10334, 1}, {10336, 1}, {10338, 1}, {10340, 1}, {10342, 1}, {10344, 1}, {10346, 1}, {10348, 1}, {10350, 1}, {10352, 1}/*, {22494, 1}*/ };
	
	private static int[] randomRewards() {
		return magic_key_rewards[(int) (Math.random() * magic_key_rewards.length)];
	}
	
	public void magicKeyChest() {
		int loot[] = randomRewards();
		int item = loot[0], amount = loot[1];
		if (!player.getItems().playerHasItem(1547))
			return;
		if (player.getItems().playerHasItem(1547) && player.getItems().getFreeSlots() >= 1) {
				player.getItems().deleteItem(1547);
				player.getItems().addItem(item, amount);
		} else {
			player.getItems().deleteItem(1547);
			GroundItemHandler.createGroundItem(new GroundItem(new Item(item, amount), player.getX(), player.getY(), player.getZ(), player));
			player.write(new SendMessagePacket("Your "+player.getItems().getItemName(item)+" was dropped beneath you because you had no space."));
		}
		PlayerUpdating.executeGlobalMessage("<img=12>[@red@Server@bla@]: @dbl@"+player.getName()+"@red@ has received a @dbl@"+player.getItems().getItemName(item)+"@red@ from a Magic chest key.");
	}
	
	private final int[][] rewards = { {4151, 1}, {6585, 1}, {11235, 1}, {11283, 1}, {537, 100}, {12436, 1}, {12853, 1}, {4153, 1}, {12849, 1}, {12765, 1}, {12766, 1}, {12767, 1}, {12768, 1}, {12804, 1}, {13071, 1}, {3145, 25}, {11836, 1}, {10926, 10}, {4152, 3}, {6570, 1}, {6586, 2} };
	
	private final int[] getReward() {
		return rewards[(int) (Math.random() * rewards.length)];
	}
	
	public void mysteryBox() {
		int reward[] = getReward();
		
		if (player.getItems().playerHasItem(6199) && player.getItems().getFreeSlots() >= 1) {
			player.getItems().deleteItem(6199);
			player.getItems().addItem(reward[0], reward[1]);
			PlayerUpdating.executeGlobalMessage("<img=12>[@red@Server@bla@]: @dbl@"+player.getName()+"@red@ opened a Mystery box and received "+reward[1]+ "x @blu@"+player.getItems().getItemName(reward[0])+"@red@!");
		}
		
	}
	
	private final int[] common_reward = {11808, 11806, 11804};
	
	private final int[] uncommon_reward = {10887};
	
	private final int[] rare_reward = { 14484, 11785, 11802, 12904, 11791 };
	
	private final int commonReward() {
		return common_reward[(int) (Math.random() * common_reward.length)];
	}
	
	private final int uncommonReward() {
		return uncommon_reward[(int) (Math.random() * uncommon_reward.length)];
	}
	
	private final int rareReward() {
		return rare_reward[(int) (Math.random() * rare_reward.length)];
	}
	
	public void weaponGamble() {
		int common_reward = commonReward();
		int uncommon_reward = uncommonReward();
		int rare_reward = rareReward();
		
		int roll = Utility.getRandom(50);

		if (player.getItems().playerHasItem(13307, 75)) {
			if (player.getItems().getFreeSlots() >= 1) {
				if (roll > 0 && roll <= 5) {
					player.getItems().addItem(rare_reward, 1);
					PlayerUpdating.executeGlobalMessage("<img=12>[@red@Server@bla@]: @red@"+player.getName()+ "@bla@ has received an @blu@ "+player.getItems().getItemName(rare_reward)+ "@bla@ from the weapon gamble.");
				} else if (roll > 5 && roll < 10) {
					player.getItems().addItem(uncommon_reward, 1);
				} else {
					player.getItems().addItem(common_reward, 1);
				}
				player.getItems().deleteItem(13307, 75);
			} else {
				player.write(new SendMessagePacket("You do not have space in your backpack to open the weapon gamble."));
				player.write(new SendClearScreen());
				return;
			}
		} else {
			player.write(new SendMessagePacket("You do not have enough blood money to open the weapon gamble."));
			player.write(new SendClearScreen());
		}
	}
	
	private final int[] armour_reward = {11826, 11828, 11830, 11832, 11834, 11836, 11283};
	
	private final int randomArmour() {
		return armour_reward[(int) (Math.random() * armour_reward.length)];
	}
	
	public void armourGamble() {
		int reward = randomArmour();
		
		if (player.getItems().playerHasItem(13307, 50)) {
			if (player.getItems().getFreeSlots() >= 1) {
				player.getItems().addItem(reward, 1);
				player.getItems().deleteItem(13307, 50);
			} else {
				player.write(new SendMessagePacket("You do not have enough blood money to open the armour gamble."));
				player.write(new SendClearScreen());
				return;
			}
		} else {
			player.write(new SendMessagePacket("You do not have enough blood money to open the armour gamble."));
			player.write(new SendClearScreen());
		}
	}
	
	public void coinsGamble() {
		int coins = 995;
		int amount = 10_000 + Utility.getRandom(90_000);
		
		if (player.getItems().getFreeSlots() >= 1 && player.getItems().playerHasItem(13307, 1)) {
			player.getItems().addItem(coins, amount);
		} else {
			player.getItems().sendItemToAnyTab(coins, amount);
		}
		
		player.getItems().deleteItem(13307, 1);
		
		player.write(new SendMessagePacket("You have received @blu@"+amount+"@bla@ coins, you have @red@"+player.getPkPoints()+"@bla@ blood money left."));
		
	}
	
    private final int[] void_reward = {8839, 8840, 8842, 13072, 13073, 11663, 11664, 11665};
	
	private final int randomVoid() {
		return void_reward[(int) (Math.random() * void_reward.length)];
	}
	
	public void voidGamble() {
		int reward = randomVoid();
		
		if (player.getItems().playerHasItem(13307, 25)) {
			if (player.getItems().getFreeSlots() >= 1) {
				player.getItems().addItem(reward, 1);
				player.getItems().deleteItem(13307, 25);
			} else {
				player.write(new SendMessagePacket("You do not have enough blood money to open the void gamble."));
				player.write(new SendClearScreen());
				return;
			}
		} else {
			player.write(new SendMessagePacket("You do not have enough blood money to open the void gamble."));
			player.write(new SendClearScreen());
		}
	}
}

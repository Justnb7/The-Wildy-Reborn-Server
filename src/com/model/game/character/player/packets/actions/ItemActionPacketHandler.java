package com.model.game.character.player.packets.actions;

import com.model.Server;
import com.model.game.character.Animation;
import com.model.game.character.player.Player;
import com.model.game.character.player.content.consumable.potion.PotionData;
import com.model.game.character.player.content.rewards.RewardCasket;
import com.model.game.character.player.content.teleport.TeleTabs;
import com.model.game.character.player.content.teleport.TeleTabs.TabData;
import com.model.game.character.player.packets.PacketType;
import com.model.game.character.player.packets.encode.impl.SendMessagePacket;
import com.model.game.character.player.skill.prayer.Prayer.Bone;
import com.model.game.item.container.impl.LootingBagContainer;
import com.model.game.item.container.impl.RunePouchContainer;
import com.model.task.ScheduledTask;

/**
 * Clicking an item, bury bone, eat food etc
 */
public class ItemActionPacketHandler implements PacketType {

	@Override
	public void processPacket(Player player, int packetType, int packetSize) {
		int interfaceIndex = player.getInStream().readSignedWordBigEndianA();
		int slot = player.getInStream().readUnsignedWordA();
		int item = player.getInStream().readUnsignedWordBigEndian();
		player.lastClickedItem = item;
		
		if (interfaceIndex != 3214) {
			return;
		}
		/*
		 * if its an invalid item, refresh the inventory
		 */
		if ((slot < 0) || (slot > 27) || (item != player.playerItems[slot] - 1) || (player.playerItemsN[slot] <= 0)) {
			player.getItems().resetItems(3214);
			return;
		}
		
		if (player.teleporting) {
			return;
		}
		
		if (player.getBankPin().requiresUnlock()) {
			player.isBanking = false;
			player.getBankPin().open(2);
			return;
		}
		
		System.out.println("Item Action 1: " + interfaceIndex + " : " + slot + " : " + item);

		PotionData potion = PotionData.forId(item);
		if (potion != null) {
			player.sendConsumable("potion", potion.getPotionId(), slot);
		}
		
		if (player.getFood().isFood(item)) {
			player.getFood().eat(item, slot);
		}

		TabData tabData = TabData.forId(item);
		if (tabData != null) {
			TeleTabs.useTeleTab(player, slot, tabData);
		}
		
		if(RunePouchContainer.open(player, item)) {
			return;
		}
		
		if(LootingBagContainer.open(player, item)) {
			return;
		}

		Bone bone = Bone.forId(item);
		if (bone != null) {
			player.getSkills().getPrayer().bury(item, slot);
			return;
		}

		player.getHerblore().clean(item);

		switch (item) {
		
		case 21999:
			RewardCasket.armourCasket(player);
			break;

		case 22000:
			RewardCasket.weaponCasket(player);
			break;

		case 22002:
			RewardCasket.cosmeticCasket(player);
			break;

		case 22003:
			RewardCasket.venomCasket(player);
			break;

		case 22004:
			RewardCasket.zenyteCasket(player);
			break;
			
		case 22005:
			RewardCasket.advancedItemsCasket(player);
			break;
		
		case 6199:
			
			break;

		case 4155: //Enchanted Gem
			player.dialogue().start("ENCHANTED_GEM", player);
			break;
			
		default:
			if (player.in_debug_mode())
				player.write(new SendMessagePacket("First item option clicked on a: (" + player.getItems().getItemName(item) + ")"));
			break;
			
		}
		if (item == 952) {
			handleShovel(player);
		}
	}

	private void handleShovel(final Player player) {
		player.playAnimation(Animation.create(830));
		Server.getTaskScheduler().schedule(new ScheduledTask(1) {

			@Override
			public void execute() {
				stop();
			}

			@Override
			public void onStop() {
				doShovelActions(player);

			}
		});
	}

	private void doShovelActions(Player player) {
		
	}
}

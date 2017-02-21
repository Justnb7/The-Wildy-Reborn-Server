package com.model.game.character.player.packets.actions;

import java.util.Arrays;
import java.util.Optional;

import com.model.Server;
import com.model.game.character.Animation;
import com.model.game.character.player.Player;
import com.model.game.character.player.content.achievements.AchievementType;
import com.model.game.character.player.content.achievements.Achievements;
import com.model.game.character.player.content.cluescrolls.ClueDifficulty;
import com.model.game.character.player.content.cluescrolls.ClueScrollContainer;
import com.model.game.character.player.content.cluescrolls.ClueScrollHandler;
import com.model.game.character.player.content.consumable.potion.PotionData;
import com.model.game.character.player.content.rewards.Mysterybox;
import com.model.game.character.player.content.rewards.RewardCasket;
import com.model.game.character.player.content.teleport.TeleTabs;
import com.model.game.character.player.content.teleport.TeleTabs.TabData;
import com.model.game.character.player.packets.PacketType;
import com.model.game.character.player.packets.encode.impl.SendMessagePacket;
import com.model.game.character.player.skill.prayer.Prayer.Bone;
import com.model.game.item.Item;
import com.model.game.item.container.impl.LootingBagContainer;
import com.model.game.item.container.impl.RunePouchContainer;
import com.model.game.location.Location;
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
		
		case 6798:
			player.dialogue().start("TELEPORT_TO_TASK", player);
			break;
		
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
			Mysterybox.open(player);
			break;

		case 4155: //Enchanted Gem
			player.dialogue().start("ENCHANTED_GEM", player);
			break;
			
		case 2677:
		case 2801:
		case 2722:
		case 12073:
			if (player.clueContainer == null) {
				Optional<ClueDifficulty> cd = ClueDifficulty.getDifficulty(item);
				if (!cd.isPresent())
					return;
				player.clueContainer = new ClueScrollContainer(player, ClueScrollHandler.getStages(cd.get()));
			}
			player.clueContainer.current(item);
			break;

		case 2714:
			
			if (player.bossDifficulty == null) {
				player.write(new SendMessagePacket("You have not completed a clue scroll!"));
				return;
			}
			
			Item[] items = ClueScrollHandler.determineReward(player, player.bossDifficulty);

			if (player.getItems().getFreeSlots() < items.length + 1) {
				player.write(new SendMessagePacket("You do not have enough space in your inventory!"));
				return;
			}
			if (player.bossDifficulty.equals(ClueDifficulty.EASY)) {
				player.easyClue += 1;
				player.write(new SendMessagePacket("<col=009900> You have now completed " + player.easyClue + " easy clues"));
			}
			if (player.bossDifficulty.equals(ClueDifficulty.MEDIUM)) {
				player.mediumClue += 1;
				player.write(new SendMessagePacket("<col=FF5050> You have now completed " + player.mediumClue + " medium clues"));
				Achievements.increase(player, AchievementType.MEDIUM_CLUE, 1);
			}
			if (player.bossDifficulty.equals(ClueDifficulty.HARD)) {
				player.hardClue += 1;
				Achievements.increase(player, AchievementType.HARD_CLUE, 1);
				player.write(new SendMessagePacket("<col=CC3300> You have now completed " + player.hardClue + " hard clues"));
			}
			if (player.bossDifficulty.equals(ClueDifficulty.ELITE)) {
				player.eliteClue += 1;
				Achievements.increase(player, AchievementType.ELITE_CLUE, 1);
				player.write(new SendMessagePacket("<col=5A0000> You have now completed " + player.eliteClue + " elite clues"));
			}
			Achievements.increase(player, AchievementType.TREASURE_TRIAL, 1);
			player.getItems().deleteItem(2714);
			player.getPA().displayReward(items);
			Arrays.stream(items).forEach(player.getItems()::addItem);
			player.write(new SendMessagePacket("You open the casket and obtain your reward!"));
			player.bossDifficulty = null;
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
		if (!sendClue(player, player.getItems().search(ClueDifficulty.getClueIds()))) {
			player.write(new SendMessagePacket("Nothing interesting happens."));
		}
	}
	
	private boolean sendClue(Player player, int id) {
		if (player.clueContainer == null || id == -1) {
			return false;
		}
		Location l = player.clueContainer.stages.peek().getLocation();
		if (player.getPosition().inLocation(l)) {
			player.clueContainer.next(id);
			return true;
		}
		return false;
	}
}

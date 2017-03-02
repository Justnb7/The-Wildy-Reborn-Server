package com.model.game.character.player.packets.in;

import java.util.Arrays;
import java.util.Optional;

import com.model.Server;
import com.model.game.character.Animation;
import com.model.game.character.player.Player;
import com.model.game.character.player.Rights;
import com.model.game.character.player.content.BossTracker;
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
import com.model.game.character.player.dialogue.impl.RottenPotato;
import com.model.game.character.player.packets.PacketType;
import com.model.game.character.player.packets.encode.impl.SendMessagePacket;
import com.model.game.character.player.skill.prayer.Prayer.Bone;
import com.model.game.character.player.skill.slayer.SlayerTaskManagement.Teleports;
import com.model.game.item.Item;
import com.model.game.item.container.impl.LootingBagContainer;
import com.model.game.item.container.impl.RunePouchContainer;
import com.model.game.location.Location;
import com.model.task.ScheduledTask;

public class ItemOptionPacket implements PacketType {
	
	/**
	 * Option 1 opcode.
	 */
	private static final int OPTION_1 = 122;
	
	/**
	 * Option 2 opcode.
	 */
	private static final int OPTION_2 = 16;
	
	/**
	 * Option 3 opcode.
	 */
	private static final int OPTION_3 = 75;

	@Override
	public void handle(Player player, int id, int size) {
		switch (id) {
		case OPTION_1:
			handleItemOption1(player, id);
			break;
		case OPTION_2:
			handleItemOption2(player, id);
			break;
		case OPTION_3:
			handleItemOption3(player, id);
			break;
		}
	}
	
	/**
	 * Handles item option 1.
	 * @param player
	 * @param id
	 */
	private void handleItemOption1(Player player, int packetId) {
		final int interfaceIndex = player.getInStream().readSignedWordBigEndianA();
		final int slot = player.getInStream().readUnsignedWordA();
		final int id = player.getInStream().readUnsignedWordBigEndian();

		//Safety checks
		if (player.isDead() || interfaceIndex != 3214 || player.teleporting) {
			return;
		}
		
		//Player has bank pin
		if (player.getBankPin().requiresUnlock()) {
			player.isBanking = false;
			player.getBankPin().open(2);
			return;
		}
		
		//Debug mode
		if(player.in_debug_mode()) {
			System.out.println("First item option: interface:" + interfaceIndex + " slot: " + slot + " item: " + id);
		}
		
		//Last clicked item
		player.lastClickedItem = id;
		
		//if its an invalid item, refresh the inventory
		if ((slot < 0) || (slot > 27) || (id != player.playerItems[slot] - 1) || (player.playerItemsN[slot] <= 0)) {
			player.getItems().resetItems(3214);
			return;
		}
		
		PotionData potion = PotionData.forId(id);
		if (potion != null) {
			player.sendConsumable("potion", potion.getPotionId(), slot);
		}
		
		if (player.getFood().isFood(id)) {
			player.getFood().eat(id, slot);
		}

		TabData tabData = TabData.forId(id);
		if (tabData != null) {
			TeleTabs.useTeleTab(player, slot, tabData);
		}
		
		if(RunePouchContainer.open(player, id)) {
			return;
		}
		
		if(LootingBagContainer.open(player, id)) {
			return;
		}

		Bone bone = Bone.forId(id);
		if (bone != null) {
			player.getSkills().getPrayer().bury(id, slot);
			return;
		}

		player.getHerblore().clean(id);
		
		switch (id) {

		case 5733: // rotten potato jagex item
			if (player.rights == Rights.ADMINISTRATOR) {
				RottenPotato.option = 0;
				player.dialogue().start("POTATO", player);
			}
			break;

		case 13658:
			player.dialogue().start("TELEPORT_CARD", player);
			break;

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

		case 4155: // Enchanted Gem
			player.dialogue().start("ENCHANTED_GEM", player);
			break;
			
		case 2677:
		case 2801:
		case 2722:
		case 12073:
			if (player.clueContainer == null) {
				Optional<ClueDifficulty> cd = ClueDifficulty.getDifficulty(id);
				if (!cd.isPresent())
					return;
				player.clueContainer = new ClueScrollContainer(player, ClueScrollHandler.getStages(cd.get()));
			}
			player.clueContainer.current(id);
			break;

		case 2714:
			if (player.bossDifficulty == null) {
				player.write(new SendMessagePacket("You have not completed a clue scroll!"));
				return;
			}
			
			System.out.println("Difficulty is "+player.bossDifficulty);
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
			
		case 952:
			handleShovel(player);
			break;
		}
	}
	
	/**
	 * Handles item option 2.
	 * @param player
	 * @param id
	 */
	private void handleItemOption2(Player player, int packetId) {
		final int itemId = player.getInStream().readSignedWordA();
		final int slot = player.getInStream().readSignedWordBigEndianA();
		final int interfaceId =player.getInStream().readSignedWordBigEndianA();

		// Safety checks
		if (player.isDead() || player.teleporting) {
			return;
		}

		// Player has bank pin
		if (player.getBankPin().requiresUnlock()) {
			player.isBanking = false;
			player.getBankPin().open(2);
			return;
		}

		// Debug mode
		if (player.in_debug_mode()) {
			System.out.println("Second item option: interface:" +interfaceId+ " slot: "+slot+ " item: " +itemId);
		}

		// Last clicked item
		player.lastClickedItem = itemId;
		
		switch (itemId) {
		case 5733: // rotten potato jagex item
			if (player.rights == Rights.ADMINISTRATOR) {
				RottenPotato.option = 2;
				player.dialogue().start("POTATO", player);
			}
			break;
			
		case 4155:
			Teleports.teleport(player);
			break;

		case 2572:
			BossTracker.open(player);
			break;
		}
		
	}
	
	/**
	 * Handles item option 3.
	 * @param player
	 * @param id
	 */
	private void handleItemOption3(Player player, int packetId) {
		final int interfaceId = player.getInStream().readSignedWordBigEndianA();
		final int slot = player.getInStream().readSignedWordBigEndian();
		final int itemId = player.getInStream().readSignedWordA();
		
		// Safety checks
		if (player.isDead() || interfaceId != 3214 || player.teleporting) {
			return;
		}

		// Player has bank pin
		if (player.getBankPin().requiresUnlock()) {
			player.isBanking = false;
			player.getBankPin().open(2);
			return;
		}

		// Debug mode
		if (player.in_debug_mode()) {
			System.out.println("Third item option: interface:" + interfaceId + " slot: " + slot + " item: " + itemId);
		}

		// Last clicked item
		player.lastClickedItem = itemId;

		// if its an invalid item, refresh the inventory
		if ((slot < 0) || (slot > 27) || (itemId != player.playerItems[slot] - 1) || (player.playerItemsN[slot] <= 0)) {
			player.getItems().resetItems(3214);
			return;
		}
		
		switch (itemId) {
		case 5733: // rotten potato jagex item
			if (player.rights == Rights.ADMINISTRATOR) {
				RottenPotato.option = 3;
				player.dialogue().start("POTATO", player);
			}
			break;
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

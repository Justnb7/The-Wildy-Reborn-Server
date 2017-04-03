package com.model.game.character.player.packets.in;

import com.model.Server;
import com.model.game.character.Animation;
import com.model.game.character.combat.Combat;
import com.model.game.character.npc.pet.Pet;
import com.model.game.character.player.Player;
import com.model.game.character.player.Rights;
import com.model.game.character.player.Skills;
import com.model.game.character.player.content.BossTracker;
import com.model.game.character.player.content.bounty_hunter.BountyHunter;
import com.model.game.character.player.content.clicking.items.ItemOnItem;
import com.model.game.character.player.content.clicking.magic.MagicOnItems;
import com.model.game.character.player.content.consumable.potion.PotionData;
import com.model.game.character.player.content.rewards.Mysterybox;
import com.model.game.character.player.content.rewards.RewardCasket;
import com.model.game.character.player.content.teleport.TeleTabs;
import com.model.game.character.player.content.teleport.TeleTabs.TabData;
import com.model.game.character.player.content.trade.Trading;
import com.model.game.character.player.dialogue.impl.RottenPotato;
import com.model.game.character.player.packets.PacketType;
import com.model.game.character.player.packets.out.SendSoundPacket;
import com.model.game.character.player.skill.prayer.Prayer.Bone;
import com.model.game.character.player.skill.runecrafting.Runecrafting;
import com.model.game.character.player.skill.slayer.SlayerTaskManagement.Teleports;
import com.model.game.item.Item;
import com.model.game.item.ground.GroundItem;
import com.model.game.item.ground.GroundItemHandler;
import com.model.game.location.Position;
import com.model.task.ScheduledTask;
import com.model.task.impl.DistancedActionTask;
import com.model.utility.json.definitions.ItemDefinition;

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
	
	/**
	 * Option drop/destroy opcode.
	 */
	private static final int OPTION_DROP_DESTROY = 87;

	/**
	 * Option pickup opcode.
	 */
	private static final int OPTION_PICKUP = 236;
	
	/**
	 * Item on item opcode.
	 */
	private static final int ITEM_ON_ITEM = 53;

	/**
	 * Magic on item opcode.
	 */
	private static final int MAGIC_ON_ITEM = 237;

	/**
	 * Sent when a player uses an item on another item thats on the floor.
	 */
	private static final int ITEM_ON_GROUND_ITEM = 25;

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
		case OPTION_DROP_DESTROY:
			handleDropOrDestroy(player, id);
			break;
		case OPTION_PICKUP:
			handlePickup(player, id);
			break;
		case ITEM_ON_ITEM:
			handleItemOptionItem(player, id);
			break;
		case MAGIC_ON_ITEM:
			handleMagicOnItem(player, id);
			break;
		case ITEM_ON_GROUND_ITEM:
			handleItemOnGround(player, id);
			break;
		}
		player.getSkillCyclesTask().stop();
	}
	
	private void handleItemOptionItem(Player player, int id) {
		final int usedWithSlot = player.getInStream().readUnsignedWord();
		final int itemUsedSlot = player.getInStream().readUnsignedWordA();

		final Item used = player.getItems().getItemFromSlot(usedWithSlot);
		final Item with = player.getItems().getItemFromSlot(itemUsedSlot);
		
		if (used == null || with == null) {
			return;
		}
		
		if (!player.getItems().playerHasItem(with.getId(), 1) || !player.getItems().playerHasItem(used.getId(), 1)) {
			return;
		}
		
		ItemOnItem.handleAction(player, used, with);
	}

	private void handleMagicOnItem(Player player, int id) {
		final int slot = player.getInStream().readSignedWord();
		final int itemId = player.getInStream().readSignedWordA();
		final int childId = player.getInStream().readSignedWord();
		final int spellId = player.getInStream().readSignedWordA();
		
		MagicOnItems.handleAction(player, itemId, slot, childId, spellId);
	}
	
	private void handleItemOnGround(Player player, int id) {
		final int a1 = player.getInStream().readSignedWord();
		final int itemUsed = player.getInStream().readSignedWordA();
		final int groundItem = player.getInStream().readUnsignedWord();
		final int gItemY = player.getInStream().readSignedWordA();
		final int itemUsedSlot = player.getInStream().readSignedWordBigEndianA();
		final int gItemX = player.getInStream().readUnsignedWord();

		Position position = new Position(gItemX, gItemY, player.getPosition().getZ());
		
		if (player.inDebugMode()) {
			System.out.println("ItemUsed: " + itemUsed + " groundItem: " + groundItem + " itemUsedSlot: " + itemUsedSlot + " gItemX: " + gItemX + " gItemY: " + gItemY + " a1: " + a1);
		}
		
		if (!player.getItems().playerHasItem(itemUsed, 1, itemUsedSlot) || GroundItemHandler.get(groundItem, position) == null) {
			return;
		}
		
	}

	private void handlePickup(Player player, int packetId) {
		final int y = player.getInStream().readSignedWordBigEndian();
		final int id = player.getInStream().readUnsignedWord();
		final int x = player.getInStream().readSignedWordBigEndian();
		
		Item item = new Item(id);
		
		Position position = new Position(x, y, player.getPosition().getZ());
		
		if (player.inDebugMode()) {
			System.out.println(String.format("[handlePickup] - Item: %s Location: %s", item.toString(), position.toString()));
		}
		
		if (Math.abs(player.getPosition().getX() - x) > 25 || Math.abs(player.getPosition().getY() - y) > 25) {
			player.getMovementHandler().resetWalkingQueue();
			return;
		}
		
		if (player.isTeleporting()) {
			return;
		}
		
		if (player.isDead() || player.getSkills().getLevel(Skills.HITPOINTS) <= 0) {
			return;
		}
		
		Combat.resetCombat(player);
		
		if (onSpot(player, position)) {
			pickup(player, id, position);
		} else {
			player.setDistancedTask(new DistancedActionTask() {

				@Override
				public void onReach() {
					pickup(player, id, position);
					stop();
				}

				@Override
				public boolean reached() {
					return onSpot(player, position);
				}
			}.attach(player));
		}
	}

	private void handleDropOrDestroy(Player player, int packetId) {
		int itemId = player.getInStream().readUnsignedWordA();
		player.getInStream().readUnsignedByte();
		player.getInStream().readUnsignedByte();
		int slot = player.getInStream().readUnsignedWordA();
		
		final Item item = player.getItems().getItemFromSlot(slot);
		
		if (item == null || item.getId() != itemId) {
			return;
		}
		
		//We don't even have the item
		if (!player.getItems().playerHasItem(item.getId(), 1, slot)) {
			return;
		}
		
		if(player.inDebugMode()) {
			System.out.println("drop_or_destroy option: dropped: " + item.getId() + " from slot: " + slot);
		}
		
		//During teleport we cannot drop any items.
		if(player.isTeleporting()) {
			return;
		}
		
		//We're death
		if (player.isDead() || player.getSkills().getLevel(Skills.HITPOINTS) <= 0) {
			return;
		}
		
		//Check if player is in combat, in combat we cannot drop items worth more then 10,000 gold
		if (Combat.incombat(player) && (ItemDefinition.forId(itemId).getGeneralPrice()* .75) > 10_000) {
			player.getActionSender().sendMessage("You can't drop items worth over 10,000 gold in combat.");
			return;
		}
		
		//When dropping items during trade, trade resets.
		if (Trading.isTrading(player)) {
        	Trading.decline(player);
        }
		
		// We are dropping an pet item.
		if (!Pet.drop(player, item)) {
			return;
		}

		//Special case for destroying items.
		if(!ItemDefinition.forId(item.getId()).isTradeable()) {
			player.getPA().destroyItem(item);
			player.setDestroyItem(item.getId());
			return;
		}
		
		//We can go ahead and drop the item on the ground.
		GroundItemHandler.createGroundItem(new GroundItem(new Item(itemId, player.itemAmount[slot]), player.getX(), player.getY(), player.getZ(), player));
		
		//After we've dropped our item, the server deletes it from our inventory.
		player.getItems().deleteItem(item.getId(), slot, player.itemAmount[slot]);
		
		//When dropping items combat resets.
		Combat.resetCombat(player);
		
		//No idea why this is in the drop packet
		BountyHunter.determineWealth(player);
		
		//Once completed all checks we can go ahead and send the sound
		player.write(new SendSoundPacket(376, 1, 0));
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
		
		Item item = new Item(id);

		//Safety checks
		if (player.isDead() || interfaceIndex != 3214 || player.isTeleporting()) {
			return;
		}
		
		//Debug mode
		if(player.inDebugMode()) {
			System.out.println(String.format("[handleItemOption1] - Item: %s Interface: %s Slot: %s", item.toString(), interfaceIndex, slot));
		}
		
		//Last clicked item
		player.lastClickedItem = id;
		
		//if its an invalid item, refresh the inventory
		if ((slot < 0) || (slot > 27) || (id != player.playerInventory[slot] - 1) || (player.itemAmount[slot] <= 0)) {
			player.getItems().resetItems(3214);
			return;
		}
		
		PotionData potion = PotionData.forId(item.getId());
		if (potion != null) {
			player.sendConsumable("potion", potion.getPotionId(), slot);
		}
		
		if (player.getFood().isFood(item.getId())) {
			player.getFood().eat(item.getId(), slot);
		}

		TabData tabData = TabData.forId(item.getId());
		if (tabData != null) {
			TeleTabs.useTeleTab(player, slot, tabData);
		}
		
		/*if (player.runePouch.handleRunePouch(item.getId())) {
			return;
		}*/
		
		if(player.getRunePouchContainer().open(player, item.getId())) {
			return;
		}
		
		if(player.getLootingBagContainer().open(player, item.getId())) {
			return;
		}

		Bone bone = Bone.forId(item.getId());
		if (bone != null) {
			player.getSkills().getPrayer().bury(item.getId(), slot);
			return;
		}

		player.getHerblore().clean(item.getId());
		
		switch (item.getId()) {

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

		Item item = new Item(itemId);
		
		// Safety checks
		if (player.isDead() || player.isTeleporting()) {
			return;
		}

		// Debug mode
		if (player.inDebugMode()) {
			System.out.println(String.format("[handleItemOption2] - Item: %s Interface: %s Slot: %s", item.toString(), interfaceId, slot));
		}

		// Last clicked item
		player.lastClickedItem = item.getId();
		
		switch (item.getId()) {
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
		
		Item item = new Item(itemId);
		
		// Safety checks
		if (player.isDead() || interfaceId != 3214 || player.isTeleporting()) {
			return;
		}

		// Debug mode
		if (player.inDebugMode()) {
			System.out.println(String.format("[handleItemOption3] - Item: %s Interface: %s Slot: %s", item.toString(), interfaceId, slot));
		}

		// Last clicked item
		player.lastClickedItem = item.getId();

		// if its an invalid item, refresh the inventory
		if ((slot < 0) || (slot > 27) || (item.getId() != player.playerInventory[slot] - 1) || (player.itemAmount[slot] <= 0)) {
			player.getItems().resetItems(3214);
			return;
		}
		
		if(Runecrafting.locateTalisman(player, item)) {
			return;
		}
		
		switch (item.getId()) {
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
		player.getActionSender().sendMessage("Nothing interesting happens.");
	}
	
	/**
	 * Handles picking up the item
	 * 
	 * @param player
	 *            The {@link Player} picking up the item
	 * @param id
	 *            The id of the item
	 * @param amount
	 *            The amount of the item
	 * @param x
	 *            The x coordinate of the item
	 * @param y
	 *            The y coordinate of the item
	 * @param z
	 *            The z coordinate of the item
	 */
	private void pickup(Player player, int id, Position position/*int x, int y, int z*/) {
		if (GroundItemHandler.get(id, position) != null) {
			player.write(new SendSoundPacket(356, 0, 0));
			GroundItemHandler.pickup(player, id, position);
			BountyHunter.determineWealth(player);
		}
	}
	
	/**
	 * Checks if a player is in a specific location
	 *
	 * @param x
	 *            The x location
	 * @param y
	 *            The y location
	 * @param z
	 *            The z location
	 * @return If the player is standing on this spot
	 */
	private boolean onSpot(Player player, Position position) {
		return player.absX == position.getX() && player.absY == position.getY() && player.heightLevel == position.getZ();
	}

}

package com.venenatis.game.net.packet.in;

import com.venenatis.game.consumables.potion.PotionData;
import com.venenatis.game.content.KillTracker;
import com.venenatis.game.content.bounty.BountyHunter;
import com.venenatis.game.content.clicking.items.ItemOnItem;
import com.venenatis.game.content.clicking.magic.MagicOnItems;
import com.venenatis.game.content.rewards.Mysterybox;
import com.venenatis.game.content.rewards.RewardCasket;
import com.venenatis.game.content.skills.prayer.Prayer.Bone;
import com.venenatis.game.content.skills.runecrafting.Runecrafting;
import com.venenatis.game.content.skills.slayer.SlayerTaskManagement.Teleports;
import com.venenatis.game.location.Location;
import com.venenatis.game.model.Item;
import com.venenatis.game.model.Skills;
import com.venenatis.game.model.combat.Combat;
import com.venenatis.game.model.definitions.ItemDefinition;
import com.venenatis.game.model.entity.npc.pet.Pet;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.Rights;
import com.venenatis.game.model.entity.player.dialogue.impl.RottenPotato;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.net.packet.PacketType;
import com.venenatis.game.task.Task;
import com.venenatis.game.task.impl.DistancedActionTask;
import com.venenatis.game.world.ground_item.GroundItem;
import com.venenatis.game.world.ground_item.GroundItemHandler;
import com.venenatis.server.Server;

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
	}
	
	private void handleItemOptionItem(Player player, int id) {
		final int usedWithSlot = player.getInStream().readUnsignedWord();
		final int itemUsedSlot = player.getInStream().readUnsignedWordA();

		final Item used = player.getInventory().get(usedWithSlot);
		final Item with = player.getInventory().get(itemUsedSlot);
		
		if (used == null || with == null) {
			return;
		}
		
		if (!player.getInventory().contains(with.getId(), 1) || !player.getInventory().contains(used.getId(), 1)) {
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

		Location position = new Location(gItemX, gItemY, player.getLocation().getZ());
		
		if (player.inDebugMode()) {
			System.out.println("ItemUsed: " + itemUsed + " groundItem: " + groundItem + " itemUsedSlot: " + itemUsedSlot + " gItemX: " + gItemX + " gItemY: " + gItemY + " a1: " + a1);
		}
		
		if (!player.getInventory().contains(itemUsed) || GroundItemHandler.get(groundItem, position) == null) {
			return;
		}
		
	}

	private void handlePickup(Player player, int packetId) {
		final int y = player.getInStream().readSignedWordBigEndian();
		final int id = player.getInStream().readUnsignedWord();
		final int x = player.getInStream().readSignedWordBigEndian();
		
		Item item = new Item(id);
		
		Location position = new Location(x, y, player.getLocation().getZ());
		
		if (player.inDebugMode()) {
			System.out.println(String.format("[handlePickup] - Item: %s Location: %s", item.toString(), position.toString()));
		}
		
		if (Math.abs(player.getLocation().getX() - x) > 25 || Math.abs(player.getLocation().getY() - y) > 25) {
			player.getWalkingQueue().reset();
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
		
		final Item item = player.getInventory().get(slot);
		
		if (item != null && item.getId() != itemId) {
			return;
		}
		
		//We don't even have the item
		if (!player.getInventory().contains(item.getId())) {
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
		if (Combat.incombat(player) && (ItemDefinition.get(itemId).getValue()) > 10_000) {
			player.getActionSender().sendMessage("You can't drop items worth over 10,000 gold in combat.");
			return;
		}
		
		// We are dropping an pet item.
		if (!Pet.drop(player, item)) {
			return;
		}

		//Special case for destroying items.
		if(item.isDestroyable()) {
			destroyItem(player, item);
			player.setDestroyItem(item.getId());
			return;
		}
		
		//We can go ahead and drop the item on the ground.
		GroundItemHandler.createGroundItem(new GroundItem(new Item(itemId, player.getInventory().get(slot).getAmount()), player.getX(), player.getY(), player.getZ(), player));
		
		//After we've dropped our item, the server deletes it from our inventory.
		player.getInventory().remove(item);
		player.getInventory().refresh();
		
		//When dropping items combat resets.
		Combat.resetCombat(player);
		
		//No idea why this is in the drop packet
		BountyHunter.determineWealth(player);
		
		//Once completed all checks we can go ahead and send the sound
		player.getActionSender().sendSound(376, 1, 0);
	}

	private final void destroyItem(Player player, Item item) {
		player.getActionSender().sendUpdateItem(14171, item.getId(), 0, 1);
		player.getActionSender().sendString("Are you sure you want to drop this item?", 14174);
		player.getActionSender().sendString("Yes.", 14175);
		player.getActionSender().sendString("No.", 14176);
		player.getActionSender().sendString("", 14177);
		player.getActionSender().sendString("This item is valuable, you will not", 14182);
		player.getActionSender().sendString("get it back once lost.", 14183);
		player.getActionSender().sendString(item.getName(), 14184);
		player.getActionSender().sendChatBoxInterface(14170);
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
		
		PotionData potion = PotionData.forId(item.getId());
		if (potion != null) {
			player.sendConsumable("potion", potion.getPotionId(), slot);
		}
		
		if (player.getFood().isFood(item.getId())) {
			player.getFood().eat(item.getId(), slot);
		}
		
		if(player.getRunePouch().open(player, item.getId())) {
			return;
		}
		

		Bone bone = Bone.forId(item.getId());
		if (bone != null) {
			player.getSkills().getPrayer().bury(item, slot);
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
			KillTracker.open(player);
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
		Server.getTaskScheduler().schedule(new Task(1) {

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
	private void pickup(Player player, int id, Location position/*int x, int y, int z*/) {
		if (GroundItemHandler.get(id, position) != null) {
			player.getActionSender().sendSound(356, 0, 0);
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
	private boolean onSpot(Player player, Location position) {
		return player.getX() == position.getX() && player.getY() == position.getY() && player.getZ() == position.getZ();
	}

}

package com.venenatis.game.content.clicking.items;

import com.venenatis.game.content.PotionCombinating;
import com.venenatis.game.content.SkillCapePerks;
import com.venenatis.game.content.rewards.CrystalChest;
import com.venenatis.game.content.skills.crafting.Crafting;
import com.venenatis.game.content.skills.crafting.impl.ZulrahCrafting;
import com.venenatis.game.content.skills.firemaking.Firemaking;
import com.venenatis.game.content.skills.fletching.Fletching;
import com.venenatis.game.content.skills.slayer.SlayerHelmAction;
import com.venenatis.game.model.Item;
import com.venenatis.game.model.definitions.ItemDefinition;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.Rights;

public class ItemOnItem {
	
	/**
	 * Handles the action of using an item with another item.
	 * 
	 * @param player
	 *            The player performing this action.
	 * 
	 * @param usedItem
	 *            The {@link Item} that is being initially selected.
	 * 
	 * @param withItem
	 *            The {@link Item} that is being selected on.
	 */
	public static void handleAction(Player player, Item usedItem, Item withItem) {
		player.debug(String.format("[ItemOnItem] - itemUsed: %d usedWith: %d ", usedItem.getId(), withItem.getId()));
		
		if (Firemaking.startFire(player, usedItem, withItem, player.getLocation())) {
			return;
		}
		
		if (Crafting.SINGLETON.itemOnItem(player, withItem, usedItem)) {
			return;
		}
		
		if (Fletching.SINGLETON.itemOnItem(player, withItem, usedItem)) {
			return;
		}
		
		if (player.rights == Rights.OWNER) {
			if(usedItem.getId() == 5733 || withItem.getId() == 5733) {
				//int amount = player.getItems().checkAmount(withItem.getId());
				//player.getInventory().remove(new Item(withItem.getId(), amount));
				player.getActionSender().sendMessage("Whee... "+ItemDefinition.get(withItem.getId()).getName()+" All gone!");
			}
		}
		
		if (PotionCombinating.get().isPotion(usedItem) && PotionCombinating.get().isPotion(withItem)) {
			if (PotionCombinating.get().matches(usedItem, withItem)) {
				PotionCombinating.get().mix(player, usedItem, withItem);
			} else {
				player.getActionSender().sendMessage("You cannot combine two potions of different types.");
			}
			return;
		}
		
		if (CrystalChest.createKey(player)) {
			return;
		}
		
		if (usedItem.getId() == 5605 || withItem.getId() == 5605) {
			Item other = null;
			if (usedItem.getId() == 5605) {
				other = withItem;
			} else {
				other = usedItem;
			}
			ZulrahCrafting.ZulrahItems zulrahItems = ZulrahCrafting.ZulrahItems.of(other.getId());
			if (zulrahItems != null && zulrahItems.getRequiredItem() == 5605) {
				player.getActionQueue().addAction(new ZulrahCrafting(player, zulrahItems));
			}
		}
		
		if (usedItem.getId() == 4153 && withItem.getId() == 12849 || usedItem.getId() == 12849 && withItem.getId() == 4153) {
			player.getDialogueManager().start("GRANITE_MAUL_UPGRADE", player);
		}
		
		if (usedItem.getId() == 11235 && withItem.getId() == 12763 || usedItem.getId() == 12763 && withItem.getId() == 11235) {
			player.getDialogueManager().start("WHITE_DARK_BOW", player);
		}
		
		if (usedItem.getId() == 11235 && withItem.getId() == 12761 || usedItem.getId() == 12761 && withItem.getId() == 11235) {
			player.getDialogueManager().start("YELLOW_DARK_BOW", player);
		}
		
		if (usedItem.getId() == 11235 && withItem.getId() == 12759 || usedItem.getId() == 12759 && withItem.getId() == 11235) {
			player.getDialogueManager().start("GREEN_DARK_BOW", player);
		}
		
		if (usedItem.getId() == 11235 && withItem.getId() == 12757 || usedItem.getId() == 12757 && withItem.getId() == 11235) {
			player.getDialogueManager().start("BLUE_DARK_BOW", player);
		}
		
		if (usedItem.getId() == 4151 && withItem.getId() == 12769 || usedItem.getId() == 12769 && withItem.getId() == 4151) {
			player.getDialogueManager().start("FROZEN_WHIP", player);
		}
		
		if (usedItem.getId() == 4151 && withItem.getId() == 12771 || usedItem.getId() == 12771 && withItem.getId() == 4151) {
			player.getDialogueManager().start("LAVA_WHIP", player);
		}
		
		if (usedItem.getId() == 4151 && withItem.getId() == 12004 || usedItem.getId() == 12004 && withItem.getId() == 4151) {
			player.getDialogueManager().start("ABYSSAL_TENTACLE", player);
		}
		
		if (usedItem.getId() == 11838 && withItem.getId() == 12804 || usedItem.getId() == 12804 && withItem.getId() == 11838) {
			player.getDialogueManager().start("BLESSED_SARADOMIN_SWORD", player);
		}
		
		if (usedItem.getId() == 2572 && withItem.getId() == 12783 || usedItem.getId() == 12783 && withItem.getId() == 2572) {
			player.getDialogueManager().start("RING_OF_WEALTH", player);
			player.getInventory().remove(new Item(2572), new Item(12783));
			player.getInventory().add(new Item(12785));
		}
		
		if (usedItem.getId() == 861 && withItem.getId() == 12786 || usedItem.getId() == 12786 && withItem.getId() == 861) {
			player.getDialogueManager().start("MAGIC_SHORTBOW", player);
			player.getInventory().remove(new Item(861), new Item(12786));
			player.getInventory().add(new Item(12788));
		}
		
		if (usedItem.getId() == 227 || withItem.getId() == 227) {
			int primary = usedItem.getId() == 227 ? withItem.getId() : usedItem.getId();
			player.getHerblore().mix(primary);
			return;
		}

		if (usedItem.getId() == 227 || withItem.getId() == 227) {
			int primary = usedItem.getId() == 227 ? withItem.getId() : usedItem.getId();
			player.getHerblore().mix(primary);
			return;
		}
		
		if (SlayerHelmAction.handleItemOnItem(player, usedItem, withItem)) {
			return;
		}
		
		if (usedItem.getId() == 11810 && withItem.getId() == 11798 || usedItem.getId() == 11798 && withItem.getId() == 11810) {
			player.getInventory().remove(new Item(11810, 1));
			player.getInventory().remove(new Item(11798, 1));
			player.getInventory().add(new Item(11802, 1));
			return;
		}
		
		if (usedItem.getId() == 11812 && withItem.getId() == 11798 || usedItem.getId() == 11798 && withItem.getId() == 11812) {
			player.getInventory().remove(new Item(11812, 1));
			player.getInventory().remove(new Item(11798, 1));
			player.getInventory().add(new Item(11804, 1));
			return;
		}
		
		if (usedItem.getId() == 11814 && withItem.getId() == 11798 || usedItem.getId() == 11798 && withItem.getId() == 11814) {
			player.getInventory().remove(new Item(11814, 1));
			player.getInventory().remove(new Item(11798, 1));
			player.getInventory().add(new Item(11806, 1));
			return;
		}
		
		if (usedItem.getId() == 11816 && withItem.getId() == 11798 || usedItem.getId() == 11798 && withItem.getId() == 11816) {
			player.getInventory().remove(new Item(11816, 1));
			player.getInventory().remove(new Item(11798, 1));
			player.getInventory().add(new Item(11808, 1));
			return;
		}
		
		if (usedItem.getId() == 11800 && withItem.getId() == 11818 || usedItem.getId() == 11818 && withItem.getId() == 11800) {
			player.getInventory().remove(new Item(11818, 1));
			player.getInventory().remove(new Item(11800, 1));
			player.getInventory().add(new Item(11798, 1));
			return;
		}
		
		if (usedItem.getId() == 11820 && withItem.getId() == 11796 || usedItem.getId() == 11796 && withItem.getId() == 11820) {
			player.getInventory().remove(new Item(11796, 1));
			player.getInventory().remove(new Item(11820, 1));
			player.getInventory().add(new Item(11798, 1));
			return;
		}
		
		if (usedItem.getId() == 11822 && withItem.getId() == 11794 || usedItem.getId() == 11794 && withItem.getId() == 11822) {
			player.getInventory().remove(new Item(11794, 1));
			player.getInventory().remove(new Item(11822, 1));
			player.getInventory().add(new Item(11798, 1));
			return;
		}
		
		if (usedItem.getId() == 11818 && withItem.getId() == 11820 || usedItem.getId() == 11820 && withItem.getId() == 11818) {
			player.getInventory().remove(new Item(11818, 1));
			player.getInventory().remove(new Item(11820, 1));
			player.getInventory().add(new Item(11794, 1));
			return;
		}
		
		if (usedItem.getId() == 11822 && withItem.getId() == 11818 || usedItem.getId() == 11818 && withItem.getId() == 11822) {
			player.getInventory().remove(new Item(11818, 1));
			player.getInventory().remove(new Item(11822, 1));
			player.getInventory().add(new Item(11796, 1));
			return;
		}
		
		if (usedItem.getId() == 11820 && withItem.getId() == 11822 || usedItem.getId() == 11822 && withItem.getId() == 11820) {
			player.getInventory().remove(new Item(11820, 1));
			player.getInventory().remove(new Item(11822, 1));
			player.getInventory().add(new Item(11800, 1));
			return;
		}

		switch (withItem.getId()) {

		case 13280:
			switch (usedItem.getId()) {
			case 13124:
				SkillCapePerks.mixCape(player, "ARDOUGNE");
				break;

			case 6570:
				SkillCapePerks.mixCape(player, "FIRE");
				break;

			case 10499:
				SkillCapePerks.mixCape(player, "AVAS");
				break;

			case 2412:
				SkillCapePerks.mixCape(player, "SARADOMIN");
				break;

			case 2413:
				SkillCapePerks.mixCape(player, "GUTHIX");
				break;

			case 2414:
				SkillCapePerks.mixCape(player, "ZAMORAK");
				break;
			}
			break;
		}
	}
}

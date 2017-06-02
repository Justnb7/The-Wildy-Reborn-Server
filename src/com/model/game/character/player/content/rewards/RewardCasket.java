package com.model.game.character.player.content.rewards;

import com.model.game.character.player.Player;
import com.model.game.item.Item;
import com.model.utility.Utility;

/**
 * A class handled for the 'custom' reward caskets.
 * @author Patrick van Elderen
 * @date 28 Nov 10:43 AM
 *
 */
public class RewardCasket {
	
	/**
	 * Constants
	 */
	public static Item ARMOUR_CASKET = new Item(21999);
	public static Item WEAPON_CASKET = new Item(22000);
	public static Item PET_CASKET = new Item(22001);
	public static Item COSMETIC_CASKET = new Item(22002);
	public static Item VENOM_CASKET = new Item(22003);
	public static Item ZENYTE_CASKET = new Item(22004);
	public static Item ADVANCED_ITEMS_CASKET = new Item(22005);
	
	/**
	 * Rewards
	 */
    public final static Item[] UNCOMMON_ARMOUR_CASKET_REWARDS = { new Item(11828), new Item(11830), new Item(12002) };
	public final static Item[] RARE_ARMOUR_CASKET_REWARDS = { new Item(11832), new Item(11834) };
	public final static Item[] COMMON_ARMOUR_CASKET_REWARDS = { new Item(6586, 5), new Item(10551), new Item(10548), new Item(11836) };
	
	public final static Item[] UNCOMMON_WEAPON_CASKET_REWARDS = { new Item(11907), new Item(12006) };
	public final static Item[] RARE_WEAPON_CASKET_REWARDS = { new Item(11802), new Item(11791), new Item(13652) };
	public final static Item[] COMMON_WEAPON_CASKET_REWARDS = { new Item(4152, 5), new Item(4154, 5), new Item(3205, 5), new Item(10887), new Item(11804), new Item(11806), new Item(11808)};
	
	public final static Item[] UNCOMMON_PET_CASKET_REWARDS = { new Item(13178), new Item(12647), new Item(12648), new Item(12649), new Item(12650), new Item(12651), new Item(12652), new Item(13323), new Item(13322), new Item(13321), new Item(20661) };
	public final static Item[] RARE_PET_CASKET_REWARDS = { new Item(13262), new Item(13247), new Item(12921), new Item(13181), new Item(13225), new Item(13177), new Item(13179), new Item(20663) };
	public final static Item[] COMMON_PET_CASKET_REWARDS = { new Item(12646), new Item(12653), new Item(12655), new Item(11995), new Item(12643), new Item(12644), new Item(12645), new Item(12703), new Item(13320), new Item(20659), new Item(20665) };
	
	public final static Item[] UNCOMMON_COSMETIC_CASKET_REWARDS = {new Item(1053), new Item(1055), new Item(1057)};
	public final static Item[] RARE_COSMETIC_CASKET_REWARDS = { new Item(11862), new Item(11863), new Item(12399), new Item(1038), new Item(1040), new Item(1042), new Item(1044), new Item(1046), new Item(1048) };
	public final static Item[] COMMON_COSMETIC_CASKET_REWARDS = { new Item(1050), new Item(13343), new Item(1037) };
	
	public final static Item[] UNCOMMON_VENOM_CASKET_REWARDS = { new Item(12931) };
	public final static Item[] RARE_VENOM_CASKET_REWARDS = { new Item(12926), new Item(12904) };
	public final static Item[] COMMON_VENOM_CASKET_REWARDS = { new Item(12899) };
	
	public final static Item[] UNCOMMON_ZENYTE_CASKET_REWARDS = { new Item(19547) };
	public final static Item[] RARE_ZENYTE_CASKET_REWARDS = { new Item(19553) };
	public final static Item[] COMMON_ZENYTE_CASKET_REWARDS = { new Item(19550), new Item(19544) };
	
	public final static Item[] UNCOMMON_ADVANCED_ITEMS_CASKET_REWARDS = { new Item(11785), new Item(12899), new Item(19550), new Item(19544), new Item(19547), new Item(6586, 15), new Item(4152, 15), new Item(10548), new Item(11836) };
	public final static Item[] RARE_ADVANCED_ITEMS_CASKET_REWARDS = { new Item(12817), new Item(19481), new Item(13576), new Item(11802), new Item(11791), new Item(12006), new Item(12926), new Item(12904), new Item(19550), new Item(19544), new Item(13652) };
	public final static Item[] COMMON_ADVANCED_ITEMS_CASKET_REWARDS = { new Item(4152, 15), new Item(4154, 15), new Item(3205, 15), new Item(12931), new Item(6586, 5), new Item(10551), new Item(10548), new Item(11836), new Item(11832), new Item(11834), new Item(11907), new Item(10887), new Item(11804), new Item(11806), new Item(11808) };
	
	/**
	 * The pet casket rewarding the player a random pet.
	 * @param player
	 * The player opening the casket.
	 */
	public static void petCasket(Player player) {
		
		boolean hasAllPetsUnlocked = false;
		if (hasAllPetsUnlocked) {
			player.getActionSender().sendMessage("You've already collected all pets.");
			// looping bank+inv+equip = 800+28+13 for 28 items is hugely stressful, better to store a boolean
			// as unlockedPets[28] and reference that. when you get a drop/drop a pet (interact with one) then unlock it.
			return;
		}
		
		
		if (player.getInventory().contains(PET_CASKET)) {
			player.getInventory().remove(PET_CASKET);
			Item itemReceived = rollPet();
			// Loops forever until we don't have the rolled pet.
			while (player.getInventory().contains(itemReceived.id)) {
				itemReceived = rollPet();
			}
			player.getInventory().addOrCreateGroundItem(new Item(itemReceived.getId(), itemReceived.getAmount()));
			player.getActionSender().sendMessage("You find " + Utility.determineIndefiniteArticle(itemReceived.getName()) + " " + itemReceived.getName() + " in the chest.");
		}
	}
	
	private static Item rollPet() {
		switch (Utility.random(30)) {
		case 0:
		case 1:
		case 2:
		case 3:
		case 4:
		case 5:
		case 6:
		case 7:
		case 8:
		case 9:
		case 10:
			return Utility.randomElement(UNCOMMON_PET_CASKET_REWARDS);
		case 25:
			return Utility.randomElement(RARE_PET_CASKET_REWARDS);
		default:
			return Utility.randomElement(COMMON_PET_CASKET_REWARDS);
		}
	}

	/**
	 * The armour casket rewarding the player a random piece of armouring of many tiers.
	 * @param player
	 * The player opening the casket.
	 */
	public static void armourCasket(Player player) {
		if (player.getInventory().contains(ARMOUR_CASKET)) {
			player.getInventory().remove(ARMOUR_CASKET);
			Item itemReceived;
			switch (Utility.random(30)) {
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
			case 8:
			case 9:
			case 10:
				itemReceived = Utility.randomElement(UNCOMMON_ARMOUR_CASKET_REWARDS);
				break;
			case 25:
			case 20:
			case 27:
				itemReceived = Utility.randomElement(RARE_ARMOUR_CASKET_REWARDS);
				break;
			default:
				itemReceived = Utility.randomElement(COMMON_ARMOUR_CASKET_REWARDS);
			}
			player.getInventory().addOrCreateGroundItem(new Item(itemReceived.getId(), itemReceived.getAmount()));
			player.getActionSender().sendMessage("You find " + Utility.determineIndefiniteArticle(itemReceived.getName()) + " " + itemReceived.getName() + " in the chest.");
		}
	}
	
	/**
	 * The weapon casket rewarding the player a random weapon of many tiers.
	 * @param player
	 * The player opening the casket.
	 */
	public static void weaponCasket(Player player) {
		if (player.getInventory().contains(WEAPON_CASKET)) {
			player.getInventory().remove(WEAPON_CASKET);
			Item itemReceived;
			switch (Utility.random(30)) {
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
			case 8:
			case 9:
			case 10:
				itemReceived = Utility.randomElement(UNCOMMON_WEAPON_CASKET_REWARDS);
				break;
			case 25:
			case 29:
			case 22:
				itemReceived = Utility.randomElement(RARE_WEAPON_CASKET_REWARDS);
				break;
			default:
				itemReceived = Utility.randomElement(COMMON_WEAPON_CASKET_REWARDS);
			}
			player.getInventory().addOrCreateGroundItem(new Item(itemReceived.getId(), itemReceived.getAmount()));
			player.getActionSender().sendMessage("You find " + Utility.determineIndefiniteArticle(itemReceived.getName()) + " " + itemReceived.getName() + " in the chest.");
		}
	}
	
	/**
	 * The cosmetic casket rewarding the player a partyhat halloweenmask or other cosmetics.
	 * @param player
	 * The player opening the casket.
	 */
	public static void cosmeticCasket(Player player) {
		if (player.getInventory().contains(COSMETIC_CASKET)) {
			player.getInventory().remove(COSMETIC_CASKET);
			Item itemReceived;
			switch (Utility.random(30)) {
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
			case 8:
			case 9:
			case 10:
				itemReceived = Utility.randomElement(UNCOMMON_COSMETIC_CASKET_REWARDS);
				break;
			case 25:
			case 28:
			case 29:
			case 30:
				itemReceived = Utility.randomElement(RARE_COSMETIC_CASKET_REWARDS);
				break;
			default:
				itemReceived = Utility.randomElement(COMMON_COSMETIC_CASKET_REWARDS);
			}
			player.getInventory().addOrCreateGroundItem(new Item(itemReceived.getId(), itemReceived.getAmount()));
			player.getActionSender().sendMessage("You find " + Utility.determineIndefiniteArticle(itemReceived.getName()) + " " + itemReceived.getName() + " in the chest.");
		}
	}
	
	/**
	 * The venom casket rewarding the player a random item that can deliver deathly venom attacks.
	 * @param player
	 * The player opening the casket.
	 */
	public static void venomCasket(Player player) {
		if (player.getInventory().contains(VENOM_CASKET)) {
			player.getInventory().remove(VENOM_CASKET);
			Item itemReceived;
			switch (Utility.random(30)) {
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
			case 8:
			case 9:
			case 10:
				itemReceived = Utility.randomElement(UNCOMMON_VENOM_CASKET_REWARDS);
				break;
			case 25:
			case 20:
			case 21:
			case 22:
				itemReceived = Utility.randomElement(RARE_VENOM_CASKET_REWARDS);
				break;
			default:
				itemReceived = Utility.randomElement(COMMON_VENOM_CASKET_REWARDS);
			}
			player.getInventory().addOrCreateGroundItem(new Item(itemReceived.getId(), itemReceived.getAmount()));
			player.getActionSender().sendMessage("You find " + Utility.determineIndefiniteArticle(itemReceived.getName()) + " " + itemReceived.getName() + " in the chest.");
		}
	}
	
	/**
	 * The zenyte casket rewarding the player a random item from zenyte.
	 * @param player
	 * The player opening the casket.
	 */
	public static void zenyteCasket(Player player) {
		if (player.getInventory().contains(ZENYTE_CASKET)) {
			player.getInventory().remove(ZENYTE_CASKET);
			Item itemReceived;
			switch (Utility.random(30)) {
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
			case 8:
			case 9:
			case 10:
				itemReceived = Utility.randomElement(UNCOMMON_ZENYTE_CASKET_REWARDS);
				break;
			case 25:
			case 26:
			case 27:
			case 28:
				itemReceived = Utility.randomElement(RARE_ZENYTE_CASKET_REWARDS);
				break;
			default:
				itemReceived = Utility.randomElement(COMMON_ZENYTE_CASKET_REWARDS);
			}
			player.getInventory().addOrCreateGroundItem(new Item(itemReceived.getId(), itemReceived.getAmount()));
			player.getActionSender().sendMessage("You find " + Utility.determineIndefiniteArticle(itemReceived.getName()) + " " + itemReceived.getName() + " in the chest.");
		}
	}
	
	/**
	 * The advanced items casket rewarding the player a random item from all caskets except cosmetics.
	 * @param player
	 * The player opening the casket.
	 */
	public static void advancedItemsCasket(Player player) {
		if (player.getInventory().contains(ADVANCED_ITEMS_CASKET)) {
			player.getInventory().remove(ADVANCED_ITEMS_CASKET);
			Item itemReceived;
			int roll = Utility.random(30);
			switch (roll) {
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
			case 8:
			case 9:
			case 10:
				itemReceived = Utility.randomElement(UNCOMMON_ADVANCED_ITEMS_CASKET_REWARDS);
				break;
			case 25:
			case 30:
			case 21:
			case 23:
				itemReceived = Utility.randomElement(RARE_ADVANCED_ITEMS_CASKET_REWARDS);
				break;
			default:
				itemReceived = Utility.randomElement(COMMON_ADVANCED_ITEMS_CASKET_REWARDS);
			}
			player.getInventory().addOrCreateGroundItem(new Item(itemReceived.getId(), itemReceived.getAmount()));
			player.getActionSender().sendMessage("You find " + Utility.determineIndefiniteArticle(itemReceived.getName()) + " " + itemReceived.getName() + " in the chest.");
			//player.sendMessage(player.getUsername()+" has rolled "+roll+" on the drop table. (25, 30, 21 and 23 are rare rolls.)");
		}
	}

}
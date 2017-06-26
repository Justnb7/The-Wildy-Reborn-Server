package com.venenatis.game.content.rewards;

import com.venenatis.game.model.Item;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.util.Utility;

/**
 * Rewards you with a random item from the availableRewards array.
 * @author Patrick van Elderen
 * @date 21-2-2017 20:49
 *
 */
public class Mysterybox {
	
	public static Item MYSTERY_BOX = new Item(6199);
	
	/**
     * All available rare items to win
     */
	public static final Item[] RARE_REWARDS = { new Item(10332, 1), // 3rd age range legs
			new Item(10340, 1), // 3rd age robe
			new Item(12422, 1), // 3rd age wand
			new Item(12437, 1), // 3rd age cloak
			new Item(10348, 1), // 3rd age platebody
			new Item(10346, 1), // 3rd age platelegs
			new Item(10350, 1), // 3rd age full helmet
			new Item(10338, 1), // 3rd age robe top
			new Item(10342, 1), // 3rd age mage hat
			new Item(10334, 1), // 3rd age range coif
			new Item(10336, 1), // 3rd age vambraces
			new Item(10344, 1), // 3rd age amulet
			new Item(10352, 1), // 3rd age kiteshield
			new Item(12424, 1), // 3rd age bow
			new Item(12426, 1), // 3rd age longsword
			new Item(10330, 1) // 3rd age range top
			};
	/**
     * All available uncommon items to win
     */
	public static final Item[] UNCOMMON_REWARDS = { new Item(13307, 5), // Blood money
			new Item(995, 5_000_000), // Coins
			new Item(4716, 1), // Dharok's helm
			new Item(4718, 1), // Dharok's greataxe
			new Item(4720, 1), // Dharok's platebody
			new Item(4722, 1), // Dharok's platelegs
			new Item(4753, 1), // Verac's helm
			new Item(4755, 1), // Verac's flail
			new Item(4757, 1), // Verac's brassard
			new Item(4759, 1), // Verac's plateskirt
			new Item(4745, 1), // Torag's helm
			new Item(4747, 1), // Torag's hammers
			new Item(4749, 1), // Torag's platebody
			new Item(4751, 1), // Torag's platelegs
			new Item(4745, 1), // Torag's helm
			new Item(4747, 1), // Torag's hammers
			new Item(4749, 1), // Torag's platebody
			new Item(4751, 1), // Torag's platelegs
			new Item(4724, 1), // Guthan's helm
			new Item(4745, 1), // Torag's helm
			new Item(4747, 1), // Torag's hammers
			new Item(4749, 1), // Torag's platebody
			new Item(4751, 1), // Torag's platelegs
			new Item(4724, 1), // Guthan's helm
			new Item(4726, 1), // Guthan's warspear
			new Item(4728, 1), // Guthan's platebody
			new Item(4730, 1), // Guthan's chainskirt
			new Item(4726, 1), // Guthan's warspear
			new Item(4728, 1), // Guthan's platebody
			new Item(4730, 1), // Guthan's chainskirt
			new Item(4732, 1), // Karil's coif
			new Item(4734, 1), // Karil's crossbow
			new Item(4736, 1), // Karil's leathertop
			new Item(4738, 1), // Karil's leatherskirt
			new Item(4708, 1), // Ahrim's hood
			new Item(4710, 1), // Ahrim's staff
			new Item(4712, 1), // Ahrim's robetop
			new Item(4714, 1) // Ahrim's robeskirt
			};
	/**
     * All available common items to win
     */
	public static final Item[] COMMON_REWARDS = { new Item(12391, 1), // Gilded boots
			new Item(6920, 1), // Infinity boots
			new Item(12397, 1), // Royal crown
			new Item(12439, 1), // Royal sceptre
			new Item(11840, 1), // Dragon boots
			new Item(7462, 1), // Barrows gloves
			new Item(11937, 150), // Dark crab
			new Item(10548, 1), // Fighter hat
			new Item(10551, 1), // Fighter torso
			new Item(12829, 1), // Spirit shield
			new Item(6731, 1), // Seers ring
			new Item(6733, 1), // Archers ring
			new Item(6735, 1), // Warrior ring
			new Item(11937, 250), // Dark crab
			new Item(8839, 1), // Void knight top
			new Item(8840, 1), // Void knight robe
			new Item(8841, 1), // Void knight mace
			new Item(8842, 1), // Void knight gloves
			new Item(10611, 1), // Void knight top
			new Item(11663, 1), // Void mage helm
			new Item(11664, 1), // Void ranger helm
			new Item(11665, 1), // Void melee helm
			new Item(7158, 1), // Dragon 2h sword
			new Item(12371, 1), // Lava dragon mask
			new Item(12369, 1), // Mithril dragon mask
			new Item(12363, 1), // Bronze dragon mask
			new Item(12518, 1), // Green dragon mask
			new Item(12365, 1), // Iron dragon mask
			new Item(12520, 1), // Blue dragon mask
			new Item(12524, 1), // Black dragon mask
			new Item(12522, 1), // Red dragon mask
			new Item(12367, 1), // Steel dragon mask
			new Item(6570, 1), // Fire cape
			new Item(6585, 1), // Amulet of fury
			new Item(11283, 1), // Dragonfire shield
			new Item(6737, 1), // Berserker ring
			new Item(11808, 1) // Zamorak godsword
			};
	
	
	public static void open(Player player) {
		if(player.getInventory().contains(MYSTERY_BOX)) {
			Item itemReceived;
			switch (Utility.getRandom(50)) {
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
				itemReceived = Utility.randomElement(UNCOMMON_REWARDS);
				break;
			case 25:
				itemReceived = Utility.randomElement(RARE_REWARDS);
				break;
			default:
				itemReceived = Utility.randomElement(COMMON_REWARDS);
			}
			//TODO spawn pet in inv or bank
			player.getActionSender().sendMessage("You unwrap your mystery box and found yourself " + Utility.determineIndefiniteArticle(itemReceived.getName()) + " " + itemReceived.getName() + ".");
			player.getInventory().remove(MYSTERY_BOX);
		}
	}

}

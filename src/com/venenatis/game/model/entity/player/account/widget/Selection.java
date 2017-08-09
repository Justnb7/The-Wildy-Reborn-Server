package com.venenatis.game.model.entity.player.account.widget;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import com.venenatis.game.model.Item;
import com.venenatis.game.model.combat.magic.spell.SpellBook;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.Rights;
import com.venenatis.game.model.entity.player.account.Account;
import com.venenatis.game.model.entity.player.save.PlayerSave;
import com.venenatis.game.model.entity.player.save.PlayerSaveUtility;

/**
 * The class which represents functionality for selecting your game mode.
 * @author <a href="http://www.rune-server.org/members/_Patrick_/">Patrick van Elderen</a>
 *
 */
public class Selection {
	
	private static final Item[] starterItems = { new Item(4587, 10), new Item(1305, 10), new Item(1434, 10),
			new Item(5698, 10), new Item(1215, 10), new Item(10828, 10), new Item(3751, 10), new Item(2550, 10),
			new Item(3105, 10), new Item(1725, 10), new Item(1704, 10), new Item(1731, 10), new Item(1163, 10),
			new Item(1127, 10), new Item(1079, 10), new Item(1201, 10), new Item(7460, 10), new Item(4131, 10),
			new Item(861, 10), new Item(892, 10000), new Item(9185, 10), new Item(9244, 100), new Item(868, 10000),
			new Item(811, 10000), new Item(10498, 10), new Item(10499, 10), new Item(3749, 10), new Item(2503, 10),
			new Item(2497, 10), new Item(2491, 10), new Item(4675, 10), new Item(3755, 10), new Item(1540, 10),
			new Item(2412, 10), new Item(3840, 10), new Item(2413, 10), new Item(3844, 10), new Item(2414, 10),
			new Item(3842, 10), new Item(4089, 10), new Item(4091, 10), new Item(4093, 10), new Item(4095, 10),
			new Item(4097, 10), new Item(4099, 10), new Item(4101, 10), new Item(4103, 10), new Item(4105, 10),
			new Item(4107, 10), new Item(4109, 10), new Item(4111, 10), new Item(4113, 10), new Item(4115, 10),
			new Item(4117, 10), new Item(554, 10000), new Item(555, 10000), new Item(556, 10000), new Item(557, 10000),
			new Item(558, 10000), new Item(559, 10000), new Item(560, 10000), new Item(561, 10000),
			new Item(562, 10000), new Item(563, 10000), new Item(564, 10000), new Item(565, 10000),
			new Item(9075, 10000), new Item(6685, 5000), new Item(3024, 5000), new Item(2436, 5000),
			new Item(2440, 5000), new Item(2442, 5000), new Item(2444, 5000), new Item(3040, 5000),
			new Item(2434, 5000), new Item(2448, 5000), new Item(6687, 5000), new Item(3026, 5000), new Item(145, 5000),
			new Item(157, 5000), new Item(163, 5000), new Item(169, 5000), new Item(3042, 5000), new Item(139, 5000),
			new Item(181, 5000), new Item(6689, 5000), new Item(3028, 5000), new Item(147, 5000), new Item(159, 5000),
			new Item(165, 5000), new Item(171, 5000), new Item(3044, 5000), new Item(141, 5000), new Item(183, 5000),
			new Item(6691, 5000), new Item(3030, 5000), new Item(149, 5000), new Item(161, 5000), new Item(167, 5000),
			new Item(173, 5000), new Item(3046, 5000), new Item(143, 5000), new Item(185, 5000), new Item(391, 1000),
			new Item(385, 10000), new Item(3144, 100), new Item(4315, 5000), new Item(4317, 5000), new Item(4319, 5000),
			new Item(4335, 5000), new Item(4337, 5000), new Item(4339, 5000), new Item(4355, 5000),
			new Item(4357, 5000), new Item(4359, 5000), new Item(4375, 5000), new Item(4377, 5000),
			new Item(4379, 5000), new Item(4395, 5000), new Item(4397, 5000), new Item(4399, 5000)

	};
	
	
	public static final int[] NORMAL_TAB_AMOUNTS = new int[] { 1, 17, 12, 37, 39, 15, 0, 0, 0, 0, };
	
	/**
	 * Game modes
	 *
	 */
	enum GameModes {
		NONE, IRON_MAN, ULTIMATE_IRON_MAN, HARDCORE_IRON_MAN;
		
		/**
		 * We don't have to set a constructor because the Enum only consists of Types
		 */
		private GameModes() {
		}
		
		/**
		 * Gets the spriteId from the client.
		 * @return The spriteId
		 */
		public int getSpriteId() {
			return 42402 + (ordinal() * 1);
		}
		
		/**
		 * The buttonId
		 * @return The button we receive from the client.
		 */
		public int getButtonId() {
			return 165162 + (ordinal() * 1);
		}
		
		//A set of game types
		public static final Set<GameModes> TYPE = Collections.unmodifiableSet(EnumSet.allOf(GameModes.class));

	}
	
	//We're storing the last button ticked. The 'None' option by default.
	private int selectedIronmanButton = 165165;
	
	/**
	 * Opening the selection interface.
	 * @param player
	 *        The player opening the interface.
	 */
	public void open(Player player) {
		player.getActionSender().sendChangeSprite(42402, (byte) 0);//Ironman
		player.getActionSender().sendChangeSprite(42403, (byte) 0);//Hardcore ironman
		player.getActionSender().sendChangeSprite(42404, (byte) 0);//Ultimate ironman
		player.getActionSender().sendChangeSprite(42405, (byte) 2);//None option
		player.getActionSender().sendInterface(42400);
		selectedIronmanButton = 165165;
	}
	
	/**
	 * We're checking which mode was selected.
	 * So that we can deactivate the other options.
	 * @param player
	 *        The player refreshing the options.
	 */
	public void refreshOptions(Player player) {
		switch (selectedIronmanButton) {
		case 165162:
			player.getActionSender().sendChangeSprite(42402, (byte) 2);
			player.getActionSender().sendChangeSprite(42403, (byte) 0);
			player.getActionSender().sendChangeSprite(42404, (byte) 0);
			player.getActionSender().sendChangeSprite(42405, (byte) 0);
			break;
		case 165163:
			player.getActionSender().sendChangeSprite(42402, (byte) 0);
			player.getActionSender().sendChangeSprite(42403, (byte) 2);
			player.getActionSender().sendChangeSprite(42404, (byte) 0);
			player.getActionSender().sendChangeSprite(42405, (byte) 0);
			break;
		case 165164:
			player.getActionSender().sendChangeSprite(42402, (byte) 0);
			player.getActionSender().sendChangeSprite(42403, (byte) 0);
			player.getActionSender().sendChangeSprite(42404, (byte) 2);
			player.getActionSender().sendChangeSprite(42405, (byte) 0);
			break;
		case 165165:
			player.getActionSender().sendChangeSprite(42402, (byte) 0);
			player.getActionSender().sendChangeSprite(42403, (byte) 0);
			player.getActionSender().sendChangeSprite(42404, (byte) 0);
			player.getActionSender().sendChangeSprite(42405, (byte) 2);
			break;
		default:
			player.getActionSender().sendChangeSprite(42405, (byte) 2);
		}
	}
	
	/**
	 * Option ticking for a game mode of choice.
	 * @param player
	 *        The player ticking the option.
	 * @param buttonId
	 *        The buttonId sent by the client.
	 */
	public void selectMode(Player player, int buttonId) {
		for (GameModes type : GameModes.values()) {
			if (type.getButtonId() == buttonId) {
				if (selectedIronmanButton == buttonId) {
					player.getActionSender().sendMessage("You've already selected this option.");
				} else {
					if (buttonId == 165162) {//Ironman button
						selectedIronmanButton = 165162;
					} else if (buttonId == 165163) {//Hardcore ironman button
						selectedIronmanButton = 165163;
					} else if (buttonId == 165164) {//Ultimate ironman button
						selectedIronmanButton = 165164;
					} else if (buttonId == 165165) {//None button
						selectedIronmanButton = 165165;
					}
					refreshOptions(player);
				}
			}
		}
	}
	
	/**
	 * Reward each game mode with a different starter
	 * @param player
	 *        The player receiving the starter
	 * @param gameMode
	 *        The game mode
	 */
	public void starter(Player player, int gameMode) {
		switch (gameMode) {
		case 0: //Ironman
			player.getEquipment().setSlot(0, new Item(12810));
			player.getEquipment().setSlot(4, new Item(12811));
			player.getEquipment().setSlot(7, new Item(12812));
			break;
		case 1: //Hardcore ironman
			player.getEquipment().setSlot(0, new Item(20792));
			player.getEquipment().setSlot(4, new Item(20794));
			player.getEquipment().setSlot(7, new Item(20796));
			break;
		case 2: //Ultimate ironman
			player.getEquipment().setSlot(0, new Item(12813));
			player.getEquipment().setSlot(4, new Item(12814));
			player.getEquipment().setSlot(7, new Item(12815));
			break;
		case 3: //Regular account
			if (player.getBank().isEmpty()) {
				player.getBank().clear(false);
				player.getBank().add(starterItems);
				System.arraycopy(NORMAL_TAB_AMOUNTS, 0, player.getBank().getTabAmounts(), 0, NORMAL_TAB_AMOUNTS.length);
				player.getBank().shift(false);
			}
			for (int skill = 0; skill < 7; skill++) {
				player.getSkills().setMaxLevel(skill, 99);
			}
			break;
		}
		
		if (PlayerSaveUtility.setStarter(player)) {
			player.getInventory().add(new Item(995, 250_000));
			player.getInventory().add(new Item(6199, 1));
		}
		
		//Set default spellbook
		player.setSpellBook(SpellBook.LUNAR_MAGICS);
		//Remove tutorial flag, and remove starter flag.
		player.setTutorial(false);
		player.setReceivedStarter(true);
		//Update players gear bonusses
		player.getEquipment().calculateWeight();
		player.getEquipment().updateWeapon();
		player.getEquipment().setBonus();
		
		PlayerSave.save(player);
	}
	
	/**
	 * Once done choosing our game mode we can confirm.
	 * @param player
	 *        The player confirming their game mode.
	 */
	public void confirm(Player player) {
		
		//We can only steal once per 2 seconds
		if (System.currentTimeMillis() - lastInteraction < INTERACTION_DELAY) {
			return;
		}
		
		boolean validButtons = selectedIronmanButton >= 165162 && selectedIronmanButton <= 165165;
		
		if(!validButtons) {
			player.getActionSender().sendMessage("You have yet to select an game mode.");
			return;
		}

		switch (selectedIronmanButton) {

		case 165162:
			player.getAccount().setType(Account.REGULAR_TYPE);
			player.setRights(Rights.PLAYER);
			starter(player, 3);
			/*player.getAccount().setType(Account.IRON_MAN_TYPE);
			player.setRights(Rights.IRON_MAN);
			starter(player, 0);*/
			break;

		case 165163:
			player.getAccount().setType(Account.REGULAR_TYPE);
			player.setRights(Rights.PLAYER);
			starter(player, 3);
			/*player.getAccount().setType(Account.HARDCORE_IRON_MAN_TYPE);
			player.setRights(Rights.HARDCORE_IRON_MAN);
			starter(player, 1);*/
			break;

		case 165164:
			player.getAccount().setType(Account.REGULAR_TYPE);
			player.setRights(Rights.PLAYER);
			starter(player, 3);
			/*player.getAccount().setType(Account.ULTIMATE_IRON_MAN_TYPE);
			player.setRights(Rights.ULTIMATE_IRON_MAN);
			starter(player, 2);*/
			break;
			
		case 165165:
			player.getAccount().setType(Account.REGULAR_TYPE);
			player.setRights(Rights.PLAYER);
			starter(player, 3);
			break;

		}
		player.getActionSender().removeAllInterfaces();
		//Open make-over interface
		player.getActionSender().sendInterface(3559);
	}

	/**
	 * The constant delay that is required inbetween interactions
	 */
	private final long INTERACTION_DELAY = 2_000L;
	
	/**
	 * The last interaction that player made that is recorded in milliseconds
	 */
	private long lastInteraction;
	
}
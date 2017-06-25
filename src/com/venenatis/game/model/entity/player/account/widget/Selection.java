package com.venenatis.game.model.entity.player.account.widget;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import com.venenatis.game.model.Item;
import com.venenatis.game.model.combat.magic.SpellBook;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.Rights;
import com.venenatis.game.model.entity.player.account.Account;

/**
 * The class which represents functionality for selecting your game mode.
 * @author <a href="http://www.rune-server.org/members/_Patrick_/">Patrick van Elderen</a>
 *
 */
public class Selection {
	
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
			/*player.getItems().setEquipment(12810, 1, 0);
			player.getItems().setEquipment(12811, 1, 4);
			player.getItems().setEquipment(12812, 1, 7);*/
			break;
		case 1: //Hardcore ironman
			/*player.getItems().setEquipment(20792, 1, 0);
			player.getItems().setEquipment(20794, 1, 4);
			player.getItems().setEquipment(20796, 1, 7);*/
			break;
		case 2: //Ultimate ironman
			/*player.getItems().setEquipment(12813, 1, 0);
			player.getItems().setEquipment(12814, 1, 4);
			player.getItems().setEquipment(12815, 1, 7);*/
			break;
		case 3: //Regular account
			
			Item[] starterItems = {
			new Item(995, 250_000), new Item(841, 1), new Item(882, 500), new Item(558, 500), new Item(556, 500), new Item(554, 500), new Item(555, 500),
			new Item(557, 500), new Item(1323, 1), new Item(1153, 1), new Item(1115, 1), new Item(1067, 1), new Item(1191, 1), new Item(1712, 1), new Item(3105, 1)
			};
			for (Item item : starterItems) {
				player.getInventory().add(item);
			}
			break;
		}
		
		//Set default spellbook
		player.setSpellBook(SpellBook.MODERN);
		//Remove tutorial flag, and remove starter flag.
		player.setTutorial(false);
		player.setReceivedStarter(true);
		//Update players gear bonusses
		//player.getItems().getBonus();
		//player.getItems().writeBonus();
	}
	
	/**
	 * Once done choosing our game mode we can confirm.
	 * @param player
	 *        The player confirming their game mode.
	 */
	public void confirm(Player player) {
		
		boolean validButtons = selectedIronmanButton >= 165162 && selectedIronmanButton <= 165165;
		
		if(!validButtons) {
			player.getActionSender().sendMessage("You have yet to select an game mode.");
			return;
		}

		switch (selectedIronmanButton) {

		case 165162:
			player.getAccount().setType(Account.IRON_MAN_TYPE);
			player.setRights(Rights.IRON_MAN);
			starter(player, 0);
			break;

		case 165163:
			player.getAccount().setType(Account.HARDCORE_IRON_MAN_TYPE);
			player.setRights(Rights.HARDCORE_IRON_MAN);
			starter(player, 1);
			break;

		case 165164:
			player.getAccount().setType(Account.ULTIMATE_IRON_MAN_TYPE);
			player.setRights(Rights.ULTIMATE_IRON_MAN);
			starter(player, 2);
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

}
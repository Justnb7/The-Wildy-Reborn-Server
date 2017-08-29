package com.venenatis.game.content.skills.slayer.interfaceController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import com.venenatis.game.content.skills.slayer.Slayer;
import com.venenatis.game.content.skills.slayer.interfaceController.ExtendInterface.ExtendButtons;
import com.venenatis.game.content.skills.slayer.interfaceController.UnlockInterface.UnlockButtons;
import com.venenatis.game.model.entity.npc.NPC;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.task.Task;
import com.venenatis.game.util.Utility;
import com.venenatis.game.world.shop.ShopManager;
import com.venenatis.server.Server;

/**
 * 
 * @author Harambe_ Class represents and handles the Slayer Interface
 *
 */
public class SlayerInterface {

	public enum Action {
		SHOP, CANCEL, UNBLOCK, TASK_INTERFACE, EXTEND_BUTTON, UNLOCK_BUTTON, INTERFACE, CLOSE, CONFIRM, BACK, EXTEND_INERFACE, PURCHASE, BLOCK;
	}

	public enum ButtonData {
		/**
		 * Basic core actions
		 */
		UNLOCK_INTERFACE(new int[] { 91005, 90161, 91105, 89222 }, 23400, Action.INTERFACE, 0, 0), SLAYER_EXTEND_INTERFACE(new int[] { 91006, 90162, 91106, 89223 }, 23300, Action.EXTEND_INERFACE, 0, 0), SLAYER_BUY_INTERFACE(new int[] { 91007, 90163, 91107, 89224 }, 23000, Action.SHOP, 0, 0), SLAYER_TASK_INTERFACE(new int[] { 91008, 90164, 91108, 89225 }, 23200, Action.TASK_INTERFACE, 0, 0), BACK(new int[] { 90062 }, getPreviousInterface(), Action.BACK, 0, 0), CLOSE(new int[] { 89218 }, 0, Action.CLOSE, 0, 0), PURCHSAE(new int[] { 90063 }, 0, Action.PURCHASE, 0, 0), BLOCK(new int[] { 90170 }, 0, Action.BLOCK, 0, 100), UNBLOCK(new int[] { 90186, 90187, 90188, 90189, 90190, 90191 }, 0, Action.UNBLOCK, 0, 0), CANCEL(new int[] { 90169 }, 0, Action.CANCEL, 0, 30),
		// 9
		/**
		 * Unlock Buttons
		 */
		GARGOYLE_SMASHER(new int[] { 91110 }, 0, Action.UNLOCK_BUTTON, 0, 150), SLUG_SALTER(new int[] { 91111 }, 0, Action.UNLOCK_BUTTON, 0, 150), REPTILE_FREEZER(new int[] { 91112 }, 0, Action.UNLOCK_BUTTON, 0, 150), SHROOM_SPRAYER(new int[] { 91113 }, 0, Action.UNLOCK_BUTTON, 0, 400), BROADER_FLETCHING(new int[] { 91114 }, 0, Action.UNLOCK_BUTTON, 0, 110), MALEVOLENT_MASQUERADE(new int[] { 91115 }, 0, Action.UNLOCK_BUTTON, 0, 400), RING_BLING(new int[] { 91116 }, 0, Action.UNLOCK_BUTTON, 0, 300), SEEING_RED(new int[] { 91117 }, 0, Action.UNLOCK_BUTTON, 247, 80), MITH_ME(new int[] { 91118 }, 0, Action.UNLOCK_BUTTON, 2919, 80), WATCH_THE_BIRDIE(new int[] { 91119 }, 0, Action.UNLOCK_BUTTON, 0, 80), HOT_STUFF(new int[] { 91120 }, 0, Action.UNLOCK_BUTTON, 0, 100), REPTILE_GOT_RIPPED(new int[] { 91121 }, 0, Action.UNLOCK_BUTTON, 0, 75), LIKE_A_BOSS(new int[] { 91122 }, 0, Action.UNLOCK_BUTTON, 0, 200), KING_BLACK_BONNET(new int[] { 91123 }, 0, Action.UNLOCK_BUTTON, 0, 1000), KALPHITE_KHAT(new int[] { 91124 }, 0, Action.UNLOCK_BUTTON, 0, 1000), UNHOLY_HELMET(new int[] { 91125 }, 0, Action.UNLOCK_BUTTON, 0, 1000), BIGGER_AND_BADDER(new int[] { 91126 }, 0, Action.UNLOCK_BUTTON, 0, 150), DULY_NOTED(new int[] { 91127 }, 0, Action.UNLOCK_BUTTON, 0, 200),

		// 25
		/**
		 * Extend Actions
		 */
		NEED_MORE_DARKNESS(new int[] { 91010 }, 0, Action.EXTEND_BUTTON, 4005, 100), ANKOU_VERY_MUCH(new int[] { 91011 }, 0, Action.EXTEND_BUTTON, 7257, 100), SUQ_A_NOTHER_ONE(new int[] { 91012 }, 0, Action.EXTEND_BUTTON, 0, 100), FIRE_AND_DARKNESS(new int[] { 91013 }, 0, Action.EXTEND_BUTTON, 252, 50), PEDAL_TO_THE_METALS(new int[] { 91014 }, 0, Action.EXTEND_BUTTON, 270, 100), I_REALLY_MITH_YOU(new int[] { 91015 }, 0, Action.EXTEND_BUTTON, 247, 120), SPIRITUAL_FERVOUR(new int[] { 91016 }, 0, Action.EXTEND_BUTTON, 0, 100), BIRDS_OF_A_FEATHER(new int[] { 91017 }, 0, Action.EXTEND_BUTTON, 0, 100), GREATER_CHALLENGE(new int[] { 91018 }, 0, Action.EXTEND_BUTTON, 2026, 100), ITS_DARK_IN_HERE(new int[] { 91019 }, 0, Action.EXTEND_BUTTON, 1432, 100), BLEED_ME_DRY(new int[] { 91020 }, 0, Action.EXTEND_BUTTON, 484, 75), SMELL_YA_LATER(new int[] { 91021 }, 0, Action.EXTEND_BUTTON, 7, 75), HORROIFIC(new int[] { 91022 }, 0, Action.EXTEND_BUTTON, 3209, 100), DUST_YOU_SHALL_RETURN(new int[] { 91023 }, 0, Action.EXTEND_BUTTON, 234, 100), WYVER_ANOTHER_ONE(new int[] { 91024 }, 0, Action.EXTEND_BUTTON, 465, 100), GET_SMASHED(new int[] { 91025 }, 0, Action.EXTEND_BUTTON, 0, 100), NECHS_PLEASE(new int[] { 91026 }, 0, Action.EXTEND_BUTTON, 11, 100), AUGMENT_MY_ABBIES(new int[] { 91027 }, 0, Action.EXTEND_BUTTON, 415, 100), KRACK_ON(new int[] { 91028 }, 0, Action.EXTEND_BUTTON, 402, 100);

		private int[] button;
		private int interfaceId;
		Action action;
		int npcId;
		int points;

		public int getPoints() {
			return points;
		}

		public void setPoints(int points) {
			this.points = points;
		}

		private ButtonData(int[] button, int interfaceId, Action action, int npcId, int points) {
			this.button = button;
			this.interfaceId = interfaceId;
			this.action = action;
			this.npcId = npcId;
			this.points = points;
		}

		public int[] getButton() {
			return button;
		}

		public int getInterface() {
			return interfaceId;
		}

		public Action getAction() {
			return action;
		}

		public int getunlockId() {
			return npcId;
		}

		public String format() {
			return Utility.capitalize(name().toLowerCase().replaceAll("_", " ").trim());
		}

		public static HashMap<Integer, ButtonData> buttonMap = new HashMap<Integer, ButtonData>();

		static {
			for (final ButtonData buttonData : ButtonData.values()) {
				for (final int button : buttonData.getButton()) {
					ButtonData.buttonMap.put(button, buttonData);
				}
			}

		}

	}

	/**
	 * Stores the blocked tasks
	 */
	private ArrayList<String> blockedTasks = new ArrayList<String>(6);

	public void setBlockedTasks(ArrayList<String> blockedTasks) {
		this.blockedTasks = blockedTasks;
	}

	/**
	 * Blocked task getter method
	 * 
	 * @return
	 */
	public ArrayList<String> getBlockedTasks() {
		return blockedTasks;
	}

	/**
	 * Stores all extension HashMaps
	 */
	private HashMap<Integer, Integer> extensions = new HashMap<Integer, Integer>();
	/**
	 * Retuns the extension HashMaps
	 * 
	 * @return
	 */
	public HashMap<Integer, Integer> getExtensions() {
		return extensions;
	}

	/**
	 * Sets the extension
	 * 
	 * @param extension
	 */
	public void setExtension(HashMap<Integer, Integer> extension) {
		this.extensions = extension;
	}

	/**
	 * Checks for extension hashmap
	 * 
	 * @unused
	 */
	public boolean playerHasExtension(HashMap<Integer, String> Hash) {
		return true;
	}

	/**
	 * Stores all unlocked HashMaps
	 */
	private HashMap<Integer, String> unlocks = new HashMap<Integer, String>();

	/**
	 * Retuns the unlocked HashMaps
	 * 
	 * @return
	 */
	public HashMap<Integer, String> getUnlocks() {
		return unlocks;
	}

	/**
	 * Sets the unlocks
	 * 
	 * @param unlocks
	 */
	public void setUnlocks(HashMap<Integer, String> unlocks) {
		this.unlocks = unlocks;
	}

	/**
	 * Checks for unlocked hashmap
	 * 
	 * @unused
	 */
	public boolean playerHasUnlock(HashMap<Integer, String> Hash) {
		return true;
	}

	/**
	 * Stores previous interface being viewed
	 */
	public static int prevInterfaceId = 23400;

	/**
	 * Gets the previous interface
	 */
	public static int getPreviousInterface() {
		return prevInterfaceId;
	}

	/**
	 * Sets the previous interface
	 */
	public void setPreviousInterface(int interfaceId) {
		prevInterfaceId = interfaceId;
	}

	public String getButtonLowercaseName(int i){
		return ButtonData.buttonMap.get(i).name().toLowerCase();
	}
	
	/**
	 * Checks for existing purchases and marks with checkmark
	 * 
	 * @param player
	 */
	public void generateCheckMarks(Player player) {
		for (Entry<Integer, String> entrys : player.getSlayerInterface().getUnlocks().entrySet()) {
			for (ButtonData buttonData : ButtonData.values()) {
				if (entrys.getValue().equals(buttonData.name())) {
					player.debug("We have found: " + entrys.getValue());
					if (buttonData.getAction() == Action.UNLOCK_BUTTON) {
						player.getActionSender().sendConfig(580 + (buttonData.ordinal() - 10), 1);
					}
				}
			}
		}

		for (Entry<Integer, Integer> entrys : player.getSlayerInterface().getExtensions().entrySet()) {
			for (ButtonData buttonData : ButtonData.values()) {
				for (int button : buttonData.getButton()) {
					if (entrys.getKey().equals(button)) {
						player.debug("We have found extend " + entrys.getValue());
						if (buttonData.getAction() == Action.EXTEND_BUTTON) {
							player.getActionSender().sendConfig(560 + (buttonData.ordinal() - 26), 1);
						}
					}
				}
			}
		}
	}

	/**
	 * Opens the interface (not used yet)
	 * 
	 * @param player
	 */
	public void open(Player player) {
		Server.getTaskScheduler().schedule(new Task(1) {
			@Override
			public void execute() {
				generateCheckMarks(player);
				player.getActionSender().sendString("Reward Points: " + player.getSlayerPoints(), 23014);
				UnlockInterface unlock = new UnlockInterface();
				unlock.write(player);
				player.getActionSender().sendInterface(23400);
				this.stop();
			}
		});
	}

	/**
	 * 
	 * @param player
	 * @param buttonId
	 * @return Handles unlocking of slayer additions
	 */
	public boolean unlock(Player player, int buttonId) {
		UnlockButtons button = UnlockButtons.unlockButtons.get(buttonId);
		if (button == null) {
			player.debug("Null unlock button");
			return false;
		}
		player.getActionSender().sendString("" + Utility.optimizeText(button.getName()), 23106); 
		player.getActionSender().sendString("" + button.getDescription(), 23107);
		for(int lineId = 23108; lineId < 23110; lineId++)
		player.getActionSender().sendString("", lineId);
		player.getActionSender().sendString("<col=ff0000>Are you sure you want to pay?</col>", 23110);
		player.getActionSender().sendInterface(23100);
		return true;
	}

	/**
	 * 
	 * @param player
	 * @param buttonId
	 * @return Handles unlocking of slayer additions
	 */
	public static boolean extend(Player player, int buttonId) {
		ExtendButtons button = ExtendButtons.extendButtons.get(buttonId);
		if (button == null) {
			player.debug("Null extend button");
			return false;
		}
		player.getActionSender().sendString("" + Utility.optimizeText(button.getName()), 23106);
		player.getActionSender().sendString("" + button.getDescription(), 23107);
		player.getActionSender().sendString("<col=ff0000>Are you sure you want to pay?</col>", 23110);
		player.getActionSender().sendInterface(23100);
		return true;
	}

	/**
	 * 
	 * @param player
	 * @param buttonId
	 * @return Handles unlocking of slayer additions
	 */
	public static boolean block(Player player, int buttonId) {
		player.setSlayerAction(1);
		player.getActionSender().sendString("You are about to block: " + player.getSlayerTask(), 23106); 
		player.getActionSender().sendString("This costs 100 Slayer Points", 23107);
		player.getActionSender().sendString("<col=ff0000>Are you sure you want to pay?</col>", 23110);
		player.getActionSender().sendInterface(23100);

		return true;
	}

	/**
	 * Handles purchasing with slayer points
	 * 
	 * @param player
	 * @param amount
	 * @return
	 */
	public boolean purchase(Player player, int amount) {
		if (player.getSlayerPoints() >= amount) {
			player.setSlayerPoints(player.getSlayerPoints() - amount);
			player.getActionSender().sendString("Reward Points: " + player.getSlayerPoints(), 23014);
			return true;
		} else
			return false;

	}

	/**
	 * 
	 * @param player
	 * @param buttonId
	 * @return Handles the selection of the interface
	 */
	public boolean controlPanel(Player player, int buttonId) {
		ButtonData button = ButtonData.buttonMap.get(buttonId);
		if (button == null) {
			return false;
		}
		player.getActionSender().sendString("Reward Points: " + player.getSlayerPoints(), 23014);
		if(player.getSlayerInterface().getUnlocks().containsKey(buttonId)) {
			if (button.getAction() == Action.UNLOCK_BUTTON) {
				player.getActionSender().sendConfig(580 + (button.ordinal() - 10), 1);
				player.getActionSender().sendMessage("You have already purchased this unlock.");
				return false;
			}
		}
		if(player.getSlayerInterface().getExtensions().containsKey(buttonId)){
			if (button.getAction() == Action.EXTEND_BUTTON) {
				player.getActionSender().sendConfig(560 + (button.ordinal() - 26), 1); //turn back on
				player.getActionSender().sendMessage("You have already purchased this unlock.");
				return false;
			}
		}
		switch (button.getAction()) {	
		case INTERFACE:
			player.debug("Button: " + buttonId + " trying to open interface " + button.getInterface());
			player.getActionSender().sendInterface(button.getInterface());
			setPreviousInterface(button.getInterface());
			player.getActionSender().sendMessage("Previous interface = " + getPreviousInterface());
			return true;
		case SHOP:
			ShopManager.slayerShop(player);
			return true;
		case CANCEL:
			if (!Slayer.hasTask(player))
				return false;
			if (!purchase(player, button.getPoints())) {
				player.getActionSender().sendMessage("@red@You don't have enough points");
				return false;
			}
			player.setSlayerTask("none");
			player.setSlayerTaskAmount(0);
			player.getActionSender().sendMessage("@red@ Your task has been canceled.");
			String currentTask = player.getSlayerTask() != null ? "" + player.getSlayerTask() + " X " + player.getSlayerTaskAmount() : "Nothing";
			player.getActionSender().sendString("" + currentTask, 23208);
			return true;
		case BLOCK:
			if (blockedTasks.contains(player.getSlayerTask()))
				return false;
			if (Slayer.hasTask(player))
				block(player, buttonId);
			return true;
		case UNBLOCK:
			int max = 90186 + blockedTasks.size();
			System.out.println("SIZE: " + blockedTasks.size() + " MAX: " + (90186 + blockedTasks.size()));
			if (buttonId == 90186) {
				if (max >= 90186 && blockedTasks.size() >= 1)
					blockedTasks.remove(0);
			} else if (buttonId == 90187 && blockedTasks.size() >= 2) {
				if (max >= 90187)
					blockedTasks.remove(1);
			} else if (buttonId == 90188 && blockedTasks.size() >= 3) {
				if (max >= 90188)
					blockedTasks.remove(2);
			} else if (buttonId == 90189 && blockedTasks.size() >= 4) {
				if (max >= 90189)
					blockedTasks.remove(3);
			} else if (buttonId == 90190 && blockedTasks.size() >= 5) {
				if (max >= 90190)
					blockedTasks.remove(4);
			} else if (buttonId == 90191 && blockedTasks.size() >= 6) {
				if (max >= 90191)
					blockedTasks.remove(5);
			}
			blockedTasks.trimToSize();
			TaskInterface task = new TaskInterface();
			task.write(player);
			return true;

		case TASK_INTERFACE:
			TaskInterface penis = new TaskInterface();
			penis.write(player);
			setPreviousInterface(button.getInterface());
			player.getActionSender().sendInterface(button.getInterface());
			return true;
		case EXTEND_INERFACE:
			ExtendInterface unlock = new ExtendInterface();
			unlock.write(player);
			player.getActionSender().sendInterface(button.getInterface());
			setPreviousInterface(button.getInterface());
			player.getActionSender().sendString("Current Streak: " + player.getSlayerStreak() + " Record: " + player.getSlayerStreakRecord(), 23399);
			player.debug("Previous interface = " + getPreviousInterface());
			return true;
		case UNLOCK_BUTTON:
			unlock(player, buttonId);
			player.setSlayerSelection(buttonId, button.getunlockId(), button.name(), button.ordinal() - 7, 0);
			player.debug("Button: " + buttonId + " trying to open interface " + button.getInterface());
			return true;
		case EXTEND_BUTTON:
			extend(player, buttonId);
			player.setSlayerSelection(buttonId, button.getunlockId(), button.name(), button.ordinal() - 23, 1);
			player.debug("Button: " + buttonId + " trying to open interface " + button.getInterface());
			return true;
		case PURCHASE:
			if (player.getSlayerAction() == 0) { /// what one is unlock? what do you mean
				if (!purchase(player, ButtonData.buttonMap.get(player.getSlayerSelection()).getPoints())) {
					player.getActionSender().sendMessage("You don't have enough points");
					return false;
				}
				///unlocks
				if (player.getType() == 0) {
					if(getButtonLowercaseName(player.getSlayerSelection()).equals("teleporting")) {
						player.getInventory().contains(6798, 1);
						player.getActionSender().sendMessage("@red@You have received your Slayer Teleportation scroll");
					}
					player.getSlayerInterface().getUnlocks().put(player.getSlayerSelection(),
							player.getSlayerSelectionName());
				} else {
					player.getSlayerInterface().getExtensions().put(player.getSlayerSelection(),
							player.getSlayerNpcId());
				}
				player.getActionSender().sendMessage("You successfully purchased ");
				player.getActionSender().sendInterface(getPreviousInterface());
				return true;
			} else
			// block
			if (player.getSlayerAction() == 1) {
				if (!purchase(player, 100)) {
					player.getActionSender().sendMessage("@red@You don't have enough points");
					player.setSlayerAction(0);
					return false;
				}
				try {
					if (blockedTasks.contains(player.getSlayerTask())) {
						player.getActionSender().sendMessage("This task is already blocked.");
						player.getActionSender().removeAllInterfaces();
						player.setSlayerAction(0);
						return false;
					}
					if (blockedTasks.size() >= 6) {
						player.getActionSender().sendMessage("You have the maximum amount of blocks.");
						player.setSlayerAction(0);
						return false;
					}
					
					blockedTasks.add(player.getSlayerTask());
					TaskInterface task1 = new TaskInterface();
					task1.write(player);
					player.getActionSender().sendInterface(getPreviousInterface());
					player.setSlayerAction(0);
					return true;

				} catch (Exception name) {

				}
			}

		case BACK:
			if (getPreviousInterface() == 23400) {
				player.getActionSender().sendConfig(580 + (player.getSlayerOrdinal()), 0); // turn
																							// off
			} else {
				if (getPreviousInterface() == 23300)
					player.getActionSender().sendConfig(560 + (player.getSlayerOrdinal()), 0);
			}
			player.getActionSender().sendInterface(getPreviousInterface());
			return true;
		case CLOSE:
			player.getActionSender().removeAllInterfaces();
			return true;
		default:
			break;
		}
		return false;
	}

}
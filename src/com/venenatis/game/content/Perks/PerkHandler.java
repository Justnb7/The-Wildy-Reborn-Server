package com.venenatis.game.content.Perks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import com.venenatis.game.content.skills.slayer.interfaceController.SlayerInterface.ButtonData;
import com.venenatis.game.model.entity.player.Player;

/**
 * 
 * @author Harambe_ Class represents and handles the Slayer Interface
 *
 */
public class PerkHandler {
	
	public enum Type {
		COMBAT, SKILLING, OTHER;
	}
	/**
	 * Stores the player's selected perk
	 * from the interface
	 */
	private PlayerPerks selectedPerk;
	
	private final int SKILLING_BUTTON = 228139;
	private final int OTHER_BUTTON = 228143;
	private final int COMBAT_BUTTON = 228135;
	private final int PURCHASE = 228160;
	
	
	public enum PlayerPerks {
		CMB_EXP_MULTIPLIER(0, 100, 1, "Cmb Boost", "Receive 5% increased experience\\non all combat skills." + "\\n", "Does not apply to\\nPrayer Skill", "None", Type.COMBAT),
		SKILL_EXP_MULTIPLIER(0, 100, 1, "Skill Exp Booster", "Receive 5% increased experience\\non all non-combat skills." + "\\n", "Does not apply to\\nPrayer Skill", "None", Type.SKILLING),
		LIFESTEAL(0, 100, 1, "Life Steal I", "Receive .01 lifesteal on all npcs" + "\\n", "Does not apply to\\nPlayers", "None", Type.COMBAT),
		ANTIFIRE(0, 100, 1, "AntiFire", "No longer require anti-dragon shield" + "\\n", "None", "None", Type.COMBAT),
		HERB_DROP(0, 100, 1, "Herb Drop", "Receive double Herbs from NPC's" + "\\n", "None", "None", Type.COMBAT),
		EXP_INCREASE(0, 100, 1, "Herb Drop", "Receive double Herbs from NPC's" + "\\n", "None", "None", Type.SKILLING),
		YOUR_A_WIZARD(0, 100, 1, "You're a wizard Harry", "20% Chance to not have runes deducted\\n when casting magic spells." + "\\n", "None", "None", Type.COMBAT),
		SLAYING_GP(0, 100, 1, "Slaying GP", "40% Chance to receive cash reward\\n when completeing slayer tasks." + "\\n", "None", "50 Slayer", Type.OTHER),
		MONEYBAGS(0, 100, 1, "Money Bags", "Transform 35% of your experience gained\\n into gold.." + "\\n", "Does not apply to\\n combat skills.", "None", Type.OTHER);

		private int button;
		private int price;
		private double fuel;
		private String name;
		private String description;
		private String restrictions;
		private String requirements;
		private Type type;
		
		private PlayerPerks(int button, int price, double fuel, String name, String description, String restrictions, String requirements, Type type) {
			this.button = button;
			this.price = price;
			this.fuel = fuel;
			this.name = name;
			this.description = description;
			this.restrictions = restrictions;
			this.requirements = requirements;
			this.type = type;
		}

		public int getButton() {
			return button;
		}

		public int getPrice() {
			return price;
		}

		public String getName() {
			return name;
		}

		public String getDescription() {
			return description;
		}
		public String getRequirements() {
			return requirements;
		}
		public String getRestrictions() {
			return restrictions;
		}
		
		public int setButton(int button) {
			return this.button = button;
		}
		
		public Type getType() {
			return type;
		}

		public double getFuel() {
			return fuel;
		}

		public void setFuel(double fuel) {
			this.fuel = fuel;
		}
		
	}
	/**
	 * Blocked task getter method
	 * 
	 * @return
	 */

	private HashMap<String, Double> assignedPerksTest = new HashMap<String, Double>();
	
	public HashMap<String, Double> getAssignedPerkTest() {
		return assignedPerksTest;
	}
	
	public void setAssignedPerkTest(HashMap<String, Double> buttons) {
		this.assignedPerksTest = buttons;
	}
	
	public ArrayList<String> getAssignedPerksList() {
		return assignedPerksList;
	}
	private ArrayList<String> assignedPerksList = new ArrayList<String>(6);

	public void setAssignedPerksList(ArrayList<String> blockedTasks) {
		this.assignedPerksList = blockedTasks;
	}

	/**
	 * Retuns the extension HashMaps
	 * 
	 * @return
	 */
	public HashMap<PlayerPerks, Integer> getOwnedPerks() {
		return playerOwnedPerks;
	}
	/**
	 * Retuns the extension HashMaps
	 * 
	 * @return
	 */
	public HashMap<PlayerPerks, Integer> setOwnedPerks() {
		return playerOwnedPerks;
	}
	/**
	 * Retuns the extension HashMaps
	 * 
	 * @return
	 */
	public HashMap<PlayerPerks, Integer> getPerks() {
		return playerOwnedPerks;
	}

	/**
	 * Sets the extension
	 * 
	 * @param extension
	 */
	public void setPerkButton(HashMap<PlayerPerks, Integer> buttons) {
		this.playerOwnedPerks = buttons;
	}
	/**

	/**
	 * Stores Player's Purchased Perk
	 * 
	 */
	private HashMap<PlayerPerks, Integer> playerOwnedPerks = new HashMap<PlayerPerks, Integer>();
	
	/**
	 * Stores and lists all perks
	 * 
	 */
	private HashMap<Integer, PlayerPerks> buttons = new HashMap<Integer, PlayerPerks>();
	
	/**
	 * Clears the interface
	 * 
	 */
	public void clear(Player player) {
		int i = 0;
		for (PlayerPerks buttonData : PlayerPerks.values()) {
			player.getActionSender().sendString("", 55006 + i); 
			i += 4;
		}
	}
	
	/**
	 * Checks if player has 
	 * @param perk
	 * 
	 */
	public boolean hasPerk(PlayerPerks perk, Player player) {
		System.out.println("Checking if I have "+perk+" ? "+getPerks().containsKey(perk));
		if(this.getPerks().containsKey(perk)) {
			return true;
		}
		return false;
	}
	
	/**
	 * Opens Buy Interface
	 * @param player
	 */
	public void openBuyInterface(Player player) {
		player.getActionSender().sendInterface(55000);
		execute(player, 228135);
	}
	
	public void openAssignInterface(Player player) {
		writeAssign(player);
		player.getActionSender().sendInterface(65000);
		
	}
	
	/**
	 * Writes the Interface
	 * 
	 */
	public void writeBuy(Player player, Type type) {
		int i = 0;
		clear(player);
		buttons.clear();
		for (PlayerPerks buttonData : PlayerPerks.values()) {
			if(buttonData.getType() == type) {
				if(!player.getPerkHandler().hasPerk(buttonData, player)) {	
				buttonData.setButton(214219 + i);
				buttons.put(buttonData.getButton(), buttonData);
			player.getActionSender().sendString("" + buttonData.getName() /*ADD HERE FOR IXI LEVEL*/, 55006 + i); 
			player.getActionSender().sendString("My Points: "+player.getPerkPoints(), 58525); 
			i += 4;
				}
			}
		}
	}
	
	/**
	 * Executes the entire interface
	 * @param player
	 * @param buttonId
	 * @return
	 */
	public boolean execute(Player player, int buttonId) {
		if(buttonId == SKILLING_BUTTON) {
			writeBuy(player, Type.SKILLING);
		} else
		
		if(buttonId == OTHER_BUTTON) {
			writeBuy(player, Type.OTHER);
		} else
		
		if(buttonId == COMBAT_BUTTON) {
			writeBuy(player, Type.COMBAT);
		} else
		
		if(buttonId == PURCHASE) {
			System.out.println("Selected Perk: "+selectedPerk.toString());
			purchasePerk(player, selectedPerk);
			return true;
		} 
		PlayerPerks button = buttons.get(buttonId);
		
		if (button == null) {
			return false;
		}
		selectedPerk = button;
		player.getActionSender().sendString(button.getName(), 58519);
		player.getActionSender().sendString(button.getDescription(), 58601 );
		player.getActionSender().sendString(button.getRestrictions(), 58604 );
		player.getActionSender().sendString(button.getRequirements(), 58614 );	
		player.getActionSender().sendString("Price: "+button.getPrice(), 58527 );	
		return true;
	}
	
	/**
	 * Handles the purchasing of perks
	 * @param player
	 * @param perk
	 * @return
	 */
	public boolean purchasePerk(Player player, PlayerPerks perk){
		 if(player.getPerkPoints() >= perk.getPrice()) {
		if(!playerOwnedPerks.containsKey(perk)) {
			 player.getActionSender().sendMessage("You have successfully purcahsed "+perk.getName());
			 player.setPerkPoints(player.getPerkPoints() - perk.getPrice());
			 playerOwnedPerks.put(perk, 1);
			// playerAssignedPerks.put(perk, 1);
		} else {
			player.getActionSender().sendMessage("You already own this perk "+perk);
		}
	 } else 
		 player.getActionSender().sendMessage("You don't have enough Perk Points");
		return false;
	}
	
	/**
	 * Writes the Assign Interface
	 * 
	 */
	public void writeAssign(Player player) {
			int i = 0;
		for (Entry<PlayerPerks, Integer> entrys : player.getPerkHandler().getPerks().entrySet()) {
			player.getActionSender().sendString(""+entrys.getKey().getName().toString(), 65113 + i ); 
			entrys.getKey().setButton(254086 + i);
			selectedPerk = entrys.getKey();
			i += 4;
		}
			int i2=0;
		for (Entry<PlayerPerks, Integer> entrys : player.getPerkHandler().getOwnedPerks().entrySet()) {
			player.getActionSender().sendString(""+ entrys.getKey().getName().toString(), 65506 + i2 ); //assigned perks
			i2 += 4;
		}
	}
	public boolean executeAssign(Player player, int buttonId) {
		/*if (buttonId <= 254086 && buttonId >= 255070 || buttonId != 253239) {
			return false;
		}*/
		if(buttonId == 253239) {
			player.getActionSender().sendMessage("HERE2");
			player.getActionSender().sendMessage("Selected perk: "+selectedPerk.toString());
			assign(player, selectedPerk);
			writeAssign(player);
			return true;
		}
		
		PlayerPerks button = null;
		
		for (Entry<PlayerPerks, Integer> entrys : player.getPerkHandler().getPerks().entrySet()) {
			if(entrys.getKey().getButton() == buttonId) {
				button = entrys.getKey();
				System.out.println(button.toString());
				break;
			} 
		}
		PlayerPerks newButton = button;
		selectedPerk = newButton;
		if (button == null) {
			player.getActionSender().sendMessage("HERE1");
			return false;
		}
		player.getActionSender().sendString(button.getName(), 65004);
		player.getActionSender().sendString(button.getDescription(), 65051);
		player.getActionSender().sendString("Price: "+button.getPrice(), 58527 );	
		return true;
	

}
	public void assign(Player player, PlayerPerks perks) {
		//if(!assignedPerksList.contains(perks.getName())) {
		assignedPerksTest.put(perks.toString(), perks.getFuel());	
		assignedPerksList.add(perks.toString());
			player.getActionSender().sendMessage("Successfully assigned");
		//} else 
			//player.getActionSender().sendMessage("You already have this perk assigned");
	}	
	
	public double reduceFuel(Player player, String string) {
		switch(string) {
			case "MONEYBAGS":
				player.getActionSender().sendMessage("@red@Reducing fuel");
				return 1;
			}
		return 0.0;
	}
	
	
}

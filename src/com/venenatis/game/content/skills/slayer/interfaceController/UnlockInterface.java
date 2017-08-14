package com.venenatis.game.content.skills.slayer.interfaceController;

import java.util.HashMap;

import com.venenatis.game.model.entity.player.Player;


/**
 * 
 * @author Harambe_
 * Class represents and handles the Slayer Interface
 *
 */
public class UnlockInterface {
	

	
	public enum UnlockButtons {
		
		TELEPORTING(new int[]{91110}, 13666, 
				"Task Teleport",
				"Teleport to your tasks by right"
				+ "\\non your slayer gem <col=ff0000>(150 points)</col>"
				+ "\\n"),;
		
		
		
		private int[] button;
		private int itemNum;
		private String name;
		private String description;
		//points?
		
		private UnlockButtons(int[] button, int itemNum, String name, String description){
			this.button = button;
			this.itemNum = itemNum;
			this.name = name;
			this.description = description;
		}
		public int[] getButton() {
			return button;
		}
		public int getItemNum() {
			return itemNum;		
		}
		public String getName() {
			return name;
		}
		public String getDescription() {
			return description;
		}
		public static HashMap<Integer, UnlockButtons> unlockButtons = new HashMap<Integer, UnlockButtons>();

		static {
			for (final UnlockButtons unlockButtons : UnlockButtons.values()) {
				for(final int button : unlockButtons.getButton()) {
					UnlockButtons.unlockButtons.put(button, unlockButtons);
				}
			}

	}
	}
	
	public void write(Player player){
		for (UnlockButtons buttonData : UnlockButtons.values()) {
			player.getActionSender().sendUpdateItem(23425 + buttonData.ordinal(), buttonData.getItemNum(), 0,1);
			player.getActionSender().sendString(""+buttonData.getName(),23444 + buttonData.ordinal()); // make new classes for each tab
			player.getActionSender().sendString(""+buttonData.getDescription(),23463 + buttonData.ordinal());
			
		}
	}
	
}
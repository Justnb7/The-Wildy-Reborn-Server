package com.model.game.character.player.skill.slayer.interfaceController;

import java.util.HashMap;
import com.model.game.character.player.Player;


/**
 * 
 * @author Harambe_
 * Class represents and handles the Slayer Interface
 *
 */
public class ExtendInterface {
	

	
	public enum ExtendButtons {
		
		NEED_MORE_DARKNESS(new int[]{91010}, SlayerConstants.DARK_BEAST, 
				"Need more darkness",
				"Whenever you get a Dark Beast task, it"
				+ "\\nwill be a biger task."
				+ " <col=ff0000>(100 points)</col>"),
		
		ANKOU_VERY_MUCH(new int[]{91011}, SlayerConstants.ANKOU_MASK, 
				"Ankou very much",
				"Whenever you get an Ankou task, it will be"
				+ "\\na bigger task"
				+ " @red@100 points)"),

		SUQ_A_NOTHER_ONE(new int[]{91012}, SlayerConstants.SUQA, 
				"Suq-a-nother one",
				"Whenever you get a Suqah task, it will be"
				+ "\\na bigger task"
				+ " @red@100 points)"),
		
		FIRE_AND_DARKNESS(new int[]{91013}, SlayerConstants.KING_BLACK_DRAG, 
				"Fire & Darkness",
				"Whenever you get a Black Dragon task, it "
				+ "\\nwill be a bigger task"
				+ " @red@50 points)"),
		
		PEDAL_TO_THE_METALS(new int[]{91014}, SlayerConstants.STEEL_DRAGON, 
				"Pedal to the metals",
				"Whenever you get a Bronze, Iron, or Steel"
				+ "\\nDragon task, will be a bigger task"
				+ " @red@100 points)"),
		
		I_REALLY_MITH_YOU(new int[]{91015}, SlayerConstants.MITHRIL_BAR, 
				"I really mith you",
				"Whenever you get a Mithril Dragon task, it "
						+ "\\nwill be a bigger task"
						+ " @red@120 points)"),
		
		
		SPIRITUAL_FERVOUR(new int[]{91016}, SlayerConstants.STAFF, 
				"Spiritual fervour",
				"Whenever you get a Spiritual creature"
						+ "\\ntask it will will be a bigger task"
						+ " @red@100 points)"),
		
		BIRDS_OF_A_FEATHER(new int[]{91017}, SlayerConstants.FEATHER, 
				"Birds of a feather",
				"Whenever you get a Aviansie task, it will"
				+ "\\nbe a bigger task"
				+ " @red@100 points)"),	
		
		GREATER_CHALLENGE(new int[]{91018}, SlayerConstants.DEMON, 
				"Greater Challenge",
				"Whenever you get a Greater Demon task,"
				+ "\\nit will be a bigger task"
				+ " @red@100 points)"),	
		
		ITS_DARK_IN_HERE(new int[]{91019}, SlayerConstants.BLACK_DEMON, 
				"It's dark in here",
				"Whenever you get a Black Demon task,"
				+ "\\nit will be a bigger task"
				+ " @red@100 points)"),	
		
		BLEED_ME_DRY(new int[]{91020}, SlayerConstants.BLOODVELD, 
				"Bleed me dry",
				"Whenever you get a Bloodveld task,"
				+ "\\nit will be a bigger task"
				+ " @red@75 points)"),	
		
		SMELL_YA_LATER(new int[]{91021}, SlayerConstants.ABERANT_SPECTRE, 
				"Smell ya later",
				"Whenever you get a Aberrant Spectre"
				+ "\\ntask, it will be a bigger task"
				+ " @red@75 points)"),	
		HORROIFIC(new int[]{91022}, SlayerConstants.CAVE_HORROR, 
				"Horrorific",
				"Whenever you get a Cave Horror task, it"
				+ "\\ntask, it will be a bigger task"
				+ " @red@100 points)"),	
		DUST_YOU_SHALL_RETURN(new int[]{91023}, SlayerConstants.DUST_DEVIL, 
				"To dust you shall return",
				"Whenever you get a Dust devil task, it will"
				+ "\\nit will be a bigger task"
				+ " @red@100 points)"),	
		WYVER_ANOTHER_ONE(new int[]{91024}, SlayerConstants.SKELETAL_WYVERN, 
				"Wyver-nother one",
				"Whenever you get a Skeletal Wyvern task,"
				+ "\\nit will be a bigger task"
				+ " @red@100 points)"),	
		GET_SMASHED(new int[]{91025}, SlayerConstants.GARGOYLE, 
				"Get smashed",
				"Whenever you get a Gargoyle task, it will"
				+ "\\nbe a bigger task"
				+ " @red@100 points)"),	
		NECHS_PLEASE(new int[]{91026}, SlayerConstants.NECHRYAEL, 
				"Nechs please",
				"Whenever you get a Nechryael task, it will"
				+ "\\nbe a bigger task"
				+ " @red@100 points)"),	
		
		AUGMENT_MY_ABBIES(new int[]{91027}, SlayerConstants.ABYSSAL_DEMON, 
				"Augment my abbies",
				"Whenever you get an Abyssal Demon task,"
				+ "\\nit will be a bigger task"
				+ " @red@100 points)"),	
		KRACK_ON(new int[]{91028}, SlayerConstants.CAVE_KRAKEN, 
				"Krack on",
				"Whenever you get a Cave Kraken task, it"
				+ "\\nwill be a bigger task"
				+ " @red@100 points)");	
		
		
		private int[] button;
		private int itemNum;
		private String name;
		private String description;
		//points?
		
		private ExtendButtons(int[] button, int itemNum, String name, String description){
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
		public static HashMap<Integer, ExtendButtons> extendButtons = new HashMap<Integer, ExtendButtons>();

		static {
			for (final ExtendButtons unlockButtons : ExtendButtons.values()) {
				for(final int button : unlockButtons.getButton()) {
					ExtendButtons.extendButtons.put(button, unlockButtons);
				}
			}

	}
	}
	
	public void write(Player player){
		for (ExtendButtons buttonData : ExtendButtons.values()) {
			player.getActionSender().sendUpdateItem(23325 + buttonData.ordinal(), buttonData.getItemNum(), 0,1);
			player.getActionSender().sendString(""+buttonData.getName(),23344 + buttonData.ordinal()); // make new classes for each tab
			player.getActionSender().sendString(""+buttonData.getDescription(),23363 + buttonData.ordinal());
			
		}
	}
	
}
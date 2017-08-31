package com.venenatis.game.content.skills.slayer.interfaceController;

import java.util.HashMap;

import com.venenatis.game.model.entity.player.Player;


/**
 * Class represents and handles the Slayer Interface
 * @author Harambe_ && Patrick van Elderen redone the data loading, all other credits go to Harambe_
 *
 */
public class UnlockInterface {
	
	public enum UnlockButtons {
		
		GARGOYLE_SMASHER(new int[]{91110}, 4147, 
				"Gargoyle smasher",
				"Automatically smash gargoyles when"
				+ "\\nthey're on critical health, if you have the\\nright tool. <col=ff0000>(120 points)</col>"),
		
		SLUG_SALTER(new int[]{91111},4136, 
				"Slug slater",
				"Autmatically salt rock slugs when they're"
				+ "\\non critical health, if you have salt.@red@(80 \\n@red@points)"),
				
		REPTILE_FREEZER(new int[]{91112}, 6695,
				"Reptile freezer",
				"Autmatically freeze desert lizards when"
				+ "\\nthey're on critical health, if you have ice"
				+ "\\nwater. @red@90 points)"),
		
		SHROOM_SPRAYER(new int[]{91113}, 7420, 
				"'Shroom sprayer",
				"Autmatically spray mutated zygomites"
				+ "\\nwhen they're on critical health, if you"
				+ "\\nhave fungicide.@red@(110 points)"),
		
		BROADER_FLETCHING(new int[]{91114}, 11874, 
				"Broader fletching",
				"Learn to fletch borad arrows (with level 52\\n"
				+ "Fletching), broad bolts (with level 55\\n"
				+ "Fletching) and amethyst broad bolts (with\\nlevel 76 Fletching). @red@(300 points)"),
		
		MALEVOLENT_MASQUERADE(new int[]{91115}, 11864, 
				"Malevolent masquerade",
				"Learn to combine the protective Slayer"
				+ "\\nheadgear and Slayer gem into one"
				+ "\\n universal helmet, with level 55 crafting \\n@red@(400 points)"),
		
		RING_BLING(new int[]{91116},  11866, 
				"Ring bling",
				"Learn to craft your own Slayer Rings, with"
				+ "\\nlevel 75 crafting @red@(300 points)"),
		
		SEEING_RED(new int[]{91117}, 8134, 
				"Seeing red",
				"Duradel and Nieve will be able to assign\\nRed Dragons as your task. @red@(50 points)"),
		
		MITH_ME(new int[]{91118}, 3687, 
				"I hope you mith me",
				"Duradel and Nieve will be able to assign \\nMithril Dragons as your task. \\n@red@(80 points)"),
		
		WATCH_THE_BIRDIE(new int[]{91119}, 11525, 
				"Watch The Birdie",
				"Duradel and Nieve will be able to assign\\n Aviansies as your task. @red@(80 points)"),
		
		HOT_STUFF(new int[]{91120}, 3903, 
				"Hot stuff",
				"Duradel, Nieve and Chaeldar will be able \\nto assign TzHaar as your task You may\\nalso be offered a chance to slay TzTok-Jad\\nor Tzkal-Zuk. @red@(100 points)"),
		
		REPTILE_GOT_RIPPED(new int[]{91121}, 3260, 
				"Reptile got ripped",
				"Duradel, Nieve and Chaeldar will be able\\n to assign you Lizardmen. You need"
				+ "\\nShayzien House favour to fight lizardmen."
				+ "\\n@red@(75 points)"),
		
		LIKE_A_BOSS(new int[]{91122}, 3064, 
				"Like a boss",
				"Duradel and Nieve will be able to assign\\n boss monsters as your task. They will \\nchoose which boss you must kill. @red@(200 \\n@red@points)"),
		
		KING_BLACK_BONNET(new int[]{91123}, 19639, 
				"King black bonnet",
				"Learn how to combine a KBD head with your \\nslayer helm to colour it black.@red@(1000 poitns)"),
		
		KALPHITE_KHAT(new int[]{91124}, 19643, 
				"Kalphite Khat",
				"Learn how to combine a Kalphite Queen \\nhead with your slayer helm to colour it\\ngreen @red@(1000 points)"),
		
		UNHOLY_HELMET(new int[]{91125}, 19647, 
				"Unholy helmet",
				"Learn how to combine a Abyssal Demon \\nhead with your slayer helm to colour it red \\n@red@(1000 points)"),
		
		BIGGER_AND_BADDER(new int[]{91126}, 1451, 
				"Bigger and Badder",
				"Increase the risk against certain slayer"
				+ "\\nmonsters with the chance of a superior "
				+ "\\nversion spawning whilst on a slayer task\\n@red@ (150 points)"),
		
		DULY_NOTED(new int[]{91127}, 2360, 
				"Duly Noted",
				"Mithril dragons drop mithril bars in"
				+ "\\nbanknote form while killed on assignment."
				+ "\\n@red@ (200 points) ");
		
		
		
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
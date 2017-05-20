package com.model.game.character.player.skill.slayer.interfaceController;

import java.util.HashMap;
import com.model.game.character.player.Player;


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
				+ "\\n"),
		
		/*SLUG_SALTER(SlayerConstants.ROCKSLUG, 
				"Slug slater",
				"Autmatically salt rock slugs when they're"
				+ "\\non critical health, if you have salt. @red@(80"
				+ "\\npoints)"),
		REPTILE_FREEZER(SlayerConstants.DESERT_LIZARD, 
				"Reptile freezer",
				"Autmatically freeze desert lizards when"
				+ "\\nthey're on critical health, if you have ice"
				+ "\\nwater. @red@90 points)"),*/
		LIME_WHIP(new int[]{91111}, 20405, 
				"Taco Limey Time",
				"Have a chance to receive a drop of a "
				+ "\\nime whip when killing Abbysal Demons."
				+ "\\n \\n@red@400 points)"),
		
		BROADER_FLETCHING(new int[]{91112}, SlayerConstants.BROAD_ARROWS, 
				"Broader fletching",
				"Learn to fletch borad arrows (with level 52"
				+ "\\nFletching) and broad bolts (with level 55"
				+ "\\n Fletching). \\n@red@(110 points)"),
		
		MALEVOLENT_MASQUERADE(new int[]{91113}, SlayerConstants.SLAYER_HELMET, 
				"Malevolent masquerade",
				"Learn to combine the protective Slayer"
				+ "\\nheadgear and Slayer gem into one"
				+ "\\n universal helmet, with level 55 crafting \\n@red@(400 points)"),
		
		RING_BLING(new int[]{91114},  SlayerConstants.SLAYER_HELMET, 
				"Ring bling",
				"Learn to craft your own Slayer Rings, with"
				+ "\\nlevel 75 crafting \\n@red@(300 points)"),
		
		SEEING_RED(new int[]{91115},SlayerConstants.BABY_RED_DRAGON, 
				"Seeing red",
				"You can now be assigned Red Dragons as"
				+ "\\nyour task. \\n@red@(80 points)"),
		
		MITH_ME(new int[]{91116}, SlayerConstants.MITHRIL_DRAGON_MASK, 
				"I hope you mith me",
				"You can now be assigned Mithril Dragons"
				+ "\\nas your task. \\n@red@(80 points)"),
		
		WATCH_THE_BIRDIE(new int[]{91117}, SlayerConstants.FEATHER, 
				"Watch The Birdie",
				"You can now be assigned Aviansies as"
				+ "\\nyour task. \\n@red@(80 points)"),
		
		HOT_STUFF(new int[]{91118}, SlayerConstants.TOK_XIL, 
				"Hot stuff",
				"You can now be assigned TzHaar as your "
				+ "\\ntask. You may also be offered a chance "
				+ "\\nto slay TzTok-Jad. \\n@red@(100 points)"),
		
		REPTILE_GOT_RIPPED(new int[]{91119}, SlayerConstants.LIZARDMAN, 
				"Reptile got ripped",
				"You can now be assigned Lizardmen. You"
				+ "\\nneed XXX to fight lizardmen. "
				+ "\\n@red@(75 points)"),
		LIKE_A_BOSS(new int[]{91120}, SlayerConstants.VENENATIS, 
				"Like a boss",
				"You can now be assigned boss monsters as"
				+ "\\nas your task. They will choose which boss "
				+ "\\nyou must kill.\\n@red@(200 points)"),
		KING_BLACK_BONNET(new int[]{91121},SlayerConstants.BLACK_SLAYER_HELMET, 
				"King black bonnet",
				"Learn how to combine a KBD head with your"
				+ "\\nslayer helm to colour it black. \\n@red@(1000 poitns)"),
		KALPHITE_KHAT(new int[]{91122}, SlayerConstants.GREEN_SLAYER_HELMET, 
				"Kalphite Khat",
				"Learn how to combine a Kalphite Queen"
				+ "\\nhead with your slayer helm to colour it green "
				+ "\\ngreen. \\n@red@(1000 points)"),
		UNHOLY_HELMET(new int[]{91123}, SlayerConstants.RED_SLAYER_HELMET, 
				"Unholy helmet",
				"Learn how to combine a Abyssal Demonn"
				+ "\\nhead with your slayer helm to colour it red "
				+ "\\ngreen. \\n@red@(1000 points)"),
		BIGGER_AND_BADDER(new int[]{91124}, SlayerConstants.ABERANT_SPECTRE, 
				"Bigger and Badder",
				"Increase the risk against certain slayer"
				+ "\\nmonsters with the chance of a superior "
				+ "\\ngversion spawning whilst on a slayer task\\n@red@ (150 points)"),
		DULY_NOTED(new int[]{91125}, SlayerConstants.MITHRIL_BAR, 
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
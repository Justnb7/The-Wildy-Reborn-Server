package com.venenatis.game.content.clicking.npcs;

import com.venenatis.game.content.skills.thieving.Pickpocket;
import com.venenatis.game.model.entity.npc.NPC;
import com.venenatis.game.model.entity.npc.pet.Pet;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.world.shop.ShopManager;


public class NpcInteraction {

	/**
	 * First npc interaction
	 * 
	 * @param player
	 *            The player interaction with the npc
	 * @param npc
	 *            The npc
	 */
	public static void firstOption(Player player, NPC npc) {

		player.debug(String.format("[NpcInteraction #1] - NpcId: %d", npc.getId()));
		
		if (Pet.talktoPet(player, npc)) {
			return;
		}
		
		if (player.getFishing().clickNpc(player, npc, 1)) {
			return;
		}

		switch (npc.getId()) {
		
		case 6481:
			player.getDialogueManager().start("MAC", player);
			break;
		
		case 7456:
			player.getDialogueManager().start("PERDU", player);
			break;
		
		case 317:
			player.getDialogueManager().start("IRONMAN_PAUL", player);
			break;
			
		case 8016:
			ShopManager.open(player, 13);
			break;
		
		/* General store */
		case 506:
			ShopManager.open(player, 0);
			break;
			
		/* Mandrith PK Rewards */
		case 6599:
			ShopManager.open(player, 1);
			break;
			
		/* F2P Gear */	
		case 1304:
			ShopManager.open(player, 2);
			break;
			
		/* Vote store */
		case 5362:
			player.getActionSender().sendMessage("You currently have " + player.getVotePoints() + " vote points and " + player.getTotalVotes() + " total votes.");
			ShopManager.open(player, 4);
			break;
			
		/* Gear points */
		case 3951:
			player.getActionSender().sendMessage("@dre@[Server]: Free points refill to 2500 every 6 minutes.");
			player.getActionSender().sendMessage("@dre@[DYK]: You can type ::food, ::veng, ::barrage, and ::pots, to spawn them?");
			ShopManager.open(player, 7);
			break;
			
		/* PK Supplies */
		case 3894:
			ShopManager.open(player, 8);
			break;
		/* Skiller Shop */	
		case 505:
			ShopManager.open(player, 9);
			break;

		case 3257:
			player.getThieving().pickpocket(Pickpocket.FARMER, npc);
			break;

		/**
		 * Appearance npc
		 */
		case 1306:
			player.getActionSender().sendInterface(3559);
			break;
			
		case 315:
			player.getDialogueManager().start("EMBLEM_TRADER", player);
			break;
			
			/**
			 * Slayer masters
			 */
			case 401: // Turael
				player.getDialogueManager().start("TURAEL_DIALOGUE", player);
				break;
			case 402: // Mazchna
				player.getDialogueManager().start("MAZCHNA_DIALOGUE", player);
				break;

			case 403: // Vannaka
				player.getDialogueManager().start("VANNAKA_DIALOGUE", player);
				break;

			case 404: // Chaeldar
				player.getDialogueManager().start("CHAELDAR_DIALOGUE", player);
				break;

			case 405: // Duradel
				player.getDialogueManager().start("DURADEL_DIALOGUE", player);
				break;

			case 490: // Nieve
				player.getDialogueManager().start("NIEVE_DIALOGUE", player);
				break;
		}
	}

	/**
	 * Second interaction
	 * 
	 * @param player
	 *            The player interaction with the npc
	 * @param npc
	 *            The npc
	 */
	public static void secondOption(Player player, NPC npc) {
		
		player.debug(String.format("[NpcInteraction #2] - NpcId: %d", npc.getId()));
		
		if (Pet.pickup(player, npc)) {
			return;
		}
		
		if (player.getFishing().clickNpc(player, npc, 2)) {
			return;
		}

		switch (npc.getId()) {
		
		case 8016:
			ShopManager.open(player, 13);
			break;
		
		/* General store */
		case 506:
			ShopManager.open(player, 0);
			break;
			
		/* Mandrith PK Rewards */
		case 6599:
			ShopManager.open(player, 1);
			break;
		
		/* Skiller Shop */	
		case 505:
			ShopManager.open(player, 11);
			break;	
			
		case 315:
			ShopManager.open(player, 6);
			break;

		case 3078:
			player.getThieving().pickpocket(Pickpocket.MAN, npc);
			break;

		case 394:
			player.getBank().open();
			break;
			
		
		
		}
	}

	/**
	 * Third npc interaction
	 * 
	 * @param player
	 *            The player interaction with the npc
	 * @param npc
	 *            The npc
	 */
	public static void thirdOption(Player player, NPC npc) {

		if (Pet.transformPet(player, npc)) {
			return;
		}
		
		switch (npc.getId()) {
		case 401: // Turael
			ShopManager.open(player, 12);
			break;
			
		case 402: // Mazchna
			ShopManager.open(player, 12);
			break;
			
		case 403: // Vannaka
			ShopManager.open(player, 12);
			break;

		case 404: // Chaeldar
			ShopManager.open(player, 12);
			break;

		case 405: // Duradel
			ShopManager.open(player, 12);
			break;

		case 490: // Nieve
			ShopManager.open(player, 12);
			break;
		
		}
	}

	/**
	 * Fourth npc interaction
	 * 
	 * @param player
	 *            The player interaction with the npc
	 * @param npc
	 *            The npc
	 */
	public static void fourthOption(Player player, NPC npc) {

		switch (npc.getId()) {
		case 401: // Turael
			player.getSlayerInterface().open(player);
			break;
		case 402: // Mazchna
			player.getSlayerInterface().open(player);
			break;

		case 403: // Vannaka
			player.getSlayerInterface().open(player);
			break;

		case 404: // Chaeldar
			player.getSlayerInterface().open(player);
			break;

		case 405: // Duradel
			player.getSlayerInterface().open(player);
			break;

		case 490: // Nieve
			player.getSlayerInterface().open(player);
			break;
		}
	}

}

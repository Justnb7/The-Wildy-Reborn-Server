package com.venenatis.game.content.clicking.npcs;

import com.venenatis.game.content.skills.fishing.Fishing;
import com.venenatis.game.content.skills.fishing.FishingSpot;
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

		if (FishingSpot.fishingNPC(npc.getId())) {
			Fishing.attemptFishing(player, npc, 1);
			return;
		}

		switch (npc.getId()) {
		
		/* Skilling store */
		case 1325:
			ShopManager.open(player, 10);
			break;

		case 3257:
			player.getThieving().pickpocket(Pickpocket.FARMER, npc);
			break;

		case 315:
			player.dialogue().start("emblem_trader_dialogue", player);
			break;

		case 2180:
			player.dialogue().start("FIGHT_CAVE");
			break;

		case 6742:
			player.dialogue().start("MAXCAPE", player);
			break;

		case 954:
			player.dialogue().start("BARROWS", player);
			break;

		case 6599:
			player.dialogue().start("MANDRITH", player);
			break;

		/**
		 * Slayer masters
		 */
		case 401: // Turael
			player.dialogue().start("TURAEL_DIALOGUE", player);
			break;
		case 402: // Mazchna
			player.dialogue().start("MAZCHNA_DIALOGUE", player);
			break;

		case 403: // Vannaka
			player.dialogue().start("VANNAKA_DIALOGUE", player);
			break;

		case 404: // Chaeldar
			player.dialogue().start("CHAELDAR_DIALOGUE", player);
			break;

		case 405: // Duradel
			player.dialogue().start("DURADEL_DIALOGUE", player);
			break;

		case 490: // Nieve
			player.dialogue().start("NIEVE_DIALOGUE", player);
			break;

		/**
		 * Appearance npc
		 */
		case 1306:
			//player.write(new SendInterfacePacket(3559));
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

		if (FishingSpot.fishingNPC(npc.getId())) {
			Fishing.attemptFishing(player, npc, 2);
			return;
		}

		switch (npc.getId()) {

		case 2180:
			player.getAttributes().put("second_option", Boolean.TRUE);
			player.dialogue().start("FIGHT_CAVE");
			break;

		case 3078:
			player.getThieving().pickpocket(Pickpocket.MAN, npc);
			break;

		case 401: // Turael
			player.dialogue().start("TURAEL_DIALOGUE", player);
			break;
		case 402: // Mazchna
			player.dialogue().start("MAZCHNA_DIALOGUE", player);
			break;

		case 403: // Vannaka
			player.dialogue().start("VANNAKA_DIALOGUE", player);
			break;

		case 404: // Chaeldar
			player.dialogue().start("CHAELDAR_DIALOGUE", player);
			break;

		case 405: // Duradel
			player.dialogue().start("DURADEL_DIALOGUE", player);
			break;

		case 490: // Nieve
			player.dialogue().start("NIEVE_DIALOGUE", player);
			break;

		case 394:
			//player.getBank().open();
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

		

		}
	}

}

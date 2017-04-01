package com.model.game.character.player.content.clicking.npc;

import com.model.game.character.npc.NPC;
import com.model.game.character.npc.pet.Pet;
import com.model.game.character.player.Player;
import com.model.game.character.player.packets.out.SendInterfacePacket;
import com.model.game.character.player.skill.fishing.Fishing;
import com.model.game.character.player.skill.fishing.FishingSpot;
import com.model.game.character.player.skill.thieving.Pickpocket;
import com.model.game.shop.Shop;

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

		if (Pet.talktoPet(player, npc)) {
			return;
		}

		if (FishingSpot.fishingNPC(npc.getId())) {
			Fishing.attemptFishing(player, npc, 1);
			return;
		}

		switch (npc.getId()) {

		/**
		 * Kamfreena
		 */
		case 2461:
			player.getWarriorsGuild().handleDoor();
			break;

		case 3257:
			player.getThieving().pickpocket(Pickpocket.FARMER, npc);
			break;

		case 315:
			player.dialogue().start("emblem_trader_dialogue", player);
			break;

		case 5567:
			if (!player.deathShopChat) {
				player.dialogue().start("DEATH_SHOP_DIALOGUE", player);
			} else {
				player.dialogue().start("DEATH_SHOP_DIALOGUE2", player);
			}
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

		/**
		 * Shops
		 */

		case 3254:
			Shop.SHOPS.get("Donator Ticket Shop").openShop(player);
			break;

		case 6599:
			player.dialogue().start("MANDRITH", player);
			break;

		case 5362:
			Shop.SHOPS.get("Vote Rewards Shop").openShop(player);
			player.getActionSender().sendMessage("You currently have @blu@" + player.getVotePoints()
					+ "@bla@ vote points, and @blu@" + player.getTotalVotes() + "@bla@ total votes.");
			break;

		case 4058:
			Shop.SHOPS.get("Royalty Shop").openShop(player);
			break;

		case 505:
			Shop.SHOPS.get("Skilling Shop").openShop(player);
			break;

		case 2200:
			Shop.SHOPS.get("Team Cape Shop").openShop(player);
			break;

		case 1304:
			Shop.SHOPS.get("Low Level Shop").openShop(player);
			break;

		case 3193:
			Shop.SHOPS.get("Costume Shop").openShop(player);
			break;

		case 3894:
			player.dialogue().start("SIGMUND_THE_MERCHANT", player);
			break;

		case 3951:
			Shop.SHOPS.get("Gear Point Store").openShop(player);
			player.getActionSender().sendMessage("@red@Gear points@bla@ refill to @blu@2500@bla@ every 5 minutes.");
			player.getActionSender().sendMessage(
					"@blu@Did you know, you can type ::food, ::veng, ::barrage, and ::pots, to spawn them?");
			break;

		case 508:
		case 506:
			Shop.SHOPS.get("General Store").openShop(player);
			break;

		/**
		 * Skillcape shop
		 */
		case 4306:
			Shop.openSkillCape(player);
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
			player.write(new SendInterfacePacket(3559));
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

		if (Pet.pickup(player, npc)) {
			return;
		}

		if (FishingSpot.fishingNPC(npc.getId())) {
			Fishing.attemptFishing(player, npc, 2);
			return;
		}

		switch (npc.getId()) {

		case 2180:
			player.secondOption = true;
			player.dialogue().start("FIGHT_CAVE");
			break;

		case 3078:
			player.getThieving().pickpocket(Pickpocket.MAN, npc);
			break;

		case 315:
			Shop.SHOPS.get("Bounty Hunter Store").openShop(player);
			break;

		case 5567:
			if (!player.deathShopChat) {
				player.dialogue().start("DEATH_SHOP_DIALOGUE", player);
			} else {
				player.dialogue().start("DEATH_SHOP_DIALOGUE2", player);
			}
			break;

		case 7007:
		case 539:
			Shop.SHOPS.get("Vote Rewards Shop.").openShop(player);
			player.getActionSender().sendMessage("You currently have @blu@" + player.getVotePoints()
					+ "@bla@ vote points, and @blu@" + player.getTotalVotes() + "@bla@ total votes.");
			break;

		case 7008:
		case 547:
		case 6599:
			Shop.SHOPS.get("Player Killing Reward Shop.").openShop(player);
			break;

		case 6060:
			Shop.SHOPS.get("Ranged Equipment.").openShop(player);
			break;

		case 1052:
			Shop.SHOPS.get("Betty's Magic Emporium.").openShop(player);
			break;

		case 5251:
			Shop.SHOPS.get("Tutab's Magical Market.").openShop(player);
			break;

		case 1791:
			Shop.SHOPS.get("Food Shop.").openShop(player);
			break;

		case 1174:
			Shop.SHOPS.get("Potions Shop.").openShop(player);
			break;

		case 535:
			Shop.SHOPS.get("Horvik's Armour Shop.").openShop(player);
			break;

		case 1944:
			Shop.SHOPS.get("Weapons And Accessories Galore.").openShop(player);
			break;

		case 508:
		case 506:
			Shop.SHOPS.get("General Store").openShop(player);
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
			player.getPA().openBank();
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

		switch (npc.getId()) {
		
		case 401: // Turael
			Shop.SHOPS.get("Slayer Equipment").openShop(player);
			break;
		case 402: // Mazchna
			Shop.SHOPS.get("Slayer Equipment").openShop(player);
			break;

		case 403: // Vannaka
			Shop.SHOPS.get("Slayer Equipment").openShop(player);
			break;

		case 404: // Chaeldar
			Shop.SHOPS.get("Slayer Equipment").openShop(player);
			break;

		case 405: // Duradel
			Shop.SHOPS.get("Slayer Equipment").openShop(player);
			break;

		case 490: // Nieve
			Shop.SHOPS.get("Slayer Equipment").openShop(player);
			break;

		case 6599:
			Shop.SHOPS.get("PK Points Shop").openShop(player);
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
			Shop.SHOPS.get("Slayer Rewards").openShop(player);
			break;

		case 402: // Mazchna
			Shop.SHOPS.get("Slayer Rewards").openShop(player);
			break;

		case 403: // Vannaka
			Shop.SHOPS.get("Slayer Rewards").openShop(player);
			break;

		case 404: // Chaeldar
			Shop.SHOPS.get("Slayer Rewards").openShop(player);
			break;

		case 405: // Duradel
			Shop.SHOPS.get("Slayer Rewards").openShop(player);
			break;

		case 490: // Nieve
			Shop.SHOPS.get("Slayer Rewards").openShop(player);
			break;

		}
	}

}

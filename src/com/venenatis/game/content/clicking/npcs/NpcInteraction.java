package com.venenatis.game.content.clicking.npcs;

import com.venenatis.game.content.bounty.BountyHunter;
import com.venenatis.game.content.skills.fishing.Fishing;
import com.venenatis.game.content.skills.fishing.FishingSpot;
import com.venenatis.game.content.skills.thieving.Pickpocket;
import com.venenatis.game.model.combat.Combat;
import com.venenatis.game.model.combat.data.SkullType;
import com.venenatis.game.model.entity.npc.NPC;
import com.venenatis.game.model.entity.npc.pet.Pet;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.dialogue.DialogueManager;
import com.venenatis.game.model.entity.player.dialogue.DialogueOptions;
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
			//And then start dialogue
			DialogueManager.start(player, 0);
			//Set dialogue options
			player.setDialogueOptions(new DialogueOptions() {
				@Override
				public void handleOption(Player player, int option) {
					switch(option) {
					case 1:
						//Open pvp shop
						ShopManager.open(player, 6);
						break;
					case 2:
						//Sell emblems option
						player.setDialogueOptions(new DialogueOptions() {
							@Override
							public void handleOption(Player player, int option) {
								if(option == 1) {
									int cost = BountyHunter.getNetworthForEmblems(player);
									player.getActionSender().sendMessage("@red@You have received "+cost+" blood money for your emblem(s).");
									DialogueManager.start(player, 4);
								} else {
									player.getActionSender().removeAllInterfaces();
								}
							}
						});
						int value = BountyHunter.getNetworthForEmblems(player);
						if(value > 0) {
							player.setDialogue(DialogueManager.getDialogues().get(10)); //Yes / no option
							DialogueManager.sendStatement(player, "I will give you "+value+" blood money for those emblems. Agree?");
						} else {
							DialogueManager.start(player, 5);
						}
						break;
					case 3:
						//Skull me option
						if(player.isSkulled()) {
							DialogueManager.start(player, 3);
						} else {
							DialogueManager.start(player, 6);
							player.setDialogueOptions(new DialogueOptions() {
								@Override
								public void handleOption(Player player, int option) {
									if(option == 1) {
										Combat.skull(player, SkullType.SKULL, 300);
									} else if(option == 2) {
										Combat.skull(player, SkullType.RED_SKULL, 300);
									}
									player.getActionSender().removeAllInterfaces();
								}
							});
						}
						break;
					case 4:
						//Cancel option
						player.getActionSender().removeAllInterfaces();
						break;
					}
				}
			});
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
		
		/* General store */
		case 506:
			ShopManager.open(player, 0);
			break;
			
		/* Mandrith PK Rewards */
		case 6599:
			ShopManager.open(player, 1);
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
		case 315:
			//Sell emblems option
			player.setDialogueOptions(new DialogueOptions() {
				@Override
				public void handleOption(Player player, int option) {
					if(option == 1) {
						int cost = BountyHunter.getNetworthForEmblems(player);
						player.getActionSender().sendMessage("@red@You have received "+cost+" blood money for your emblem(s).");
						DialogueManager.start(player, 4);
					} else {
						player.getActionSender().removeAllInterfaces();
					}
				}
			});
			int value = BountyHunter.getNetworthForEmblems(player);
			if(value > 0) {
				player.setDialogue(DialogueManager.getDialogues().get(10)); //Yes / no option
				DialogueManager.sendStatement(player, "I will give you "+value+" blood money for those emblems. Agree?");
			} else {
				DialogueManager.start(player, 5);
			}
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
		case 315:
			if(player.isSkulled()) {
				DialogueManager.start(player, 3);
			} else {
				DialogueManager.start(player, 6);
				player.setDialogueOptions(new DialogueOptions() {
					@Override
					public void handleOption(Player player, int option) {
						if(option == 1) {
							Combat.skull(player, SkullType.SKULL, 300);
						} else if(option == 2) {
							Combat.skull(player, SkullType.RED_SKULL, 300);
						}
						player.getActionSender().removeAllInterfaces();
					}
				});
			}
			break;
		}
	}

}

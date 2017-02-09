package com.model.game.character.player.skill.crafting.leather;

import com.model.Server;
import com.model.game.Constants;
import com.model.game.character.Animation;
import com.model.game.character.player.Player;
import com.model.game.character.player.Skills;
import com.model.game.character.player.packets.encode.impl.SendClearScreen;
import com.model.game.character.player.packets.encode.impl.SendChatBoxInterface;
import com.model.game.character.player.packets.encode.impl.SendMessagePacket;
import com.model.game.character.player.packets.encode.impl.SendString;
import com.model.game.character.player.packets.encode.impl.SendInterface;
import com.model.game.character.player.packets.encode.impl.SendInterfaceModel;
import com.model.game.character.player.skill.crafting.CraftingData;
import com.model.task.ScheduledTask;

/**
 * 
 * @author Patrick van Elderen
 * @date 1-4-2016 00:53 AM
 * @credits to the creator of the data being used
 */

public class LeatherMaking extends CraftingData {

	/**
	 * Method used for sending the interface
	 * @param player
	 * @param itemUsed
	 * @param usedWith
	 */
	public static void craftLeatherInterface(final Player player, final int itemUsed, final int usedWith) {
		for (final leatherData l : leatherData.values()) {
			final int leather = (itemUsed == 1733 ? usedWith : itemUsed);
			if (leather == l.getLeather()) {
				if (l.getLeather() == 1741) {
					player.write(new SendInterface(2311));
					player.leatherType = leather;
					return;
				}
				String[] name = { "Body", "Chaps", "Bandana", "Boots", "Vamb", };
				if (l.getLeather() == 6289) {
					player.write(new SendChatBoxInterface(8938));
					player.write(new SendInterfaceModel(8941, 180, 6322));
					player.write(new SendInterfaceModel(8942, 180, 6324));
					player.write(new SendInterfaceModel(8943, 180, 6326));
					player.write(new SendInterfaceModel(8944, 180, 6328));
					player.write(new SendInterfaceModel(8945, 180, 6330));
					for (int i = 0; i < name.length; i++) {
						player.write(new SendString(name[i], 8949 + (i * 4)));
					}
					player.leatherType = leather;
					return;
				}
			}
		}
		for (final leatherDialogueData d : leatherDialogueData.values()) {
			final int leather = (itemUsed == 1733 ? usedWith : itemUsed);
			String[] name = { "Vamb", "Chaps", "Body", };
			if (leather == d.getLeather()) {
				player.write(new SendChatBoxInterface(8880));
				player.write(new SendInterfaceModel(8883, 180, d.getVambraces()));
				player.write(new SendInterfaceModel(8885, 180, d.getChaps()));
				player.write(new SendInterfaceModel(8884, 180, d.getBody()));
				for (int i = 0; i < name.length; i++) {
					player.write(new SendString(name[i], 8889 + (i * 4)));
				}
				player.leatherType = leather;
				return;
			}
		}
	}

	/**
	 * Represents the item amount needed "leather"
	 */
	private static int amount;

	/**
	 * This method is used for crafting the leather into d'hide armour
	 * @param player
	 * @param buttonId
	 */
	public static void craftLeather(final Player player, final int buttonId) {
		if (player.isSkilling) {
			return;
		}
		for (final leatherData l : leatherData.values()) {
			if (buttonId == l.getButtonId(buttonId)) {
				if (player.leatherType == l.getLeather()) {
					if (player.getSkills().getLevel(Skills.CRAFTING) < l.getLevel()) {
						player.write(new SendMessagePacket("You need a crafting level of " + l.getLevel() + " to make this."));
						player.write(new SendClearScreen());
						return;
					}
					if (!player.getItems().playerHasItem(1734)) {
						player.write(new SendMessagePacket("You need some thread to make this."));
						player.write(new SendClearScreen());
						return;
					}
					if (!player.getItems().playerHasItem(player.leatherType, l.getHideAmount())) {
						player.write(new SendMessagePacket("You need " + l.getHideAmount() + " " + player.getItems().getItemName(player.leatherType).toLowerCase() + " to make " + player.getItems().getItemName(l.getProduct()).toLowerCase() + "."));
						player.write(new SendClearScreen());
						return;
					}
					player.playAnimation(Animation.create(1249));
					player.write(new SendClearScreen());
					player.isSkilling = true;
					amount = l.getAmount(buttonId);
					Server.getTaskScheduler().schedule(new ScheduledTask(5) {
						@Override
						public void execute() {
							if (player.isSkilling) {
								if (!player.getItems().playerHasItem(1734)) {
									player.write(new SendMessagePacket("You have run out of thread."));
									this.stop();
									return;
								}
								if (!player.getItems().playerHasItem(player.leatherType, l.getHideAmount())) {
									player.write(new SendMessagePacket("You have run out of leather."));
									this.stop();
									return;
								}
								if (amount == 0) {
									this.stop();
									return;
								}
								player.getItems().deleteItem(1734, player.getItems().getItemSlot(1734), 1);
								player.getItems().deleteItem(player.leatherType, l.getHideAmount());
								player.getItems().addItem(l.getProduct(), 1);
								player.write(new SendMessagePacket("You make " + ((player.getItems().getItemName(l.getProduct()).contains("body")) ? "a" : "some") + " " + player.getItems().getItemName(l.getProduct()) + "."));
								player.getSkills().addExperience(Skills.CRAFTING, l.getXP() * Constants.SKILL_MODIFIER);
								player.playAnimation(Animation.create(1249));
								amount--;
								if (!player.getItems().playerHasItem(1734)) {
									player.write(new SendMessagePacket("You have run out of thread."));
									this.stop();
									return;
								}
								if (!player.getItems().playerHasItem(player.leatherType, l.getHideAmount())) {
									player.write(new SendMessagePacket("You have run out of leather."));
									this.stop();
									return;
								}
							} else {
								this.stop();
							}
						}

						@Override
						public void onStop() {
							player.isSkilling = false;
						}
					});
				}
			}
		}
	}
}
package com.model.game.character.player.skill.crafting.leather;

import com.model.game.character.player.Player;
import com.model.game.character.player.packets.encode.impl.SendMessagePacket;
import com.model.game.character.player.packets.encode.impl.SendString;
import com.model.game.character.player.packets.encode.impl.SendInterface;
import com.model.game.character.player.packets.encode.impl.SendInterfaceModel;
import com.model.game.character.player.skill.crafting.CraftingData;

public class Tanning extends CraftingData {

	public static void sendTanningInterface(final Player player) {
		player.write(new SendInterface(14670));
		for (final tanningData t : tanningData.values()) {
			player.write(new SendInterfaceModel(t.getItemFrame(), 250, t.getLeatherId()));
			player.write(new SendString(t.getName(), t.getNameFrame()));
			if (player.getItems().playerHasItem(995, t.getPrice())) {
				player.write(new SendString("@gre@Price: " + t.getPrice(), t.getCostFrame()));
			} else {
				player.write(new SendString("@red@Price: " + t.getPrice(), t.getCostFrame()));
			}
		}
	}

	public static void tanHide(final Player player, final int buttonId) {
		for (final tanningData t : tanningData.values()) {
			if (buttonId == t.getButtonId(buttonId)) {
				int amount = player.getItems().getItemCount(t.getHideId());
				if (amount > t.getAmount(buttonId)) {
					amount = t.getAmount(buttonId);
				}
				int price = (amount * t.getPrice());
				int coins = player.getItems().getItemCount(995);
				if (price > coins) {
					price = (coins - (coins % t.getPrice()));
				}
				if (coins < t.getPrice()) {
					player.write(new SendMessagePacket("You do not have enough coins to tan this hide."));
					return;
				}
				amount = (price / t.getPrice());
				final int hide = t.getHideId();
				final int leather = t.getLeatherId();
				if (player.getItems().playerHasItem(995, price)) {
					if (player.getItems().playerHasItem(hide)) {
						player.getItems().deleteItem2(hide, amount);
						player.getItems().deleteItem(995, player.getItems().getItemSlot(995), 20);
						player.getItems().addItem(leather, amount);
						player.write(new SendMessagePacket("The tanner tans the hides for you."));
					} else {
						player.write(new SendMessagePacket("You do not have any hides to tan."));
						return;
					}
				} else {
					player.write(new SendMessagePacket("You do not have enough coins to tan this hide."));
					return;
				}
			}
		}
	}
}
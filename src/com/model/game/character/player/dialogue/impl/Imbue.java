package com.model.game.character.player.dialogue.impl;

import com.model.game.character.player.dialogue.Dialogue;
import com.model.game.character.player.dialogue.Type;
import com.model.game.item.Item;

public class Imbue extends Dialogue {

	@Override
	protected void start(Object... parameters) {
		send(Type.CHOICE, "Select Option", "Imbue Archers Ring (20 BM)", "Imbue Berserker Ring (25 BM)", "Imbue Seers Ring (20 BM)", "Imbue Warriors Ring (20 BM)", "More...");
		setPhase(0);
	}
	
	@Override
	public void select(int index) {
		if (getPhase() == 0) {
			switch(index) {
			case 1:
				if (player.getInventory().playerHasItem(13307, 20) && player.getInventory().playerHasItem(6733)) {
					player.getInventory().remove(new Item(13307, 20));
					player.getInventory().remove(new Item(6733));
					player.getInventory().add(new Item(11771, 1));
					player.getActionSender().sendRemoveInterfacePacket();
				} else {
					player.getActionSender().sendMessage("You do not have the required items to imbue this ring.");
					player.getActionSender().sendRemoveInterfacePacket();
				}
				break;
			case 2:
				if (player.getInventory().playerHasItem(13307, 25) && player.getInventory().playerHasItem(6737)) {
					player.getInventory().remove(new Item(13307, 25));
					player.getInventory().remove(new Item(6737));
					player.getInventory().add(new Item(11773, 1));
					player.getActionSender().sendRemoveInterfacePacket();
				} else {
					player.getActionSender().sendMessage("You do not have the required items to imbue this ring.");
					player.getActionSender().sendRemoveInterfacePacket();
				}
				break;
			case 3:
				if (player.getInventory().playerHasItem(13307, 20) && player.getInventory().playerHasItem(6731)) {
					player.getInventory().remove(new Item(13307, 20));
					player.getInventory().remove(new Item(6731));
					player.getInventory().add(new Item(11770, 1));
					player.getActionSender().sendRemoveInterfacePacket();
				} else {
					player.getActionSender().sendMessage("You do not have the required items to imbue this ring.");
					player.getActionSender().sendRemoveInterfacePacket();
				}
				break;
			case 4:
				if (player.getInventory().playerHasItem(13307, 20) && player.getInventory().playerHasItem(6735)) {
					player.getInventory().remove(new Item(13307, 20));
					player.getInventory().remove(new Item(6735));
					player.getInventory().add(new Item(11772, 1));
					player.getActionSender().sendRemoveInterfacePacket();
				} else {
					player.getActionSender().sendMessage("You do not have the required items to imbue this ring.");
					player.getActionSender().sendRemoveInterfacePacket();
				}
				break;
			case 5:
				send(Type.CHOICE, "Select Option", "Imbue Ring Of The Gods (30BM)", "Imbue Tyrannical Ring (30BM)", "Imbue Treasonous Ring (30BM)", "More...");
				setPhase(1);
				break;
			}
		} else if (getPhase() == 1) {
			switch (index) {
			case 1:
				if (player.getInventory().playerHasItem(13307, 30) && player.getInventory().playerHasItem(12601)) {
					player.getInventory().remove(new Item(13307, 30));
					player.getInventory().remove(new Item(12601));
					player.getInventory().add(new Item(13202, 1));
					player.getActionSender().sendRemoveInterfacePacket();
				} else {
					player.getActionSender().sendMessage("You do not have the required items to imbue this ring.");
					player.getActionSender().sendRemoveInterfacePacket();
				}
				break;
			case 2:
				if (player.getInventory().playerHasItem(13307, 30) && player.getInventory().playerHasItem(12603)) {
					player.getInventory().remove(new Item(13307, 30));
					player.getInventory().remove(new Item(12603));
					player.getInventory().add(new Item(12691, 1));
					player.getActionSender().sendRemoveInterfacePacket();
				} else {
					player.getActionSender().sendMessage("You do not have the required items to imbue this ring.");
					player.getActionSender().sendRemoveInterfacePacket();
				}
				break;
			case 3:
				if (player.getInventory().playerHasItem(13307, 30) && player.getInventory().playerHasItem(12605)) {
					player.getInventory().remove(new Item(13307, 30));
					player.getInventory().remove(new Item(12605));
					player.getInventory().add(new Item(12692, 1));
					player.getActionSender().sendRemoveInterfacePacket();
				} else {
					player.getActionSender().sendMessage("You do not have the required items to imbue this ring.");
					player.getActionSender().sendRemoveInterfacePacket();
				}
				break;
			case 4:
				send(Type.CHOICE, "Select Option", "Imbue Crystal Bow (15 BM)", "Imbue Crystal Shield (15BM)", "Nevermind");
				setPhase(2);
				break;
			}
		} else if (getPhase() == 2) {
			switch (index) {
			case 1:
				if (player.getInventory().playerHasItem(13307, 15) && player.getInventory().playerHasItem(4212)) {
					player.getInventory().remove(new Item(13307, 15));
					player.getInventory().remove(new Item(4212));
					player.getInventory().add(new Item(11748, 1));
					player.getActionSender().sendRemoveInterfacePacket();
				} else {
					player.getActionSender().sendMessage("You do not have the required items to imbue this ring.");
					player.getActionSender().sendRemoveInterfacePacket();
				}
				break;
			case 2:
				if (player.getInventory().playerHasItem(13307, 15) && player.getInventory().playerHasItem(4224)) {
					player.getInventory().remove(new Item(13307, 15));
					player.getInventory().remove(new Item(4224));
					player.getInventory().add(new Item(11759, 1));
					player.getActionSender().sendRemoveInterfacePacket();
				} else {
					player.getActionSender().sendMessage("You do not have the required items to imbue this ring.");
					player.getActionSender().sendRemoveInterfacePacket();
				}
				break;
			case 3:
				player.getActionSender().sendRemoveInterfacePacket();
				break;
			}
		}
	}
}

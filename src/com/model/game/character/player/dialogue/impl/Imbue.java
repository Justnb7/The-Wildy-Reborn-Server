package com.model.game.character.player.dialogue.impl;

import com.model.game.character.player.dialogue.Dialogue;
import com.model.game.character.player.dialogue.Type;
import com.model.game.character.player.packets.encode.impl.SendClearScreen;
import com.model.game.character.player.packets.encode.impl.SendMessagePacket;

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
				if (player.getItems().playerHasItem(13307, 20) && player.getItems().playerHasItem(6733)) {
					player.getItems().deleteItem(13307, 20);
					player.getItems().deleteItem(6733);
					player.getItems().addItem(11771, 1);
					player.write(new SendClearScreen());
				} else {
					player.write(new SendMessagePacket("You do not have the required items to imbue this ring."));
					player.write(new SendClearScreen());
				}
				break;
			case 2:
				if (player.getItems().playerHasItem(13307, 25) && player.getItems().playerHasItem(6737)) {
					player.getItems().deleteItem(13307, 25);
					player.getItems().deleteItem(6737);
					player.getItems().addItem(11773, 1);
					player.write(new SendClearScreen());
				} else {
					player.write(new SendMessagePacket("You do not have the required items to imbue this ring."));
					player.write(new SendClearScreen());
				}
				break;
			case 3:
				if (player.getItems().playerHasItem(13307, 20) && player.getItems().playerHasItem(6731)) {
					player.getItems().deleteItem(13307, 20);
					player.getItems().deleteItem(6731);
					player.getItems().addItem(11770, 1);
					player.write(new SendClearScreen());
				} else {
					player.write(new SendMessagePacket("You do not have the required items to imbue this ring."));
					player.write(new SendClearScreen());
				}
				break;
			case 4:
				if (player.getItems().playerHasItem(13307, 20) && player.getItems().playerHasItem(6735)) {
					player.getItems().deleteItem(13307, 20);
					player.getItems().deleteItem(6735);
					player.getItems().addItem(11772, 1);
					player.write(new SendClearScreen());
				} else {
					player.write(new SendMessagePacket("You do not have the required items to imbue this ring."));
					player.write(new SendClearScreen());
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
				if (player.getItems().playerHasItem(13307, 30) && player.getItems().playerHasItem(12601)) {
					player.getItems().deleteItem(13307, 30);
					player.getItems().deleteItem(12601);
					player.getItems().addItem(13202, 1);
					player.write(new SendClearScreen());
				} else {
					player.write(new SendMessagePacket("You do not have the required items to imbue this ring."));
					player.write(new SendClearScreen());
				}
				break;
			case 2:
				if (player.getItems().playerHasItem(13307, 30) && player.getItems().playerHasItem(12603)) {
					player.getItems().deleteItem(13307, 30);
					player.getItems().deleteItem(12603);
					player.getItems().addItem(12691, 1);
					player.write(new SendClearScreen());
				} else {
					player.write(new SendMessagePacket("You do not have the required items to imbue this ring."));
					player.write(new SendClearScreen());
				}
				break;
			case 3:
				if (player.getItems().playerHasItem(13307, 30) && player.getItems().playerHasItem(12605)) {
					player.getItems().deleteItem(13307, 30);
					player.getItems().deleteItem(12605);
					player.getItems().addItem(12692, 1);
					player.write(new SendClearScreen());
				} else {
					player.write(new SendMessagePacket("You do not have the required items to imbue this ring."));
					player.write(new SendClearScreen());
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
				if (player.getItems().playerHasItem(13307, 15) && player.getItems().playerHasItem(4212)) {
					player.getItems().deleteItem(13307, 15);
					player.getItems().deleteItem(4212);
					player.getItems().addItem(11748, 1);
					player.write(new SendClearScreen());
				} else {
					player.write(new SendMessagePacket("You do not have the required items to imbue this ring."));
					player.write(new SendClearScreen());
				}
				break;
			case 2:
				if (player.getItems().playerHasItem(13307, 15) && player.getItems().playerHasItem(4224)) {
					player.getItems().deleteItem(13307, 15);
					player.getItems().deleteItem(4224);
					player.getItems().addItem(11759, 1);
					player.write(new SendClearScreen());
				} else {
					player.write(new SendMessagePacket("You do not have the required items to imbue this ring."));
					player.write(new SendClearScreen());
				}
				break;
			case 3:
				player.write(new SendClearScreen());
				break;
			}
		}
	}
}

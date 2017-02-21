package com.model.game.character.player.dialogue.impl.chat;

import com.model.game.character.player.dialogue.Dialogue;
import com.model.game.character.player.dialogue.Type;
import com.model.game.shop.Shop;

/**
 * 
 * @author Patrick van Elderen
 *
 */
public class Mandrith extends Dialogue {

	@Override
	protected void start(Object... parameters) {
		send(Type.CHOICE, "Select Option", "Blood Money Rewards", "Imbue");
		setPhase(0);
	}
	
	@Override
	public void select(int index) {
		if (getPhase() == 0) {
			switch(index) {
			case 1:
				Shop.SHOPS.get("Blood money rewards").openShop(player);
				break;
			case 2:
				player.dialogue().start("IMBUE", player);
				break;
			
			}
		}
	}

}

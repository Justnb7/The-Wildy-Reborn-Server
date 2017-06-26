package com.venenatis.game.model.entity.player.dialogue.impl.chat;

import com.venenatis.game.model.entity.player.dialogue.Dialogue;
import com.venenatis.game.model.entity.player.dialogue.Type;

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
				//Shop.SHOPS.get("Blood money rewards").openShop(player);
				break;
			case 2:
				player.dialogue().start("IMBUE", player);
				break;
			
			}
		}
	}

}

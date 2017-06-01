package com.model.game.character.player.dialogue.impl;

import com.model.game.character.player.dialogue.Dialogue;
import com.model.game.character.player.dialogue.Type;

/**
 * 
 * @author Patrick van Elderen
 *
 */
public class SigmundTheMerchant extends Dialogue {

	@Override
	protected void start(Object... parameters) {
		send(Type.CHOICE, "Select Option", "Food && Potions", "Armour Shop", "Magic Shop", "Range Shop", "Weapons and Accessories");
		setPhase(0);
	}
	
	@Override
	public void select(int index) {
		if (getPhase() == 0) {
			switch (index) {
			case 1:
				//Shop.SHOPS.get("Food && Potions Shop").openShop(player);
				break;
			case 2:
				//Shop.SHOPS.get("Armour Shop").openShop(player);
				break;
			case 3:
				//Shop.SHOPS.get("Magic Shop").openShop(player);
				break;
			case 4:
				//Shop.SHOPS.get("Range Shop").openShop(player);
				break;
			case 5:
				//Shop.SHOPS.get("Weapons and Accessories").openShop(player);
				break;
			}
		}
	}

}

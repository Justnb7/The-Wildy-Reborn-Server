package com.model.game.character.player.dialogue.impl.slayer;

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
		send(Type.CHOICE, "Select Option", "Blood Money Rewards", "Imbue"/*, "Gambles..."*/);
		setPhase(0);
	}
	
	@Override
	public void select(int index) {
		if (getPhase() == 0) {
			switch(index) {
			case 1:
				Shop.SHOPS.get("PK Rewards").openShop(player);
				break;
			case 2:
				player.dialogue().start("IMBUE", player);
				break;
			case 3:
				send(Type.CHOICE, "Select Option", "Coins Gamble@blu@(10K/100K)@red@ 1 BM", "Void Gamble@blu@(Helm, Gloves, Top, Bottom) @red@25 BM", "Armour Gamble@blu@(Bandos, DFS, Armadyl) @red@50 BM", "Weapon Gamble@blu@(Claws, Godsword, Anchor) @red@75 BM");
				setPhase(1);
				break;
			}
		} else if(getPhase() == 1) {
			switch(index) {
			case 1:
				player.getGamble().coinsGamble();
				break;
			case 2:
				player.getGamble().voidGamble();
				break;
			case 3:
				player.getGamble().armourGamble();
				break;
			case 4:
				player.getGamble().weaponGamble();
				break;
			}
		}
	}

}

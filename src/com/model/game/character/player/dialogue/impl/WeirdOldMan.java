package com.model.game.character.player.dialogue.impl;

import com.model.game.character.player.dialogue.Dialogue;
import com.model.game.character.player.dialogue.Type;
import com.model.game.item.Item;
import com.model.game.shop.Shop;

public class WeirdOldMan extends Dialogue {

	@Override
	protected void start(Object... parameters) {
		send(Type.CHOICE, "Select Option", "Barrows Shop", "Fix Barrows");
		setPhase(0);
	}
	
	@Override
	public void select(int index) {
		if (getPhase() == 0) {
			switch(index) {
			case 1:
				Shop.SHOPS.get("Barrows Shop").openShop(player);
				break;
			case 2:
				fixAllBarrows();
				break;
			}
		}
	}
	
	public int[][] brokenBarrows = { { 4708, 4860 }, { 4710, 4866 }, { 4712, 4872 }, { 4714, 4878 }, { 4716, 4884 }, { 4720, 4896 },
			{ 4718, 4890 }, { 4722, 4902 }, { 4732, 4932 }, { 4734, 4938 }, { 4736, 4944 }, { 4738, 4950 }, { 4724, 4908 }, { 4726, 4914 },
			{ 4728, 4920 }, { 4730, 4926 }, { 4745, 4956 }, { 4747, 4962 }, { 4749, 4968 }, { 4751, 4974 }, { 4753, 4980 }, { 4755, 4986 },
			{ 4757, 4992 }, { 4759, 4998 } };
	
	public void fixAllBarrows() {
		int totalCost = 0;
		int cashAmount = player.getInventory().amount(995);
		for (int j = 0; j < player.playerInventory.length; j++) {
			boolean breakOut = false;
			for (int i = 0; i < brokenBarrows.length; i++) {
				if (player.playerInventory[j]-1 == brokenBarrows[i][1]) {					
					if (totalCost + 50000 > cashAmount) {
						breakOut = true;
						player.getActionSender().sendMessage("You need 50 000 coins to repair a piece of Barrows' armour!");
						player.getActionSender().sendRemoveInterfacePacket();
						break;
					} else {
						totalCost += 50000;
					}
					player.playerInventory[j] = brokenBarrows[i][0]+1;
					player.getActionSender().sendMessage("You repair your "+player.getItems().getItemName(brokenBarrows[i][0]+1)+".");
				}		
			}
			if (breakOut)		
				break;
		}
		if (totalCost > 0)
			player.getInventory().remove(new Item(995, player.getItems().getItemSlot(995)), totalCost);	
		player.getActionSender().sendRemoveInterfacePacket();
	}
	
}

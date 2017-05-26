package com.model.game.character.player.content.clicking.object;

import com.model.action.Action;
import com.model.game.character.player.Player;
import com.model.game.character.player.skill.smithing.Smithing;
import com.model.game.character.player.skill.smithing.Smithing.Bar;
import com.model.game.item.Item;
import com.model.game.location.Location;
import com.model.utility.cache.ObjectDefinition;

public class ItemOnObjectInteract {
	
	public static void handle(Player player, int obj, Location loc, Item item) {
		int itemId = item.getId();
		
		ObjectDefinition definition = ObjectDefinition.get(obj);
		Action action = null;
		
		action = new Action(player, 0) {
			@Override
			public CancelPolicy getCancelPolicy() {
				return CancelPolicy.ALWAYS;
			}
			@Override
			public StackPolicy getStackPolicy() {
				return StackPolicy.NEVER;
			}
			@Override
			public AnimationPolicy getAnimationPolicy() {
				return AnimationPolicy.RESET_ALL;
			}
			@Override
			public void execute() {
				player.debug("atleast we enter the task...");
				if(definition.getName().equalsIgnoreCase("Anvil")) {
					
					Bar bar = Bar.forId(item.getId());
					if(bar != null) {
						Smithing.openSmithingInterface(player, bar);
					}
				} else {
					switch (itemId) {

					}
				}
				this.stop();
			}			
		};
		if(action != null) {
			player.getActionQueue().addAction(action);
		}
	}

}
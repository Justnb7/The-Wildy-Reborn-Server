package com.model.game.character.player.dialogue.impl.chat;

import com.model.game.character.combat.magic.SpellBook;
import com.model.game.character.combat.weapon.AttackStyle;
import com.model.game.character.player.Player;
import com.model.game.character.player.dialogue.Dialogue;
import com.model.game.character.player.dialogue.Expression;
import com.model.game.character.player.dialogue.Type;
import com.model.game.character.player.packets.out.SendRemoveInterface;
import com.model.game.character.player.packets.out.SendInterface;
import com.model.game.character.player.packets.out.SendSidebarInterface;
import com.model.game.item.Item;

public class RunescapeGuide extends Dialogue {
	
	private static final int NPC_ID = 3308;

	@Override
	protected void start(Object... parameters) {
		send(Type.NPC, NPC_ID, Expression.DEFAULT, "Welcome to Luzoxpk, "+player.getName(), "First, you need to choose your game mode.");
		setPhase(0);
	}
	
	@Override
	protected void next() {
		if (getPhase() == 0) {
			send(Type.CHOICE, "Select an Option", "@red@Instant PK", "(click-to-change stats, normal point gain)", "@red@Train", "(train your stats; bonus loot/PK points and more!)");
			setPhase(1);
		}
	}

	@Override
	protected void select(int index) {
		if (getPhase() == 1) {
			switch(index) {
			case 2:
				player.setGameMode("PKER");
				handleStarterKit(player);
				player.setSpellBook(SpellBook.LUNAR);
				player.setTutorial(false);
				player.write(new SendRemoveInterface());
				for (int i = 0; i < 7; i++) {
					player.getSkills().setLevel(i, 99);
					player.getSkills().setExperience(i, 13034431);
				}
	    		player.updateRequired = true;
	    		player.appearanceUpdateRequired = true;
	    		player.write(new SendInterface(3559));
				player.canChangeAppearance = true;
				break;
			case 4:
				player.setGameMode("TRAINED");
				handleStarterKit(player);
				player.setSpellBook(SpellBook.MODERN);
				player.setTutorial(false);
				player.write(new SendRemoveInterface());
	    		player.updateRequired = true;
	    		player.appearanceUpdateRequired = true;
	    		player.write(new SendInterface(3559));
				player.canChangeAppearance = true;
				break;
			}
		}
	}
	
	private void handleStarterKit(Player player) {
		player.setAttackStyle(AttackStyle.ACCURATE);
		player.write(new SendSidebarInterface(0, 5855));
		player.getActionSender().sendString("Unarmed", 5857);
		Item[] starterItems = { //Runes
				new Item(560, 100_000),
				new Item(565, 100_000),
				new Item(555, 100_000),
				new Item(557, 100_000),
				new Item(9075, 100_000),
				//Consumables
				new Item(2440, 1000),
				new Item(2436, 1000),
				new Item(2442, 1000),
				new Item(2434, 1000),
				new Item(6685, 1000),
				new Item(3024, 1000),
				new Item(2444, 1000),
				new Item(2442, 1000),
				new Item(2442, 1000),
				new Item(385, 1000),
				//Gear
				new Item(1079, 100),
				new Item(1093, 100),
				new Item(1127, 100),
				new Item(10828, 100),
				new Item(4131, 100),
				new Item(4587, 100),
				new Item(5698, 100),
				new Item(2503, 100),
				new Item(2491, 100),
				new Item(2497, 100),
				new Item(4089, 100),
				new Item(4091, 100),
				new Item(4093, 100),
				new Item(4095, 100),
				new Item(4097, 100),
				new Item(4089, 100) };
		for (Item item : starterItems) {
			player.getItems().addItemToBank(item);
		}
		player.write(new SendInterface(3559));
		player.setReceivedStarter(true);
		
	}
	
}

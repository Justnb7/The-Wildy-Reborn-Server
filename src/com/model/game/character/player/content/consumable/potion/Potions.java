package com.model.game.character.player.content.consumable.potion;

import java.util.Objects;

import com.model.Server;
import com.model.game.character.Animation;
import com.model.game.character.combat.Combat;
import com.model.game.character.player.Boundary;
import com.model.game.character.player.Player;
import com.model.game.character.player.content.consumable.Consumable;
import com.model.game.character.player.content.multiplayer.MultiplayerSessionType;
import com.model.game.character.player.content.multiplayer.duel.DuelSession;
import com.model.game.character.player.content.multiplayer.duel.DuelSessionRules.Rule;
import com.model.game.character.player.packets.out.SendSoundPacket;
import com.model.game.item.Item;

/**
 * Handles drinking potions
 * 
 * @author Arithium
 * 
 */
public class Potions extends Consumable {

	/**
	 * The data of the potion attempting to be consumed
	 */
	private final PotionData data;
	/**
	 * The slot of the consumable potion
	 */
	private final int slot;

	/**
	 * Constructs a new consumable potion
	 * 
	 * @param player
	 * @param data
	 * @param slot
	 */
	public Potions(Player player, PotionData data, int slot) {
		super(player, 0, 1800);
		this.data = data;
		this.slot = slot;
	}

	@Override
	public void consume() {
		super.setCurrentDelay("potion");
		if (getPlayer().isDead()) {
			return;
		}
		if (Boundary.isIn(getPlayer(), Boundary.DUEL_ARENAS)) {
			DuelSession session = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(getPlayer(), MultiplayerSessionType.DUEL);
			if (Objects.nonNull(session)) {
				if (session.getRules().contains(Rule.NO_DRINKS)) {
					getPlayer().getActionSender().sendMessage("Drinks have been disabled for this duel.");
					return;
				}
			}
		}
		Item item = new Item(data.getPotionId());
		Combat.resetCombat(getPlayer());
		getPlayer().attackDelay += 2;
		getPlayer().playAnimation(Animation.create(829));
		getPlayer().write(new SendSoundPacket(334, 1, 2));
		getPlayer().getInventory().set(slot, new Item(data.getReplacement()));
		data.getPotionEffect().handle(getPlayer());
		String message = data.getReplacement() != 229 ? "You drink a dose of the " + item.getDefinition().getName() + "." : "You drink the last dose of your " + item.getDefinition().getName() + ".";
		getPlayer().getActionSender().sendMessage(message);
	}
}
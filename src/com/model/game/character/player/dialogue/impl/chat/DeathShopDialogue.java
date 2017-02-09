package com.model.game.character.player.dialogue.impl.chat;

import com.model.game.character.player.dialogue.Dialogue;
import com.model.game.character.player.dialogue.Expression;
import com.model.game.character.player.dialogue.Type;

/**
 * @author lare96 <http://github.com/lare96>
 */
public class DeathShopDialogue extends Dialogue {

	@Override
	protected void start(Object... parameters) {
		send(Type.NPC, 5567, Expression.EVIL, "Greetings " + player.getName() + ", I am a new feature ", "known as the 'Death store and every time you", " die you're given a limited amount of time", "To return to your items..");
		setPhase(0);
	}

	@Override
	public void next() {
		if (getPhase() == 0) {
			send(Type.NPC, 5567, Expression.EVIL, "In the unlikely event that your items dissappear", "Before you collect them I will collect them for you..", "This however will cost you GP to purchase back", "Your items that are collected by me.");
			setPhase(getPhase() + 1);
		} else {
			if (getPhase() == 1) {
				send(Type.NPC, 5567, Expression.EVIL, "If you log out after dying while your items are", "Are on the ground, they will NOT be collected", "If you log back in while they're still on ", "The ground they will be collected when they vanish");
				setPhase(getPhase() + 1);
			} else {
				if (getPhase() == 2) {
					send(Type.NPC, 5567, Expression.EVIL_LAUGH_SHORT, "Please take notice that if you die again the ", "items that are in the store WILL be deleted you", "should also note that it can be expensive to purchase ", "Your items back, so attempt to retrieve them after dying.");
					setPhase(getPhase() + 1);
				} else {
					if (getPhase() == 3) {
						player.deathShopChat = true;
						stop();
					}
				}
			}
		}
	}

	@Override
	protected void select(int index) {
	}
}
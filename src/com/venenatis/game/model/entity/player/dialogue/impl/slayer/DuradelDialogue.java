package com.venenatis.game.model.entity.player.dialogue.impl.slayer;

import com.venenatis.game.content.skills.slayer.Slayer;
import com.venenatis.game.content.skills.slayer.SlayerTaskManagement;
import com.venenatis.game.model.entity.npc.NPC;
import com.venenatis.game.model.entity.player.dialogue.Dialogue;
import com.venenatis.game.model.entity.player.dialogue.Expression;
import com.venenatis.game.model.entity.player.dialogue.Type;

/**
 * The dialogue enacted by Duradel the boss slayer master (Boss Tasks)
 * 
 * @author Patrick van Elderen
 *
 */
public class DuradelDialogue extends Dialogue {

	private static final int NPC_ID = 405;

	@Override
	protected void start(Object... parameters) {
		if (!player.getFirstSlayerTask()) {
			player.getActionSender().sendMessage("You cannot talk to Duradel as you are yet to start the 'Slayer' skill.");
			player.getActionSender().sendMessage("Talk to @blu@Turael@bla@ in Edgeville to do so.");
		} else {
			send(Type.NPC, NPC_ID, Expression.DEFAULT, "What do you want?");
			if (!player.getFirstBossSlayerTask()) {
				setPhase(0);
			} else {
				setPhase(10);
			}
		}
	}

	@Override
	protected void next() {

		if (getPhase() == 0) {
			send(Type.CHOICE, DEFAULT_OPTION_TITLE, "Who are you?", "Don't be so rude.", "Nevermind.");
			setPhase(1);
		} else if (getPhase() == 1) {
			send(Type.NPC, NPC_ID, Expression.DEFAULT, "What do you mean - who am I?", "I am Duradel, the true master of the Slayer skill", "I can set tasks that will have their contractors", "pleading for mercy.");
			setPhase(2);
		} else if (getPhase() == 2) {
			send(Type.NPC, NPC_ID, Expression.DEFAULT, "My assignments give real rewards and they", "are not like the other so called", "Slayer Master's tasks...");
			setPhase(3);
		} else if (getPhase() == 3) {
			send(Type.NPC, NPC_ID, Expression.DEFAULT, "The assignments I set are for boss-slayers.", "You will have to kill high-leveled monsters,", "in the aim to complete the task I set.");
			setPhase(4);
		} else if (getPhase() == 4) {
			if (Slayer.hasTask(player)) {
				send(Type.NPC, NPC_ID, Expression.DEFAULT, "It seems like you have a task anyway.", "You should probably go and finish that or you", "can reset it by talking to Nieve.");
				setPhase(9);
			} else {
				SlayerTaskManagement.bossTask(player);
				send(Type.NPC, NPC_ID, Expression.DEFAULT, "I didn't think you'd actually take the task.", "Fine. Your task is to kill " + player.getSlayerTaskAmount(), "@blu@" + NPC.getName(player.getSlayerTask()) + "@bla@.", "Good luck " + player.getName() + ", you'll need it.");
				player.setFirstBossSlayerTask(false);
				setPhase(9);
			}
		} else if (getPhase() == 5) {
			send(Type.CHOICE, DEFAULT_OPTION_TITLE, "I would like a boss task please.", "No thanks, I'd rather stick to normal combatants.");
			setPhase(7);
		} else if (getPhase() == 6) {
			SlayerTaskManagement.bossTask(player);
			send(Type.NPC, NPC_ID, Expression.DEFAULT, "I didn't think you'd actually take the task.", "Fine. Your task is to kill " + player.getSlayerTaskAmount(), "@blu@" + NPC.getName(player.getSlayerTask()) + "@bla@.", "Good luck " + player.getName() + ", you'll need it.");
			player.setFirstBossSlayerTask(false);
			setPhase(9);
		} else if (getPhase() == 7) {
			stop();
		} else if (getPhase() == 8) {
			send(Type.CHOICE, DEFAULT_OPTION_TITLE, "I'd like an assignment please.", "Do you have anything for trade?", "Nevermind.");
			setPhase(11);
		} else if (getPhase() == 9) {
			if (Slayer.hasTask(player)) {
				send(Type.NPC, NPC_ID, Expression.DEFAULT, "It seems you already have an assignment.", "You can reset it by talking to Nieve.");
				setPhase(9);
			} else {
				send(Type.NPC, NPC_ID, Expression.DEFAULT, "Fine. Your task is to kill " + player.getSlayerTaskAmount(), "@blu@" + NPC.getName(player.getSlayerTask()) + "s@bla@. Good luck " + player.getName() + ".");
				setPhase(9);
			}
		}
	}

	@Override
	protected void select(int index) {
		if (getPhase() == 1) {
			if (index == 1) {
				send(Type.PLAYER, Expression.DEFAULT, "Who are you?");
				setPhase(1);
			} else if (index == 2) {
				send(Type.PLAYER, Expression.ANGRY, "No need to be so rude...");
				player.getActionSender().sendMessage("Durael turns away with a look of disgust.");
				setPhase(9);
			} else if (index == 3) {
				send(Type.PLAYER, Expression.DEFAULT, "Nevermind.");
				setPhase(9);
			}
		} else {
			if (getPhase() == 7) {
				if (index == 1) {
					send(Type.PLAYER, Expression.DEFAULT, "I would like a boss slayer task.");
					setPhase(8);
				} else if (index == 2) {
					send(Type.PLAYER, Expression.DEFAULT, "No thanks, I'd rather stick to normal combatants.");
					setPhase(9);
				}
			} else {
				if (getPhase() == 11) {
					if (index == 1) {
						send(Type.PLAYER, Expression.DEFAULT, "I'd like an assignment please.");
						setPhase(12);
					} else if (index == 2) {
						send(Type.PLAYER, Expression.DEFAULT, "Do you have anything for trade?");
						setPhase(13);
					} else if (index == 3) {
						send(Type.PLAYER, Expression.DEFAULT, "Nevermind.");
						setPhase(9);
					}
				}
			}
		}
	}
}
package com.venenatis.game.model.entity.player.dialogue.impl.slayer;

import com.venenatis.game.content.skills.slayer.Slayer;
import com.venenatis.game.content.skills.slayer.SlayerMasters;
import com.venenatis.game.content.skills.slayer.SlayerTaskManagement;
import com.venenatis.game.model.entity.npc.NPC;
import com.venenatis.game.model.entity.player.dialogue.Dialogue;
import com.venenatis.game.model.entity.player.dialogue.Expression;
import com.venenatis.game.model.entity.player.dialogue.DialogueType;

/**
 * The dialogue enacted by Vannaka the medium slayer master (Medium Tasks)
 * 
 * @author Patrick van Elderen
 *
 */
public class VannakaDialogue extends Dialogue {
	
	private static final int NPC_ID = 403;
	
	@Override
	protected void start(Object... parameters) {
		if (player.getFirstSlayerTask()) {
			send(DialogueType.NPC, NPC_ID, Expression.DEFAULT, "Hmm... What do you want?");
			setPhase(0);
		} else {
			player.getActionSender().sendMessage("You cannot speak to Vannaka as you are yet to start the 'Slayer' skill.");
			player.getActionSender().sendMessage("Speak to @blu@Turael@bla@ who is located in Edgeville to do so.");
		}
	}
	
	@Override
	protected void next() {
		if (getPhase() == 0) {
			send(DialogueType.CHOICE, DEFAULT_OPTION_TITLE, "I need another assignment.", "Do you have anything for trade?", "Nevermind.");
			setPhase(1);
		} else {
			if (getPhase() == 2) {			
				if (Slayer.hasTask(player)) {
					send(DialogueType.NPC, NPC_ID, Expression.DEFAULT, "You already have an assignment. You can", "reset your task by talking to Nieve.");
					setPhase(3);
				} else if (Slayer.suitableMaster(player) == SlayerMasters.TURAEL || Slayer.suitableMaster(player) == SlayerMasters.MAZCHNA) {
					send(DialogueType.NPC, NPC_ID, Expression.DEFAULT, "You are not stroung enough to handle my assignments.", "Come back to me when you are a bit more experienced.");
					player.getActionSender().sendMessage("You need a combat level of 40 to get an assignment from Vannaka.");
					setPhase(3);
				} else if (Slayer.suitableMaster(player) == SlayerMasters.VANNAKA && !Slayer.hasTask(player)) {
					SlayerTaskManagement.vannakaTask(player);
					send(DialogueType.NPC, NPC_ID, Expression.DEFAULT, "Okay, your task is to kill " + player.getSlayerTaskAmount(), "@blu@ " + player.getSlayerTask() + "s@bla@. Good luck " + player.getUsername() + ".");
					player.getActionSender().sendString("<img=17><col=FFFFFF>Task: <col=00CC00>" + player.getSlayerTaskAmount() + " " + player.getSlayerTask(), 29172);
					setPhase(3);
				} else if (Slayer.suitableMaster(player) == SlayerMasters.CHAELDAR && !Slayer.hasTask(player)) {
					send(DialogueType.NPC, NPC_ID, Expression.DEFAULT, "Someone of your strength should go and see Chaeldar.", "Would you like to get an assignment from her?");
					setPhase(4);
				} else if (Slayer.suitableMaster(player) == SlayerMasters.NIEVE && !Slayer.hasTask(player)) {
					send(DialogueType.NPC, NPC_ID, Expression.DEFAULT, "Someone of your strength should go and see Nieve.", "Would you like to get a task from her?");
					setPhase(4);
				} else if (Slayer.suitableMaster(player) == SlayerMasters.DURADEL && !Slayer.hasTask(player)) {
					send(DialogueType.NPC, NPC_ID, Expression.DEFAULT, "Someone of your strength should go and see Duradel.", "Would you like to get a task from him?");
					setPhase(4);
				}
			} else {
				if (getPhase() == 3) {
					stop();
				} else {
					if (getPhase() == 4) {
						send(DialogueType.CHOICE, DEFAULT_OPTION_TITLE, "Yes, I'd like more of a challenge.", "No thanks, I'd like an assignment from you.");
						setPhase(5);
					} else {
						if (getPhase() == 6) {
							if (Slayer.suitableMaster(player) == SlayerMasters.CHAELDAR) {
								send(DialogueType.NPC, NPC_ID, Expression.DEFAULT, "Okay. Chaeldar can be found in Fairy Land.", "You can get there using the 'Teleport' option", "on an Enchanted gem.");
								setPhase(3);
							} else if (Slayer.suitableMaster(player) == SlayerMasters.NIEVE) {
								send(DialogueType.NPC, NPC_ID, Expression.DEFAULT, "Okay. Nieve can be found in Draynor.", "You can get there using the 'Teleport' option", "on an Enchanted gem.");
								setPhase(3);
							} else if (Slayer.suitableMaster(player) == SlayerMasters.DURADEL) {
								send(DialogueType.NPC, NPC_ID, Expression.DEFAULT, "Okay. Duradel can be found in Edgevile.", "You can get there using the 'Teleport' option", "on an Enchanted gem.");
								setPhase(3);
							}
						} else {
							if (getPhase() == 7) {
								SlayerTaskManagement.vannakaTask(player);
								send(DialogueType.NPC, NPC_ID, Expression.DEFAULT, "Okay, fine.", "Your task is to kill " + player.getSlayerTaskAmount(), "@blu@ " + player.getSlayerTask() + "s@bla@. Good luck " + player.getUsername() + ".");
								player.getActionSender().sendString("<img=17><col=FFFFFF>Task: <col=00CC00>" + player.getSlayerTaskAmount() + " " + player.getSlayerTask(), 29172);
								setPhase(3);
							} else {
								if (getPhase() == 8) {
									setPhase(3);
									player.getActionSender().sendMessage("The @blu@Rewards@bla@ store can be accessed by speaking to Nieve.");
								}
							}
						}
					}
				}
			}
		}
	}
	
	@Override
	protected void select(int index) {
		if (getPhase() == 1) {
			if (index == 1) {
				send(DialogueType.PLAYER, Expression.DEFAULT, "I need another assignment.");
				setPhase(2);
			} else if (index == 2) {
				send(DialogueType.PLAYER, Expression.DEFAULT, "Do you have anything for trade?");
				if (!player.getFirstSlayerTask()) {
					setPhase(3);
					player.getActionSender().sendMessage("You do not have access to the Slayer store as you have not started the 'Slayer' skill.");
					player.getActionSender().sendMessage("Talk to @blu@Turael@bla@ who is located in Edgevile.");
				} else {
					setPhase(8);
				}
			} else if (index == 3) {
				send(DialogueType.PLAYER, Expression.DEFAULT, "Nevermind.");
				setPhase(3);
			}
		} else {
			if (getPhase() == 5) {
				if (index == 1) {
					send(DialogueType.PLAYER, Expression.DEFAULT, "Yes, I'd like more of a challenge.");
					setPhase(6);
				} else if (index == 2) {
					send(DialogueType.PLAYER, Expression.DEFAULT, "No thanks, I'd like an assignment from you.");
					setPhase(7);
				}
			}
		}
	}
}
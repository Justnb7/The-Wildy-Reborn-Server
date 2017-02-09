package com.model.game.character.player.dialogue.impl.slayer;

import com.model.game.character.npc.Npc;
import com.model.game.character.player.dialogue.Dialogue;
import com.model.game.character.player.dialogue.Expression;
import com.model.game.character.player.dialogue.Type;
import com.model.game.character.player.packets.encode.impl.SendMessagePacket;
import com.model.game.character.player.skill.slayer.Slayer;
import com.model.game.character.player.skill.slayer.SlayerMasters;
import com.model.game.character.player.skill.slayer.SlayerTaskManagement;

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
			send(Type.NPC, NPC_ID, Expression.DEFAULT, "Hmm... What do you want?");
			setPhase(0);
		} else {
			player.write(new SendMessagePacket("You cannot speak to Vannaka as you are yet to start the 'Slayer' skill."));
			player.write(new SendMessagePacket("Speak to @blu@Turael@bla@ who is located in Edgeville to do so."));
		}
	}
	
	@Override
	protected void next() {
		if (getPhase() == 0) {
			send(Type.CHOICE, DEFAULT_OPTION_TITLE, "I need another assignment.", "Do you have anything for trade?", "Nevermind.");
			setPhase(1);
		} else {
			if (getPhase() == 2) {			
				if (Slayer.hasTask(player)) {
					send(Type.NPC, NPC_ID, Expression.DEFAULT, "You already have an assignment. You can", "reset your task by talking to Nieve.");
					setPhase(3);
				} else if (Slayer.suitableMaster(player) == SlayerMasters.TURAEL || Slayer.suitableMaster(player) == SlayerMasters.MAZCHNA) {
					send(Type.NPC, NPC_ID, Expression.DEFAULT, "You are not stroung enough to handle my assignments.", "Come back to me when you are a bit more experienced.");
					player.write(new SendMessagePacket("You need a combat level of 40 to get an assignment from Vannaka."));
					setPhase(3);
				} else if (Slayer.suitableMaster(player) == SlayerMasters.VANNAKA && !Slayer.hasTask(player)) {
					SlayerTaskManagement.mediumTask(player);
					send(Type.NPC, NPC_ID, Expression.DEFAULT, "Okay, your task is to kill " + player.getSlayerTaskAmount(), "@blu@ " + Npc.getName(player.getSlayerTask()) + "s@bla@. Good luck " + player.getName() + ".");
					setPhase(3);
				} else if (Slayer.suitableMaster(player) == SlayerMasters.CHAELDAR && !Slayer.hasTask(player)) {
					send(Type.NPC, NPC_ID, Expression.DEFAULT, "Someone of your strength should go and see Chaeldar.", "Would you like to get an assignment from her?");
					setPhase(4);
				} else if (Slayer.suitableMaster(player) == SlayerMasters.NIEVE && !Slayer.hasTask(player)) {
					send(Type.NPC, NPC_ID, Expression.DEFAULT, "Someone of your strength should go and see Nieve.", "Would you like to get a task from her?");
					setPhase(4);
				} else if (Slayer.suitableMaster(player) == SlayerMasters.DURADEL && !Slayer.hasTask(player)) {
					send(Type.NPC, NPC_ID, Expression.DEFAULT, "Someone of your strength should go and see Duradel.", "Would you like to get a task from him?");
					setPhase(4);
				}
			} else {
				if (getPhase() == 3) {
					stop();
				} else {
					if (getPhase() == 4) {
						send(Type.CHOICE, DEFAULT_OPTION_TITLE, "Yes, I'd like more of a challenge.", "No thanks, I'd like an assignment from you.");
						setPhase(5);
					} else {
						if (getPhase() == 6) {
							if (Slayer.suitableMaster(player) == SlayerMasters.CHAELDAR) {
								send(Type.NPC, NPC_ID, Expression.DEFAULT, "Okay. Chaeldar can be found in Fairy Land.", "You can get there using the 'Teleport' option", "on an Enchanted gem.");
								setPhase(3);
							} else if (Slayer.suitableMaster(player) == SlayerMasters.NIEVE) {
								send(Type.NPC, NPC_ID, Expression.DEFAULT, "Okay. Nieve can be found in Draynor.", "You can get there using the 'Teleport' option", "on an Enchanted gem.");
								setPhase(3);
							} else if (Slayer.suitableMaster(player) == SlayerMasters.DURADEL) {
								send(Type.NPC, NPC_ID, Expression.DEFAULT, "Okay. Duradel can be found in Burthorpe.", "You can get there using the 'Teleport' option", "on an Enchanted gem.");
								setPhase(3);
							}
						} else {
							if (getPhase() == 7) {
								SlayerTaskManagement.mediumTask(player);
								send(Type.NPC, NPC_ID, Expression.DEFAULT, "Okay, fine.", "Your task is to kill " + player.getSlayerTaskAmount(), "@blu@ " + Npc.getName(player.getSlayerTask()) + "s@bla@. Good luck " + player.getName() + ".");
								setPhase(3);
							} else {
								if (getPhase() == 8) {
									setPhase(3);
									player.write(new SendMessagePacket("The @blu@Rewards@bla@ store can be accessed by speaking to Nieve."));
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
				send(Type.PLAYER, Expression.DEFAULT, "I need another assignment.");
				setPhase(2);
			} else if (index == 2) {
				send(Type.PLAYER, Expression.DEFAULT, "Do you have anything for trade?");
				if (!player.getFirstSlayerTask()) {
					setPhase(3);
					player.write(new SendMessagePacket("You do not have access to the Slayer store as you have not started the 'Slayer' skill."));
					player.write(new SendMessagePacket("Talk to @blu@Turael@bla@ who is located in Edgeville to do."));
				} else {
					setPhase(8);
				}
			} else if (index == 3) {
				send(Type.PLAYER, Expression.DEFAULT, "Nevermind.");
				setPhase(3);
			}
		} else {
			if (getPhase() == 5) {
				if (index == 1) {
					send(Type.PLAYER, Expression.DEFAULT, "Yes, I'd like more of a challenge.");
					setPhase(6);
				} else if (index == 2) {
					send(Type.PLAYER, Expression.DEFAULT, "No thanks, I'd like an assignment from you.");
					setPhase(7);
				}
			}
		}
	}
}
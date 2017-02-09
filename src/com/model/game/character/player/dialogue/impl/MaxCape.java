package com.model.game.character.player.dialogue.impl;

import com.model.game.character.player.PlayerUpdating;
import com.model.game.character.player.Skills;
import com.model.game.character.player.dialogue.Dialogue;
import com.model.game.character.player.dialogue.Expression;
import com.model.game.character.player.dialogue.Type;

public class MaxCape extends Dialogue {
	
private static final int MYSTERIOUS_OLD_MAN = 6742;
	
	@Override
	protected void start(Object... parameters) {
		send(Type.NPC, MYSTERIOUS_OLD_MAN, Expression.DEFAULT, "Are you here to claim your veteran or completionist cape?");
		if (!player.hasClaimedMax()) {
			setPhase(0);
		} else {
			setPhase(4);
		}
	}
	
	@Override
	protected void next() {
		if (getPhase() == 0) {
			send(Type.CHOICE, "Select Option", "Veteran Cape.", "Completionist Cape.", "No, sir.");
			setPhase(1);
		} else {
			if (getPhase() == 2) {
				send(Type.NPC, MYSTERIOUS_OLD_MAN, Expression.DEFAULT, "@blu@To get the Completionist cape, you must", "@red@- Have 99 slayer", "@red@- Have a killcount of 1000", "@red@- Get a 50 killstreak");
				setPhase(3);
			} else {
				if (getPhase() == 3) {
					stop();
				} else {
					if (getPhase() == 4) {
						send(Type.NPC, MYSTERIOUS_OLD_MAN, Expression.DEFAULT, "You have already claimed your max cape.");
						setPhase(5);
					} else {
						if (getPhase() == 5) {
							send(Type.CHOICE, "Select Option", "I lost my cape.", "No, thanks.");
							setPhase(6);
						} else {
							if (getPhase() == 7) {
								send(Type.NPC, MYSTERIOUS_OLD_MAN, Expression.DEFAULT, "Here, a brand new cape and hood,", "do not lose it again.");
								setPhase(8);
							} else {
								if (getPhase() == 8) {
									player.getItems().addOrCreateGroundItem(13280, 1);
									player.getItems().addOrCreateGroundItem(13281, 1);
									send(Type.PLAYER, Expression.DEFAULT, "Thank you.");
									setPhase(9);
								} else {
									if (getPhase() == 9) {
										stop();
									}
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
			switch(index) {
			case 1:
				
				break;
			case 2:
				if (maxCapeRequirements()) {
					if (player.hasClaimedMax()) {
						PlayerUpdating.executeGlobalMessage("@red@" + player.getName() + " @yel@has just claimed the completionist cape for the first time!");
						player.getItems().addOrCreateGroundItem(13280, 1);
						player.getItems().addOrCreateGroundItem(13281, 1);
						stop();
					}
				} else {
					send(Type.NPC, MYSTERIOUS_OLD_MAN, Expression.DEFAULT, "You haven't passed all of the requirements!");
					setPhase(2);
				}
				break;
			case 3:
				break;
			}
		} else {
			if (getPhase() == 6) {
				switch(index) {
				case 1:
					send(Type.PLAYER, Expression.DEFAULT, "I lost my cape...");
					setPhase(7);
					break;
				case 2:
					stop();
					break;
				}
			}
		}
	}
	
	public boolean maxCapeRequirements() {
		if (!player.hasClaimedMax()) {
			if (player.getKillCount() >= 1000 && player.getSkills().getLevel(Skills.SLAYER) == 99 && player.getHighestKillStreak() >= 50) {
				player.setClaimedMaxCape(true);
				return true;
			}
		}
		return false;
	}

}

package com.venenatis.game.model.entity.player.dialogue.impl.slayer;

import com.venenatis.game.content.skills.slayer.Slayer;
import com.venenatis.game.model.entity.npc.NPC;
import com.venenatis.game.model.entity.player.dialogue.Dialogue;
import com.venenatis.game.model.entity.player.dialogue.Expression;
import com.venenatis.game.model.entity.player.dialogue.Type;

/**
 * The dialogue carried out by the Enchanted gem item.
 * 
 * @author Patrick van Elderen
 *
 */
public class EnchantedGem extends Dialogue {

	private static final int TURAEL = 401;
	private static final int MAZCHNA = 402;
	private static final int VANNAKA = 403;
	private static final int CHAELDAR = 404;
	private static final int NIEVE = 490;
	private static final int DURADEL = 405;
	
	@Override
	protected void start(Object... parameters) {
		send(Type.PLAYER, Expression.DEFAULT, "How do I turn this stupid thing o-");
		if (Slayer.hasTask(player)) {
			setPhase(0);
		} else {
			setPhase(20);
		}
	}
	
	@Override
	protected void next() {
		if (getPhase() == 0) {
			if (player.getSlayerTaskDifficulty() == 0) {
				send(Type.NPC, TURAEL, Expression.DEFAULT, "'Ello, and what are you after, then?");
				setPhase(1);
			} else if (player.getSlayerTaskDifficulty() == 1) {
				send(Type.NPC, MAZCHNA, Expression.DEFAULT, "Hello brave warrior. What would you like?");
				setPhase(5);
			} else if (player.getSlayerTaskDifficulty() == 2) {
				send(Type.NPC, VANNAKA, Expression.DEFAULT, "Hmm... What do you want?");
				setPhase(8);
			} else if (player.getSlayerTaskDifficulty() == 3) {
				send(Type.NPC, CHAELDAR, Expression.DEFAULT, "Hello human.", "What brings you around these parts?");
				setPhase(11);
			} else if (player.getSlayerTaskDifficulty() == 4) {
				send(Type.NPC, NIEVE, Expression.DEFAULT, "Hello there Adventurer.", "I am Nieve one of the more elite Slayer Masters.", "What do you need?");
				setPhase(14);
			} else if (player.getSlayerTaskDifficulty() == 5) {
				send(Type.NPC, DURADEL, Expression.DEFAULT, "What do you want?");
				setPhase(17);
			} else if (player.getSlayerTaskDifficulty() == -1) {
				send(Type.CHOICE, DEFAULT_OPTION_TITLE, "Teleport to a Slayer Master", "More options soon...");
				setPhase(20);
			}
		} else {
			if (getPhase() == 1) {
				send(Type.CHOICE, DEFAULT_OPTION_TITLE, "I have forgotten my assignment.", "Nevermind.");
				setPhase(2);
			} else {
				if (getPhase() == 3) {
					send(Type.NPC, TURAEL, Expression.DEFAULT, "Your task was to kill " + player.getSlayerTaskAmount() +  " @blu@" + NPC.getName(player.getSlayerTask()) + "s@bla@.");
					setPhase(4);
				} else {
					if (getPhase() == 4) {
						stop();
					} else {
						if (getPhase() == 5) {
							send(Type.CHOICE, DEFAULT_OPTION_TITLE, "I have forgotten my assignment.", "Nevermind.");
							setPhase(6);
						} else {
							if (getPhase() == 7) {
								send(Type.NPC, MAZCHNA, Expression.DEFAULT, "Your task is to kill " + player.getSlayerTaskAmount() + " @blu@" + NPC.getName(player.getSlayerTask()) + "s@bla@.");
								setPhase(4);
							} else {
								if (getPhase() == 8) {
									send(Type.CHOICE, DEFAULT_OPTION_TITLE, "I have forgotten my assignment.", "Nevermind.");
									setPhase(9);
								} else {
									if (getPhase() == 10) {
										send(Type.NPC, VANNAKA, Expression.DEFAULT, "Your task is to kill " + player.getSlayerTaskAmount() + " @blu@" + NPC.getName(player.getSlayerTask()) + "s@bla@.");
										setPhase(4);
									} else {
										if (getPhase() == 11) {
											send(Type.CHOICE, DEFAULT_OPTION_TITLE, "I have forgotten my assignment.", "Nevermind.");
											setPhase(12);
										} else {
											if (getPhase() == 13) {
												send(Type.NPC, CHAELDAR, Expression.DEFAULT, "Your task is to kill " + player.getSlayerTaskAmount() + " @blu@" + NPC.getName(player.getSlayerTask()) + "s@bla@.");
												setPhase(4);
											} else {
												if (getPhase() == 14) {
													send(Type.CHOICE, DEFAULT_OPTION_TITLE, "I have forgotten my assignment.", "Nevermind.");
													setPhase(15);
												} else {
													if (getPhase() == 16) {
														send(Type.NPC, NIEVE, Expression.DEFAULT, "Your task is to kill " + player.getSlayerTaskAmount() + " @blu@" + NPC.getName(player.getSlayerTask()) + "s@bla@.");
														setPhase(4);
													} else {
														if (getPhase() == 17) {
															send(Type.CHOICE, DEFAULT_OPTION_TITLE, "I have forgotten my assignment.", "Nevermind.");
															setPhase(18);
														} else {
															if (getPhase() == 19) {
																send(Type.NPC, DURADEL, Expression.DEFAULT, "Your task is to kill " + player.getSlayerTaskAmount() + " @blu@" + NPC.getName(player.getSlayerTask()) + "s@bla@.");
																setPhase(4);
															} else {
																if (getPhase() == 20) {
																	send(Type.CHOICE, DEFAULT_OPTION_TITLE, "Teleport me to a Slayer Master", "Nevermind.");
																	setPhase(21);
																}
															}
														}
													}
												}
											}
										}
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
		if (getPhase() == 2) {
			if (index == 1) {
				send(Type.PLAYER, Expression.DEFAULT, "I have forgotten my assignment.");
				setPhase(3);
			} else if (index == 2) {
				send(Type.PLAYER, Expression.DEFAULT, "Nevermind.");
				setPhase(4);
			}
		} else {
			if (getPhase() == 6) {
				if (index == 1) {
					send(Type.PLAYER, Expression.DEFAULT, "I have forgotten my assignment.");
					setPhase(7);
				} else if (index == 2) {
					send(Type.PLAYER, Expression.DEFAULT, "Nevermind.");
					setPhase(4);
				}
			} else {
				if (getPhase() == 9) {
					if (index == 1) {
						send(Type.PLAYER, Expression.DEFAULT, "I have forgotten my assignment.");
						setPhase(10);
					} else if (index == 2) {
						send(Type.PLAYER, Expression.DEFAULT, "Nevermind.");
						setPhase(4);
					}
				} else {
					if (getPhase() == 12) {
						if (index == 1) {
							send(Type.PLAYER, Expression.DEFAULT, "I have forgotten my assignment.");
							setPhase(13);
						} else if (index == 2) {
							send(Type.PLAYER, Expression.DEFAULT, "Nevermind.");
							setPhase(4);
						}
					} else {
						if (getPhase() == 15) {
							if (index == 1) {
								send(Type.PLAYER, Expression.DEFAULT, "I have forgotten my assignment.");
								setPhase(16);
							} else if (index == 2) {
								send(Type.PLAYER, Expression.DEFAULT, "Nevermind.");
								setPhase(4);
							}
						} else {
							if (getPhase() == 18) {
								if (index == 1) {
									send(Type.PLAYER, Expression.DEFAULT, "I have forgotten my assignment.");
									setPhase(19);
								} else if (index == 2) {
									send(Type.PLAYER, Expression.DEFAULT, "Nevermind.");
									setPhase(4);
								}
							} else {
								if (getPhase() == 21) {
									if (index == 1) {
										player.dialogue().start("ENCHANTED_GEM_TELEPORT", player);
									} else if (index == 2) {
										send(Type.PLAYER, Expression.DEFAULT, "Nevermind.");
										setPhase(4);
									}
								}
							}
						}
					}
				}
			}
		}	
	}
}
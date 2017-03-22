package com.model.game.character.player.dialogue.impl.minigames.fight_caves;

import com.model.game.World;
import com.model.game.character.npc.pet.Pet;
import com.model.game.character.player.dialogue.Dialogue;
import com.model.game.character.player.dialogue.Expression;
import com.model.game.character.player.dialogue.Type;
import com.model.game.item.Item;
import com.model.utility.Utility;

public class Tzhaar_Mej_Jal extends Dialogue {
	
	private static int TZHAAR_MEJ_JAL = 2180;
	
	private boolean receivedPet = false;
	
	@Override
	protected void start(Object... parameters) {
		if (player.getItems().playerHasItem(6570) && player.secondOption) {
			send(Type.PLAYER, Expression.DEFAULT, "I have a fire cape here.");
			setPhase(36);
		} else {
			send(Type.NPC, TZHAAR_MEJ_JAL, Expression.DEFAULT, "You want help JalYt-Mej-"+player.getName()+"?");
			setPhase(0);
		}
	}
	
	@Override
	protected void next() {
		if (getPhase() == 0) {
			send(Type.CHOICE, "Select an Option", "What is this place?", "What did you call me?", "No i'm fine thanks.");
			setPhase(1);
		} else {
			if (getPhase() == 2) {
				send(Type.NPC, TZHAAR_MEJ_JAL, Expression.DEFAULT, "This is the fight cave, Tzhaar-Xil made it for practise,", "but many JalYt come here to fight too.", "Just enter the cave and make sure you're prepared.");
				setPhase(3);
			} else {
				if (getPhase() == 3) {
					send(Type.CHOICE, "Select an Option", "Are there any rules?", "Ok thanks.");
					setPhase(4);
				} else {
					if (getPhase() == 5) {
						send(Type.NPC, TZHAAR_MEJ_JAL, Expression.DEFAULT, "Rules? Survival is the only rule in there.");
						setPhase(7);
					} else {
						if (getPhase() == 6) {
							stop();
						} else {
							if (getPhase() == 7) {
								send(Type.CHOICE, "Select an Option", "Do I win anything?", "Sounds good.");
								setPhase(29);
							} else {
								if (getPhase() == 8) {
									stop();
								} else {
									if (getPhase() == 9) {
										send(Type.NPC, TZHAAR_MEJ_JAL, Expression.DEFAULT, "Are you not JalYt-Mej?");
										setPhase(10);
									} else {
										if (getPhase() == 10) {
										send(Type.CHOICE, "Select an Option", "What's a 'JalYt-Mej?", "I guess so...", "No I'm not!");
										setPhase(11);
										} else {
											if (getPhase() == 12) {
												send(Type.NPC, TZHAAR_MEJ_JAL, Expression.DEFAULT, "That what you are... you user of mystic powers no?");
												setPhase(13);
											} else {
												if (getPhase() == 13) {
													send(Type.PLAYER, Expression.DEFAULT, "Well yes I suppose I am...");
													setPhase(14);
												} else {
													if (getPhase() == 14) {
														send(Type.NPC, TZHAAR_MEJ_JAL, Expression.DEFAULT, "Then you JalYt-Mej!");
														setPhase(15);
													} else {
														if (getPhase() == 15) {
															send(Type.CHOICE, "Select an Option", "What's a 'What are you then?", "Thanks for explaining it.");
															setPhase(16);
														} else {
															if (getPhase() == 17) {
																send(Type.NPC, TZHAAR_MEJ_JAL, Expression.DEFAULT, "Foolish JalYt, I am TzHaar-Mej, one of the mystics of", "this city.");
																setPhase(19);
															} else {
																if (getPhase() == 18) {
																	stop();
																} else {
																	if (getPhase() == 19) {
																		send(Type.CHOICE, "Select an Option", "What other types are there?", "Ah ok then.");
																		setPhase(20);
																	} else {
																		if (getPhase() == 21) {
																			send(Type.NPC, TZHAAR_MEJ_JAL, Expression.DEFAULT, "There are the mighty TzHaar-Ket who guard us, the", "swift TzHaar-Xil who hunt for our food. and the skilled", "TzHaar-Hur who craft our homes and tools.");
																			setPhase(23);
																		} else {
																			if (getPhase() == 22) {
																				stop();
																			} else {
																				if (getPhase() == 23) {
																					stop();
																				} else {
																					if (getPhase() == 24) {
																						send(Type.NPC, TZHAAR_MEJ_JAL, Expression.DEFAULT, "Well then, no problems.");
																						setPhase(28);
																					} else {
																						if (getPhase() == 25) {
																							send(Type.PLAYER, Expression.DEFAULT, "No I'm not!");
																							setPhase(26);
																						} else {
																							if (getPhase() == 26) {
																								send(Type.NPC, TZHAAR_MEJ_JAL, Expression.DEFAULT, "What ever you say, crazy JalYt!");
																								setPhase(27);
																							} else {
																								if (getPhase() == 27) {
																									stop();
																								} else {
																									if (getPhase() == 28) {
																										stop();
																									} else {
																										if (getPhase() == 30) {
																											send(Type.NPC, TZHAAR_MEJ_JAL, Expression.DEFAULT, "You ask a lot of questions.", "Might give you TokKul if you last long enough.");
																											setPhase(32);
																										} else {
																											if (getPhase() == 31) {
																												stop();
																											} else {
																												if (getPhase() == 32) {
																													send(Type.PLAYER, Expression.DEFAULT, "...");
																													setPhase(33);
																												} else {
																													if (getPhase() == 33) {
																														send(Type.NPC, TZHAAR_MEJ_JAL, Expression.DEFAULT, "Before you ask, TokKul is like your Coins.");
																														setPhase(34);
																													} else {
																														if (getPhase() == 34) {
																															send(Type.NPC, TZHAAR_MEJ_JAL, Expression.DEFAULT, "Gold is like you JalYt, soft and easily broken, we use", "hard rock forged in fire like TzHaar!");
																															setPhase(35);
																														} else {
																															if (getPhase() == 35) {
																																stop();
																															} else {
																																if (getPhase() == 36) {
																																	send(Type.CHOICE, "Sell your fire cape?", "Yes, sell it for 8,000 TokKul.", "No, keep it.", "Bargain for TzRek-Jad.");
																																	setPhase(37);
																																	} else {
																																		if (getPhase() == 39) {
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
								}
							}
						}
					}
				}
			}
		}
	}
	
	@Override
	public void select(int index) {
		if (getPhase() == 1) {
			switch (index) {
			case 1:
				send(Type.PLAYER, Expression.DEFAULT, "What is this place?");
				setPhase(2);
				break;
			case 2:
				send(Type.PLAYER, Expression.DEFAULT, "What did you call me?");
				setPhase(9);
				break;
			case 3:
				send(Type.PLAYER, Expression.DEFAULT, "No i'm fine thanks.");
				setPhase(8);
				break;
			}
		} else {
			if (getPhase() == 4) {
				switch (index) {
				case 1:
					send(Type.PLAYER, Expression.DEFAULT, "Are there any rules?");
					setPhase(5);
					break;
				case 2:
					send(Type.PLAYER, Expression.DEFAULT, "Ok thanks.");
					setPhase(6);
					break;
				}
			} else {
				if (getPhase() == 11) {
					switch (index) {
					case 1:
						send(Type.PLAYER, Expression.DEFAULT, "What's a 'JalYt-Mej?");
						setPhase(12);
						break;
					case 2:
						send(Type.PLAYER, Expression.DEFAULT, "I guess so...");
						setPhase(24);
						break;
					case 3:
						send(Type.PLAYER, Expression.DEFAULT, "No I'm not!");
						setPhase(25);
						break;
					}
				} else {
					if (getPhase() == 16) {
						switch (index) {
						case 1:
							send(Type.PLAYER, Expression.DEFAULT, "What are you then?");
							setPhase(17);
							break;
						case 2:
							send(Type.PLAYER, Expression.DEFAULT, "Thanks for explaining it.");
							setPhase(18);
							break;
						}
					} else {
						if (getPhase() == 20) {
							switch (index) {
							case 1:
								send(Type.PLAYER, Expression.DEFAULT, "What other types are there?");
								setPhase(21);
								break;
							case 2:
								send(Type.PLAYER, Expression.DEFAULT, "Ah ok then.");
								setPhase(22);
								break;
							}
						} else {
							if (getPhase() == 29) {
								switch (index) {
								case 1:
									send(Type.PLAYER, Expression.DEFAULT, "Do I win anything?");
									setPhase(30);
									break;
								case 2:
									send(Type.PLAYER, Expression.DEFAULT, "Sounds good.");
									setPhase(31);
									break;
								}
							} else {
								if (getPhase() == 37) {
									switch (index) {
									case 1:
										player.getItems().deleteItem(6570);
										player.getItems().addOrCreateGroundItem(new Item(6529, 8000));
										player.getActionSender().sendRemoveInterfacePacket();
										break;
									case 2:
										send(Type.PLAYER, Expression.DEFAULT, "No, I'd like to keep my cape");
										setPhase(39);
										break;
									case 3:
										send(Type.CHOICE, "Sacrifice your firecape for a chance at TzRek-Jad?", "Yes, I know I won't get my cape back.", "No, I like my cape!");
										setPhase(38);
										break;
									}
								} else {
									if (getPhase() == 38) {
										switch (index) {
										case 1:
											if (player.getItems().alreadyHasItem(13225)) {
												player.getActionSender().sendMessage("You already have a pet jad.");
												player.getActionSender().sendRemoveInterfacePacket();
												return;
											}
											player.getItems().deleteItem(6570);
											int roll = Utility.getRandom(25);
											if (roll == 1 || player.getRights().isAdministrator()) {
												receivedPet = true;
											}
											
											if (receivedPet) {
												send(Type.NPC, TZHAAR_MEJ_JAL, Expression.DEFAULT, "You lucky. Better train him good else TzTok-Jad find", "you JalYt.");
												if (player.isPetSpawned()) {
													player.getItems().addOrSendToBank(13225, 1);
													World.getWorld().sendWorldMessage("<col=7f00ff>" + player.getName() + " has just received 1x Tzrek Jad.", false);
												} else {
													Pet.Pets pets = Pet.Pets.TZREK_JAD;
													Pet pet = new Pet(player, pets.getNpc());
													player.setPetSpawned(true);
													player.setPet(pets.getNpc());
													World.getWorld().register(pet);
													World.getWorld().sendWorldMessage("<col=7f00ff>" + player.getName() + " has just received 1x Tzrek Jad.", false);
												}
												player.getActionSender().sendMessage("You have a funny feeling like you're being followed.");
												setPhase(39);
											} else {
												send(Type.NPC, TZHAAR_MEJ_JAL, Expression.DEFAULT, "You not lucky. Maybe next time, JalYt.");
												setPhase(39);
											}
											break;
										case 2:
											send(Type.PLAYER, Expression.DEFAULT, "No, I like my cape!");
											setPhase(39);
											break;
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
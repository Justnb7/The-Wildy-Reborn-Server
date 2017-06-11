package com.model.game.character.pathfinder.impl;

import com.model.game.character.Entity;
import com.model.game.character.combat.combat_data.CombatStyle;
import com.model.game.character.npc.NPC;
import com.model.game.character.pathfinder.Directions;
import com.model.game.character.pathfinder.region.Coverage;
import com.model.game.character.player.Player;
import com.model.game.location.Location;
import com.model.utility.cache.map.Tile;
public class NPCPathFinder {
	
	public static boolean execute_path(Entity mob, Entity partner, boolean combat) {
		try {
			mob.setFollowingMob(partner); // TODO
			Tile pos = mob.getPosition();
			int dx = pos.getX() - partner.getPosition().getX(); 
			int dy = pos.getY() - partner.getPosition().getY();
			int distance = mob.getCoverage().center().distanceToPoint(
					partner.getCoverage().center());
			boolean successful = false;
			int x = mob.getPosition().getX();
			int y = mob.getPosition().getY();
			int counter = 1;
			if (mob.isPlayer()) {
				Player player = (Player) mob;
				if (partner.isPlayer()) {
					counter = (player.moving() || partner.moving()) ? 2 : 1;
				} else {
					counter = player.moving() ? 2 : 1;
				}
			} else {
				counter = 1;
			}
			for (int i = 0; i < counter; i++) {
				successful = false;
				pos = Tile.create(x, y, mob.getPosition().getZ());
				NextNode next = getNextNode(pos, dx, dy, distance, combat, mob, partner);
				if (next == null) {
					break;
				}
				if (next.tile == null) {
					break;
				}
				if (next.canMove) {
					if (partner.getCoverage().within(next.tile) && !Tile.standingOn(mob, partner)) {
						successful = true;
						continue;
					}
					x = next.tile.getX();
					y = next.tile.getY();
					dx = x - partner.getPosition().getX();
					dy = y - partner.getPosition().getY();
					successful = true;
					mob.updateCoverage(next.tile);
					if (mob.isPlayer()) {
						((Player)mob).getMovementHandler().addToPath(Location.create(next.tile.getX(), next.tile.getY()));
						((Player)mob).getMovementHandler().finish();
					} else {
						((NPC)mob).moveX = next.tile.getX();
						((NPC)mob).moveY = next.tile.getY();
						((NPC)mob).getNextNPCMovement(((NPC)mob));
					}
					// hyperion
					//mob.getWalkingQueue().addStep(next.tile.getX(), next.tile.getY());
					//mob.getWalkingQueue().finish();
				} else {
					// TODO handle being stucked!
					break;
				}
			}
			return successful;
		} catch (Exception failed) {
			failed.printStackTrace();
			return false;
		}
	}
	
	protected static NextNode getNextNode(Tile loc, int dx, int dy, int distance, boolean combat, Entity mob, Entity partner) {
        Directions.NormalDirection direction = null;
        boolean npcCheck = (mob.isNPC());
        if (combat) {
        	if (mob.getCoverage().correctCombatPosition(mob, partner, partner.getCoverage(), 1, CombatStyle.MELEE)) {
        		return null;
        	}
        } else {
        	if (mob.getCoverage().correctFinalFollowPosition(partner.getCoverage())) {
        		return null;
        	}
        }
        if (mob.size() > 1) {
			Tile eCenter = mob.getCoverage().center();
			Tile pCenter = partner.getCoverage().center();
        	if (mob.getCoverage().intersect(partner.getCoverage())) {
        		if (eCenter == pCenter) {
        			if (Tile.canMove(mob, Directions.NormalDirection.SOUTH_WEST, mob.size(), npcCheck)) {
    					direction = Directions.NormalDirection.SOUTH_WEST;
    				} else if (Tile.canMove(mob, Directions.NormalDirection.WEST, mob.size(), npcCheck)) {
    					direction = Directions.NormalDirection.WEST;
    				} else if (Tile.canMove(mob, Directions.NormalDirection.SOUTH, mob.size(), npcCheck)) {
    					direction = Directions.NormalDirection.SOUTH;
    				} else if (Tile.canMove(mob, Directions.NormalDirection.NORTH_WEST, mob.size(), npcCheck)) {
    					direction = Directions.NormalDirection.NORTH_WEST;
    				} else if (Tile.canMove(mob, Directions.NormalDirection.NORTH_EAST, mob.size(), npcCheck)) {
    					direction = Directions.NormalDirection.NORTH_EAST;
    				} else if (Tile.canMove(mob, Directions.NormalDirection.SOUTH_EAST, mob.size(), npcCheck)) {
    					direction = Directions.NormalDirection.SOUTH_EAST;
    				} else if (Tile.canMove(mob, Directions.NormalDirection.EAST, mob.size(), npcCheck)) {
    					direction = Directions.NormalDirection.EAST;
    				} else if (Tile.canMove(mob, Directions.NormalDirection.NORTH, mob.size(), npcCheck)) {
    					direction = Directions.NormalDirection.NORTH;
    				} 
        		} else if (eCenter.right(pCenter)) {
        			if (eCenter.above(pCenter)) {
        				if (Tile.canMove(mob, Directions.NormalDirection.NORTH_EAST, mob.size(), npcCheck)) {
        					direction = Directions.NormalDirection.NORTH_EAST;
        				} else if (Tile.canMove(mob, Directions.NormalDirection.EAST, mob.size(), npcCheck)) {
        					direction = Directions.NormalDirection.EAST;
        				} else if (Tile.canMove(mob, Directions.NormalDirection.NORTH, mob.size(), npcCheck)) {
        					direction = Directions.NormalDirection.NORTH;
        				}
        			} else if (pCenter.under(pCenter)) {
        				if (Tile.canMove(mob, Directions.NormalDirection.SOUTH_EAST, mob.size(), npcCheck)) {
        					direction = Directions.NormalDirection.SOUTH_EAST;
        				} else if (Tile.canMove(mob, Directions.NormalDirection.EAST, mob.size(), npcCheck)) {
        					direction = Directions.NormalDirection.EAST;
        				} else if (Tile.canMove(mob, Directions.NormalDirection.SOUTH, mob.size(), npcCheck)) {
        					direction = Directions.NormalDirection.SOUTH;
        				}
        			} else {
        				if (Tile.canMove(mob, Directions.NormalDirection.EAST, mob.size(), npcCheck)) {
        					direction = Directions.NormalDirection.EAST;
        				} else if (Tile.canMove(mob, Directions.NormalDirection.NORTH_EAST, mob.size(), npcCheck)) {
        					direction = Directions.NormalDirection.NORTH_EAST;
        				} else if (Tile.canMove(mob, Directions.NormalDirection.SOUTH_EAST, mob.size(), npcCheck)) {
        					direction = Directions.NormalDirection.SOUTH_EAST;
        				} else if (Tile.canMove(mob, Directions.NormalDirection.NORTH, mob.size(), npcCheck)) {
        					direction = Directions.NormalDirection.NORTH;
        				} else if (Tile.canMove(mob, Directions.NormalDirection.SOUTH, mob.size(), npcCheck)) {
        					direction = Directions.NormalDirection.SOUTH;
        				}
        			}
        		} else if (eCenter.left(pCenter)) {
        			if (eCenter.above(pCenter)) {
        				if (Tile.canMove(mob, Directions.NormalDirection.NORTH_WEST, mob.size(), npcCheck)) {
        					direction = Directions.NormalDirection.NORTH_WEST;
        				} else if (Tile.canMove(mob, Directions.NormalDirection.WEST, mob.size(), npcCheck)) {
        					direction = Directions.NormalDirection.WEST;
        				} else if (Tile.canMove(mob, Directions.NormalDirection.NORTH, mob.size(), npcCheck)) {
        					direction = Directions.NormalDirection.NORTH;
        				}
        			} else if (pCenter.under(pCenter)) {
        				if (Tile.canMove(mob, Directions.NormalDirection.SOUTH_WEST, mob.size(), npcCheck)) {
        					direction = Directions.NormalDirection.SOUTH_WEST;
        				} else if (Tile.canMove(mob, Directions.NormalDirection.WEST, mob.size(), npcCheck)) {
        					direction = Directions.NormalDirection.WEST;
        				} else if (Tile.canMove(mob, Directions.NormalDirection.SOUTH, mob.size(), npcCheck)) {
        					direction = Directions.NormalDirection.SOUTH;
        				}
        			} else {
        				if (Tile.canMove(mob, Directions.NormalDirection.WEST, mob.size(), npcCheck)) {
        					direction = Directions.NormalDirection.WEST;
        				} else if (Tile.canMove(mob, Directions.NormalDirection.NORTH_WEST, mob.size(), npcCheck)) {
        					direction = Directions.NormalDirection.NORTH_WEST;
        				} else if (Tile.canMove(mob, Directions.NormalDirection.SOUTH_WEST, mob.size(), npcCheck)) {
        					direction = Directions.NormalDirection.SOUTH_WEST;
        				} else if (Tile.canMove(mob, Directions.NormalDirection.NORTH, mob.size(), npcCheck)) {
        					direction = Directions.NormalDirection.NORTH;
        				} else if (Tile.canMove(mob, Directions.NormalDirection.SOUTH, mob.size(), npcCheck)) {
        					direction = Directions.NormalDirection.SOUTH;
        				}
        			}
        		} else {
        			if (eCenter.above(pCenter)) {
        				if (Tile.canMove(mob, Directions.NormalDirection.NORTH, mob.size(), npcCheck)) {
        					direction = Directions.NormalDirection.NORTH;
        				} else if (Tile.canMove(mob, Directions.NormalDirection.NORTH_EAST, mob.size(), npcCheck)) {
        					direction = Directions.NormalDirection.NORTH_EAST;
        				} else if (Tile.canMove(mob, Directions.NormalDirection.NORTH_WEST, mob.size(), npcCheck)) {
        					direction = Directions.NormalDirection.NORTH_WEST;
        				} else if (Tile.canMove(mob, Directions.NormalDirection.EAST, mob.size(), npcCheck)) {
        					direction = Directions.NormalDirection.EAST;
        				} else if (Tile.canMove(mob, Directions.NormalDirection.WEST, mob.size(), npcCheck)) {
        					direction = Directions.NormalDirection.WEST;
        				}
        			} else if (eCenter.under(pCenter)) {
        				if (Tile.canMove(mob, Directions.NormalDirection.SOUTH, mob.size(), npcCheck)) {
        					direction = Directions.NormalDirection.SOUTH;
        				} else if (Tile.canMove(mob, Directions.NormalDirection.SOUTH_EAST, mob.size(), npcCheck)) {
        					direction = Directions.NormalDirection.SOUTH_EAST;
        				} else if (Tile.canMove(mob, Directions.NormalDirection.SOUTH_WEST, mob.size(), npcCheck)) {
        					direction = Directions.NormalDirection.SOUTH_WEST;
        				} else if (Tile.canMove(mob, Directions.NormalDirection.EAST, mob.size(), npcCheck)) {
        					direction = Directions.NormalDirection.EAST;
        				} else if (Tile.canMove(mob, Directions.NormalDirection.WEST, mob.size(), npcCheck)) {
        					direction = Directions.NormalDirection.WEST;
        				}
        			}
        		}
        	} else {
        		Coverage eC = mob.getCoverage();
        		Coverage pC = partner.getCoverage();
        		int absDX = Math.abs(dx);
        		int absDY = Math.abs(dy);
        		if (eC.right(pC)) {
        			if (eC.above(pC)) {
        				if (Tile.canMove(mob, Directions.NormalDirection.SOUTH_WEST, mob.size(), npcCheck)) {
        					if (absDY <= 1 && absDY >= 0) {
        						if (Tile.canMove(mob, Directions.NormalDirection.WEST, mob.size(), npcCheck)) {
                					direction = Directions.NormalDirection.WEST;
        						}
        					} else if (absDX <= 1 && absDX >= 0) {
        						if (Tile.canMove(mob, Directions.NormalDirection.SOUTH, mob.size(), npcCheck)) {
                					direction = Directions.NormalDirection.SOUTH;
        						}
        					} else {
        						direction = Directions.NormalDirection.SOUTH_WEST;
        					}
        				} else {
        					if (dx > dy) {
        						if (Tile.canMove(mob, Directions.NormalDirection.WEST, mob.size(), npcCheck)) {
                					direction = Directions.NormalDirection.WEST;
        						} else if (Tile.canMove(mob, Directions.NormalDirection.SOUTH, mob.size(), npcCheck)) {
                					direction = Directions.NormalDirection.SOUTH;
        						}
        					} else if (dx < dy) {
        						if (Tile.canMove(mob, Directions.NormalDirection.SOUTH, mob.size(), npcCheck)) {
                					direction = Directions.NormalDirection.SOUTH;
        						} else if (Tile.canMove(mob, Directions.NormalDirection.WEST, mob.size(), npcCheck)) {
                					direction = Directions.NormalDirection.WEST;
        						}
        					} else {
        						if (Tile.canMove(mob, Directions.NormalDirection.SOUTH, mob.size(), npcCheck)) {
                					direction = Directions.NormalDirection.SOUTH;
        						} else if (Tile.canMove(mob, Directions.NormalDirection.WEST, mob.size(), npcCheck)) {
                					direction = Directions.NormalDirection.WEST;
        						}
        					}
        				}
        			} else if (eC.under(pC)) {
        				if (Tile.canMove(mob, Directions.NormalDirection.NORTH_WEST, mob.size(), npcCheck)) {
        					if (absDY <= 1 && absDY >= 0) {
        						if (Tile.canMove(mob, Directions.NormalDirection.WEST, mob.size(), npcCheck)) {
                					direction = Directions.NormalDirection.WEST;
        						}
        					} else if (absDX <= 1 && absDX >= 0) {
        						if (Tile.canMove(mob, Directions.NormalDirection.NORTH, mob.size(), npcCheck)) {
                					direction = Directions.NormalDirection.NORTH;
        						}
        					} else {
        						direction = Directions.NormalDirection.NORTH_WEST;
        					}
        				} else {
        					if (dx > -dy) {
        						if (Tile.canMove(mob, Directions.NormalDirection.WEST, mob.size(), npcCheck)) {
                					direction = Directions.NormalDirection.WEST;
        						} else if (Tile.canMove(mob, Directions.NormalDirection.NORTH, mob.size(), npcCheck)) {
                					direction = Directions.NormalDirection.NORTH;
        						}
        					} else if (dx < -dy) {
        						if (Tile.canMove(mob, Directions.NormalDirection.NORTH, mob.size(), npcCheck)) {
                					direction = Directions.NormalDirection.NORTH;
        						} else if (Tile.canMove(mob, Directions.NormalDirection.WEST, mob.size(), npcCheck)) {
                					direction = Directions.NormalDirection.WEST;
        						}
        					} else {
        						if (Tile.canMove(mob, Directions.NormalDirection.NORTH, mob.size(), npcCheck)) {
                					direction = Directions.NormalDirection.NORTH;
        						} else if (Tile.canMove(mob, Directions.NormalDirection.WEST, mob.size(), npcCheck)) {
                					direction = Directions.NormalDirection.WEST;
        						}
        					}
        				}
        			} else {
        				if (Tile.canMove(mob, Directions.NormalDirection.WEST, mob.size(), npcCheck)) {
        					direction = Directions.NormalDirection.WEST;
        				}
        			}
        		} else if (eC.left(pC)) {
        			if (eC.above(pC)) {
        				if (Tile.canMove(mob, Directions.NormalDirection.SOUTH_EAST, mob.size(), npcCheck)) {
        					if (absDY <= 1 && absDY >= 0) {
        						if (Tile.canMove(mob, Directions.NormalDirection.EAST, mob.size(), npcCheck)) {
                					direction = Directions.NormalDirection.EAST;
        						}
        					} else if (absDX <= 1 && absDX >= 0) {
        						if (Tile.canMove(mob, Directions.NormalDirection.SOUTH, mob.size(), npcCheck)) {
                					direction = Directions.NormalDirection.SOUTH;
        						}
        					} else {
        						direction = Directions.NormalDirection.SOUTH_EAST;
        					}
        				} else {
        					if (-dx > dy) {
        						if (Tile.canMove(mob, Directions.NormalDirection.EAST, mob.size(), npcCheck)) {
                					direction = Directions.NormalDirection.EAST;
        						} else if (Tile.canMove(mob, Directions.NormalDirection.SOUTH, mob.size(), npcCheck)) {
                					direction = Directions.NormalDirection.SOUTH;
        						}
        					} else if (-dx < dy) {
        						if (Tile.canMove(mob, Directions.NormalDirection.SOUTH, mob.size(), npcCheck)) {
                					direction = Directions.NormalDirection.SOUTH;
        						} else if (Tile.canMove(mob, Directions.NormalDirection.EAST, mob.size(), npcCheck)) {
                					direction = Directions.NormalDirection.EAST;
        						}
        					} else {
        						if (Tile.canMove(mob, Directions.NormalDirection.SOUTH, mob.size(), npcCheck)) {
                					direction = Directions.NormalDirection.SOUTH;
        						} else if (Tile.canMove(mob, Directions.NormalDirection.EAST, mob.size(), npcCheck)) {
                					direction = Directions.NormalDirection.EAST;
        						}
        					}
        				}
        			} else if (eC.under(pC)) {
        				if (Tile.canMove(mob, Directions.NormalDirection.NORTH_EAST, mob.size(), npcCheck)) {
        					if (absDY <= 1 && absDY >= 0) {
        						if (Tile.canMove(mob, Directions.NormalDirection.EAST, mob.size(), npcCheck)) {
                					direction = Directions.NormalDirection.EAST;
        						}
        					} else if (absDX <= 1 && absDX >= 0) {
        						if (Tile.canMove(mob, Directions.NormalDirection.NORTH, mob.size(), npcCheck)) {
                					direction = Directions.NormalDirection.NORTH;
        						}
        					} else {
        						direction = Directions.NormalDirection.NORTH_EAST;
        					}
        				} else {
        					if (-dx > -dy) {
        						if (Tile.canMove(mob, Directions.NormalDirection.EAST, mob.size(), npcCheck)) {
                					direction = Directions.NormalDirection.EAST;
        						} else if (Tile.canMove(mob, Directions.NormalDirection.NORTH, mob.size(), npcCheck)) {
                					direction = Directions.NormalDirection.NORTH;
        						}
        					} else if (-dx < -dy) {
        						if (Tile.canMove(mob, Directions.NormalDirection.NORTH, mob.size(), npcCheck)) {
                					direction = Directions.NormalDirection.NORTH;
        						} else if (Tile.canMove(mob, Directions.NormalDirection.EAST, mob.size(), npcCheck)) {
                					direction = Directions.NormalDirection.EAST;
        						}
        					} else {
        						if (Tile.canMove(mob, Directions.NormalDirection.NORTH, mob.size(), npcCheck)) {
                					direction = Directions.NormalDirection.NORTH;
        						} else if (Tile.canMove(mob, Directions.NormalDirection.EAST, mob.size(), npcCheck)) {
                					direction = Directions.NormalDirection.EAST;
        						}
        					}
        				}
        			} else {
        				if (Tile.canMove(mob, Directions.NormalDirection.EAST, mob.size(), npcCheck)) {
        					direction = Directions.NormalDirection.EAST;
        				}
        			}
        		} else {
        			if (eC.above(pC)) {
        				if (Tile.canMove(mob, Directions.NormalDirection.SOUTH, mob.size(), npcCheck)) {
        					direction = Directions.NormalDirection.SOUTH;
        				}
        			} else if (eC.under(pC)) {
        				if (Tile.canMove(mob, Directions.NormalDirection.NORTH, mob.size(), npcCheck)) {
        					direction = Directions.NormalDirection.NORTH;
        				}
        			}
        		}
        	}
        	if (direction == null) {
        		return null;
        	}
        	return new NextNode(loc, direction, Tile.canMove(mob, direction, mob.size(), npcCheck));
        } else {
	        if (dx > 0) {
	            if (dy > 0) {
	                if (dx == 1 && dy == 1 && combat) {
	                    if (Tile.canMove(mob, Directions.NormalDirection.WEST, mob.size(), npcCheck)) {
	                        direction = Directions.NormalDirection.WEST;
	                    } else if (Tile.canMove(mob, Directions.NormalDirection.SOUTH, mob.size(), npcCheck)) {
	                        direction = Directions.NormalDirection.SOUTH;
	                    } else {
	                        direction = Directions.NormalDirection.WEST; // random w/e
	                    }
	                } else {
	                    if (Tile.canMove(mob, Directions.NormalDirection.SOUTH_WEST, mob.size(), npcCheck)) {
	                        direction = Directions.NormalDirection.SOUTH_WEST;
	                    } else {
	                        if (dy > dx) {
	                            if (Tile.canMove(mob, Directions.NormalDirection.WEST, mob.size(), npcCheck)) {
	                                direction = Directions.NormalDirection.WEST;
	                            } else {
	                                direction = Directions.NormalDirection.SOUTH;
	                            }
	                        } else if (dy < dx) {
	                            if (Tile.canMove(mob, Directions.NormalDirection.SOUTH, mob.size(), npcCheck)) {
	                                direction = Directions.NormalDirection.SOUTH;
	                            } else {
	                                direction = Directions.NormalDirection.WEST;
	                            }
	                        } else {
	                        	if (Tile.canMove(mob, Directions.NormalDirection.SOUTH_WEST, mob.size(), npcCheck)) {
	                        		direction = Directions.NormalDirection.SOUTH_WEST;
	                        	} else if (Tile.canMove(mob, Directions.NormalDirection.SOUTH, mob.size(), npcCheck)) {
	                                direction = Directions.NormalDirection.SOUTH;
	                            } else {
	                                direction = Directions.NormalDirection.WEST;
	                            }
	                        }
	                    }
	                }
	            } else if (dy < 0) {
	                if (dx == 1 && dy == -1 && combat) {
	                    if (Tile.canMove(mob, Directions.NormalDirection.WEST, mob.size(), npcCheck)) {
	                        direction = Directions.NormalDirection.WEST;
	                    } else if (Tile.canMove(mob, Directions.NormalDirection.NORTH, mob.size(), npcCheck)) {
	                        direction = Directions.NormalDirection.NORTH;
	                    } else {
	                        direction = Directions.NormalDirection.WEST; // random w/e
	                    }
	                } else {
	                    if (Tile.canMove(mob, Directions.NormalDirection.NORTH_WEST, mob.size(), npcCheck)) {
	                        direction = Directions.NormalDirection.NORTH_WEST;
	                    } else {
	                        if (Math.abs(dy) > Math.abs(dx)) {
	                            if (Tile.canMove(mob, Directions.NormalDirection.WEST, mob.size(), npcCheck)) {
	                                direction = Directions.NormalDirection.WEST;
	                            } else {
	                                direction = Directions.NormalDirection.NORTH;
	                            }
	                        } else if (Math.abs(dy) < Math.abs(dx)) {
	                            if (Tile.canMove(mob, Directions.NormalDirection.NORTH, mob.size(), npcCheck)) {
	                                direction = Directions.NormalDirection.NORTH;
	                            } else {
	                                direction = Directions.NormalDirection.WEST;
	                            }
	                        } else {
	                        	if (Tile.canMove(mob, Directions.NormalDirection.NORTH_WEST, mob.size(), npcCheck)) {
	                        		direction = Directions.NormalDirection.NORTH_WEST;
	                        	} else if (Tile.canMove(mob, Directions.NormalDirection.NORTH, mob.size(), npcCheck)) {
	                                direction = Directions.NormalDirection.NORTH;
	                            } else {
	                                direction = Directions.NormalDirection.WEST;
	                            }
	                        }
	                    }
	                }
	            } else {
	                direction = Directions.NormalDirection.WEST;
	            }
	        } else if (dx < 0) {
	            if (dy > 0) {
	                if (dx == -1 && dy == 1 && combat) {
	                    if (Tile.canMove(mob, Directions.NormalDirection.EAST, mob.size(), npcCheck)) {
	                        direction = Directions.NormalDirection.EAST;
	                    } else if (Tile.canMove(mob, Directions.NormalDirection.SOUTH, mob.size(), npcCheck)) {
	                        direction = Directions.NormalDirection.SOUTH;
	                    } else {
	                        direction = Directions.NormalDirection.EAST; // random w/e
	                    }
	                } else {
	                    if (Tile.canMove(mob, Directions.NormalDirection.SOUTH_EAST, mob.size(), npcCheck)) {
	                        direction = Directions.NormalDirection.SOUTH_EAST;
	                    } else {
	                        if (Math.abs(dy) > Math.abs(dx)) {
	                            if (Tile.canMove(mob, Directions.NormalDirection.EAST, mob.size(), npcCheck)) {
	                                direction = Directions.NormalDirection.EAST;
	                            } else if (Tile.canMove(mob, Directions.NormalDirection.SOUTH, mob.size(), npcCheck)) {
	                                direction = Directions.NormalDirection.SOUTH;
	                            }
	                        } else if (Math.abs(dy) < Math.abs(dx)) {
	                            if (Tile.canMove(mob, Directions.NormalDirection.SOUTH, mob.size(), npcCheck)) {
	                                direction = Directions.NormalDirection.SOUTH;
	                            } else if (Tile.canMove(mob, Directions.NormalDirection.EAST, mob.size(), npcCheck)) {
	                                direction = Directions.NormalDirection.EAST;
	                            }
	                        } else {
	                        	if (Tile.canMove(mob, Directions.NormalDirection.SOUTH_EAST, mob.size(), npcCheck)) {
	                        		direction = Directions.NormalDirection.SOUTH_EAST;
	                        	} else if (Tile.canMove(mob, Directions.NormalDirection.SOUTH, mob.size(), npcCheck)) {
	                                direction = Directions.NormalDirection.SOUTH;
	                            } else if (Tile.canMove(mob, Directions.NormalDirection.EAST, mob.size(), npcCheck)) {
	                                direction = Directions.NormalDirection.EAST;
	                            }
	                        }
	                    }
	                }
	            } else if (dy < 0) {
	                if (dx == -1 && dy == -1 && combat) {
	                    if (Tile.canMove(mob, Directions.NormalDirection.EAST, mob.size(), npcCheck)) {
	                        direction = Directions.NormalDirection.EAST;
	                    } else if (Tile.canMove(mob, Directions.NormalDirection.NORTH, mob.size(), npcCheck)) {
	                        direction = Directions.NormalDirection.NORTH;
	                    } else {
	                        direction = Directions.NormalDirection.EAST; // random w/e
	                    }
	                } else {
	                    if (Tile.canMove(mob, Directions.NormalDirection.NORTH_EAST, mob.size(), npcCheck)) {
	                        direction = Directions.NormalDirection.NORTH_EAST;
	                    } else {
	                        if (Math.abs(dy) > Math.abs(dx)) {
	                            if (Tile.canMove(mob, Directions.NormalDirection.EAST, mob.size(), npcCheck)) {
	                                direction = Directions.NormalDirection.EAST;
	                            } else if (Tile.canMove(mob, Directions.NormalDirection.NORTH, mob.size(), npcCheck)) {
	                                direction = Directions.NormalDirection.NORTH;
	                            }
	                        } else if (Math.abs(dy) < Math.abs(dx)) {
	                            if (Tile.canMove(mob, Directions.NormalDirection.NORTH, mob.size(), npcCheck)) {
	                                direction = Directions.NormalDirection.NORTH;
	                            } else if (Tile.canMove(mob, Directions.NormalDirection.EAST, mob.size(), npcCheck)) {
	                                direction = Directions.NormalDirection.EAST;
	                            }
	                        } else {
	                        	if (Tile.canMove(mob, Directions.NormalDirection.NORTH_EAST, mob.size(), npcCheck)) {
	                        		direction = Directions.NormalDirection.NORTH_EAST;
	                        	} else if (Tile.canMove(mob, Directions.NormalDirection.NORTH, mob.size(), npcCheck)) {
	                                direction = Directions.NormalDirection.NORTH;
	                            } else if (Tile.canMove(mob, Directions.NormalDirection.EAST, mob.size(), npcCheck)) {
	                                direction = Directions.NormalDirection.EAST;
	                            }
	                        }
	                    }
	                }
	            } else {
	                direction = Directions.NormalDirection.EAST;
	            }
	        } else {
	            if (dy > 0) {
	                direction = Directions.NormalDirection.SOUTH;
	            } else if (dy < 0) {
	                direction = Directions.NormalDirection.NORTH;
	            } else {
	                if (Tile.canMove(mob, Directions.NormalDirection.WEST, mob.size(), npcCheck)) {
	                    direction = Directions.NormalDirection.WEST;
	                } else if (Tile.canMove(mob, Directions.NormalDirection.EAST, mob.size(), npcCheck)) {
	                    direction = Directions.NormalDirection.EAST;
	                } else if (Tile.canMove(mob, Directions.NormalDirection.NORTH, mob.size(), npcCheck)) {
	                    direction = Directions.NormalDirection.NORTH;
	                } else if (Tile.canMove(mob, Directions.NormalDirection.SOUTH, mob.size(), npcCheck)) {
	                    direction = Directions.NormalDirection.SOUTH;
	                } else {
	                    direction = Directions.NormalDirection.SOUTH; // random w/e
	                }
	
	            }
	        }
	        if (direction == null) {
	            return null;
	        }
	        return new NextNode(loc, direction, Tile.canMove(mob, direction, mob.size(), npcCheck));
        }
    }

	private static class NextNode {
        Tile tile = null;
        boolean canMove = false;
        public NextNode(Tile pos, Directions.NormalDirection dir, boolean canMove) {
            this.canMove = canMove;
            if (canMove) {
                tile = pos.transform(Directions.DIRECTION_DELTA_X[dir.intValue()], Directions.DIRECTION_DELTA_Y[dir.intValue()], 0);
            }
        }
    }
}

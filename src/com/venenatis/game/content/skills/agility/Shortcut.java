package com.venenatis.game.content.skills.agility;

import com.venenatis.game.location.Location;
import com.venenatis.game.model.Skills;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.dialogue.SimpleDialogues;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.model.masks.forceMovement.Direction;
import com.venenatis.game.model.masks.forceMovement.ForceMovement;
import com.venenatis.game.task.Task;
import com.venenatis.game.world.World;
import com.venenatis.game.world.object.GameObject;

public class Shortcut {

	public static boolean processAgilityShortcut(Player player, GameObject object) {
		
		int x;
		int y;
		int y2 = 0;
		Direction face;
		
		switch (object.getId()) {
		/* Taverly obstacle pipe */
		case 16509:
			if (!object.getLocation().equals(Location.create(2887, 9799, 0))) {
				player.removeAttribute("busy");
				return false; // return false because this is the wrong trigger (same id but probs diff loc)
			}
			
			if(player.getSkills().getLevel(Skills.AGILITY) < 70) {
				SimpleDialogues.sendStatement(player, "You need an Agility level of 70 to enter this area.");
				return true; // return TRUE because THIS IS THE RIGHT ONE .. inform them they dont have level req
			}
			player.setAttribute("busy", true);
			
			if (player.getLocation().getX() == 2886) {
				y = 4;
				y2 = 2;
				x = 1;
				face = Direction.NORTH;
			} else {
				y = -4;
				y2 = -2;
				x = -1;
				face = Direction.WEST;
			}
			player.playAnimation(new Animation(746));
			player.forceMove(new ForceMovement(x, 0, y2, 0, 45, 100, 2, face), false);
			player.playAnimation(new Animation(748, 50));
			player.forceMove(new ForceMovement(0, 0, y, 0, 0, 15, 1, face), true);
			return true;
		
		/* Brimhaven stepping stones */
		case 21738:
			if (player.getX() != 2649 && player.getY() != 9562) {
				player.removeAttribute("busy");
				return false;
			}
			
			if(player.getSkills().getLevel(Skills.AGILITY) < 12) {
				SimpleDialogues.sendStatement(player, "You need an Agility level of 12 to enter this area.");
				return true;
			}
			player.setAttribute("busy", true);
			World.getWorld().schedule(new Task(1) {
				public int tick = 0;
	            @Override
	            public void execute() {
	            	
	            	if (tick == 1) {
	            		player.forceTeleport(new Animation(769), player.getLocation().transform(0, -1), 0, 1);
					}
	            	
	            	if (tick == 3) {
	            		player.forceTeleport(new Animation(769), player.getLocation().transform(0, -1), 0, 1);
	            	}
	            	
	            	if (tick == 5) {
	            		player.face(player.getLocation().transform(-1, 0));
	            		player.forceTeleport(new Animation(769), player.getLocation().transform(-1, 0), 0, 1);
	            	}
	            	
	            	if (tick == 7) {
	            		player.face(player.getLocation().transform(-1, 0));
	            		player.forceTeleport(new Animation(769), player.getLocation().transform(-1, 0), 0, 1);
	            	}
	            	
	            	if (tick == 9) {
	            		player.face(player.getLocation().transform(0, -1));
	            		player.forceTeleport(new Animation(769), player.getLocation().transform(0, -1), 0, 1);
	            	}
	            	
	            	if (tick == 11) {
	            		player.forceTeleport(new Animation(769), player.getLocation().transform(0, -1), 0, 1);
	            	}
	            	
	            	if (tick == 13) {
	            		player.forceTeleport(new Animation(769), player.getLocation().transform(0, -1), 0, 1);
	            		player.getSkills().addExperience(Skills.AGILITY, 3);
						this.stop();
	            	}
		            tick++;
	            }
	        });
			return true;
			
		/* Brimhaven stepping stones */	
		case 21739:
			if (player.getX() != 2647 && player.getY() != 9557) {
				player.removeAttribute("busy");
				return false;
			}

			player.setAttribute("busy", true);
			World.getWorld().schedule(new Task(1) {
				public int tick = 0;
	            @Override
	            public void execute() {
	            	
	            	if (tick == 1) {
	            		player.forceTeleport(new Animation(769), player.getLocation().transform(0, +1), 0, 1);
					}
	            	
	            	if (tick == 3) {
	            		player.forceTeleport(new Animation(769), player.getLocation().transform(0, +1), 0, 1);
	            	}
	            	
	            	if (tick == 5) {
	            		player.forceTeleport(new Animation(769), player.getLocation().transform(0, +1), 0, 1);
	            	}
	            	
	            	if (tick == 7) {
	            		player.face(player.getLocation().transform(+1, 0));
	            		player.forceTeleport(new Animation(769), player.getLocation().transform(+1, 0), 0, 1);
	            	}
	            	
	            	if (tick == 9) {
	            		player.forceTeleport(new Animation(769), player.getLocation().transform(+1, 0), 0, 1);
	            	}
	            	
	            	if (tick == 11) {
	            		player.face(player.getLocation().transform(0, +1));
	            		player.forceTeleport(new Animation(769), player.getLocation().transform(0, +1), 0, 1);
	            	}
	            	
	            	if (tick == 13) {
	            		player.forceTeleport(new Animation(769), player.getLocation().transform(0, +1), 0, 1);
	            		player.getSkills().addExperience(Skills.AGILITY, 3);
						this.stop();
	            	}
		            tick++;
	            }
	        });
			return true;
			
		/* Falador crumbling wall */
		case 24222:
			if(player.getSkills().getLevel(Skills.AGILITY) < 5) {
				SimpleDialogues.sendStatement(player, "You need an Agility level of 5 to enter this area.");
				return true;
			}
			
			x = 2;
			Direction dir = Direction.NORTH;
			if(player.getLocation().getX() >= 2936) {
				x = -2;
			}
			player.playAnimation(new Animation(839));
			player.forceMove(new ForceMovement(0, 0, x, 0, 20, 60, 2, dir), true);
			player.getSkills().addExperience(Skills.AGILITY, 0.5);
			return true;
		
		/* Shaman rock climbing */
		case 27362:
			if (player.getLocation().getX() == 1454) {
				player.setRunningToggled(false, 6);
				player.forceWalk(new Animation(737), player.getX() + 6, player.getY(), 0, 4, true);
			} else if (player.getLocation().getX() == 1460) {
				player.setRunningToggled(false, 6);
				player.forceWalk(new Animation(737), player.getX() - 6, player.getY(), 0, 4, true);
			} else if(player.getLocation().getX() == 1470) {
				player.setRunningToggled(false, 6);
				player.face(Location.create(player.getX() - 6, player.getY(), 0));
				player.forceWalk(new Animation(737), player.getX() + 6, player.getY(), 0, 6, true);
		    } else if(player.getLocation().getX() == 1476) {
		    	player.setRunningToggled(false, 6);
			    player.face(Location.create(1480, 3690, 0));
			    player.forceWalk(new Animation(737), player.getX() - 6, player.getY(), 0, 6, true);
		    }
			return true;
		
		/* Ardougne log balance */
		case 16548:
			if(player.getSkills().getLevel(Skills.AGILITY) < 5) {
				SimpleDialogues.sendStatement(player, "You need an Agility level of 5 to enter this area.");
				return true;
			}
			
			if(player.getLocation().getY() != 3336) {
		        player.removeAttribute("busy");
		        return false;
		    }
			
		    x = -4;
		    if (player.getLocation().getX() == 2598) {
		        x = 4;
		    }
		    player.setRunningToggled(false, 4);
		    player.forceWalk(new Animation(762), player.getX() + x, player.getY(), 0, 4, true);
		    player.getSkills().addExperience(Skills.AGILITY, 4);
			return true;
			
		/* Edgevile dungeon obstacle pipe */
		case 16511:
			if (!player.getLocation().equals(Location.create(3149, 9906, 0)) && !player.getLocation().equals(Location.create(3155, 9906, 0))) {
				player.removeAttribute("busy");
				return false;
			}
			y = 0;
			y2 = 0;
			x = 0;
			face = Direction.NORTH;
			if (player.getLocation().getX() == 3149) {
				y = 4;
				y2 = 2;
				x = 1;
			} else {
				y = -4;
				y2 = -2;
				x = -1;
			}
			player.playAnimation(Animation.create(746));
			player.forceMove(new ForceMovement(x, 0, y2, 0, 45, 100, 3, face), false);
			player.playAnimation(Animation.create(748));
			player.forceMove(new ForceMovement(0, 0, y, 0, 0, 15, 1, face), true);
			return true;
		
		/* Taverly spikes */
		case 16510:
			if(!player.getLocation().equals(Location.create(2880, 9813, 0)) && !player.getLocation().equals(Location.create(2878, 9813))) {
		        player.removeAttribute("busy");
		        return false;
		    }
		    x = -2;
		    if (player.getLocation().equals(Location.create(2878, 9813, 0))) {
		        x = 2;
		    }
		    player.forceTeleport(Animation.create(2586), Location.create(player.getX() + x, 9813, 0), 0, 2);
		    return true;
		    
		/* Fremmenik spikes */
		case 16544:
			if (object.getLocation().equals(Location.create(2774, 10003, 0))) {
				x = -2;
				if (player.getLocation().equals(Location.create(2773, 10003, 0))) {
					x = 2;
				}
				player.forceTeleport(Animation.create(2586), Location.create(player.getX() + x, 10003, 0), 0, 2);
			} else if (object.getLocation().equals(Location.create(2769, 10002, 0))) {
				x = -2;
			    if (player.getLocation().equals(Location.create(2768, 10002, 0))) {
			        x = 2;
			    }
			    player.forceTeleport(Animation.create(2586), Location.create(player.getX() + x, 10002, 0), 0, 2);
		    }
			return true;
			
		case 10047:
			if(player.getLocation().equals(Location.create(3759, 5670, 0))) {
				player.forceTeleport(Animation.create(746), Location.create(3765, 5671, 0), 0, 2);
		    } else if(player.getLocation().equals(Location.create(3765, 5671, 0))) {
		    	player.forceTeleport(Animation.create(746), Location.create(3759, 5670, 0), 0, 2);
		    }
			return true;

		}
		return false;
	}
}
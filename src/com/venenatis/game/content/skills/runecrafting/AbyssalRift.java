package com.venenatis.game.content.skills.runecrafting;

import com.couchbase.client.deps.com.lmax.disruptor.util.Util;
import com.venenatis.game.content.SetSkill.SkillData;
import com.venenatis.game.content.skills.mining.Mining.PickAxe;
import com.venenatis.game.location.Location;
import com.venenatis.game.model.Skills;
import com.venenatis.game.model.combat.Combat;
import com.venenatis.game.model.combat.data.SkullType;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.model.masks.forceMovement.Direction;
import com.venenatis.game.task.Task;
import com.venenatis.game.util.Utility;
import com.venenatis.game.world.World;
import com.venenatis.game.world.object.GameObject;
import com.venenatis.server.Server;
/**
 * 
 * @author Justin aka Harambe_
 *
 */
public class AbyssalRift {

	final int EYES = 26146;
	final int EYES2 = 26147;
	final int EYES3 = 26148;
	
	final int TENDRILS = 25425; // anim 2173
	final int TENDRILS2 = 26168;
	final int TENDRILS3 = 26169;
	
	final int BOIL = 26144;
	final int BOIl2 = 26145;
	
	final int SQUEAZE = 25428;
	
	final int ROCK = 25422;
	final int ROCK2 = 25423;
	final int ROCK3 = 25424;
	
	
	private Location spawn;
	private Location randomTele(Player player) {
		switch(Utility.random(6)) {
		case 0:
			spawn = new Location(3017,4848);
			break;
		case 1:
			spawn = new Location(3014,4828);
			break;
		case 2:
			spawn = new Location(3022,4813);
			break;
		case 3:
			spawn = new Location(3046,4811);
			break;
		case 4:
			spawn = new Location(3060,4823);
			break;
		case 5:
			spawn = new Location(3061,4836);
			break;
		}
		
		return spawn;
		
	}
	public void enterRift(Player player) {
		player.getTeleportAction().teleport(randomTele(player));
		World.getWorld().schedule(new Task(3) {
			public void execute() {
				int modification = (int) (player.getSkills().getLevel(5));
				Combat.skull(player, SkullType.SKULL, 300);
				player.getSkills().decreaseLevelToZero(5, modification);
				player.getActionSender().sendMessage("You enter the Rift Abyss");
				this.stop();
			}
		});
	}
	
	
	public boolean obstical(Player player, GameObject object) {
			RiftTeles t = RiftTeles.forId(object.getId());
			if(t != null)
			player.getTeleportAction().teleport(t.getInsideLocation());	
		switch (object.getId()) {
		case 25428:
			player.setAttribute("busy", true);
			player.playAnimation(new Animation(746));
			if(Utility.random(3) == 0) {	
				World.getWorld().schedule(new Task(3) {
					public void execute() {
						if(object.getLocation().matches(3018, 4833)) {
							Location location = new Location(3024, 4834);
							player.setTeleportTarget(location);
							player.sendDelayedMessage(3, "You slip through the gap.");
						}
						if(object.getLocation().matches(3049, 4849)) {
							Location location = new Location(3047, 4842);
							player.setTeleportTarget(location);
							player.sendDelayedMessage(3, "You slip through the gap.");
						}
						player.removeAttribute("busy");
						this.stop();
					}
				});
			} else 
				player.removeAttribute("busy");
			return true;
		case 25422:
			mine(player, object);
			break;
		case 26146:
			distract(player, object);
			break;
		}
	 return false;
	}
	private int trys = 0;
	private void distract(Player player, GameObject object) {
		if(object == null)
			return;
		trys++;
		player.playAnimation(new Animation(865));
		if(Utility.random(5) == 0) {
			player.sendDelayedMessage(1, "You distracted the eyes");
			Server.getGlobalObjects().replaceObject (object, new GameObject(EYES3, object.getLocation()), 30);		
			World.getWorld().schedule(new Task(4) {
				public void execute() {
					if(object.getLocation().matches(3028, 4849)) {
						Location location = new Location(3032, 4844);
						player.setTeleportTarget(location);
						trys = 0;
					}
					if(object.getLocation().matches(3058, 4839)) {
						Location location = new Location(3051, 4837);
						player.setTeleportTarget(location);
						trys = 0;
					}
					this.stop();
				}
			});
			
		}
		if(trys > 5) {
			Server.getGlobalObjects().replaceObject(object, new GameObject(EYES2, object.getLocation()), 30);		
			player.getActionSender().sendMessage("Your attemps to distract the eyes have failed");
			trys = 0;
		}
	}
	

	private PickAxe pickaxe;
	
	private void mine(Player player, GameObject object) {
		if(object == null)
			return;
		if(canMine(player)) {
			player.playAnimation(pickaxe.getAnimation());
			object.setCurrentHealth(100);
			World.getWorld().schedule(new Task(1) {
				public void execute() {
					object.decreaseCurrentHealth(4+Utility.random(10));
					player.removeAttribute("busy");
					player.getActionSender().sendMessage("Obj health "+object.getCurrentHealth());
					if(object.getCurrentHealth() <= 40)  
					Server.getGlobalObjects().replaceObject(object, new GameObject(ROCK2, object.getLocation()), 30);
					if(object.getCurrentHealth() <= 0) {
					Server.getGlobalObjects().replaceObject(object, new GameObject(ROCK3, object.getLocation()), -1);	
					if(object.getLocation().matches(3021, 4842)) {
						Location location = new Location(3029, 4842);
						player.setTeleportTarget(location);
						player.sendDelayedMessage(1, "You mine the rock away..");
					}
					if(object.getLocation().matches(3038, 4853)) {
						Location location = new Location(3038, 4844);
						player.setTeleportTarget(location);
						player.sendDelayedMessage(1, "You mine the rock away..");
					}
					this.stop();
					}
				}
			});
			
		}
		
	}
	private boolean canMine(Player player) {
		for(PickAxe pickaxe : PickAxe.values()) {
			if((player.getInventory().contains(pickaxe.getId()) || player.getEquipment().contains(pickaxe.getId()))
							&& player.getSkills().getLevelForExperience(Skills.MINING) >= pickaxe.getRequiredLevel()) {
				this.pickaxe = pickaxe;
				break;
			}
		}
		if(pickaxe == null) {
		player.getActionSender().sendMessage("You do not have a pickaxe that you can use.");
			return false;
		}
		return true;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}

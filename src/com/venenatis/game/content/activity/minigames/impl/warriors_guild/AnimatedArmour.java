package com.venenatis.game.content.activity.minigames.impl.warriors_guild;

import com.venenatis.game.location.Location;
import com.venenatis.game.model.Item;
import com.venenatis.game.model.entity.npc.NPC;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.dialogue.SimpleDialogues;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.model.masks.UpdateFlags.UpdateFlag;
import com.venenatis.game.task.Task;
import com.venenatis.game.world.World;
import com.venenatis.game.world.ground_item.GroundItem;
import com.venenatis.game.world.ground_item.GroundItemHandler;
import com.venenatis.server.Server;

/**
 * 
 * @author Jason http://www.rune-server.org/members/jason
 * @date Oct 20, 2013
 */
public class AnimatedArmour {
	
	public static boolean animator_west = false;

	public enum Armour {

		BRONZE(2450, 1155, 1117, 1075, 5), 
		IRON(2451, 1153, 1115, 1067, 10), 
		STEEL(2452, 1157, 1119, 1069, 15), 
		MITHRIL(2454, 1159, 1121, 1071, 50), 
		ADAMANT(2455, 1161, 1123, 1073, 60), 
		RUNE(2456, 1163, 1127, 1079, 80);

		int npcId, helm, platebody, platelegs, tokens;

		Armour(int npcId, int helm, int platebody, int platelegs, int tokens) {
			this.npcId = npcId;
			this.helm = helm;
			this.platebody = platebody;
			this.platelegs = platelegs;
			this.tokens = tokens;
		}

		public int getNpcId() {
			return npcId;
		}

		public int getHelmId() {
			return helm;
		}

		public int getPlatebodyId() {
			return platebody;
		}

		public int getPlatelegsId() {
			return platelegs;
		}

		public int getAmountOfTokens() {
			return tokens;
		}
	}

	private static Armour getArmourForItemId(int itemId) {
		for (Armour a : Armour.values())
			if (a.getHelmId() == itemId || a.getPlatebodyId() == itemId || a.getPlatelegsId() == itemId)
				return a;
		return null;
	}

	private static Armour getArmourForNpcId(NPC npc) {
		for (Armour a : Armour.values())
			if (a.getNpcId() == npc.getId())
				return a;
		return null;
	}

	public static boolean isAnimatedArmourNpc(NPC npc) {
		for (Armour armour : Armour.values()) {
			if (armour.npcId == npc.getId()) {
				return true;
			}
		}
		return false;
	}

	public static void itemOnAnimator(final Player player, int itemId) {
		int x = player.getX(), y = player.getY();
		
		if (y != 3537) {
			player.getActionSender().sendMessage("You need to move closer.");
			return;
		}
		
		if (x == 2851) {
			animator_west = true;
		} else {
			animator_west = false;
		}
		
		if (player.hasAttribute("animation_armour_spawned")) {
			player.getActionSender().sendMessage("An Animated Armour npc is already spawned.");
			return;
		}
		
		final Armour armour = getArmourForItemId(itemId);
		if (armour == null) {
			player.getActionSender().sendMessage("This is not a feasable animated armour item.");
			return;
		}
		
		if (!player.getInventory().contains(armour.getPlatebodyId(), 1) || !player.getInventory().contains(armour.getPlatelegsId(), 1) || !player.getInventory().contains(armour.getHelmId(), 1)) {
			player.getActionSender().sendMessage("You need the helm, platebody, and platelegs to spawn the animated armour.");
			return;
		}
		
		player.setAttribute("animation_armour_spawned", true);
		player.getInventory().remove(armour.getPlatebodyId(), 1);
		player.getInventory().remove(armour.getPlatelegsId(), 1);
		player.getInventory().remove(armour.getHelmId(), 1);
		
	    World.getWorld().schedule(new Task(1) {
	    	public int tick = 0;
			@Override
			public void execute() {
				if(tick == 0) {
					SimpleDialogues.sendStatement(player, "You place your armour on the platform where it dissapears...");
				}
				
				if(tick == 3) {
					SimpleDialogues.sendStatement(player, "The animator hims, something appears to be working. You stand", "back...");
				}
				
				if(tick == 5) {
					player.forceWalk(new Animation(820), player.getX(), player.getY()+3, 0, 0, true);
					player.getActionSender().removeAllInterfaces();
					player.face(player.getLocation().transform(-1, 0));
				}
				
				if (tick == 7) {
					NPC npc = new NPC(armour.getNpcId());
					// TODO ask Jak how to perform the anim and forced text
					npc.spawn(player, armour.getNpcId(), new Location(animator_west ? 2851 : 2857, 3536, 0), 1, true);
					npc.playAnimation(new Animation(4166));
					npc.sendForcedMessage("I'M ALIVE!");
					// npc.getActionSender().sendEntityHint(npc);
				}
				tick++;
			}
		});
	}

	public static void dropTokens(Player player, NPC npc, Location location) {
		Armour armour = getArmourForNpcId(npc);
		if (armour != null) {
			GroundItemHandler.createGroundItem(new GroundItem(new Item(8851, armour.getAmountOfTokens()), location, player));
			player.removeAttribute("animation_armour_spawned");
		}
	}

}
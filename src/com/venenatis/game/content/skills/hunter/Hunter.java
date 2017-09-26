package com.venenatis.game.content.skills.hunter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.venenatis.game.content.skills.hunter.trap.Trap;
import com.venenatis.game.content.skills.hunter.trap.Trap.TrapState;
import com.venenatis.game.content.skills.hunter.trap.Trap.TrapType;
import com.venenatis.game.event.CycleEventHandler;
import com.venenatis.game.content.skills.hunter.trap.TrapProcessor;
import com.venenatis.game.content.skills.hunter.trap.TrapTask;
import com.venenatis.game.model.Item;
import com.venenatis.game.model.Skills;
import com.venenatis.game.model.entity.Boundary;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.util.Utility;
import com.venenatis.game.world.World;
import com.venenatis.game.world.ground_item.GroundItem;
import com.venenatis.game.world.ground_item.GroundItemHandler;
import com.venenatis.game.world.object.GameObject;
import com.venenatis.game.world.pathfinder.clipmap.Region;
import com.venenatis.server.Server;

/**
 * The class which holds static functionality for the hunter skill.
 * @author <a href="http://www.rune-server.org/members/stand+up/">Stand Up</a>
 */
public final class Hunter {

	/**
	 * The mappings which contain each trap by player on the world.
	 */
	public static final Map<Player, TrapProcessor> GLOBAL_TRAPS = new HashMap<>();

	/**
	 * Retrieves the maximum amount of traps a player can lay.
	 * @param player	the player to lay a trap down for.
	 * @return a numerical value determining the amount a player can lay.
	 */
	private static int getMaximumTraps(Player player) {
		int level = player.getSkills().getLevel(Skills.HUNTER);
		return level / 20 + 1;

	}

	/**
	 * Attempts to abandon the specified {@code trap} for the player.
	 * @param trap		the trap that was abandoned.
	 * @param logout	if the abandon was due to the player logging out.
	 */
	public static void abandon(Player player, Trap trap, boolean logout) {
		if(GLOBAL_TRAPS.get(player) == null) {
			return;
		}
		
		if(logout) {
			GLOBAL_TRAPS.get(player).getTraps().forEach(t -> {
				t.setAbandoned(true);
				Server.getGlobalObjects().remove(t.getObject());
				Server.getGlobalObjects().remove(t.getObject().getId(), t.getObject().getX(), t.getObject().getY(), t.getObject().getZ());
				GroundItemHandler.createGroundItem(new GroundItem(new Item(t.getType().getItemId()), t.getObject().getLocation(), player));
			});
			GLOBAL_TRAPS.get(player).getTraps().clear();
		} else {
			GLOBAL_TRAPS.get(player).getTraps().remove(trap);
			trap.setAbandoned(true);
			Server.getGlobalObjects().remove(trap.getObject());
			Server.getGlobalObjects().remove(trap.getObject().getId(), trap.getObject().getX(), trap.getObject().getY(), trap.getObject().getZ());
			GroundItemHandler.createGroundItem(new GroundItem(new Item(trap.getType().getItemId()), trap.getObject().getLocation(), player));
			player.getActionSender().sendMessage("You have abandoned your trap...");
		}

		if(GLOBAL_TRAPS.get(player).getTraps().isEmpty()) {
			GLOBAL_TRAPS.get(player).setTask(Optional.empty());
			GLOBAL_TRAPS.remove(player);
		}
	}

	/**
	 * Attempts to lay down the specified {@code trap} for the specified {@code player}.
	 * @param player	the player to lay the trap for.
	 * @param trap		the trap to lay down for the player.
	 * @return {@code true} if the trap was laid, {@code false} otherwise.
	 */
	public static boolean lay(Player player, Trap trap) {
		if(!player.last_trap_layed.elapsed(1200)) {
			return false;
		}

		player.last_trap_layed.reset();
		
		if (!Boundary.isIn(player, Boundary.HUNTER_BOUNDARIES)) {
			player.getActionSender().sendMessage("This is not a suitable spot to place a trap.");
			return false;
		}

		GLOBAL_TRAPS.putIfAbsent(player, new TrapProcessor());

		if(!GLOBAL_TRAPS.get(player).getTask().isPresent()) {
			GLOBAL_TRAPS.get(player).setTask(new TrapTask(player));
			CycleEventHandler.getSingleton().addEvent(player, GLOBAL_TRAPS.get(player).getTask().get(), 10);
		}

		if(GLOBAL_TRAPS.get(player).getTraps().size() >= getMaximumTraps(player)) {
			player.getActionSender().sendMessage("You cannot lay more then " + getMaximumTraps(player) + " with your hunter level.");
			return false;
		}

		if(Server.getGlobalObjects().anyExists(player.getX(), player.getY(), player.getZ())) {
			player.getActionSender().sendMessage("You can't lay down your trap here.");
			return false;
		}

		GLOBAL_TRAPS.get(player).getTraps().add(trap);

		trap.submit();
		player.playAnimation(new Animation(827));
		player.getInventory().remove(trap.getType().getItemId(), 1);
		Server.getGlobalObjects().add(trap.getObject());
		if (Region.getClipping(player.getX() - 1, player.getY(), player.getZ(), -1, 0)) {
			player.getWalkingQueue().walkTo(-1, 0);
		} else if (Region.getClipping(player.getX() + 1, player.getY(), player.getZ(), 1, 0)) {
			player.getWalkingQueue().walkTo(1, 0);
		} else if (Region.getClipping(player.getX(), player.getY() - 1, player.getZ(), 0, -1)) {
			player.getWalkingQueue().walkTo(0, -1);
		} else if (Region.getClipping(player.getX(), player.getY() + 1, player.getZ(), 0, 1)) {
			player.getWalkingQueue().walkTo(0, 1);
		}
		return true;
	}

	/**
	 * Attempts to pick up the trap for the specified {@code player}.
	 * @param player	the player to pick this trap up for.
	 * @param id		the object id that was clicked.
	 * @return {@code true} if the trap was picked up, {@code false} otherwise.
	 */
	public static boolean pickup(Player player, GameObject object) {
		Optional<TrapType> type = TrapType.getTrapByObjectId(object.getId());
		
		if (System.currentTimeMillis() - player.lastPickup < 2500)
			return false;		

		if(!type.isPresent()) {
			return false;
		}

		Trap trap = getTrap(player, object).orElse(null);

		if(trap == null) {
			return false;
		}

		if(trap.getPlayer() == null) {
			player.getActionSender().sendMessage("You can't pickup someone elses trap...");
			return false;
		}
		
		if(trap.getState().equals(TrapState.CAUGHT)) {
			return false;
		}

		GLOBAL_TRAPS.get(player).getTraps().remove(trap);

		if(GLOBAL_TRAPS.get(player).getTraps().isEmpty()) {
			GLOBAL_TRAPS.get(player).setTask(Optional.empty());
			GLOBAL_TRAPS.remove(player);
		}

		trap.onPickUp();
		Server.getGlobalObjects().remove(trap.getObject());
		Server.getGlobalObjects().remove(trap.getObject().getId(), trap.getObject().getX(), trap.getObject().getY(), trap.getObject().getZ());
		player.getInventory().add(trap.getType().getItemId(), 1);
		player.playAnimation(new Animation(827));
		player.lastPickup = System.currentTimeMillis();
		return true;
	}


	/**
	 * Attempts to claim the rewards of this trap.
	 * @param player		the player attempting to claim the items.
	 * @param object		the object being interacted with.
	 * @return {@code true} if the trap was claimed, {@code false} otherwise.
	 */
	public static boolean claim(Player player, GameObject object) {
		Trap trap = getTrap(player, object).orElse(null);
		
		if (System.currentTimeMillis() - player.lastPickup < 2500)
			return false;		

		if(trap == null) {
			player.getActionSender().sendMessage("You can't pickup someone elses trap...");
			return false;
		}

		if(!trap.canClaim(object)) {
			return false;
		}

		if(trap.getPlayer() == null) {
			player.getActionSender().sendMessage("You can't claim the rewards of someone elses trap...");
			return false;
		}

		if(!trap.getState().equals(TrapState.CAUGHT)) {
			return false;
		}

		Arrays.stream(trap.reward()).forEach(reward -> player.getInventory().add(reward.getId(), reward.getAmount()));

		player.getSkills().addExperience(Skills.HUNTER, trap.experience());

		GLOBAL_TRAPS.get(player).getTraps().remove(trap);
		
		if(GLOBAL_TRAPS.get(player).getTraps().isEmpty()) {
			GLOBAL_TRAPS.get(player).setTask(Optional.empty());
			GLOBAL_TRAPS.remove(player);
		}
		
		Server.getGlobalObjects().remove(trap.getObject());
		Server.getGlobalObjects().remove(trap.getObject().getId(), trap.getObject().getX(), trap.getObject().getY(), trap.getObject().getZ());
		player.getInventory().add(trap.getType().getItemId(), 1);
		player.playAnimation(new Animation(827));
		player.lastPickup = System.currentTimeMillis();
		
		int randomGray = Utility.random(1500);
		int randomRed = Utility.random(2500);
		int randomBlack = Utility.random(3500);
		int randomGold = Utility.random(15000);
		
		 if (randomGold == 2 && player.getInventory().contains(13326) && player.getPet() != 13326) {
			 World.getWorld().sendWorldMessage("[<col=CC0000>News</col>] @cr18@ <col=255>" + player.getUsername() + "</col> caught a <col=CC0000>Golden Chinchompa</col> pet lucky enough!", false);
			 player.getInventory().addOrSentToBank(player, new Item(13326));
		 }
		switch (trap.getType().getItemId()) {
		case 10033:
			 if (randomGray == 25 && player.getInventory().contains(13324) && player.getPet() != 13324) {
				 World.getWorld().sendWorldMessage("[<col=CC0000>News</col>] @cr18@ <col=255>" + player.getUsername() + "</col> caught a <col=CC0000>Gray Chinchompa</col> pet!", false);
				 player.getInventory().addOrSentToBank(player, new Item(13324));
			 }
			break;
			
		case 10034:
			 if (randomRed == 15 && player.getInventory().contains(13323) && player.getPet() != 13323) {
				 World.getWorld().sendWorldMessage("[<col=CC0000>News</col>] @cr18@ <col=255>" + player.getUsername() + "</col> caught a <col=CC0000>Red Chinchompa</col> pet!", false);
				 player.getInventory().addOrSentToBank(player, new Item(13323));
			 }
			break;
			
		case 11959:
			 if (randomBlack == 8 && player.getInventory().contains(13325) && player.getPet() != 13325) {
				 World.getWorld().sendWorldMessage("[<col=CC0000>News</col>] @cr18@ <col=255>" + player.getUsername() + "</col> caught a <col=CC0000>Black Chinchompa</col> pet!", false);
				 player.getInventory().addOrSentToBank(player, new Item(13325));
			 }
			break;
		}
		return true;
	}


	/**
	 * Gets a trap for the specified global object given.
	 * @param player	the player to return a trap for.
	 * @param object	the object to compare.
	 * @return a trap wrapped in an optional, {@link Optional#empty()} otherwise.
	 */
	public static Optional<Trap> getTrap(Player player, GameObject object) {
		return !GLOBAL_TRAPS.containsKey(player) ? Optional.empty() : GLOBAL_TRAPS.get(player).getTraps().stream().filter(trap -> trap.getObject().getId() == object.getId() && trap.getObject().getX() == object.getX() && trap.getObject().getY() == object.getY() && trap.getObject().getZ() == object.getZ()).findAny();
	}
}
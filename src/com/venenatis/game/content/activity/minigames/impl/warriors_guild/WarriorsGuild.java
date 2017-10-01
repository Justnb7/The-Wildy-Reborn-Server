package com.venenatis.game.content.activity.minigames.impl.warriors_guild;

import java.util.Arrays;
import java.util.Optional;

import com.venenatis.game.location.Location;
import com.venenatis.game.model.Item;
import com.venenatis.game.model.definitions.ItemDefinition;
import com.venenatis.game.model.entity.Boundary;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.dialogue.SimpleDialogues;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.task.Task;
import com.venenatis.game.util.Utility;
import com.venenatis.game.world.World;
import com.venenatis.game.world.ground_item.GroundItem;
import com.venenatis.game.world.ground_item.GroundItemHandler;

/**
 * 
 * @author Jason http://www.rune-server.org/members/jason
 * @date Oct 20, 2013
 */
public class WarriorsGuild {
	
	private Player player;
	
	public WarriorsGuild(Player player) {
		this.player = player;
	}

	public static final Boundary CYCLOPS_BOUNDARY = new Boundary(2833, 3530, 2880, 3560, 2);

	public static final Boundary[] WAITING_ROOM_BOUNDARY = new Boundary[] { new Boundary(2838, 3536, 2846, 3542, 2), new Boundary(2847, 3537, 2847, 3537, 2) };

	private boolean active;

	public static final int[][] DEFENDER_DATA = { { 8844, 10 }, { 8845, 15 }, { 8846, 20 }, { 8847, 22 }, { 8848, 25 }, { 8849, 30 }, { 8850, 35 }, { 12954, 50 } };

	public void cycle() {
		setActive(true);
		World.getWorld().schedule(new Task(100) {

			@Override
			public void execute() {
				if (player == null || !player.isActive()) {
					this.stop();
					return;
				}
				if (!Boundary.isIn(player, CYCLOPS_BOUNDARY) || Boundary.isIn(player, WAITING_ROOM_BOUNDARY)) {
					setActive(false);
					this.stop();
					return;
				}
				if (!player.getInventory().contains(8851, 10)) {
					removeFromRoom();
					setActive(false);
					this.stop();
					return;
				}
				player.getInventory().remove(8851, 20);
				player.getActionSender().sendMessage("You notice some of your warrior guild tokens dissapear..", 255);
			}

		});
	}

	public void handleDoor() {
		//Walking out of the cyclops room
		if (player.getX() == 2847 && player.getY() == 3540 || player.getX() == 2847 && player.getY() == 3541) {
			player.forceWalk(new Animation(0x333), player.getX() -1, player.getY(), 0, 1, false);
			
		//Walking into the cyclops room
		} else if (player.getX() == 2846 && player.getY() == 3540 || player.getX() == 2846 && player.getY() == 3541 || Boundary.isIn(player, WAITING_ROOM_BOUNDARY)) {
			if (player.getInventory().contains(8851, 200)) {
				int current = currentDefender();
				if (current == -1) {
					SimpleDialogues.sendMobStatement(player, 2461, "You are not in the possesion of a defender.", "You must kill cyclops to obtain a defender.", "The fee for entering the area is 200 tokens.", "Do you want to enter?");
					player.setAttribute("yes_no_action", 1);
					//player.getDialogueManager().start("YES_OR_NO", player);
				} else {
					SimpleDialogues.sendMobStatement(player, 2461, "You are currently in posession of a " + ItemDefinition.get(current) + ".", "It will cost 200 tokens to re-enter the cyclops area.", "Do you want to enter? It will cost you.");
					player.setAttribute("yes_no_action", 1);
					//player.getDialogueManager().start("YES_OR_NO", player);
				}
			} else {
				SimpleDialogues.sendMobStatement(player, 2461, "You need atleast 200 warrior guild tokens.", "You can get some by operating the armour animator.");
			}
		}
	}

	/**
	 * Attempts to return the value of the defender the player is wearing or is in posession of in their inventory.
	 * 
	 * @return -1 will be returned in the case that the player does not have a defender
	 */
	private int currentDefender() {
		for (int index = DEFENDER_DATA.length - 1; index > -1; index--) {
			int[] defender = DEFENDER_DATA[index];
			if (player.getInventory().contains(defender[0]) || player.getEquipment().contains(defender[0])) {
				return defender[0];
			}
		}
		return -1;
	}

	/**
	 * Attempts to return the next best defender.
	 * 
	 * @return The first defender, bronze, if the player doesnt have a defender. If the player has the best it will return the best. If either of the afforementioned conditions are
	 *         not met, the next best defender is returned.
	 */
	private int nextDefender() {
		int defender = currentDefender();
		if (defender == -1) {
			return DEFENDER_DATA[0][0];
		}
		int best = DEFENDER_DATA[DEFENDER_DATA.length - 1][0];
		if (best == defender) {
			return best;
		}
		int index = indexOf(defender);
		if (index != -1) {
			defender = DEFENDER_DATA[index + 1][0];
		}
		return defender;
	}

	/**
	 * Attempts to retrieve the index in the array of the defender
	 * 
	 * @param defender the defender
	 * @return -1 will be returned if the defender cannot be found
	 */
	private int indexOf(int defender) {
		for (int index = 0; index < DEFENDER_DATA.length; index++) {
			if (defender == DEFENDER_DATA[index][0]) {
				return index;
			}
		}
		return -1;
	}

	/**
	 * Retrieves the drop chance of the next best defender the player can receive.
	 * 
	 * @return the chance of the dropped dagger.
	 */
	private int chance() {
		Optional<int[]> defender = Arrays.asList(DEFENDER_DATA).stream().filter(data -> data[0] == nextDefender()).findFirst();
		return defender.isPresent() ? defender.get()[1] : 0;
	}

	public void dropDefender(Location location) {
		int amount = player.getInventory().getAmount(8851);
		if (isActive() && Boundary.isIn(player, CYCLOPS_BOUNDARY) && !Boundary.isIn(player, WAITING_ROOM_BOUNDARY) && amount > 1) {
			int chance = chance();
			int current = currentDefender();
			int item = current == -1 ? DEFENDER_DATA[0][0] : nextDefender();
			if (Utility.random(chance) == 0) {
				GroundItemHandler.createGroundItem(new GroundItem(new Item(item), location, player));
				player.getActionSender().sendMessage("@blu@The cyclops dropped a " + ItemDefinition.get(item) + " on the ground.", 600000);
			}
		}
	}

	public void removeFromRoom() {
		player.setTeleportTarget(new Location(2846, 3540, 2));
		SimpleDialogues.sendStatement(player, "You do not have enough tokens to continue.");
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

}
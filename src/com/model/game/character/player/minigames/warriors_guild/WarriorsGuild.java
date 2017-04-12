package com.model.game.character.player.minigames.warriors_guild;

import java.util.Arrays;
import java.util.Optional;

import com.model.game.character.player.Boundary;
import com.model.game.character.player.Player;
import com.model.game.item.Item;
import com.model.game.item.ground.GroundItem;
import com.model.game.item.ground.GroundItemHandler;
import com.model.game.location.Position;
import com.model.task.events.CycleEvent;
import com.model.task.events.CycleEventContainer;
import com.model.task.events.CycleEventHandler;
import com.model.utility.Utility;

/**
 * 
 * @author Jason http://www.rune-server.org/members/jason
 * @date Oct 20, 2013
 */
public class WarriorsGuild {
	
	public static final Boundary CYCLOPS_BOUNDARY = new Boundary(2833, 3530, 2880, 3560, 2);
	
	public static final Boundary[] WAITING_ROOM_BOUNDARY = new Boundary[] {
		new Boundary(2838, 3536, 2846, 3542, 2),
		new Boundary(2847, 3537, 2847, 3537, 2)
	};
	
	private Player player;
	
	private boolean active;
	
	public static final int[][] DEFENDER_DATA = {
		{8844, 10},
		{8845, 15},
		{8846, 20},
		{8848, 25},
		{8849, 30},
		{8850, 35},
		{12954, 50}
	};
	
	public WarriorsGuild(Player player) {
		this.player = player;
	}
	
	public void cycle() {
		CycleEventHandler.getSingleton().stopEvents(this);
		setActive(true);
		CycleEventHandler.getSingleton().addEvent(this, new CycleEvent() {

			@Override
			public void execute(CycleEventContainer event) {
				if(player == null || player.properLogout) {
					event.stop();
					return;
				}
				if(!player.getItems().playerHasItem(8851, 10)) {
					removeFromRoom();
					setActive(false);
					event.stop();
					return;
				}
				if(!Boundary.isIn(player, CYCLOPS_BOUNDARY) || Boundary.isIn(player, WAITING_ROOM_BOUNDARY)) {
					setActive(false);
					event.stop();
					return;
				}
				player.getItems().deleteItem(8851, 20);
				player.getActionSender().sendMessage("You notice some of your warrior guild tokens dissapear..");
			}

			@Override
			public void stop() {
				
			}
			
		}, 100);
	}
	
	public void handleDoor() {
		if(player.absX == 2847 && player.absY == 3540 || player.absX == 2847 && player.absY == 3541) {
			CycleEventHandler.getSingleton().stopEvents(this);
			player.move(new Position(player.absX - 1, player.absY, 2));
		} else if(player.absX == 2846 && player.absY == 3540 || player.absX == 2846 && player.absY == 3541 || Boundary.isIn(player, WAITING_ROOM_BOUNDARY)) {
			if(player.getItems().playerHasItem(8851, 200)) {
				int current = currentDefender();
				if (current == -1) {
					player.dialogue().start("NO_DEFENDER");
				} else {
					player.dialogue().start("HAS_DEFENDER");
				}
			} else {
				player.dialogue().start("PLAYER_HAS_NO_TOKENS");
			}
		}
	}
	
	/**
	 * Attempts to return the value of the defender the player is wearing or is in posession of
	 * in their inventory. 
	 * @return	-1 will be returned in the case that the player does not have a defender
	 */
	public int currentDefender() {
		for(int index = DEFENDER_DATA.length - 1; index > -1; index--) {
			int[] defender = DEFENDER_DATA[index];
			if (player.getItems().playerHasItem(defender[0]) || player.getItems().isWearingItem(defender[0])) {
				return defender[0];
			}
		}
		return -1;
	}
	
	/**
	 * Attempts to return the next best defender.
	 * @return	The first defender, bronze, if the player doesnt have a defender.
	 * If the player has the best it will return the best. If either of the afforementioned
	 * conditions are not met, the next best defender is returned. 
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
	 * @param defender	the defender
	 * @return	-1 will be returned if the defender cannot be found
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
	 * Retrieves the drop chance of the next best defender the player
	 * can receive.
	 * @return	the chance of the dropped dagger.
	 */
	private int chance() {
		Optional<int[]> defender = Arrays.asList(DEFENDER_DATA).stream().filter(data -> data[0] == nextDefender()).findFirst();
		return defender.isPresent() ? defender.get()[1] : 0;
	}
	
	public void dropDefender(int x, int y) {
		int amount = player.getItems().getItemAmount(8851);
		if(isActive() && Boundary.isIn(player, CYCLOPS_BOUNDARY) && !Boundary.isIn(player, WAITING_ROOM_BOUNDARY) && amount > 1) {
			int chance = chance();
			int current = currentDefender();
			int item = current == -1 ? DEFENDER_DATA[0][0] : nextDefender();
			if (Utility.random(chance) == 0) {
				GroundItemHandler.createGroundItem(new GroundItem(new Item(item, 1), x, y, 2, player));
				player.getActionSender().sendMessage("The cyclops dropped a "+player.getItems().getItemName(item)+" on the ground.");
			}
		}
	}
	
	public void removeFromRoom() {
		if (Boundary.isIn(player, CYCLOPS_BOUNDARY)) {
			player.move(new Position(2846, 3540, 2));
			player.dialogue().start("OUT_OF_TOKENS");
		}
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

}
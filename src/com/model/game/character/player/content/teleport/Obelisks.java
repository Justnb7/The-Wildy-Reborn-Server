package com.model.game.character.player.content.teleport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.model.Server;
import com.model.game.World;
import com.model.game.character.player.Boundary;
import com.model.game.character.player.Player;
import com.model.game.location.Location;
import com.model.game.object.GlobalObject;
import com.model.task.events.CycleEvent;
import com.model.task.events.CycleEventContainer;
import com.model.task.events.CycleEventHandler;
import com.model.utility.Utility;



/**
 * Obelisks are the objects that exist in the wilderness that aid player teleportation.
 * Once activated, any player within the obelisk boundary is moved to another obelisk.
 * 
 * @author Jason MacKeigan
 * @date Jan 3, 2015, 11:54:03 PM
 * 
 * @author Patrick van Elderen
 * @date Jan 20, 2017, 20:52:24 PM
 */
public class Obelisks {
	
	/**
	 * The state of each obelisk, if they are either active or inactive.
	 */
	private static Map<Integer, Boolean> state = new HashMap<>();
	
	/**
	 * A single instance of this class
	 */
	private static Obelisks INSTANCE = new Obelisks();
	
	/**
	 * Returns the single instance of the Obelisks class
	 * @return the instance
	 */
	public static Obelisks get() {
		return INSTANCE;
	}
	
	/**
	 * Stores the obelisk object ids with the default state, false, in a map.
	 */
	static {
		for (Obelisk location : Obelisk.values()) {
			state.put(location.objectId, false);
		}
	}
	
	/**
	 * The function used to activate a obelisk
	 * @param player	the player attempting to activate a obelisk
	 * @param objectId	the object id of the obelisk
	 */
	public void activate(Player player, int objectId) {
		Obelisk location = Obelisk.forObject(objectId);
		if (location == null || player == null) {
			return;
		}
		boolean active = state.get(objectId);
		if (CycleEventHandler.getSingleton().isAlive(location) || active) {
			player.getActionSender().sendMessage("The obelisk is already active, please wait.");
			return;
		}
		if (player.teleTimer > 0) {
			player.getActionSender().sendMessage("You cannot do this whilst teleporting.");
			return;
		}
		player.usingObelisk = true;
		state.put(objectId, true);
		int x = location.getBoundaries().getMinimumX();
		int y = location.getBoundaries().getMinimumY();
		Server.getGlobalObjects().add(new GlobalObject(14825, x, y, 0, 0, 10, 14, objectId));
		Server.getGlobalObjects().add(new GlobalObject(14825, x + 4, y, 0, 0, 10, 14, objectId));
		Server.getGlobalObjects().add(new GlobalObject(14825, x, y + 4, 0, 0, 10, 14, objectId));
		Server.getGlobalObjects().add(new GlobalObject(14825, x + 4, y + 4, 0, 0, 10, 14, objectId));
		CycleEventHandler.getSingleton().addEvent(location, new Event(location), 14);
	}
	
	public enum Obelisk {
		LEVEL_13(14829, new Boundary(3154, 3618, 3158, 3622), 148224),
		LEVEL_19(14830, new Boundary(3225, 3665, 3229, 3669), 148225),
		LEVEL_27(14827, new Boundary(3033, 3730, 3037, 3734), 148226),
		LEVEL_35(14828, new Boundary(3104, 3792, 3108, 3796), 148227),
		LEVEL_44(14826, new Boundary(2978, 3864, 2982, 3868), 148228),
		LEVEL_50(14831, new Boundary(3305, 3914, 3309, 3918), 148229);
		
		private int objectId;
		private Boundary boundary;
		private int interfaceButton;
		
		private Obelisk(int objectId, Boundary boundary, int interfaceButton) {
			this.objectId = objectId;
			this.boundary = boundary;
			this.interfaceButton = interfaceButton;
		}
		
		public int getObjectId() {
			return objectId;
		}
		
		public Boundary getBoundaries() {
			return boundary;
		}
		
		public int getButtonId() {
			return interfaceButton;
		}
		
		static Obelisk forObject(int objectId) {
			for (Obelisk l : values()) {
				if (l.objectId == objectId) {
					return l;
				}
			}
			return null;
		}
		
		static Obelisk getObeliskByButton(int button) {
			for(Obelisk i : values()) {
				if(i.interfaceButton == button) {
					return i;
				}
			}
			return null;
		}
		
		static Obelisk getRandom(Obelisk exclude) {
			ArrayList<Obelisk> locations = new ArrayList<>(Arrays.asList(values()));
			locations.remove(exclude);
			return locations.get(Utility.random(locations.size() - 2));
		}
		
	}
	
	static final class Event extends CycleEvent {
		
		private Obelisk location;
		
		public Event(Obelisk location) {
			this.location = location;
		}

		@Override
		public void execute(CycleEventContainer container) {
					state.put(location.objectId, false);
					container.stop();
					Boundary boundary = new Boundary(location.boundary.getMinimumX() + 1, location.boundary.getMinimumY() + 1, location.boundary.getMinimumX() + 3, location.boundary.getMinimumY() + 3);
					List<Player> players = World.getWorld().getPlayers().stream().filter(Objects::nonNull).filter(player -> Boundary.isIn(player, boundary)).collect(Collectors.toList());
					if (players.size() > 0) {
						Obelisk randomObelisk = Obelisk.getRandom(location);
						int x = randomObelisk.getBoundaries().getMinimumX() + 1;
						int y = randomObelisk.getBoundaries().getMinimumY() + 1;
						players.forEach(player -> TeleportExecutor.teleport(player, new Location(x + Utility.getRandom(2), y + Utility.getRandom(2), 0)));
						for (Player p : World.getWorld().getPlayers()) {
							if (p != null) {
								p.usingObelisk = false;
					}
				}
			}
		}
	}
	
	public static void openInterface(Player player) {
		int line = 38111;
		for (String strings : sendTextToInterface) {
			line++;			
			player.getActionSender().sendString("<col=46320a>" + strings, line);
		}
		player.getActionSender().sendInterface(38100);
	}
	
	
	public static void chooseTeleport(Player player, int button) {
		openInterface(player);
		Optional<Obelisk> obelisk = Arrays.asList(Obelisk.values()).stream().filter(x -> x.interfaceButton == button).findAny();
       
		/**
         * Safety checks
         */
		if(!obelisk.isPresent()) {
            return;
        }
        
        Obelisk teleport = obelisk.get();
        
		if(button == teleport.getButtonId()) {
			switch(button) {
			case 148224:

				break;
				
			}
		}
	}
	
	public static final String[] sendTextToInterface = { "Level 44 wilderness", "Level 27 wilderness",
			"Level 35 wilderness", "Level 13 wilderness", "Level 19 wilderness", "Level 50 wilderness" };


	public static void setTeleport() {
		
	}

}
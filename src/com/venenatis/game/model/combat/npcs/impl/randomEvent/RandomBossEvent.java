package com.venenatis.game.model.combat.npcs.impl.randomEvent;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.venenatis.game.location.Area;
import com.venenatis.game.location.Location;
import com.venenatis.game.model.Item;
import com.venenatis.game.model.combat.npcs.impl.randomEvent.impl.DarkVenenatis;
import com.venenatis.game.model.entity.npc.NPC;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.util.Utility;
import com.venenatis.game.world.World;
import com.venenatis.game.world.ground_item.GroundItem;
import com.venenatis.game.world.ground_item.GroundItemHandler;

public class RandomBossEvent extends RandomEvent {
	
	private enum SpawnLocations {
		MAGE_BANK("The mage arena", new Location(3113, 3960, 0)),
		EDGEVILE("Edgevile Wilderness", new Location(3036, 3538, 0)),
		GREEN_DRAGONS("The Green dragons", new Location(3367, 3644, 0)),
		;
		
		private String locationName;
		private Location spawnLocation;
		
		private SpawnLocations(String locationName, Location spawnLocation) {
			this.locationName = locationName;
			this.spawnLocation = spawnLocation;
		}

		/**
		 * Gets the locationName.
		 * @return the locationName
		 */
		public String getLocationName() {
			return locationName;
		}

		/**
		 * Gets the spawnLocation.
		 * @return the spawnLocation
		 */
		public Location getSpawnLocation() {
			return spawnLocation;
		}
		
		private static final List<SpawnLocations> VALUES = Collections.unmodifiableList(Arrays.asList(values()));
		private static final int SIZE = VALUES.size();
		private static final Random RANDOM = new Random();

		/**
		 * Gets a random location.
		 * @return the location data.
		 */
		public static SpawnLocations getRandomLocation()  {
			return VALUES.get(RANDOM.nextInt(SIZE));
		}
	}
	
	//Fields.
	private NPC boss;
	SpawnLocations location = SpawnLocations.getRandomLocation();
	
	@Override
	public boolean start() {	
		int random = Utility.random(3);	
		switch (random) {
			//Dark Venenatis
			case 1:
				boss = new DarkVenenatis(location.getSpawnLocation());
				break;
				
			default:
				boss = new DarkVenenatis(location.getSpawnLocation());
		}
		sendMessage("an " + boss.getDefinition().getName() + " has spawned near " + location.getLocationName() + "!");
		return true;
	}

	@Override
	public boolean preStartupCheck() {
		if (boss != null)
			return true;
		System.err.println("---[EVENT]--- Boss has spawned null.");
		return false;
	}

	private int duration;
	
	@Override
	public int process() {
		duration++;
		if (boss.getCombatState().isDead()) {
			sendMessage("The event is now over, the boss has been killed.");
			Player killer = World.getWorld().lookupPlayerByName(boss.getCombatState().getDamageMap().getKiller());
			if (killer.getLocation().inBossEvent() && killer != null && boss != null) {
				if(Area.inWilderness(killer)) {
					GroundItemHandler.createGroundItem(new GroundItem(new Item(12746, 1), boss.getLocation().clone(), killer));
				}
				GroundItemHandler.createGroundItem(new GroundItem(new Item(2944, 1), boss.getLocation().clone(), killer));
				killer.getActionSender().sendMessage("Use the Key on the Event Chest at home to receive your rewards!");
			}
			return -1; //Ends event.
		}
		if (duration >= 3000) {
			sendMessage("The event is now over, the boss has disappeared.");
			stop();
			return -1;
		}
		return 1;
	}

	@Override
	public void stop() {
		if (!boss.getCombatState().isDead()) {
			boss.remove(boss);
		}	
	}
}
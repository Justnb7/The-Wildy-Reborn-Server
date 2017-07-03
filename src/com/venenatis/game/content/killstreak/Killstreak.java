package com.venenatis.game.content.killstreak;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.venenatis.game.model.Item;
import com.venenatis.game.model.entity.player.Player;

/**
 * 
 * The class which represents the killstreaks, there are two types of
 * killstreak. One wilderness streak where if you leave the wilderness it will
 * be reset. And the normal killstreak that stacks untill you die.
 * 
 * @author <a href="http://www.rune-server.org/members/_Patrick_/">Patrick van  Elderen</a>
 *
 */
public class Killstreak {
	
	/**
	 * A mapping of the different killstreaks the player has
	 */
	private Map<Type, Integer> killstreaks = new HashMap<>();

	/**
	 * The player receiving the killstreaks
	 */
	private Player player;

	/**
	 * Creates a new object that will manage player killstreaks
	 * 
	 * @param player the player
	 */
	public Killstreak(Player player) {
		this.player = player;
	}

	/**
	 * Resets the type of killstreak for the player
	 * 
	 * @param type the type of killstreak
	 */
	public void reset(Type type) {
		if (killstreaks.containsKey(type)) {
			killstreaks.remove(type);
		}
	}

	/**
	 * Resets all player killstreaks. This is generally called when a player dies during PVP combat.
	 */
	public void resetAll() {
		killstreaks.clear();
	}

	/**
	 * Increases the killstreak for the specific type
	 * 
	 * @param type the type of killstreak
	 */
	public void increase(Type type) {
		int value = 1 + killstreaks.getOrDefault(type, 0);
		killstreaks.put(type, value);
		reward(type);
	}

	/**
	 * Rewards the player with some item, points, and or some other form of currency.
	 * 
	 * @param type the type of killstreak
	 */
	private void reward(Type type) {
		int streak = killstreaks.getOrDefault(type, 0);
		Optional<KillstreakReward> reward = Arrays.asList(type.rewards).stream().filter(s -> s.killstreak == streak).findFirst();
		if (reward.isPresent()) {
			reward.get().append(player);
		}
		if (streak >= type.maximumKillstreak) {
			//TODO max reward stop giving out rewards
		}
	}

	/**
	 * The amount of killstreaks a player has for the type of killstreak
	 * 
	 * @param type the type of killstreak
	 * @return zero will be returned if there is no mapping for the killstreak, otherwise the value for the killstreak mapping will be returned.
	 */
	public int getAmount(Type type) {
		return killstreaks.getOrDefault(type, 0);
	}

	/**
	 * A mapping of all of the killstreaks
	 * 
	 * @return a mapping
	 */
	public Map<Type, Integer> getKillstreaks() {
		return killstreaks;
	}

	/**
	 * Returns the sum of all killstreaks.
	 * 
	 * @return The sum of all killstreaks.
	 */
	public int getTotalKillstreak() {
		int total = 0;
		for (Type type : Type.values()) {
			total += getAmount(type);
		}
		return total;
	}

	/**
	 * There are several different types of killstreaks. Allowing early support for different types of killstreaks will allow for the addition of more in the future without having
	 * to do an overhaul.
	 */
	public enum Type {
		ROGUE(10, new KillstreakReward(2) {

			@Override
			public void append(Player player) {
				player.getInventory().addOrCreateGroundItem(new Item(13307, 1));
				player.getActionSender().sendMessage("You are on a 2 killstreak, you have been given 1 extra blood money.");
			}

		}, new KillstreakReward(3) {

			@Override
			public void append(Player player) {
				player.getInventory().addOrCreateGroundItem(new Item(13307, 2));
				player.getActionSender().sendMessage("You are on a 3 killstreak, you have been given 2 extra blood money.");
			}

		}, new KillstreakReward(4) {

			@Override
			public void append(Player player) {
				player.getInventory().addOrCreateGroundItem(new Item(13307, 1));
				player.getActionSender().sendMessage("You are on a 4 killstreak, you have been given 3 extra blood money.");
			}

		}, new KillstreakReward(5) {

			@Override
			public void append(Player player) {
				player.getInventory().addOrCreateGroundItem(new Item(13307, 1));
				player.getActionSender().sendMessage("You are on a 5 killstreak, you have been given 4 extra blood money.");
			}
		}, new KillstreakReward(6) {

			@Override
			public void append(Player player) {
				player.getInventory().addOrCreateGroundItem(new Item(13307, 5));
				player.getActionSender().sendMessage("You are on a 6 killstreak, you have been given 5 extra blood money.");
			}
		}, new KillstreakReward(7) {

			@Override
			public void append(Player player) {
				player.getInventory().addOrCreateGroundItem(new Item(13307, 6));
				player.getActionSender().sendMessage("You are on a 7 killstreak, you have been given 6 extra blood money.");
			}
		}, new KillstreakReward(8) {

			@Override
			public void append(Player player) {
				player.getInventory().addOrCreateGroundItem(new Item(13307, 10));
				player.getActionSender().sendMessage("You are on a 10 killstreak, you have been given 10 extra blood money.");
			}
		}, new KillstreakReward(9) {

			@Override
			public void append(Player player) {
				player.getInventory().addOrCreateGroundItem(new Item(13307, 12));
				player.getActionSender().sendMessage("You are on a 2 killstreak, you have been given 1122 extra blood money.");
			}
		}, new KillstreakReward(10) {

			@Override
			public void append(Player player) {
				player.getInventory().addOrCreateGroundItem(new Item(13307, 15));
				player.getActionSender().sendMessage("You are on a 15 killstreak, you have been given 15 extra blood money.");
			}
		}), HUNTER(10, new KillstreakReward(2) {

			@Override
			public void append(Player player) {
				
			}

		}, new KillstreakReward(3) {

			@Override
			public void append(Player player) {
				
			}

		}, new KillstreakReward(4) {

			@Override
			public void append(Player player) {
				
			}

		}, new KillstreakReward(5) {

			@Override
			public void append(Player player) {
				
			}

		}, new KillstreakReward(6) {

			@Override
			public void append(Player player) {
				
			}

		}, new KillstreakReward(7) {

			@Override
			public void append(Player player) {
				
			}

		}, new KillstreakReward(8) {

			@Override
			public void append(Player player) {
				
			}

		}, new KillstreakReward(9) {

			@Override
			public void append(Player player) {
				
			}

		}, new KillstreakReward(10) {

			@Override
			public void append(Player player) {
				
			}

		});

		private int maximumKillstreak;
		private KillstreakReward[] rewards;

		private Type(int maximumKillstreak, KillstreakReward... rewards) {
			this.maximumKillstreak = maximumKillstreak;
			this.rewards = rewards;
		}

		public static Type get(String name) {
			Optional<Type> op = Arrays.asList(values()).stream().filter(t -> t.name().equals(name)).findFirst();
			return op.orElse(null);
		}

	}

}

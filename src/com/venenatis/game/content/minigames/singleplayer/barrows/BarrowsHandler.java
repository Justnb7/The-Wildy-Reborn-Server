package com.venenatis.game.content.minigames.singleplayer.barrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import com.venenatis.game.location.Location;
import com.venenatis.game.model.Item;
import com.venenatis.game.model.boudary.Boundary;
import com.venenatis.game.model.entity.npc.NPC;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.world.World;
import com.venenatis.game.world.object.GameObject;

/**
 * Handles most actions that have to do with the Barrows minigame.
 * 
 * @author Stan
 * @author Lennard
 *
 */
public class BarrowsHandler {

	/**
	 * The "Random Number Generator" of this class.
	 */
	private final static Random random = new Random();
	
	public static Random getRandom() {
		return random;
	}

	/**
	 * The identifier of the Dharok Barrows NPC.
	 */
	public static final int DHAROK = 0;

	/**
	 * The identifier of the Guthan Barrows NPC.
	 */
	public static final int GUTHAN = 1;

	/**
	 * The identifier of the Verac Barrows NPC.
	 */
	public static final int VERAC = 2;

	/**
	 * The identifier of the Torag Barrows NPC.
	 */
	public static final int TORAG = 3;

	/**
	 * The identifier of the Ahrim Barrows NPC.
	 */
	public static final int AHRIM = 4;

	/**
	 * The identifier of the Karil Barrows NPC.
	 */
	public static final int KARIL = 5;

	/**
	 * 2 dimensional array holding barrows equipment with their respective
	 * barrows brother identifier, see {@link BarrowsInformation} for details.
	 */
	private static final int[][] BARROWS_EQUIPMENT = new int[][] { { 4716, 4718, 4720, 4722 },
		{ 4724, 4726, 4728, 4730 }, { 4753, 4755, 4757, 4759 }, { 4745, 4747, 4749, 4751 },
		{ 4708, 4710, 4712, 4714 }, { 4732, 4734, 4736, 4738 } };

	/**
	 * The object Id of the barrows reward chest.
	 */
	private static final int BARROWS_CHEST_OBJECT_ID = 20973;
	
	/**
	 * The start ID of the puzzle models
	 */
	private static final int[] PUZZLES = new int[] { 6713, 6719, 6725, 6731 };
	
	/**
	 * The locations of the spots you teleport to when entering a tunnel
	 */
	public static final Location[] TUNNEL_LOCATIONS = new Location[] {
			new Location(3552, 9667, 0),
			new Location(3524, 9695, 0),
			new Location(3551, 9722, 0),
			new Location(3579, 9694, 0)
	};

	/**
	 * The single instance of the BarrowsHandler class.
	 */
	private static final BarrowsHandler SINGLETON = new BarrowsHandler();

	/**
	 * Handles object clicks for the barrows object interactions.
	 * 
	 * @param player
	 *            The Player performing this action.
	 * @param objectId
	 *            The objectId that has been clicked on.
	 * @return {@code true} if the action has been successfully executed,
	 *         {@code false} otherwise.
	 */
	public boolean handleObjectClick(final Player player, final GameObject object) {
		if (BarrowsDoors.handleDoor(player, object)) {
			return true;
		}
		
		/*if(player.getBarrowsDetails().getSpawnedBrother() > 0) {
			player.message("already spawned.");
			//This doesn't work either because it will no longer spawn other barrows borthers
			return false;
		}*/
		
		if (World.getWorld().getNPCs().get(player.getBarrowsDetails().getSpawnedBrother()) != null) {
			return false;
		}
		
		int objectId = object.getId();
		if (objectId == BARROWS_CHEST_OBJECT_ID) {
			final Optional<BarrowsInformation> info = BarrowsInformation.forBrotherIdentifier(player.getBarrowsDetails().getTunnelLocation());
			if (!info.isPresent()) {
				return true;
			}
			if (player.getBarrowsDetails().getBrothersKilled()[player.getBarrowsDetails().getTunnelLocation()]) {
				giveReward(player);
			} else {
				NPC.spawnNpc(player, info.get().getNpcId(), player.getLocation(), 0, true, true);
			}
			return true;
		} else {
			final Optional<BarrowsInformation> info = BarrowsInformation.forObjectId(objectId);
			if (!info.isPresent()) {
				return false;
			}
			if (player.getCombatState().getUnderAttackBy() != null) {
				return false;
			}
			final BarrowsInformation brother = info.get();
			if (player.getBarrowsDetails().getTunnelLocation() == brother.getIdentifier()) {
				player.getDialogueManager().start("BARROS_TUNNEL");
				return true;
			}
			if (player.getBarrowsDetails().getBrothersKilled()[brother.getIdentifier()]) {
				player.getActionSender().sendMessage("It's empty....");
				return true;
			}
			player.getBarrowsDetails().setSpawnedBrother(NPC.spawnNpc(player, brother.getNpcId(), new Location(player.getX(), player.getY(), player.getZ()), 1, true, true).getIndex());
			return true;
		}
	}

	/**
	 * Gets the amount of brothers that the Player has killed.
	 * 
	 * @param player
	 *            The Player performing this action.
	 * @return The amount of brothers that the Player has killed.
	 */
	private int getBrothersKilled(final Player player) {
		int count = 0;
		for (boolean killed : player.getBarrowsDetails().getBrothersKilled()) {
			if (killed) {
				count++;
			}
		}
		return count;
	}

	/**
	 * Updates the Player settings when killing a brother.
	 * 
	 * @param player
	 *            The Player performing this action.
	 * @param barrowsIdentifier
	 *            The identifier of the barrows brother. See
	 *            {@link BarrowsInformation} for details.
	 */
	public void killBrother(final Player player, final int barrowsIdentifier) {
		if (player == null) {
			return;
		}
		player.getBarrowsDetails().getBrothersKilled()[barrowsIdentifier] = true;
		player.getBarrowsDetails().increaseCryptKillCount();
		updateOverlayInterface(player);
	}
	
	/**
	 * Opens a puzzle interface
	 * 
	 * @param player
	 *            The Player performing this action.
	 */
	public void openPuzzle(final Player player) {
		if (player == null) {
			return;
		}
		
		int puzzleStartId = PUZZLES[random.nextInt(4)];
		
		List<Integer> answers = new ArrayList<>(Arrays.asList(puzzleStartId, puzzleStartId + 1, puzzleStartId + 2));
		Collections.shuffle(answers);
		
		player.getActionSender().sendMediaInterface(4545, puzzleStartId + 3);
		player.getActionSender().sendMediaInterface(4546, puzzleStartId + 4);
		player.getActionSender().sendMediaInterface(4547, puzzleStartId + 5);
		
		Iterator<Integer> answersIterator = answers.iterator();

		for (int i = 0; answersIterator.hasNext(); i++) {
			int answer = answersIterator.next();
			player.getActionSender().sendMediaInterface(4550 + i, answer);
			if (answer == puzzleStartId)
				player.setPuzzleAnswer(i);
		}
		
		player.getActionSender().sendInterface(4543);
	}
	
	/**
	 * Answers the barrows puzzle
	 * 
	 * @param player
	 *            The Player performing this action.
	 * @param answer
	 * 			  The chosen answer
	 */
	public void answerPuzzle(final Player player, final int answer) {
		player.getActionSender().removeAllInterfaces();
		if (player.getPuzzleLocation() != null && player.getPuzzleLocation().distanceToPoint(player.getLocation()) <= 2) {
			if (answer == player.getPuzzleAnswer()) {
				player.setTeleportTarget(player.getPuzzleLocation());
				player.getActionSender().sendMessage("You hear the doors' locking mechanism grind open.");
				if (player.getBarrowsDetails().getSpawnedBrother() > 0) {
					if (World.getWorld().getNPCs().get(player.getBarrowsDetails().getSpawnedBrother()) != null) {
						World.getWorld().getNPCs().get(player.getBarrowsDetails().getSpawnedBrother()).remove();
					}
					player.getBarrowsDetails().setSpawnedBrother(-1);
				}
			} else {
				player.getActionSender().sendMessage("The chosen shape seems to be incorrect...");
			}
		}
	}
	

	/**
	 * Updates the Strings on the Barrows overlay interface.
	 * 
	 * @param player
	 *            The Player performing this action.
	 */
	public void updateOverlayInterface(final Player player) {
		player.getActionSender().sendString("Kill Count: " + player.getBarrowsDetails().getCryptKillCount(), 4536);
	}

	/**
	 * Gives the Player the reward after completing a barrows run. Also displays
	 * the interface and resets the barrows details.
	 * 
	 * @param player
	 *            The Player performing this action.
	 */
	public void giveReward(final Player player) {
		player.setTeleportTarget((new Location(3565, 3316, 0)));
		final Collection<Item> rewards = getRewards(player);
		player.getActionSender().sendItemsOnInterface(42006, rewards);
		player.getActionSender().sendInterface(42000);
		if (!rewards.isEmpty()) {
			for (Item item : rewards) {
				if (item == null) {
					continue;
				}
				player.getInventory().add(item);
			}
		}
		player.getBarrowsDetails().finishGame();
		player.getActionSender().sendMessage("Your Barrows chest count is: @red@" + player.getBarrowsDetails().getChestsOpened() + ".");
		BarrowsHandler.getSingleton().updateOverlayInterface(player);
	}

	/**
	 * Creates a {@link Collection} of {@link Item}s holding all the calculated
	 * rewarded items.
	 * 
	 * @param player
	 *            The Player performing this action.
	 * @return Collection of Items holding all the calculated rewarded items.
	 */
	public Collection<Item> getRewards(final Player player) {
		final Collection<Item> rewards = new ArrayList<Item>();
		
		BarrowsDetails details = player.getBarrowsDetails();
		
		int brothersKilled = getBrothersKilled(player);
		int chance = details.getCryptCombatKill();
		if (chance > 1000) chance = 1000;
		chance += brothersKilled * 2;
		
		for (int i = 0; i < brothersKilled; i++) {
			if (random.nextInt(450 - 58 * brothersKilled) == 0) {
				List<Integer> possibleRewards = new ArrayList<Integer>();
				for (int j = 0; j < 6; j++) {
					if (details.getBrothersKilled()[j]) {
						for (int item : BARROWS_EQUIPMENT[j]) {
							if (!possibleRewards.contains(item)) {
								possibleRewards.add(item);
							}
						}
					}
				}
				Collections.shuffle(possibleRewards);
				rewards.add(new Item(possibleRewards.get(0)));
			}
		}
		
		int itemRolls = 6 - rewards.size();
		int totalRoll = 0;
		List<BarrowsRewards> possibleRewards = BarrowsRewards.getPossibleRewards(chance);
		for (BarrowsRewards reward : possibleRewards) {
			totalRoll += reward.getChance();
		}
		
		if (totalRoll > 0) {
			for (int i = 0; i < itemRolls; i++) {
				int randomRoll = random.nextInt((int) (totalRoll * 1.75));
				int totalChance = 0;
				for (BarrowsRewards reward : possibleRewards) {
					totalChance += reward.getChance();
					if (randomRoll < totalChance) {
						int maxQuantity = reward.getMaximumQuantity();
						if (reward.getQuantityRequirement() > chance) {
							maxQuantity *= 0.5;
						}
						int amount = random.nextInt(maxQuantity) + 1;
						Iterator<Item> rewardsIterator = rewards.iterator();
						while (rewardsIterator.hasNext()) {
							Item item = rewardsIterator.next();
							if (item.getId() == reward.getItemId()) {
								amount += item.getAmount();
								rewardsIterator.remove();
								break;
							}
						}
						rewards.add(new Item(reward.getItemId(), amount));
						break;
					}
				}
			}
		}
		
		return rewards;
	}

	/**
	 * Checks if the Player is within the Barrows boundaries. Used to send the
	 * interface overlay.
	 * 
	 * @param player
	 *            The Player performing this action.
	 * @return {@code true} if the player is within the Barrows boundaries,
	 *         {@code false} otherwise.
	 */
	public boolean withinBarrows(final Player player) {
		final Boundary barrowsBoundary = new Boundary("barrows_boundary", new Location(3541, 3265),
				new Location(3584, 3316));
		final Boundary cryptBoundary = new Boundary("crypt_boundary", new Location(3510, 9660),
				new Location(3600, 9740));
		return barrowsBoundary.isIn(player) || cryptBoundary.isIn(player);
	}
	
	public boolean withinCrypt(final Player player) {
		final Boundary cryptBoundary = new Boundary("crypt_boundary", new Location(3510, 9660),
				new Location(3600, 9740));
		return cryptBoundary.isIn(player);
	}

	/**
	 * Gets the single instance of the BarrowsHandler class.
	 * 
	 * @return The single instance of the BarrowsHandler class.
	 */
	public static BarrowsHandler getSingleton() {
		return SINGLETON;
	}

}
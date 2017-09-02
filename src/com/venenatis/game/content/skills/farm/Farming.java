package com.venenatis.game.content.skills.farm;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import com.venenatis.game.content.achievements.AchievementHandler;
import com.venenatis.game.content.achievements.AchievementList;
import com.venenatis.game.location.Location;
import com.venenatis.game.model.Item;
import com.venenatis.game.model.Skills;
import com.venenatis.game.model.entity.npc.pet.Pet;
import com.venenatis.game.model.entity.npc.pet.Pets;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.dialogue.SimpleDialogues;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.task.Task;
import com.venenatis.game.task.Task.BreakType;
import com.venenatis.game.task.Task.StackType;
import com.venenatis.game.util.Utility;
import com.venenatis.game.world.World;

/**
 * 
 * @author Jason http://www.rune-server.org/members/jason
 * @date Oct 27, 2013
 */

public class Farming {
	
	public static int[] farmersOutfit = { 13640, 13642, 13644, 13646 };

	public static final int MAX_PATCHES = 1;

	private Player player;

	private int weeds;

	private long lastPoisonBerryFarm;

	public Farming(Player player) {
		this.player = player;
	}
	
	private boolean hasMagicSecateurs() {
		return player.getInventory().contains(7409) || player.getEquipment().contains(7409, 3);
	}

	public void patchObjectInteraction(final int objectId, final int itemId, Location location) {
		
		/**
		 * Skilling outfit pieces
		 */
		int pieces = 0;
		for (int i = 0; i < farmersOutfit.length; i++) {
			if (player.getInventory().contains(farmersOutfit[i])) {
				pieces++;
			}
		}
		
		Patch patch = Patch.get(location.getX(), location.getY());
		if (patch == null)
			return;
		final int id = patch.getId();

		if (objectId == FarmingConstants.GRASS_OBJECT || objectId == FarmingConstants.HERB_PATCH_DEPLETED) {
			if (player.getFarmingState(id) < State.RAKED.getId()) {
				if (!player.getInventory().contains(FarmingConstants.RAKE, 1))
					player.getActionSender().sendMessage("You need to rake this patch to remove all the weeds.");
				else if (itemId == FarmingConstants.RAKE || player.getInventory().contains(FarmingConstants.RAKE)) {
					player.playAnimation(new Animation(FarmingConstants.RAKING_ANIM));
			
					if (weeds <= 0)
						weeds = 3;
					World.getWorld().schedule(new Task(player, 3, false, StackType.NEVER_STACK, BreakType.ON_MOVE) {
						
						public void execute() {
							if (player == null) {
								stop();
								return;
							}
							if (weeds > 0) {
								weeds--;
						
								player.getInventory().add(6055, 1);
								player.playAnimation(new Animation(FarmingConstants.RAKING_ANIM));
								switch(weeds) {
								case 3:
									player.getActionSender().sendConfig(529, 0);
									break;
								case 2:
									player.getActionSender().sendConfig(529, 1);
									break;
								case 1:
									player.getActionSender().sendConfig(529, 2);
									break;
								case 0:
									player.getActionSender().sendConfig(529, 3);
									break;
								}
							} else if (weeds == 0) {
								player.setFarmingState(id, State.RAKED.getId());
								player.getActionSender().sendMessage("You raked the patch of all it's weeds, now the patch is ready for compost.", 255);
								player.playAnimation(new Animation(65535));
								updateObjects();
								stop();
							}
						}
					});
				}
			} else if (player.getFarmingState(id) >= State.RAKED.getId() && player.getFarmingState(id) < State.COMPOST.getId()) {
				if (!player.getInventory().contains(FarmingConstants.COMPOST, 1))
					player.getActionSender().sendMessage("You need to put compost on this to enrich the soil.");
				else if (itemId == FarmingConstants.COMPOST || player.getInventory().contains(FarmingConstants.COMPOST) && itemId == -1) {
			
					player.playAnimation(new Animation(FarmingConstants.PUTTING_COMPOST));
					player.getInventory().remove(FarmingConstants.COMPOST, 1);
					player.getInventory().add(1925, 1);
					player.setFarmingState(id, State.COMPOST.getId());
					player.getActionSender().sendMessage("You put compost on the soil, it is now time to seed it.");
				}
			} else if (player.getFarmingState(id) >= State.COMPOST.getId() && player.getFarmingState(id) < State.SEEDED.getId()) {
				if (!player.getInventory().contains(FarmingConstants.SEED_DIBBER, 1)) {
					player.getActionSender().sendMessage("You need to use a seed dibber with a seed on this patch.");
					return;
				}
				final FarmingHerb.Herb herb = FarmingHerb.getHerbForSeed(itemId);
				if (herb == null) {
					player.getActionSender().sendMessage("You must use an appropriate seed on the patch at this stage.");
					return;
				}
				if (player.getSkills().getXPForLevel(Skills.FARMING) < herb.getLevelRequired()) {
					player.getActionSender().sendMessage("You need a farming level of " + herb.getLevelRequired() + " to grow " + herb.getSeedName().replaceAll(" seed", "") + ".");
					return;
				}
				if (itemId == herb.getSeedId() && player.getInventory().contains(FarmingConstants.SEED_DIBBER)) {
			
					player.playAnimation(new Animation(FarmingConstants.SEED_DIBBING));
					/**
					 * Calculate experience
					 */
					double osrsExperience = herb.getPlantingXp() + herb.getPlantingXp() / 20 * pieces;
					World.getWorld().schedule(new Task(player, 3, false, StackType.NEVER_STACK, BreakType.ON_MOVE) {

						public void execute() {
							if (player == null || !player.isActive()) {
								stop();
								return;
							}
							if (!player.getInventory().contains(herb.getSeedId()))
								return;
							player.getInventory().remove(herb.getSeedId(), 1);
							player.setFarmingState(id, State.SEEDED.getId());
							player.setFarmingSeedId(id, herb.getSeedId());
							player.setFarmingTime(id, hasMagicSecateurs() ? herb.getGrowthTime() / 2 : herb.getGrowthTime());
							player.setFarmingHarvest(id, 3 + Utility.random(hasMagicSecateurs() ? 7 : 4));
							player.getSkills().addExperience(Skills.FARMING, osrsExperience);
							player.getActionSender().sendMessage("You dib a seed into the soil, it is now time to water it.");
							updateObjects();
							stop();
						}

					});
				}
			}
		} else if (objectId == FarmingConstants.HERB_OBJECT) {
			boolean wateringCans = IntStream.of(FarmingConstants.WATERING_CAN).anyMatch(identification -> identification == itemId);
			boolean hasWateringCan = IntStream.of(FarmingConstants.WATERING_CAN).anyMatch(identification -> player.getInventory().contains(identification));
			if (player.getFarmingState(id) >= State.SEEDED.getId() && player.getFarmingState(id) < State.GROWTH.getId()) {
				if (!hasWateringCan)
					player.getActionSender().sendMessage("You need to water the herb before you can harvest it.");
				else if (wateringCans || hasWateringCan && itemId == -1) {
					int time = (int) Math.round(player.getFarmingTime(id) * .6);
			
					player.playAnimation(new Animation(FarmingConstants.WATERING_CAN_ANIM));
					player.setFarmingState(id, State.GROWTH.getId());
					player.getInventory().replace(new Item(itemId), new Item(itemId == 5333 ? 5331 : itemId - 1));
					player.getActionSender().sendMessage("You water the herb, wait " + Math.round(player.getFarmingTime(id) * .6) + " seconds for the herb to mature.");
					player.getActionSender().sendWidget(5, time);
					return;
				}
			}
			if (player.getFarmingState(id) == State.GROWTH.getId()) {
				if (player.getFarmingTime(id) > 0) {
					player.getActionSender().sendMessage("You need to wait another " + Math.round(player.getFarmingTime(id) * .6) + " seconds until the herb is mature.");
					return;
				}
			}
			if (player.getFarmingState(id) == State.HARVEST.getId()) {
				if (player.getInventory().getFreeSlots() < 1) {
					SimpleDialogues.sendStatement(player, "You need atleast 1 free space to harvest some herbs.");
					return;
				}
				if (player.getFarmingHarvest(id) == 0 || player.getFarmingState(id) != State.HARVEST.getId()) {
					resetValues(id);
					updateObjects();
					return;
				}
				final FarmingHerb.Herb herb = FarmingHerb.getHerbForSeed(player.getFarmingSeedId(id));
				
				/**
				 * Experience calculation
				 */
				double osrsHarvestExperience = herb.getHarvestingXp() + herb.getHarvestingXp() / 5 * pieces;
				if (herb != null) {
					World.getWorld().schedule(new Task(player, 3, false, StackType.NEVER_STACK, BreakType.ON_MOVE) {

						public void execute() {
							if (player == null || !player.isActive()) {
								this.stop();
								return;
							}
							if (player.getInventory().getFreeSlots() < 1) {
								SimpleDialogues.sendStatement(player, "You need atleast 1 free space to harvest some herbs.");
								player.playAnimation(new Animation(65535));
								stop();
								return;
							}
							if (player.getFarmingHarvest(id) <= 0) {
								player.getActionSender().sendMessage("The herb patch has completely depleted...", 600000);
								AchievementHandler.activate(player, AchievementList.NOVICE_FARMER, 1);
								player.playAnimation(new Animation(65535));
								resetValues(id);
								updateObjects();
								player.getActionSender().sendConfig(529, 0);
								stop();
								return;
							}
							switch (herb) {
							case AVANTOE:
								break;
							case CADANTINE:
								break;
							case DRAWF_WEED:
								break;
							case GUAM:
								break;
							case HARRALANDER:
								break;
							case IRIT:
								break;
							case KWUARM:
								break;
							case LANTADYME:
								break;
							case MARRENTIL:
								break;
							case RANARR:
								break;
							case SNAP_DRAGON:
								break;
							case TARROMIN:
								break;
							case TOADFLAX:
								break;
							case TORSTOL:
								break;
							default:
								break;
							
							}
							 if (Utility.random(herb.getPetChance()) == 20 && player.getInventory().contains(20661) && player.getPet() != 20661) {
								 pet(player);
							 }
							player.playAnimation(new Animation(FarmingConstants.PICKING_HERB_ANIM));
							player.setFarmingHarvest(id, player.getFarmingHarvest(id) - 1);
							player.getInventory().add(herb.getGrimyId(), 1);
							player.getSkills().addExperience(Skills.FARMING, osrsHarvestExperience);
						}

					});
				}
			}
		}
	}
	
	private static void pet(Player player) {
		Pets pets = Pets.TANGLEROOT;
		Pet pet = new Pet(player, pets.getNpc());

		if (player.alreadyHasPet(player, 20661) || player.getPet() == pets.getNpc()) {
			return;
		}
		if (player.getPet() > -1) {
			player.getInventory().addOrSentToBank(player, new Item(20661));
			World.getWorld().sendWorldMessage("<col=7f00ff>" + player.getUsername() + " has just received Tangleroot.", false);
		} else {
			player.setPet(pets.getNpc());
			World.getWorld().register(pet);
			World.getWorld().sendWorldMessage("<col=7f00ff>" + player.getUsername() + " has just received Tangleroot.", false);
			player.getActionSender().sendMessage("You have a funny feeling like you're being followed.");
		}
	}

	public void farmPoisonBerry() {
		if (System.currentTimeMillis() - lastPoisonBerryFarm < TimeUnit.MINUTES.toMillis(5)) {
			player.getActionSender().sendMessage("You can only pick berries from this bush every 5 minutes.");
			return;
		}
		int level = player.getSkills().getLevel(Skills.FARMING);
		if (level < 70) {
			player.getActionSender().sendMessage("You need a farming level of 70 to get this.");
			return;
		}
		if (player.getInventory().getFreeSlots() < (hasMagicSecateurs() ? 2 : 1)) {
			player.getActionSender().sendMessage("You need at least " + (hasMagicSecateurs() ? 2 : 1) + " free slot " + (hasMagicSecateurs() ? "s" : "") + " to do this.");
			return;
		}
		int maximum = player.getSkills().getLevelForExperience(Skills.FARMING);
		if (Utility.random(100) < (10 + (maximum - level))) {
			//TODO figure out what this logic is
			//player.getHealth().proposeStatus(HealthStatus.POISON, 6, Optional.empty());
		}
		player.playAnimation(new Animation(881));
		lastPoisonBerryFarm = System.currentTimeMillis();
		player.getInventory().add(6018, hasMagicSecateurs() ? 2 : 1);
		player.getSkills().addExperience(Skills.FARMING, 50);

	}

	public void farmingProcess() {
		for (int i = 0; i < Farming.MAX_PATCHES; i++) {
			if (player.getFarmingTime(i) > 0 && player.getFarmingState(i) == Farming.State.GROWTH.getId()) {
				player.setFarmingTime(i, player.getFarmingTime(i) - 1);
				if (player.getFarmingTime(i) == 0) {
					FarmingHerb.Herb herb = FarmingHerb.getHerbForSeed(player.getFarmingSeedId(i));
					if (herb != null)
						player.getActionSender().sendMessage("Your farming patch of " + herb.getSeedName().replaceAll(" seed", "") + " is ready to be harvested.", 255);
					player.setFarmingState(i, Farming.State.HARVEST.getId());
				}
			}
		}
	}

	public void resetValues(int id) {
		player.setFarmingHarvest(id, 0);
		player.setFarmingSeedId(id, 0);
		player.setFarmingState(id, 0);
		player.setFarmingTime(id, 0);
	}

	public void updateObjects() {
		for (int i = 0; i < Farming.MAX_PATCHES; i++) {
			Patch patch = Patch.get(i);
			if (patch == null)
				continue;
			if (player.distanceToPoint(patch.location.getX(), patch.location.getY()) > 60)
				continue;
			if (player.getFarmingState(i) < State.RAKED.getId()) {
				player.getActionSender().sendObject(FarmingConstants.GRASS_OBJECT, patch.location.getX(), patch.location.getY(), player.getZ(), 0, 10);
			} else if (player.getFarmingState(i) >= State.RAKED.getId() && player.getFarmingState(i) < State.SEEDED.getId()) {
				player.getActionSender().sendConfig(529, 3);
			} else if (player.getFarmingState(i) >= State.SEEDED.getId()) {
				player.getActionSender().sendObject(FarmingConstants.HERB_OBJECT, patch.location.getX(), patch.location.getY(), player.getZ(), 0, 10);
			}
		}
	}

	public boolean isHarvestable(int id) {
		return player.getFarmingState(id) == State.HARVEST.getId();
	}

	public long getLastBerryFarm() {
		return lastPoisonBerryFarm;
	}

	public void setLastBerryFarm(long millis) {
		this.lastPoisonBerryFarm = millis;
	}

	public enum State {
		NONE(0), RAKED(1), COMPOST(2), SEEDED(3), WATERED(4), GROWTH(5), HARVEST(6);

		private int id;

		State(int id) {
			this.id = id;
		}

		public int getId() {
			return id;
		}
	}

	static enum Patch {
		FALADOR_PARK(0, new Location(3003, 3372, 0)),
		CATHERBY(1, new Location(2813, 3463, 0));

		private int id;
		
		private Location location;

		Patch(int id, Location loc) {
			this.id = id;
			this.location = loc;
		}

		public int getId() {
			return this.id;
		}
		
		public Location getPatchLocation() {
			return this.location;
		}

		static List<Patch> patches = new ArrayList<>();

		static {
			for (Patch patch : Patch.values())
				patches.add(patch);
		}

		public static Patch get(int x, int y) {
			for (Patch patch : patches)
				if (patch.location.getX() == x && patch.location.getY() == y)
					return patch;
			return null;
		}

		public static Patch get(int id) {
			for (Patch patch : patches)
				if (patch.getId() == id)
					return patch;
			return null;
		}
	}
}
package com.venenatis.game.content.skills.farming;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

import com.venenatis.game.content.skills.farming.ChopTree.Tree;
import com.venenatis.game.model.Skills;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.controller.Controller;
import com.venenatis.game.model.entity.player.controller.ControllerManager;
import com.venenatis.game.model.entity.player.dialogue.SimpleDialogues;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.task.Task;
import com.venenatis.game.task.Task.BreakType;
import com.venenatis.game.task.Task.StackType;
import com.venenatis.game.util.Utility;
import com.venenatis.game.world.World;

public class Herbs {

	private Player player;

	// set of global constants for Farming

	private static final int START_HARVEST_AMOUNT = 3;
	private static final int END_HARVEST_AMOUNT = 18;

	private static final double COMPOST_CHANCE = 0.9;
	private static final double SUPERCOMPOST_CHANCE = 0.7;
	private static final double CLEARING_EXPERIENCE = 4;

	public Herbs(Player player) {
		this.player = player;
	}

	// Farming data
	public int[] herbStages = new int[4];
	public int[] herbSeeds = new int[4];
	public int[] herbHarvest = new int[4];
	public int[] herbState = new int[4];
	public long[] herbTimer = new long[4];
	public double[] diseaseChance = { 1, 1, 1, 1, 1 };

	/* set of the constants for the patch */

	// states - 2 bits plant - 6 bits
	public static final int GROWING = 0x00;

	public static final int MAIN_HERB_LOCATION_CONFIG = 515;

	/* This is the enum holding the seeds info */

	public enum HerbData {
		GUAM(5291, 199, 9, 60, 0.25, 11, 12.5, 0x04, 0x08),
		MARRENTILL(5292, 201, 14, 60, 0.25, 13.5, 15, 0x0b, 0x0f),
		TARROMIN(5293, 203, 19, 60, 0.25, 16, 18, 0x12, 0x16),
		HARRALANDER(5294, 205, 26, 60, 0.25, 21.5, 24, 0x19, 0x1d),
		GOUT_TUBER(6311, 3261, 29, 60, 0.25, 105, 45, 0xc0, 0xc4),
		RANARR(5295, 207, 32, 60, 0.20, 27, 30.5, 0x20, 0x24),
		TOADFLAX(5296, 3049, 38, 60, 0.20, 34, 38.5, 0x27, 0x2b),
		IRIT(5297, 209, 44, 60, 0.20, 43, 48.5, 0x2e, 0x32),
		AVANTOE(5298, 211, 50, 60, 0.20, 54.5, 61.5, 0x35, 0x39),
		KUARM(5299, 213, 56, 60, 0.20, 69, 78, 0x44, 0x48),
		SNAPDRAGON(5300, 3051, 62, 60, 0.15, 87.5, 98.5, 0x4b, 0x4f),
		CADANTINE(5301, 215, 67, 60, 0.15, 106.5, 120, 0x52, 0x56),
		LANTADYME(5302, 2485, 73, 60, 0.15, 134.5, 151.5, 0x59, 0x5d),
		DWARF(5303, 217, 79, 60, 0.15, 170.5, 192, 0x60, 0x64),
		TORSOL(5304, 219, 85, 60, 0.15, 199.5, 224.5, 0x67, 0x6b);

		private int seedId;
		private int harvestId;
		private int levelRequired;
		private int growthTime;
		private double diseaseChance;
		private double plantingXp;
		private double harvestXp;
		private int startingState;
		private int endingState;

		private static Map<Integer, HerbData> seeds = new HashMap<Integer, HerbData>();

		static {
			for (HerbData data : HerbData.values()) {
				seeds.put(data.seedId, data);
			}
		}

		HerbData(int seedId, int harvestId, int levelRequired, int growthTime, double diseaseChance, double plantingXp, double harvestXp, int startingState, int endingState) {
			this.seedId = seedId;
			this.harvestId = harvestId;
			this.levelRequired = levelRequired;
			this.growthTime = 10;
			this.diseaseChance = diseaseChance;
			this.plantingXp = plantingXp;
			this.harvestXp = harvestXp;
			this.startingState = startingState;
			this.endingState = endingState;
		}

		public static HerbData forId(int seedId) {
			return seeds.get(seedId);
		}

		public int getSeedId() {
			return seedId;
		}

		public int getHarvestId() {
			return harvestId;
		}

		public int getLevelRequired() {
			return levelRequired;
		}

		public int getGrowthTime() {
			return growthTime;
		}

		public double getDiseaseChance() {
			return diseaseChance;
		}

		public double getPlantingXp() {
			return plantingXp;
		}

		public double getHarvestXp() {
			return harvestXp;
		}

		public int getStartingState() {
			return startingState;
		}

		public int getEndingState() {
			return endingState;
		}
	}

	/* This is the enum data about the different patches */

	public enum HerbFieldsData {
		ARDOUGNE(0, new Point[] { new Point(2670, 3374), new Point(2671, 3375) }),
		PHASMATYS(1, new Point[] { new Point(3605, 3529), new Point(3606, 3530) }),
		FALADOR(2, new Point[] { new Point(3058, 3311), new Point(3059, 3312) }),
		CATHERBY(3, new Point[] { new Point(2813, 3463), new Point(2814, 3464) });
		private int herbIndex;
		private Point[] herbPosition;

		HerbFieldsData(int herbIndex, Point[] herbPosition) {
			this.herbIndex = herbIndex;
			this.herbPosition = herbPosition;
		}

		public static HerbFieldsData forIdPosition(int x, int y) {
			for (HerbFieldsData herbFieldsData : HerbFieldsData.values()) {
				if (FarmingConstants.inRangeArea(herbFieldsData.getHerbPosition()[0], herbFieldsData.getHerbPosition()[1], x, y)) {
					return herbFieldsData;
				}
			}
			return null;
		}

		public int getHerbIndex() {
			return herbIndex;
		}

		public Point[] getHerbPosition() {
			return herbPosition;
		}
	}

	/* This is the enum that hold the different data for inspecting the plant */

	public enum InspectData {

		GUAM(5291, new String[][] { { "The seed has only just been planted." }, { "The herb is now ankle height." }, { "The herb is now knee height." }, { "The herb is now mid-thigh height." }, { "The herb is fully grown and ready to harvest." } }),
		MARRENTILL(5292, new String[][] { { "The seed has only just been planted." }, { "The herb is now ankle height." }, { "The herb is now knee height." }, { "The herb is now mid-thigh height." }, { "The herb is fully grown and ready to harvest." } }),
		TARROMIN(5293, new String[][] { { "The seed has only just been planted." }, { "The herb is now ankle height." }, { "The herb is now knee height." }, { "The herb is now mid-thigh height." }, { "The herb is fully grown and ready to harvest." } }),
		HARRALANDER(5294, new String[][] { { "The seed has only just been planted." }, { "The herb is now ankle height." }, { "The herb is now knee height." }, { "The herb is now mid-thigh height." }, { "The herb is fully grown and ready to harvest." } }),
		GOUT_TUBER(6311, new String[][] { { "The seed has only just been planted." }, { "The herb is now ankle height." }, { "The herb is now knee height." }, { "The herb is now mid-thigh height." }, { "The herb is fully grown and ready to harvest." } }),
		RANARR(5295, new String[][] { { "The seed has only just been planted." }, { "The herb is now ankle height." }, { "The herb is now knee height." }, { "The herb is now mid-thigh height." }, { "The herb is fully grown and ready to harvest." } }),
		TOADFLAX(5296, new String[][] { { "The seed has only just been planted." }, { "The herb is now ankle height." }, { "The herb is now knee height." }, { "The herb is now mid-thigh height." }, { "The herb is fully grown and ready to harvest." } }),
		IRIT(5297, new String[][] { { "The seed has only just been planted." }, { "The herb is now ankle height." }, { "The herb is now knee height." }, { "The herb is now mid-thigh height." }, { "The herb is fully grown and ready to harvest." } }),
		AVANTOE(5298, new String[][] { { "The seed has only just been planted." }, { "The herb is now ankle height." }, { "The herb is now knee height." }, { "The herb is now mid-thigh height." }, { "The herb is fully grown and ready to harvest." } }),
		KUARM(5299, new String[][] { { "The seed has only just been planted." }, { "The herb is now ankle height." }, { "The herb is now knee height." }, { "The herb is now mid-thigh height." }, { "The herb is fully grown and ready to harvest." } }),
		SNAPDRAGON(5300, new String[][] { { "The seed has only just been planted." }, { "The herb is now ankle height." }, { "The herb is now knee height." }, { "The herb is now mid-thigh height." }, { "The herb is fully grown and ready to harvest." } }),
		CADANTINE(5301, new String[][] { { "The seed has only just been planted." }, { "The herb is now ankle height." }, { "The herb is now knee height." }, { "The herb is now mid-thigh height." }, { "The herb is fully grown and ready to harvest." } }),
		LANTADYME(5302, new String[][] { { "The seed has only just been planted." }, { "The herb is now ankle height." }, { "The herb is now knee height." }, { "The herb is now mid-thigh height." }, { "The herb is fully grown and ready to harvest." } }),
		DWARF(5303, new String[][] { { "The seed has only just been planted." }, { "The herb is now ankle height." }, { "The herb is now knee height." }, { "The herb is now mid-thigh height." }, { "The herb is fully grown and ready to harvest." } }),
		TORSOL(5304, new String[][] { { "The seed has only just been planted." }, { "The herb is now ankle height." }, { "The herb is now knee height." }, { "The herb is now mid-thigh height." }, { "The herb is fully grown and ready to harvest." } })

		;
		private int seedId;
		private String[][] messages;

		private static Map<Integer, InspectData> seeds = new HashMap<Integer, InspectData>();

		static {
			for (InspectData data : InspectData.values()) {
				seeds.put(data.seedId, data);
			}
		}

		InspectData(int seedId, String[][] messages) {
			this.seedId = seedId;
			this.messages = messages;
		}

		public static InspectData forId(int seedId) {
			return seeds.get(seedId);
		}

		public int getSeedId() {
			return seedId;
		}

		public String[][] getMessages() {
			return messages;
		}
	}

	/* update all the patch states */

	public void updateHerbsStates() {
		// falador catherby ardougne phasmatys
		int[] configValues = new int[herbStages.length];

		int configValue;
		for (int i = 0; i < herbStages.length; i++) {
			configValues[i] = getConfigValue(herbStages[i], herbSeeds[i], herbState[i], i);
		}

		configValue = (configValues[0] << 16) + (configValues[1] << 8 << 16) + configValues[2] + (configValues[3] << 8);
		player.getActionSender().sendConfig(MAIN_HERB_LOCATION_CONFIG, configValue);

	}

	/* getting the different config values */

	public int getConfigValue(int herbStage, int seedId, int plantState, int index) {
		HerbData herbData = HerbData.forId(seedId);
		switch (herbStage) {
		case 0:// weed
			return (GROWING << 6) + 0x00;
		case 1:// weed cleared
			return (GROWING << 6) + 0x01;
		case 2:
			return (GROWING << 6) + 0x02;
		case 3:
			return (GROWING << 6) + 0x03;
		}
		if (herbData == null) {
			return -1;
		}
		if (herbSeeds[index] == 6311) {
			if (plantState == 1) {
				return herbStages[index] + 0xc1;
			} else if (plantState == 2) {
				return herbStages[index] + 0xc3;
			}
		}
		return (plantState == 2 ? herbStages[index] + 0x9e : plantState == 1 ? herbStages[index] + 0x9a : getPlantState(plantState) << 6) + herbData.getStartingState() + herbStage - 4;
	}

	/* getting the plant states */

	public int getPlantState(int plantState) {
		switch (plantState) {
		case 0:
			return GROWING;
		}
		return -1;
	}

	/* calculating the disease chance and making the plant grow */

	public void doCalculations() {
		// displayAll();
		for (int i = 0; i < herbSeeds.length; i++) {
			if (herbStages[i] > 0 && herbStages[i] <= 3 && (Farming.getMinutesCounter(player) - herbTimer[i]) >= 5) {
				herbStages[i] -= 2;
				herbTimer[i] = Farming.getMinutesCounter(player);
				updateHerbsStates();
			}
			HerbData herbData = HerbData.forId(herbSeeds[i]);
			if (herbData == null) {
				continue;
			}

			long difference = Farming.getMinutesCounter(player) - herbTimer[i];
			long growth = herbData.getGrowthTime();
			int nbStates = herbData.getEndingState() - herbData.getStartingState();
			int state = (int) (difference * nbStates / growth);
			if (herbTimer[i] == 0 || herbState[i] == 2 || state > nbStates) {
				continue;
			}
			if (4 + state != herbStages[i]) {
				herbStages[i] = 4 + state;
				doStateCalculation(i);
				updateHerbsStates();
			}
		}
	}

	/* calculations about the diseasing chance */

	public void doStateCalculation(int index) {
		if (herbState[index] == 2) {
			return;
		}
		if (herbState[index] == 1) {
			herbState[index] = 2;
		}

		if (herbState[index] == 4 && herbStages[index] != 3) {
			herbState[index] = 0;
		}

		if (herbState[index] == 0 && herbStages[index] >= 4 && herbStages[index] <= 7) {
			HerbData herbData = HerbData.forId(herbSeeds[index]);
			if (herbData == null) {
				return;
			}
			double chance = diseaseChance[index] * herbData.getDiseaseChance();
			int maxChance = (int) chance * 100;
			if (Utility.random(100) <= maxChance) {
				herbState[index] = 1;
			}
		}
	}

	/* clearing the patch with a rake of a spade */

	public boolean clearPatch(int objectX, int objectY, int itemId) {
		final HerbFieldsData herbFieldsData = HerbFieldsData.forIdPosition(objectX, objectY);
		int finalAnimation;
		int finalDelay;
		if (herbFieldsData == null || (itemId != FarmingConstants.RAKE && itemId != FarmingConstants.SPADE)) {
			return false;
		}
		if (herbStages[herbFieldsData.getHerbIndex()] == 3) {
			return true;
		}
		if (herbStages[herbFieldsData.getHerbIndex()] <= 3) {
			if (!player.getInventory().contains(FarmingConstants.RAKE)) {
				SimpleDialogues.sendStatement(player, "You need a rake to clear this path.");
				return true;
			} else {
				finalAnimation = FarmingConstants.RAKING_ANIM;
				finalDelay = 5;
			}
		} else {
			if (!player.getInventory().contains(FarmingConstants.SPADE)) {
				SimpleDialogues.sendStatement(player, "You need a spade to clear this path.");
				return true;
			} else {
				finalAnimation = FarmingConstants.SPADE_ANIM;
				finalDelay = 3;
			}
		}
		final int animation = finalAnimation;
		player.playAnimation(new Animation((animation)));

		Controller controller = player.getController();
		player.setController(ControllerManager.DEFAULT_CONTROLLER);
		World.getWorld().schedule(new Task(player, finalDelay, false, StackType.NEVER_STACK, BreakType.NEVER) {
			@Override
			public void execute() {
				player.playAnimation(new Animation((animation)));
				if (herbStages[herbFieldsData.getHerbIndex()] <= 2) {
					herbStages[herbFieldsData.getHerbIndex()]++;
					player.getInventory().add(6055, 1);
				} else {
					herbStages[herbFieldsData.getHerbIndex()] = 3;
					stop();
				}
				player.getSkills().addExperience(Skills.FARMING, CLEARING_EXPERIENCE);
				herbTimer[herbFieldsData.getHerbIndex()] = Farming.getMinutesCounter(player);
				updateHerbsStates();
				if (herbStages[herbFieldsData.getHerbIndex()] == 3) {
					stop();
					return;
				}
			}

			@Override
			public void onStop() {
				resetHerbs(herbFieldsData.getHerbIndex());
				player.getActionSender().sendMessage("You clear the patch.");
				player.playAnimation(new Animation(65535));
				player.setController(controller);
			}
		});
		return true;

	}

	/* planting the seeds */
	public boolean plantSeed(int objectX, int objectY, final int seedId) {
		final HerbFieldsData herbFieldsData = HerbFieldsData.forIdPosition(objectX, objectY);
		final HerbData herbData = HerbData.forId(seedId);
		if (herbFieldsData == null || herbData == null) {
			return false;
		}
		if (herbStages[herbFieldsData.getHerbIndex()] != 3) {
			player.getActionSender().sendMessage("You can't plant a seed here.");
			return false;
		}
		if (herbData.getLevelRequired() > player.getSkills().getLevel(Skills.FARMING)) {
			SimpleDialogues.sendStatement(player, "You need a farming level of " + herbData.getLevelRequired() + " to plant this seed.");
			return true;
		}
		if (!player.getInventory().contains(FarmingConstants.SEED_DIBBER)) {
			SimpleDialogues.sendStatement(player, "You need a seed dibber to plant seed here.");
			return true;
		}
		player.playAnimation(new Animation((FarmingConstants.SEED_DIBBING)));
		player.getInventory().remove(seedId, 1);

		Controller controller = player.getController();
		player.setController(ControllerManager.DEFAULT_CONTROLLER);
		World.getWorld().schedule(new Task(player, 3, false, StackType.NEVER_STACK, BreakType.NEVER) {
			@Override
			public void execute() {
				herbState[herbFieldsData.getHerbIndex()] = 0;
				herbStages[herbFieldsData.getHerbIndex()] = 4;
				herbSeeds[herbFieldsData.getHerbIndex()] = seedId;
				herbTimer[herbFieldsData.getHerbIndex()] = Farming.getMinutesCounter(player);
				player.getSkills().addExperience(Skills.FARMING, herbData.getPlantingXp());
				stop();
			}

			@Override
			public void onStop() {
				updateHerbsStates();
				player.playAnimation(new Animation(65535));
				player.setController(controller);
			}
		});
		return true;
	}

	public void displayAll() {
		for (int i = 0; i < herbStages.length; i++) {
			if (herbSeeds[i] == 0) {
				continue;
			}
			System.out.println("minutes : " + Farming.getMinutesCounter(player));
			System.out.println("index : " + i);
			System.out.println("state : " + herbState[i]);
			System.out.println("harvest : " + herbHarvest[i]);
			System.out.println("seeds : " + herbSeeds[i]);
			System.out.println("stage : " + herbStages[i]);
			System.out.println("timer : " + herbTimer[i]);
			System.out.println("disease chance : " + diseaseChance[i]);
			System.out.println("-----------------------------------------------------------------");
		}
	}

	/* harvesting the plant resulted */

	public boolean harvest(int objectX, int objectY) {
		final HerbFieldsData herbFieldsData = HerbFieldsData.forIdPosition(objectX, objectY);
		if (herbFieldsData == null) {
			return false;
		}
		final HerbData herbData = HerbData.forId(herbSeeds[herbFieldsData.getHerbIndex()]);
		if (herbData == null) {
			return false;
		}
		if (!player.getInventory().contains(FarmingConstants.SPADE)) {
			SimpleDialogues.sendStatement(player, "You need a spade to harvest here.");
			return true;
		}

		player.playAnimation(new Animation((FarmingConstants.PICKING_VEGETABLE_ANIM)));
		Controller controller = player.getController();
		player.setController(ControllerManager.DEFAULT_CONTROLLER);
		World.getWorld().schedule(new Task(player, 3, false, StackType.NEVER_STACK, BreakType.NEVER) {
			@Override
			public void execute() {
				if (herbHarvest[herbFieldsData.getHerbIndex()] == 0) {
					herbHarvest[herbFieldsData.getHerbIndex()] = (int) (1 + (START_HARVEST_AMOUNT + Utility.random((END_HARVEST_AMOUNT + (player.getEquipment().contains(7409) ? 5 : 0)) - START_HARVEST_AMOUNT)) * (1));
				}

				if (herbHarvest[herbFieldsData.getHerbIndex()] == 1) {
					resetHerbs(herbFieldsData.getHerbIndex());
					herbStages[herbFieldsData.getHerbIndex()] = 3;
					herbTimer[herbFieldsData.getHerbIndex()] = Farming.getMinutesCounter(player);
					stop();
					return;
				}
				if (player.getInventory().getFreeSlots() <= 0) {
					stop();
					return;
				}
				herbHarvest[herbFieldsData.getHerbIndex()]--;
				player.playAnimation(new Animation((FarmingConstants.PICKING_HERB_ANIM)));
				player.getActionSender().sendMessage("You harvest the crop, and get some herbs.");
				player.getInventory().add(herbData.getHarvestId(), 1);
				player.getSkills().addExperience(Skills.FARMING, herbData.getHarvestXp());
			}

			@Override
			public void onStop() {
				updateHerbsStates();
				player.playAnimation(new Animation(65535));
				player.setController(controller);
			}
		});
		return true;
	}

	/* putting compost onto the plant */

	public boolean putCompost(int objectX, int objectY, final int itemId) {
		if (itemId != 6032 && itemId != 6034) {
			return false;
		}
		final HerbFieldsData herbFieldsData = HerbFieldsData.forIdPosition(objectX, objectY);
		if (herbFieldsData == null) {
			return false;
		}
		if (herbStages[herbFieldsData.getHerbIndex()] != 3 || herbState[herbFieldsData.getHerbIndex()] == 4) {
			player.getActionSender().sendMessage("This patch doesn't need compost.");
			return true;
		}
		player.getInventory().remove(itemId, 1);
		player.getInventory().add(1925, 1);

		player.getActionSender().sendMessage("You pour some " + (itemId == 6034 ? "super" : "") + "compost on the patch.");
		player.playAnimation(new Animation((FarmingConstants.PUTTING_COMPOST)));
		player.getSkills().addExperience(Skills.FARMING, itemId == 6034 ? Compost.SUPER_COMPOST_EXP_USE : Compost.COMPOST_EXP_USE);

		Controller controller = player.getController();
		player.setController(ControllerManager.DEFAULT_CONTROLLER);
		World.getWorld().schedule(new Task(player, 7, false, StackType.NEVER_STACK, BreakType.NEVER) {
			@Override
			public void execute() {
				diseaseChance[herbFieldsData.getHerbIndex()] *= itemId == 6032 ? COMPOST_CHANCE : SUPERCOMPOST_CHANCE;
				herbState[herbFieldsData.getHerbIndex()] = 4;
				stop();
			}

			@Override
			public void onStop() {
				player.playAnimation(new Animation(65535));
				player.setController(controller);
			}
		});
		return true;
	}

	/* inspecting a plant */

	public boolean inspect(int objectX, int objectY) {
		final HerbFieldsData herbFieldsData = HerbFieldsData.forIdPosition(objectX, objectY);
		if (herbFieldsData == null) {
			return false;
		}
		final InspectData inspectData = InspectData.forId(herbSeeds[herbFieldsData.getHerbIndex()]);
		final HerbData herbData = HerbData.forId(herbSeeds[herbFieldsData.getHerbIndex()]);
		System.out.println(herbStages[herbFieldsData.getHerbIndex()] + " 0x" + Integer.toHexString(herbStages[herbFieldsData.getHerbIndex()]));
		if (herbState[herbFieldsData.getHerbIndex()] == 1) {
			SimpleDialogues.sendStatement(player, "This plant is diseased. Use a plant cure on it to cure it, ", "or clear the patch with a spade.");
			return true;
		} else if (herbState[herbFieldsData.getHerbIndex()] == 2) {
			SimpleDialogues.sendStatement(player, "This plant is dead. You did not cure it while it was diseased.", "Clear the patch with a spade.");
			return true;
		}
		if (herbStages[herbFieldsData.getHerbIndex()] == 0) {
			SimpleDialogues.sendStatement(player, "This is an herb patch. The soil has not been treated.", "The patch needs weeding.");
		} else if (herbStages[herbFieldsData.getHerbIndex()] == 3) {
			SimpleDialogues.sendStatement(player, "This is an herb patch. The soil has not been treated.", "The patch is empty and weeded.");
		} else if (inspectData != null && herbData != null) {
			player.getActionSender().sendMessage("You bend down and start to inspect the patch...");

			player.playAnimation(new Animation((1331)));

			Controller controller = player.getController();
			player.setController(ControllerManager.DEFAULT_CONTROLLER);
			World.getWorld().schedule(new Task(player, 5, false, StackType.NEVER_STACK, BreakType.NEVER) {
				@Override
				public void execute() {
					if (herbStages[herbFieldsData.getHerbIndex()] - 4 < inspectData.getMessages().length - 2) {
						SimpleDialogues.sendStatement(player, inspectData.getMessages()[herbStages[herbFieldsData.getHerbIndex()] - 4]);
					} else if (herbStages[herbFieldsData.getHerbIndex()] < herbData.getEndingState() - herbData.getStartingState() + 2) {
						SimpleDialogues.sendStatement(player, inspectData.getMessages()[inspectData.getMessages().length - 2]);
					} else {
						SimpleDialogues.sendStatement(player, inspectData.getMessages()[inspectData.getMessages().length - 1]);
					}
					stop();
				}

				@Override
				public void onStop() {
					player.playAnimation(new Animation((1332)));
					player.playAnimation(new Animation(65535));
					player.setController(controller);
				}
			});
		}
		return true;
	}

	/* Curing the plant */

	public boolean curePlant(int objectX, int objectY, int itemId) {
		final HerbFieldsData herbFieldsData = HerbFieldsData.forIdPosition(objectX, objectY);
		if (herbFieldsData == null || itemId != 6036) {
			return false;
		}
		final HerbData herbData = HerbData.forId(herbSeeds[herbFieldsData.getHerbIndex()]);
		if (herbData == null) {
			return false;
		}
		if (herbState[herbFieldsData.getHerbIndex()] != 1) {
			player.getActionSender().sendMessage("This plant doesn't need to be cured.");
			return true;
		}
		player.getInventory().remove(itemId, 1);
		player.getInventory().add(229, 1);
		player.playAnimation(new Animation((FarmingConstants.CURING_ANIM)));

		Controller controller = player.getController();
		player.setController(ControllerManager.DEFAULT_CONTROLLER);
		World.getWorld().schedule(new Task(player, 7, false, StackType.NEVER_STACK, BreakType.NEVER) {
			@Override
			public void execute() {
				player.getActionSender().sendMessage("You cure the plant with a plant cure.");
				herbState[herbFieldsData.getHerbIndex()] = 0;
				stop();
			}

			@Override
			public void onStop() {
				updateHerbsStates();
				player.playAnimation(new Animation(65535));
				player.setController(controller);
			}
		});
		return true;

	}

	@SuppressWarnings("unused")
	private void resetHerbs() {
		for (int i = 0; i < herbStages.length; i++) {
			herbSeeds[i] = 0;
			herbState[i] = 0;
			diseaseChance[i] = 0;
			herbHarvest[i] = 0;
		}
	}

	/* reseting the patches */

	private void resetHerbs(int index) {
		herbSeeds[index] = 0;
		herbState[index] = 0;
		diseaseChance[index] = 1;
		herbHarvest[index] = 0;
	}

	/* checking if the patch is raked */

	public boolean checkIfRaked(int objectX, int objectY) {
		final HerbFieldsData herbFieldsData = HerbFieldsData.forIdPosition(objectX, objectY);
		if (herbFieldsData == null)
			return false;
		if (herbStages[herbFieldsData.getHerbIndex()] == 3)
			return true;
		return false;
	}
}
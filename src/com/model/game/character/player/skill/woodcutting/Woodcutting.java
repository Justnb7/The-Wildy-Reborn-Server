package com.model.game.character.player.skill.woodcutting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.model.action.impl.HarvestingAction;
import com.model.game.World;
import com.model.game.character.Animation;
import com.model.game.character.Entity;
import com.model.game.character.Graphic;
import com.model.game.character.npc.pet.Pet;
import com.model.game.character.npc.pet.Pets;
import com.model.game.character.player.Player;
import com.model.game.character.player.Skills;
import com.model.game.item.Item;
import com.model.game.object.GameObject;
import com.model.utility.Utility;


public class Woodcutting extends HarvestingAction {
	
	/**
	 * The random number generator.
	 */
	private final Random random = new Random();

	/**
	 * The tree we are cutting down.
	 */
	private GameObject tree_cut;
	
	/**
	 * The hatchet we are using.
	 */
	private Hatchet hatchet;
	
	/**
	 * The tree we are cutting down.
	 */
	private Tree tree;
	
	/**
	 * Constructs a new woodcutting action
	 * @param entity
	 * @param object
	 */
	public Woodcutting(Entity entity, GameObject object) {
		super(entity);
		this.tree_cut = object;
		this.tree = Tree.forId(object.getId());
	}
	
	/**
	 * Represents types of axe hatchets.
	 * @author Michael (Scu11)
	 *
	 */
	public enum Hatchet {
		
		/**
		 * Infernal axe.
		 */
		INFERNAL(13241, 61, Animation.create(2846)),

		/**
		 * Dragon axe.
		 */
		DRAGON(6739, 61, Animation.create(2846)),

		/**
		 * Rune axe.
		 */
		RUNE(1359, 41, Animation.create(867)),

		/**
		 * Adamant axe.
		 */
		ADAMANT(1357, 31, Animation.create(869)),

		/**
		 * Mithril axe.
		 */
		MITHRIL(1355, 21, Animation.create(871)),

		/**
		 * Black axe.
		 */
		BLACK(1361, 6, Animation.create(873)),

		/**
		 * Steel axe.
		 */
		STEEL(1353, 6, Animation.create(875)),

		/**
		 * Iron axe.
		 */
		IRON(1349, 1, Animation.create(877)),

		/**
		 * Bronze axe.
		 */
		BRONZE(1351, 1, Animation.create(879));
		
		/**
		 * The item id of this hatchet.
		 */
		private int id;

		/**
		 * The level required to use this hatchet.
		 */
		private int level;
		
		/**
		 * The animation performed when using this hatchet.
		 */
		private Animation animation;

		/**
		 * A list of hatchets.
		 */
		private static List<Hatchet> hatchets = new ArrayList<Hatchet>();
		
		/**
		 * Gets the list of hatchets.
		 * @return The list of hatchets.
		 */
		public static List<Hatchet> getHatchets() {
			return hatchets;
		}

		/**
		 * Populates the hatchet map.
		 */
		static {
			for(Hatchet hatchet : Hatchet.values()) {
				hatchets.add(hatchet);
			}
		}
		
		private Hatchet(int id, int level, Animation animation) {
			this.id = id;
			this.level = level;
			this.animation = animation;
		}

		/**
		 * @return the id
		 */
		public int getId() {
			return id;
		}
		
		/**
		 * @return the level
		 */
		public int getRequiredLevel() {
			return level;
		}
		
		/**
		 * @return the animation
		 */
		public Animation getAnimation() {
			return animation;
		}
	}
	
	/**
	 * Represents types of tree.
	 * @author Michael
	 *
	 */
	public enum Tree {

		/**
		 * Normal tree.
		 */
		NORMAL(1511, 1, 50, 15, 1, new int[]{1276, 1277, 1278, 1279, 1280, 1282,
				1283, 1284, 1285, 1286, 1289, 1290, 1291, 1315, 1316, 1318,
				1319, 1330, 1331, 1332, 1365, 1383, 1384, 3033, 3034, 3035,
				3036, 3881, 3882, 3883, 5902, 5903, 5904}, 10000),

		/**
		 * Willow tree.
		 */
		WILLOW(1519, 30, 135, 22, 16, new int[]{7480, 7422, 7482, 7424}, 7000),

		/**
		 * Oak tree.
		 */
		OAK(1521, 15, 75, 22, 12, new int[]{7417}, 8000),

		/**
		 * Magic tree.
		 */
		MAGIC(1513, 75, 500, 150, 18, new int[]{7483,}, 2500),

		/**
		 * Maple tree.
		 */
		MAPLE(1517, 45, 200, 60, 17, new int[]{7481,}, 5000),

		/**
		 * Mahogany tree.
		 */
		MAHOGANY(6332, 50, 60, 22, 12, new int[]{9034}, 10000),

		/**
		 * Teak tree.
		 */
		TEAK(6333, 35, 170, 22, 10, new int[]{9036}, 10000),

		/**
		 * Achey tree.
		 */
		ACHEY(2862, 1, 50, 22, 4, new int[]{2023}, 10000),

		/**
		 * Yew tree.
		 */
		YEW(1515, 60, 350, 120, 16, new int[]{7419}, 4000),

		/**
		 * Dramen tree
		 */
		DRAMEN(771, 36, 0, 22, 4, new int[]{}, 10000);

		/**
		 * The object ids of this tree.
		 */
		private int[] objects;

		/**
		 * The level required to cut this tree down.
		 */
		private int level;

		/**
		 * The logging rewarded for each cut of the tree.
		 */
		private int log;

		/**
		 * The time it takes for this tree to respawn.
		 */
		private int respawnTimer;

		/**
		 * The amount of logs this tree contains.
		 */
		private int logCount;

		/**
		 * The experience granted for cutting a logging.
		 */
		private double experience;

		private int petRate;

		/**
		 * A map of object ids to trees.
		 */
		private static Map<Integer, Tree> trees = new HashMap<Integer, Tree>();

		/**
		 * Gets a tree by an object id.
		 *
		 * @param object The object id.
		 * @return The tree, or <code>null</code> if the object is not a tree.
		 */
		public static Tree forId(int object) {
			return trees.get(object);
		}

		static {
			for (Tree tree : Tree.values()) {
				for (int object : tree.objects) {
					trees.put(object, tree);
				}
			}
		}

		/**
		 * Creates the tree.
		 *
		 * @param log        The logging id.
		 * @param level      The required level.
		 * @param experience The experience per logging.
		 * @param objects    The object ids.
		 */
		Tree(int log, int level, double experience, int respawnTimer, int logCount, int[] objects, int petRate) {
			this.objects = objects;
			this.level = level;
			this.experience = experience;
			this.respawnTimer = respawnTimer;
			this.logCount = logCount;
			this.log = log;
			this.petRate = petRate;
		}

		/**
		 * Gets the logging id.
		 *
		 * @return The logging id.
		 */
		public int getLogId() {
			return log;
		}

		/**
		 * Gets the object ids.
		 *
		 * @return The object ids.
		 */
		public int[] getObjectIds() {
			return objects;
		}

		/**
		 * Gets the required level.
		 *
		 * @return The required level.
		 */
		public int getRequiredLevel() {
			return level;
		}

		/**
		 * Gets the experience.
		 *
		 * @return The experience.
		 */
		public double getExperience() {
			return experience * 2;
		}

		/**
		 * @return the respawnTimer
		 */
		public int getRespawnTimer() {
			return respawnTimer;
		}

		/**
		 * @return the logCount
		 */
		public int getLogCount() {
			return logCount;
		}

		public int getPetRate() {
			return petRate;
		}
	}

	@Override
	public Animation getAnimation() {
		return hatchet.getAnimation();
	}

	@Override
	public int getCycleCount() {
		int skill = getEntity().asPlayer().getSkills().getLevel(getSkill());
		int level = tree.getRequiredLevel();
		int modifier = hatchet.getRequiredLevel();
		int randomAmt = random.nextInt(3);
		double cycleCount = 1;
		cycleCount = Math.ceil((level * 50 - skill * 10) / modifier * 0.25 - randomAmt * 4);
		if (cycleCount < 1) {
			cycleCount = 1;
		}
		return (int) cycleCount;
	}

	@Override
	public double getExperience() {
		int random = Utility.random(tree.getPetRate());
		if (random == 0) {
			Pets pets = Pets.BEAVER;
			if (!getEntity().isPlayer()) {
			}
			Player player = (Player) getEntity();
			if (player.isPetSpawned()) {
				if (player.getInventory().getFreeSlots() < 1) {
					player.getInventory().add(new Item(13322));
				} else {
					//player.getBank().add(new Item(13322));
				}
				World.getWorld().sendWorldMessage("<col=7f00ff>" + player.getName() + " has just received 1x Beaver.", false);
			} else {
				Pet pet = new Pet(player, pets.getNpc());
				player.setPetSpawned(true);
				player.setPet(pets.getNpc());
				World.getWorld().register(pet);
				World.getWorld().sendWorldMessage("<col=7f00ff>" + player.getName() + " has just received 1x Beaver.", false);
			}
		}
		return tree.getExperience();
	}

	@Override
	public GameObject getGameObject() {
		return tree_cut;
	}

	@Override
	public int getGameObjectMaxHealth() {
		return Utility.random(1, tree.getLogCount());
	}

	@Override
	public String getHarvestStartedMessage() {
		return "You swing your axe at the tree.";
	}

	@Override
	public String getLevelTooLowMessage() {
		return "You need a " + Skills.SKILL_NAME[getSkill()] + " level of " + tree.getRequiredLevel() + " to cut this tree.";
	}

	@Override
	public int getObjectRespawnTimer() {
		return tree.getRespawnTimer();
	}

	@Override
	public GameObject getReplacementObject() {
		return new GameObject(getGameObject().getPosition(), 1342, 10, 0);
	}

	@Override
	public int getRequiredLevel() {
		return tree.getRequiredLevel();
	}

	@Override
	public Item getReward() {
		if (hatchet == Hatchet.INFERNAL && Utility.random(8) == 0) {
			getEntity().asPlayer().getSkills().addExperience(Skills.FIREMAKING, tree.getExperience() / 2);
			getEntity().playGraphics(Graphic.create(86));
			return null;
		}
		return new Item(tree.getLogId(), 1);
	}

	@Override
	public int getSkill() {
		return Skills.WOODCUTTING;
	}

	@Override
	public String getSuccessfulHarvestMessage() {
		return "You get some " + getReward().getDefinition().getName().toLowerCase() + ".";
	}

	@Override
	public boolean canHarvest() {
		for(Hatchet hatchet : Hatchet.values()) {
			if((getEntity().asPlayer().getInventory().contains(hatchet.getId()) || getEntity().asPlayer().getEquipment().contains(hatchet.getId())) && getEntity().asPlayer().getSkills().getLevelForExperience(getSkill()) >= hatchet.getRequiredLevel()) {
				this.hatchet = hatchet;
				break;
			}
		}
		if(hatchet == null) {
			getEntity().getActionSender().sendMessage("You do not have an axe that you can use.");
			return false;
		}
		return true;
	}
	
	@Override
	public String getInventoryFullMessage() {
		return "Your inventory is too full to hold any more " + getReward().getDefinition().getName().toLowerCase() + ".";
	}

}
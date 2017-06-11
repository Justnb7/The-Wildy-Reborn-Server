package com.model.game.character.player.skill.mining;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.model.action.impl.HarvestingAction;
import com.model.game.World;
import com.model.game.character.Animation;
import com.model.game.character.Entity;
import com.model.game.character.npc.pet.Pet;
import com.model.game.character.npc.pet.Pets;
import com.model.game.character.player.Player;
import com.model.game.character.player.Skills;
import com.model.game.item.Item;
import com.model.game.item.container.Container;
import com.model.game.object.GameObject;
import com.model.utility.Utility;


public class Mining extends HarvestingAction {
	
	/*
	 * 317 style rock replacements; 8828-8830
	 * 474+ style rock replacements; 
	 * 
	 * Replacements: [13456, 15198],
	 */

	/**
	 * The rock we are mining.
	 */
	private GameObject object;
	
	/**
	 * The pick axe we are using.
	 */
	private PickAxe pickaxe;
	
	/**
	 * The rock we are mining.
	 */
	private Rock rock;
	
	public Mining(Entity entity, GameObject object) {
		super(entity);
		this.object = object;
		this.rock = Rock.forId(object.getId());
	}
	
	/**
	 * Represents types of pick axes.
	 * @author Michael (Scu11)
	 *
	 */
	public static enum PickAxe {

		/**
		 * Infernal pickaxe.
		 */
		INFERNAL(13243, 61, Animation.create(7139)),
		
		/**
		 * Dragon pickaxe.
		 */
		DRAGON_OR(12797, 61, Animation.create(335)),
		
		DRAGON(11920, 61, Animation.create(7139)),

		/**
		 * Rune pickaxe.
		 */
		RUNE(1275, 41, Animation.create(624)),

		/**
		 * Adamant pickaxe.
		 */
		ADAMANT(1271, 31, Animation.create(628)),

		/**
		 * Mithril pickaxe.
		 */
		MITHRIL(1273, 21, Animation.create(629)),

		/**
		 * Steel pickaxe.
		 */
		STEEL(1269, 6, Animation.create(627)),

		/**
		 * Iron pickaxe.
		 */
		IRON(1267, 1, Animation.create(626)),

		/**
		 * Bronze pickaxe.
		 */
		BRONZE(1265, 1, Animation.create(625));
		
		/**
		 * The item id of this pick axe.
		 */
		private int id;

		/**
		 * The level required to use this pick axe.
		 */
		private int level;
		
		/**
		 * The animation performed when using this pick axe.
		 */
		private Animation animation;

		/**
         * A list of pick axes.
         */
        private static List<PickAxe> pickaxes = new ArrayList<PickAxe>();

        /**
         * Gets the list of pick axes.
         * @return The list of pick axes.
         */
        public static List<PickAxe> getPickaxes() {
            return pickaxes;
        }

        /**
         * Populates the pick axe map.
         */
        static {
            for(PickAxe pickaxe : PickAxe.values()) {
                pickaxes.add(pickaxe);
            }
        }
		
		private PickAxe(int id, int level, Animation animation) {
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
	 * Represents types of rocks.
	 * @author Michael
	 *
	 */
	public static enum Rock {

		/**
		 * Clay ore.
		 */
		CLAY(434, 1, 10, 2, 1, new int[] { 13456, 13457, 14176, 14181, }, new int[] { 13459, 13460, 10798, 10796}, 10000),
		
		/**
		 * Copper ore.
		 */
		COPPER(436, 1, 33, 4, 1, new int[] { 7453, 7484,  }, new int[] { 7468, 7469 }, 10000),
		
		/**
		 * Tin ore.
		 */
		TIN(438, 1, 33, 4, 1, new int[] { 7485, 7486, }, new int[] { 7468, 7469}, 10000),
		
		/**
		 * Iron ore.
		 */
		IRON(440, 15, 70, 10, 1, new int[] { 7488, 7455,  }, new int[] { 7469, 7468, }, 9000),
		
		/**
		 * Silver ore.
		 */
		SILVER(442, 20, 80, 100, 1, new int[] { 8976, 8977, 8978,  13439, 13440, 13447, 13438, }, new int[] { 8979, 8980, 8981, 13460, 13461, 13459, 13459}, 8500),
		
		/**
		 * Gold ore.
		 */
		GOLD(444, 40, 130, 100, 1, new int[] { 7458, 7491 }, new int[] { 7469, 7468 }, 7000),
		
		/**
		 * Coal ore.
		 */
		COAL(453, 30, 100, 50, 1, new int[] { 7456, 7489,  }, new int[] { 7469, 7468,  }, 5000),
		
		/**
		 * Mithril ore.
		 */
		MITHRIL(447, 55, 160, 200, 1, new int[] {7459, 7492 }, new int[] { 7468, 7469 }, 3000),
		
		/**
		 * Adamantite ore.
		 */
		ADAMANTITE(449, 70, 190, 400, 1, new int[] { 7460 }, new int[] { 7469 }, 2000),
		
		/**
		 * Rune ore.
		 */
		RUNE(451, 85, 430, 1000, 1, new int[] { 7418, 7419, 7494, 7461, }, new int[] { 7468, 7469, 7469, 7469, }, 1000)
		
		;
		
		/**
		 * The object ids of this rock.
		 */
		private int[] objects;
		
		/**
		 * The level required to mine this rock.
		 */
		private int level;
		
		/**
		 * The ore rewarded for each cut of the tree.
		 */
		private int log;
		
		/**
		 * The time it takes for this rock to respawn.
		 */
		private int respawnTimer;

		/**
		 * The amount of ores this rock contains.
		 */
		private int oreCount;

		/**
		 * The experience granted for mining this rock.
		 */
		private double experience;
		
		/**
		 * The rocks to replace.
		 */
		private int[] replacementRocks;

		private final int petRate;
		
		/**
		 * A map of object ids to rocks.
		 */
		private static Map<Integer, Rock> rocks = new HashMap<Integer, Rock>();
		
		/**
		 * Gets a rock by an object id.
		 * @param object The object id.
		 * @return The rock, or <code>null</code> if the object is not a rock.
		 */
		public static Rock forId(int object) {
			return rocks.get(object);
		}
		
		static {
			for(Rock rock : Rock.values()) {
				for(int object : rock.objects) {
					rocks.put(object, rock);
				}
			}
		}

		/**
		 * Creates the rock.
		 * @param log The logging id.
		 * @param level The required level.
		 * @param experience The experience per logging.
		 * @param objects The object ids.
		 */
		private Rock(int log, int level, double experience, int respawnTimer, int oreCount, int[] objects, int[] replacementRocks, int petRate) {
			this.objects = objects;
			this.level = level;
			this.experience = experience;
			this.respawnTimer = respawnTimer;
			this.oreCount = oreCount;
			this.log = log;
			this.replacementRocks = replacementRocks;
			this.petRate = petRate;
		}

		/**
		 * @return the replacementRocks
		 */
		public int[] getReplacementRocks() {
			return replacementRocks;
		}

		/**
		 * Gets the logging id.
		 * 
		 * @return The logging id.
		 */
		public int getOreId() {
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
			return experience;
		}
		
		/**
		 * @return the respawnTimer
		 */
		public int getRespawnTimer() {
			return respawnTimer;
		}
		
		/**
		 * @return the oreCount
		 */
		public int getOreCount() {
			return oreCount;
		}

		public int getPetRate() { return petRate; }
	}

	@Override
	public Animation getAnimation() {
		return pickaxe.getAnimation();
	}

	@Override
	public int getCycleCount() {
		int skill = getEntity().asPlayer().getSkills().getLevel(getSkill());
		int level = rock.getRequiredLevel();
		int modifier = pickaxe.getRequiredLevel();
		double cycleCount = 1;
		cycleCount = Math.ceil((level * 50 - skill * 10) / modifier * 0.0625 * 4);
		if (cycleCount < 1) {
			cycleCount = 1;
		}
		return (int) cycleCount;
	}

	@Override
	public double getExperience() {
		int random = Utility.random(rock.getPetRate());
		if (random == 0) {
			Pets pets = Pets.ROCK_GOLEM;
			if (!getEntity().isPlayer()) {
			}
			Player player = (Player) getEntity();
			if (player.isPetSpawned()) {
				return rock.getExperience() * (getEntity().isPlayer() ? getProspectorKitExperienceModifier((Player) getEntity()) : 1f) * 2;
			} else {
				Pet pet = new Pet(player, pets.getNpc());
				player.setPet(pets.getNpc());
				player.setPetSpawned(true);
				World.getWorld().register(pet);
				World.getWorld().sendWorldMessage("<col=7f00ff>" + player.getName() + " has just received 1x Rock Golem.", false);
				return rock.getExperience() * (getEntity().isPlayer() ? getProspectorKitExperienceModifier((Player) getEntity()) : 1f) * 2;
			}
		}
		return rock.getExperience() * (getEntity().isPlayer() ? getProspectorKitExperienceModifier((Player) getEntity()) : 1f) * 2;
	}

	private float getProspectorKitExperienceModifier(Player player) {
		float modifier = 1f;
		final Container eq = player.getEquipment();
		if (eq.contains(12013)) {//helmet
			modifier += 0.04f;
		}
		if (eq.contains(12014)) {//jacket
			modifier += 0.08f;
		}
		if (eq.contains(12015)) {//legs
			modifier += 0.06f;
		}
		if (eq.contains(12016)) {//boots
			modifier += 0.02f;
		}
		return modifier;
	}

	@Override
	public GameObject getGameObject() {
		return object;
	}

	@Override
	public int getGameObjectMaxHealth() {
		return rock.getOreCount();
	}

	@Override
	public String getHarvestStartedMessage() {
		return "You swing your pick at the rock.";
	}

	@Override
	public String getLevelTooLowMessage() {
		return "You need a " + Skills.SKILL_NAME[getSkill()] + " level of " + rock.getRequiredLevel() + " to mine this rock.";
	}

	@Override
	public int getObjectRespawnTimer() {
		return rock.getRespawnTimer();
	}

	@Override
	public GameObject getReplacementObject() {
		int index = 0;
		for(int i = 0; i < rock.getObjectIds().length; i++) {
			if(rock.getObjectIds()[i] == getGameObject().getId()) {
				index = i;
				break;
			}
		}
		return new GameObject(rock.getReplacementRocks()[index], getGameObject().getPosition(), getGameObject().getType(), getGameObject().getFace());
	}

	@Override
	public int getRequiredLevel() {
		return rock.getRequiredLevel();
	}

	@Override
	public Item getReward() {
		return new Item(rock.getOreId(), 1);
	}

	@Override
	public int getSkill() {
		return Skills.MINING;
	}

	@Override
	public String getSuccessfulHarvestMessage() {
		return "You manage to mine some " + getReward().getName().toLowerCase().replaceAll(" ore", "") + ".";
	}

	@Override
	public boolean canHarvest() {
		for(PickAxe pickaxe : PickAxe.values()) {
			if((getEntity().asPlayer().getInventory().contains(pickaxe.getId()) || getEntity().asPlayer().getEquipment().contains(pickaxe.getId()))
							&& getEntity().asPlayer().getSkills().getLevelForExperience(getSkill()) >= pickaxe.getRequiredLevel()) {
				this.pickaxe = pickaxe;
				break;
			}
		}
		if(pickaxe == null) {
			getEntity().getActionSender().sendMessage("You do not have a pickaxe that you can use.");
			return false;
		}
		return true;
	}
	
	@Override
	public String getInventoryFullMessage() {
		return "Your inventory is too full to hold any more " + getReward().getName().toLowerCase().replaceAll(" ore", "") + ".";
	}

}

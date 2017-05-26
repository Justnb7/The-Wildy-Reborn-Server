package com.model.game.character.player.skill.smithing;

import java.util.HashMap;

import java.util.Map;

import com.model.action.impl.ProductionAction;
import com.model.game.character.Animation;
import com.model.game.character.Entity;
import com.model.game.character.Graphic;
import com.model.game.character.player.Skills;
import com.model.game.item.Item;

public class Smelting extends ProductionAction {
	
	
	/**
	 * The amount of times to produce this item.
	 */
	private int productionCount;
	
	
	
	/**
	 * The ingredient type.
	 */
	private final Smelt smelt;
	
	public Smelting(Entity entity, int productionCount, Smelt smelt) { 
		super(entity);
		this.productionCount = productionCount;
		this.smelt = smelt;
	}
	
	
	public enum Smelt { 
		BRONZE(0, 436, new Item(438, 1), 2349, 1, 6.2),
		BURTILE(1, 668, new Item(453, 1), 9467, 8, 8),
		IRON(2, 440, new Item(440, 0), 2351, 15, 12.5),
		SILVER(3, 442, new Item(442, 0), 2353, 20, 13.7),
		STEEL(4, 440, new Item(453, 2), 2355, 30, 17.5),
		GOLD(5, 444, new Item(444, 0), 2357, 40, 22.5),
		MITH(6, 447, new Item(453, 4), 2359, 50, 30),
		ADDY(7, 449, new Item(453, 6), 2361, 70, 37.5),
		RUNE(8, 451, new Item(453, 8), 2363, 85, 50);


		
		private int index; 



		/**
		 * The first item.
		 */
		private int firstItem;
		
		/**
		 * The 2nd item. && amount of it Ex. coal
		 */
		private Item secondItem;

		/**
		 * The amount for secondary.
		 */
		/**
		 * The bar.
		 */
		private int itemId;
		
		/**
		 * The level.
		 */
		private int level;
		
		/**
		 * The experience.
		 */
		private double exp;

	
		private static Map<Integer, Smelt> ores = new HashMap<>();

	
		public static Smelt forId(int item) {
			return ores.get(item);
		}

		/**
		 * Populates the map.
		 */
		static {
			for (Smelt smelt : Smelt.values()) {
				ores.put(smelt.index, smelt);
			}
		}

		private Smelt(int index, int firstItem, Item secondItem, int itemId, int level, double exp) {
			this.index = index;
			this.firstItem = firstItem;
			this.secondItem = secondItem;
			this.itemId = itemId;
			this.level = level;
			this.exp = exp;
		}

		/**
		 * Gets the index.
		 * 
		 * @return The index.
		 */
		public int getIndex() {
			return index;
		}
		
	
		/**
		 * gets the first ore ex. copper
		 * @return
		 */
		public int getFirst() {
			return firstItem;
		}
		/**
		 * Gets the second ore ex coal
		 * 
		 * @return The required id.
		 */
		public Item getSecond() {
			return secondItem;
		}
		
		/**
		 * Gets the bar id.
		 * 
		 * @return The id.
		 */
		public int getId() {
			return itemId;
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
		 * Gets the exp.
		 * 
		 * @return The exp.
		 */
		public double getExperience() {
			return exp;
		}

	}
	
	@Override
	public boolean canProduce() {
	
		return true;
			
	}

	@Override
	public Animation getAnimation() {
		return Animation.create(899);
	}

	@Override
	public Item[] getConsumedItems() {
		return new Item[] { new Item(smelt.getFirst()), smelt.getSecond()};
		}
	
	@Override
	public int getCycleCount() {
		return 2;
	}

	@Override
	public double getExperience() {
			return smelt.getExperience();
	}

	@Override
	public Graphic getGraphic() {
		return null;
	}

	@Override
	public String getLevelTooLowMessage() {
		return "You need a " + Skills.SKILL_NAME[getSkill()] + " level of " + getRequiredLevel() + " to smelt these ores.";
	}

	@Override
	public int getProductionCount() {
		return productionCount;
	}

	@Override
	public int getRequiredLevel() {
		return smelt.getRequiredLevel();
	}

	@Override
	public Item[] getRewards() {
			return new Item[] { new Item(smelt.getId()) };
	}

	@Override
	public int getSkill() {
		return Skills.SMITHING;
	}

	@Override
	public String getSuccessfulProductionMessage() {
			return "You smelt the ores into a bar.";
	}

}
package com.model.game.character.player.skill.fletching;

import com.model.action.impl.ProductionAction;
import com.model.game.character.Animation;
import com.model.game.character.Entity;
import com.model.game.character.Graphic;
import com.model.game.character.player.Skills;
import com.model.game.item.Item;
import com.model.utility.json.definitions.ItemDefinition;


public class Fletching extends ProductionAction {

	/**
	 * The amount of items to produce.
	 */
	private int productionCount;
	
	/**
	 * The log index.
	 */
	private int logIndex;
	
	/**
	 * The log we are fletching.
	 */
	private Log log;
	
	/**
	 * Constructs a new fletching action
	 * @param entity
	 * @param productionCount
	 * @param logIndex
	 * @param log
	 */
	public Fletching(Entity entity, int productionCount, int logIndex, Log log) {
		super(entity);
		this.productionCount = productionCount;
		this.logIndex = logIndex;
		this.log = log;
	}

	@Override
	public boolean canProduce() {
		return true;
	}

	@Override
	public Animation getAnimation() {
		return Animation.create(1248);
	}

	@Override
	public Item[] getConsumedItems() {
		return new Item[] { new Item(log.getLogId()) };
	}

	@Override
	public int getCycleCount() {
		return 3;
	}

	@Override
	public double getExperience() {
		return log.getExperience()[logIndex];
	}

	@Override
	public Graphic getGraphic() {
		return null;
	}

	@Override
	public String getLevelTooLowMessage() {
		return "You need a Fletching level of " + getRequiredLevel() + " to fletch this.";
	}

	@Override
	public int getProductionCount() {
		return productionCount;
	}

	@Override
	public int getRequiredLevel() {
		return log.getLevel()[logIndex];
	}

	@Override
	public Item[] getRewards() {
		return new Item[] { new Item(log.getItem()[logIndex], log.getItem()[logIndex] == 53 ? 15 : 1) };
	}

	@Override
	public int getSkill() {
		return Skills.FLETCHING;
	}

	@Override
	public String getSuccessfulProductionMessage() {
		String prefix = "a";
		String suffix = "";
		char first = ItemDefinition.forId(log.getItem()[logIndex]).getName().toLowerCase().charAt(0);
		if(first == 'a' || first == 'e' || first == 'i' || first == 'o' || first == 'u') {
			prefix = "an";
		}
		if(log.getItem()[logIndex] == 52) {
			prefix = "some";
			suffix = "s";
		}
		return "You successfully fletch " + prefix + " " + ItemDefinition.forId(log.getItem()[logIndex]).getName().toLowerCase() + "" + suffix + ".";
	}

}
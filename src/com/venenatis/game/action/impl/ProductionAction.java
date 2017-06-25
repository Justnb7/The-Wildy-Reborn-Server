package com.venenatis.game.action.impl;

import com.venenatis.game.action.Action;
import com.venenatis.game.model.Item;
import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.model.masks.Graphic;

/**
 * <p>
 * A producing action is an action where on item is transformed into another,
 * typically this is in skills such as smithing and crafting.
 * </p>
 * 
 * <p>
 * This class implements code related to all production-type skills, such as
 * dealing with the action itself, replacing the items and checking levels.
 * </p>
 * 
 * <p>
 * The individual crafting, smithing, and other skills implement functionality
 * specific to them such as random events.
 * </p>
 * 
 * @author Michael (Scu11)
 */
public abstract class ProductionAction extends Action {
	
	/**
	 * Creates the production action for the specified mob.
	 * @param entity The mob to create the action for.
	 */
	public ProductionAction(Entity entity) {
		super(entity, 0);
	}
	
	/**
	 * Gets the amount of cycles before the item is produced.
	 * @return The amount of cycles before the item is produced.
	 */
	public abstract int getCycleCount();

	/**
	 * Gets the amount of times an item is produced.
	 * @return The amount of times an item is produced.
	 */
	public abstract int getProductionCount();

	/**
	 * Gets the rewarded items from production.
	 * @return The rewarded items from production.
	 */
	public abstract Item[] getRewards();
	
	/**
	 * Gets the consumed item in the production of this item.
	 * @return The consumed item in the production of this item.
	 */
	public abstract Item[] getConsumedItems();
	
	/**
	 * Gets the skill we are using to produce.
	 * @return The skill we are using to produce.
	 */
	public abstract int getSkill();
	
	/**
	 * Gets the required level to produce this item.
	 * @return The required level to produce this item.
	 */
	public abstract int getRequiredLevel();
	
	/**
	 * Gets the experience granted for each item that is successfully produced.
	 * @return The experience granted for each item that is successfully produced.
	 */
	public abstract double getExperience();
	
	/**
	 * Gets the message sent when the mob's level is too low to produce this item.
	 * @return The message sent when the mob's level is too low to produce this item.
	 */
	public abstract String getLevelTooLowMessage();
	
	/**
	 * Gets the message sent when the mob successfully produces an item.
	 * @return The message sent when the mob successfully produce an item.
	 */
	public abstract String getSuccessfulProductionMessage();
	
	/**
	 * Gets the animation played whilst producing the item.
	 * @return The animation played whilst producing the item.
	 */
	public abstract Animation getAnimation();
	
	/**
	 * Gets the graphic played whilst producing the item.
	 * @return The graphic played whilst producing the item.
	 */
	public abstract Graphic getGraphic();
	
	/**
	 * Performs extra checks that a specific production event independently uses, e.g. checking for ingredients in herblore.
	 */
	public abstract boolean canProduce();
	
	/**
	 * This starts the actions animation and requirement checks, but prevents the production from immediately executing.
	 */
	private boolean started = false;
	
	/**
	 * The cycle count.
	 */
	private int cycleCount = 0;
	
	/**
	 * The amount of items to produce.
	 */
	private int productionCount = 0;

	@Override
	public CancelPolicy getCancelPolicy() {
		return CancelPolicy.ALWAYS;
	}

	@Override
	public StackPolicy getStackPolicy() {
		return StackPolicy.NEVER;
	}

	@Override
	public AnimationPolicy getAnimationPolicy() {
		return AnimationPolicy.RESET_ALL;
	}

	@Override
	public void execute() {
		if(getEntity().asPlayer().getSkills().getLevelForExperience(getSkill()) < getRequiredLevel()) {
			getEntity().getActionSender().removeAllInterfaces();
			getEntity().getActionSender().sendMessage(getLevelTooLowMessage());
			getEntity().playAnimation(Animation.create(-1));
			this.stop();
			return;
		}
		for(Item item : getConsumedItems()) {
			if(getEntity().asPlayer().getInventory().getAmount(item.getId()) < item.getAmount()) {
				getEntity().playAnimation(Animation.create(-1));
				this.stop();
				return;
			}
		}
		if(!canProduce()) {
			this.stop();
			return;
		}
		if(!started) {
			started = true;
			if(getAnimation() != null) {
				getEntity().playAnimation(getAnimation());
			}
			if(getGraphic() != null) {
				getEntity().playGraphics(getGraphic());
			}
			productionCount = getProductionCount();
			cycleCount = getCycleCount();
			return;
		}
		
		if(getAnimation() != null) {
			getEntity().playAnimation(getAnimation());
		}
		if(getGraphic() != null) {
			getEntity().playGraphics(getGraphic());
		}
		
		if(cycleCount > 1) {
			cycleCount--;
			return;
		}
		
		cycleCount = getCycleCount();
		
		productionCount--;
		
		getEntity().getActionSender().sendMessage(getSuccessfulProductionMessage());
		for(Item item : getConsumedItems()) {
			getEntity().asPlayer().getInventory().remove(item);
		}
		for(Item item : getRewards()) {
			getEntity().asPlayer().getInventory().add(item);
		}
		getEntity().asPlayer().getSkills().addExperience(getSkill(), getExperience());
		
		if(productionCount < 1) {
			getEntity().playAnimation(Animation.create(-1));
			this.stop();
			return;
		}
		for(Item item : getConsumedItems()) {
			if(getEntity().asPlayer().getInventory().getAmount(item.getId()) < item.getAmount()) {
				getEntity().playAnimation(Animation.create(-1));
				this.stop();
				return;
			}
		}
	}

}
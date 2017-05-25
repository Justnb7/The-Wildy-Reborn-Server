package com.model.action.impl;

import com.model.action.Action;
import com.model.game.character.Animation;
import com.model.game.character.Entity;
import com.model.game.character.Graphic;
import com.model.game.item.Item;

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
	
	public abstract boolean isSuccessfull();

	public abstract String getFailProductionMessage();

	public abstract Item getFailItem();
	
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
			if(item != null && getEntity().asPlayer().getInventory().getCount(item.getId()) < item.getAmount()) {
				getEntity().playAnimation(Animation.create(-1));
				this.stop();
				return;
			}
		}
		if(!canProduce()) {
			this.stop();
			return;
		}

		final Animation anim = getAnimation();
		final Graphic graphic = getGraphic();
		if(!started) {
			started = true;
			if(anim != null) {
				getEntity().playAnimation(anim);
			}
			if(graphic != null) {
				getEntity().playGraphics(graphic);
			}
			productionCount = getProductionCount();
			cycleCount = getCycleCount();
			return;
		}

        if(anim != null && cycleCount == 0 || (anim != null && cycleCount == getCycleCount() && getProductionCount() != productionCount)) {
            getEntity().playAnimation(getAnimation());
        }
		if(graphic != null && cycleCount == 0 || (graphic != null && cycleCount == getCycleCount() && getProductionCount() != productionCount)) {
			getEntity().playGraphics(graphic);
		}

		if(cycleCount > 1) {
			cycleCount--;
			return;
		}

		cycleCount = getCycleCount();

		productionCount--;

		if (isSuccessfull()) {
			getEntity().getActionSender().sendMessage(getSuccessfulProductionMessage());
			if (getRewards() != null) {
				for (Item item : getRewards()) {
					getEntity().asPlayer().getInventory().add(item);
				}
			}
			getEntity().asPlayer().getSkills().addExperience(getSkill(), getExperience());
		} else {
			getEntity().getActionSender().sendMessage(getFailProductionMessage());
			getEntity().asPlayer().getInventory().add(getFailItem());
		}
		if(productionCount < 1) {
			getEntity().playAnimation(Animation.create(-1));
			onSuccess();
			this.stop();
		}
		for(Item item : getConsumedItems()) {
			if(item != null && getEntity().asPlayer().getInventory().getCount(item.getId()) < item.getAmount()) {
				getEntity().playAnimation(Animation.create(-1));
				this.stop();
				return;
			}
		}
	}

	public void onSuccess() {

	}

}
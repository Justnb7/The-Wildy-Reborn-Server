package com.venenatis.game.action.impl.actions;

import java.util.concurrent.TimeUnit;

import com.venenatis.game.action.Action;
import com.venenatis.game.constants.Constants;
import com.venenatis.game.consumables.Consumables.Drink;
import com.venenatis.game.consumables.Consumables.Food;
import com.venenatis.game.consumables.Consumables.PotionType;
import com.venenatis.game.model.Item;
import com.venenatis.game.model.Skills;
import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.entity.Hit;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.model.masks.UpdateFlags.UpdateFlag;
import com.venenatis.game.task.Task;
import com.venenatis.game.util.Utility;
import com.venenatis.game.world.World;

public class ConsumeItemAction extends Action {

	/**
	 * The item we are consuming
	 */
	private Item item;

	/**
	 * The item's slot.
	 */
	private int slot;

	public enum Monkey {
		MONKEY(23), SMALL_NINJA(1462), MEDIUM_NINJA(1463), ANCIENT(1466), SMALL_ZOMBIE_MONKEY(
				1467), LARGE_ZOMBIE_MONKEY(1468), BLUE_MONKEY(1825), RED_MONKEY(1826);

		final int id;

		Monkey(int id) {
			this.id = id;
		}

		public int getId() {
			return id;
		}
	}

	public ConsumeItemAction(Entity entity, Item item, int slot) {
		super(entity, 0);
		this.item = item;
		this.slot = slot;
	}

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
		Player player = (Player) getEntity();
		this.stop();
		if (player.getCombatState().isDead()) {
			return;
		}
		final Food food = Food.forId(item.getId());
		final Drink drink = Drink.forId(item.getId());

		/**
		 * Food
		 */
		if (food != null) {
			Food last = player.getCombatState().getLastAte();
			if (food == Food.KARAMBWAN && last != Food.KARAMBWAN) {
				player.getCombatState().setEatDelay(0);
			}
			// System.out.println(food + ", " + last + ", " + player.getCombatState().getEatDelay());
			if (player.getCombatState().getEatDelay() == 0) {
				player.getCombatState().setCanEat(true);
				player.playAnimation(Animation.create(829));
				if (food != Food.PURPLE_SWEETS)
					player.getInventory().remove(item);

				int delay = food == Food.KARAMBWAN && last == Food.KARAMBWAN ? 1 : 3;
				player.getCombatState().setEatDelay(delay);

				/**
				 * Increases the mob's attack delay by 3 cycles if the current
				 * timer is below its max cooldown + 3 cycles.
				 */
				player.getCombatState().increaseAttackDelay(2);

				/**
				 * Send the confirmation message and start the animation.
				 */
				if (food == Food.PURPLE_SWEETS) {
					player.getActionSender().sendMessage("The sugary goodness heals some energy.");
				} else {
					player.getActionSender()
							.sendMessage("You eat the " + item.getDefinition().getName().toLowerCase() + ".");
				}
				player.getCombatState().setLastAte(food);
				int increasedBy = 0;
				int beforeHitpoints = player.getSkills().getLevel(Skills.HITPOINTS);

				if (food == Food.ANGLERFISH) {
					player.getSkills().increaseLevelToSetMaximum(Skills.HITPOINTS, food.getHeal(), player.getSkills().getLevelForExperience(Skills.HITPOINTS) + Constants.getModification(player.getSkills().getLevelForExperience(Skills.HITPOINTS)));
				} else {
					player.getSkills().increaseLevelToMaximum(Skills.HITPOINTS, food.getHeal());
				}

				increasedBy = player.getSkills().getLevel(Skills.HITPOINTS) - beforeHitpoints;
				if (increasedBy > 0 && food != Food.PURPLE_SWEETS) {
					/**
					 * Only show the message of healing if we actually healed.
					 */
					player.getActionSender().sendMessage("It heals some health.");
				}
				if (food == Food.PURPLE_SWEETS) {
					int energy = player.asPlayer().getRunEnergy();
					int add = Utility.random(2, 5);
					if (energy + add > 100)
						energy = 100;
					else
						energy = energy + add;
					player.setRunEnergy(energy);
					player.getActionSender().sendRunEnergy();
					player.getInventory().remove(new Item(food.getId(), 1));
				}

				/**
				 * If the item has a new id, add it (e.g. cakes decreasing in
				 * amount).
				 */
				if (food.getNewId() != -1) {
					player.getInventory().add(new Item(food.getNewId(), 1));
				}
			}
			} else if (drink != null && player.getCombatState().canDrink()) {
				/**
				 * Drink
				 */
				player.playAnimation(Animation.create(829));

				/**
				 * Stops the mob from drinking for 3 cycles (1.8 secs).
				 */
				player.getCombatState().setCanDrink(false);
				World.getWorld().schedule(new Task(2) {
					public void execute() {
						player.getCombatState().setCanDrink(true);
						this.stop();
					}
				});
				if (item.getDefinition() == null) {
					return;
				}
				/**
				 * Potion Types.
				 */
				String potionName = item.getDefinition().getName().toLowerCase()
						.substring(0, item.getDefinition().getName().length() - 3).replaceAll(" potion", "");
				switch (drink.getPotionType()) {
				case NORMAL_POTION:
					player.getActionSender().sendMessage("You drink some of your " + potionName + " potion.");
					for (int i = 0; i < drink.getSkills().length; i++) {
						int skill = drink.getSkill(i);
						int modification = (int) Math.floor((drink == Drink.RANGE_POTION ? 4 : 3)
								+ (player.getSkills().getLevelForExperience(skill) * 0.1));
						player.getSkills().increaseLevelToMaximumModification(skill, modification);
					}
					break;
				case SUPER_POTION:
					player.getActionSender().sendMessage("You drink some of your " + potionName + " potion.");
					for (int i = 0; i < drink.getSkills().length; i++) {
						int skill = drink.getSkill(i);
						int modification = (int) Math
								.floor(5 + (player.getSkills().getLevelForExperience(skill) * 0.15));
						player.getSkills().increaseLevelToMaximumModification(skill, modification);
					}
					break;
				case PRAYER_POTION:
					player.getActionSender().sendMessage("You drink some of your restore prayer potion.");
					for (int i = 0; i < drink.getSkills().length; i++) {
						int skill = drink.getSkill(i);
						int modification = (int) Math
								.floor(7 + (player.getSkills().getLevelForExperience(skill) * 0.25));
						/**
						 * Holy wrench increases prayer restoration.
						 */
						if (skill == Skills.PRAYER) {
							if (player.getInventory().contains(6714)) {
								modification++;
								if (player.getSkills().getLevelForExperience(Skills.PRAYER) >= 40) {
									modification++;
								}
								if (player.getSkills().getLevelForExperience(Skills.PRAYER) >= 70) {
									modification++;
								}
							}
							player.getSkills().increasePrayerPoints(modification);
						} else {
							player.getSkills().increaseLevelToMaximum(skill, modification);
						}
					}
					break;
				case RESTORE:
				case SUPER_RESTORE:
					player.getActionSender().sendMessage("You drink some of your " + potionName + " potion.");
					for (int i = 0; i < drink.getSkills().length; i++) {
						int skill = drink.getSkill(i);
						int modification = (int) (player.getSkills().getLevelForExperience(skill) / 3);
						/**
						 * Holy wrench increases prayer restoration.
						 */
						if (skill == Skills.PRAYER) {
							if (player.getInventory().contains(6714)) {
								modification++;
								if (player.getSkills().getLevelForExperience(Skills.PRAYER) >= 40) {
									modification++;
								}
								if (player.getSkills().getLevelForExperience(Skills.PRAYER) >= 70) {
									modification++;
								}
							}
							player.getSkills().increasePrayerPoints(modification);
						} else {
							player.getSkills().increaseLevelToMaximum(skill, modification);
						}
					}
					break;
				case SANFEW_SERUM:
					player.getActionSender().sendMessage("You drink some of your " + potionName + " potion.");
					for (int i = 0; i < drink.getSkills().length; i++) {
						int skill = drink.getSkill(i);
						int modification = (int) (player.getSkills().getLevelForExperience(skill) / 3);
						/**
						 * Holy wrench increases prayer restoration.
						 */
						if (skill == Skills.PRAYER) {
							if (player.getInventory().contains(6714)) {
								modification++;
								if (player.getSkills().getLevelForExperience(Skills.PRAYER) >= 40) {
									modification++;
								}
								if (player.getSkills().getLevelForExperience(Skills.PRAYER) >= 70) {
									modification++;
								}
							}
							player.getSkills().increasePrayerPoints(modification);
						} else {
							player.getSkills().increaseLevelToMaximum(skill, modification);
						}
						player.infection = 0;
						player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
					}

					break;
				case PLUS_5:
					player.getActionSender().sendMessage("You drink some of your " + potionName + " potion.");
					for (int i = 0; i < drink.getSkills().length; i++) {
						int skill = drink.getSkill(i);
						int modification = 5;
						player.getSkills().increaseLevelToMaximumModification(skill, modification);
					}
					break;
				case SARADOMIN_BREW:
					player.getActionSender().sendMessage("You drink some of the foul liquid.");
					for (int i = 0; i < drink.getSkills().length; i++) {
						int skill = drink.getSkill(i);
						if (skill == Skills.HITPOINTS) {
							int hitpointsModification = (int) (player.getSkills()
									.getLevelForExperience(Skills.HITPOINTS) * 0.15);
							player.getSkills().increaseLevelToMaximumModification(skill,
									hitpointsModification);
						} else if (skill == Skills.DEFENCE) {
							int defenceModification = (int) (player.getSkills()
									.getLevelForExperience(Skills.DEFENCE) * 0.25);
							player.getSkills().increaseLevelToMaximumModification(skill,
									defenceModification);
						} else {
							int modification = (int) (player.getSkills().getLevel(skill) * 0.10);
							player.getSkills().decreaseLevelToOne(skill, modification);
						}
					}
					break;
				case ZAMORAK_BREW:
					player.getActionSender().sendMessage("You drink some of the foul liquid.");
					for (int i = 0; i < drink.getSkills().length; i++) {
						int skill = drink.getSkill(i);
						if (skill == Skills.ATTACK) {
							int attackModification = (int) Math.floor(2
									+ (player.getSkills().getLevelForExperience(Skills.ATTACK)) * 0.20);
							player.getSkills().increaseLevelToMaximumModification(skill,
									attackModification);
						} else if (skill == Skills.STRENGTH) {
							int strengthModification = (int) Math.floor(
									2 + (player.getSkills().getLevelForExperience(Skills.STRENGTH)
											* 0.12));
							player.getSkills().increaseLevelToMaximumModification(skill,
									strengthModification);
						} else if (skill == Skills.PRAYER) {
							int prayerModification = (int) Math.floor(
									player.getSkills().getLevelForExperience(Skills.STRENGTH) * 0.10);
							player.getSkills().increaseLevelToMaximum(skill, prayerModification);
						} else if (skill == Skills.DEFENCE) {
							int defenceModification = (int) Math
									.floor(2 + (player.getSkills().getLevelForExperience(Skills.DEFENCE)
											* 0.10));
							player.getSkills().decreaseLevelToZero(skill, defenceModification);
						} else if (skill == Skills.HITPOINTS) {
							World.getWorld().schedule(new Task(3) {
								@Override
								public void execute() {
									int hitpointsModification = (int) Math.floor(
											2 + (player.getSkills().getLevel(Skills.HITPOINTS) * 0.10));
									if (player.getSkills().getLevel(Skills.HITPOINTS)
											- hitpointsModification < 0) {
										hitpointsModification = player.getSkills()
												.getLevel(Skills.HITPOINTS);
									}
									player.damage(new Hit(hitpointsModification));
									this.stop();
								}
							});
						}
					}
					break;
				case ANTIPOISON:
				case SUPER_ANTIPOISON:
					player.getActionSender().sendMessage("You drink some of your " + item.getDefinition().getName()
							.toLowerCase().substring(0, item.getDefinition().getName().length() - 3) + ".");
					player.infection = 0;
					player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
					break;
				case BEER:
					player.getActionSender().sendMessage("You drink the beer. You feel slightly reinvigorated...");
					player.getActionSender().sendMessage("...and slightly dizzy too.");
					for (int i = 0; i < drink.getSkills().length; i++) {
						int skill = drink.getSkill(i);
						if (skill == Skills.ATTACK) {
							int attackModification = (int) (player.getSkills()
									.getLevelForExperience(Skills.STRENGTH) * 0.07);
							player.getSkills().decreaseLevelToZero(Skills.ATTACK, attackModification);
						} else if (skill == Skills.STRENGTH) {
							int strengthModification = (int) (player.getSkills()
									.getLevelForExperience(Skills.STRENGTH) * 0.04);
							player.getSkills().increaseLevelToMaximumModification(Skills.STRENGTH,
									strengthModification);
						}
					}
					break;
				case WINE:
					player.getActionSender().sendMessage("You drink the wine. You feel slightly reinvigorated...");
					player.getActionSender().sendMessage("...and slightly dizzy too.");
					for (int i = 0; i < drink.getSkills().length; i++) {
						int skill = drink.getSkill(i);
						if (skill == Skills.ATTACK) {
							int attackModification = 2;
							player.getSkills().decreaseLevelToZero(Skills.ATTACK, attackModification);
						} else if (skill == Skills.HITPOINTS) {
							player.getSkills().increaseLevelToMaximum(Skills.HITPOINTS, 11);
						}
					}
					break;
				case ANTIFIRE:
					player.getActionSender().sendMessage("You drink some of your " + item.getDefinition().getName()
							.toLowerCase().substring(0, item.getDefinition().getName().length() - 3) + ".");
					player.setAttribute("antiFire", System.currentTimeMillis());
					break;
				case EXTENDED_ANTIFIRE:
					player.getActionSender().sendMessage("You drink some of your " + item.getDefinition().getName()
							.toLowerCase().substring(0, item.getDefinition().getName().length() - 3) + ".");
					player.setAttribute("extended_antiFire", System.currentTimeMillis());
					break;
				case ANTI_VENOM:
					player.getActionSender().sendMessage("You drink some of your " + item.getDefinition().getName()
							.toLowerCase().substring(0, item.getDefinition().getName().length() - 3) + ".");
					player.setVenomImmunity(0);
					player.infection = 0;
					player.setLastVenomCure(System.currentTimeMillis());
					player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
					break;
				case ANTI_VENOM_PLUS:
					player.getActionSender().sendMessage("You drink some of your " + item.getDefinition().getName()
							.toLowerCase().substring(0, item.getDefinition().getName().length() - 3) + ".");
					player.setVenomImmunity(TimeUnit.MINUTES.toMillis(5));
					player.infection = 0;
					player.setLastVenomCure(System.currentTimeMillis());
					player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
					break;
				case ENERGY:
					player.getActionSender().sendMessage("You drink some of your " + item.getDefinition().getName()
							.toLowerCase().substring(0, item.getDefinition().getName().length() - 3) + ".");
					int energy = 10 + player.getRunEnergy();
					if (energy > 100) {
						energy = 100;
					}
					player.setRunEnergy(energy);
					player.getActionSender().sendRunEnergy();
					break;
				case SUPER_ENERGY:
					player.getActionSender().sendMessage("You drink some of your " + item.getDefinition().getName()
							.toLowerCase().substring(0, item.getDefinition().getName().length() - 3) + ".");
					energy = 20 + player.getRunEnergy();
					if (energy > 100) {
						energy = 100;
					}
					player.setRunEnergy(energy);
					player.getActionSender().sendRunEnergy();
					break;
				case STAMINA_POTION:
					player.getActionSender().sendMessage("You drink some of your " + item.getDefinition().getName()
							.toLowerCase().substring(0, item.getDefinition().getName().length() - 3) + ".");
					energy = 20 + player.getRunEnergy();
					if (energy > 100) {
						energy = 100;
					}
					player.setRunEnergy(energy);
					player.getActionSender().sendRunEnergy();
					player.setAttribute("staminaPotion", true);
					// World.getWorld().submit(new
					// StaminaPotionTick(getEntity()));
					break;
				}
				int currentPotionDose = 0;
				for (int i = 0; i < drink.getIds().length; i++) {
					if (item.getId() == drink.getId(i)) {
						currentPotionDose = i + 1;
						break;
					}
				}
				if (drink.getPotionType() != PotionType.BEER && drink.getPotionType() != PotionType.WINE) {
					player.getActionSender().sendMessage(currentPotionDose > 1 ? ("You have " + (currentPotionDose - 1) + " dose" + (currentPotionDose > 2 ? "s" : "") + " of potion left.") : "You have finished your potion.");
				}
				int newPotion = 229;
				if (currentPotionDose > 1) {
					newPotion = drink.getId(currentPotionDose - 2);
				}
				player.getInventory().setSlot(slot, new Item(newPotion, player.getInventory().get(slot).getAmount()));
		}
	}
}
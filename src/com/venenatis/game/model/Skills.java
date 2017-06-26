package com.venenatis.game.model;

import com.venenatis.game.constants.Constants;
import com.venenatis.game.content.skills.SkillData;
import com.venenatis.game.content.skills.prayer.Prayer;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.Graphic;
import com.venenatis.game.model.masks.UpdateFlags.UpdateFlag;
import com.venenatis.game.util.Utility;

/**
 * Represents a player's skill and experience levels.
 * 
 * @author Graham Edgecombe
 * 
 */
public class Skills {
	
	/**
	 * The number of skills.
	 */
	public static final int SKILL_COUNT = 25;

	/**
	 * The largest allowed experience.
	 */
	public static final double MAXIMUM_EXP = 200000000;

	/**
	 * The skill names.
	 */
	public static final String[] SKILL_NAME = { "Attack", "Defence", "Strength", "Hitpoints", "Range", "Prayer", "Magic", "Cooking", "Woodcutting", "Fletching", "Fishing", "Firemaking", "Crafting", "Smithing", "Mining", "Herblore", "Agility", "Thieving", "Slayer", "Farming", "Runecrafting",
	          "Hunter", "Construction" };

	/**
	 * Constants for the skill numbers.
	 */
	public static final int ATTACK = 0, DEFENCE = 1, STRENGTH = 2, HITPOINTS = 3, RANGE = 4, PRAYER = 5, MAGIC = 6, COOKING = 7, WOODCUTTING = 8, FLETCHING = 9, FISHING = 10, FIREMAKING = 11, CRAFTING = 12, SMITHING = 13, MINING = 14, HERBLORE = 15, AGILITY = 16, THIEVING = 17, SLAYER = 18,
	          FARMING = 19, RUNECRAFTING = 20, HUNTER = 21, CONSTRUCTION = 22;
	
	/**
	 * The player object.
	 */
	private Player player;
	
	/**
	 * The experience counter.
	 */
	private double expCounter;
	
	/**
	 * The levels array.
	 */
	private int[] levels = new int[SKILL_COUNT];

	/**
	 * The experience array.
	 */
	private double[] exps = new double[SKILL_COUNT];

	/**
	 * Creates a skills object.
	 * 
	 * @param player
	 *            The player whose skills this object represents.
	 */
	public Skills(Player player) {
		this.player = player;
		for (int i = 0; i < SKILL_COUNT; i++) {
			levels[i] = 1;
			exps[i] = 0;
		}
		levels[3] = 10;
		exps[3] = 1184;
	}

    /**
     * Sets the mob's prayer points.
     *
     * @param prayerPoints The amount of prayer points to set.
     */
    public void setPrayerPoints(double prayerPoints, boolean update) {
        int lvlBefore = (int) Math.ceil(player.getPrayerPoint());
        player.setPrayerPoint(prayerPoints);
        int lvlAfter = (int) Math.ceil(player.getPrayerPoint());
        if (update && (lvlBefore - lvlAfter >= 1 || lvlAfter - lvlBefore >= 1) && player != null) {
        	player.getActionSender().sendSkillLevel(PRAYER);
        }
    }

    /**
     * Increases the mob's prayer points to its maxmimum.
     *
     * @param modification The amount to increase by.
     */
    public void increasePrayerPoints(double modification) {
        if (player.getPrayerPoint() < getLevelForExperience(PRAYER)) {
            setPrayerPoints(player.getPrayerPoint() + modification >= getLevelForExperience(PRAYER) ? getLevelForExperience(PRAYER) : player.getPrayerPoint() + modification, true);
        }
    }

    /**
     * Decreases the mob's prayer points to its minimum.
     *
     * @param modification The amount to increase by.
     */
    public void decreasePrayerPoints(double modification) {
        if (player.getPrayerPoint() > 0) {
            setPrayerPoints(player.getPrayerPoint() - modification, true);
        }
    }
	
	public void handleLevelUp(int skillId) {
		final SkillData skillData = SkillData.values()[skillId];
		if (skillData == null)
			return;

		final String line1 = "Congratulations! You've just advanced " + Utility.getAOrAn(skillData.toString()) + " " + skillData + " level!";
		final String line2 = "You have reached level " + getLevelForExperience(skillId) + "!";

		player.getActionSender().sendMessage("You've reached " + Utility.getAOrAn(skillData.toString()) + " " + skillData + " level of " + getLevelForExperience(skillId) + ".");

		player.getActionSender().sendString(line1, skillData.getFirstLine());
		player.getActionSender().sendString(line2, skillData.getSecondLine());
		player.getActionSender().sendChatInterface(skillData.getChatbox());
		if(getLevel(skillData.getId()) == 99) {
			player.getActionSender().sendMessage("<col=8b0000>Well done! You've achieved the highest possible level in this skill!</col>");
		}
		if (skillData.getId() < 7 || skillData.getId() == 21) {
			player.combatLevel = getCombatLevel();
			getCombatLevel();
			player.getActionSender().sendString("Combat Level: " + player.getSkills().getCombatLevel(), 3983);
		}
	}
	
	/**
	 * Gets the total level.
	 * 
	 * @return The total level.
	 */
	public int getTotalLevel() {
		int total = 0;
		for (int i = 0; i < levels.length; i++) {
			total += getLevelForExperience(i);
		}
		return total;
	}
	
	/**
	 * Gets the combat level.
	 * 
	 * @return The combat level.
	 */
	public int getCombatLevel() {
		final int attack = getLevelForExperience(0);
		final int defence = getLevelForExperience(1);
		final int strength = getLevelForExperience(2);
		final int hp = getLevelForExperience(3);
		final int prayer = getLevelForExperience(5);
		final int ranged = getLevelForExperience(4);
		final int magic = getLevelForExperience(6);
		int combatLevel = 3;
		combatLevel = (int) ((defence + hp + Math.floor(prayer / 2)) * 0.2535) + 1;
		final double melee = (attack + strength) * 0.325;
		final double ranger = Math.floor(ranged * 1.5) * 0.325;
		final double mage = Math.floor(magic * 1.5) * 0.325;
		if (melee >= ranger && melee >= mage) {
			combatLevel += melee;
		} else if (ranger >= melee && ranger >= mage) {
			combatLevel += ranger;
		} else if (mage >= melee && mage >= ranger) {
			combatLevel += mage;
		}
		if (combatLevel <= 126) {
			return combatLevel;
		} else {
			return 126;
		}
	}
	
	/**
	 * Sets a skill.
	 * 
	 * @param skill
	 *            The skill id.
	 * @param level
	 *            The level.
	 * @param exp
	 *            The experience.
	 */
	public void setSkill(int skill, int level, double exp) {
		levels[skill] = level;
		exps[skill] = exp;
		player.getActionSender().sendSkillLevel(skill);
	}
	
	/**
	 * Sets a level.
	 * 
	 * @param skill
	 *            The skill id.
	 * @param level
	 *            The level.
	 */
	public void setLevel(int skill, int level) {
		levels[skill] = level;
		player.getActionSender().sendSkillLevel(skill);
		//System.out.println("skill "+skill+ " level "+level);
	}

	/**
	 * Sets experience.
	 * 
	 * @param skill
	 *            The skill id.
	 * @param exp
	 *            The experience.
	 */
	public void setExperience(int skill, double exp) {
		int oldLvl = getLevelForExperience(skill);
		exps[skill] = exp;
		player.getActionSender().sendSkillLevel(skill);
		int newLvl = getLevelForExperience(skill);
		if (oldLvl != newLvl) {
			player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
		}
	}

	/**
	 * Increments a level.
	 * 
	 * @param skill
	 *            The skill to increment.
	 */
	public void incrementLevel(int skill) {
		levels[skill]++;
		player.getActionSender().sendSkillLevel(skill);
	}

	/**
	 * Decrements a level.
	 * 
	 * @param skill
	 *            The skill to decrement.
	 */
	public void decrementLevel(int skill) {
		levels[skill]--;
		player.getActionSender().sendSkillLevel(skill);
	}

	/**
	 * Detracts a given level a given amount.
	 * 
	 * @param skill
	 *            The level to detract.
	 * @param amount
	 *            The amount to detract from the level.
	 */
	public void detractLevel(int skill, int amount) {
		if (levels[skill] == 0) {
			amount = 0;
		}
		if (amount > levels[skill]) {
			amount = levels[skill];
		}
		levels[skill] = levels[skill] - amount;
		player.getActionSender().sendSkillLevel(skill);
	}

	/**
	 * Normalizes a level (adjusts it until it is at its normal value).
	 * 
	 * @param skill
	 *            The skill to normalize.
	 */
	public void normalizeLevel(int skill) {
		int norm = getLevelForExperience(skill);
		if (levels[skill] > norm) {
			levels[skill]--;
			player.getActionSender().sendSkillLevel(skill);
		} else if (levels[skill] < norm) {
			levels[skill]++;
			player.getActionSender().sendSkillLevel(skill);
		}
	}

	/**
	 * Gets a level.
	 * 
	 * @param skill
	 *            The skill id.
	 * @return The level.
	 */
	public int getLevel(int skill) {
		return levels[skill];
	}

	/**
	 * Gets a level by experience.
	 * 
	 * @param skill
	 *            The skill id.
	 * @return The level.
	 */
	public int getLevelForExperience(int skill) {
		double exp = exps[skill];
		int points = 0;
		int output = 0;
		if (exp >= 13034430) {
			return 99;
		}
		for (int lvl = 1; lvl <= 99; lvl++) {
			points += Math.floor((double) lvl + 300.0 * Math.pow(2.0, (double) lvl / 7.0));
			output = (int) Math.floor(points / 4);
			if (output >= exp)
				return lvl;
		}
		return 1;
	}

	/**
	 * Gets a experience from the level.
	 * 
	 * @param level
	 *            The level.
	 * @return The experience.
	 */
	public int getXPForLevel(int level) {
		int points = 0;
		int output = 0;
		for (int lvl = 1; lvl <= level; lvl++) {
			points += Math.floor((double) lvl + 300.0 * Math.pow(2.0, (double) lvl / 7.0));
			if (lvl >= level) {
				return output;
			}
			output = (int) Math.floor(points / 4);
		}
		return 0;
	}

	/**
	 * Gets experience.
	 * 
	 * @param skill
	 *            The skill id.
	 * @return The experience.
	 */
	public double getExperience(int skill) {
		return exps[skill];
	}
	
	public void setExpCounter(int expCounter) {
		this.expCounter = expCounter;
	}

	public int getExpCounter() {
		return (int) expCounter;
	}
	
	/**
     * Adds experience.
     *
     * @param skillId The skill.
     * @param experience   The experience to add.
     */
	public void addExperience(int skillId, double experience) {

		boolean combatSkill = skillId >= ATTACK && skillId <= MAGIC && skillId != PRAYER;
        int multi = combatSkill ? Constants.EXP_MODIFIER : Constants.EXP_MODIFIER;
        int oldLevel = getLevelForExperience(skillId);
        

        //TODO add 1.25 and 1.50 exp additional for extreme/gold donator
        exps[skillId] += experience * multi;
        expCounter += experience*multi;
        player.getActionSender().sendExperienceCounter(skillId, (int) (experience*multi));
        //player.getActionSender().sendMessage("Exp received: "+experience+ " times "+multi+" so "+(experience*multi));
       
		if (exps[skillId] > MAXIMUM_EXP) {
            exps[skillId] = MAXIMUM_EXP;
        }
        
        int newLevel = getLevelForExperience(skillId);
        int levelDiff = newLevel - oldLevel;
        if (levelDiff > 0) {
            levels[skillId] += levelDiff;
            player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
			player.playGraphics(Graphic.highGraphic(199));
			handleLevelUp(skillId);
        }
        if (player != null) {
        	player.getActionSender().sendSkillLevel(skillId);
        }
    }
	
	
	/**
     * Decreases a level to 0.
     *
     * @param skill        The skill id.
     * @param modification The modification amount.
     */
    public void decreaseLevelToZero(int skill, int modification) {
        if (levels[skill] > 0) {
            setLevel(skill, levels[skill] - modification <= 0 ? 0 : levels[skill] - modification);
        }
    }
    
    /**
     * Checks if a level is below it's normal level for experience + a certain modification.
     * This is used for consumables, e.g. 130/99 str, then drinking a super strength potion and it returning to 118/99.
     *
     * @param skill        The skill id.
     * @param modification The modification amount.
     * @return If the level is below it's normal level + a certain modification.
     */
    public boolean isLevelBelowOriginalModification(int skill, int modification) {
        return levels[skill] < (getLevelForExperience(skill) + modification);
    }
    
    /**
     * Checks if a level is below it's normal level for experience.
     * This is used for consumables, e.g. 130/99hp, then eating a manta ray and it returning to 99/99hp.
     *
     * @param skill The skill id.
     * @return If the level is below it's normal level.
     */
    public boolean isLevelBelowOriginal(int skill) {
        return levels[skill] < getLevelForExperience(skill);
    }
    
    /**
     * Increases a level to its level for experience, depending on the modification amount.
     *
     * @param skill        The skill id.
     * @param modification The modification amount.
     */
	public void increaseLevelToMaximum(int skill, int modification) {
        if (isLevelBelowOriginal(skill)) {
            setLevel(skill, levels[skill] + modification >= getLevelForExperience(skill) ? getLevelForExperience(skill) : levels[skill] + modification);
        }
    }
    
    /**
     * Increases a level to its level for experience + modification amount.
     *
     * @param skill        The skill id.
     * @param modification The modification amount.
     */
    public void increaseLevelToMaximumModification(int skill, int modification) {
        if (isLevelBelowOriginalModification(skill, modification)) {
            setLevel(skill, levels[skill] + modification >= (getLevelForExperience(skill) + modification) ? (getLevelForExperience(skill) + modification) : levels[skill] + modification);
        }
    }
    
    /**
     * Decreases a level.
     *
     * @param skill  The skill to decrease.
     * @param amount The amount to decrease the skill by.
     */
    public void decreaseLevel(int skill, int amount) {
        levels[skill] -= amount;
        if (player.getActionSender() != null) {
        	player.getActionSender().sendSkillLevel(skill);
        }
    }

	public void addLevel(int skill, int amount) {
		if (levels[skill] == 0) {
			amount = 0;
		}
		if (amount > levels[skill]) {
			amount = levels[skill];
		}
		levels[skill] = levels[skill] + amount;
		player.getActionSender().sendSkillLevel(skill);
	}
	
	public static final boolean isSuccess(Player p, int skillId, int levelRequired) {
		double level = p.getSkills().getLevels()[skillId];
		double req = levelRequired;
		double successChance = Math.ceil((level * 50.0D - req * 15.0D) / req / 3.0D * 4.0D);
		int roll = Utility.random(99);

		if (successChance >= roll) {
			return true;
		}

		return false;
	}

	public int[] getLevels() {
		return levels;
	}

	public double[] getExperience() {
		return exps;
	}

	public int[] getAllDynamicLevels() {
		return levels;
	}

	public double[] getAllXP() {
		return exps;
	}

	public void setAllExp(double[] skillXP) {
		exps = skillXP;
	}

	public void setDynamicLevels(int[] dynamicLevels) {
		levels = dynamicLevels;
	}

	 public Prayer getPrayer() {
        return new Prayer(player);
    }
}

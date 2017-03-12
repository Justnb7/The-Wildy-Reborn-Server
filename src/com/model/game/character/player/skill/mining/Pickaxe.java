package com.model.game.character.player.skill.mining;

import java.util.ArrayList;
import java.util.List;

import com.model.game.character.Animation;
import com.model.game.character.player.Player;
import com.model.game.character.player.Skills;

/**
 * Represents types of pick axes.
 * @author Michael (Scu11)
 *
 */
public enum Pickaxe {

	/**
	 * Infernal pickaxe.
	 */
	INFERNAL(8, 13243, 61, Animation.create(7139), 12),
	
	/**
	 * Dragon pickaxe.
	 */
	DRAGON_OR(7, 12797, 61, Animation.create(335), 12),
	
	DRAGON(6, 11920, 61, Animation.create(7139), 12),

	/**
	 * Rune pickaxe.
	 */
	RUNE(5, 1275, 41, Animation.create(624), 10),

	/**
	 * Adamant pickaxe.
	 */
	ADAMANT(4, 1271, 31, Animation.create(628), 7),

	/**
	 * Mithril pickaxe.
	 */
	MITHRIL(3, 1273, 21, Animation.create(629), 5),

	/**
	 * Steel pickaxe.
	 */
	STEEL(2, 1269, 6, Animation.create(627), 3),

	/**
	 * Iron pickaxe.
	 */
	IRON(1, 1267, 1, Animation.create(626), 2),

	/**
	 * Bronze pickaxe.
	 */
	BRONZE(0, 1265, 1, Animation.create(625), 0);
	
	/**
	 * The priority of the pickaxe. The higher the value, the higher the priority.
	 * This serves as a replacement to the {@link Enum.ordinal()} function.
	 */
	private final int priority;
	
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
	 * The amount of cycles operating this pickaxe will reduce the time by 
	 */
	private final int extractionReduction;

	/**
     * A list of pick axes.
     */
    private static List<Pickaxe> pickaxes = new ArrayList<Pickaxe>();

    /**
     * Gets the list of pick axes.
     * @return The list of pick axes.
     */
    public static List<Pickaxe> getPickaxes() {
        return pickaxes;
    }

    /**
     * Populates the pick axe map.
     */
    static {
        for(Pickaxe pickaxe : Pickaxe.values()) {
            pickaxes.add(pickaxe);
        }
    }
	
	private Pickaxe(int priority, int id, int level, Animation animation, int extractionReduction) {
		this.priority = priority;
		this.id = id;
		this.level = level;
		this.animation = animation;
		this.extractionReduction = extractionReduction;
	}
	
	
	/**
	 * The level of priority the pickaxe has.
	 * @return	the level of priority
	 */
	public int getPriority() {
		return priority;
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
	
	/**
	 * The amount of reduction this pickaxe effects the extraction process
	 * @return	the reduction
	 */
	public int getExtractionReduction() {
		return extractionReduction;
	}
	
	/**
	 * Attempts to retrieve the best pickaxe the player has on their person.
	 * @param player	the	player we're trying to get the best pickaxe of
	 * 
	 * @return the best pickaxe the person has will be returned, otherwise 
	 * null to ensure the player has no pickaxe;
	 */
	public static Pickaxe getBestPickaxe(Player player) {
		Pickaxe pickaxe = null;
		for (Pickaxe pick : pickaxes) {
			if (player.getItems().playerHasItem(pick.id) || player.getItems().isWearingItem(pick.id)) {
				if (player.getSkills().getLevel(Skills.MINING) >= pick.level) {
					if (pickaxe == null || pick.priority > pickaxe.priority) {
						pickaxe = pick;
					}
				}
			}
		}
		return pickaxe;
	}
}

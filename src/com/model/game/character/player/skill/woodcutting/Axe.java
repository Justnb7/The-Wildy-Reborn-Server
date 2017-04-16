package com.model.game.character.player.skill.woodcutting;

import com.model.game.character.Animation;
import com.model.game.character.player.Player;
import com.model.game.character.player.Skills;

public enum Axe {
	
	BRONZE_AXE(1351, 1, Animation.create(879), 1.0),
	IRON_AXE(1349, 1, Animation.create(877), 1.0),
	STEEL_AXE(1353, 6, Animation.create(875), .9),
	BLACK_AXE(1361, 6, Animation.create(873), .9),
	MITHRIL_AXE(1355, 21, Animation.create(871), .80),
	ADAMANT_AXE(1357, 31, Animation.create(869), .65),
	RUNE_AXE(1359, 41, Animation.create(867), .55),
	DRAGON_AXE(6739, 61, Animation.create(2846), .45),
	INFERNAL_AXE(13241, 61, Animation.create(2117), .45),
	THIRD_AGE_AXE(20011, 61, Animation.create(7264), .45);
	
	private int itemId, levelRequired;
	private Animation animation;
	private double chopSpeed;
	
	/**
	 * Constructs a new {@link Axe} used to cut down trees.
	 * @param itemId			the item identification value of the hatchet
	 * @param levelRequired		the level required for use
	 * @param animation			the animation displayed during use
	 * @param chopSpeed			the effectiveness of the hatchet when determining a log has been cut
	 */
	private Axe(int itemId, int levelRequired, Animation animation, double chopSpeed) {
		this.itemId = itemId;
		this.levelRequired = levelRequired;
		this.animation = animation;
		this.chopSpeed = chopSpeed;
	}
	
	/**
	 * The item id associated with the hatchet. 
	 * @return	the item id
	 */
	public int getItemId() {
		return itemId;
	}
	
	/**
	 * The level required to operate the hatchet whether its in your inventory or
	 * in your equipment.
	 * @return	the level required for operation
	 */
	public int getLevelRequired() {
		return levelRequired;
	}
	
	/**
	 * The animation displayed when the hatchet is being operated
	 * @return	the hatchet animation
	 */	
	public Animation getAnimation() {
		return animation;
	}
	
	/**
	 * The speed at which this axe effects log cut time
	 * @return	the chop speed of the hatchet
	 */
	public double getChopSpeed() {
		return chopSpeed;
	}
	
	/**
	 * Determines the best hatchet the player has in their inventory, or equipment.
	 * @param player	the player we're trying to find the best axe for
	 * @return			null if the player doesn't have a hatchet they can operate, otherwise
	 * the best hatchet on their person.
	 */
	public static Axe getBest(Player player) {
		Axe hatchet = null;
		for (Axe h : values()){
			if ((player.getItems().playerHasItem(h.itemId) || player.getEquipment().contains(h.itemId)) && player.getSkills().getLevel(Skills.WOODCUTTING) >= h.levelRequired) {
				if (hatchet == null) {
					hatchet = h;
					continue;
				}
				if (hatchet.levelRequired < h.levelRequired) {
					hatchet = h;
				}
			}
		}
		return hatchet;
	}
}
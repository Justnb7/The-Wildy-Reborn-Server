package com.model.game.character.player.content;

import com.model.game.character.player.Player;
import com.model.game.item.Item;

/**
 * @author Jason MacKeigan
 * @date Jan 5, 2015, 11:25:35 AM
 */
public class PotionCombinating {
	/**
	 * A single instance of the PotionMixing class
	 */
	private static PotionCombinating POTION_MIXING = new PotionCombinating();
	
	/**
	 * When an item is clicked we want to establish between items that are and are not potions
	 * @param item	the item we're determining this for
	 * @return	true if the item is a potion based on its item id, otherwise it will return false
	 */
	public boolean isPotion(Item item) {
		Potion potion = Potion.get(item.getId());
		return potion != null;
	}
	
	/**
	 * Determines if two potions have the same name. This allows us to ensure
	 * two potions are the same.
	 * @param potion1	the first potion
	 * @param potion2	the second potion
	 * @return	true if they both match.
	 */
	public boolean matches(Item potion1, Item potion2) {
		Potion p1 = Potion.get(potion1.getId());
		Potion p2 = Potion.get(potion2.getId());
		if (p1 == null || p2 == null) {
			return false;
		}
		return p1.equals(p2);
	}
	
	/**
	 * Mixes two single potions together to combine as one
	 * @param player	the player combining the potions
	 * @param potion1	the first potion
	 * @param potion2	the second potion
	 */
	public void mix(Player player, Item item1, Item item2) {
		if (!player.getInventory().contains(item1.getId(), item1.getAmount())) {
			return;
		}
		if (!player.getInventory().contains(item2.getId(), item2.getAmount())) {
			return;
		}
		Potion potion1 = Potion.get(item1.getId()); 
		Potion potion2 = Potion.get(item2.getId());
		if (potion1 == null || potion2 == null) {
			return;
		}
		if (potion1.isFull(item1.getId()) || potion2.isFull(item2.getId())) {
			player.getActionSender().sendMessage("You cannot mix a 4 dose potion.");
			return;
		}
		player.getInventory().remove(new Item(item1.getId(), item1.getAmount()));
		player.getInventory().remove(new Item(item2.getId(), item2.getAmount()));
		int dose1 = potion1.getDosage(item1.getId());
		int dose2 = potion2.getDosage(item2.getId());
		int sum = dose1 + dose2;
		if (sum >= 4) {
			item1 = new Item(potion1.full);
			if (sum - 4 > 0) {
				item2 = new Item(potion2.getItemId(sum - 4));
			} else {
				item2 = new Item(229);
			}
		} else {
			item1 = new Item(potion1.getItemId(sum));
			item2 = new Item(229);
		}
		player.getInventory().add(new Item(item1.getId(), item1.getAmount()));
		player.getInventory().add(new Item(item2.getId(), item2.getAmount()));
	}
	
	/**
	 * Retrieves the single instance of the PotionMixing class
	 * @return
	 */
	public static PotionCombinating get() {
		return POTION_MIXING;
	}
	
	public enum Potion {
	    STRENGTH(113, 115, 117, 119),
	    ATTACK(2428, 121, 123, 125),
	    RESTORE(2430, 127, 129, 131),
	    DEFENCE(2432, 133, 135, 137),
	    PRAYER(2434, 139, 141, 143),
	    FISHING(2438, 151, 153, 155),
	    RANGING(2444, 169, 171, 173),
	    ANTIFIRE(2452, 2454, 2456, 2458),
	    ENERGY(3008, 3010, 3012, 3014),
	    AGILITY(3032, 3034, 3036, 3038),
	    MAGIC(3040, 3042, 3044, 3046),
	    COMBAT(9739, 9741, 9743, 9745),
	    SARADOMIN_BREW(6685, 6687, 6689, 6691),
	    SUPER_ATTACK(2436, 145, 147, 149),
	    SUPER_STRENGTH(2440, 157, 159, 161),
	    SUPER_DEFENCE(2442, 163, 165, 167),
	    SUPER_ENERGY(3016, 3018, 3020, 3022),
	    SUPER_RESTORE(3024, 3026, 3028, 3030),
	    SUPER_COMBAT(12695, 12697, 12699, 12701),
	    OVERLOAD(11730, 11731, 11732, 11733);
	    
	    Potion(int full, int threeQuarters, int half, int quarter) {
	        this.quarter = quarter;
	        this.half = half;
	        this.threeQuarters = threeQuarters;
	        this.full = full;
	    }
	    
	    private int quarter, half, threeQuarters, full;
	    
	    public boolean isQuarter(int id) {
	    	return quarter == id;
	    }
	    
	    public boolean isHalf(int id) {
	    	return half == id;
	    }
	    
	    public boolean isThreeQuarters(int id) {
	    	return threeQuarters == id;
	    }
	    
	    public boolean isFull(int id) {
	    	return full == id;
	    }
	    
	    public int getDosage(int id) {
	    	return id == full ? 4 : id == threeQuarters ? 3 : id == half ? 2 : id == quarter ? 1 : 0;
	    }
	    
	    public int getItemId(int dosage) {
	    	return dosage == 4 ? full : dosage == 3 ? threeQuarters : dosage == 2 ? half : dosage == 1 ? quarter : 0;
	    }
	    
	    static Potion get(int id) {
	    	for (Potion p : values()) {
	    		if (p.full == id || p.half == id || p.quarter == id || p.threeQuarters == id) {
	    			return p;
	    		}
	    	}
	    	return null;
	    }
	}

}

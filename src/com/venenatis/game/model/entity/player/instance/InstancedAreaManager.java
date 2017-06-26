package com.venenatis.game.model.entity.player.instance;

import java.util.HashMap;
import java.util.Map;

import com.venenatis.game.model.entity.Boundary;
import com.venenatis.game.model.entity.player.Player;

/**
 * A class that manages all {@link InstancedArea} objects created. 
 * 
 * @author Jason MacKeigan
 * @date Jan 28, 2015, 1:07:55 PM
 */
public class InstancedAreaManager {
	
	/**
	 * A single instance of this class for global usage
	 */
	private static final InstancedAreaManager SINGLETON = new InstancedAreaManager();
	
	/**
	 * The maximum height of any one instance
	 */
	private static final int MAXIMUM_HEIGHT = 4 * 1024;
	
	/**
	 * A mapping of all {@InstancedArea} objects that are being operated on
	 * and are active.
	 */
	private Map<Integer, InstancedArea> active = new HashMap<>();
	
	/**
	 * A private empty {@link InstancedAreaManager} constructor exists to ensure that
	 * no other instance of this class can be created from outside this class.
	 */
	private InstancedAreaManager() { }
	
	/**
	 * Creates a new {@link SingleInstancedArea} object with the given params
	 * @param height
	 * 			the height of the new instance
	 * @param instance	
	 * 			the instance that will be added
	 * @return	
	 * 			null if no height can be found for this area, otherwise the new 
	 * 			{@link SingleInstancedArea} object will be returned.
	 */
	public void add(int height, InstancedArea instance) {
		active.put(height, instance);
	}
	
	/**
	 * Determines if the {@link InstancedArea} paramater exists within
	 * the mapping of active {@link InstancedArea} objects and can be 
	 * disposed of.
	 *  
	 * @param area	the instanced area 
	 * @return		true if the area exists in the mapping with the same height level
	 * 				and the same reference
	 */
	public boolean disposeOf(InstancedArea area) {
		if (area == null) 
			return false;
		int height = area.getHeight();
		if (!active.containsKey(height)) {
			return false;
		}
		InstancedArea found = active.get(height);
		if (!found.equals(area)) {
			return false;
		}
		active.remove(height);
		return true;
	}
	
	/**
	 * Creates a new {@link SingleInstancedArea} object with the given params
	 * @param player	the player for this instanced area
	 * @param boundary	the boundary of the area
	 * @return	null if no height can be found for this area, otherwise the new 
	 * {@link SingleInstancedArea} object will be returned.
	 */
	public InstancedArea createSingleInstancedArea(Player player, Boundary boundary) {
		int height = getNextOpenHeight();
		if (height == -1) {
			return null;
		}
		SingleInstancedArea area = new SingleInstancedArea(player, boundary, height);
		active.put(height, area);
		return area;
	}
	
	/**
	 * Retrieves an open height level by sifting through the mapping and attempting
	 * to retrive the lowest height level.
	 * @return	
	 * 		the next lowest, open height level will be returned otherwise -1
	 * 		will be returned. When -1 is returned it signifies that there are
	 * 		no heights open from 0 to {@link MAXIMUM_HEIGHT}.
	 */
	private int getNextOpenHeight() {
		for (int height = 0; height < MAXIMUM_HEIGHT; height += 4) {
			if(height >= MAXIMUM_HEIGHT) {
				return -1;
			}
			if (active.containsKey(height)) {
				continue;
			}
			return height;
		}
		return -1;
	}
	
	/**
	 * Retrieves the single instance of this class
	 * @return	the single instance
	 */
	public static InstancedAreaManager getSingleton() {
		return SINGLETON;
	}

	public int getNextOpenHeight(Boundary boundary) {
		for (int height = 0; height < MAXIMUM_HEIGHT; height += 4) {
			if(height >= MAXIMUM_HEIGHT) {
				return -1;
			}
			if (active.containsKey(height)) {
				continue;
			}
			return height;
		}
		return -1;
	}

}
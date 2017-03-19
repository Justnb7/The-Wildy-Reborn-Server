package com.model.game.character.npc;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

import com.model.game.character.player.Boundary;
import com.model.task.impl.NPCDeathTask;

public enum GroupRespawn {
	
	BANDOS_GROUP(new Boundary(2859, 5346, 2884, 5376), 60, 2215, 2216, 2217, 2218),
	ARMADYL_GROUP(new Boundary(2818, 5292, 2848, 5312), 60, 3162, 3163, 3164, 3165),
	ZAMORAK_GROUP(new Boundary(2913, 5313, 2942, 5338), 60, 3129, 3130, 3131, 3132),
	SARADOMIN_GROUP(new Boundary( 2889, 5258, 2907, 5276), 60, 2205, 2206, 2207, 2208);
	
	/**
	 * The perimeter of the group spawn
	 *  South west, North east.
	 */
	private Boundary boundary;
	
	/**
	 * The time it takes for all the npcs to respawn
	 */
	private int respawn;
	
	/**
	 * An array of npc id's that represent all the npcs in the group.
	 * Each npc must be death before the respawn time can continue.
	 */
	private int[] npcs;
	
	/**
	 * Creates a new group of npcs that spawn at the same time
	 * @param boundary	the perimeter that all npcs must be in to be in the group
	 * @param respawn	the time it takes for them all to respawn after all die
	 * @param npcs		the array of npcs that are included in the group
	 */
	GroupRespawn(Boundary boundary, int respawn, int... npcs) {
		if (Objects.isNull(boundary)) {
			throw new RuntimeException("Boundary cannot be null, it must represent a valid perimeter on the map.");
		}
		this.boundary = boundary;
		this.respawn = respawn;
		this.npcs = npcs;
	}
	
	/**
	 * Returns the boundary for the group
	 * @return the boundary
	 */
	public Boundary getBoundary() {
		return boundary;
	}
	
	/**
	 * Returns the time it takes for all npcs to respawn
	 * @return the respawn time
	 */
	public int getRespawnTime() {
		return respawn;
	}
	
	/**
	 * Returns the array of npcs
	 * @return the npcs
	 */
	public int[] getNpcs() {
		return npcs;
	}
	
	/**
	 * Determines if the npc is in the group
	 * @param npcType 	the npc
	 * @return			true if the npc is in the group
	 */
	public boolean contains(int npcType) {
		for (int groupNpc : npcs) {
			if (groupNpc == npcType) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * A set containing all of the elements of the NPCGroupRespawn enum. This set is 
	 * immutable because all groups are predetermined and cannot be changed.
	 */
	private static final Set<GroupRespawn> GROUPS = EnumSet.allOf(GroupRespawn.class);
	
	/**
	 * Determines if the npc is in a group by determining if it's type, and location corresponds
	 * with the predetermined boundary.
	 * @param npc	the npc
	 * @return		true if the npc is in the perimeter and is a valid npc
	 */
	public static boolean isInGroup(int npcType) {
		for (GroupRespawn group : GROUPS) {
			for (int npc : group.npcs) {
				if (npc == npcType) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Returns the NPCGroupRespawn element in corresponance to the array of npcs that
	 * contains the same value passed.
	 * @param npcType	the npc type
	 * @return			null if no group contains the npc type, otherwise the corresponding group
	 */
	public static GroupRespawn getGroup(int npcType) {
		for (GroupRespawn group : GROUPS) {
			for (int npc : group.npcs) {
				if (npc == npcType) {
					return group;
				}
			}
		}
		return null;
	}

	// If we're going to be spawned by our boss, not just 60s later from our death.
	public static boolean check_groups(NPC npc) {
		NPC boss = (NPC) npc.getAttributes().getOrDefault("boss_owner", null);
		if (boss != null) {
			//System.out.println("boss found for "+npc+" : "+boss);
			// If the boss isn't dead we just respawn 60s later as usual.
			// Otherwise, my nigga bandos graddor jr will respawn me.
			if (boss.isDead) {
				return true;
			}
		}
		return false;
	}

	public static void on_boss_spawned(NPC npc) {
		@SuppressWarnings("unchecked")
		ArrayList<NPC> minions = (ArrayList<NPC>) npc.getAttributes().getOrDefault("group_spawn_map", null);
		if (minions != null) {
			minions.stream().forEach(min -> {
				//System.out.println("minion "+min+" beglongs to "+npc);
				if (min.isDead) {
					NPCDeathTask.respawn(min);
				}
			});
		}
	}

}

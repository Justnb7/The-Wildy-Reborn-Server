package com.model.game.character.npc;

import com.model.game.World;
import com.model.game.character.player.Player;
import com.model.game.location.Location;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public final class NPCHandler {

	public void declare() {
        loadAutoSpawn("./data/text_files/npc_spawns.txt");
	}

	public static boolean loadAutoSpawn(String FileName) {
		String line = "";
		String token = "";
		String token2 = "";
		String token2_2 = "";
		String[] token3 = new String[4];
		boolean EndOfFile = false;
		BufferedReader characterfile = null;
		try {
			characterfile = Files.newBufferedReader(Paths.get(FileName));
		} catch (IOException fileex) {
			fileex.printStackTrace();
		}
		try {
			line = characterfile.readLine();
		} catch (IOException ioexception) {
			ioexception.printStackTrace();
		}
		while (!EndOfFile && line != null) {
			line = line.trim();
			int spot = line.indexOf("-");
			if (spot > -1) {
				token = line.substring(0, spot);
				token = token.trim();
				token2 = line.substring(spot + 1);
				token2 = token2.trim();
				token2_2 = token2.replaceAll("\t\t", "\t");
				token2_2 = token2_2.replaceAll("\t\t", "\t");
				token2_2 = token2_2.replaceAll("\t\t", "\t");
				token2_2 = token2_2.replaceAll("\t\t", "\t");
				token2_2 = token2_2.replaceAll("\t\t", "\t");
				token3 = token2_2.split("\t");
				if (token.equals("spawn")) {
					spawn(Integer.parseInt(token3[0]), Integer.parseInt(token3[1]), Integer.parseInt(token3[2]), Integer.parseInt(token3[3]), Integer.parseInt(token3[4]));
				}
			} else {
				if (line.equals("[ENDOFSPAWNLIST]")) {
					try {
						characterfile.close();
					} catch (IOException ignored) {
						ignored.printStackTrace();
					}
				}
			}
			try {
				line = characterfile.readLine();
			} catch (IOException ioexception1) {
				EndOfFile = true;
			}
		}
		try {
			characterfile.close();
		} catch (IOException ignored) {
		}
		return false;
	}
	
	public static void spawn(int npcType, int x, int y, int heightLevel, int WalkingType) {

		NPC npc = new NPC(npcType);

		npc.setLocation(Location.create(x, y, heightLevel));
		npc.makeX = x;
		npc.makeY = y;
        npc.walking_type = WalkingType;
		npc.setOnTile(x, y, heightLevel);
		if (World.getWorld().register(npc)) {
			// successfully added to game world
			handleForGroup(npc);
		}
		World.getWorld().register(npc);
	}
	
	public static NPC spawnNpc(Player player, int id, Location spawn, int walkingType, boolean attacksEnemy, boolean hasHeadIcon, boolean bossOffspring) {
		NPC npc = new NPC(id, spawn, walkingType);
		
		npc.setLocation(spawn);
		npc.makeX = spawn.getX();
		npc.makeY = spawn.getY();
		npc.walking_type = walkingType;
		npc.spawnedBy = player.getIndex();
		npc.setOnTile(spawn.getX(), spawn.getY(), spawn.getZ());
		npc.faceEntity(player);
		if (attacksEnemy) {
			npc.underAttack = true;
			if (player != null) {
				npc.targetId = player.getIndex();
			}
		}
		if (hasHeadIcon) {
			player.getActionSender().drawHeadIcon(1, npc.getIndex(), 0, 0);
		}
		if (bossOffspring) {
			npc.shouldRespawn = false;
		}
		World.getWorld().register(npc);
		return npc;
	}
	
	private static GroupRespawn tempGroup = null;
	private static NPC tempboss = null;
	
	/**
	 * This method links instances of NPCs to each other by using their Attribute system.
	 */
	private static void handleForGroup(NPC n) {
		GroupRespawn gr = null;
		//System.out.println("group check for "+n+" using "+tempGroup +" | "+tempboss); //go
		if (tempGroup == null) {
			gr = GroupRespawn.getGroup(n.getId());
			if (gr != null) {
				// We're a boss. Npc ID should be the first in the int[] array on this group.
				//System.out.println("Checking group [0] -> "+gr.getNpcs()[0] +" vs "+ n.getId());
				if (gr.getNpcs()[0] == n.getId()) {
					// Only set it to temp when we've identified the boss.
					tempGroup = gr;
					n.setAttribute("group_spawn_map", new ArrayList<NPC>());
					//System.out.println("boss "+n+" map set.");
					tempboss = n;
				}
			}
		} else {
			// Temp attrib is set. We've located a boss already in spawn.txt
			GroupRespawn bossgroup = GroupRespawn.getGroup(n.getId());
			if (bossgroup != null) {
				// We're a minion
				ArrayList<NPC> minion_list = tempboss.getAttribute("group_spawn_map", new ArrayList<NPC>());
				// Add the minion NPC instance to the bosses attributes
				minion_list.add(n);
				
				// Add a reference from the minion instance to the boss instance.
				n.setAttribute("boss_owner", tempboss);
				//System.out.println("minion now has boss reference");
				
				// The list of minions is full with the correct minions (3 in the case of bandos)
				// (not including the boss npc)
				if (bossgroup.getNpcs().length - 1 == minion_list.size()) {
					//System.out.println("finished map for "+tempboss);
					tempGroup = null; // Start again!
					tempboss = null;
				}
			}
		}
	}

	public static boolean isSpawnedBy(Player player, NPC npc) {
		if (player != null && npc != null)
			if (npc.spawnedBy == player.getIndex() || npc.targetId == player.getIndex())
				return true;
		return false;
	}
}
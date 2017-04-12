package com.model.game.character.npc;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

import com.model.game.World;
import com.model.game.character.npc.drops.NpcDropSystem;
import com.model.game.character.player.Player;
import com.model.game.character.player.content.KillTracker;
import com.model.game.character.player.content.KillTracker.KillEntry;
import com.model.game.character.player.content.achievements.AchievementType;
import com.model.game.character.player.content.achievements.Achievements;
import com.model.game.character.player.minigames.warriors_guild.AnimatedArmour;
import com.model.game.character.player.packets.out.SendKillFeedPacket;
import com.model.game.location.Position;
import com.model.utility.Utility;
import com.model.utility.json.definitions.NpcDefinition;
import com.model.utility.json.loader.NPCDefinitionLoader;

public final class NPCHandler {

	public static void declare() {
        Arrays.fill(NpcDefinition.getDefinitions(), null);
        new NPCDefinitionLoader().load();
        loadAutoSpawn("./Data/text_files/npc_spawns.txt");
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
					newNPC(Integer.parseInt(token3[0]), Integer.parseInt(token3[1]), Integer.parseInt(token3[2]), Integer.parseInt(token3[3]), Integer.parseInt(token3[4]));
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
	
	public static void newNPC(int npcType, int x, int y, int heightLevel, int WalkingType) {

		NPC newNPC = new NPC(npcType);

		newNPC.setAbsX(x);
		newNPC.setAbsY(y);
		newNPC.makeX = x;
		newNPC.makeY = y;
		newNPC.heightLevel = heightLevel;
        newNPC.walking_type = WalkingType;
		newNPC.setOnTile(x, y, heightLevel);
		if (World.getWorld().register(newNPC)) {
			// successfully added to game world
			handleForGroup(newNPC);
		}
	}
	
	public static NPC spawnNpc(Player player, int id, Position spawn, int walkingType, boolean attacksEnemy, boolean hasHeadIcon, boolean bossOffspring) {
		NPC npc = new NPC(id);
		
		npc.setAbsX(spawn.getX());
		npc.setAbsY(spawn.getY());
		npc.makeX = spawn.getX();
		npc.makeY = spawn.getY();
		npc.heightLevel = spawn.getZ();
		npc.walking_type = walkingType;
		npc.spawnedBy = player.getIndex();
		System.out.printf("Spawned npc id %d for player index %d on position %s%n", id, player.getIndex(), npc.getPosition());
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
	
	public static void dropItems(NPC npc) {
		if (npc.killedBy == -1) {
			return;
		}
		
		Player player = World.getWorld().getPlayers().get(npc.killedBy);
		if (player == null) {
			return;
		}

		if (npc != null) {
			/* Add kills to tracker */
			for (int id : NPC.BOSSES) {
				if (npc.getId() == id) {
					KillTracker.submit(player, new KillEntry(npc.getName(), 1), true);
				}
			}
			if (npc.getId() == player.getSlayerTask())
				player.getSlayerDeathTracker().add(npc);
			
			switch(npc.getId()) {
			case 6610:
				Achievements.increase(player, AchievementType.VENENATIS, 1);
				break;
			case 2054:
				Achievements.increase(player, AchievementType.CHAOS_ELEMENTAL, 1);
				break;
			case 6619:
				Achievements.increase(player, AchievementType.CHAOS_FANATIC, 1);
				break;
			case 319:
				Achievements.increase(player, AchievementType.CORPOREAL_BEAST, 1);
				break;
			case 6609:
				Achievements.increase(player, AchievementType.CALLISTO, 1);
				break;
			case 6611:
				Achievements.increase(player, AchievementType.VETION, 1);
				break;
			case 6615:
				Achievements.increase(player, AchievementType.SCORPIA, 1);
				break;
			case 494:
				Achievements.increase(player, AchievementType.KRAKEN, 1);
				break;
			case 3162:
				Achievements.increase(player, AchievementType.KREE_ARRA, 1);
				break;
			case 3131:
				Achievements.increase(player, AchievementType.KRIL_TSUTSAROTH, 1);
				break;
			case 2205:
				Achievements.increase(player, AchievementType.COMMANDER_ZILYANA, 1);
				break;
			case 2215:
				Achievements.increase(player, AchievementType.GENERAL_GRAARDOR, 1);
				break;
			case 239:
				Achievements.increase(player, AchievementType.KING_BLACK_DRAGON, 1);
				break;
			case 6342:
				Achievements.increase(player, AchievementType.BARRELCHEST, 1);
				break;
			case 3359:
				Achievements.increase(player, AchievementType.ZOMBIE_CHAMPION, 1);
				break;
			case 6618:
				Achievements.increase(player, AchievementType.CRAZY_ARCHAEOLOGIST, 1);
				break;
			}
		}
	
		
		int weapon = player.playerEquipment[player.getEquipment().getWeaponId()];
		player.write(new SendKillFeedPacket(Utility.formatPlayerName(player.getName()), npc.getDefinition().getName(), weapon, npc.isPoisoned()));
		
		player.getWarriorsGuild().dropDefender(npc.absX, npc.absY);
		if(AnimatedArmour.isAnimatedArmourNpc(npc.getId()))
			AnimatedArmour.dropTokens(player, npc.getId(), npc.absX, npc.absY);
		
		// get the drop table
		
		float yourIncrease = 0;

		if (player.playerEquipment[player.getEquipment().getRingId()] == 2572) {
			yourIncrease += 2;
		}
		if (player.playerEquipment[player.getEquipment().getRingId()] == 12785) {
			yourIncrease += 5;
		}
		if(player.getTotalAmountDonated() > 100 && player.getTotalAmountDonated() < 200) {
			yourIncrease += 10;
		} else if(player.getTotalAmountDonated() > 200) {
			yourIncrease += 15;
		}
		NpcDropSystem.get().drop(player, npc, yourIncrease);
	}

	public static boolean isSpawnedBy(Player player, NPC npc) {
		if (player != null && npc != null)
			if (npc.spawnedBy == player.getIndex() || npc.targetId == player.getIndex())
				return true;
		return false;
	}
}
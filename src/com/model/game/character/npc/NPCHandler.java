package com.model.game.character.npc;

import com.model.game.Constants;
import com.model.game.World;
import com.model.game.character.Entity;
import com.model.game.character.combat.combat_data.CombatType;
import com.model.game.character.npc.drops.NpcDropSystem;
import com.model.game.character.player.Boundary;
import com.model.game.character.player.Player;
import com.model.game.character.player.ProjectilePathFinder;
import com.model.game.character.player.content.achievements.AchievementType;
import com.model.game.character.player.content.achievements.Achievements;
import com.model.game.character.player.content.cluescrolls.ClueDifficulty;
import com.model.game.character.player.content.cluescrolls.ClueScrollHandler;
import com.model.game.character.player.packets.out.SendKillFeedPacket;
import com.model.game.location.Position;
import com.model.utility.Utility;
import com.model.utility.json.NPCDefinitionLoader;
import com.model.utility.json.definitions.NpcDefinition;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

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
					newNPC(Integer.parseInt(token3[0]), Integer.parseInt(token3[1]), Integer.parseInt(token3[2]), Integer.parseInt(token3[3]), Integer.parseInt(token3[4]), -1);
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
	
	public static void newNPC(int npcType, int x, int y, int heightLevel, int WalkingType, int HP) {

		Npc newNPC = new Npc(npcType);

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
	
	public static Npc spawnNpc(Player player, int id, int x, int y, int heightLevel, int walkingType, int health, int maxHit, int attackBonus, int meleeDefence, int rangeDefence, int magicDefence, boolean attacksEnemy, boolean hasHeadIcon, boolean bossOffspring) {
		Npc npc = new Npc(id);
		
		npc.setAbsX(x);
		npc.setAbsY(y);
		npc.makeX = x;
		npc.makeY = y;
		npc.heightLevel = heightLevel;
		npc.currentHealth = npc.getDefinition() == null ? health : npc.getDefinition().getHitpoints();
        npc.maximumHealth = npc.getDefinition() == null ? health : npc.getDefinition().getHitpoints();
        npc.attack_bonus = npc.getDefinition().getAttackBonus();
        npc.melee_defence = npc.getDefinition().getMeleeDefence();
        npc.range_defence = npc.getDefinition().getRangedDefence();
        npc.magic_defence = npc.getDefinition().getMagicDefence();
        npc.maxHit = npc.getDefinition().getMaxHit();
		npc.walking_type = walkingType;
		npc.spawnedBy = player.getIndex();
		System.out.printf("Spawned npc id %d for player index %d%n", id, player.getIndex());
		npc.setOnTile(x, y, heightLevel);
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
		if(bossOffspring) {
			npc.shouldRespawn = false;
		}
		World.getWorld().register(npc);
		return npc;
	}
	
	private static GroupRespawn tempGroup = null;
	private static Npc tempboss = null;
	
	/**
	 * This method links instances of NPCs to each other by using their Attribute system.
	 */
	private static void handleForGroup(Npc n) {
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
					n.setAttribute("group_spawn_map", new ArrayList<Npc>());
					//System.out.println("boss "+n+" map set.");
					tempboss = n;
				}
			}
		} else {
			// Temp attrib is set. We've located a boss already in spawn.txt
			GroupRespawn bossgroup = GroupRespawn.getGroup(n.getId());
			if (bossgroup != null) {
				// We're a minion
				ArrayList<Npc> minion_list = tempboss.getAttribute("group_spawn_map", new ArrayList<Npc>());
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
	
	public static void dropItems(Npc npc) {
		if (npc.killedBy == -1) {
			return;
		}
		
		Player player = World.getWorld().getPlayers().get(npc.killedBy);
		if (player == null) {
			return;
		}

		if (npc != null) {
			player.getBossDeathTracker().add(npc);
			if (npc.npcId == player.getSlayerTask())
				player.getSlayerDeathTracker().add(npc);
			
			switch(npc.npcId) {
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
		if (!ClueScrollHandler.npcDrop(player, npc)) {
			NpcDropSystem.get().drop(player, npc, yourIncrease);
		}
	}
	
	
	/**
	 * Handles following a player
	 * 
	 * @param npc
	 *            The {@link Npc} which is following the player
	 * @param target
	 *            The id of the player being followed
	 */
	public static void attemptFollowEntity(Npc npc, Entity target) {
		npc.forceChat("folo: "+target);
		if (target == null || npc == null) {
			npc.setFollowing(null);
			npc.resetFace();
			return;
		}
		
		boolean isBoss = false; // TODO
		
		// Only check rooms if npc is even remotely related to GWD.. otherwise.. LAG!
		if (isBoss) {
			if (Boundary.isIn(npc, Boundary.GODWARS_BOSSROOMS)) {
				if (!Boundary.isIn(target, Boundary.GODWARS_BOSSROOMS)) {
					npc.setFollowing(null);
					npc.resetFace();
					npc.targetId = 0; // reset cb as well.. not valid
					return;
				}
			}
		}

		if (target.isDead() || !target.isVisible() || npc.heightLevel != target.heightLevel) {
			npc.setFollowing(null);
			npc.resetFace();
			npc.walkingHome = true;
			npc.underAttack = false;
			return;
		}

		int targX = target.getX();
		int targY = target.getY();
		/*if (npc.isPet) { // always stand 1 south
			targX = target.getX();
			targY = target.getY() -1;
		} */
		//npc.forceChat("delta "+(npc.absX-targX)+" by "+(npc.absY-targY));

		// At this point, the target is valid, don't start walking off randomly.
		
		// Stop the npc from walking home and from random walking
		npc.walkingHome = npc.randomWalk = false;
		

		if (npc.frozen()) {
			// Don't reset, we just can't reach.
			return;
		}

		/*
		 * If close enough, stop following
		 */
		
		for (Position pos : npc.getTiles()) {
			double distance = pos.distance(target.getPosition());
			boolean magic = npc.getCombatType() == CombatType.MAGIC;
			boolean ranged = !magic && npc.getCombatType() == CombatType.RANGED;
			boolean melee = !magic && !ranged;
			if (melee || npc.isPet) {
				if (distance <= 1) { // Stop following when close
					return;
				}
			} else {
				if (distance <= (ranged ? 7 : 10)) {
					return;
				}
			}
		}
		
		// Spawned by a player.. we're (1) a pet (2) a warrior guild armour.. we follow forever
		boolean locked_to_plr = npc.spawnedBy > 0 || npc.ownerId > 0; // pets have spawnBy set
		// Within +/- 15 tiles from where our spawn pos is.
		boolean in_spawn_area = ((npc.getX() < npc.makeX + 15) && (npc.getX() > npc.makeX - 15) && (npc.getY() < npc.makeY + 15) && (npc.getY() > npc.makeY - 15));
		
		// Let's calculate a path to the target now.
		if (locked_to_plr || in_spawn_area) {
			npc.faceEntity(target);
			npc.setFollowing(null); // reset existing walking queue
			walkToNextTile(npc, targX, targY); // update walking queue to new target pos
			//npc.forceChat("my nigga");
			
		} else {
			// Reset following
			npc.setFollowing(null);
			npc.resetFace();
			npc.walkingHome = true;
			npc.underAttack = false;
		}
	}

	/**
	 * Calculates the movement required to reach a target X Y
	 * @param mob
	 * @param destinationX
	 * @param destinationY
	 */
	public static void walkToNextTile(Npc mob, int destinationX, int destinationY) {
		if (mob.absX == destinationX && mob.absY == destinationY)
			return;

		int direction = -1;

		final int x = mob.absX;
		final int y = mob.absY;
		final int xDifference = destinationX - x;
		final int yDifference = destinationY - y;

		int toX = 0;
		int toY = 0;

		if (xDifference > 0) {
			toX = 1;
		} else if (xDifference < 0) {
			toX = -1;
		}

		if (yDifference > 0) {
			toY = 1;
		} else if (yDifference < 0) {
			toY = -1;
		}

		int toDir = ProjectilePathFinder.getDirection(x, y, x + toX, y + toY);

		if (mob.canMoveTo(mob.getPosition(), toDir)) {
			direction = toDir;
		} else {
			if (toDir == 0) {
				if (mob.canMoveTo(mob.getPosition(), 3)) {
					direction = 3;
				} else if (mob.canMoveTo(mob.getPosition(), 1)) {
					direction = 1;
				}
			} else if (toDir == 2) {
				if (mob.canMoveTo(mob.getPosition(), 1)) {
					direction = 1;
				} else if (mob.canMoveTo(mob.getPosition(), 4)) {
					direction = 4;
				}
			} else if (toDir == 5) {
				if (mob.canMoveTo(mob.getPosition(), 3)) {
					direction = 3;
				} else if (mob.canMoveTo(mob.getPosition(), 6)) {
					direction = 6;
				}
			} else if (toDir == 7) {
				if (mob.canMoveTo(mob.getPosition(), 4)) {
					direction = 4;
				} else if (mob.canMoveTo(mob.getPosition(), 6)) {
					direction = 6;
				}
			}
		}

		if (direction == -1) {
			return;
		}

		mob.absX = x + Constants.DIRECTION_DELTA_X[direction];
		mob.absY = y + Constants.DIRECTION_DELTA_Y[direction];
		mob.direction = direction;
		mob.updateRequired = true;
		mob.setOnTile(mob.absX, mob.absY, mob.heightLevel);
	}

	public static Optional<Npc> spawnNpc3(Player c, int npcType, int x, int y, int heightLevel, ClueDifficulty d) {
		NpcDefinition def = NpcDefinition.getDefinitions()[npcType];
		if (d == ClueDifficulty.EASY) {
			////Player player, int id, int x, int y, int heightLevel, int walkingType, int health, int maxHit, int attackBonus, int meleeDefence, int rangeDefence, int magicDefence, boolean attacksEnemy, boolean hasHeadIcon
			return Optional.of(spawnNpc(c, npcType, x, y, heightLevel, 0, def.getHitpoints(), 10, 25, 25, 25, 25, true, true, false));
		} else if (d == ClueDifficulty.MEDIUM) {
			return Optional.of(spawnNpc(c, npcType, x, y, heightLevel, 0, def.getHitpoints(), 15, 50, 25, 25, 25, true, true, false));
		} else if (d == ClueDifficulty.HARD) {
			return Optional.of(spawnNpc(c, npcType, x, y, heightLevel, 0, def.getHitpoints(), 20, 75, 99, 25, 25, true, true, false));
		} else if (d == ClueDifficulty.ELITE) {
			return Optional.of(spawnNpc(c, npcType, x, y, heightLevel, 0, def.getHitpoints(), 35, 99, 120, 25, 25, true, true, false));
		}
		return Optional.empty();
	}
}
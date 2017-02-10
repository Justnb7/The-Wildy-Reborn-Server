package com.model.game.character.npc;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.model.game.Constants;
import com.model.game.World;
import com.model.game.character.combat.combat_data.CombatType;
import com.model.game.character.npc.combat.Bosses;
import com.model.game.character.npc.drops.NpcDropSystem;
import com.model.game.character.npc.pet.Pet;
import com.model.game.character.player.Boundary;
import com.model.game.character.player.Player;
import com.model.game.character.player.ProjectilePathFinder;
import com.model.game.character.player.packets.encode.impl.SendMessagePacket;
import com.model.game.character.player.packets.encode.impl.DrawHeadicon;
import com.model.game.character.player.packets.encode.impl.SendKillFeed;
import com.model.game.location.Location;
import com.model.utility.Utility;
import com.model.utility.json.NPCDefinitionLoader;
import com.model.utility.json.definitions.NpcDefinition;

public final class NPCHandler {

	public static int maxNPCs = 10000;
	public static Npc npcs[] = new Npc[maxNPCs];

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
	
	
	public boolean getsPulled(Npc npc) {
		switch (npc.npcId) {
		case 2215:
			if (npc.firstAttacker > 0)
				return false;
			break;
		}
		return true;
	}
	
	/**
	 * Summon npc, barrows, etc
	 */
	public static Npc spawnNpcBossOffspring(Player c, int npcType, int x, int y, int heightLevel, int walkType, int HP, int maxHit, int attack, int defence, boolean attackPlayer) {
		//System.out.println("spawn "+npcType+" at x "+x+" y "+y);
		Npc newNPC = new Npc(npcType);
		newNPC.absX = x;
		newNPC.absY = y;
		newNPC.makeX = x;
		newNPC.makeY = y;
		newNPC.setOnTile(x, y, heightLevel);
		newNPC.heightLevel = heightLevel;
		newNPC.walking_type = walkType;
		newNPC.currentHealth = HP;
		newNPC.maximumHealth = HP;
		newNPC.shouldRespawn = false;
		World.getWorld().register(newNPC);
		if (newNPC.npcId == 5054) {
			newNPC.forcedText = "GRRRRRRRRRRRR";
			newNPC.forcedChatRequired = true;
			newNPC.updateRequired = true;
		}
		if (attackPlayer) {
			newNPC.underAttack = true;
			if (c != null) {
				newNPC.killerId = c.getIndex();
			}
		}
		//npcs[slot] = newNPC;
		return newNPC;
	}
	
	public static Npc spawnPetNpc(Player owner, int npcType, int x, int y, int heightLevel) {

		Npc npc = new Npc(npcType);
		npc.setAbsX(x);
		npc.setAbsY(y);
		npc.makeX = x;
		npc.makeY = y;
		npc.heightLevel = heightLevel;
		npc.currentHealth = 0;
		npc.maximumHealth = 0;
		npc.walking_type = 0;
		npc.spawnedBy = owner.getIndex(); // PlayerId which owns this Npc 
		System.out.printf("Spawned npc id %d for player index %d%n", npcType, owner.getIndex());
		npc.setOnTile(x, y, heightLevel);
		World.getWorld().register(npc);
		npc.ownerId = owner.getIndex(); // same as spawnedBy should be removed in future 
		npc.isPet = true;
		// newNPC.killerId = c.playerId;
		for (Npc i : NPCHandler.npcs) {
			if (i == null)
				continue;
			if (i.ownerId == owner.getIndex())
				followPlayer(npc, owner.getIndex());
		}
		return npc;
	}
	
	public static void newNPC(int npcType, int x, int y, int heightLevel, int WalkingType, int HP) {

		Npc newNPC = new Npc(npcType);

		newNPC.setAbsX(x);
		newNPC.setAbsY(y);
		newNPC.makeX = x;
		newNPC.makeY = y;
		newNPC.heightLevel = heightLevel;
		newNPC.currentHealth = newNPC.getDefinition() == null ? HP : newNPC.getDefinition().getHitpoints();
        newNPC.maximumHealth = newNPC.getDefinition() == null ? HP : newNPC.getDefinition().getHitpoints();
        newNPC.getDefinition().getAttackBonus();
        newNPC.getDefinition().getMagicDefence();
        newNPC.getDefinition().getMagicDefence();
        newNPC.getDefinition().getRangedDefence();
        newNPC.walking_type = WalkingType;
		newNPC.setOnTile(x, y, heightLevel);
		if (World.getWorld().register(newNPC)) {
			// successfully added to game world
			handleForGroup(newNPC);
		}
	}
	
	public static Npc spawnNpc(Player player, int id, int x, int y, int heightLevel, int walkingType, int health, int maxHit, int attackBonus, int meleeDefence, int rangeDefence, int magicDefence, boolean attacksEnemy, boolean hasHeadIcon) {
		Npc npc = new Npc(id);
		
		npc.setAbsX(x);
		npc.setAbsY(y);
		npc.makeX = x;
		npc.makeY = y;
		npc.heightLevel = heightLevel;
		npc.currentHealth = npc.getDefinition() == null ? health : npc.getDefinition().getHitpoints();
        npc.maximumHealth = npc.getDefinition() == null ? health : npc.getDefinition().getHitpoints();
		npc.walking_type = walkingType;
		npc.spawnedBy = player.getIndex();
		System.out.printf("Spawned npc id %d for player index %d%n", id, player.getIndex());
		npc.setOnTile(x, y, heightLevel);
		npc.facePlayer(player.getIndex());
		npc.getDefinition().setAttackBonus(attackBonus);
		npc.getDefinition().setMeleeDefence(meleeDefence);
		npc.getDefinition().setRangedDefence(rangeDefence);
		npc.getDefinition().setMagicDefence(magicDefence);
		if (attacksEnemy) {
			npc.underAttack = true;
			if (player != null) {
				npc.killerId = player.getIndex();
			}
		}
		if (hasHeadIcon) {
			player.write(new DrawHeadicon(1, npc.getIndex(), 0, 0));
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

	public final static int[][] BOSSES_PKP = { { 2054, 1 }, {239, 1}, {6342, 3}, {2265, 1}, {2266, 1}, {2267, 1}, {6612, 3}, {6610, 3}, {6612, 3} };
	public static void getPKP(Player player, int npc) {
		for (int[] i : BOSSES_PKP)
			if (i[0] == npc) {
				player.setPkPoints(player.getPkPoints() + i[1]);
				player.write(new SendMessagePacket("<img=1>@blu@<shad>You have killed the @red@<shad>"+NpcDefinition.getDefinitions()[npc].getName()+" @blu@and received a potential bonus of @mag@<shad>" + i[1] + " @blu@PkP."));
				break;
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
		}
		
		boolean isBoss = Bosses.isBoss(npc.npcId);

		if (isBoss) {
			Bosses.get(npc.npcId).onDeath(player, npc);
		}
		
		int weapon = player.playerEquipment[player.getEquipment().getWeaponId()];
		player.write(new SendKillFeed(Utility.formatPlayerName(player.getName()), npc.getDefinition().getName(), weapon, npc.isPoisoned()));
		// get the drop table
		
		Pet.drop(player, npc.npcId);
		float yourIncrease = 0;

		if (player.playerEquipment[player.getEquipment().getRingId()] == 2572) {
			yourIncrease += 2;
		}
		if (player.playerEquipment[player.getEquipment().getRingId()] == 12785) {
			yourIncrease += 5;
		}
		NpcDropSystem.get().drop(player, npc, yourIncrease);
	}

	/**
	 * Resets players in combat
	 */
	public static void resetPlayersInCombat(int i) {
		for (int j = 0; j < World.getWorld().getPlayers().capacity(); j++) {
			if (World.getWorld().getPlayers().get(j) != null) {
				if (World.getWorld().getPlayers().get(j).underAttackBy2 == i) {
					World.getWorld().getPlayers().get(j).underAttackBy2 = 0;
				}
			}
		}
	}
	
	public static boolean followPlayer(int i) {
		switch (World.getWorld().getNpcs().get(i).npcId) {
		case 2042:
		case 2043:
		case 2044:
		case 492:
		case 494:
		case 5535:
			return false;
		case 6610:
			return false;
		}
		return true;
	}
	
	/**
	 * Handles following a player
	 * 
	 * @param npc
	 *            The {@link Npc} which is following the player
	 * @param playerId
	 *            The id of the player being followed
	 */
	public static void followPlayer(Npc npc, int playerId) {
		Player player = World.getWorld().getPlayers().get(playerId);
		if (player == null || npc == null) {
			return;
		}
		
		if (Boundary.isIn(npc, Boundary.GODWARS_BOSSROOMS)) {
			if (!Boundary.isIn(player, Boundary.GODWARS_BOSSROOMS)) {
				npc.killerId = 0;
				return;
			}
		}
		if (followPlayer(npc.getIndex())) {
			npc.facePlayer(playerId);
			//return;
		}
		if (npc.frozen()) {
			return;
		}

		if (player.isDead() || !player.isVisible()) {
			npc.facePlayer(0);
			npc.walkingHome = true;
			npc.underAttack = false;
			return;
		}
		if (Boundary.isIn(npc, Boundary.GODWARS_BOSSROOMS)) {
			if (!Boundary.isIn(player, Boundary.GODWARS_BOSSROOMS)) {
				npc.killerId = 0;
				return;
			}
		}
		if (!followPlayer(npc.getIndex())) {
			npc.facePlayer(playerId);
			return;
		}
		int playerX = player.getX();
		int playerY = player.getY();

		/*
		 * Stop the npc from walking home and from random walking
		 */
		npc.walkingHome = npc.randomWalk = false;

		/*
		 * If close enough, stop following
		 */
		for (Location Location : npc.getTiles()) {
			double distance = Location.distance(player.getLocation());
			boolean magic = npc.getCombatType() == CombatType.MAGIC;
			boolean ranged = !magic && npc.getCombatType() == CombatType.RANGED;
			boolean melee = !magic && !ranged;
			if (melee) {
				if (distance <= 1) {
					return;
				}
			} else {
				if (distance <= (ranged ? 7 : 10)) {
					return;
				}
			}
		}
		if (npc.spawnedBy > 0 || ((npc.getX() < npc.makeX + 15) && (npc.getX() > npc.makeX - 15) && (npc.getY() < npc.makeY + 15) && (npc.getY() > npc.makeY - 15))) {
			if (npc.heightLevel == player.heightLevel) {
				npc.resetFollowing(); // dont use the new system
				walkToNextTile(npc, playerX, playerY);
			}
		} else {
			npc.facePlayer(0);
			npc.walkingHome = true;
			npc.underAttack = false;
		}
	}

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

		if (mob.canMoveTo(mob.getLocation(), toDir)) {
			direction = toDir;
		} else {
			if (toDir == 0) {
				if (mob.canMoveTo(mob.getLocation(), 3)) {
					direction = 3;
				} else if (mob.canMoveTo(mob.getLocation(), 1)) {
					direction = 1;
				}
			} else if (toDir == 2) {
				if (mob.canMoveTo(mob.getLocation(), 1)) {
					direction = 1;
				} else if (mob.canMoveTo(mob.getLocation(), 4)) {
					direction = 4;
				}
			} else if (toDir == 5) {
				if (mob.canMoveTo(mob.getLocation(), 3)) {
					direction = 3;
				} else if (mob.canMoveTo(mob.getLocation(), 6)) {
					direction = 6;
				}
			} else if (toDir == 7) {
				if (mob.canMoveTo(mob.getLocation(), 4)) {
					direction = 4;
				} else if (mob.canMoveTo(mob.getLocation(), 6)) {
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

	public static boolean goodDistance(int objectX, int objectY, int playerX, int playerY, int distance) {
		return Math.sqrt(Math.pow(objectX - playerX, 2) + Math.pow(objectY - playerY, 2)) <= distance;
	}

	public static boolean isArmadylNpc(int i) {
		return World.getWorld().getNpcs().get(i).npcId >= 3162 && World.getWorld().getNpcs().get(i).npcId <= 3165;
	}
	
	public static Npc[] getNpcsById(int npcType) {
		List<Npc> npcList = new ArrayList<>();
		for (Npc npc : npcs) {
			if (npc == null) {
				continue;
			}
			if (npc.npcId != npcType) {
				continue;
			}
			npcList.add(npc);
		}
		return npcList.toArray(new Npc[npcList.size()]);
	}
}
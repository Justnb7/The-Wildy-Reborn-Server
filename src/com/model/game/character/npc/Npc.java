package com.model.game.character.npc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import com.model.Server;
import com.model.game.World;
import com.model.game.character.Entity;
import com.model.game.character.Hit;
import com.model.game.character.combat.CombatDamage;
import com.model.game.character.combat.combat_data.CombatType;
import com.model.game.character.combat.nvp.NpcVsPlayerCombat;
import com.model.game.character.npc.combat.combat_scripts.Scorpia;
import com.model.game.character.player.ActionSender;
import com.model.game.character.player.Boundary;
import com.model.game.character.player.Player;
import com.model.game.character.player.ProjectilePathFinder;
import com.model.game.location.Location;
import com.model.net.network.rsa.GameBuffer;
import com.model.task.impl.NPCDeathTask;
import com.model.utility.Stopwatch;
import com.model.utility.Utility;
import com.model.utility.cache.map.Region;
import com.model.utility.json.definitions.NpcDefinition;

public class Npc extends Entity {

	/**
	 * Adds the damage received into a list
	 */
	public Map<String, ArrayList<CombatDamage>> damageReceived = new HashMap<>();

	/**
	 * gets the npc ID
	 * @return
	 */
	public int getId() {
		return npcId;
	}
	
	/**
	 * Checks if the minions can be respawned
	 */
	public boolean spawnedScorpiaMinions, spawnedVetionMinions;

	/**
	 * Checks if the npc is a pet
	 */
	public boolean isPet;
	
	/**
	 * Checks if the player owns the pet
	 */
	public int ownerId;
	
	/**
	 * Checks if the minion should respawn
	 */
	public boolean shouldRespawn = true, needRespawn;
	
	/**
	 * Last special attack delay
	 */
	public long lastSpecialAttack;
	
	/**
	 * Determines wether the player was the first attacker
	 */
	public int firstAttacker;
	
	/**
	 * Stopwatch delay
	 */
	private Stopwatch delay = new Stopwatch();
	
	/**
	 * Gets the stop watch delay
	 * @return delay
	 */
	public Stopwatch getDelay() {
		return delay;
	}

	/**
	 * Sets the delay
	 * @param delay
	 */
	public void setDelay(Stopwatch delay) {
		this.delay = delay;
	}

	/**
	 * Cannot attack npcs while transforming
	 */
	public boolean transforming;

	/**
	 * Sets the transformation update
	 */
	public boolean transformUpdateRequired = false;
	
	/**
	 * Transformation identity
	 */
	public int transformId;
	
	/**
	 * Checks the last location the npc was on
	 */
	private Location lastLocation = null;
	
	/**
	 * Face the npc to the player
	 */
	private boolean facePlayer = true;
	
	/**
	 * Determines if the npc can face another player
	 * @return	{@code true} if the npc can face players
	 */
	public boolean canFacePlayer() {
		return facePlayer;
	}
	
	/**
	 * Makes the npcs either able or unable to face other players
	 * @param facePlayer	{@code true} if the npc can face players
	 */
	public void setFacePlayer(boolean facePlayer) {
		this.facePlayer = facePlayer;
		if(!facePlayer){
			this.facePlayer(65535);//reset the entity in client to -1 so its not locked onto something
		}
	}

	/**
	 * Requesting the transformation
	 * @param Id
	 */
	public void requestTransform(int Id) {
		transformId = Id;
		transformUpdateRequired = true;
		updateRequired = true;
	}

	/**
	 * Npc direction
	 */
	public int direction;
	
	/**
	 * Representing the npc id
	 */
	public int npcId;
	
	/**
	 * npc Locations
	 */
	public int makeX, makeY, moveX, moveY;

	/**
	 * representing the npc index
	 */
	public int npcIndex;
	
	/**
	 * Target following index
	 */
	public int followTargetIndex;
	
	public int attackStyle, projectileId, endGfx, spawnedBy, hitDelayTimer, currentHealth, maximumHealth,
			attackTimer, killerId, killedBy, oldIndex, underAttackBy, walking_type;
	
	public boolean noDeathEmote, isDead, walkingHome, underAttack, randomWalk, dirUpdateRequired, 
			forcedChatRequired;

	public boolean aggressive;
	
	public long lastDamageTaken;
	
	public boolean isPoisoned() {
		if (poisonDamage > 0)
			return true;
		else 
			return false;
	}
	
	public String forcedText;

	public Npc(int _npcType) {
		super(EntityType.NPC);
		npcId = _npcType;
		direction = -1;
		isDead = false;
		randomWalk = true;
	}
	
	public Npc(int _npcId, int _npcType) {
		super(EntityType.NPC);
		npcId = _npcId;
		npcId = _npcType;
		direction = -1;
		isDead = false;
		randomWalk = true;
	}
	
	/**
	 * Set an npc onto a tile, removes it from the current tile before placing
	 * another one
	 */
	public void setOnTile(int x, int y, int z) {
		removeFromTile();
		for (Location tile : getTiles(new Location(x, y, z)))
			Region.putNpcOnTile(tile.getX(), tile.getY(), tile.getZ());
		lastLocation = new Location(x, y, z);
	}

	/**
	 * Remove an npc from the current tile
	 */
	public void removeFromTile() {
		if (lastLocation != null) {
			for (Location tile : getTiles(lastLocation))
				Region.removeNpcFromTile(tile.getX(), tile.getY(), tile.getZ());
			lastLocation = null;
		}
	}

	/**
	 * Teleport an npc and set it's make location as the location
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	public void teleport(int x, int y, int z) {
		setOnTile(x, y, z);
		makeX = x;
		makeY = y;
		setAbsX(x);
		setAbsY(y);
		heightLevel = z;
		setLocation(new Location(x, y, z));
		getAttributes().put("teleporting", true);
	}

	@Override
	public Hit decrementHP(Hit hit) {
		int damage = hit.getDamage();
		if (damage > this.currentHealth)
			damage = this.currentHealth;
		this.currentHealth -= damage;

		/*
		 * Start our death task since we are now dead
		 */
		if (currentHealth <= 0 && !isDead) {
			isDead = true;
			Server.getTaskScheduler().schedule(new NPCDeathTask(this));
		}
		return new Hit(damage, hit.getType());
	}

	public Location getLocation() {
		return new Location(absX, absY, heightLevel);
	}

	public NpcDefinition getDefinition() {
        NpcDefinition def = NpcDefinition.getDefinitions()[npcId];
        if (def == null)
            return NpcDefinition.getDefinitions()[1];
        return def;
	}

	/**
	 * Text update
	 **/
	public void forceChat(String text) {
		setForcedText(text);
		forcedChatRequired = true;
		updateRequired = true;
	}

	public Location[] getTiles(Location location) {
		Location[] tiles = new Location[getSize() == 1 ? 1 : (int) Math.pow(getSize(), 2)];
		int index = 0;

		for (int i = 1; i < getSize() + 1; i++) {
			for (int k = 0; k < SIZE_DELTA_COORDINATES[i].length; k++) {
				int x3 = location.getX() + SIZE_DELTA_COORDINATES[i][k][0];
				int y3 = location.getY() + SIZE_DELTA_COORDINATES[i][k][1];
				tiles[index] = new Location(x3, y3, location.getZ());
				index++;
			}
		}
		return tiles;
	}

	public Location[] getTiles() {
		return getTiles(getLocation());
	}

	/**
	 * Gets the border around the edges of the npc.
	 * 
	 * @return the border around the edges of the npc, depending on the npc's
	 *         size.
	 */
	public Location[] getBorder() {
		int x = getLocation().getX();
		int y = getLocation().getY();
		int size = getSize();
		if (size <= 1) {
			return new Location[] { getLocation() };
		}

		Location[] border = new Location[(size) + (size - 1) + (size - 1) + (size - 2)];
		int j = 0;

		border[0] = new Location(x, y, 0);

		for (int i = 0; i < 4; i++) {
			for (int k = 0; k < (i < 3 ? (i == 0 || i == 2 ? size : size) - 1 : (i == 0 || i == 2 ? size : size) - 2); k++) {
				if (i == 0)
					x++;
				else if (i == 1)
					y++;
				else if (i == 2)
					x--;
				else if (i == 3) {
					y--;
				}
				border[(++j)] = new Location(x, y, 0);
			}
		}

		return border;
	}

	/**
	 * Can this actor move from it's current location to the destination
	 * 
	 * @param src
	 *            the source location
	 * @param direction
	 *            the walking direction
	 * @return if this actor can move from it's current location to the
	 *         destination
	 */
	public boolean canMoveTo(Location src, int direction) {
		int x = src.getX();
		int y = src.getY();
		int z = src.getZ() > 3 ? src.getZ() % 4 : src.getZ();
		int x5 = src.getX() + DIRECTION_DELTA_X[direction];
		int y5 = src.getY() + DIRECTION_DELTA_Y[direction];
		int size = getSize();

		for (int i = 1; i < size + 1; i++) {
			for (int k = 0; k < SIZE_DELTA_COORDINATES[i].length; k++) {
				int x3 = x + SIZE_DELTA_COORDINATES[i][k][0];
				int y3 = y + SIZE_DELTA_COORDINATES[i][k][1];

				int x2 = x5 + SIZE_DELTA_COORDINATES[i][k][0];
				int y2 = y5 + SIZE_DELTA_COORDINATES[i][k][1];

				Location a = new Location(x3, y3, z);
				Location b = new Location(x2, y2, z);

				if (Location.isWithinBlock(x, y, size, x2, y2)) {
					continue;
				}

				if (!Region.canMove(a, direction)) {
					return false;
				}

				if (Region.isNpcOnTile(b.getX(), b.getY(), b.getZ())) {
					return false;
				}

				for (int dir = 0; dir < 8; dir++) {
					if (Location.isWithinBlock(x5, y5, getSize(), x2 + DIRECTION_DELTA_X[dir], y2 + DIRECTION_DELTA_Y[dir])) {
						if (!Region.canMove(b, dir)) {
							return false;
						}
					}
				}
			}
		}

		if (DIRECTION_DELTA_X[direction] != 0 && DIRECTION_DELTA_Y[direction] != 0) {
			return canMoveTo(src, ProjectilePathFinder.getDirection(0, DIRECTION_DELTA_Y[direction])) && canMoveTo(src, ProjectilePathFinder.getDirection(DIRECTION_DELTA_X[direction], 0));
		}

		return true;
	}
	

	/**
	 * 
	 * Face
	 * 
	 **/
	public int FocusPointX = -1, FocusPointY = -1, face = 0;

	public void faceLocation(int x, int y) {
		FocusPointX = x;
		FocusPointY = y;
		updateRequired = true;
	}

	public int getSize() {
		return NPCSize.getSize(npcId);
	}

	public void clearUpdateFlags() {
		updateRequired = false;
		forcedChatRequired = false;
		hitUpdateRequired = false;
		hitUpdateRequired2 = false;
		animUpdateRequired = false;
		dirUpdateRequired = false;
		if (transformUpdateRequired) {
			transformUpdateRequired = false;
			this.npcId = this.transformId;
			this.transformId = -1;
	        this.maximumHealth = this.getDefinition() == null ? currentHealth : this.getDefinition().getHitpoints();
			
		}
		this.gfxUpdateRequired = false;
		setForcedText(null);
		moveX = 0;
		moveY = 0;
		direction = -1;
		FocusPointX = -1;
		FocusPointY = -1;
		Object tele = getAttribute("teleporting", null);
		boolean teleporting = tele != null ? (boolean) tele : false;
		if (teleporting) {
			setOnTile(absX, absY, heightLevel);
			setAttribute("teleporting", false);
		}
		super.clear();
	}

	/**
	 * Gets a list of surrounding players near the mob
	 *
	 * @param mob
	 *            the mob
	 * @return the list of players surrounding the mob
	 */
	public static final List<Player> getSurroundingPlayers(final Npc mob, int distance) {
		final List<Player> surrounding = new ArrayList<>();
		for (Player player : World.getWorld().getPlayers()) {
			if (player != null) {
				if (player.isDead() || (player.heightLevel != mob.heightLevel)) {
					continue;
				}

				if (player.distanceToPoint(mob.getX(), mob.getY()) < distance) {
					surrounding.add(player);
				}

			}
		}
		return surrounding;
	}
	
	public int dogs = 0;
	
	public void spawnVetDogs(Player player) {
		if (npcId == 6611) {
			NPCHandler.spawnNpcBossOffspring(player, 6613, absX - 1, absY, heightLevel, 1, 190, 20, 115, 85, true);
			NPCHandler.spawnNpcBossOffspring(player, 6613, absX - 1, absY, heightLevel, 1, 190, 20, 115, 85, true);
			dogs += 2;
			spawnedVetionMinions = true;
		} else if (npcId == 6612) {
			NPCHandler.spawnNpcBossOffspring(player, 6614, absX - 1, absY, heightLevel, 1, 190, 25, 125, 95, true);
			NPCHandler.spawnNpcBossOffspring(player, 6614, absX - 1, absY, heightLevel, 1, 190, 25, 125, 95, true);
			dogs += 2;
			spawnedVetionMinions = true;
		}
	}
	
	@Override
	public void process() {
		
		onUpdate();
		try {
			follow();
			
			super.frozen_process();

			Player player = World.getWorld().getPlayers().get(spawnedBy);
			if (isPet && ownerId > 0) {
				//System.out.println("NPC Following player");
				NPCHandler.followPlayer(this, ownerId);
			}
			
			if (currentHealth > 0 && !isDead) {
				if (npcId == 6611 || npcId == 6612) {
					if (currentHealth < (maximumHealth / 2) && !spawnedVetionMinions) {
						spawnVetDogs(player);
					}
				}
			}
			
			if (currentHealth > 0 && !isDead) {
				if (npcId == 6615) {
					if (currentHealth <= 100 && !spawnedScorpiaMinions) {
						Npc min1 = NPCHandler.spawnNpcBossOffspring(player, 6617, getX()- 1, absY, heightLevel, 1, 79, -1, -1, 0, false);
						Npc min2 = NPCHandler.spawnNpcBossOffspring(player, 6617, getX() + 1, absY, heightLevel, 1, 79, -1, -1, 0, false);
						// attributes not used atm
						this.setAttribute("min1", min1);
						min1.setAttribute("boss", this);
						this.setAttribute("min2", min2);
						min2.setAttribute("boss", this);
						// flag spawned
						spawnedScorpiaMinions = true;
						// start task
						Scorpia.heal_scorpia(this, min1);
						Scorpia.heal_scorpia(this, min2);
					}
				}
			}
			
			if (World.getWorld().getNpcs().get(getIndex()) == null)
				return;
			/*
			 * Handle our combat timers
			 */
			NpcVsPlayerCombat.handleCombatTimer(this);

			if (spawnedBy > 0 && (World.getWorld().getPlayers().get(spawnedBy) == null || World.getWorld().getPlayers().get(spawnedBy).heightLevel != heightLevel || World.getWorld().getPlayers().get(spawnedBy).isDead() || !player.goodDistance(getX(), getY(), World.getWorld().getPlayers().get(spawnedBy).getX(), World.getWorld().getPlayers().get(spawnedBy).getY(), 20))) {
				World.getWorld().unregister(this);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Resets players in combat
	 */
	public static int maxNPCs = 10000;
	public static Npc npcs[] = new Npc[maxNPCs];

	public static Npc getNpc(int npcType) {
		for (Npc npc : npcs)
			if (npc != null && npc.npcId == npcType)
				return npc;
		return null;
	}

	public void addDamageReceived(String player, int damage) {
		if (damage <= 0) {
			return;
		}
		CombatDamage combatDamage = new CombatDamage(damage);
		if (damageReceived.containsKey(player)) {
			damageReceived.get(player).add(new CombatDamage(damage));
		} else {
			damageReceived.put(player, new ArrayList<CombatDamage>(Arrays.asList(combatDamage)));
		}
	}

	public void resetDamageReceived() {
		damageReceived.clear();
	}

	public String getKiller() {
		String killer = null;
		long totalDamage = 0;
		for (Entry<String, ArrayList<CombatDamage>> entry : damageReceived.entrySet()) {
			String player = entry.getKey();
			ArrayList<CombatDamage> damageList = entry.getValue();
			int damage = 0;
			for (CombatDamage cd : damageList) {
				if (System.currentTimeMillis() - cd.getTimeInMillis() < TimeUnit.MINUTES.toMillis(5)) {
					damage += cd.getDamage();
				}
			}
			if (totalDamage == 0 || damage > totalDamage || killer == null) {
				totalDamage = damage;
				killer = player;
			}
		}
		return killer;
	}

	public void handleFacing() {
		if (walking_type == 2) {
			faceLocation(getX() + 1, getY());
			// face east
		} else if (walking_type == 3) {
			faceLocation(getX(), getY() - 1);
			// face south
		} else if (walking_type == 4) {
			faceLocation(getX() - 1, getY());
			// face west
		} else if (walking_type == 5) {
			faceLocation(getX(), getY() + 1);
			// face north
		}
	}

	/**
	 * Dummy method for abstracted npcs
	 */
	public void onUpdate() {
	}

	@Override
	public EntityType getEntityType() {
		return EntityType.NPC;
	}

	public String getForcedText() {
		return forcedText;
	}

	public void setForcedText(String forcedText) {
		this.forcedText = forcedText;
	}

	/**
	 * Contains the delta Locations for the x and y coordinate of actor model
	 * sizes.
	 */
	private static final int[][][] SIZE_DELTA_COORDINATES = {
			{ { 0, 0 } }, // 0
			{ { 0, 0 } }, // 1
			{ { 0, 1 }, { 1, 0 }, { 1, 1 } }, // 2
			{ { 2, 0 }, { 2, 1 }, { 2, 2 }, { 1, 2 }, { 0, 2 } }, // 3
			{ { 3, 0 }, { 3, 1 }, { 3, 2 }, { 3, 3 }, { 2, 3 }, { 1, 3 }, { 0, 3 } }, // 4
			{ { 4, 0 }, { 4, 1 }, { 4, 2 }, { 4, 3 }, { 4, 4 }, { 3, 4 }, { 2, 4 }, { 1, 4 }, { 0, 4 } }, // 5
			{ { 5, 0 }, { 5, 1 }, { 5, 2 }, { 5, 3 }, { 5, 4 }, { 5, 5 }, { 4, 5 }, { 3, 5 }, { 2, 5 }, { 1, 5 },
					{ 0, 5 } }, // 6
	};

	/**
	 * Difference in X coordinates for directions array.
	 */
	public static final byte[] DIRECTION_DELTA_X = new byte[] { -1, 0, 1, -1, 1, -1, 0, 1 };

	/**
	 * Difference in Y coordinates for directions array.
	 */
	public static final byte[] DIRECTION_DELTA_Y = new byte[] { 1, 1, 1, 0, 0, -1, -1, -1 };

	public int getRegion() {
		int pl_regionX = this.absX >> 3;
		int pl_regionY = this.absY >> 3;
		return (pl_regionX / 8 << 8) + pl_regionY / 8;
	}

	public void turnNpc(int i, int j) {
		FocusPointX = 2 * i + 1;
		FocusPointY = 2 * j + 1;
		updateRequired = true;
		System.out.println("focusnig npc on "+this.FocusPointX+" "+this.FocusPointY);
	}
	
	public void appendFaceEntity(GameBuffer str) {
		str.writeShort(face);
	}

	public void facePlayer(int player) {
		if (!facePlayer) {
			if (face == -1) {
				return;
			}
			face = -1;
		} else {
			face = player + 32768;
		}
		dirUpdateRequired = true;
		updateRequired = true;
	}
	
	public void face(Entity e) {
		face = e.getEntityType() == EntityType.PLAYER ? 32768 + e.getIndex() : e.getIndex();//go
		dirUpdateRequired = true;
		updateRequired = true;
	}

	public int distanceToPoint(int pointX, int pointY) {
		return (int) Math.sqrt(Math.pow(getX() - pointX, 2) + Math.pow(getY() - pointY, 2));
	}
	
	public void requestUpdates() {
        this.updateRequired = true;
    }

	private Entity following_target;
	//SETTER
	public void follow(Npc boss) {
		walkingHome = randomWalk = false;
		following_target = boss;
	}
	// clearer
	public void resetFollowing() {
		following_target = null;
	}
	
	// Actual following, called every cycle. This method supports both player and NPC types while PI didn't
	public void follow() {
		if (super.frozen()) { // TODO 
			return;
		}
		if (following_target != null) {
			if (following_target.getLocation().getZ() != this.heightLevel) {
				facePlayer(0);
				this.resetFollowing();
				return;
			}
			if (following_target.getEntityType() == EntityType.PLAYER) {
				Player targ = (Player)following_target;
				if (targ.isDead() || !targ.isVisible()) {
					facePlayer(0);
					this.resetFollowing();
					return;
				}
			} else if (following_target.getEntityType() == EntityType.NPC) {
				Npc targ = (Npc)following_target;
				if (targ.currentHealth <= 0 || !targ.isVisible()) {
					facePlayer(0);
					this.resetFollowing();
					return;
				}
			}
			/*
			 * If close enough, stop following
			 */
			for (Location Location : getTiles()) {
				double distance = Location.distance(following_target.getLocation());
				if (distance > 16) {
					facePlayer(0);
					this.resetFollowing();
					return;
				}
				boolean magic = getCombatType() == CombatType.MAGIC;
				boolean ranged = !magic && getCombatType() == CombatType.RANGED;
				boolean melee = !magic && !ranged;
				if (melee) {
					if (distance <= 1) {
						return; // dont reset follow, just dont go anywher this cycle. we're at the target.
					}
				} else {
					if (distance <= (ranged ? 7 : 10)) {
						return; // dont reset follow, just dont go anywher this cycle. we're at the target.
					}
				}
			}
			if ((getX() < makeX + 15)
					&& (getX() > makeX - 15)
					&& (getY() < makeY + 15) && (getY() > makeY
					- 15)) {
			NPCHandler.walkToNextTile(this, following_target.getLocation().getX(), following_target.getLocation().getY());
			}
		}//try
		// wtf this old follow method isnt even used LMFAO
		else if (followTargetIndex > 0) { // support the old PI system anyway (only player targets supported)
			if (World.getWorld().getPlayers().get(followTargetIndex) == null) {
				World.getWorld().unregister(this);
				return;
			}
			facePlayer(followTargetIndex);
			if (Math.abs(World.getWorld().getPlayers().get(followTargetIndex).getX() - getX()) > 5
					|| Math.abs(World.getWorld().getPlayers().get(followTargetIndex).getY() - getY()) > 5) {
				//wtf
			} else {
				NPCHandler.followPlayer(this, followTargetIndex);
			}
		}
	}
	
	public int walkX, walkY;

	/**
	 * Make an NPC walk somewhere
	 * 
	 * @param x
	 *            X coordinate
	 * @param y
	 *            Y coordinate
	 */
	public void walkTo(int x, int y) {
		walkX = x;
		walkY = y;
		walking_type = 1337;
	}

	public void getNextNPCMovement(Npc npc) {
		if (direction != -1) {
			return;
		}
		direction = getNextWalkingDirection();
	}
	
	public int getNextWalkingDirection() {
		int dir;
		dir = Utility.direction(absX, absY, (absX + moveX), (absY + moveY));
		if (dir == -1)
			return -1;
		dir >>= 1;
		absX += moveX;
		absY += moveY;
		return dir;
	}

	public int distanceTo(Player player) {
		return distanceTo(player.absX, player.absY);
	}

	public int distanceTo(Npc npc) {
		return distanceTo(npc.absX, npc.absY);
	}

	public int distanceTo(int otherX, int otherY) {
		int minDistance = (int) Math.hypot(otherX - absX, otherY - absY);
		for (int x = absX; x < absX + getSize() - 1; x++) {
			for (int y = absY; y < absY + getSize() - 1; y++) {
				int distance = (int) Math.hypot(otherX - x, otherY - y);
				if (distance < minDistance) {
					minDistance = distance;
				}
			}
		}
		return minDistance;
	}

	public boolean respawnable = true;
	
	public boolean isRespawnable() {
		return respawnable;
	}

	public void setRespawnable(boolean respawnable) {
		this.respawnable = respawnable;
	}
	
	public void remove() {
		setVisible(false);
		if (NPCHandler.npcs[npcId] == this) {
			NPCHandler.npcs[npcId] = null;
		}
	}
	
	@Override
	public ActionSender getActionSender() {
		return null;
	}

	public static String getName(int npcId) {
		if (NpcDefinition.getDefinitions()[npcId] == null || npcId < 0 || npcId >= NpcDefinition.MOB_LIMIT) {
			return "None";
		}
		return NpcDefinition.getDefinitions()[npcId].getName();
	}
	
	public String getName() {
		return NpcDefinition.getDefinitions()[getId()].getName();
	}

	public boolean inMulti() {
		if (getX() >= 2840 && getY() >= 5270 && getX() >= 2920 && getY() <= 5360) {
			return true;
		}
		
		if (Boundary.isIn(this, Boundary.GODWARS_BOSSROOMS) || Boundary.isIn(this, Boundary.SCORPIA_PIT)) {
			return true;
		}
		
		return (absX >= 3136 && absX <= 3327 && absY >= 3519 && absY <= 3607)
				|| (absX >= 3190 && absX <= 3327 && absY >= 3648 && absY <= 3839)
				|| (absX >= 2625 && absX <= 2685 && absY >= 2550 && absY <= 2620)
				|| // Pest
				(absX >= 3200 && absX <= 3390 && absY >= 3840 && absY <= 3967)
				|| (absX >= 2864 && absX <= 2877 && absY >= 5348 && absY <= 5374)
				|| // bandos
				(absX >= 2884 && absX <= 2991 && absY >= 5255 && absY <= 5278)
				|| // sara
				(absX >= 2821 && absX <= 2844 && absY >= 5292 && absY <= 5311)
				|| // armadyl
				(absX >= 2968 && absX <= 2988 && absY >= 9512 && absY <= 9523)
				|| // barrelchest
				(absX >= 2992 && absX <= 3007 && absY >= 3912 && absY <= 3967) || 
				(absX >= 2680 && absX <= 2750 && absY >= 3685 && absY <= 3765)
				|| (absX >= 2946 && absX <= 2959 && absY >= 3816 && absY <= 3831)
				|| (absX >= 3008 && absX <= 3199 && absY >= 3856 && absY <= 3903)
				|| (absX >= 3008 && absX <= 3071 && absY >= 3600 && absY <= 3711)
				|| (absX >= 3072 && absX <= 3327 && absY >= 3608 && absY <= 3647)
				|| (absX >= 2624 && absX <= 2690 && absY >= 2550 && absY <= 2619)
				|| (absX >= 2371 && absX <= 2422 && absY >= 5062 && absY <= 5117)
				|| (absX >= 2892 && absX <= 2932 && absY >= 4435 && absY <= 4464)
				|| (absX >= 2256 && absX <= 2287 && absY >= 4680 && absY <= 4711)
				|| (absX >= 3157 && absX <= 3191 && absY >= 2965 && absY <= 2995)
				|| (absX >= 2512 && absX <= 2540 && absY >= 4633 && absY <= 4659)
				|| (absX >= 3461 && absX <= 3494 && absY >= 9476 && absY <= 9506)
				|| (absX >= 3357 && absX <= 3383 && absY >= 3721 && absY <= 3749)
				|| (absX >= 2785 && absX <= 2809 && absY >= 2775 && absY <= 2795)
				|| (absX >= 3093 && absX <= 3118 && absY >= 3922 && absY <= 3947)
                || (absX >= 2932 && absX <= 2992 && absY >= 9745 && absY <= 9825)
				|| (absX >= 2980 && absX <= 2995 && absY >= 4375 && absY <= 4390)

				|| (absX >= 2660 && absX <= 2730 && absY >= 3707 && absY <= 3737);

	}
}

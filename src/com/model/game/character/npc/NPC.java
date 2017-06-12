package com.model.game.character.npc;

import com.model.UpdateFlags.UpdateFlag;
import com.model.game.World;
import com.model.game.character.Entity;
import com.model.game.character.Hit;
import com.model.game.character.combat.Combat;
import com.model.game.character.combat.DamageMap;
import com.model.game.character.combat.nvp.NpcVsPlayerCombat;
import com.model.game.character.following.NPCFollowing;
import com.model.game.character.player.ActionSender;
import com.model.game.character.player.Boundary;
import com.model.game.character.player.Player;
import com.model.game.character.player.ProjectilePathFinder;
import com.model.game.definitions.NPCDefinitions;
import com.model.game.location.Location;
import com.model.server.Server;
import com.model.task.impl.NPCDeathTask;
import com.model.utility.Stopwatch;
import com.model.utility.Utility;
import com.model.utility.cache.map.Region;

import java.util.*;

public class NPC extends Entity {
	
	public NPC(int _npcType) {
		this(_npcType, null, -1);
	}
	
	public NPC(int id, Location spawn, int dir) {
		super(EntityType.NPC);
		direction = dir;
		if (spawn != null)
			setLocation(spawn);
		npcId = id;
		setDead(false);
		randomWalk = true;
		
		NPCDefinitions definition = NPCDefinitions.get(id);
		if (definition != null) {
			size = definition.getSize();
			if (size < 1) {
				size = 1;
			}
			npcName = definition.getName();
			combatLevel = definition == null ? 1 : definition.getCombatLevel();
			hitpoints = maxHitpoints = definition.getHitpoints();
			//System.out.printf("npc: %s hitpoints: %d%n ",definition.getName(), definition.getHitpoints());
			maxHit = definition.getMaxHit();
			attack_bonus = definition.getAttackBonus();
			magic_defence = definition.getMagicDefence();
			melee_defence = definition.getMeleeDefence();
			range_defence = definition.getRangedDefence();
			setCombatCooldownDelay(definition.getAttackSpeed());
		}
	}

	/**
	 * The damage map for the npc
	 */
	private DamageMap damageMap = new DamageMap();
	
	/**
	 * Gets the npcs damage map
	 * 
	 * @return
	 */
	public DamageMap getDamageMap() {
		return damageMap;
	}

	/**
	 * gets the npc ID
	 * @return
	 */
	public int getId() {
		return npcId;
	}
	
	/**
	 * Sets the npc ID
	 * @param npcId
	 *        The npc
	 */
	public void setId(int npcId) {
		this.npcId = npcId;
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
	 * Checks if the npc should respawn
	 */
	public boolean shouldRespawn = true;
	
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
	 * Transformation identity
	 */
	public int transformId;
	
	/**
	 * Checks the last location the npc was on
	 */
	private Location lastLocation = null;

	/**
	 * Requesting the transformation
	 * @param Id
	 */
	public void requestTransform(int Id) {
		transformId = Id;
		getUpdateFlags().flag(UpdateFlag.TRANSFORM);
	}
	
	private String npcName;

	/**
	 * Npc direction
	 */
	public int direction;
	
	/**
	 * Representing the npc id
	 */
	private int npcId;
	
	/**
	 * npc Locations
	 */
	public int makeX, makeY, moveX, moveY;
	
	public int combatLevel, spawnedBy, killedBy, oldIndex, underAttackBy, walking_type;
	
	/**
	 * The hitpoints of the npc
	 */
	private int hitpoints;

	/**
	 * The maximum amount of hitpoints of the npc
	 */
	private int maxHitpoints;
	
	/**
	 * Gets the npcs hitpoints
	 * 
	 * @return The npcs hitpoints
	 */
	public int getHitpoints() {
		return hitpoints;
	}

	/**
	 * Sets the npcs hitpoints
	 * 
	 * @param hitpoints
	 *            The hitpoints of the npc
	 */
	public void setHitpoints(int hitpoints) {
		this.hitpoints = hitpoints;
	}

	/**
	 * Gets the npcs max hitpoints
	 * 
	 * @return The npcs max hitpoints
	 */
	public int getMaxHitpoints() {
		return maxHitpoints;
	}
	
	/**
	 * The Index of our Target - the Player we're attacking. PLAYER ONLY. TODO make this Entity instead of Int
	 */
	public int targetId;
	
	public boolean noDeathEmote, walkingHome, underAttack, randomWalk;

	public boolean aggressive;
	
	public long lastDamageTaken;
	
	/**
	 * Our enemys maximum hit
	 */
	public int maxHit;
	
	/**
	 * Our enemys attack level
	 */
	public int attack_bonus;
	
	/**
	 * Our enemys defence level for magic
	 */
	public int magic_defence;
	/**
	 * Our enemys defence level for melee
	 */
	
	public int melee_defence;
	
	/**
	 * Get the npcs defence level for range
	 */
	public int range_defence;

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
	 */
	public void teleport(Location position) {
		setOnTile(position.getX(), position.getY(), position.getZ());
		makeX = position.getX();
		makeY = position.getY();
		setAbsX(position.getX());
		setAbsY(position.getY());
		heightLevel = position.getZ();
		setLocation(new Location(position.getX(), position.getY(), position.getZ()));
		getAttributes().put("teleporting", true);
	}

	@Override
	public boolean moving() {
		return moveX != 0 || moveY != 0;
	}

	@Override
	public int size() {
		return getSize();
	}

	@Override
	public Hit decrementHP(Hit hit) {
		
		int damage = hit.getDamage();

		// You can't hit over an Npcs current health. Recent update on 07 means you can in PVP though.
		if (hitpoints - damage < 0) {
			damage = hitpoints;
		}
		
		hitpoints -= damage;
		
		if (hitpoints < 0)
			hitpoints = 0;

		/*
		 * Start our death task since we are now dead
		 */
		if (hitpoints == 0) {
			setDead(true);
			Combat.resetCombat(this);
			Server.getTaskScheduler().schedule(new NPCDeathTask(this));
		}
		return new Hit(damage, hit.getType());
	}

	public Location getLocation() {
		return new Location(absX, absY, heightLevel);
	}

	public NPCDefinitions getDefinition() {
        NPCDefinitions def = NPCDefinitions.get(npcId);
        if (def == null)
            return NPCDefinitions.get(1);
        return def;
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
	
	private int size = 1;
	
	public int getSize() {
		return size;
	}

	public void clearUpdateFlags() {
		this.reset();
		moveX = 0;
		moveY = 0;
		direction = -1;
		this.entityFaceIndex = -1;
		faceTileY = -1;
		Object tele = getAttribute("teleporting", null);
		boolean teleporting = tele != null && (boolean) tele;
		if (teleporting) {
			setOnTile(absX, absY, heightLevel);
			setAttribute("teleporting", false);
		}
		this.getUpdateFlags().primary = null;
		this.getUpdateFlags().secondary = null;
		this.getUpdateFlags().reset();
	}

	/**
	 * Gets a list of surrounding players near the mob
	 *
	 * @param mob
	 *            the mob
	 * @return the list of players surrounding the mob
	 */
	public static final List<Player> getSurroundingPlayers(final NPC mob, int distance) {
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
			NPCHandler.spawnNpc(player, 6613, new Location(absX - 1, absY, heightLevel), 1, true, false, true);
			NPCHandler.spawnNpc(player, 6613, new Location(absX - 1, absY, heightLevel), 1, true, false, true);
			dogs += 2;
			spawnedVetionMinions = true;
		} else if (npcId == 6612) {
			NPCHandler.spawnNpc(player, 6614, new Location(absX - 1, absY, heightLevel), 1, true, false, true);
			NPCHandler.spawnNpc(player, 6614, new Location(absX - 1, absY, heightLevel), 1, true, false, true);
			dogs += 2;
			spawnedVetionMinions = true;
		}
	}
	
	@Override
	public void process() {
		try {
			Player spawnedByPlr = World.getWorld().getPlayers().get(spawnedBy);
			// none yet again duplicate INTs by PI
			
			if ((this.getHitpoints() > 0 && !isDead()) || isPet) {
				
				super.frozen_process();

				// Only ever call following from here.
				if (isPet && ownerId > 0) {
					Player ownerPlr = World.getWorld().getPlayers().get(ownerId);
					if (ownerPlr == null) {
						System.out.println("owner disappeared!!!");
						ownerId = -1;
					} else {
						//System.out.println("NPC Following player");
						NPCFollowing.attemptFollowEntity(this, ownerPlr);
					}
				} else if (this.followTarget != null) {
					NPCFollowing.attemptFollowEntity(this, followTarget);
				}
				
				if (npcId == 6611 || npcId == 6612) {
					if (this.getHitpoints() < (this.getMaxHitpoints() / 2) && !spawnedVetionMinions) {
						spawnVetDogs(spawnedByPlr);
					}
				}
				else if (npcId == 6615) {
					if (this.getHitpoints() <= 100 && !spawnedScorpiaMinions) {
						NPC min1 = NPCHandler.spawnNpc(spawnedByPlr, 6617, new Location(getX()- 1, absY, heightLevel), 1, true, false, true);
						NPC min2 = NPCHandler.spawnNpc(spawnedByPlr, 6617, new Location(getX() + 1, absY, heightLevel), 1, true, false, true);
						// attributes not used atm
						this.setAttribute("min1", min1);
						min1.setAttribute("boss", this);
						this.setAttribute("min2", min2);
						min2.setAttribute("boss", this);
						// flag spawned
						spawnedScorpiaMinions = true;
						// start task
						//Scorpia.heal_scorpia(this, min1);
						//Scorpia.heal_scorpia(this, min2);
					}
				}
			}
			
			/*
			 * Handle our combat timers
			 */
			NpcVsPlayerCombat.handleCombatTimer(this);

			if (spawnedBy > 0 && (World.getWorld().getPlayers().get(spawnedBy) == null || World.getWorld().getPlayers().get(spawnedBy).heightLevel != heightLevel || World.getWorld().getPlayers().get(spawnedBy).isDead() || !spawnedByPlr.goodDistance(getX(), getY(), World.getWorld().getPlayers().get(spawnedBy).getX(), World.getWorld().getPlayers().get(spawnedBy).getY(), 20))) {
				World.getWorld().unregister(this);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void handleFacing() {
		if (walking_type == 2) {
			face(this, new Location(getX() + 1, getY()));
			// face east
		} else if (walking_type == 3) {
			face(this, new Location(getX(), getY() - 1));
			// face south
		} else if (walking_type == 4) {
			face(this, new Location(getX() - 1, getY()));
			// face west
		} else if (walking_type == 5) {
			face(this, new Location(getX(), getY() + 1));
			// face north
		}
	}

	@Override
	public EntityType getEntityType() {
		return EntityType.NPC;
	}


	/**
	 * Difference in X coordinates for directions array.
	 */
	public static final byte[] DIRECTION_DELTA_X = new byte[] { -1, 0, 1, -1, 1, -1, 0, 1 };

	/**
	 * Difference in Y coordinates for directions array.
	 */
	public static final byte[] DIRECTION_DELTA_Y = new byte[] { 1, 1, 1, 0, 0, -1, -1, -1 };

	public int distanceToPoint(int pointX, int pointY) {
		return (int) Math.sqrt(Math.pow(getX() - pointX, 2) + Math.pow(getY() - pointY, 2));
	}
	
	public int walkX, walkY;

	public void getNextNPCMovement(NPC npc) {
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

	public int distanceTo(NPC npc) {
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
	
	public void remove() {
		setVisible(false);
		setAbsX(0);
		setAbsY(0);
	}
	
	public boolean distance(int objectX, int objectY, int playerX, int playerY, int distance) {
		return Math.sqrt(Math.pow(objectX - playerX, 2) + Math.pow(objectY - playerY, 2)) <= distance;
	}
	
	public boolean isArmadylNpc() {
		return npcId >= 3162 && npcId <= 3165;
	}
	
	@Override
	public ActionSender getActionSender() {
		return null;
	}

	public static String getName(int npcId) {
		if (NPCDefinitions.get(npcId) == null || npcId < 0 || npcId >= NPCDefinitions.NPCS) {
			return "None";
		}
		return NPCDefinitions.get(npcId).getName();
	}
	
	public String getName() {
		return npcName;
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

	@Override
	public boolean isNPC() {
		return true;
	}

	@Override
	public boolean isPlayer() {
		return false;
	}
	
	@Override
	public int getHeight() {
		return getDefinition().getSize();
	}

	@Override
	public int getWidth() {
		return getDefinition().getSize();
	}
	
	@Override
	public Location getCentreLocation() {
		if (this.getWidth() == 1 && this.getHeight() == 1) 
			return this.getLocation();
		return Location.create(getLocation().getX() + getWidth() / 2, getLocation().getY() + getHeight() / 2, getLocation().getZ());
	}
	
	@Override
	public int getProjectileLockonIndex() {
		return getIndex() + 1;
	}

	@Override
	public int clientIndex() {
		return this.getIndex();
	}
	
	@Override
	public void setDefaultAnimations() {

	}

	public void retaliate(Entity attacker) {
		// Set npc's target to the person that attacked us
		this.targetId = attacker.getIndex();
		faceEntity(attacker);
	}

	public int getAttackAnimation() {
		if (npcId >= 1694 && npcId <= 1703) {
			return 3901;
		}
		if (npcId >= 1704 && npcId <= 1708) {
			return 3915;
		}
		return getDefinition().getAttackAnimation();
	}
	
	public int getDeathAnimation() {
		return getDefinition().getDeathAnimation();
	}

	public int getDefendAnimation() {
		return getDefinition().getDefenceAnimation();
	}
	
	/**
	 * Array of all bosses.
	 */
	public static final int[] BOSSES = { 
		6609, //Callisto
		2054, //Chaos Elemental
		6619, //Chaos Fanatic
		2205, //Commander Zilyana
		319, //Corporeal Beast
		239, //King Black Dragon
		3129, //K'ril Tsutsaroth
		2215, //General Graardor
	};
	
	/**
	 * The combat cooldown delay.
	 */
	private int combatCooldownDelay = 4;
	
	@Override
	public int getCombatCooldownDelay() {
		return combatCooldownDelay;
	}

	/**
	 * @param combatCooldownDelay
	 *            the combatCooldownDelay to set
	 */
	public void setCombatCooldownDelay(int combatCooldownDelay) {
		this.combatCooldownDelay = combatCooldownDelay;
	}
}

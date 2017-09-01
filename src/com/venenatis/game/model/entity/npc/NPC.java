package com.venenatis.game.model.entity.npc;

import java.util.ArrayList;
import java.util.List;

import com.venenatis.game.location.Location;
import com.venenatis.game.model.combat.Combat;
import com.venenatis.game.model.combat.NpcCombat;
import com.venenatis.game.model.combat.npcs.impl.wilderness.Scorpia;
import com.venenatis.game.model.definitions.NPCDefinitions;
import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.entity.Hit;
import com.venenatis.game.model.entity.following.NPCFollowing;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.UpdateFlags.UpdateFlag;
import com.venenatis.game.net.packet.ActionSender;
import com.venenatis.game.task.impl.NPCDeathTask;
import com.venenatis.game.util.Stopwatch;
import com.venenatis.game.util.Utility;
import com.venenatis.game.world.World;
import com.venenatis.game.world.pathfinder.ProjectilePathFinder;
import com.venenatis.game.world.pathfinder.clipmap.Region;
import com.venenatis.server.Server;

public class NPC extends Entity {
	
	public NPC(int _npcType) {
		this(_npcType, null, -1);
	}
	
	public NPC(int id, Location spawn, int dir) {
		super(EntityType.NPC);
		direction = dir;
		if (spawn != null) {
			setLocation(spawn);
			makeX = spawn.getX(); // boundaries where combat is cancelled stops dragging npcs over the map
			makeY = spawn.getY();
			setOnTile(spawn.getX(), spawn.getY(), spawn.getZ());
		}
		npcId = id;
		getCombatState().setDead(false);
		randomWalk = true;
		NPCDefinitions definition = NPCDefinitions.get(id);
		if (definition != null) {
			size = definition.getSize();
			if (size < 1) {
				size = 1;
			}
			npcName = definition.getName();
			setCombatLevel(definition == null ? 1 : definition.getCombatLevel());
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
	 * Checks if the npc is a pet
	 */
	public boolean isPet;
	
	/**
	 * Checks if the player owns the pet
	 */
	public int ownerId;
	
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
	 * Checks the last location the npc was on
	 */
	private Location lastLocation = null;

	/**
	 * Requesting the transformation
	 * @param Id
	 */
	public void requestTransform(int Id) {
		npcId = Id;
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
	public int makeX, makeY;
	
	public int spawnedBy, oldIndex, underAttackBy, walking_type;
	
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
			Region.getRegion(x, y).getStore().putNpcOnTile(tile.getX(), tile.getY(), tile.getZ());
		lastLocation = new Location(x, y, z);
	}

	/**
	 * Remove an npc from the current tile
	 */
	public void removeFromTile() {
		if (lastLocation != null) {
			for (Location tile : getTiles(lastLocation))
				Region.getRegion(getX(), getY()).getStore().removeNpcFromTile(tile.getX(), tile.getY(), tile.getZ());
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
		setLocation(position);
		getAttributes().put("teleporting", true);
	}
	
	@Override
	public void onDeath() {
		
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
	public void message(String s) {
		// silent
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
			getCombatState().setDead(true);
			Combat.resetCombat(this);
			Server.getTaskScheduler().schedule(new NPCDeathTask(this));
		}
		return new Hit(damage, hit.getType());
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

				if (Region.getRegion(b.getX(), b.getY()).getStore().isNpcOnTile(b.getX(), b.getY(), b.getZ())) {
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
		this.removeAttribute("teleporting");
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
				if (player.getCombatState().isDead() || (player.getZ() != mob.getZ())) {
					continue;
				}

				if (player.distanceToPoint(mob.getX(), mob.getY()) < distance) {
					surrounding.add(player);
				}

			}
		}
		return surrounding;
	}
	
	public NPC spawnBossMinion(Player p, int id, Location location, int walkType, boolean attackPlayer) {
		NPC npc = new NPC(id, location, walkType);
		npc.walking_type = walkType;
		if (attackPlayer) {
			npc.underAttack = true;
			if (p != null) {
				npc.targetId = p.getIndex();
			}
		}
		World.getWorld().register(npc);
		return npc;
	}

	@Override
	public void process() {
		try {
			Player spawnedByPlr = World.getWorld().getPlayers().get(spawnedBy);
			// none yet again duplicate INTs by PI

			if ((this.getHitpoints() > 0 && !getCombatState().isDead()) || isPet) {

				super.frozen_process();

				// Only ever call following from here.
				if (isPet && ownerId > 0) {
					Player ownerPlr = World.getWorld().getPlayers().get(ownerId);
					if (ownerPlr == null) {
						//System.out.println("owner disappeared!!!");
						ownerId = -1;
					} else {
						//System.out.println("NPC Following player");
						NPCFollowing.attemptFollowEntity(this, ownerPlr);
					}
				} else if (this.followTarget != null) {
					NPCFollowing.attemptFollowEntity(this, followTarget);
				}

				if (npcId == 6615) {
					if (this.getHitpoints() <= 100 && !hasAttribute("scorpia_minion")) {
						NPC min1 = spawnBossMinion(spawnedByPlr, 6617, new Location(getX()- 1, getY(), getZ()), 1, false);
						NPC min2 = spawnBossMinion(spawnedByPlr, 6617, new Location(getX() + 1, getY(), getZ()), 1, false);
						// attributes not used atm
						this.setAttribute("min1", min1);
						min1.setAttribute("boss", this);
						this.setAttribute("min2", min2);
						min2.setAttribute("boss", this);
						// flag spawned
						this.setAttribute("scorpia_minion", true);
						// start task
						Scorpia.heal_scorpia(this, min1);
						Scorpia.heal_scorpia(this, min2);
					}
				}
			}

			/*
			 * Handle our combat timers
			 */
			NpcCombat.handleCombatTimer(this);

			if (spawnedBy > 0 && (World.getWorld().getPlayers().get(spawnedBy) == null || World.getWorld().getPlayers().get(spawnedBy).getZ() != getZ() 
					|| World.getWorld().getPlayers().get(spawnedBy).getCombatState().isDead() 
					|| !spawnedByPlr.goodDistance(getX(), getY(), World.getWorld().getPlayers().get(spawnedBy).getX(), World.getWorld().getPlayers().get(spawnedBy).getY(), 20)) && getId() != 3127) {
				World.getWorld().unregister(this);
			}
			updateCoverage(getLocation());
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
		dir = Utility.direction(getX(), getY(), (getX() + moveX), (getY() + moveY));
		if (dir == -1)
			return -1;
		dir >>= 1;
		setLocation(getLocation().transform(moveX, moveY));
		return dir;
	}
	
	// The DIRECTION moved in x/y axis this cycle. used in updating.
	public int moveX, moveY;

	
	public boolean distance(int npcX, int npcY, int playerX, int playerY, int distance) {
		return Math.sqrt(Math.pow(npcX - playerX, 2) + Math.pow(npcY - playerY, 2)) <= distance;
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
		//System.out.println("NPC NAME: "+NPCDefinitions.get(npcId).getName());
		return NPCDefinitions.get(npcId).getName();
	}

	public String getName() {
		return npcName;
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
	public int yLength() {
		return getDefinition().getSize();
	}

	@Override
	public int getWidth() {
		return getDefinition().getSize();
	}
	
	@Override
	public Location getCentreLocation() {
		if (this.getWidth() == 1 && this.yLength() == 1)
			return this.getLocation();
		return Location.create(getLocation().getX() + getWidth() / 2, getLocation().getY() + yLength() / 2, getLocation().getZ());
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
		5779, //Giant mole
		6361, //Dagannoth mother
		6362, //Dagannoth mother
		6365, //Dagannoth mother
		3359, //Zombies champion
		2267, //Dagannoth rex
		2266, //Dagannoth prime
		2265, //Dagannoth supreme
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

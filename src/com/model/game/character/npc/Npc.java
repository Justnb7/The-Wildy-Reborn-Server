package com.model.game.character.npc;

import com.model.Server;
import com.model.game.World;
import com.model.game.character.Entity;
import com.model.game.character.Hit;
import com.model.game.character.combat.CombatDamage;
import com.model.game.character.combat.nvp.NpcVsPlayerCombat;
import com.model.game.character.npc.combat.combat_scripts.Scorpia;
import com.model.game.character.player.ActionSender;
import com.model.game.character.player.Boundary;
import com.model.game.character.player.Player;
import com.model.game.character.player.ProjectilePathFinder;
import com.model.game.location.Position;
import com.model.task.impl.NPCDeathTask;
import com.model.utility.Stopwatch;
import com.model.utility.Utility;
import com.model.utility.cache.map.Region;
import com.model.utility.json.definitions.NpcDefinition;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

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
	
	public boolean forClue;
	
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
	private Position lastLocation = null;

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
	
	//fml called to many times lmao cant we just replace all with owner? 
	// nah cos they might be used in diff situations, its fine tho everything will work
	// lets remove the debugs and commit
	
	public int combatLevel, attackStyle, projectileId, endGfx, spawnedBy, hitDelayTimer, currentHealth, maximumHealth,
			attackTimer, killedBy, oldIndex, underAttackBy, walking_type;
	
	/**
	 * The Index of our Target - the Player we're attacking. PLAYER ONLY. TODO make this Entity instead of Int
	 */
	public int targetId;
	
	public boolean noDeathEmote, isDead, walkingHome, underAttack, randomWalk;

	public boolean aggressive;
	
	public long lastDamageTaken;
	
	public boolean isPoisoned() {
		if (poisonDamage > 0)
			return true;
		else 
			return false;
	}
	
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
	
	public Npc(int _npcType) {
		this(_npcType, null, -1);
	}
	
	public Npc(int id, Position spawn, int dir) {
		super(EntityType.NPC);
		direction = dir;
		if (spawn != null)
			setLocation(spawn);
		npcId = id;
		
		isDead = false;
		randomWalk = true;
		
		NpcDefinition definition = NpcDefinition.getDefinitions()[id];
		if (definition != null) {
			size = definition.getSize();
			if (size < 1) {
				size = 1;
			}
			combatLevel = definition == null ? 1 : definition.getCombatLevel();
			currentHealth = definition.getHitpoints();
			maximumHealth = definition.getHitpoints();
			maxHit = definition.getMaxHit();
			attack_bonus = definition.getAttackBonus();
			magic_defence = definition.getMagicDefence();
			melee_defence = definition.getMeleeDefence();
			range_defence = definition.getRangedDefence();
			//System.out.println("size: "+size+ " max: "+maxHit+" melee_def: "+melee_defence+" range_def: "+range_defence+" magic_def: "+magic_defence);
		}
	}

	/**
	 * Set an npc onto a tile, removes it from the current tile before placing
	 * another one
	 */
	public void setOnTile(int x, int y, int z) {
		removeFromTile();
		for (Position tile : getTiles(new Position(x, y, z)))
			Region.putNpcOnTile(tile.getX(), tile.getY(), tile.getZ());
		lastLocation = new Position(x, y, z);
	}

	/**
	 * Remove an npc from the current tile
	 */
	public void removeFromTile() {
		if (lastLocation != null) {
			for (Position tile : getTiles(lastLocation))
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
		setLocation(new Position(x, y, z));
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

	public Position getPosition() {
		return new Position(absX, absY, heightLevel);
	}

	public NpcDefinition getDefinition() {
        NpcDefinition def = NpcDefinition.getDefinitions()[npcId];
        if (def == null)
            return NpcDefinition.getDefinitions()[1];
        return def;
	}

	public Position[] getTiles(Position location) {
		Position[] tiles = new Position[getSize() == 1 ? 1 : (int) Math.pow(getSize(), 2)];
		int index = 0;

		for (int i = 1; i < getSize() + 1; i++) {
			for (int k = 0; k < SIZE_DELTA_COORDINATES[i].length; k++) {
				int x3 = location.getX() + SIZE_DELTA_COORDINATES[i][k][0];
				int y3 = location.getY() + SIZE_DELTA_COORDINATES[i][k][1];
				tiles[index] = new Position(x3, y3, location.getZ());
				index++;
			}
		}
		return tiles;
	}

	public Position[] getTiles() {
		return getTiles(getPosition());
	}

	/**
	 * Gets the border around the edges of the npc.
	 * 
	 * @return the border around the edges of the npc, depending on the npc's
	 *         size.
	 */
	public Position[] getBorder() {
		int x = getPosition().getX();
		int y = getPosition().getY();
		int size = getSize();
		if (size <= 1) {
			return new Position[] { getPosition() };
		}

		Position[] border = new Position[(size) + (size - 1) + (size - 1) + (size - 2)];
		int j = 0;

		border[0] = new Position(x, y, 0);

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
				border[(++j)] = new Position(x, y, 0);
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
	public boolean canMoveTo(Position src, int direction) {
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

				Position a = new Position(x3, y3, z);
				Position b = new Position(x2, y2, z);

				if (Position.isWithinBlock(x, y, size, x2, y2)) {
					continue;
				}

				if (!Region.canMove(a, direction)) {
					return false;
				}

				if (Region.isNpcOnTile(b.getX(), b.getY(), b.getZ())) {
					return false;
				}

				for (int dir = 0; dir < 8; dir++) {
					if (Position.isWithinBlock(x5, y5, getSize(), x2 + DIRECTION_DELTA_X[dir], y2 + DIRECTION_DELTA_Y[dir])) {
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
		updateRequired = false;
		forcedChatUpdateRequired = false;
		hitUpdateRequired = false;
		hitUpdateRequired2 = false;
		this.animUpdateRequired = false;
		this.faceUpdateRequired = false;
		if (transformUpdateRequired) {
			transformUpdateRequired = false;
			this.npcId = this.transformId;
			this.transformId = -1;
	        this.maximumHealth = this.getDefinition() == null ? currentHealth : this.getDefinition().getHitpoints();
			
		}
		this.gfxUpdateRequired = false;
		moveX = 0;
		moveY = 0;
		direction = -1;
		this.entityFaceIndex = -1;
		faceTileY = -1;
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
			NPCHandler.spawnNpc(player, 6613, absX - 1, absY, heightLevel, 1, 190, 20, 115, 85, 85, 85, true, false, true);
			NPCHandler.spawnNpc(player, 6613, absX - 1, absY, heightLevel, 1, 190, 20, 115, 85, 85, 85, true, false, true);
			dogs += 2;
			spawnedVetionMinions = true;
		} else if (npcId == 6612) {
			NPCHandler.spawnNpc(player, 6614, absX - 1, absY, heightLevel, 1, 190, 25, 125, 95, 95, 95, true, false, true);
			NPCHandler.spawnNpc(player, 6614, absX - 1, absY, heightLevel, 1, 190, 25, 125, 95, 95, 95, true, false, true);
			dogs += 2;
			spawnedVetionMinions = true;
		}
	}
	
	@Override
	public void process() {
		try {
			Player spawnedByPlr = World.getWorld().getPlayers().get(spawnedBy);//whats the pointsin this
			// none yet again duplicate INTs by PI
			
			if (currentHealth > 0 && !isDead) {
				
				super.frozen_process();

				// Only ever call following from here.
				if (isPet && ownerId > 0) {
					Player ownerPlr = World.getWorld().getPlayers().get(ownerId);
					if (ownerPlr == null) {
						System.out.println("owner disappeared!!!");
						ownerId = -1;
					} else {
						//System.out.println("NPC Following player");
						NPCHandler.attemptFollowEntity(this, ownerPlr);
					}
				} else if (this.following_target != null) {
					NPCHandler.attemptFollowEntity(this, following_target);
				} else if (this.followTargetIndex > -1) {
					// TODO remove followTargetIndex fully in favour of follow_target - so it's
					// entity based rather than that PI shit
					Player ftarg = World.getWorld().getPlayers().get(followTargetIndex);
					if (ftarg != null) {
						NPCHandler.attemptFollowEntity(this, ftarg);
					} else {
						//System.out.println("ftarg null.. disgarding");
						this.followTargetIndex = -1;
					}
				}
				
				if (npcId == 6611 || npcId == 6612) {
					if (currentHealth < (maximumHealth / 2) && !spawnedVetionMinions) {
						spawnVetDogs(spawnedByPlr);
					}
				}
				else if (npcId == 6615) {
					if (currentHealth <= 100 && !spawnedScorpiaMinions) {
						Npc min1 = NPCHandler.spawnNpc(spawnedByPlr, 6617, getX()- 1, absY, heightLevel, 1, 79, -1, -1, -1, -1, 0, true, false, true);
						Npc min2 = NPCHandler.spawnNpc(spawnedByPlr, 6617, getX() + 1, absY, heightLevel, 1, 79, -1, -1, -1, -1, 0, true, false, true);
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
			face(this, new Position(getX() + 1, getY()));
			// face east
		} else if (walking_type == 3) {
			face(this, new Position(getX(), getY() - 1));
			// face south
		} else if (walking_type == 4) {
			face(this, new Position(getX() - 1, getY()));
			// face west
		} else if (walking_type == 5) {
			face(this, new Position(getX(), getY() + 1));
			// face north
		}
	}

	@Override
	public EntityType getEntityType() {
		return EntityType.NPC;
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

	/**
	 * We can save our npc's last health before jumping
	 */
	public int storeLastHealth;
	
	public boolean isRespawnable() {
		return respawnable;
	}

	public void setRespawnable(boolean respawnable) {
		this.respawnable = respawnable;
	}
	
	public void kill(Npc npc) {
		npc.setAbsX(-1);
		npc.setAbsY(-1);
		npc.setVisible(false);
		npc.removeFromTile();
	}
	
	public void resetCombat() {
		walkingHome = true;
		underAttack = false;
		randomWalk = true;
		currentHealth = maximumHealth;
	}
	
	public void remove() {
		setVisible(false);
		if (NPCHandler.npcs[npcId] == this) {
			NPCHandler.npcs[npcId] = null;
		}
		setAbsX(0);
		setAbsY(0);
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
	public boolean isDead() {
		return isDead;
	}

	@Override
	public int clientIndex() {
		return this.getIndex();
	}

	public void retaliate(Entity attacker) {

		// if (NPCCombatData.switchesAttackers(victim)) // TODO whats this 1 for

		// Set npc's target to the person that attacked us
		this.targetId = attacker.getIndex();
		faceEntity(attacker);
	}
}

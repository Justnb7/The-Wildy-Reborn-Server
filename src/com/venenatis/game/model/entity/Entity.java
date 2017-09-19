package com.venenatis.game.model.entity;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.google.common.base.Preconditions;
import com.venenatis.game.action.ActionQueue;
import com.venenatis.game.constants.EquipmentConstants;
import com.venenatis.game.content.sounds_and_music.sounds.MobAttackSounds;
import com.venenatis.game.content.sounds_and_music.sounds.PlayerSounds;
import com.venenatis.game.location.Location;
import com.venenatis.game.model.Projectile;
import com.venenatis.game.model.Skills;
import com.venenatis.game.model.combat.CombatState;
import com.venenatis.game.model.combat.NpcCombat;
import com.venenatis.game.model.combat.PrayerHandler;
import com.venenatis.game.model.combat.combat_effects.BarrowsEffect;
import com.venenatis.game.model.combat.data.CombatStyle;
import com.venenatis.game.model.combat.magic.spell.impl.Vengeance;
import com.venenatis.game.model.entity.npc.NPC;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.equipment.PoisonType;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.model.masks.Graphic;
import com.venenatis.game.model.masks.Sprites;
import com.venenatis.game.model.masks.UpdateFlags;
import com.venenatis.game.model.masks.UpdateFlags.UpdateFlag;
import com.venenatis.game.net.packet.ActionSender;
import com.venenatis.game.task.Task;
import com.venenatis.game.task.impl.PoisonCombatTask;
import com.venenatis.game.task.impl.VenomDrainTick;
import com.venenatis.game.util.MutableNumber;
import com.venenatis.game.util.Utility;
import com.venenatis.game.world.World;
import com.venenatis.game.world.pathfinder.BasicPoint;
import com.venenatis.game.world.pathfinder.Directions;
import com.venenatis.game.world.pathfinder.PathFinder;
import com.venenatis.game.world.pathfinder.PathState;
import com.venenatis.game.world.pathfinder.TileControl;
import com.venenatis.game.world.pathfinder.region.Coverage;
import com.venenatis.game.world.pathfinder.region.RegionStore;
import com.venenatis.game.world.pathfinder.region.RegionStoreManager;
import com.venenatis.server.Server;

/**
 * @author Patrick van Elderen
 * @author Jak
 */
public abstract class Entity {
	
	/**
	 * The current region.
	 */
	private RegionStore currentRegion;
	
	/**
	 * Gets the current region.
	 * @return The current region.
	 */
	public RegionStore getRegion() {
		return currentRegion;
	}

	/**
	 * Sets the current region.
	 * @param region The region to set.
	 */
	public void setRegion(RegionStore region) {
		this.currentRegion = region;
	}
	
    private int combatLevel;
	
	public int getCombatLevel() {
		return combatLevel;
	}

	public void setCombatLevel(int combatLevel) {
		this.combatLevel = combatLevel;
	}
	
	/**
	 * The players energy restore tickable.
	 */
	private Task energyRestoreTick;
	
	/**
	 * @return the energyRestoreTick
	 */
	public Task getEnergyRestoreTick() {
		return energyRestoreTick;
	}
	
	/**
	 * @param energyRestoreTick the energyRestoreTick to set
	 */
	public void setEnergyRestoreTick(Task energyRestoreTick) {
		this.energyRestoreTick = energyRestoreTick;
	}
	
	/**
	 * A queue of actions.
	 */
	private final ActionQueue actionQueue = new ActionQueue(this);
	
	/**
	 * Gets the action queue.
	 * @return The action queue.
	 */
	public ActionQueue getActionQueue() {
		return actionQueue;
	}
	
	/**
	 * The random identifier
	 */
	private Random random = new Random();
	
	/**
	 * Gets the random number generator.
	 *
	 * @return The random number generator.
	 */
	public Random getRandom() {
		return random;
	}

	public Entity followTarget;

    public void setFollowing(Entity following) {
        this.followTarget = following;
    }

    public void run(Task o) {
    	Server.getTaskScheduler().schedule(o);
    }

	public abstract boolean moving();

    public abstract int size();

	public boolean touchDistance(Entity target, int dist) {
		if (size() == 1 && target.size() == 1) {
			return goodDistance(getX(), getY(), target.getX(), target.getY(), dist);
		} else {
			Location[] me = getBorder();
			Location[] other = target.getBorder();
			int distance = 16;
			for (Location a : me) {
				for (Location b : other) {
					int gap = a.distance(b);
					if (gap <= dist)
						distance = gap;
				}
			}
			if (this.isPlayer()) {
				((Player)this).debug("dist to >1x1 npc is "+distance+" goal "+dist);
			}
			return distance <= dist;
		}
	}
	
	private int stamina;
	
	public int getStaminaConfig() {
		return stamina;
	}
	
	public void setStamina(int stamina) {
		this.stamina = stamina;
	}
	
	private int infection;
	
	public int getInfection() {
		return infection;
	}
	
	public void setInfection(int infection) {
		this.infection = infection;
	}

	public abstract void message(String s);

	public enum EntityType {
		PLAYER, NPC,
	}

	public int lastX;
	public int lastY;
	public transient Object distanceEvent;
	private boolean registered;
	public Location lastTile;

	/**
	 * Gets the current location.
	 * 
	 * @return The current location.
	 */
	public Location getLocation() {
		return location;
	}
	
	/**
	 * Sets the current location.
	 * 
	 * @param location
	 *            The current location.
	 */
	public void setLocation(Location location) {
		this.location = location;
		
		RegionStore newRegion = World.getWorld().regions.getRegionByLocation(location);
		if(newRegion != getRegion()) {
			if(getRegion() != null) {
				// While location is currently the older one, remove from previous regions
				if (this.isPlayer())
					World.getWorld().regions.getRegionByLocation(location).removePlayer((Player)this);
				else 
					World.getWorld().regions.getRegionByLocation(location).removeNpc((NPC)this);	
			}
			setRegion(newRegion);
			TileControl.getSingleton().setOccupiedLocation(this, this.getTiles());
			if (this.isPlayer())
				World.getWorld().regions.getRegionByLocation(location).addPlayer((Player)this);
			else 
				World.getWorld().regions.getRegionByLocation(location).addNpc((NPC)this);
		}
	}

	public int getX() {
		return location.getX();
	}

	public int getY() {
		return location.getY();
	}

	public int getZ() {
		return location.getZ();
	}

	public int getLastX() {
		return lastX;
	}

	public int getLastY() {
		return lastY;
	}

	/**
	 * The current location.
	 */
	private Location location;

	/**
	 * The last known map region.
	 */
	private Location lastKnownRegion = this.getLocation();
	
	/**
	 * Map region changing flag.
	 */
	private boolean mapRegionChanging = false;

	/**
	 * The current animation.
	 */
	private Animation currentAnimation;

	/**
	 * The current graphic.
	 */
	private Graphic currentGraphic;
	
	/**
	 * Checks if the map region has changed in this cycle.
	 * 
	 * @return The map region changed flag.
	 */
	public boolean isMapRegionChanging() {
		return mapRegionChanging;
	}

	/**
	 * Sets the map region changing flag.
	 * 
	 * @param mapRegionChanging
	 *            The map region changing flag.
	 */
	public void setMapRegionChanging(boolean mapRegionChanging) {
		this.mapRegionChanging = mapRegionChanging;
	}
	
	/**
	 * The teleportation target.
	 */
	private Location teleportTarget = null;
	
	/**
	 * Checks if this entity has a target to teleport to.
	 * @return <code>true</code> if so, <code>false</code> if not.
	 */
	public boolean hasTeleportTarget() {
		return teleportTarget != null;
	}
	
	/**
	 * Gets the teleport target.
	 * @return The teleport target.
	 */
	public Location getTeleportTarget() {
		return teleportTarget;
	}
	
	/**
	 * Sets the teleport target.
	 * @param teleportTarget The target location.
	 */
	public void setTeleportTarget(Location teleportTarget) {
		if (this.isPlayer())
			asPlayer().getWalkingQueue().reset();
		this.teleportTarget = teleportTarget;
	}
	
	/**
	 * Resets the teleport target.
	 */
	public void resetTeleportTarget() {
		this.teleportTarget = null;
	}
	
	/**
	 * The sprites i.e. walk directions.
	 */
	private final Sprites sprites = new Sprites();
	
	/**
	 * Gets the sprites.
	 * @return The sprites.
	 */
	public Sprites getSprites() {
		return sprites;
	}
	
	/**
	 * The teleporting flag.
	 */
	private boolean teleporting = false;
	
	/**
	 * The PLAYER UPDATE FLAG for if position is changing this tick. NOT generic teleporting.
	 */
	public boolean isTeleporting() {
		return teleporting;
	}
	
	/**
	 * Sets the teleporting flag.
	 * @param teleporting The teleporting flag.
	 */
	public void setTeleporting(boolean teleporting) {
		this.teleporting = teleporting;
	}
	
	/**
	 * The update flags.
	 */
	private final UpdateFlags updateFlags = new UpdateFlags();
	
	/**
	 * The list of local players.
	 */
	private final List<Player> localPlayers = new LinkedList<Player>();
	
	/**
	 * The list of local npcs.
	 */
	private final List<NPC> localNpcs = new LinkedList<NPC>();
	
	/**
	 * Gets the list of local players.
	 * 
	 * @return The list of local players.
	 */
	public List<Player> getLocalPlayers() {
		return localPlayers;
	}

	/**
	 * Gets the list of local npcs.
	 * 
	 * @return The list of local npcs.
	 */
	public List<NPC> getLocalNPCs() {
		return localNpcs;
	}
	
	/**
	 * Gets the update flags.
	 * 
	 * @return The update flags.
	 */
	public UpdateFlags getUpdateFlags() {
		return updateFlags;
	}

	/**
	 * The characters combat type, MELEE by default
	 */
	private CombatStyle combatType = CombatStyle.MELEE;
	
	/**
	 * The permanent attributes map. Items set here are only removed when told to.
	 */
	protected Map<String, Object> attributes = new HashMap<String, Object>();

	@Override
	public final int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.getEntityType().hashCode();
		result = prime * result + this.getIndex();
		return result;
	}

	@Override
	public final boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Entity))
			return false;
		Entity other = (Entity) obj;
		if (this.getEntityType() != other.getEntityType())
			return false;
		if (this.getIndex() != other.getIndex())
			return false;
		return true;
	}

	/**
	 * The mobile character is visible
	 */
	private boolean visible = true;
	
	/**
     * The amount of poison damage this character has.
     */
    private final MutableNumber poisonDamage = new MutableNumber();

    /**
     * The type of poison that was previously applied.
     */
    private PoisonType poisonType;

	public abstract Hit decrementHP(Hit hit);
	
	/**
     * The method called when an entity dies.
     */
    public abstract void onDeath();

	public long lastWasHitTime;
	public Entity lastAttacker;
	
	/**
     * Gets the amount of poison damage this character has.
     *
     * @return the amount of poison damage.
     */
    public final MutableNumber getPoisonDamage() {
        return poisonDamage;
    }
	
    /**
     * Determines if this character is poisoned.
     *
     * @return {@code true} if this character is poisoned, {@code false}
     *         otherwise.
     */
    public final boolean isPoisoned() {
        return poisonDamage.get() > 0;
    }
    
    /**
     * Gets the type of poison that was previously applied.
     * 
     * @return the type of poison.
     */
    public PoisonType getPoisonType() {
        return poisonType;
    }

    /**
     * 
     * @param poisonType
     *            the new value to set.
     */
    public void setPoisonType(PoisonType poisonType) {
        this.poisonType = poisonType;
    }

	public boolean poison(PoisonType poisonType, Entity source) {
		Entity entity = this;
		if (entity.isPoisoned() || entity.getPoisonType() != null)
			return false;
		if (random.nextInt(3) == 0) {
			if (entity.type == EntityType.PLAYER) {
				Player player = (Player) entity;
				if (player.getPoisonImmunity().get() > 0)
					return false;
				player.setPoisonType(poisonType);
				player.getActionSender().sendMessage("You have been poisoned!");
				infection = 1;
				player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
			}
			entity.getPoisonDamage().set(entity.getPoisonType().getDamage());
			Server.getTaskScheduler().schedule(new PoisonCombatTask(this, source));
			return true;
		}
		return false;
	}

	/**
	 * Makes it show in Player Updating and also reduces your HITPOINTS (can kill you).
	 * This should only be called by take_hit. Take_hit deals with veng, recoil, protection prayers, auto retal etc, all bundled into one for simplicity.
	 */
	public void renderDamage(Hit... hits) {
		Preconditions.checkArgument(hits.length >= 1 && hits.length <= 4);

		switch (hits.length) {
		case 1:
			//System.out.println("hits "+hits[0].getDamage());
			sendDamage(hits[0]);
			break;
		case 2:
			sendDamage(hits[0], hits[1]);
			break;
		case 3:
			sendDamage(hits[0], hits[1], hits[2]);
			break;
		case 4:
			sendDamage(hits[0], hits[1], hits[2], hits[3]);
			break;
		}
	}

	private void primaryDamage(Hit hit) {
		// Set the player update mask and decrease HP
		getUpdateFlags().primary = decrementHP(hit);
		this.getUpdateFlags().flag(UpdateFlag.HIT);
	}

	private void secondaryDamage(Hit hit) {
		getUpdateFlags().secondary = decrementHP(hit);
		this.getUpdateFlags().flag(UpdateFlag.HIT_2);
	}

	private void sendDamage(Hit hit) {
		if (getUpdateFlags().get(UpdateFlag.HIT)) {
			secondaryDamage(hit);
			return;
		}
		primaryDamage(hit);
	}

	private void sendDamage(Hit hit, Hit hit2) {
		sendDamage(hit);
		secondaryDamage(hit2);
	}

	/**
	 * Sets the first two hitsplats, then the second ONE TICK later
	 */
	private void sendDamage(Hit hit, Hit hit2, Hit hit3) {
		sendDamage(hit, hit2); // two hitsplats

		Server.getTaskScheduler().submit(new Task(1, false) {
			@Override
			public void execute() {
				this.stop();
				if (!registered) { // still online/active
					return;
				}
				sendDamage(hit3); // a single hitsplat
			}
		});
	}

	/**
	 * Sets two hitsplats for this update cycle, then the other two 1 tick later
	 */
	private void sendDamage(Hit hit, Hit hit2, Hit hit3, Hit hit4) {
		sendDamage(hit, hit2);

		Server.getTaskScheduler().submit(new Task(1, false) {
			@Override
			public void execute() {
				this.stop();
				if (!registered) {
					return;
				}
				sendDamage(hit3, hit4);
			}
		});
	}
	
	public boolean hasAttribute(String string) {
		return attributes.containsKey(string);
	}
	
	/**
	 * Removes an attribute.<br />
	 * WARNING: unchecked cast, be careful!
	 *
	 * @param <T> The type of the value.
	 * @param key The key.
	 * @return The old value.
	 */
	@SuppressWarnings("unchecked")
	public <T> T removeAttribute(String key) {
		return (T) attributes.remove(key);
	}

	/**
	 * Removes an attribute.<br />
	 * WARNING: unchecked cast, be careful!
	 *
	 * @return The old value.
	 */
	public void removeAllAttributes() {
		if (attributes != null && attributes.size() > 0 && attributes.keySet().size() > 0) {
			attributes = new HashMap<String, Object>();
		}
	}

	/**
	 * Sets an attribute.<br />
	 * WARNING: unchecked cast, be careful!
	 *
	 * @param <T>   The type of the value.
	 * @param key   The key.
	 * @param value The value.
	 * @return The old value.
	 */
	@SuppressWarnings("unchecked")
	public <T> T setAttribute(String key, T value) {
		return (T) attributes.put(key, value);
	}

	/**
	 * Gets an attribute.<br />
	 * WARNING: unchecked cast, be careful!
	 *
	 * @param <T> The type of the value.
	 * @param key The key.
	 * @return The value.
	 */
	@SuppressWarnings("unchecked")
	public <T> T getAttribute(String key) {
		return (T) attributes.get(key);
	}

	@SuppressWarnings("unchecked")
	public <T> T getAttribute(String key, T fail) {
		if (attributes.containsKey(key))
			return (T) attributes.get(key);
		else
			return (T) fail;
	}

	/**
	 * Gets an attribute.<br />
	 * WARNING: unchecked cast, be careful!
	 *
	 * @return The value.
	 */
	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public boolean isRegistered() {
		return registered;
	}

	protected void setRegistered(boolean registered) {
		this.registered = registered;
	}

	public Player asPlayer() {
		return (Player) this;
	}

	public NPC asNpc() {
		return (NPC) this;
	}

	/**
	 * The mobile character is visible
	 * 
	 * @return
	 */
	public boolean isVisible() {
		return visible;
	}

	/**
	 * Sets this MobileCharacters visibility
	 * 
	 * @param visible
	 */
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	/**
	 * Determines the characters {@link CombatStyle}
	 * 
	 * @return The {@link CombatStyle} this character is attacking with
	 */
	public CombatStyle getCombatType() {
		return combatType;
	}

	/**
	 * Sets the characters new {@link CombatStyle}
	 * 
	 * @param type
	 *            The {@link CombatStyle} this character is attacking with
	 */
	public void setCombatType(CombatStyle type) {
		this.combatType = type;
	}

	/**
	 * Processes the entity every 600ms
	 */
	public abstract void process();
	
	/**
	 * Is this entity a player.
	 */
	public abstract boolean isPlayer();
	
	/**
	 * Is this entity an NPC.
	 */
	public abstract boolean isNPC();
	
	public NPC toNPC() {
		return isNPC() ? (NPC) this : null;
	}
	
	public Player toPlayer() {
		return isNPC() ? null : (Player) this;
	}
	
	public Hit take_hit(Entity attacker, int damage) {
		return take_hit(attacker, damage, null);
	}

	// Since damage gets reduced you need to add XP after this method.
	public Hit take_hit(Entity attacker, int damage, CombatStyle combat_type) {
		return take_hit(attacker, damage, combat_type, false, false);
	}

	public Hit take_hit(Entity attacker, int damage, CombatStyle combat_type, boolean instant) {
		return take_hit(attacker, damage, combat_type, instant, false);
	}
	
	// Means you don't have to type as many arguments. Used for venom, poison, can be for veng/recoil
	public Hit take_hit_generic(Entity attacker, int dmg, HitType type) {
		return take_hit(attacker, dmg, CombatStyle.GENERIC, false, false, type);
	}

	public Hit take_hit(Entity attacker, int damage, CombatStyle combat_type, boolean applyInstantly, boolean throughPrayer) {
		return take_hit(attacker, damage, combat_type, applyInstantly, throughPrayer, null);
	}

	/**
	 * Has damage adjustments in one place so save you writing the same code over and over
	 * Also includes rrayer reduction, ely effect (damage reduction)
	 * Should not have damage-dealing recoil such as reing of recoil/veng cos if you're teleporting you want to ignore that damage
	 */
	public Hit take_hit(Entity attacker, int damage, CombatStyle combat_type, boolean applyInstantly, boolean throughPrayer, HitType type) {
		
		// ALWAYS: FIRST APPLY DAMAGE REDUCTIONS, ABSORBS ETC. Protection pray/ely.
		// The entity taking damage is a player. 
		if (this.isPlayer()) {
			Player player_me = (Player) this;
			player_me.putInCombat(attacker.getIndex()); // we're taking a hit. we can't logout for 10s.
			
			// The victim (this) has protection prayer enabled.
			if (combat_type != null && !throughPrayer) {
				// 40% Protection from player attacks, 100% protection from Npc attacks
				double prayProtection = attacker.isPlayer() ? 0.6D : 0.0D;
				if (combat_type == CombatStyle.MELEE && PrayerHandler.isActivated(player_me, PrayerHandler.PROTECT_FROM_MELEE)) {
					damage *= prayProtection;
				}
				if (combat_type == CombatStyle.RANGE && PrayerHandler.isActivated(player_me, PrayerHandler.PROTECT_FROM_MISSILES)) {
					damage *= prayProtection;
				}
				if (combat_type == CombatStyle.MAGIC && combat_type == CombatStyle.GREEN_BOMB && PrayerHandler.isActivated(player_me, PrayerHandler.PROTECT_FROM_MAGIC)) {
					damage *= prayProtection;
				}
			}
			int shield = player_me.getEquipment().get(EquipmentConstants.SHIELD_SLOT) == null ? -1 : player_me.getEquipment().get(EquipmentConstants.SHIELD_SLOT).getId();
			// TODO special reduction effects can go here, like Ely
			
			if (shield == 13740) { //Divine
				if (player_me.getSkills().getLevel(Skills.PRAYER) > 0) {
					double damageRecieved = damage * 0.7;
					int prayerLost = (int) (damage * 0.5);
					if (player_me.getSkills().getLevel(Skills.PRAYER) >= prayerLost) {
						damage = (int) damageRecieved;
						player_me.getSkills().setLevel(Skills.PRAYER, player_me.getSkills().getLevel(Skills.PRAYER) - prayerLost);
						if (player_me.getSkills().getLevel(Skills.PRAYER) < 0)
							player_me.getSkills().setLevel(Skills.PRAYER, 0);
					}
				}
			}
			if (shield == 12817) { //Elysian
				if (Utility.getRandom(100) > 30 && damage > 0) {
					damage *= .75;
					player_me.playGraphic(Graphic.highGraphic(321));
				}
			}

		} else if (this.isNPC()) {
			NPC victim_npc = (NPC) this;
			
			if (attacker.isPlayer())
				NpcCombat.kraken((Player)attacker, victim_npc, damage);
			if (victim_npc.getId() == 319) {
				if (attacker.isNPC() || (attacker.isPlayer() && !EquipmentConstants.isWearingSpear(((Player)attacker)))) {
					damage /= 2;
				}
			}
			if (victim_npc.getId() == 5535) {
				damage = 0;
			}
			
			//Rex and Prime do not take melee damage
			if (combat_type == CombatStyle.MELEE && (victim_npc.getId() == 2267 || victim_npc.getId() == 2266 || victim_npc.getId() == 6365 || victim_npc.getId() == 6362)) {
				if (attacker.isPlayer())
					((Player)attacker).getActionSender().sendMessage("The dagannoth is currently resistant to that attack!");
				damage = 0;
			}
			//Rex and Supreme do not take range damage
			if (combat_type == CombatStyle.RANGE && (victim_npc.getId() == 2265 || victim_npc.getId() == 2267 || victim_npc.getId() == 6362 || victim_npc.getId() == 6361)) {
				((Player)attacker).getActionSender().sendMessage("The dagannoth is currently resistant to that attack!");
				damage = 0;
			}
			//Supreme and Prime do not take magic damage
			if (combat_type == CombatStyle.MAGIC && (victim_npc.getId() == 2265 || victim_npc.getId() == 2266 || victim_npc.getId() == 6361 || victim_npc.getId() == 6365)) {
				((Player)attacker).getActionSender().sendMessage("The dagannoth is currently resistant to that attack!");
				damage = 0;
			}
		}
		
		// At this point damage accurately reduced by stuff like prots/ely. 
		// Now we can use it to give XP/add to npcs damage tracker.

		if (isPlayer()) {
			Player me = (Player)this;

			if (attacker.isPlayer()) {
				Player pAttacker = (Player)attacker;
				BarrowsEffect.applyRandomEffect(pAttacker, me, damage);
			}
		}

		// This Entity is an npc taking damage from a player. 
		if (this.isNPC() && attacker.isPlayer()) {
			Player attacker_player = (Player)attacker;
			NPC victim_npc = (NPC) this;
			
			victim_npc.retaliate(attacker);
			victim_npc.getCombatState().getDamageMap().appendDamage(attacker_player.getUsername(), damage);
			MobAttackSounds.sendBlockSound(attacker_player, victim_npc.getId()); // TODO use npc not npcid
		} else if (isPlayer() && attacker.isPlayer()) {
			//pvp
			getCombatState().getDamageMap().appendDamage(((Player)attacker).getUsername(), damage);
		}

		// Update hit instance since we've changed the 'damage' value
		Hit hit = new Hit(damage, damage == 0 ? HitType.BLOCKED : type != null ? type : HitType.NORMAL).type(combat_type).between(attacker, this);

		// NOTE: If not instantly applied, use hit.delay(ticks) to make it appear after X ticks
		if (applyInstantly) {
			// The only place the damage() method should be called from, this method.
			this.takeDamage(hit);
			if (this.isPlayer())
				PlayerSounds.sendBlockOrHitSound((Player)this, damage > 0);
			if (attacker.isPlayer())
				PlayerSounds.sendBlockOrHitSound((Player)attacker, damage > 0);
		}
		// Returning hit: might be helpful in the future. For chaining. Such as hit.x().y()..
		return hit;
	}
	
	/**
	 * The default, i.e. spawn, location.
	 */
	public static final Location DEFAULT_LOCATION = new Location(3087, 3495, 0);

    /**
     * The index of the entity
     */
    private int index;

    private final EntityType type;

    public Entity(EntityType type) {
    	setLocation(DEFAULT_LOCATION);
		this.lastKnownRegion = location;
        this.type = type;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public EntityType getEntityType() {
        return type;
    }
    
    /**
	 * Sets the last known map region.
	 * 
	 * @param lastKnownRegion
	 *            The last known map region.
	 */
	public void setLastKnownRegion(Location lastKnownRegion) {
		this.lastKnownRegion = lastKnownRegion;
	}

	/**
	 * Gets the last known map region.
	 * 
	 * @return The last known map region.
	 */
	public Location getLastKnownRegion() {
		return lastKnownRegion;
	}
	
	/**
	 * The interacting entity.
	 */
	private Entity interactingEntity;
	
	/**
	 * Checks if this entity is interacting with another entity.
	 * 
	 * @return The entity interaction flag.
	 */
	public boolean isInteracting() {
		return interactingEntity != null;
	}

	/**
	 * Sets the interacting entity.
	 * 
	 * @param entity
	 *            The new entity to interact with.
	 */
	public void faceEntity(Entity entity) {
		this.interactingEntity = entity;
		this.updateFlags.flag(UpdateFlag.FACE_ENTITY);
	}

	/**
	 * Resets the interacting entity.
	 */
	public void resetFaceEntity() {
		faceEntity(null);
	}

	/**
	 * Gets the interacting entity.
	 * 
	 * @return The entity to interact with.
	 */
	public Entity getInteractingEntity() {
		return interactingEntity;
	}
	
	/**
	 * The face location.
	 */
	private Location faceTile;
	
	/**
	 * Makes this entity face a location.
	 * 
	 * @param location
	 *            The location to face.
	 */
	public void face(Location location) {
		this.faceTile = location;
		this.updateFlags.flag(UpdateFlag.FACE_COORDINATE);
	}

	/**
	 * Checks if this entity is facing a location.
	 * 
	 * @return The entity face flag.
	 */
	public boolean isFacing() {
		return faceTile != null;
	}

	/**
	 * Resets the facing location.
	 */
	public void resetFaceTile() {
		face(null);
	}

	/**
	 * Gets the face location.
	 * 
	 * @return The face location, or <code>null</code> if the entity is not
	 *         facing.
	 */
	public Location getFaceLocation() {
		return faceTile;
	}
	
    private boolean facePlayer = true;
	
	/**
	 * Determines if the npc can face another player
	 * 
	 * @return {@code true} if the npc can face players
	 */
	public boolean canFacePlayer() {
		return facePlayer;
	}

	/**
	 * Makes the npcs either able or unable to face other players
	 * 
	 * @param facePlayer {@code true} if the npc can face players
	 */
	public void setFacePlayer(boolean facePlayer) {
		this.facePlayer = facePlayer;
	}
	
	public abstract int clientIndex();

	public void playGraphic(Graphic graphic) {
		currentGraphic = graphic;
		this.getUpdateFlags().flag(UpdateFlag.GRAPHICS);
	}
	
	/**
	 * Gets the current graphic.
	 * 
	 * @return The current graphic.
	 */
	public Graphic getCurrentGraphic() {
		return currentGraphic;
	}
	
	/**
	 * Animates the entity.
	 *
	 * @param animation
	 *            The animation.
	 */
	public void playAnimation(Animation animation) {
		this.currentAnimation = animation;
		if (animation != null) {
			this.getUpdateFlags().flag(UpdateFlag.ANIMATION);
		}
	}
	
	/**
	 * Gets the current animation.
	 * 
	 * @return The current animation;
	 */
	public Animation getCurrentAnimation() {
		return currentAnimation;
	}
	
	/**
	 * Sends a message above the entitys head.
	 * @param message
	 *        The message being sent;
	 */
	public void sendForcedMessage(String message) {
		this.getUpdateFlags().setForcedMessage(message);
	}
	
	/**
	 * Resets attributes after an update cycle.
	 */
	public void reset() {
		this.currentAnimation = null;
		this.currentGraphic = null;
	}
	
	public int frozenForTicks, refreezeTicks;
	public Entity frozenBy;
	
	// example: barrage = freeze(20s/.6 ticks = 33 ticks)
	public void freeze(int ticks) {
		if (this.refreezeTicks > 0) // we're immune
			return;
		this.frozenForTicks = ticks;
		this.refreezeTicks = ticks + 3; // 3 ticks of immuity
	}
	
	public void frozenBy(Entity mager) {
		frozenBy = mager;
	}

	public void frozen_process() {
		// Reduce timers
		if (frozenForTicks > 0)
			this.frozenForTicks--;
		if (this.refreezeTicks > 0)
			this.refreezeTicks--;
		
		check_should_unfreeze();
		
	}

	private void check_should_unfreeze() {
		// Purpose: if whoever froze you is off screen (or null, they logged off) you get unfrozen.
		
		if (frozenBy == null)
			return;
		int opX = frozenBy.getX();
		int opY = frozenBy.getY();
		
		boolean out_of_dist = !goodDistance(getX(), getY(), opX, opY, 20);
		
		if (!frozenBy.registered || out_of_dist) {
			this.frozenForTicks = 0;
			this.refreezeTicks = 0;
			frozenBy = null;
		}
	}

	public boolean frozen() {
		return this.frozenForTicks > 0;
	}


	public int distanceToPoint(int pointX, int pointY) {
		return (int) Math.sqrt(Math.pow(getX() - pointX, 2) + Math.pow(getY() - pointY, 2));
	}

	public boolean goodDistance(int objectX, int objectY, int playerX, int playerY, int distance) {
		return ((objectX - playerX <= distance && objectX - playerX >= -distance) && (objectY - playerY <= distance && objectY - playerY >= -distance));
	}
	
	/**
	 * Plays graphics.
	 * @param graphic The graphics.
	 */
	public void playProjectile(Projectile projectile) {
		for(RegionStore r : RegionStoreManager.get().getSurroundingRegions(this.getLocation())) {
			for(Player p : r.getPlayers()) {
				if(p.getLocation().isWithinDistance(this.getLocation())) {
					p.getActionSender().sendProjectile(projectile.getStart(), projectile.getFinish(), projectile.getId(), projectile.getDelay(), projectile.getAngle(), projectile.getSpeed(), projectile.getStartHeight(), projectile.getEndHeight(),  projectile.getSlope(), projectile.getRadius(), projectile.getLockon());
				}
			}
		}
	}
	
	/**
	 * Gets the width of the entity.
	 * @return The width of the entity.
	 */
	public abstract int getWidth();
	
	/**
	 * Gets the width of the entity.
	 * @return The width of the entity.
	 */
	public abstract int yLength();
	
	/**
	 * Gets the centre location of the entity.
	 * @return The centre location of the entity.
	 */
	public abstract Location getCentreLocation();
	
	/**
	 * Gets the projectile lockon index of this mob.
	 *
	 * @return The projectile lockon index of this mob.
	 */
	public abstract int getProjectileLockonIndex();
	
	/**
	 * Returns the packet sender for the entity, mainly used for players
	 * 
	 * @return
	 */
	public abstract ActionSender getActionSender();
	
	/**
	 * Resets the mob's animations.
	 * @return Resets the mob's animations.
	 */
	public abstract void setDefaultAnimations();
	
	/**
	 * The force walk variables.
	 */
	private int[] forceWalk;
	
	/**
	 * Gets the force movements values
	 * @return
	 */
	public int[] getForceWalk() {
		return forceWalk;
	}

	/**
	 * Sets the force walk data
	 * @param forceWalk
	 * @param removeAttribute
	 */
	public void setForceWalk(final int[] forceWalk, final boolean removeAttribute) {
		this.forceWalk = forceWalk;
		if (forceWalk.length > 0) {
			World.getWorld().schedule(new Task(forceWalk[7]) {
				@Override
				public void execute() {
					setTeleportTarget(getLocation().transform(forceWalk[2], forceWalk[3], 0));
					if (removeAttribute) {
						removeAttribute("busy");
					}
					this.stop();
				}
			});
		}
	}
	
	/**
	 * The combat state.
	 */
	private final CombatState combatState = new CombatState(this);
	
	/**
	 * Gets the combat state.
	 * @return The combat state.
	 */
	public CombatState getCombatState() {
		return combatState;
	}
	
	/**
	 * Gets the current combat cooldown delay in milliseconds.
	 * @return The current combat cooldown delay.
	 */
	public abstract int getCombatCooldownDelay();

	public boolean canTrade() {
    	return true;
    }
	
	public boolean canDuel() {
    	return true;
    }

	private Coverage coverage = null;

	public Coverage getCoverage() {
		return coverage;
	}

	private void setCoverage() {
		coverage = new Coverage(getLocation(), size());
	}

	@SuppressWarnings("unused")
	private void updateCoverage(Directions.NormalDirection direction) {
		coverage.update(direction, size());
	}

	public void updateCoverage(Location loc) {
		//long startTime = System.currentTimeMillis();
		if (coverage == null) {
			setCoverage();
		}
		coverage.update(loc, size());
		//long endTime = System.currentTimeMillis() - startTime; System.out.println("[updateCoverage] end time: "+endTime + " : players online: " + World.getWorld().getPlayers().size());
	}

	public Location[] getTiles(Location location) {
		Location[] tiles = new Location[size() == 1 ? 1 : (int) Math.pow(size(), 2)];
		int index = 0;

		for (int i = 1; i < size() + 1; i++) {
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
	 * Contains the delta Locations for the x and y coordinate of actor model
	 * sizes.
	 */
	protected static final int[][][] SIZE_DELTA_COORDINATES = {
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
	 * Gets the border around the edges of the npc.
	 *
	 * @return the border around the edges of the npc, depending on the npc's
	 *         size.
	 */
	public Location[] getBorder() {
		int x = getLocation().getX();
		int y = getLocation().getY();
		int size = size();
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

	@Override
	public String toString() {
		return isPlayer() ? ((Player)this).getUsername() : ((NPC)this).getName();
	}

	public Entity getEntity() {
		return isPlayer() ? asPlayer() : asNpc();
	}
	
    private boolean canDamaged = true;
    
    public boolean canBeDamaged() {
        return canDamaged;
    }
    
    public void setCanBeDamaged(boolean b) {
        canDamaged = b;
    }
    
    public boolean canInflictVenom(Player player) {
    	if(player.getEquipment().containsAny(12931, 13197, 13199)) {
    		return false;
    	}
    	return true;
    }

    public enum VenomWeapons {
		TOXIC_BLOW_PIPE(12926),

		TOXIC_STAFF_OF_THE_DEAD(12904),

		TRIDENT_OF_THE_SWAMP(12899),

		/*SERPENTINE_HELMET(12931),

		TANZANITE_HELMET(13197),

		MAGMA_HELM(13199)*/;

		private int id;

		VenomWeapons(int id) {
			this.id = id;
		}

		private static Map<Integer, VenomWeapons> venomItemsMap = new HashMap<>();

		public static VenomWeapons of(int id) {
			return venomItemsMap.get(id);
		}

		static {
			for (VenomWeapons zulrahItem : VenomWeapons.values()) {
				venomItemsMap.put(zulrahItem.getId(), zulrahItem);
			}
		}

		public int getId() {
			return id;
		}
	}
    
    private VenomDrainTick venomDrainTick;
    
    public VenomDrainTick getVenomDrainTick() {
		return venomDrainTick;
	}
    
    public void setVenomDrainTick(VenomDrainTick venomDrainTick) {
		this.venomDrainTick = venomDrainTick;
	}
    
    public int venomDamage = 6;

	public Entity inflictVenom() {
		setAttribute("venom", true);
		setVenomDrainTick(new VenomDrainTick(this));
		World.getWorld().schedule(getVenomDrainTick());
		if (isPlayer()) {
			Player player = (Player) this;
			if (hasAttribute("venom")) {
				getActionSender().sendMessage("You have been poisoned by venom!");
			}
			player.setInfection(2);
			player.setVenomDamage(6);
			player.take_hit_generic(this, venomDamage, HitType.VENOM).send();
			player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
		} else if (isNPC()) {
			venomDamage = 6;
		}
		return this;
	}
	
	private boolean[] prayerActive = new boolean[30], curseActive = new boolean[20];

	public boolean[] getPrayerActive() {
		return prayerActive;
	}

	public boolean[] getCurseActive() {
		return curseActive;
	}

	public Entity setPrayerActive(boolean[] prayerActive) {
		this.prayerActive = prayerActive;
		return this;
	}

	public Entity setPrayerActive(int id, boolean prayerActive) {
		this.prayerActive[id] = prayerActive;
		return this;
	}

	public Entity setCurseActive(boolean[] curseActive) {
		this.curseActive = curseActive;
		return this;
	}

	public Entity setCurseActive(int id, boolean curseActive) {
		this.curseActive[id] = curseActive;
		return this;
	}

	/**
	 * 
	 * @param pathFinder
	 * @param entity
	 * @param x
	 * @param y
	 * @return
	 */
	public PathState doPath(PathFinder pathFinder, Entity entity, int x, int y) {
		return doPath(pathFinder, entity, null, x, y, false, true);
	}
	
	/**
	 * 
	 * @param pathFinder
	 * @param entity
	 * @param target
	 * @param x
	 * @param y
	 * @param ignoreLastStep
	 * @param addToWalking
	 * @return
	 */
	public PathState doPath(final PathFinder pathFinder, final Entity entity, final Entity target, final int x, final int y, final boolean ignoreLastStep, boolean addToWalking) {
		if (entity.getCombatState().isDead()) {
			PathState state = new PathState();
			state.routeFailed();
			return state;
		}
		Location destination = Location.create(x, y, entity.getLocation().getZ());
		Location base = entity.getLocation();
		int srcX = base.getLocalX();
		int srcY = base.getLocalY();
		int destX = destination.getLocalX(base);
		int destY = destination.getLocalY(base);
		PathState state = pathFinder.findPath(entity, target, entity.getLocation(), srcX, srcY, destX, destY, 1, entity.isPlayer() && ((Player)entity).getWalkingQueue().isRunningQueue(), ignoreLastStep, true);
		if (state != null && addToWalking) {
			if (entity.isPlayer()) {
				Player p = (Player)entity;
				p.getWalkingQueue().reset();
				for (BasicPoint step : state.getPoints()) {
					//p.sendForcedMessage("point: "+step.getX()+","+step.getY()+","+step.getZ()+" from "+srcX+","+srcY+" to "+destX+","+destY);
					//p.getActionSender().sendGroundItem(new GroundItem(new Item(item, 1), step.getX(), step.getY(), step.getZ(), p));
					p.getWalkingQueue().addStep(step.getX(), step.getY());
				}
				p.getWalkingQueue().finish();
				//p.debug("Calc'd "+state.getPoints().size()+" moves for goal dist "+base.distance(destination));
			} else {
				System.err.println("HELP WHO");
			}
		}
		return state;
	}

	/**
	 * Actually take hitpoints from the victim. If teleporting, damage is nulled.
	 * @param hit
	 */
	public void takeDamage(Hit hit) {

        // TODO put any code /checks that STOP DAMAGE being delt such as teleporting here
        
        if(!hit.victim.canBeDamaged()) {
        	return;
		}
        Entity attacker = hit.source;

        // Worth knowing that veng only triggers if the hit actually shows up
        // Same with smite, poison
        // So if you're teleporting and the hit nulls out, your veng won't break
		if (isPlayer()) {
			Player me = (Player)this;

			if (hit.getDamage() > 0) {
				// Trigger veng and recoil once the damage has been reduced by effects/protection prayers
				if (me.hasVengeance()) {
					Vengeance.handle(me, attacker, hit.getDamage());
				}

				me.getCombatState().recoil(attacker, hit.getDamage());
			}

			if (attacker.isPlayer()) {
				Player pAttacker = (Player)attacker;
				BarrowsEffect.applyRandomEffect(pAttacker, me, hit.getDamage());
				pAttacker.getCombatState().handleSmite(pAttacker, me, hit.getDamage());
				int wepId = pAttacker.getEquipment().get(EquipmentConstants.WEAPON_SLOT) == null ? -1 : pAttacker.getEquipment().get(EquipmentConstants.WEAPON_SLOT).getId();
				PoisonCombatTask.getPoisonType(wepId).ifPresent(pt -> me.poison(pt, attacker));
			}
		}
		// Make the hit show on the victim, and reduce their HP
        this.renderDamage(hit);
	}

}

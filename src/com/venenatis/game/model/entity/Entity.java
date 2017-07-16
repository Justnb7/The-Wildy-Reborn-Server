package com.venenatis.game.model.entity;

import com.google.common.base.Preconditions;
import com.venenatis.game.action.ActionQueue;
import com.venenatis.game.constants.EquipmentConstants;
import com.venenatis.game.content.sounds_and_music.sounds.MobAttackSounds;
import com.venenatis.game.content.sounds_and_music.sounds.PlayerSounds;
import com.venenatis.game.location.Location;
import com.venenatis.game.model.Projectile;
import com.venenatis.game.model.combat.Combat;
import com.venenatis.game.model.combat.CombatState;
import com.venenatis.game.model.combat.PrayerHandler.Prayers;
import com.venenatis.game.model.combat.combat_effects.BarrowsEffect;
import com.venenatis.game.model.combat.data.CombatStyle;
import com.venenatis.game.model.combat.pvm.PlayerVsNpcCombat;
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
import com.venenatis.game.util.MutableNumber;
import com.venenatis.game.util.Utility;
import com.venenatis.game.world.World;
import com.venenatis.game.world.pathfinder.Directions;
import com.venenatis.game.world.pathfinder.region.Coverage;
import com.venenatis.server.Server;

import java.util.*;

/**
 * @author Patrick van Elderen
 * @author Jak
 */
public abstract class Entity {
	
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
					if (gap < dist)
						distance = gap;
				}
			}
			return distance < dist;
		}
	}

	public abstract void message(String s);

	public enum EntityType {
		PLAYER, NPC,
	}

	public int lastX;
	public int lastY;
	public transient Object distanceEvent;
	private boolean registered;
	public int infection;
	public boolean infected;
	public int entityFaceIndex = -1;
	public int faceTileX = -1, faceTileY = -1;
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
	 * Checks if this player is teleporting.
	 * @return <code>true</code> if so, <code>false</code> if not.
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

	private boolean inCombat;
	public long lastWasHitTime;
	public Entity lastAttacker;

	//TODO figure out are we even use this
	public boolean inCombat() {
		return inCombat;
	}

	public void setInCombat(boolean inCombat) {
		this.inCombat = inCombat;
	}
	
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

	public boolean poison(PoisonType poisonType) {
		Entity entity = this;
		if (entity.isPoisoned() || entity.getPoisonType() == null)
			return false;
		if (random.nextInt(3) == 0) {
			if (entity.type == EntityType.PLAYER) {
				Player player = (Player) entity;
				if (player.getPoisonImmunity().get() > 0)
					return false;
				player.getActionSender().sendMessage("You have been poisoned!");
				infection = 1;
			}
			entity.getPoisonDamage().set(entity.getPoisonType().getDamage());
			Server.getTaskScheduler().schedule(new PoisonCombatTask(this));
			return true;
		}
		return false;
	}

	/**
	 * Actually apply the hit. Makes it show in Player Updating and also reduces your HITPOINTS (can kill you)
	 */
	public void damage(Hit... hits) {
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

	public Hit take_hit(Entity attacker, int damage, CombatStyle combat_type, boolean applyInstantly, boolean troughPrayer) {

		// ALWAYS: FIRST APPLY DAMAGE REDUCTIONS, ABSORBS ETC. Protection pray/ely.
		// The entity taking damage is a player. 
		if (this.isPlayer()) {
			Player player_me = (Player) this;
			player_me.putInCombat(attacker.getIndex()); // we're taking a hit. we can't logout for 10s.
			
			// The victim (this) has protection prayer enabled.
			if (combat_type != null && !troughPrayer) {
				// 40% Protection from player attacks, 100% protection from Npc attacks
				double prayProtection = attacker.isPlayer() ? 0.6D : 0.0D;
				if (combat_type == CombatStyle.MELEE && player_me.isActivePrayer(Prayers.PROTECT_FROM_MELEE)) {
					damage *= prayProtection;
				}
				if (combat_type == CombatStyle.RANGE && player_me.isActivePrayer(Prayers.PROTECT_FROM_MISSILE)) {
					damage *= prayProtection;
				}
				if (combat_type == CombatStyle.MAGIC && player_me.isActivePrayer(Prayers.PROTECT_FROM_MAGIC)) {
					damage *= prayProtection;
				}
			}
			int shield = player_me.getEquipment().get(EquipmentConstants.SHIELD_SLOT) == null ? -1 : player_me.getEquipment().get(EquipmentConstants.SHIELD_SLOT).getId();
			// TODO special reduction effects can go here, like Ely
			if (shield == 12817) {
				if (Utility.getRandom(100) > 30 && damage > 0) {
					damage *= .75;
				}
			}

			if (player_me.isTeleporting()) {
				damage = 0;
			}

		} else if (this.isNPC()) {
			NPC victim_npc = (NPC) this;
			
			if (attacker.isPlayer())
				PlayerVsNpcCombat.kraken((Player)attacker, victim_npc, damage);
			if (victim_npc.getId() == 319) {
				if (attacker.isNPC() || (attacker.isPlayer() && !EquipmentConstants.isWearingSpear(((Player)attacker)))) {
					damage /= 2;
				}
			}
			if (victim_npc.getId() == 5535) {
				damage = 0;
			}
			//Rex and Prime do not take melee damage
			if (combat_type == CombatStyle.MELEE && (victim_npc.getId() == 2267 || victim_npc.getId() == 2266)) {
				if (attacker.isPlayer())
					((Player)attacker).getActionSender().sendMessage("The dagannoth is currently resistant to that attack!");
				damage = 0;
			}
			//Rex and Supreme do not take range damage
			if (combat_type == CombatStyle.RANGE && (victim_npc.getId() == 2265 || victim_npc.getId() == 2267)) {
				((Player)attacker).getActionSender().sendMessage("The dagannoth is currently resistant to that attack!");
				damage = 0;
			}
			//Supreme and Prime do not take magic damage
			if (combat_type == CombatStyle.MAGIC && (victim_npc.getId() == 2265 || victim_npc.getId() == 2266)) {
				((Player)attacker).getActionSender().sendMessage("The dagannoth is currently resistant to that attack!");
				damage = 0;
			}
			if (combat_type == CombatStyle.MAGIC && victim_npc.getId() == 5535) {
				damage = 0;
			}
		}
		
		// At this point damage accurately reduced by stuff like prots/ely. 
		// Now we can use it to give XP/add to npcs damage tracker.

		if (isPlayer()) {
			Player me = (Player)this;

			if (damage > 0) {
				// Trigger veng and recoil once the damage has been reduced by effects/protection prayers
				if (me.hasVengeance()) {
					me.getCombatState().vengeance(attacker, damage, 1);
				}

				me.getCombatState().recoil(attacker, damage);
			}

			if (attacker.isPlayer()) {
				Player pAttacker = (Player)attacker;
				BarrowsEffect.applyRandomEffect(pAttacker, me, damage);
				pAttacker.getCombatState().applySmite(me, damage);
				int wepId = pAttacker.getEquipment().get(EquipmentConstants.WEAPON_SLOT) == null ? -1 : pAttacker.getEquipment().get(EquipmentConstants.WEAPON_SLOT).getId();
				PoisonCombatTask.getPoisonType(wepId).ifPresent(attacker::poison);
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
		Hit hit = new Hit(damage, damage > 0 ? HitType.NORMAL : HitType.BLOCKED).type(combat_type).between(attacker, this);

		// NOTE: If not instantly applied, use EventManager.event(2) { entity.damage(hit) }
		if (applyInstantly) {
			this.damage(hit);
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
	 * Makes this entity face a position.
	 * 
	 * @param position
	 *            The position to face.
	 */
	public void face(Entity entity, Location position) {
		//Faces the player
		if(entity.getEntityType() == EntityType.PLAYER) {
			faceTileX = 2 * position.getX() + 1;
			faceTileY = 2 * position.getY() + 1;
		//Faces the npc
		} else if(entity.getEntityType() == EntityType.NPC) {
			faceTileX = position.getX();
			faceTileY = position.getY();
		}
		this.getUpdateFlags().flag(UpdateFlag.FACE_COORDINATE);
	}
	
	/**
	 * Sets the entity facing index
	 * @param e
	 *   The entity
	 */
	public void faceEntity(Entity e) {
		//forceChat("face: "+e);
		if (e == null || e == this) {
			//System.out.println("wtf");
			this.resetFace();
			return;
		}
		// If WE are an npc, faceIndex is 'raw' - not +32k. 
		// If we're a player, facing players = 32k+pid.. facing npcs= raw index
		entityFaceIndex = e.clientIndex();
		this.getUpdateFlags().flag(UpdateFlag.FACE_ENTITY);
		//System.out.println((this.isNPC() ? "npc" : "player")+" FACING "+e.isNPC()+" facd req to -> "+entityFaceIndex);
	}
	
	public abstract int clientIndex();

	/**
	 * Resets the facing position.
	 */
	public void resetFace() {
		this.entityFaceIndex = -1;
		this.getUpdateFlags().flag(UpdateFlag.FACE_ENTITY);
		//System.out.println(this.isNPC()+ " why "+System.currentTimeMillis() / 1000);
	}

	public void playGraphics(Graphic graphic) {
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
	 * Plays projectiles.
	 */
	public void playProjectile(Projectile projectile) {
		for (int i = 0; i < World.getWorld().getPlayers().capacity(); i++) {
			Player p = World.getWorld().getPlayers().get(i);
			if (p != null) {
				if (p.getOutStream() != null) {
					if (p.getLocation().isWithinDistance(this.getLocation())) {
						p.getActionSender().sendProjectile(projectile.getStart(), projectile.getFinish(), projectile.getId(), projectile.getDelay(), projectile.getAngle(), projectile.getSpeed(), projectile.getStartHeight(), projectile.getEndHeight(),  projectile.getSlope(), projectile.getRadius(), projectile.getLockon());
					}
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
	
	private boolean forcedMovement;
	
	public void setForcedMovement(boolean active) {
		this.forcedMovement = active;
	}
	
	public boolean isForcedMovement() {
		return forcedMovement;
	}
	
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
		if(forceWalk.length > 0) {
			
			Server.getTaskScheduler().submit(new Task(forceWalk[7]) {
				@Override
				public void execute() {
					setForcedMovement(true);
					//System.out.println("force movement: "+isForcedMovement());
					setTeleportTarget(getLocation().transform(forceWalk[2], forceWalk[3], 0));
					if(removeAttribute) {
						getAttributes().remove("busy");
					}
					setForcedMovement(false);
					//System.out.println("force movement: "+isForcedMovement());
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

	private void updateCoverage(Directions.NormalDirection direction) {
		coverage.update(direction, size());
	}

	public void updateCoverage(Location loc) {
		if (coverage == null) {
			setCoverage();
		}
		coverage.update(loc, size());
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

	public Entity getEntity() {//Would this work? :d
		return isPlayer() ? asPlayer() : asNpc();
	}//trying to fill this part

}

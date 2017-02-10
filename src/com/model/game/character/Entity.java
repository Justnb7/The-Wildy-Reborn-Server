package com.model.game.character;

import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Preconditions;
import com.model.Server;
import com.model.game.character.combat.PrayerHandler.Prayer;
import com.model.game.character.combat.combat_data.CombatType;
import com.model.game.character.npc.Npc;
import com.model.game.character.player.ActionSender;
import com.model.game.character.player.Player;
import com.model.game.character.player.content.music.sounds.MobAttackSounds;
import com.model.game.location.Location;
import com.model.task.ScheduledTask;

/**
 * @author Omicron
 */
public abstract class Entity {
	
	public enum EntityType {
		PLAYER, NPC,
	}

	public int absX;
	public int absY;
	public int lastX;
	public int lastY;
	public int heightLevel;
	public transient Object distanceEvent;
	private boolean registered;
	public int poisonDamage;
	public int infection;
	public boolean infected;
	public Hit primary;
	public Hit secondary;
	public boolean hitUpdateRequired;
	public boolean hitUpdateRequired2;
	public Graphic gfx;
	public boolean gfxUpdateRequired;

	/**
	 * The characters combat type, MELEE by default
	 */
	private CombatType combatType = CombatType.MELEE;
	/**
	 * Attributes a creature can hold
	 */
	private final HashMap<String, Object> attributes = new HashMap<>();

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

	public abstract Hit decrementHP(Hit hit);

	public void clear() {
		primary = null;
		secondary = null;
		hitUpdateRequired = false;
		hitUpdateRequired2 = false;
		updateRequired = false;
	}

	private boolean inCombat;

	public boolean inCombat() {
		return inCombat;
	}

	public void setInCombat(boolean inCombat) {
		this.inCombat = inCombat;
	}

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
		primary = decrementHP(hit);
		updateRequired = true;
		hitUpdateRequired = true;
	}

	private void secondaryDamage(Hit hit) {
		secondary = decrementHP(hit);
		updateRequired = true;
		hitUpdateRequired2 = true;
	}

	private void sendDamage(Hit hit) {
		if (hitUpdateRequired) {
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

		Server.getTaskScheduler().submit(new ScheduledTask(1, false) {
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

		Server.getTaskScheduler().submit(new ScheduledTask(1, false) {
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

	public <T> T removeAttribute(String key) {
		@SuppressWarnings("unchecked")
		T t = (T) attributes.remove(key);
		return t;
	}

	public void setAttribute(final String key, final Object value) {
		attributes.put(key, value);
	}

	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public <T> T getAttribute(String key, T fail) {

		Object object = attributes.get(key);

		if (object != null && object.getClass() == fail.getClass()) {
			@SuppressWarnings("unchecked")
			T t = (T) object;
			return t;
		}
		if (fail == null) {//wtf
			return null;
		}

		return fail;
	}

	public int getX() {
		return absX;
	}

	public int getY() {
		return absY;
	}

	public int getLastX() {
		return lastX;
	}

	public int getLastY() {
		return lastY;
	}

	public void setAbsX(int absX) {
		this.lastX = this.absX;
		this.absX = absX;
	}

	public void setAbsY(int absY) {
		this.lastY = this.absY;
		this.absY = absY;
	}
	
	public void setAbsZ(int absZ) {
		this.heightLevel = absZ;
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

	public Npc asNpc() {
		return (Npc) this;
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
	 * Determines the characters {@link CombatType}
	 * 
	 * @return The {@link CombatType} this character is attacking with
	 */
	public CombatType getCombatType() {
		return combatType;
	}

	/**
	 * Sets the characters new {@link CombatType}
	 * 
	 * @param type
	 *            The {@link CombatType} this character is attacking with
	 */
	public void setCombatType(CombatType type) {
		this.combatType = type;
	}

	/**
	 * Processes the entity every 600ms
	 */
	public abstract void process();
	
	public boolean isNPC() {
		return this instanceof Npc;
	}
	
	public boolean isPlayer() {
		return this instanceof Player;
	}
	
	public Npc toNPC() {
		return isNPC() ? (Npc) this : null;
	}
	
	public Player toPlayer() {
		return isNPC() ? null : (Player) this;
	}
	
	/**
	 * Returns the packet sender for the entity, mainly used for players
	 * 
	 * @return
	 */
	public abstract ActionSender getActionSender();
	
	public Hit take_hit(Entity attacker, int damage) {
		return take_hit(attacker, damage, null);
	}

	// Since damage gets reduced you need to add XP after this method.
	public Hit take_hit(Entity attacker, int damage, CombatType combat_type) {
		Hit hit = new Hit(damage, damage > 0 ? HitType.NORMAL : HitType.BLOCKED).type(combat_type);
		
		// The entity taking damage is a player. 
		if (this.isPlayer()) {
			Player player_me = (Player) this;
			player_me.logoutDelay.reset(); // we're taking a hit. we can't logout for 10s.
			
			// The victim (this) has protection prayer enabled. TODO you need to specify combat_type
			if (combat_type != null) {
				if (combat_type == CombatType.MELEE && player_me.isActivePrayer(Prayer.PROTECT_FROM_MELEE)) {
					damage = (int) (damage * 0.6);
				}
				if (combat_type == CombatType.RANGED && player_me.isActivePrayer(Prayer.PROTECT_FROM_MISSILE)) {
					damage = (int) (damage * 0.6);
				}
				if (combat_type == CombatType.MAGIC && player_me.isActivePrayer(Prayer.PROTECT_FROM_MAGIC)) {
					damage = (int) (damage * 0.6);
				}
			}
			
			// TODO special reduction effects can go here, like Ely

			// Trigger veng once the damage has been reduced by effects/protection prayers
			if (player_me.hasVengeance()) {
				player_me.getCombat().vengeance(attacker, damage, 1);
			}
		} else if (this.isNPC()) {
			Npc victim_npc = (Npc) this;
			// You can't hit over an Npcs current health. Recent update on 07 means you can in PVP though.
			if (victim_npc.currentHealth - damage < 0) {
				damage = victim_npc.currentHealth;
			}
		}
		
		// At this point damage accurately reduced by stuff like prots/ely. 
		// Now we can use it to give XP/add to npcs damage tracker.

		// This Entity is an npc taking damage from a player. 
		if (this.isNPC() && attacker.isPlayer()) {
			Player attacker_player = (Player)attacker;
			Npc victim_npc = (Npc) this;
			((Npc)this).addDamageReceived(attacker_player.getRealUsername(), damage);
			MobAttackSounds.sendBlockSound(attacker_player, victim_npc.getId()); // TODO use npc not npcid
		}
		
		// Returning hit: might be helpful in the future. For chaining. Such as hit.x().y()..
		
		this.damage(new Hit(damage));
		return hit;
	}
	
	/**
	 * The default, i.e. spawn, location.
	 */
	public static final Location DEFAULT_LOCATION = Location.create(3087, 3495, 0);

    /**
     * The index of the entity
     */
    private int index;
    
    /**
	 * The current location.
	 */
	private Location location;
	
	/**
	 * The teleportation target.
	 */
	private Location teleportTarget = null;

	/**
	 * The last known map region.
	 */
	private Location lastKnownRegion = this.getLocation();
	
	/**
	 * The face location.
	 */
	private Location face;

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
	 * Gets the face location.
	 * 
	 * @return The face location, or <code>null</code> if the entity is not
	 *         facing.
	 */
	public Location getFaceLocation() {
		return face;
	}
	
	/**
	 * Checks if this entity has a target to teleport to.
	 * 
	 * @return <code>true</code> if so, <code>false</code> if not.
	 */
	public boolean hasTeleportTarget() {
		return teleportTarget != null;
	}

	/**
	 * Gets the teleport target.
	 * 
	 * @return The teleport target.
	 */
	public Location getTeleportTarget() {
		return teleportTarget;
	}

	/**
	 * Sets the teleport target.
	 * 
	 * @param teleportTarget
	 *            The target location.
	 */
	public void setTeleportTarget(Location teleportTarget) {
		this.teleportTarget = teleportTarget;
	}

	/**
	 * Resets the teleport target.
	 */
	public void resetTeleportTarget() {
		this.teleportTarget = null;
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

	/**
	 * Gets the current location.
	 * 
	 * @return The current location.
	 */
	public Location getLocation() {
		return location;
	}
	
	public void setAppearanceUpdateRequired(boolean appearanceUpdateRequired) {
		this.appearanceUpdateRequired = appearanceUpdateRequired;
	}

	public boolean isAppearanceUpdateRequired() {
		return appearanceUpdateRequired;
	}
	
	public boolean forcedChatUpdateRequired, updateRequired = true, appearanceUpdateRequired = true;
	
	/**
	 * The text to display with the force chat mask.
	 */
	private String forcedChat;
	
	/**
	 * Creates the force chat mask.
	 *
	 * @param message
	 */
	public void forceChat(String message) {
		forcedChat = message;
		forcedChatUpdateRequired = true;
		updateRequired = true;
		setAppearanceUpdateRequired(true);
	}
	
	/**
	 * Creates the force chat mask.
	 *
	 * @param message
	 */
	public void setForceChat(String message) {
		forcedChat = message;
	}

	/**
	 * Gets the message to display with the force chat mask.
	 *
	 * @return The message to display with the force chat mask.
	 */
	public String getForcedChatMessage() {
		return forcedChat;
	}
	// mobilecharacter looked massive yesterday doesnt look big at all now interesting but no errors so 
	// i guess it works xd it does work i just tested everything but yeah look @ groundItems looks messy asf now
	
}
